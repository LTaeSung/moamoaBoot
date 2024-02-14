package don.us.funding;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import don.us.member.FriendEntity;
import don.us.member.FriendRepository;
import don.us.member.MemberEntity;
import don.us.member.MemberRepository;
import don.us.point.FundingHistoryEntity;
import don.us.point.FundingHistoryRepository;
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
	
	@Autowired
	private FundingHistoryRepository fundingHistoryRepo;

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
	public void getFundList() {
		List<FundingEntity> list = fundingRepo.needPayFundList();
		System.out.println(list);
		
	}
	
	@Test
	public void getFundMemberList() {
		List<FundingEntity> fundlist = fundingRepo.needPayFundList();
		for(int i=0; i<fundlist.size(); i++) {
			List<FundingMemberEntity> memberlist = fundingMemberRepo.needPayFundMemberList(fundlist.get(i).getNo());
			System.out.println(memberlist);
		}
	}
	
	@Test
	public void regularPayment() {
		ArrayList<List<FundingMemberEntity>> list = service.needPayMemberList();
		for(int i=0; i<list.size(); i++) {
			for(int j=0; j<list.get(i).size(); j++) {
				FundingHistoryEntity fundingHistory = new FundingHistoryEntity();
				fundingHistory.setMemberno(list.get(i).get(j).getMemberno());
				fundingHistory.setFundingno(list.get(i).get(j).getFundingno());
				fundingHistory.setAmount(list.get(i).get(j).getMonthlypaymentamount());
				//fundingHistory.setDirection(false); //0=false가 디폴트값이라 따로 설정 안하고 반대로 펀딩에서 돈 줄 때 true로 세팅할게요
				fundingHistoryRepo.save(fundingHistory);
			}
		}
	}
}