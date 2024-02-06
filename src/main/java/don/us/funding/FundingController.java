package don.us.funding;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/funding")
public class FundingController {
	@Autowired
	private FundingRepository repo;
	
	@PostMapping("/makeFund")
	public void makeFund() {
		
	}
}
