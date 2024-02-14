package don.us.funding;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FundingMemberService {
	@Autowired
	private FundingRepository fundingRepo;
	@Autowired
	private FundingMemberRepository fundingMemberRepo;
	
	
	public boolean checkStartFunding(int fundMember_no) {
		FundingMemberEntity fundingMemberEntity = fundingMemberRepo.findById(fundMember_no).get();
		int fund_no = fundingMemberEntity.getFundingno();
		List<FundingMemberEntity> fundingMemberList = fundingMemberRepo.findByFundingno(fund_no);
		System.out.println(fundingMemberList);
		
		List<FundingMemberEntity> result = new ArrayList<>();
		for(FundingMemberEntity e : fundingMemberList) {
			System.out.println(e.getParticipationdate() + ", " + e.getInviteddate());
			
			//아직 참여하지 않은 인원을 알아보는 방법
		}
		return false;
		
	}
}
