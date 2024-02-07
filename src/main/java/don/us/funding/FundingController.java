package don.us.funding;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/fund")
public class FundingController {
	@Autowired
	private FundingRepository repo;
	
	@Autowired
	private FundingService service;
	
	@PostMapping("/regist")
	public void makeFund(@RequestBody Map map) {
		//임시로 member_no를 4로 설정
		int member_no = 4;
		
		
		//임시로 payment_no를 1로 설정
		int payment_no = 1;
		
		System.out.println("ㅇㅇ");
		System.out.println(map);
	}
}
