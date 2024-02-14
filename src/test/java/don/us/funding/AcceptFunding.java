package don.us.funding;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.java.Log;

@SpringBootTest@Log
public class AcceptFunding {
	
	@Autowired
	private FundingMemberService service;
	@Autowired
	private FundingService fundingService;
	
	@Test
	public void testIncreaseMember() {
		int fund_no = 59;
		fundingService.increaseCandidate(fund_no);
		
	}
	
	@Test
	public void testStartFunding() {
		int fund_no = 59;
		if(fundingService.checkStartFundingWhenAcceptFund(fund_no)) {
			fundingService.setFundStart(fund_no);
		}
	}
}
