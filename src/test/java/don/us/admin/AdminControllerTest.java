package don.us.admin;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import don.us.alarm.AlarmService;
import don.us.funding.FundingController;
import don.us.funding.FundingEntity;
import don.us.funding.FundingMemberEntity;
import don.us.funding.FundingMemberRepository;
import don.us.funding.FundingRepository;
import don.us.funding.FundingService;
import don.us.member.MemberEntity;
import don.us.member.MemberRepository;
import don.us.point.FundingHistoryEntity;
import don.us.point.FundingHistoryRepository;
import don.us.point.RepaymentRepository;
import util.file.HandleDays;

@SpringBootTest
public class AdminControllerTest {
	@Autowired
	private MemberRepository memberRepo;
	
	@Autowired
	private FundingHistoryRepository fundingHistoryRepo;
	
	@Autowired
	private RepaymentRepository repayRepo;
	
	@Autowired
	private FundingRepository fundingRepo;
	
	@Autowired
	private FundingMemberRepository fundingMemberRepo;
	
	@Autowired
	private AlarmService alarmService;
	
	@Autowired
	private FundingService fundingService;
	
	@Autowired
	private FundingController fundingController;
	
	@Autowired
	private HandleDays handleDays;
	
	@Autowired
	private MainTotalRepository mainTotalRepo;
	
	@Test
	public void getFundMemberList() {
		List<FundingMemberEntity> list = fundingMemberRepo.getDontAcceptRefuseInWeekMemberList();
		for(int i=0; i<list.size(); i++) {
			System.out.println(list.get(i));
		}
	}
	
	@Transactional
	@Test
	public void setFundStatus0To1() {
		//펀딩 참여일이 없는 fundingmember 목록을 불러와서
		//for문으로 isExpired를 돌리고 true(아직 초대마감일이 오늘 안 지남)인 애들은 거름
		//오잉 근데 쿼리문에서 거르면 되는거 아닌가..?? 걍 쿼리에서 초대일에 7일 더한거랑 now() 비교함 < 아 jpql에서는 날짜함수 지원을 안해주는군요...
		List<FundingMemberEntity> fundlist = fundingMemberRepo.getDontAcceptRefuseInWeekMemberList();
		//최종적으로 남는건 '초대마감일이 지났으면서 펀딩참여일이 없는(승낙도 거절도 안한) fundingmember'들
		//저 걸러진 리스트를 가지고 병천씨가 쓴거랑 같은 3단계(펀딩멤버삭제->펀딩시작확인->되면펀딩시작)를 돌림
		for(int i=0; i<fundlist.size(); i++) {
			fundingMemberRepo.delete(fundlist.get(i));
			if(fundingService.checkStartFundingWhenAcceptFund(fundlist.get(i).getFundingno())) {
				fundingService.setFundStart(fundlist.get(i).getFundingno());
			} else System.out.println("펀딩넘버 "+fundlist.get(i).getFundingno()+"번 시작안됨");
			//나중에 sysout애들은 알람으로 바꿔줄필요가
		}
	}
	
	@Transactional
	@Test
	public void setFundStatus1To2() {
		System.out.println("실행됨");
		//펀드 상태=1 and 펀드 마감일<now()인 목록 가져옴
		List<FundingEntity> fundlist = fundingRepo.getFundingDueList();
		System.out.println("펀딩 개수: "+fundlist.size());
		for(int i=0; i<fundlist.size(); i++) {
			//펀드 상태를 1->2로 업뎃, 투표 마감일을 마감일(funding_due_date)+7일 해서 넣어줌
			System.out.println("펀드상태 업뎃전 확인"+fundlist.get(i).getState()+" 펀드 투표기한 들어가기전 확인: "+fundlist.get(i).getVoteduedate());
			fundlist.get(i).setState(2);
			fundlist.get(i).setVoteduedate(handleDays.addDays(fundlist.get(i).getFundingduedate(), 7));
			System.out.println("펀드상태 업뎃 확인"+fundlist.get(i).getState()+" 펀드 투표기한 잘 들어갔나 확인: "+fundlist.get(i).getVoteduedate());
			fundingRepo.save(fundlist.get(i));
			
			//해당 펀딩 참여자중에 중도포기 안 한(giveup=false) 사람들 목록 가져와서 투표하라고 알림보냄
			List<FundingMemberEntity> completeMemberList = fundingMemberRepo.getCompleteMemberList(fundlist.get(i).getNo());
			for(int j=0; j<completeMemberList.size(); j++) {
				alarmService.makeVoteAlarm(completeMemberList.get(j).getMemberno(), fundlist.get(i).getNo());
				System.out.println("알람갔어용");
			}
		}	
	}
	
	@Transactional
	@Test
	public void setFundStatus2To3() {
		System.out.println("실행됨");
		//펀드상태=2 and 투표마감일<now()인 목록 가져옴
		List<FundingEntity> fundlist = fundingRepo.getVoteDueList();
		System.out.println("펀딩 개수: "+fundlist.size());
		//멤버 중 투표 안 한 인원이 있으면(vote=0) 전부 실패(2)로 업뎃
		for(int i=0; i<fundlist.size(); i++) {
			List<FundingMemberEntity> dontVoteMemberList = fundingMemberRepo.needVoteFundMemberList(fundlist.get(i).getNo());
			
			for(int j=0; j<dontVoteMemberList.size(); j++) {
				vote(dontVoteMemberList.get(j), 2);
				if(checkVoteIsComplete(dontVoteMemberList.get(j).getFundingno())) {
					computeAndSetSettlementAccount(fundlist.get(i));
				}
			}
			//펀드 status 3으로 업뎃, settlement_due_date 7일 후로 업데이트해줌
			System.out.println("상태 업뎃 전: "+fundlist.get(i).getState()+" 정산마감일"+fundlist.get(i).getSettlementduedate());
			fundlist.get(i).setState(3);
			fundlist.get(i).setSettlementduedate(handleDays.addDays(fundlist.get(i).getVoteduedate(), 7));
			fundingRepo.save(fundlist.get(i));
			System.out.println("상태 업뎃 후: "+fundlist.get(i).getState()+" 정산마감일"+fundlist.get(i).getSettlementduedate());
		}
	}
	
	@Test
	//투표한 FundingMemberEntity 넣어주고, 몇 번으로 투표했는지 넣어주세용(1=성공, 2=실패)
	public void vote(FundingMemberEntity member, int result) {
		System.out.println("투표 제대로 들어가나 확인(세팅전) "+member.getVote());
		member.setVote(result);
		System.out.println("투표 제대로 들어가나 확인(세팅후) "+member.getVote());
		fundingMemberRepo.save(member);
	}
	@Test //해당 펀딩에서 투표 안 한 사람 있는지 확인
	public boolean checkVoteIsComplete(int fundingno) {
		List<FundingMemberEntity> dontVoteMemberList = fundingMemberRepo.needVoteFundMemberList(fundingno);
		if(dontVoteMemberList.size() == 0) {System.out.println("확인 true"); return true;}
		else {System.out.println("확인 false"); return false;}
	}
	@Test //전원 투표했으면 그 결과로 정산금액 계산, 알람보내줌
	public void computeAndSetSettlementAccount(FundingEntity fund) {
		List<FundingMemberEntity> successMemberList = fundingMemberRepo.getsuccessFundMemberList(fund.getNo());
		if(successMemberList.size() == 0) {
			System.out.println("성공멤버 없음");
			//만약 성공한 멤버 없으면(전체실패, list.size=0) 그냥 완주한 전체 인원(=candidate)으로 나누고 똑같이 처리, 전체 알림
			int will_settlement_amount = fund.getCollectedpoint() / fund.getCandidate();
			List<FundingMemberEntity> allMemberList = fundingMemberRepo.needPayFundMemberList(fund.getNo());
			System.out.println("정산금 확인, 총 금액 : "+fund.getCollectedpoint()+" 인당 금액: "+will_settlement_amount);
			for(int i=0; i<allMemberList.size(); i++) {
				setAndAlarmSettlementAccount(allMemberList.get(i), will_settlement_amount);
			}
		} else { //성공한 멤버 있으면 성공한 멤버한테만
			System.out.println("성공멤버 있음 "+successMemberList.size()+" 명");
			int will_settlement_amount = fund.getCollectedpoint() / successMemberList.size();
			System.out.println("정산금 확인, 총 금액 : "+fund.getCollectedpoint()+" 인당 금액: "+will_settlement_amount);
			for(int i=0; i<successMemberList.size(); i++) {
				System.out.println("정산금 제대로 들어가나 확인(세팅전) "+successMemberList.get(i).getWillsettlementamount());
				setAndAlarmSettlementAccount(successMemberList.get(i), will_settlement_amount);
				System.out.println("정산금 제대로 들어가나 확인(세팅후) "+successMemberList.get(i).getWillsettlementamount());
			}
		}
	}
	@Test //computeAndSetSettlementAccount에서 호출할, 정산예정금 저장 및 알림보내는 함수
	public void setAndAlarmSettlementAccount(FundingMemberEntity member, int amount) {
		member.setWillsettlementamount(amount);
		fundingMemberRepo.save(member);
		alarmService.makeSettlmentAlarm(member.getMemberno(), member.getFundingno());
	}
	
	@Transactional
	@Test
	public void setFundStatus3To4() {
		//fund status=3이고 settlement_due_date<now()인 펀드 리스트 불러옴
		List<FundingEntity> fundlist = fundingRepo.getSettlementDueList();
		for(int i=0; i<fundlist.size(); i++) {
			List<FundingMemberEntity> dontSettlementMemberList = fundingMemberRepo.needSettlementFundMemberList(fundlist.get(i).getNo());
			
			for(int j=0; j<dontSettlementMemberList.size(); j++) {
				settlement(dontSettlementMemberList.get(j));
				if(checkSettlementIsComplete(dontSettlementMemberList.get(j).getFundingno())) {
					//정산 끝났으니 상태 4로 업뎃, break는 쳐줘도 되지만 어차피 끝날거라 굳이?
					System.out.println("상태 업뎃 전: "+fundlist.get(i).getState());
					fundlist.get(i).setState(4);
					fundingRepo.save(fundlist.get(i));
					System.out.println("상태 업뎃 후: "+fundlist.get(i).getState());
				}
			}
		}
	}
	@Test //정산받을 FundingMemberEntity의 정산금 업뎃, 펀드포인트 거래내역 만들고 회원정보에 포인트 업데이트치고 정산알림
	public void settlement(FundingMemberEntity member) {
		System.out.println("정산금 제대로 들어가나 확인(세팅전) "+member.getSettlementamount());
		member.setSettlementamount(member.getWillsettlementamount()+"");
		System.out.println("정산금 제대로 들어가나 확인(세팅후) "+member.getSettlementamount());
		fundingMemberRepo.save(member);
		addSettlementPointToMember(member);
		makeSettlementFundingHistory(member);
		alarmService.makeSettlementEndAlarm(member);
	}
	@Test //펀드포인트 거래내역 만들기 (정산금이 0원일때는 제외)
	public void makeSettlementFundingHistory(FundingMemberEntity member) {
		if(member.getWillsettlementamount() != 0) {
			//회원번호, 펀딩번호, 거래금액(정산금액), 방향=1(true)로 세팅 후 save 치기
			FundingHistoryEntity fundingHistory = new FundingHistoryEntity();
			System.out.println("세팅 전 확인"+fundingHistory);
			fundingHistory.setMemberno(member.getMemberno());
			fundingHistory.setFundingno(member.getFundingno());
			fundingHistory.setAmount(member.getWillsettlementamount());
			fundingHistory.setDirection(true);
			fundingHistoryRepo.save(fundingHistory);
			System.out.println("세팅 후 확인"+fundingHistory);
		}
	}
	@Test //회원정보에 보유 포인트 업데이트
	public void addSettlementPointToMember(FundingMemberEntity member) {
		MemberEntity mem = memberRepo.findById(member.getMemberno()).get();
		System.out.println("회원정보 업데이트 전 확인"+mem);
		mem.setPoint(mem.getPoint() + member.getWillsettlementamount());
		memberRepo.save(mem);
		System.out.println("회원정보 업데이트 후 확인"+mem);
	}
	@Test //해당 펀딩에서 정산 안 한 사람 있는지 확인
	public boolean checkSettlementIsComplete(int fundingno) {
		List<FundingMemberEntity> dontSettlementMemberList = fundingMemberRepo.needSettlementFundMemberList(fundingno);
		if(dontSettlementMemberList.size() == 0) {System.out.println("확인 true"); return true;}
		else {System.out.println("확인 false"); return false;}
	}
	
	@Test
	//@Transactional
	public void updateMain() {
		MainTotalEntity main = mainTotalRepo.findById(1).get();
		main.setTotalchallenge(fundingRepo.getTotalChallenge());
		main.setTotalmoney(fundingRepo.getTotalMoney());
		main.setTotalsuccess(fundingMemberRepo.getTotalSuccess());
		mainTotalRepo.save(main);
		System.out.println("확인"+main);
	}
}
