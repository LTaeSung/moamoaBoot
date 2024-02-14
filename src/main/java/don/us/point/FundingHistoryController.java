package don.us.point;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import don.us.funding.FundingMemberEntity;
import don.us.funding.FundingService;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/point/funding_history")
public class FundingHistoryController {
	@Autowired
	private FundingHistoryRepository repo;
	
	@Autowired
	private FundingService fundingService;
	
	@GetMapping("/mypointHistory")
    public List<FundingHistoryEntity> pointHistory(@RequestParam(value="member_no") int member_no) {
    	List<FundingHistoryEntity> pointList = repo.findByMembernoOrderByTransactiondateDesc(member_no);
    	return pointList;
    }
	
	@Transactional
	@GetMapping("/regularPayment")
	public void regularPayment() {
		ArrayList<List<FundingMemberEntity>> list = fundingService.needPayMemberList();
		for(int i=0; i<list.size(); i++) {
			for(int j=0; j<list.get(i).size(); j++) {
				FundingHistoryEntity fundingHistory = new FundingHistoryEntity();
				fundingHistory.setMemberno(list.get(i).get(j).getMemberno());
				fundingHistory.setFundingno(list.get(i).get(j).getFundingno());
				fundingHistory.setAmount(list.get(i).get(j).getMonthlypaymentamount());
				//fundingHistory.setDirection(false); //0=false가 디폴트값이라 따로 설정 안하고 반대로 펀딩에서 돈 줄 때 true로 세팅할게요
				repo.save(fundingHistory);
			}
		}
	}
}
