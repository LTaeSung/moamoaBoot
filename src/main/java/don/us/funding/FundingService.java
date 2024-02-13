package don.us.funding;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FundingService {
	@Autowired
	private FundingRepository fundingRepo;
	@Autowired
	private FundingMemberRepository fundingMemberRepo;
	
	public void makeFund(FundingEntity fund) {
		fundingRepo.save(fund);
	}
	
	private FundingMemberEntity makeFundingMemberEntity(FundingEntity fund, int member_no) {
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
		}else {
			fundMember.setParticipation_date(null);
		}
		
		fundMember.setVote(0);
		return fundMember;
	}
	
	public Timestamp getTimestamp(String date) throws ParseException{
		SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", java.util.Locale.ENGLISH);
		Date answer = inputFormat.parse(date);
		return new Timestamp(answer.getTime());
	}
	
	public void inviteMembers (FundingEntity fund, String memberListString, int starterPaymentNo) {
		FundingMemberEntity me = makeFundingMemberEntity(fund, fund.getStartmemberno());
		me.setPaymentno(starterPaymentNo);
		me.setParticipation_date(new Timestamp(System.currentTimeMillis()));
		inviteMember(fund, me);
		
		if (memberListString != null) {
			List<String> memberList = Arrays.asList(memberListString.split(","));
			System.out.println("memberList: " + memberList);

			for (String i : memberList) {
				int member_no = Integer.valueOf(i);
				inviteMember(fund, makeFundingMemberEntity(fund, member_no));
			}
		}
	}
	
	private void inviteMember(FundingEntity fund, FundingMemberEntity fundingMember) {
		fund.setCandidate(fund.getCandidate() + 1);
		fundingRepo.save(fund);
		fundingMemberRepo.save(fundingMember);
	}
	
	
}
