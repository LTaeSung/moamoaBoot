package don.us.funding;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = { "*" })
@RestController
@RequestMapping("/fund")
public class FundingController {
	@Autowired
	private FundingRepository repo;

	@Autowired
	private FundingService service;

	@PostMapping("/regist")
	public void makeFund(@RequestBody Map map) {
		FundingEntity fund = new FundingEntity();

		fund.setStartmemberno(Integer.valueOf((String) (map.get("member_no"))));
		fund.setTitle((String)map.get("title"));
		//마감일 추가해야함
		fund.setDescription((String)map.get("description"));
		fund.setMonthlypaymentamount(Integer.valueOf((String) (map.get("monthly_payment_amount"))));
		fund.setMonthlypaymentdate((String)map.get("monthly_payment_date"));
		System.out.println("fund: " + fund);

		// 임시로 payment_no를 1로 설정
		int payment_no = 1;

		System.out.println("ㅇㅇ");
		System.out.println(map);
	}
}
