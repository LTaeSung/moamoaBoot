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
		pointHistory.setMemberno(1); //현재 접속한 사람 memberno를 받아와 넣어주면 됨
		pointHistory.setAmount(100);
		pointHistory.setTransactiondate(20240208);
		repo.save(pointHistory);
		//해당 멤버의 포인트 업데이트 처리 필요
		
		//멤버번호로 포인트를 가져오고
		MemberEntity member = memberRepo.findById(1).orElseThrow(RuntimeException::new);
		//결제된 금액이랑 더해서 save? 업데이트용 함수를 따로 만들어야 하나?
		member.setPoint(member.getPoint() + pointHistory.getAmount());
		memberRepo.save(member);

	}
	
}
