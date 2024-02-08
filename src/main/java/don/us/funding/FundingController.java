package don.us.funding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import don.us.board.BoardEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
	@GetMapping("/list")
	public List index (Model model) {

		List <FundingEntity>  fundingEntityList = repo.findAll();

		return fundingEntityList;
	}
	@GetMapping("/reg")
	public Map<String, Object> fundList(@RequestBody Map map){
		FundingEntity fundingEntity = new FundingEntity();
		fundingEntity.setNo(Integer.parseInt((String)map.get("no")));
		fundingEntity.setTitle((String) map.get("title"));
		fundingEntity.setDescription((String) map.get("description"));
//		fundingEntity.setPhoto((String) map.get("photo")); // 추후에 추가 예정
		fundingEntity.setCandidate((Integer) map.get("candidate"));
		fundingEntity.setExpected_payment_amount((Integer) map.get("expected_payment_amount"));
		fundingEntity.setGoal_amount((Integer) map.get("goal_amount"));
		fundingEntity.setMonthly_payment_amount((Integer) map.get("monthly_payment_amount"));
		fundingEntity.setMonthly_payment_date((Integer) map.get("monthly_payment_date"));
		fundingEntity.setComplete_interest((Integer) map.get("complete_interest"));

		FundingEntity entity = repo.save(fundingEntity);
		Map<String, Object> result = new HashMap<>();
		result.put("entity", entity);
		result.put("result", entity == null ? "fail" : "success");
		return result;
	}
}
