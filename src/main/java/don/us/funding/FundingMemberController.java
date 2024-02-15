package don.us.funding;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
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
	private FundingService fundingService;
	
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
		int fundMemberNo = (int)map.get("fundingMemberNo");
		FundingMemberEntity fundMemberEntity = repo.findById(fundMemberNo).get();
		System.out.println("fundMemberEntity: " + fundMemberEntity);
		fundMemberEntity.setParticipationdate(new Timestamp(System.currentTimeMillis()));
		
		int payment_no = Integer.valueOf((String)map.get("payment_no"));
		fundMemberEntity.setPaymentno(payment_no);
		try {
			repo.save(fundMemberEntity);
			
			int fund_no = fundMemberEntity.getFundingno();
			fundingService.increaseCandidate(fund_no);
			if(fundingService.checkStartFundingWhenAcceptFund(fund_no)) {
				fundingService.setFundStart(fund_no);
			}
			System.out.println("참여 완료");
			
			
			
			return "success";
			
//			FundingEntity 
			//참여인원 늘리고
			//펀딩 시작하나 체크하고
		}catch(Exception e) {
			System.out.println("참여 실패");
			return "fail";
		}
	}
	
	
	
	@PostMapping("refuse")
	public String refuse(@RequestBody Map map) {
		Map<String, String> result = new HashMap<>();
		int fundMemberNo = Integer.valueOf((String)map.get("no"));
		FundingMemberEntity fundMemberEntity = repo.findById(fundMemberNo).get();
		try {
			int fund_no = fundMemberEntity.getFundingno();
			System.out.println("fund_no: " + fund_no);
			repo.delete(fundMemberEntity);
			if(fundingService.checkStartFundingWhenAcceptFund(fund_no)) {
				fundingService.setFundStart(fund_no);
			}
			System.out.println("삭제 완료");
			return "success";
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("삭제 실패");
			return "fail";
		}
	}
	
	
	@GetMapping("/join")
	public List<FundingMemberEntity> joinList (@RequestParam("member_no") int member_no){

		return null;
	}
}
