package don.us.point;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import don.us.member.MemberEntity;
import don.us.member.MemberRepository;

@SpringBootTest
@EnableTransactionManagement
public class MemberApplicationTest {
	@Autowired
	private PointHistoryRepository repo;
	
	@Autowired
	private MemberRepository memberRepo;
	
	@Transactional
	@Test
	public void chargeTest() {
		
		PointHistoryEntity pointHistory = new PointHistoryEntity();
		pointHistory.setMemberno(1);
		pointHistory.setAmount(100);
		pointHistory.setTransactiondate(20240208);
		repo.save(pointHistory);
		
		MemberEntity member = memberRepo.findById(1).orElseThrow(RuntimeException::new);
		member.setPoint(member.getPoint() + pointHistory.getAmount());
		memberRepo.save(member);

	}
	
}
