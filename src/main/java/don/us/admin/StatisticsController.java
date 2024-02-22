package don.us.admin;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import don.us.alarm.AlarmService;
import don.us.funding.FundingController;
import don.us.funding.FundingMemberRepository;
import don.us.funding.FundingRepository;
import don.us.funding.FundingService;
import don.us.point.FundingHistoryRepository;
import don.us.point.RepaymentRepository;
import util.file.HandleDays;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/statistics")
public class StatisticsController {
	@Autowired
	private FundingHistoryRepository fundingHistoryRepo;
	
	@Autowired
	private RepaymentRepository repayRepo;
	
	@Autowired
	private FundingRepository fundingRepo;
	
	@Autowired
	private FundingMemberRepository fundingMemberRepo;
	
	@Autowired
	private MainTotalRepository mainTotalRepo;
	
	@Autowired
	private AlarmService alarmService;
	
	@Autowired
	private FundingService fundingService;
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private FundingController fundingController;
	
	@Autowired
	private HandleDays handleDays;
	
	@GetMapping("/giveup")
	public Map<String, Integer> giveupStatistics(){
		Map<String, Integer> map = fundingMemberRepo.getGiveupStatistics();
		return map;
	}
	
	@GetMapping("/successAndFail")
	public Map<String, Integer> successAndFail(){
		Map<String, Integer> map = fundingMemberRepo.getSuccessFailStatistics();
		return map;
	}
}
