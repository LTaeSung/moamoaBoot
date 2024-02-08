package don.us.funding;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import don.us.member.FriendRepository;
import don.us.member.MemberRepository;

@Service
public class FundingService {
	@Autowired
	private FundingRepository fundingRepo;
	@Autowired
	private FundingMemberRepository fundingMemberRepo;
	
	@Autowired
	private FriendRepository friendRepo;
	
	@Autowired
	private MemberRepository memberRepo;
	
	
	public void makeFund(FundingEntity fund) {
		fundingRepo.save(fund);
	}
	
	public void inviteMember(FundingEntity fund, int payment_no, int member_no) {
		FundingMemberEntity fundMember = new FundingMemberEntity();
		fundMember.setFundingno(fund.getNo());
		fundMember.setMemberno(member_no);

		
		FundingEntity funding = fundingRepo.findById(fund.getNo()).get();
		fundMember.setFundingtype(funding.getFundingtype());
		fundMember.setMonthlypaymentamount(funding.getMonthlypaymentamount());
		fundMember.setMonthlypaymentdate(funding.getMonthlypaymentdate());
		fundMember.setTotalpayamount(0);
		fundMember.setGiveup(false);
		if(fund.getStartmemberno() == member_no) {
			fundMember.setParticipation_date(new Timestamp(System.currentTimeMillis()));
			fundMember.setPaymentno(payment_no);
		}else {
			fundMember.setParticipation_date(null);
		}
		
		fundMember.setVote(0);
		
		fundingMemberRepo.save(fundMember);
	}
	
	
}
