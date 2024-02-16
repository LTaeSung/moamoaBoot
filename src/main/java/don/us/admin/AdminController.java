package don.us.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import don.us.alarm.AlarmService;
import don.us.funding.FundingEntity;
import don.us.funding.FundingMemberEntity;
import don.us.funding.FundingMemberRepository;
import don.us.funding.FundingRepository;
import don.us.funding.FundingService;
import don.us.point.FundingHistoryEntity;
import don.us.point.FundingHistoryRepository;
import don.us.point.RepaymentEntity;
import don.us.point.RepaymentRepository;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/admin")
public class AdminController {
	@Autowired
	private FundingHistoryRepository fundingHistoryRepo;
	
	@Autowired
	private RepaymentRepository repayRepo;
	
	@Autowired
	private FundingRepository fundingRepo;
	
	@Autowired
	private FundingMemberRepository fundingMemberRepo;
	
	@Autowired
	private AlarmService alarmService;
	
	@Autowired
	private FundingService fundingService;
	
	@GetMapping("/regularPaymentList")
	public List<FundingMemberEntity> regularPaymentList(){
		List<FundingMemberEntity> list = fundingService.needPayMemberList();
		return list;
	}
	
	@GetMapping("/regularPayment")
	public String regularPayment() {
		List<FundingMemberEntity> list = fundingService.needPayMemberList();
		for(int i=0; i<list.size(); i++) {
			Optional<FundingMemberEntity> fundMem = fundingMemberRepo.findById(list.get(i).getNo());
			Optional<FundingEntity> fund = fundingRepo.findById( fundMem.get().getFundingno() );
			try {
				//펀딩결제 진행
				FundingHistoryEntity fundingHistory = 
						makeFundingHistory(fundMem.get().getMemberno(), fundMem.get().getFundingno(), fundMem.get().getMonthlypaymentamount());
				updateTotalPayAmount(fundMem, fund);
				
				//결제 성공 알람
				String content = "챌린지 ["+fund.get().getTitle()+"]의 이번 달 결제가 완료되었습니다.";
				alarmService.makePayAlarm(fundMem.get().getMemberno(), content, fundMem.get().getFundingno());
			} catch(Exception e) {
				//여기서 해당 멤버에게 알람을 보내주고, 재결제 테이블에 정보 추가함
				String content = "챌린지 ["+fund.get().getTitle()+"]의 이번 달 결제에 실패했습니다. 자동으로 재결제가 진행될 예정이오니 해당 펀딩에 등록된 결제 카드를 다른 카드로 변경해주세요.";
				alarmService.makePayAlarm(fundMem.get().getMemberno(), content, fundMem.get().getFundingno());
				RepaymentEntity repay = new RepaymentEntity();
				repay.setFundingmemberno(list.get(i).getNo());
				repayRepo.save(repay);
			}
		}
		return "success";
	}
	
	@Transactional
	private FundingHistoryEntity makeFundingHistory(int memberno, int fundingno, int amount) throws Exception {
		FundingHistoryEntity fundingHistory = new FundingHistoryEntity();
		fundingHistory.setMemberno(memberno);
		fundingHistory.setFundingno(fundingno);
		fundingHistory.setAmount(amount);
		return fundingHistoryRepo.save(fundingHistory);
	}
	
	//재결제 목록을 전부 불러옴
	@GetMapping("/repayList")
	public List<RepaymentEntity> repayList() {
		List<RepaymentEntity> list = repayRepo.findAll();
		return list;
	}
	
	//재결제하는 함수
	@GetMapping("/doRepay")
	public String doRepay() throws Exception {
		List<RepaymentEntity> repayList = repayRepo.findAll();
		for(int i=0; i<repayList.size(); i++) {
			Optional<FundingMemberEntity> fundMem = fundingMemberRepo.findById(repayList.get(i).getFundingmemberno());
			Optional<FundingEntity> fund = fundingRepo.findById( fundMem.get().getFundingno() );
			try {
//				if(fundMem.get().getNo() == 127) throw new Exception(); 재결제 실패 데이터 만들기 위해 일부러 에러 유발하는 코드
				//재결제 시도
				makeFundingHistory(fundMem.orElseThrow().getMemberno(), fundMem.orElseThrow().getFundingno(), fundMem.orElseThrow().getMonthlypaymentamount());
				updateTotalPayAmount(fundMem, fund);
				
				//재결제 성공 알림
				String content = "챌린지 ["+fund.get().getTitle()+"]의 재결제에 성공했습니다.";
				alarmService.makePayAlarm(fundMem.get().getMemberno(), content, fundMem.get().getFundingno());
				//성공하면 repay 테이블에서 삭제
				repayRepo.deleteById(repayList.get(i).getNo());
			} catch(Exception e) {
				//만약 이미 실패한 횟수가 2이면 방금 한 재결제로 3회째 실패인 것이므로 해당 멤버 강제 중도포기로 전환, 최종 실패 알람 보냄, 테이블에서 삭제
				if(repayList.get(i).getRepaycount() >= 2) {
//					fundMem.get().setGiveup(true);
//					//펀딩 멤버 수 1 줄여야함!!!
//					fundingMemberRepo.save(fundMem.get());
					
					String content = "챌린지 ["+fund.get().getTitle()+"]의 재결제에 3회 실패했습니다. 자동으로 중도포기 처리됩니다.";
					alarmService.makePayAlarm(fundMem.get().getMemberno(), content, fundMem.get().getFundingno());
					
					repayRepo.deleteById(repayList.get(i).getNo());
				} else {
					//재결제에 실패했으나 아직 기회가 남음
					repayList.get(i).setRepaycount(repayList.get(i).getRepaycount()+1);
					repayRepo.save(repayList.get(i));

					//재결제 실패 알림
					String content = "챌린지 ["+fund.get().getTitle()+"]의 재결제에 실패했습니다. 자동으로 재결제가 진행될 예정이오니 해당 펀딩에 등록된 결제 카드를 다른 카드로 변경해주세요.";
					alarmService.makePayAlarm(fundMem.get().getMemberno(), content, fundMem.get().getFundingno());
				}
			}
		}
		return "success";
	}
	
	//결제 후 해당 펀딩에 모인 총 포인트, 결제한 멤버의 총결제금액 업데이트 치는 함수
	private void updateTotalPayAmount(Optional<FundingMemberEntity> fundMem, Optional<FundingEntity> fund) {
		//해당 펀딩 결제된 포인트에 돈 더해서 업데이트
		fund.orElseThrow().setCollectedpoint( fund.orElseThrow().getCollectedpoint() + fundMem.get().getMonthlypaymentamount() );
		fundingRepo.save(fund.get());
		//해당 펀딩 멤버의 총 결제금액에 더해서 업데이트
		fundMem.get().setTotalpayamount( fundMem.get().getTotalpayamount() + fundMem.get().getMonthlypaymentamount() );
		fundingMemberRepo.save(fundMem.get());
	}
	
	public void startFunding() {
		//
	}
	//투표안했으면 거절로
}
