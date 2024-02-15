package don.us.funding;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import don.us.member.FriendEntity;
import don.us.member.FriendRepository;
import don.us.member.MemberEntity;
import don.us.member.MemberRepository;
import don.us.point.FundingHistoryEntity;
import don.us.point.FundingHistoryRepository;
import don.us.point.RepaymentEntity;
import don.us.point.RepaymentRepository;
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
//		ArrayList<List<FundingMemberEntity>> list = service.needPayMemberList();
//		for(int i=0; i<list.size(); i++) {
//			for(int j=0; j<list.get(i).size(); j++) {
//				FundingHistoryEntity fundingHistory = new FundingHistoryEntity();
//				fundingHistory.setMemberno(list.get(i).get(j).getMemberno());
//				fundingHistory.setFundingno(list.get(i).get(j).getFundingno());
//				fundingHistory.setAmount(list.get(i).get(j).getMonthlypaymentamount());
//				//fundingHistory.setDirection(false); //0=false가 디폴트값이라 따로 설정 안하고 반대로 펀딩에서 돈 줄 때 true로 세팅할게요
//				fundingHistoryRepo.save(fundingHistory);
//			}
//		}
		List<FundingMemberEntity> memberlist = new ArrayList<>();
		List<FundingEntity> fundlist = fundingRepo.needPayFundList();
		for(int i=0; i<fundlist.size(); i++) {
			List<FundingMemberEntity> templist = fundingMemberRepo.needPayFundMemberList(fundlist.get(i).getNo());
			for(int j=0; j<templist.size(); j++) {
				memberlist.add( templist.get(j) );				
			}
		}
		System.out.println("확인: "+memberlist);
	}
	
	@Test
	public void timetest() throws ParseException {
		String temp = "Mon Jul 15 2024 11:12:32 GMT+0900 (한국 표준시)";
		String[] temparr = temp.split(" ");
//		for(int i=0; i<temparr.length; i++) {			
//			System.out.println("확인 "+temparr[i]);
//		}
		temparr[4] = "23:59:59";
		StringBuffer buffer = new StringBuffer();
		for(int i=0; i<temparr.length; i++) {
			buffer.append(temparr[i]+" ");
		}
		SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", java.util.Locale.ENGLISH);
		Date answer = inputFormat.parse(buffer.toString());
		System.out.println("확인" + new Timestamp(answer.getTime()));
	}
	
	@Autowired
	private RepaymentRepository repayRepo;
	
	@Test
	public void repayList() {
		List<RepaymentEntity> list = repayRepo.findAll();
		for(int i=0; i<list.size(); i++) {			
			System.out.println("확인" + list.get(i));
		}
	}
	
	private FundingHistoryEntity makeFundingHistory(int memberno, int fundingno, int amount) throws Exception {
		FundingHistoryEntity fundingHistory = new FundingHistoryEntity();
		fundingHistory.setMemberno(memberno);
		fundingHistory.setFundingno(fundingno);
		fundingHistory.setAmount(amount);
		//fundingHistory.setDirection(false); //0=false가 디폴트값이라 따로 설정 안하고 반대로 펀딩에서 돈 줄 때 true로 세팅할게요
		
		return fundingHistoryRepo.save(fundingHistory);
	}
	
	@Test
	public void doRepay() {
		List<RepaymentEntity> repayList = repayRepo.findAll();
		for(int i=0; i<repayList.size(); i++) {
			Optional<FundingMemberEntity> fundingMem = fundingMemberRepo.findById(repayList.get(i).getFundingmemberno());
			try {
				//여기서 재결제 시도를 함
				if(repayList.get(i).getFundingmemberno() == 127) throw new Exception();
				makeFundingHistory(fundingMem.orElseThrow().getMemberno(), fundingMem.orElseThrow().getFundingno(), fundingMem.orElseThrow().getMonthlypaymentamount());
				//성공하면 repay 테이블에서 삭제
				repayRepo.deleteById(repayList.get(i).getNo());
			} catch(Exception e) {
				//안되면 재결제 횟수를 가져와서 2인지 체크함
				if(repayList.get(i).getRepaycount() >= 2) {
					//만약 2이면 방금 한 재결제로 3회째 실패인 것이므로 해당 멤버 강제 중도포기로 전환, 알람 보냄, 테이블에서 삭제
					fundingMem.get().setGiveup(true);
					fundingMemberRepo.save(fundingMem.get());
					repayRepo.deleteById(repayList.get(i).getNo());
				} else {
					repayList.get(i).setRepaycount(repayList.get(i).getRepaycount()+1);
					repayRepo.save(repayList.get(i));
				}
			}
		}
	}
	
	@Test
	public void deleteTest() {
		System.out.println("삭제전");
		repayRepo.deleteById(3);
		System.out.println("삭제후");
	}
}