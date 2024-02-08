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
	private FundingService service;
	
	@Autowired
	private FundingMemberRepository fundingMemberRepo;
	
	@Autowired
	private FriendRepository friendRepo;
	
	@Autowired
	private MemberRepository memberRepo;

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
	public void makeFundAddFriend() {
		int myMemberNo = 4;
		int myPaymentNo = 1;
		
		FundingEntity fund = new FundingEntity();

		fund.setStartmemberno(myMemberNo);
		fund.setTitle("정처기 따는 챌린지");
		fund.setDescription("2024년 1회 정보처리기사 땁시다. 실기 합격까지 해야 성공 시험 안보거나 불합격시 실패");
		
		fund.setFundingtype(0);
		fund.setFundingduedate("2024/06/09");
		fund.setMonthlypaymentamount(10000);
		fund.setMonthlypaymentdate("31");
		
		service.makeFund(fund);
		service.inviteMember(fund, myPaymentNo, myMemberNo);
		
		//invite friend
//		int[] friendNoList = new int[] {1, 2, 3};
//		for(int i : friendNoList) {
//			service.inviteMember(fund, -100, i);
//		}
	}
}