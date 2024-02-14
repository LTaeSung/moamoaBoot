package don.us.point;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/point/funding_history")
public class FundingHistoryController {
	@Autowired
	private FundingHistoryRepository repo;
	
	@GetMapping("/mypointHistory")
    public List<FundingHistoryEntity> pointHistory(@RequestParam(value="member_no") int member_no) {
    	List<FundingHistoryEntity> pointList = repo.findByMemberno(member_no);
    	return pointList;
    }
}
