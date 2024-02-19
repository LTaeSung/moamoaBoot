package don.us.funding;

import java.sql.Timestamp;
import java.util.ArrayList;
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
	
	@GetMapping("invitedList")
	public List<FundingMemberEntity> getInvitedList(@RequestParam("member_no") int member_no) {
		List<FundingMemberEntity> result = new ArrayList<>();
		
		List<FundingMemberEntity> allFundOfMe = repo.findByMemberno(member_no);
		
		for(FundingMemberEntity fund : allFundOfMe) {
			if(fund.getParticipationdate() != null) {
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
		System.out.println("실행은되나");
		Map row = repo.getMyFundInfo(no, member_no);
		Map result = new HashMap<>();
		result.put("myFundInfo", service.setMapOfFundingAndMember(row));
		return result;
	}
	
	@GetMapping("/challenge/{fund_no}")
	public List funding (@PathVariable int fund_no, Model model) {

		List <FundingMemberEntity>  fundingMemberEntity = repo.findByFundingno(fund_no);
		System.out.println("ㅎㅎ: " + fundingMemberEntity);
		return fundingMemberEntity;
	}

	@PostMapping("modifycard")
	public String modifycard(@RequestBody Map map) {
		System.out.println("Map.fundingNo: " + map.get("fundingNo"));
		System.out.println("Map.fundingNo: " + map.get("memberNo"));
 		String fundMemberNo_string = (String)map.get("memberNo");
		int fundMemberNo = Integer.parseInt(fundMemberNo_string);
		System.out.println("fundMemberNo: " + fundMemberNo);
		String fundingNo_string = (String)map.get("fundingNo");
		int fundingNo = Integer.parseInt(fundingNo_string);
		FundingMemberEntity fundMemberEntity = repo.findByFundingnoAndMemberno(fundingNo, fundMemberNo);
		System.out.println("fundMemberEntity: " + fundMemberEntity);
		
		int payment_no = Integer.valueOf((String)map.get("payment_no"));
		fundMemberEntity.setPaymentno(payment_no);
		try {
			repo.save(fundMemberEntity);
			
			System.out.println("카드 수정 완료");
			
			return "success";
		}catch(Exception e) {
			System.out.println("카드 수정 실패");
			return "fail";
		}
	}
	

}
