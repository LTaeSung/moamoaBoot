package don.us.member;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
		MemberEntity ls = memberRepo.findByEmail("rlaqudcjs96@naver.com").get();
		
			
			System.out.println("유저 번호: "+ ls.getNo());
			System.out.println("    이름: "+ ls.getName());
			System.out.println("   이메일: "+ ls.getEmail());

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
//	// 친구 정보 불러오기 (신정훈 작업 2024 - 02 - 07)
//	@GetMapping("/list")
//	public List friendList(@RequestParam("member_no") int member_no) {
//		List<FriendEntity> friendList = frRepo.findByMemberno(member_no);
//		
//			System.out.println("잘나온다 병천아~~~ " + friendList.toString());
//			
//			return friendList;
//	}
	@Test
	public void getFriend() {
		List<FriendEntity> friendList = friendRepo.findByMemberno(6);
		System.out.println("list " + friendList.toString());
	}
}
