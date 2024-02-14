package don.us.funding;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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

import jakarta.persistence.EntityManager;



@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/funding/member")
public class FundingMemberController {
	@Autowired
	private FundingMemberRepository repo;
	
	@Autowired
	private EntityManager entityManager;
	
	@GetMapping("invitedList")
	public List<FundingMemberEntity> getInvitedList(@RequestParam("member_no") int member_no) {
		List<FundingMemberEntity> result = new ArrayList<>();
		
		List<FundingMemberEntity> allFundOfMe = repo.findByMemberno(member_no);
		
		for(FundingMemberEntity fund : allFundOfMe) {
			if(fund.getParticipationdate() != null) {
				continue;
			}
			if(isExpired(fund)) {
				continue;
			}
			
			result.add(fund);
		}
		
		return result;
	}
	
	private boolean isExpired(FundingMemberEntity fund) {
		Timestamp invitedDate = fund.getInviteddate();
		Calendar cal = Calendar.getInstance();
		cal.setTime(invitedDate);
		cal.add(Calendar.DATE, 7);
		invitedDate.setTime(cal.getTime().getTime());
		
		Timestamp today = new Timestamp(System.currentTimeMillis());
		
		if(invitedDate.before(today)) {
			return true;
		}else {
			return false;
		}
	}
	
	@PostMapping("accept")
	public String accept(@RequestBody Map map) {
		Map<String, String> result = new HashMap<>();
		System.out.println("map: " + map);
		int fundMemberNo = Integer.valueOf((String)map.get("no"));
		FundingMemberEntity fundMemberEntity = repo.findById(fundMemberNo).get();
		fundMemberEntity.setParticipationdate(new Timestamp(System.currentTimeMillis()));
		try {
			repo.save(fundMemberEntity);
			System.out.println("참여 완료");
			return "success";
		}catch(Exception e) {
			System.out.println("참여 실패");
			return "fail";
		}
	}
	@PostMapping("refuse")
	public String refuse(@RequestBody Map map) {
		Map<String, String> result = new HashMap<>();
		int fundMemberNo = Integer.valueOf((String)map.get("no"));
		FundingMemberEntity fundMemberEntity = new FundingMemberEntity();
		fundMemberEntity.setNo(fundMemberNo);
		try {
			repo.delete(fundMemberEntity);
			System.out.println("삭제 완료");
			return "success";
		}catch(Exception e) {
			System.out.println("삭제 실패");
			return "fail";
		}
	}
	
	
	@GetMapping("/join")
	public List<FundingMemberEntity> joinList (@RequestParam("member_no") int member_no){
		
		String jpql ="SELECT fm FROM FundingMemberEntity fm "
				+ "WHERE fm.memberno = :member_no AND fm.startmemberno != :start_member_no";
		
		List<FundingMemberEntity> joinList = entityManager.createQuery(jpql,FundingMemberEntity.class)
															.setParameter("member_no" , member_no)
															.setParameter("start_member_no" , member_no)
															.getResultList();
		if (joinList.isEmpty()) {
			System.out.println("참여한 모금이 없습니다.");
		}else {
			System.out.println("모금 리스트: " + joinList);
		}
		
		return joinList;
		
		
	}
}
