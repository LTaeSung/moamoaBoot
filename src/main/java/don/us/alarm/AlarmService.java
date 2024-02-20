package don.us.alarm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import don.us.funding.FundingMemberEntity;
import don.us.funding.FundingMemberRepository;

@Service
public class AlarmService {
	@Autowired
	private FundingMemberRepository fundingMemberRepo;

	@Autowired
	private AlarmRepository repo;
	
	public void makeAlarm(int memberno, String content, String link, int state) {
		AlarmEntity alarm = new AlarmEntity();
		alarm.setMemberno(memberno);
		alarm.setContent(content);
		alarm.setLink(link);
		alarm.setState(state);
		repo.save(alarm);
		System.out.println("알람 확인: "+alarm);
	}
	
	public void makeInviteAlarm(FundingMemberEntity fundingMember) {
		String content = "" + fundingMember.getStartmembername() + "님이 " + "[" + fundingMember.getFundtitle()
				+ "]에 초대하셨습니다.";
		String link = "/funding/invited";

//		AlarmEntity alarm = new AlarmEntity();
//		alarm.setMemberno(fundingMember.getMemberno());
//		alarm.setContent(content);
//		alarm.setLink(link);
//		alarm.setState(0);
//		repo.save(alarm);
		makeAlarm(fundingMember.getMemberno(), content, link, 0);
	}
	
	public void makeFundStartAlarm(FundingMemberEntity fundingMember) {
		String content = "[" + fundingMember.getFundtitle() + "] 챌린지가 시작되었습니다.";
		String link = "/funding/info?no="+fundingMember.getFundingno();
		makeAlarm(fundingMember.getMemberno(), content, link, 0);
	}
	
	public void makePayAlarm(int memberno, String content, int fundingno) {
		String link = "/funding/info?no="+fundingno;
		
//		AlarmEntity alarm = new AlarmEntity();
//		alarm.setMemberno(memberno);
//		alarm.setContent(content);
//		alarm.setLink(link);
//		alarm.setState(0);
//		repo.save(alarm);
		makeAlarm(memberno, content, link, 0);
	}
	
	public void makeVoteAlarm(int memberno, int fundingno) {
		String content = "참여하신 챌린지가 종료되었습니다. 챌린지 성공/실패 여부를 체크해주세요. 일주일 내에 입력하지 않으실 경우, 자동으로 실패 처리됩니다.";
		String link = "/funding/info?no="+fundingno;
		
//		AlarmEntity alarm = new AlarmEntity();
//		alarm.setMemberno(memberno);
//		alarm.setContent(content);
//		alarm.setLink(link);
//		alarm.setState(0);
//		repo.save(alarm);
		makeAlarm(memberno, content, link, 0);
	}
	
	public void makeSettlmentAlarm(int memberno, int fundingno) {
		String content = "참여하신 챌린지의 투표가 종료되었습니다. 정산금을 받아가세요.";
		String link = "/funding/info?no="+fundingno;
		
//		AlarmEntity alarm = new AlarmEntity();
//		alarm.setMemberno(memberno);
//		alarm.setContent(content);
//		alarm.setLink(link);
//		alarm.setState(0);
//		repo.save(alarm);
		makeAlarm(memberno, content, link, 0);
	}
	
	public void makeSettlementEndAlarm(FundingMemberEntity member) {
		String content = "참여하신 챌린지의 정산이 완료되었습니다. 마이페이지-포인트 내역에서 확인 가능합니다.";
		String link = "/point/fundpointlist"; //나중에 프론트 링크 바뀌면 맞춰서 바꿔줘야함
		makeAlarm(member.getMemberno(), content, link, 0);
	}
}
