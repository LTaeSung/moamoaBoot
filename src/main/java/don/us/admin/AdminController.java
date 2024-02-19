package don.us.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import don.us.alarm.AlarmService;
import don.us.funding.FundingController;
import don.us.funding.FundingEntity;
import don.us.funding.FundingMemberEntity;
import don.us.funding.FundingMemberRepository;
import don.us.funding.FundingRepository;
import don.us.funding.FundingService;
import don.us.point.FundingHistoryEntity;
import don.us.point.FundingHistoryRepository;
import don.us.point.RepaymentEntity;
import don.us.point.RepaymentRepository;
import util.file.HandleDays;

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
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private FundingController fundingController;
	
	@Autowired
	private HandleDays handleDays;
	
	@GetMapping("/regularPaymentList")
	public List<FundingMemberEntity> regularPaymentList(){
		List<FundingMemberEntity> list = fundingService.needPayMemberList();
		return list;
	}
	
	@GetMapping("/regularPayment")
	public String regularPayment() {
		List<FundingMemberEntity> list = fundingService.needPayMemberList();
		for(int i=0; i<list.size(); i++) {
			FundingMemberEntity fundMem = fundingMemberRepo.findById(list.get(i).getNo()).get();
			FundingEntity fund = fundingRepo.findById( fundMem.getFundingno() ).get();
			try {
				//펀딩결제 진행
				adminService.makePayToFundingFundingHistory(fundMem.getMemberno(), fundMem.getFundingno(), fundMem.getMonthlypaymentamount());
				updateTotalPayAmount(fundMem, fund);
				
				//결제 성공 알람
				String content = "챌린지 ["+fund.getTitle()+"]의 이번 달 결제가 완료되었습니다.";
				alarmService.makePayAlarm(fundMem.getMemberno(), content, fundMem.getFundingno());
			} catch(Exception e) {
				//여기서 해당 멤버에게 알람을 보내주고, 재결제 테이블에 정보 추가함
				String content = "챌린지 ["+fund.getTitle()+"]의 이번 달 결제에 실패했습니다. 자동으로 재결제가 진행될 예정이오니 해당 펀딩에 등록된 결제 카드를 다른 카드로 변경해주세요.";
				alarmService.makePayAlarm(fundMem.getMemberno(), content, fundMem.getFundingno());
				RepaymentEntity repay = new RepaymentEntity();
				repay.setFundingmemberno(list.get(i).getNo());
				repayRepo.save(repay);
			}
		}
		return "success";
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
			FundingMemberEntity fundMem = fundingMemberRepo.findById(repayList.get(i).getFundingmemberno()).get();
			FundingEntity fund = fundingRepo.findById( fundMem.getFundingno() ).get();
			try {
				//재결제 시도
				adminService.makePayToFundingFundingHistory(fundMem.getMemberno(), fundMem.getFundingno(), fundMem.getMonthlypaymentamount());
				updateTotalPayAmount(fundMem, fund);
				
				//재결제 성공 알림 보내고 테이블에서 삭제
				String content = "챌린지 ["+fund.getTitle()+"]의 재결제에 성공했습니다.";
				alarmService.makePayAlarm(fundMem.getMemberno(), content, fundMem.getFundingno());
				repayRepo.deleteById(repayList.get(i).getNo());
			} catch(Exception e) {
				//만약 이미 실패한 횟수가 2인데 또 실패 시 방금 한 재결제로 3회째 실패인 것이므로 해당 멤버 강제 중도포기로 전환, 최종 실패 알람 보냄, 테이블에서 삭제
				if(repayList.get(i).getRepaycount() >= 2) {
					fundingController.giveupMethod(fundMem, fund);
					String content = "챌린지 ["+fund.getTitle()+"]의 재결제에 3회 실패했습니다. 자동으로 중도포기 처리됩니다.";
					alarmService.makePayAlarm(fundMem.getMemberno(), content, fundMem.getFundingno());
					
					repayRepo.deleteById(repayList.get(i).getNo());
				} else {
					//재결제에 실패했으나 아직 기회가 남음
					repayList.get(i).setRepaycount(repayList.get(i).getRepaycount()+1);
					repayRepo.save(repayList.get(i));

					//재결제 실패 알림
					String content = "챌린지 ["+fund.getTitle()+"]의 재결제에 실패했습니다. 자동으로 재결제가 진행될 예정이오니 해당 펀딩에 등록된 결제 카드를 다른 카드로 변경해주세요.";
					alarmService.makePayAlarm(fundMem.getMemberno(), content, fundMem.getFundingno());
				}
			}
		}
		return "success";
	}
	
	//결제 후 해당 펀딩에 모인 총 포인트, 결제한 멤버의 총결제금액 업데이트 치는 함수
	private void updateTotalPayAmount(FundingMemberEntity fundMem, FundingEntity fund) {
		//해당 펀딩 결제된 포인트에 돈 더해서 업데이트
		fund.setCollectedpoint( fund.getCollectedpoint() + fundMem.getMonthlypaymentamount() );
		fundingRepo.save(fund);
		//해당 펀딩 멤버의 총 결제금액에 더해서 업데이트
		fundMem.setTotalpayamount( fundMem.getTotalpayamount() + fundMem.getMonthlypaymentamount() );
		fundingMemberRepo.save(fundMem);
	}
	
	public void setFundStatus0To1() {
		//초대마감일이 지났는데 펀딩 참여일이 없는(승낙도 거절도 안한) fundingmember 목록을 불러와서
		List<FundingMemberEntity> list = fundingMemberRepo.getDontAcceptRefuseInWeekMemberList();
		for(int i=0; i<list.size(); i++) {
			//해당 정보를 펀딩멤버 테이블에서 삭제해버림
			fundingMemberRepo.delete(list.get(i));
			System.out.println("펀딩멤버넘버 "+list.get(i).getNo()+"번 삭제됨");
			//앞에서 펀딩멤버를 삭제했으니 이제 펀딩을 시작할 수 있는지 확인, 가능하면 start(status를 0에서 1로)
			if(fundingService.checkStartFundingWhenAcceptFund(list.get(i).getFundingno())) {
				fundingService.setFundStart(list.get(i).getFundingno());
				System.out.println("펀딩넘버 "+list.get(i).getFundingno()+"번 시작됨");
				//펀딩넘버 갖다가 남은 참여 전체 인원한테 시작알림 보내줘도 좋을듯?
			} else System.out.println("펀딩넘버 "+list.get(i).getFundingno()+"번 시작안됨");
		}
	}
	
	public void setFundStatus1To2() {
		System.out.println("실행됨");
		//펀드 상태=1 and 펀드 마감일<now()인 목록 가져옴
		List<FundingEntity> fundlist = fundingRepo.getFundingDueList();
		System.out.println("펀딩 개수: "+fundlist.size());
		for(int i=0; i<fundlist.size(); i++) {
			//펀드 상태를 1->2로 업뎃, 투표 마감일을 마감일(funding_due_date)+7일 해서 넣어줌
			System.out.println("펀드상태 업뎃전 확인"+fundlist.get(i).getState()+" 펀드 투표기한 들어가기전 확인: "+fundlist.get(i).getVoteduedate());
			fundlist.get(i).setState(2);
			fundlist.get(i).setVoteduedate(handleDays.addDays(fundlist.get(i).getFundingduedate(), 7));
			System.out.println("펀드상태 업뎃 확인"+fundlist.get(i).getState()+" 펀드 투표기한 잘 들어갔나 확인: "+fundlist.get(i).getVoteduedate());
			fundingRepo.save(fundlist.get(i));
			
			//해당 펀딩 참여자중에 중도포기 안 한(giveup=false) 사람들 목록 가져와서 투표하라고 알림보냄
			List<FundingMemberEntity> completeMemberList = fundingMemberRepo.getCompleteMemberList(fundlist.get(i).getNo());
			for(int j=0; j<completeMemberList.size(); j++) {
				alarmService.makeVoteAlarm(completeMemberList.get(j).getMemberno(), fundlist.get(i).getNo());
				System.out.println("알람갔어용");
			}
		}	
	}
	
	public void setFundStatus2To3() {
		System.out.println("실행됨");
		//펀드상태=2 and 투표마감일<now()인 목록 가져옴
		List<FundingEntity> fundlist = fundingRepo.getVoteDueList();
		System.out.println("펀딩 개수: "+fundlist.size());
		//멤버 중 투표 안 한 인원이 있으면(vote=0) 전부 실패(2)로 업뎃
		for(int i=0; i<fundlist.size(); i++) {
			List<FundingMemberEntity> dontVoteMemberList = fundingMemberRepo.needVoteFundMemberList(fundlist.get(i).getNo());
			
			for(int j=0; j<dontVoteMemberList.size(); j++) {
				adminService.vote(dontVoteMemberList.get(j), 2);
				if(adminService.checkVoteIsComplete(dontVoteMemberList.get(j).getFundingno())) {
					adminService.computeAndSetSettlementAccount(fundlist.get(i));
				}
			}
			//펀드 status 3으로 업뎃, settlement_due_date 7일 후로 업데이트해줌
			System.out.println("상태 업뎃 전: "+fundlist.get(i).getState()+" 정산마감일"+fundlist.get(i).getSettlementduedate());
			fundlist.get(i).setState(3);
			fundlist.get(i).setSettlementduedate(handleDays.addDays(fundlist.get(i).getVoteduedate(), 7));
			fundingRepo.save(fundlist.get(i));
			System.out.println("상태 업뎃 후: "+fundlist.get(i).getState()+" 정산마감일"+fundlist.get(i).getSettlementduedate());
		}
	}
	
	public void setFundStatus3To4() {
		//fund status=3이고 settlement_due_date<now()인 펀드 리스트 불러옴
		List<FundingEntity> fundlist = fundingRepo.getSettlementDueList();
		for(int i=0; i<fundlist.size(); i++) {
			List<FundingMemberEntity> dontSettlementMemberList = fundingMemberRepo.needSettlementFundMemberList(fundlist.get(i).getNo());
			
			for(int j=0; j<dontSettlementMemberList.size(); j++) {
				adminService.settlement(dontSettlementMemberList.get(j));
				if(adminService.checkSettlementIsComplete(dontSettlementMemberList.get(j).getFundingno())) {
					//정산 끝났으니 상태 4로 업뎃, break는 쳐줘도 되지만 어차피 끝날거라 굳이?
					System.out.println("상태 업뎃 전: "+fundlist.get(i).getState());
					fundlist.get(i).setState(4);
					fundingRepo.save(fundlist.get(i));
					System.out.println("상태 업뎃 후: "+fundlist.get(i).getState());
				}
			}
		}
	}
}
