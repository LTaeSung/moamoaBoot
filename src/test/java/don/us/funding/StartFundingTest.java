package don.us.funding;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import don.us.member.FriendEntity;
import don.us.member.FriendRepository;
import don.us.member.MemberEntity;
import don.us.member.MemberRepository;
import lombok.extern.java.Log;

@SpringBootTest
@Log
public class StartFundingTest {

	@Autowired
	private FundingRepository fundingRepo;

	@Autowired
	private FundingMemberRepository fundingMemberRepo;
	
	@Autowired
	private FriendRepository friendRepo;
	
	@Autowired
	private MemberRepository memberRepo;
	
	@Test
	public void makeFunding() {
		FundingEntity fund = new FundingEntity();

		fund.setStartmemberno(4);
		fund.setTitle("정처기 따는 챌린지");
		fund.setDescription("2024년 1회 정보처리기사 땁시다. 실기 합격까지 해야 성공 시험 안보거나 불합격시 실패");
		
		fund.setFundingtype(0);
		fund.setFundingduedate("2024/06/09");
		fund.setMonthly_payment_amount(10000);
		fund.setMonthly_payment_date(31);
		
		System.out.println("fund: " + fund);
		fundingRepo.save(fund);
	}
	
	@Test
	public void getFriendList() {
		//
		List<FriendEntity> list = friendRepo.findByMemberno(4);
		for (FriendEntity fe : list) {
			MemberEntity friend = fe.getFriend(); 
			log.info("no: " + friend.getNo());
			log.info("name: " + friend.getName());
		}
	}
	
	@Test
	public void addFundingMember() {
		int[] friend_list = new int[] {1, 2};
		
	}
	
	@Test
	public void addOneMember() {
		int fund_no = 8;
		int member_no = 4;
		int payment_no = 1;
		
		FundingMemberEntity fundMember = new FundingMemberEntity();
		fundMember.setFundingno(fund_no);
		fundMember.setMemberno(member_no);
		fundMember.setPaymentno(payment_no);
		
		FundingEntity funding = fundingRepo.findById(fund_no).get();
		fundMember.setFundingtype(funding.getFundingtype());
		fundMember.setMonthlypaymentamount(funding.getMonthly_payment_amount());
		fundMember.setMonthlypaymentdate(funding.getMonthly_payment_date());
		fundMember.setTotalpayamount(0);
		fundMember.setGiveup(false);
		fundMember.setParticipation_date(null);
		fundMember.setVote(0);
		
		fundingMemberRepo.save(fundMember);
	}
}