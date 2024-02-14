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
		fundingService.increaseCandidate(59);
		
	}
	
	@Test
	public void testStartFunding() {
		System.out.println("result: " + service.checkStartFunding(65));
	}
}
