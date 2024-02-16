package util.file;

import java.sql.Timestamp;
import java.util.Calendar;

import don.us.funding.FundingMemberEntity;

public class HandleDays {
	public Timestamp addDays(Timestamp target, int day) {
		Timestamp result = new Timestamp(target.getTime());
		Calendar cal = Calendar.getInstance();
		cal.setTime(result);
		cal.add(Calendar.DATE, day);
		result.setTime(cal.getTime().getTime());
		return result;
	}
	
	public boolean isExpired(FundingMemberEntity fund) {
		Timestamp invitedDueDate = addDays(fund.getInviteddate(), 7);
		
		Timestamp today = new Timestamp(System.currentTimeMillis());
		
		if(invitedDueDate.before(today)) {
			return true;
		}else {
			return false;
		}
	}
}
