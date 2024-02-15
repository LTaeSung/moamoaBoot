package don.us.funding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FundingMemberService {
	@Autowired
	private FundingRepository fundingRepo;
	@Autowired
	private FundingMemberRepository fundingMemberRepo;

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
}
