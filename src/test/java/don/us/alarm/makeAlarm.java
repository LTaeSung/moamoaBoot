package don.us.alarm;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import don.us.funding.FundingMemberEntity;
import lombok.extern.java.Log;

@SpringBootTest
@Log
public class makeAlarm {
	@Autowired
	AlarmService service;

	@Test
	public void testMakeInviteAlarm() {
		FundingMemberEntity fundingMember = new FundingMemberEntity();
		fundingMember.setMemberno(4);
		fundingMember.setFundingno(69);
		fundingMember.setStartmembername("신정훈");
		fundingMember.setFundtitle("스테이시 콘서트 티켓 모금합니다.");

		service.makeInviteAlarm(fundingMember);
	}
	
}
