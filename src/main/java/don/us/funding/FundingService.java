package don.us.funding;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import don.us.alarm.AlarmService;

@Service
public class FundingService {
	@Autowired
	private FundingRepository fundingRepo;
	@Autowired
	private FundingMemberRepository fundingMemberRepo;
	
	@Autowired
	private AlarmService alarmService;
	
	public void increaseCandidate(int fund_no) {
		FundingEntity fund = fundingRepo.findById(fund_no).get();
		fund.setCandidate(fund.getCandidate() + 1);
		fundingRepo.save(fund);
	}
	
	public void setFundStart(int fund_no) {
		FundingEntity fund = fundingRepo.findById(fund_no).get();
		fund.setState(1);
		fundingRepo.save(fund);
	}
	
	public boolean checkStartFundingWhenAcceptFund(int fund_no) {
		List<FundingMemberEntity> fundingMemberList = fundingMemberRepo.findByFundingno(fund_no);
		System.out.println(fundingMemberList);
		
		List<FundingMemberEntity> result = new ArrayList<>();
		for(FundingMemberEntity e : fundingMemberList) {
			if(e.getParticipationdate() == null) {
				//아직 참여하지 않은 인원을 알아보는 방법
				//participationDate == null
				//수락을 누를 때는 초대마감일이 지나지 않은 상태이기 때문에 초대 마감일이 지나지 않은 맴버는 고려하지 않아도 된다.
				result.add(e);
			}
		}
		
		//아직 참여하지 않은 인원이 없을 경우
		if(result.size() == 0) {
			return true;
		}
		return false;
		
	}
	public void makeFund(FundingEntity fund) {
		fundingRepo.save(fund);
	}
	
	
	private FundingMemberEntity makeFundingMemberEntity(FundingEntity fund, int member_no) {
		FundingMemberEntity fundMember = new FundingMemberEntity();
		fundMember.setFundingno(fund.getNo());
		fundMember.setMemberno(member_no);
		fundMember.setStartmemberno(fund.getStartmemberno());
		fundMember.setStartmembername(fund.getStartmembername());
		fundMember.setInviteddate(new Timestamp(System.currentTimeMillis()));
		fundMember.setPhoto(fund.getPhoto());
		
		fundMember.setFundtitle(fund.getTitle());
		fundMember.setFundingtype(fund.getFundingtype());
		fundMember.setMonthlypaymentamount(fund.getMonthlypaymentamount());
		fundMember.setMonthlypaymentdate(fund.getMonthlypaymentdate());
		fundMember.setTotalpayamount(0);
		fundMember.setGiveup(false);
		if(fund.getStartmemberno() == member_no) {
			fundMember.setParticipationdate(new Timestamp(System.currentTimeMillis()));
		}else {
			fundMember.setParticipationdate(null);
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
		me.setParticipationdate(new Timestamp(System.currentTimeMillis()));
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
		
		fundingRepo.save(fund);
		fundingMemberRepo.save(fundingMember);
		
		//펀드를 주최한 맴버에게는 초대 알람을 보내지 않는다.
		if(fundingMember.getMemberno() != fund.getStartmemberno()) {
			alarmService.makeInviteAlarm(fundingMember);
		}
	}
	
	
	public List<FundingMemberEntity> needPayMemberList(){
		List<FundingMemberEntity> memberlist = new ArrayList<>();
		List<FundingEntity> fundlist = fundingRepo.needPayFundList();
		for(int i=0; i<fundlist.size(); i++) {
			List<FundingMemberEntity> templist = fundingMemberRepo.needPayFundMemberList(fundlist.get(i).getNo());
			for(int j=0; j<templist.size(); j++) {
				memberlist.add( templist.get(j) );				
			}
		}
		return memberlist;
	}
	
}
