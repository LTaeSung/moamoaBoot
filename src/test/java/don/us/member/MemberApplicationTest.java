package don.us.member;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import jakarta.transaction.Transactional;
import lombok.extern.java.Log;

@SpringBootTest
@Log
@Commit
public class MemberApplicationTest {
	@Autowired
	private MemberRepository memberRepo;
	
	@Autowired
	private FriendRepository friendRepo;
	
	//신원 조회
	@Test
	public void searchMember() {
		List <MemberEntity> ls = memberRepo.findByEmail("rlaqudcjs96@naver.com");
		for (MemberEntity me : ls) {
			
			System.out.println("유저 번호: "+ me.getNo());
			System.out.println("    이름: "+ me.getName());
			System.out.println("   이메일: "+ me.getEmail());
		}
	}
	
	//친구 추가 기능
	@Test
	@Transactional
	public void addFriend() {
		
		// 흐름 
		int member_no = 2;
		
		FriendEntity friend = new FriendEntity();
	    MemberEntity member = memberRepo.findById(member_no).get();
		
		friend.setMemberno(member_no);
		friend.setFriend(member);
		
		System.out.println("친구 출력 : " + friend);
		friendRepo.save(friend);

	}
	
}
