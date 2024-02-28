package don.us.funding;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import don.us.admin.AdminService;
import util.file.HandleDays;


@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/funding/member")
public class FundingMemberController {
	@Autowired
	private FundingMemberRepository repo;
	
	@Autowired
	private FundingMemberService service;
	
	@Autowired
	private FundingService fundingService;
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private HandleDays handleDays;
	
	@Autowired
	private FundingRepository fundrepo;
	
	@GetMapping("invitedList")
	public List<Map> getInvitedList(@RequestParam("member_no") int member_no) {
		List<Map> result = new ArrayList<>();
		
		List<Map> allFundOfMe = repo.getInvitedFundinglist(member_no);

		
		for(Map fund : allFundOfMe) {
			if(fund.get("participationDate") != null) {
				continue;
			}
			if(handleDays.isExpired(fund)) {
				continue;
			}
			result.add(fund);
		}
		
		return result;
	}
	
	@PostMapping("accept")
	public String accept(@RequestBody Map map) {
		Map<String, String> result = new HashMap<>();
		int fundMemberNo = (int)map.get("fundingMemberNo");
		FundingMemberEntity fundMemberEntity = repo.findById(fundMemberNo).get();
		fundMemberEntity.setParticipationdate(new Timestamp(System.currentTimeMillis()));
		
		int payment_no = (int)map.get("payment_no");
		fundMemberEntity.setPaymentno(payment_no);
		try {
			repo.save(fundMemberEntity);
			
			int fund_no = fundMemberEntity.getFundingno();
			fundingService.increaseCandidate(fund_no);
			if(fundingService.checkStartFundingWhenAcceptFund(fund_no)) {
				fundingService.setFundStart(fund_no);
			}
			
			return "success";
		}catch(Exception e) {
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
			repo.delete(fundMemberEntity);
			if(fundingService.checkStartFundingWhenAcceptFund(fund_no)) {
				fundingService.setFundStart(fund_no);
			}
			return "success";
		}catch(Exception e) {
			e.printStackTrace();
			return "fail";
		}
	}
	
	@GetMapping("/join/ongoing")
	public List<Map> joinListOnGoing (@RequestParam("member_no") String member_no){
		List<Map> rowList = repo.getJoinedFundingList_OnGoing(member_no);
		
		List<Map> result = new ArrayList<>();
		for(Map fund : rowList) {

			result.add(service.setMapOfFundingAndMember(fund));
		}
		
		return result;
	}
	


	
	@GetMapping("/join/end")
	public List<Map> joinListEnd (@RequestParam("member_no") String member_no){
		List<Map> rowList = repo.getJoinedFundingList_End(member_no);
		
		List<Map> result = new ArrayList<>();
		for(Map fund : rowList) {

			result.add(service.setMapOfFundingAndMember_End(fund));
		}
		
		return result;
	}
	

	@GetMapping("/info")
	public Map myFundingInfo(@RequestParam("no") String no, @RequestParam("member_no") String member_no){
		Map row = null;
		try {
			row = repo.getMyFundInfo(no, member_no);

			Map result = new HashMap<>();
			result.put("myFundInfo", service.setMapOfFundingAndMember(row));
			return result;	
		}catch(Exception e) {
			return row;
		}
	}
	
	@GetMapping("/challenge/{fund_no}")
	public List funding (@PathVariable int fund_no, Model model) {

		List <FundingMemberEntity>  fundingMemberEntity = repo.findByFundingno(fund_no);
		return fundingMemberEntity;
	}

	@PostMapping("modifycard")
	public String modifycard(@RequestBody Map map) {
 		String fundMemberNo_string = (String)map.get("memberNo");
		int fundMemberNo = Integer.parseInt(fundMemberNo_string);
		String fundingNo_string = (String)map.get("fundingNo");
		int fundingNo = Integer.parseInt(fundingNo_string);
		FundingMemberEntity fundMemberEntity = repo.findByFundingnoAndMemberno(fundingNo, fundMemberNo);
		
		int payment_no = (int)map.get("payment_no");
		fundMemberEntity.setPaymentno(payment_no);
		try {
			repo.save(fundMemberEntity);
			
			
			return "success";
		}catch(Exception e) {
			return "fail";
		}
	}
	

}
