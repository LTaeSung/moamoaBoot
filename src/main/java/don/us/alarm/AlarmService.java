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
	
	public void makeInviteAlarm(FundingMemberEntity fundingMember) {
		String content = "" + fundingMember.getStartmembername() + "님이 " + "[" + fundingMember.getFundtitle()
				+ "]에 초대하셨습니다.";
		String link = "/funding/invited";

		AlarmEntity alarm = new AlarmEntity();
		alarm.setMemberno(fundingMember.getMemberno());
		alarm.setContent(content);
		alarm.setLink(link);
		alarm.setState(0);
		
		repo.save(alarm);
	}
}
