package don.us.admin;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
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
import don.us.point.PointHistoryRepository;
import don.us.point.RepaymentRepository;
import util.file.HandleDays;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/statistics")
public class StatisticsController {
	@Autowired
	private FundingHistoryRepository fundingHistoryRepo;
	
	@Autowired
	private PointHistoryRepository pointHistoryRepo;
	
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
	
	@Autowired 
	private StatisticsService statisticService;
	
	@GetMapping("/default")
	public HashMap<String, BigDecimal> defaultStatistics(){
		HashMap<String, BigDecimal> map = new HashMap<>();
		map.put("avgSettlement", fundingMemberRepo.getAvgSettlementStatistics()); //인당 평균 정산금액
		map.put("avgMonthlyPayAmount", fundingRepo.getAvgMonthlyPayAmountStatistics()); //펀드당 평균 월결제금액
		map.put("avgMonthlyCollected", fundingRepo.getAvgMonthlyCollectedStatistics()); //완료된 펀드당 평균 총 결제금액
		map.put("monthlyNewFund", fundingRepo.getMonthlyNewFundStatistics()); //이번달 펀드 총 개최 수
		map.put("monthlyMember", fundingMemberRepo.getMonthlyMemberStatistics()); //이번달 펀드 총 참여인원
		BigDecimal monthlypay = fundingHistoryRepo.getMonthlyPayStatistics().add(pointHistoryRepo.getMonthlyPayStatistics());
		map.put("monthlyPay", monthlypay); //이번달 펀드 총 금액
		return map;
	}
	
	@GetMapping("/giveup")
	public Map<String, BigDecimal> giveupStatistics(){
		Map<String, BigDecimal> map = fundingMemberRepo.getGiveupStatistics();
		return map;
	}
	
	@GetMapping("/successAndFail")
	public Map<String, BigDecimal> successAndFail(){
		Map<String, BigDecimal> map = fundingMemberRepo.getSuccessFailStatistics();
		return map;
	}
	
	@GetMapping("/personal")
	public Map<String, BigDecimal> personal(){
		Map<String, BigDecimal> map = fundingMemberRepo.getPersonalStatistics();
		return map;
	}
	
	@GetMapping("/rank/joinedFund")
	public List<Map> getRankOrderByJoinedFundNumber(){
		return  statisticService.getRankOrderByJoinedFundNumber();
	}
	@GetMapping("/rank/totalPay")
	public List<Map> getRankOrderByTotalPayAmount(){
		return  statisticService.getRankOrderByTotalPayAmount();
	}
	@GetMapping("/rank/totalGet")
	public List<Map> getRankOrderByTotalGet(){
		return  statisticService.getRankOrderByTotalGet();
	}
}
