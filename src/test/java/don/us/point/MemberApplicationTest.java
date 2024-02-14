package don.us.point;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import don.us.member.MemberEntity;
import don.us.member.MemberRepository;
import lombok.extern.java.Log;

@Log
@SpringBootTest
public class MemberApplicationTest {
	@Autowired
	private PointHistoryRepository repo;
	
	@Autowired
	private MemberRepository memberRepo;
	
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
	
}
