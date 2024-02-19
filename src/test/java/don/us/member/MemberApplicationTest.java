package don.us.member;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import don.us.funding.FundingMemberRepository;
import jakarta.transaction.Transactional;
import lombok.extern.java.Log;

@SpringBootTest
@Log
@Commit
public class MemberApplicationTest {
	@Autowired
	private MemberRepository memberRepo;
	
	@Autowired
	private FundingMemberRepository fundingmemrepo;
	
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
	
	//친구 추가 기능 구현 (신정훈 작업 2024 - 02 - 12 작업)
	@Test
	@Transactional
	public void addFriend() {
		
		// 흐름 
	    int memberNo = 2;
	    
	    int friendNo = 3;
	    // 새로운 친구 엔티티 생성
	    FriendEntity friend = new FriendEntity();

	    // 회원 엔티티 생성
	    MemberEntity member1 = memberRepo.findById(memberNo).orElse(null);
	    MemberEntity member2 = memberRepo.findById(friendNo).orElse(null);

	    if (member1 != null && member2 != null) {
	        // 친구 엔티티에 회원 엔티티 설정
	        friend.setMemberno(member1.getNo());
	        friend.setFriend(member2);

	        // 친구 엔티티 저장
	        friendRepo.save(friend);

	        // 출력 확인
	        System.out.println("친구 출력 : " + friend);
	    } else {
	        System.out.println("회원을 찾을 수 없습니다.");
	    }
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
	
	
	// 친구 검색 기능 구현 (신정훈 작업 2024 - 02 - 12)
	@Test
	public void MemberList() {
		List<MemberEntity> memberList = memberRepo.findByNameContaining("김병천");
		
		System.out.println("검색된 멤버 리스트: " + memberList.toString());
			
	}
	
	// 친구 삭제 기능 구현 (신정훈 작업 2024 - 02 - 12)
	@Test
	@Transactional
	public void FriendDelete() {
		List <FriendEntity> friendLost = friendRepo.deleteByMembernoAndFriend_No(4,1);
		
		System.out.print("삭제된 친구 리스트: " + friendLost.toString());
	}
	
	
	@Test
	public void getFriend() {
		List<FriendEntity> friendList = friendRepo.findByMemberno(6);
		System.out.println("list " + friendList.toString());
	}
	
	@Test
	public void leave() {
		String member_no = "4";
		
		List list = new ArrayList(); 
		list = fundingmemrepo.findByMemberno(Integer.parseInt(member_no));
		System.out.println(list);
	}
	
}
