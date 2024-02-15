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

import don.us.alarm.AlarmService;
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
	
	@Autowired
	private FundingService fundingService;
	
	@Test
	public void regularPayment() {
		List<FundingMemberEntity> list = fundingService.needPayMemberList();
		for(int i=0; i<list.size(); i++) {
			try {
				FundingMemberEntity fundMem = list.get(i);
				FundingHistoryEntity fundingHistory = makeFundingHistory(fundMem.getMemberno(), fundMem.getFundingno(), fundMem.getMonthlypaymentamount());
				//해당 펀딩 결제된 포인트에 돈 더해서 업데이트
				Optional<FundingEntity> fund = fundingRepo.findById( fundMem.getFundingno() );
				System.out.println("펀딩 총 모인 금액 업데이트전 "+fund.get().getCollectedpoint());
				fund.orElseThrow().setCollectedpoint( fund.orElseThrow().getCollectedpoint() + fundMem.getMonthlypaymentamount() );
				fundingRepo.save(fund.get());
				System.out.println("펀딩 총 모인 금액 업데이트후 "+fund.get().getCollectedpoint());
				//해당 펀딩 멤버의 총 결제금액에 더해서 업데이트
				System.out.println("인당 결제한 총 금액 업데이트전 "+fundMem.getTotalpayamount());
				fundMem.setTotalpayamount( fundMem.getTotalpayamount() + fundMem.getMonthlypaymentamount() );
				fundingMemberRepo.save(fundMem);
				System.out.println("인당 결제한 총 금액 업데이트후 "+fundMem.getTotalpayamount());
			} catch(Exception e) {
				//여기서 해당 멤버에게 알람을 보내주고, 재결제 테이블에 정보 추가함
				System.out.println(list.get(i).getMemberno()+"번 고객님의 "+list.get(i).getFundingno()+"번 펀딩 결제에서 문제가 발생했습니다.");
				RepaymentEntity repay = new RepaymentEntity();
				repay.setFundingmemberno(list.get(i).getNo());
				repayRepo.save(repay);
			}
		}
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
	
	@Autowired
	private AlarmService alarmService;
	
	@Test
	public void alarmTest() {
		String content = "챌린지 ["+81+"]의 이번 달 결제에 실패했습니다. 자동으로 재결제가 진행될 예정이오니 해당 펀딩에 등록된 결제 카드를 다른 카드로 변경해주세요.";
		alarmService.makePayAlarm(7, content, 81);
	}
}