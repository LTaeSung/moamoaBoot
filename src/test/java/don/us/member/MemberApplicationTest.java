package don.us.member;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.transaction.Transactional;
import lombok.extern.java.Log;

@SpringBootTest
@Log
public class MemberApplicationTest {
	@Autowired
	private MemberRepository memberRepo;
	
	@Autowired
	private FriendRepository friendRepo;
	
	@Test
	public void searchMember() {
		List <MemberEntity> ls = memberRepo.findByEmail("rlaqudcjs96@naver.com");
		for (MemberEntity me : ls) {
			
			System.out.println("유저 번호: "+ me.getNo());
			System.out.println("    이름: "+ me.getName());
			System.out.println("   이메일: "+ me.getEmail());
		}
	}
	
	@Test
	@Transactional
	public void addFriend() {
		
		// 흐름 
		// 먼저 친구가 등록이 되어있는지 확인하고 없으면 등록이 되게 해야한다...
		int member_no = 2;
		//int friend_no = 1;
		
		FriendEntity friend = new FriendEntity();
		MemberEntity member = new MemberEntity();
		friend.setMemberno(member_no);
		friend.setFriend(member);
		
		
		
	}
}
