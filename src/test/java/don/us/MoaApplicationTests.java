package don.us;


import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import don.us.member.FriendEntity;
import don.us.member.FriendRepository;
import jakarta.transaction.Transactional;
import lombok.extern.java.Log;

@SpringBootTest@Log
class MoaApplicationTests {
	
	@Autowired
	FriendRepository friendRepo;
//	@Autowired
//	MemberRepository memberRepo;
	
	@Test
	@Transactional
	public void testTest() {
		List<FriendEntity> list = friendRepo.findByMemberno(1);
		for (FriendEntity fe : list) {
			System.out.println(fe.getFriend().getName());
			System.out.println(fe.getFriend().getEmail());
		}
		
	}
	
}
