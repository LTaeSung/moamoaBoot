package don.us.alarm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/alarm")
public class AlarmController {
	@Autowired
	private AlarmRepository repo;
	
	@GetMapping("list")
	public List<AlarmEntity> list(@RequestParam("member_no") int member_no) {
		return repo.findByMembernoOrderByAlarmdateDesc(member_no);
	}
	
	@GetMapping("erase")
	public void erase(@RequestParam("alarm_no") int alarm_no) {
		AlarmEntity alarm = repo.findById(alarm_no).get();
		repo.delete(alarm);
	}
}
