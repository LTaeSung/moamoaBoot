package don.us.funding;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.java.Log;

@SpringBootTest@Log
public class Before7DaysTest {
	
	@Autowired
	FundingMemberController controller;
	
	@Test
	public void test() {
		
	}
}
