package don.us.funding;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import util.file.HandleDays;

@Service
public class FundingMemberService {
	@Autowired
	private FundingRepository fundingRepo;
	@Autowired
	private FundingMemberRepository fundingMemberRepo;
	@Autowired
	private HandleDays handleDays;
	@Autowired
	private FundingMemberRepository repo;

	public List<Map> seperateFundState(List<Map> list) {
		for (Map m : list) {
			printMap(m);
		}

		return null;
	}

	private void printMap(Map<String, String> m) {
		String str = "{";
		int i = 0;
		for (Map.Entry entry : m.entrySet()) {
			str += entry.getKey() + "=" + entry.getValue();
			i++;
			if (i != m.size()) {
				str += ",";
			}
		}
		str += "}";
		System.out.println(str);
	}
	
	public Map setMapOfFundingAndMember(Map fund) {
		Map target = new HashMap<>();
		target.put("fundingNo", fund.get("fundingNo"));
		target.put("fundingMemberNo", fund.get("fundingMemberNo"));
		target.put("title", fund.get("fundingTitle"));
		target.put("state", fund.get("state"));
		target.put("myPayAmount", fund.get("myPayAmount"));
		target.put("totalPayAmount", fund.get("totalPayAmount"));
		target.put("photo", fund.get("photo"));
		target.put("willsettlementamount", fund.get("willsettlementamount"));

		
		setDueDate(target, fund);
		setStateMessage(target, fund);
		return target;
	}
	
	private void setStateMessage(Map result, Map fund) {
		int state = (Integer)fund.get("state");
		switch(state) {
		case 0://초대중
			System.out.println(fund.get("fundingTitle"));
			System.out.println(fund.get("participationDate"));
			if(fund.get("participationDate") == null) {
				//메세지 스테이트를 추가한다?
				result.put("stateMessage", "펀드에 참여해주세요!");
				result.put("color", "red");
				result.put("messageNo", 0);
			}else {
				result.put("stateMessage", "초대중이에요!");
				result.put("color", "black");
				result.put("messageNo", 1);
			}
			break;
		case 1://진행중
			System.out.println("fund: " + fund.get("giveup"));
			if((boolean)fund.get("giveup") == false) {
				result.put("stateMessage", "진행중");
				result.put("color", "black");
				result.put("messageNo", 2);
			}else {
				result.put("stateMessage", "중도포기");
				result.put("color", "black");
				result.put("messageNo", 3);
			}
			break;
		case 2://투표중
			if((int)fund.get("vote") == 0) {
				result.put("stateMessage", "결과를 입력해주세요!");
				result.put("color", "red");
				result.put("messageNo", 4);
			}else {
				result.put("stateMessage", "집계중");
				result.put("color", "black");
				result.put("messageNo", 5);
			}
			break;
		case 3://정산중
			if(fund.get("settlementAmount") == null) {
				result.put("stateMessage", "정산받아가세요!");
				result.put("color", "red");
				result.put("messageNo", 6);
			}else {
				result.put("stateMessage", "정산중");
				result.put("color", "black");
				result.put("messageNo", 7);
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
			dueDate = handleDays.addDays((Timestamp)fund.get("startDate"), 7);
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
	
	public Map setMapOfFundingAndMember_End(Map fund) {
		Map target = new HashMap<>();
		target.put("fundingNo", fund.get("fundingNo"));
		target.put("title", fund.get("fundingTitle"));
		target.put("myPayAmount", fund.get("myPayAmount"));
		target.put("settlementAmount", fund.get("settlementAmount"));
		target.put("photo", fund.get("photo"));
		
		setSuccessMessage(target, fund);
		
		return target;
	}
	
	private void setSuccessMessage(Map result, Map fund) {
		if((boolean)fund.get("giveup") == true) {
			result.put("message", "중도포기");
		}else if((int)fund.get("vote") == 1) {
			result.put("message", "성공");
		}else if((int)fund.get("vote") == 2) {
			result.put("message", "실패");
		}
	}
}
