package don.us.funding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/funding/member")
public class FundingMemberController {
	@Autowired
	private FundingMemberRepository repo;
	
	@GetMapping("invitedList")
	public List<FundingMemberEntity> getInvitedList(@RequestParam("member_no") int member_no) {
		List<FundingMemberEntity> result = new ArrayList<>();
		
		List<FundingMemberEntity> allFundOfMe = repo.findByMemberno(member_no);
		System.out.println("allFundOfMe" + allFundOfMe);
		
		for(FundingMemberEntity fund : allFundOfMe) {
			if(fund.getParticipationdate() != null) {
				continue;
			}
			result.add(fund);
		}
		
		System.out.println("result: " + result);
		return result;
	}
}
