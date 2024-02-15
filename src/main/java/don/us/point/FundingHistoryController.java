package don.us.point;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import don.us.funding.FundingMemberEntity;
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
				FundingHistoryEntity fundingHistory = 
						makeFundingHistory(list.get(i).getMemberno(), list.get(i).getFundingno(), list.get(i).getMonthlypaymentamount());
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
		List<RepaymentEntity> repayList = repayList();
		for(int i=0; i<repayList.size(); i++) {
			try {
				//여기서 재결제 시도를 함
				//성공하면 맨 마지막에 테이블에서 삭제
			} catch(Exception e) {
				//안되면 재결제 횟수를 가져와서 2인지 체크함
				//만약 2면 3회째 실패인 것이므로 해당 멤버 강제 중도포기로 전환, 알람 보냄, 테이블에서 삭제
			}
		}
	}
}
