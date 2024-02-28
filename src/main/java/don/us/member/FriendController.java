package don.us.member;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.websocket.Session;
import retrofit2.http.DELETE;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/member/friend")
public class FriendController {
	@Autowired
	private FriendRepository frRepo;
	
	@Autowired
	private MemberRepository meRepo;
	
	@Autowired
    private EntityManager entityManager;
	
	// 친구 정보 불러오기 (신정훈 작업 2024 - 02 - 07)
	@GetMapping("/list")
	public List<FriendEntity> friendList(@RequestParam("member_no") int member_no) {
		List<FriendEntity> friendList = frRepo.findByMemberno(member_no);
			return friendList;
	}
	
	// 친구 검색 기능 
	// 개발 의도: 한번에 멤버 목록을 다 가져오면 시간 소요 검색한 멤버만 나오게 하려함
	@Transactional
	@GetMapping("/search")
	public List<FriendEntity> friendList(@RequestParam("member_no") int member_no, @RequestParam("email") String email) {
		String jpql = "SELECT m FROM MemberEntity m " +
                "WHERE m.id NOT IN (" +
                "    SELECT f.friend.id " + // 여기서 FriendEntity의 friend 속성 대신에 멤버 엔티티의 ID 속성을 사용합니다.
                "    FROM FriendEntity f " +
                "    WHERE f.memberno = :member_no" +
                ") AND m.email LIKE :email";

	    
		List<FriendEntity> friendList = entityManager.createQuery(jpql, FriendEntity.class)
	                                                 .setParameter("member_no", member_no)
	                                                 .setParameter("email", email + "@naver.com")
	                                                 .getResultList();
	    return friendList;
	}
	
	
	// 친구 추가 기능 (신정훈 작업 2024 - 02 - 07)	
	@Transactional
	@GetMapping("/input")
	public FriendEntity friendAdd(@RequestParam("member_no") int member_no, @RequestParam("friend_no") int friend_no) {
		
		FriendEntity friend = new FriendEntity();
	    MemberEntity member = meRepo.findById(friend_no).get();
	    
	    friend.setMemberno(member_no);
	    friend.setFriend(member);
	    
		FriendEntity entity = frRepo.save(friend);
		
		return entity;
	}
	
	// 친구 삭제 기능 (신정훈 작업 2024 - 02 - 07)	
	@Transactional
	@GetMapping("/delete")
	public List<FriendEntity> friendDel(@RequestParam("member_no") int member_no, @RequestParam("friend_no") int friend_no){
		
		List <FriendEntity> friendLost = frRepo.deleteByMembernoAndFriend_No(member_no,friend_no);
		
		
		Map<String, Object> result = new HashMap<>();
		result.put("result", "success");
	
		List<FriendEntity> updatedFriendList = frRepo.findByMemberno(member_no);
		return updatedFriendList;
		
	}
}
