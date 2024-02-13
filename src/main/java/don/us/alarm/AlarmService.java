package don.us.alarm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import don.us.funding.FundingEntity;
import don.us.funding.FundingMemberEntity;
import don.us.funding.FundingMemberRepository;

@Service
public class AlarmService {
	@Autowired
	private FundingMemberRepository fundingMemberRepo;
	
	@Autowired
	private AlarmRepository repo;
	
	public void makeAlarm(FundingEntity fund) {
		List<FundingMemberEntity> memberList = fundingMemberRepo.findByFundingno(fund.getNo());
		
		for(FundingMemberEntity member : memberList) {
			if(member.getMemberno() != fund.getStartmemberno()) {
				System.out.println("추가된 맴버: " + member);
			}
		}
		
	}
}
