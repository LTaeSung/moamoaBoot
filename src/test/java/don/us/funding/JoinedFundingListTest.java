package don.us.funding;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.java.Log;

@SpringBootTest@Log
public class JoinedFundingListTest {
	@Autowired
	private FundingMemberRepository repo;
	
	@Autowired
	private FundingMemberService service;
	
	@Test
	public void testJoinedList() {
		List<Map> list = repo.getJoinedFundingList_OnGoing("14");
		System.out.println(list);
		for(Map m : list) {
			System.out.println(m.get("fundingTitle"));
		}
	}
	
	@Test
	public void testSeperateFundState() {
		List<Map> list = repo.getJoinedFundingList_OnGoing("14");
		list = service.seperateFundState(list);
	}
}
