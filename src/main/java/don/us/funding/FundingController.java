package don.us.funding;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import util.file.FileController;
import util.file.FileNameVO;

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

	@Value("${realPath.registed_img_path}")
	private String registed_img_path;

	@PostMapping("/regist")
	public void makeFund(@RequestParam Map map, @RequestParam(name = "file", required = false) MultipartFile photo) {
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
		int payment_no = 1;

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

	@GetMapping("/host/{no}")
	public FundingEntity show(@PathVariable int no) {
//		FundingEntity> result = new ArrayList<>();
		FundingEntity result = null;
		result = repo.findById(no).get();
//		repo.findById(no).ifPresent((data) -> {
//			result = data;
//		});
		return result;
//		System.out.println("result: " + result);
//		return result;
	}

	@GetMapping("/regularPaymentList")
	public List<FundingMemberEntity> regularPaymentList() {
		List<FundingMemberEntity> list = service.needPayMemberList();
		return list;
	}

	private boolean giveupMethod(FundingMemberEntity fundMember, FundingEntity funding) throws ParseException {
		
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

}
