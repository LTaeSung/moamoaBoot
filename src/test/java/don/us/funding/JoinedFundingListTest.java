package don.us.funding;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.java.Log;

@SpringBootTest@Log
public class JoinedFundingListTest {
	@Autowired
	private FundingMemberRepository repo;
	
	@Test
	public void testJoinedList() {
//		System.out.println(repo.getJoinedFundingList("4"));
	}
}
