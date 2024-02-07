package don.us.member;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
	
	
	@GetMapping("/list")
	public List friendList(@RequestParam("member_no") int member_no) {
		List<FriendEntity> ls = frRepo.findByMemberno(member_no);
		
			System.out.println("잘나온다 병천아~~~ " + ls.toString());
			
			return ls;
	}
	
		
	@PostMapping("/input")
	@Transactional
	public FriendEntity friendAdd(@RequestParam("member_no") int member_no, @RequestParam("friend_no") int friend_no) {
		
		FriendEntity friend = new FriendEntity();
	    MemberEntity member = meRepo.findById(friend_no).get();
	    
	    friend.setMemberno(member_no);
	    friend.setFriend(member);
	    
	    
		System.out.println("친구 출력 : " + friend);
		FriendEntity entity = frRepo.save(friend);
		return entity;
	}
	

}
