package don.us.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import don.us.alarm.AlarmService;
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

@Service
public class AdminService {
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
	
	@Transactional
	public FundingHistoryEntity makePayToFundingFundingHistory(int memberno, int fundingno, int amount) throws Exception {
		FundingHistoryEntity fundingHistory = new FundingHistoryEntity();
		fundingHistory.setMemberno(memberno);
		fundingHistory.setFundingno(fundingno);
		fundingHistory.setAmount(amount);
		return fundingHistoryRepo.save(fundingHistory);
	}
	
	//결제 후 해당 펀딩에 모인 총 포인트, 결제한 멤버의 총결제금액 업데이트 치는 함수
	public void updateTotalPayAmount(FundingMemberEntity fundMem, FundingEntity fund) {
		//해당 펀딩 결제된 포인트에 돈 더해서 업데이트
		fund.setCollectedpoint( fund.getCollectedpoint() + fundMem.getMonthlypaymentamount() );
		fundingRepo.save(fund);
		//해당 펀딩 멤버의 총 결제금액에 더해서 업데이트
		fundMem.setTotalpayamount( fundMem.getTotalpayamount() + fundMem.getMonthlypaymentamount() );
		fundingMemberRepo.save(fundMem);
	}
	
	//투표한 FundingMemberEntity 넣어주고, 몇 번으로 투표했는지 넣어주세용(1=성공, 2=실패)
	public void vote(FundingMemberEntity member, int result) {
		System.out.println("투표 제대로 들어가나 확인(세팅전) "+member.getVote());
		member.setVote(result);
		System.out.println("투표 제대로 들어가나 확인(세팅후) "+member.getVote());
		fundingMemberRepo.save(member);
	}
	
	//해당 펀딩에서 투표 안 한 사람 있는지 확인
	public boolean checkVoteIsComplete(int fundingno) {
		List<FundingMemberEntity> dontVoteMemberList = fundingMemberRepo.needVoteFundMemberList(fundingno);
		if(dontVoteMemberList.size() == 0) {System.out.println("확인 true"); return true;}
		else {System.out.println("확인 false"); return false;}
	}
	
	//전원 투표했으면 그 결과로 정산금액 계산, 알람보내줌
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
	
	//computeAndSetSettlementAccount에서 호출할, 정산예정금 저장 및 알림보내는 함수
	public void setAndAlarmSettlementAccount(FundingMemberEntity member, int amount) {
		member.setWillsettlementamount(amount);
		fundingMemberRepo.save(member);
		alarmService.makeSettlmentAlarm(member.getMemberno(), member.getFundingno());
	}
	
	//정산받을 FundingMemberEntity의 정산금 업뎃, 펀드포인트 거래내역 만들고 회원정보에 포인트 업데이트치고 정산알림
	public void settlement(FundingMemberEntity member) {
		System.out.println("정산금 제대로 들어가나 확인(세팅전) "+member.getSettlementamount());
		member.setSettlementamount(member.getWillsettlementamount()+"");
		System.out.println("정산금 제대로 들어가나 확인(세팅후) "+member.getSettlementamount());
		fundingMemberRepo.save(member);
		addSettlementPointToMember(member);
		makeSettlementFundingHistory(member);
		alarmService.makeSettlementEndAlarm(member);
	}
	
	//펀드포인트 거래내역 만들기 (정산금이 0원일때는 제외)
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
	
	//회원정보에 보유 포인트 업데이트
	public void addSettlementPointToMember(FundingMemberEntity member) {
		MemberEntity mem = memberRepo.findById(member.getMemberno()).get();
		System.out.println("회원정보 업데이트 전 확인"+mem);
		mem.setPoint(mem.getPoint() + member.getWillsettlementamount());
		memberRepo.save(mem);
		System.out.println("회원정보 업데이트 후 확인"+mem);
	}
	
	//해당 펀딩에서 정산 안 한 사람 있는지 확인
	public boolean checkSettlementIsComplete(int fundingno) {
		List<FundingMemberEntity> dontSettlementMemberList = fundingMemberRepo.needSettlementFundMemberList(fundingno);
		if(dontSettlementMemberList.size() == 0) {System.out.println("확인 true"); return true;}
		else {System.out.println("확인 false"); return false;}
	}
}
	
	
