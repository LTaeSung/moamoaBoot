package don.us.admin;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import don.us.alarm.AlarmService;
import don.us.funding.FundingEntity;
import don.us.funding.FundingMemberController;
import don.us.funding.FundingMemberEntity;
import don.us.funding.FundingMemberRepository;
import don.us.funding.FundingRepository;
import don.us.funding.FundingService;
import don.us.point.FundingHistoryRepository;
import don.us.point.RepaymentRepository;
import util.file.HandleDays;

@SpringBootTest
public class AdminControllerTest {
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
	private FundingMemberController fundingMemberController;
	
	@Autowired
	private HandleDays handleDays;
	
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
		//펀드 상태=1 and 펀드 마감일<now()인 목록 가져옴
		List<FundingEntity> fundlist = fundingRepo.getFundingDueList();
		for(int i=0; i<fundlist.size(); i++) {
			//펀드 상태를 1->2로 업뎃, 투표 마감일을 마감일(funding_due_date)+7일 해서 넣어줌
			fundlist.get(i).setState(2);
			fundlist.get(i).setVoteduedate(handleDays.addDays(fundlist.get(i).getFundingduedate(), 7));
			fundingRepo.save(fundlist.get(i));
			
			//해당 펀딩 참여자중에 중도포기 안 한(giveup=false) 사람들 목록 가져와서 투표하라고 알림보냄
			List<FundingMemberEntity> completeMemberList = fundingMemberRepo.getCompleteMemberList(fundlist.get(i).getNo());
			for(int j=0; j<completeMemberList.size(); j++) {
				alarmService.makeVoteAlarm(completeMemberList.get(j).getMemberno(), fundlist.get(i).getNo());
			}
		}	
	}
	
	@Transactional
	@Test
	public void setFundStatus2To3() {
		//펀드상태=2 and 투표마감일<now()인 목록 가져옴
		List<FundingEntity> fundlist = fundingRepo.getVoteDueList();
		//멤버 중 투표 안 한 인원이 있으면(vote=0) 전부 실패(2)로 업뎃
		for(int i=0; i<fundlist.size(); i++) {
			List<FundingMemberEntity> dontVoteMemberList = fundingMemberRepo.needVoteFundMemberList(fundlist.get(i).getNo());
			for(int j=0; j<dontVoteMemberList.size(); j++) {
				dontVoteMemberList.get(j).setVote(2);
				fundingMemberRepo.save(dontVoteMemberList.get(j));
			}
			//성공한 멤버들만 불러와서 전체모금액/list.size해 인당 정산금 구한 뒤, 펀딩의 1인당정산금액에 저장, 성공자들한테만 정산받으라고 알림
			List<FundingMemberEntity> successMemberList = fundingMemberRepo.getsuccessFundMemberList(fundlist.get(i).getNo());
			if(successMemberList.size() == 0) {
				//만약 성공한 멤버 없으면(전체실패, list.size=0) 그냥 완주한 전체 인원(=candidate)으로 나누고 똑같이 처리, 전체 알림
				
			} else {
				
			}
		}
		//펀드 status 3으로 업뎃~
	}
	
	@Test
	public void setFundStatus3To4() {
		//
	}
	
}
