package don.us.point;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import don.us.funding.FundingMemberEntity;
import don.us.funding.FundingService;
import don.us.member.MemberEntity;
import don.us.member.MemberRepository;
import lombok.extern.java.Log;

@Log
@SpringBootTest
public class PointApplicationTest {
	@Autowired
	private PointHistoryRepository repo;
	
	@Autowired
	private MemberRepository memberRepo;
	
	@Autowired
	private FundingHistoryRepository fundrepo;
	
	@Transactional
	@Test
	public void chargeTest() throws RuntimeException {
		try {
			PointHistoryEntity pointHistory = new PointHistoryEntity();
			pointHistory.setMemberno(0);
			pointHistory.setAmount(1);
			pointHistory.setMerchantuid("1707803442643_7");
			pointHistory.setImpuid("imp_552443415375");
			//그 외 시간 등은 erd에 맞춰서
			repo.save(pointHistory);
			
			MemberEntity member = memberRepo.findById(pointHistory.getMemberno()).orElseThrow(RuntimeException::new);
			member.setPoint(member.getPoint() + pointHistory.getAmount());
			memberRepo.save(member);
		} catch(Exception e) {
			System.out.println("취소요청");
			throw new RuntimeException();
		}
		
	}
	
	@Test
	public void pointHistory() {
    	List<PointHistoryEntity> pointList = repo.findByMembernoOrderByTransactiondateDesc(7);
    	System.out.println("확인"+pointList);
    }
	
	@Autowired
	private FundingService fundingService;
	
	@Autowired
	private RepaymentRepository repayRepo;
	
	@Test
	public void paylisttest() {
		List<FundingMemberEntity> list = fundingService.needPayMemberList();
		for(int i=0; i<list.size(); i++) {
			try {
			FundingHistoryEntity fundingHistory = 
					makeFundingHistory(list.get(i).getMemberno(), list.get(i).getFundingno(), list.get(i).getMonthlypaymentamount());
			} catch(Exception e) {
				//여기서 해당 멤버에게 알람을 보내주면??
				System.out.println(list.get(i).getMemberno()+"번 고객님의 "+list.get(i).getFundingno()+"번 펀딩 결제에서 문제가 발생했습니다.");
				RepaymentEntity repay = new RepaymentEntity();
				repay.setFundingmemberno(list.get(i).getNo());
				repayRepo.save(repay);
			}
		}
	}
	
	@Transactional
	private FundingHistoryEntity makeFundingHistory(int memberno, int fundingno, int amount) throws Exception {
		FundingHistoryEntity fundingHistory = new FundingHistoryEntity();
		fundingHistory.setMemberno(memberno);
		fundingHistory.setFundingno(fundingno);
		fundingHistory.setAmount(amount);
		if(1 == 1) throw new Exception();
		return fundrepo.save(fundingHistory);
	}
	
}
