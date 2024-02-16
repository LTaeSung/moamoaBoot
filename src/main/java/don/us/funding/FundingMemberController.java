package don.us.funding;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
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


@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/funding/member")
public class FundingMemberController {
	@Autowired
	private FundingMemberRepository repo;
	
	@Autowired
	private FundingService fundingService;
	
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
		Timestamp invitedDueDate = addDays(fund.getInviteddate(), 7);
		
		Timestamp today = new Timestamp(System.currentTimeMillis());
		
		if(invitedDueDate.before(today)) {
			return true;
		}else {
			return false;
		}
	}
	
	private Timestamp addDays(Timestamp target, int day) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(target);
		cal.add(Calendar.DATE, day);
		target.setTime(cal.getTime().getTime());
		return target;
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
	
	
	@GetMapping("/join")
	public List<Map> joinList (@RequestParam("member_no") String member_no){
		List<Map> rowList = repo.getJoinedFundingList_OnGoing(member_no);
		
		List<Map> result = new ArrayList<>();
		for(Map fund : rowList) {
			Map target = new HashMap<>();
			target.put("title", fund.get("fundingTitle"));
			target.put("state", fund.get("state"));
			target.put("myPayAmount", fund.get("myPayAmount"));
			target.put("totalPayAmount", fund.get("totalPayAmount"));
			
			setDueDate(target, fund);
			setStateMessage(target, fund);
			result.add(target);
		}
		
		return result;
	}
	private void setStateMessage(Map result, Map fund) {
		int state = (Integer)fund.get("state");
		switch(state) {
		case 0://초대중
			if(fund.get("participationDate") == null) {
				result.put("stateMessage", "펀드에 참여해주세요!");
				result.put("color", "red");
			}else {
				result.put("stateMessage", "초대중이에요!");
				result.put("color", "black");
			}
			break;
		case 1://진행중
			System.out.println("fund: " + fund.get("giveup"));
			if((boolean)fund.get("giveup") == false) {
				result.put("stateMessage", "진행중");
				result.put("color", "black");
			}else {
				result.put("stateMessage", "중도포기");
				result.put("color", "black");
			}
			break;
		case 2://투표중
			if((int)fund.get("vote") == 0) {
				result.put("stateMessage", "결과를 입력해주세요!");
				result.put("color", "red");
			}else {
				result.put("stateMessage", "집계중");
				result.put("color", "black");
			}
			break;
		case 3://정산중
			if(fund.get("settlementAmount") == null) {
				result.put("stateMessage", "정산받아가세요!");
				result.put("color", "red");
			}else {
				result.put("stateMessage", "정산중");
				result.put("color", "black");
			}
			break;
		}
	}
	
	private void setDueDate(Map result, Map fund) {
		int state = (Integer)fund.get("state");
		int dueDay_Left = 9999999;
		Timestamp dueDate = null;
		switch(state) {
		case 0://초대중
			dueDate = addDays((Timestamp)fund.get("startDate"), 7);
			result.put("dueDate", dueDate);
			result.put("dueDateLeft", leftDays(dueDate));
			break;
		case 1://진행중
			dueDate = (Timestamp)fund.get("fundingDueDate");
			result.put("dueDate", dueDate);
			result.put("dueDateLeft", leftDays(dueDate));
			break;
		case 2://투표중
			dueDate = (Timestamp)fund.get("voteDueDate");
			result.put("dueDate", dueDate);
			result.put("dueDateLeft", leftDays(dueDate));
			break;
		case 3://정산중
			dueDate = (Timestamp)fund.get("settlementDueDate");
			result.put("dueDate", dueDate);
			result.put("dueDateLeft", leftDays(dueDate));
			break;
		}
	}
	
	private int leftDays(Timestamp dueDate) {
		int day = (int)((dueDate.getTime() - System.currentTimeMillis()) / (1000 * 60 * 60 * 24));
		return day;
	}
	
	@GetMapping("/challenge/{fund_no}")
	public List funding (@PathVariable int fund_no, Model model) {

		List <FundingMemberEntity>  fundingMemberEntity = repo.findByFundingno(fund_no);
		System.out.println("ㅎㅎ: " + fundingMemberEntity);
		return fundingMemberEntity;
	}


//	@GetMapping("/challenge/{funding_no}")
//	public FundingMemberEntity show(@PathVariable int funding_no) {
////		FundingEntity> result = new ArrayList<>();
//		FundingMemberEntity result = null;
//		result = repo.findByFundingno(funding_no).get(funding_no);
////		repo.findById(no).ifPresent((data) -> {
////			result = data;
////		});
//		System.out.println("result: " + result);
//		return result;
//	}

}
