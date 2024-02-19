package don.us.funding;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import don.us.admin.AdminService;
import util.file.FileController;
import util.file.FileNameVO;
import util.file.HandleDays;

@CrossOrigin(origins = { "*" })
@RestController
@RequestMapping("/fund")
public class FundingController {
	@Autowired
	private FundingRepository repo;
	@Autowired
	private FileController fileController;
	@Autowired
	private FundingService service;
	@Autowired
	private FundingMemberService fundingMemberSerivce;
	@Autowired
	private FundingMemberRepository fundingmemrepo;
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private HandleDays handleDays;

	@Value("${realPath.registed_img_path}")
	private String registed_img_path;

	@PostMapping("/regist")
	public void makeFund(@RequestParam Map map, @RequestParam(name = "file", required = false) MultipartFile photo, @RequestParam int payment_no) {
		System.out.println("map: " + map);
		FundingEntity fund = new FundingEntity();

		fund.setStartmemberno(Integer.valueOf((String) (map.get("member_no"))));
		fund.setStartmembername((String) map.get("member_name"));
		fund.setTitle((String) map.get("title"));
		fund.setDescription((String) map.get("description"));
		fund.setMonthlypaymentamount(Integer.valueOf((String) (map.get("monthly_payment_amount"))));
		fund.setMonthlypaymentdate((String) map.get("monthly_payment_date"));

		try {
			Timestamp timestamp = service.getTimestamp((String) map.get("dueDate"));
			fund.setFundingduedate(timestamp);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (photo != null) {
			FileNameVO fvo = fileController.upload(photo , registed_img_path);
			fund.setPhoto(fvo.getSaved_filename());
		}

		repo.save(fund);
		
//		// 임시로 payment_no를 1로 설정
//		int payment_no = 1;

		service.inviteMembers(fund, (String) map.get("memberList"), payment_no);
	}

	@GetMapping("/host/ongoing")
	public List<Map> hostListOnGoing(@RequestParam("member_no") String member_no) {
		List<Map> rowList = repo.getHostFundingList_OnGoing(member_no);

		List<Map> result = new ArrayList<>();
		for(Map fund : rowList) {

			result.add(fundingMemberSerivce.setMapOfFundingAndMember(fund));
		}
		
		return result;
	}
	
	@GetMapping("/host/end")
	public List<Map> hostListEnd(@RequestParam("member_no") String member_no) {
		List<Map> rowList = repo.getHostFundingList_End(member_no);

		List<Map> result = new ArrayList<>();
		for(Map fund : rowList) {

			result.add(fundingMemberSerivce.setMapOfFundingAndMember_End(fund));
		}
		
		return result;
	}

	@GetMapping("/list/test")
	public List funding(Model model) {
		List<FundingEntity> fundingEntityList = repo.findAll();
		return fundingEntityList;
	}

	@GetMapping("/info")
	public Map show(@RequestParam("no") String no) {
		Map result = new HashMap<>();
		result.put("fundEntity", repo.findById(Integer.valueOf(no)).get());
		return result;
	}

	@GetMapping("/regularPaymentList")
	public List<FundingMemberEntity> regularPaymentList() {
		List<FundingMemberEntity> list = service.needPayMemberList();
		return list;
	}

	public boolean giveupMethod(FundingMemberEntity fundMember, FundingEntity funding) throws ParseException {
		
		int fund_candi = funding.getCandidate();
		
		// 참가자가 1보다 크다. 그러면
		if (fund_candi > 1) {
			//인원수 1보다 크면서 giveup이 false인 상태(중도포기를 아직 안 한 상태)
			if (fundMember.isGiveup() == false) {

				fundMember.setGiveup(true);
				fundingmemrepo.save(fundMember);

				// 인원수 -1 수정후 db업데이트
				funding.setCandidate(fund_candi - 1);
				repo.save(funding);

				return true;
			} else {
				return true;
			}
		} else if(fund_candi == 1){
			Date now = new Date();
			String now_string = now.toString();
			Timestamp nowtime = service.getTimestamp2(now_string);
			
			// 펀딩멤버의 vote를 1(성공상태)로 설정
			fundMember.setVote(1);
			// 펀딩엔티티의 투표마감일, state, 정산마감일을 설정
			funding.setVoteduedate(service.getTimestamp3(now_string));
			funding.setState(3);
			funding.setSettlementduedate(service.plusdays(nowtime, 7));
			
			repo.save(funding);
			fundingmemrepo.save(fundMember);
		
			return true;
		} else {
			return false;
		}
	}
	
	@PostMapping("/giveup")
	public Map<String, Object> giveup(@RequestBody Map<String, String> request) throws ParseException {

		Map<String, Object> result = new HashMap<>();

		int funding_no = Integer.parseInt(request.get("fundingno"));
		int member_no = Integer.parseInt(request.get("memberno"));

		FundingEntity funding = repo.findById(funding_no).get();
		FundingMemberEntity fund_mem = fundingmemrepo.findByFundingnoAndMemberno(funding_no, member_no);
		
		boolean giveupcheck = giveupMethod(fund_mem, funding);
		
		if (giveupcheck) {
			result.put("result", "giveup_success");
		} else {
			result.put("result", "giveup_fail");
		}
		
		return result;
		
	}

	@PostMapping("/checkgiveup")
	public Map<String, Object> checkgiveup(@RequestBody Map<String, String> request) throws ParseException {

		Map<String, Object> result = new HashMap<>();

		int funding_no = Integer.parseInt(request.get("fundingno"));
		int member_no = Integer.parseInt(request.get("memberno"));

		FundingMemberEntity fund_mem = fundingmemrepo.findByFundingnoAndMemberno(funding_no, member_no);
		System.out.println("값" + fund_mem.isGiveup());

		result.put("checkgiveup", fund_mem.isGiveup());

		return result;
	}
	
	@PostMapping("/addcard")
	public String accept(@RequestBody Map map) {
		Map<String, String> result = new HashMap<>();
		System.out.println("map: " + map);
		int fundMemberNo = (int)map.get("fundingMemberNo");
		FundingMemberEntity fundMemberEntity = fundingmemrepo.findById(fundMemberNo).get();
		System.out.println("fundMemberEntity: " + fundMemberEntity);
		fundMemberEntity.setParticipationdate(new Timestamp(System.currentTimeMillis()));
		
		int payment_no = Integer.valueOf((String)map.get("payment_no"));
		fundMemberEntity.setPaymentno(payment_no);
		try {
			fundingmemrepo.save(fundMemberEntity);
			
			int fund_no = fundMemberEntity.getFundingno();
			service.increaseCandidate(fund_no);
			if(service.checkStartFundingWhenAcceptFund(fund_no)) {
				service.setFundStart(fund_no);
			}
			System.out.println("참여 완료");
			
			return "success";
		}catch(Exception e) {
			System.out.println("참여 실패");
			return "fail";
		}
	}
	
	@PostMapping("/voteSuccess")
	public Map<String, Object> voteSuccess(@RequestBody Map<String, String> request) throws ParseException {

		Map<String, Object> result = new HashMap<>();
		System.out.println(request);

		int funding_no = Integer.parseInt(request.get("fundingNo"));
		int member_no = Integer.parseInt(request.get("memberNo"));

		FundingEntity funding = repo.findById(funding_no).get();
		FundingMemberEntity fund_mem = fundingmemrepo.findById(member_no).get();
		
		adminService.vote(fund_mem, 1);
		result.put("result", "vote_success");
		if(adminService.checkVoteIsComplete(fund_mem.getFundingno())) {
			adminService.computeAndSetSettlementAccount(funding);
			funding.setState(3);
			funding.setSettlementduedate(handleDays.addDays(new Timestamp(System.currentTimeMillis()), 7));
			repo.save(funding);
			result.put("result", "vote_success_end");
		}
		return result;
	}
	
	@PostMapping("/voteFail")
	public Map<String, Object> voteFail(@RequestBody Map<String, String> request) throws ParseException {

		Map<String, Object> result = new HashMap<>();

		int funding_no = Integer.parseInt(request.get("fundingNo"));
		int member_no = Integer.parseInt(request.get("memberNo"));

		FundingEntity funding = repo.findById(funding_no).get();
		FundingMemberEntity fund_mem = fundingmemrepo.findById(member_no).get();
		
		adminService.vote(fund_mem, 2);
		result.put("result", "vote_fail");
		if(adminService.checkVoteIsComplete(fund_mem.getFundingno())) {
			adminService.computeAndSetSettlementAccount(funding);
			funding.setState(3);
			funding.setSettlementduedate(handleDays.addDays(new Timestamp(System.currentTimeMillis()), 7));
			repo.save(funding);
			result.put("result", "vote_fail_end");
		}
		return result;
	}
	
	@PostMapping("/doSettlement")
	public Map<String, Object> doSettlement(@RequestBody Map<String, String> request) throws ParseException {

		Map<String, Object> result = new HashMap<>();
		System.out.println(request);

		int funding_no = Integer.parseInt(request.get("fundingNo"));
		int member_no = Integer.parseInt(request.get("memberNo"));

		FundingEntity funding = repo.findById(funding_no).get();
		FundingMemberEntity fund_mem = fundingmemrepo.findById(member_no).get();
		
		adminService.settlement(fund_mem);
		result.put("result", "settlememt_success");
		if(adminService.checkSettlementIsComplete(fund_mem.getFundingno())) {
			//정산 끝났으니 상태 4로 업뎃, break는 쳐줘도 되지만 어차피 끝날거라 굳이?
			System.out.println("상태 업뎃 전: "+funding.getState());
			funding.setState(4);
			repo.save(funding);
			result.put("result", "settlement_success_end");
		}
		return result;
	}

}
