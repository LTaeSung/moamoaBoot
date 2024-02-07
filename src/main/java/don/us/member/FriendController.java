package don.us.member;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.transaction.Transactional;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/member/friend")
public class FriendController {
	@Autowired
	private FriendRepository frRepo;
	
	@Autowired
	private MemberRepository meRepo;
	
	
	// 친구 정보 불러오기 (신정훈 작업 2024 - 02 - 07)
	@GetMapping("/list")
	public List friendList(@RequestParam("member_no") int member_no) {
		List<FriendEntity> friendList = frRepo.findByMemberno(member_no);
		
			System.out.println("잘나온다 병천아~~~ " + friendList.toString());
			
			return friendList;
	}
	
	// 친구 추가 기능 (신정훈 작업 2024 - 02 - 07)	
	@Transactional
	@PostMapping("/input")
	public FriendEntity friendAdd(@RequestParam("member_no") int member_no, @RequestParam("friend_no") int friend_no) {
		
		FriendEntity friend = new FriendEntity();
	    MemberEntity member = meRepo.findById(friend_no).get();
	    
	    friend.setMemberno(member_no);
	    friend.setFriend(member);
	    
		System.out.println("친구 출력 : " + friend);
		FriendEntity entity = frRepo.save(friend);
		return entity;
	}
	
	// 친구 삭제 기능 (신정훈 작업 2024 - 02 - 07)	
	@Transactional
	@PostMapping("/delete")
	public Map friendDel(@RequestParam("member_no") int member_no, @RequestParam("friend_no") int friend_no){
		
		frRepo.findByMemberno(member_no);
		frRepo.deleteByFriendNo(friend_no);
		
		Map<String, Object> result = new HashMap<>();
		result.put("result", "success");
		return result;
		
	}
}
