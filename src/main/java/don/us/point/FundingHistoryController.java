package don.us.point;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import don.us.funding.FundingEntity;
import don.us.funding.FundingMemberEntity;
import don.us.funding.FundingMemberRepository;
import don.us.funding.FundingRepository;
import don.us.funding.FundingService;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/point/funding_history")
public class FundingHistoryController {
	@Autowired
	private FundingHistoryRepository repo;
	
	@Autowired
	private RepaymentRepository repayRepo;
	
	@Autowired
	private FundingRepository fundingRepo;
	
	@Autowired
	private FundingMemberRepository fundingMemberRepo;
	
	@Autowired
	private FundingService fundingService;
	
	@GetMapping("/mypointHistory")
    public List<FundingHistoryEntity> pointHistory(@RequestParam(value="member_no") int member_no) {
    	List<FundingHistoryEntity> pointList = repo.findByMembernoOrderByTransactiondateDesc(member_no);
    	return pointList;
    }
	
	@GetMapping("/regularPayment")
	public String regularPayment() {
		List<FundingMemberEntity> list = fundingService.needPayMemberList();
		for(int i=0; i<list.size(); i++) {
			try {
				FundingMemberEntity fundMem = list.get(i);
				FundingHistoryEntity fundingHistory = makeFundingHistory(fundMem.getMemberno(), fundMem.getFundingno(), fundMem.getMonthlypaymentamount());
				//해당 펀딩 결제된 포인트에 돈 더해서 업데이트
				Optional<FundingEntity> fund = fundingRepo.findById( fundMem.getFundingno() );
				fund.orElseThrow().setCollectedpoint( fund.orElseThrow().getCollectedpoint() + fundMem.getMonthlypaymentamount() );
				fundingRepo.save(fund.get());
				//해당 펀딩 멤버의 총 결제금액에 더해서 업데이트
				fundMem.setTotalpayamount( fundMem.getTotalpayamount() + fundMem.getMonthlypaymentamount() );
				fundingMemberRepo.save(fundMem);
			} catch(Exception e) {
				//여기서 해당 멤버에게 알람을 보내주고, 재결제 테이블에 정보 추가함
				System.out.println(list.get(i).getMemberno()+"번 고객님의 "+list.get(i).getFundingno()+"번 펀딩 결제에서 문제가 발생했습니다.");
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
		//fundingHistory.setDirection(false); //0=false가 디폴트값이라 따로 설정 안하고 반대로 펀딩에서 돈 줄 때 true로 세팅할게요
		return repo.save(fundingHistory);
	}
	
	//재결제 목록을 쭉 불러옴
	@GetMapping("/repayList")
	public List<RepaymentEntity> repayList() {
		List<RepaymentEntity> list = repayRepo.findAll();
		return list;
	}
	
	//재결제하는 함수
	@GetMapping("/doRepay")
	public void doRepay() {
		List<RepaymentEntity> repayList = repayRepo.findAll();
		for(int i=0; i<repayList.size(); i++) {
			Optional<FundingMemberEntity> fundingMem = fundingMemberRepo.findById(repayList.get(i).getFundingmemberno());
			try {
				//여기서 재결제 시도를 함
				if(repayList.get(i).getFundingmemberno() == 127) throw new Exception();
				makeFundingHistory(fundingMem.orElseThrow().getMemberno(), fundingMem.orElseThrow().getFundingno(), fundingMem.orElseThrow().getMonthlypaymentamount());
				//성공하면 repay 테이블에서 삭제
				repayRepo.deleteById(repayList.get(i).getNo());
			} catch(Exception e) {
				//안되면 재결제 횟수를 가져와서 2인지 체크함
				if(repayList.get(i).getRepaycount() >= 2) {
					//만약 2이면 방금 한 재결제로 3회째 실패인 것이므로 해당 멤버 강제 중도포기로 전환, 알람 보냄, 테이블에서 삭제
					fundingMem.get().setGiveup(true);
					fundingMemberRepo.save(fundingMem.get());
					repayRepo.deleteById(repayList.get(i).getNo());
				} else {
					repayList.get(i).setRepaycount(repayList.get(i).getRepaycount()+1);
					repayRepo.save(repayList.get(i));
				}
			}
		}
	}
}
