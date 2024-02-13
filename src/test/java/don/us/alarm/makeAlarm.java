package don.us.alarm;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import don.us.funding.FundingEntity;
import lombok.extern.java.Log;
@SpringBootTest@Log
public class makeAlarm {
	@Autowired
	AlarmService service;
	
	@Test
	public void testMakeAlarm() {
		FundingEntity fund = new FundingEntity();
		fund.setStartmemberno(2);
		fund.setNo(52);
		service.makeAlarm(fund);
	}
}
