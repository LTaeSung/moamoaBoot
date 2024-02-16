package don.us.funding;

import java.sql.Timestamp;
import java.text.ParseException;
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

	@GetMapping("/host")
	public List<FundingEntity> myFunding(@RequestParam("start_member_no") int start_member_no) {

		List<FundingEntity> myFundinglist = repo.findBystartmemberno(start_member_no);

		return myFundinglist;
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

	private boolean giveupMethod(FundingMemberEntity fundMember, FundingEntity fund) {
		
		
		
		return true;
		//return false;
	}
	
	@PostMapping("/giveup")
	public Map<String, Object> giveup(@RequestBody Map<String, String> request) throws ParseException {

		Map<String, Object> result = new HashMap<>();

		int funding_no = Integer.parseInt(request.get("fundingno"));
		int member_no = Integer.parseInt(request.get("memberno"));

		// 중도포기 버튼 눌렸을 때 인원체크 먼저
		Optional<FundingEntity> funding = repo.findById(funding_no);
		int fund_candi = funding.get().getCandidate();
		Optional<FundingMemberEntity> fund_mem = fundingmemrepo.findByFundingnoAndMemberno(funding_no, member_no);
		
		// 참가자가 1보다 크다. 그러면
		if (fund_candi > 1) {
			System.out.println("미깁업" + fund_mem);
			if (fund_mem.isPresent()) {
				if (fund_mem.get().isGiveup() == false) {

					Optional<FundingMemberEntity> fund_mem2 = fund_mem;
					Optional<FundingEntity> funding2 = funding;

//				FundingMemberEntity fundingmem = new FundingMemberEntity();
					fund_mem2.get().setGiveup(true);
//				fundingmem.setFundingno(funding_no);
//				fundingmem.setMemberno(member_no);
					fundingmemrepo.save(fund_mem2.get());

					result.put("giveup", fund_mem2.get().isGiveup());
					System.out.println(fund_mem2.get().isGiveup());

					// 인원수 -1 수정후 db업데이트
					funding2.get().setCandidate(fund_candi - 1);
					repo.save(funding2.get());

					result.put("result", "giveup_success");
				} else { // 펀딩멤버는 존재하나 이미 giveup이 1일 때
					result.put("result", "giveup_success");
				}
			} else {
				result.put("result", "fail");
			}
		}
		// 참가자가 1보다 큰 경우가 아닌 경우. 즉 1인 경우
		else {

			// 참가자가 1이면서 펀딩이 진행상황 state가 1(진행중)인 경우. 참가자수를 줄이지 않고 펀딩을 정산중 상태(투표는 강제로 한 것으로
			// 처리)로 변경한다.
			if (funding.get().getState() == 1) {

				Optional<FundingMemberEntity> fund_mem2 = fund_mem;
				Optional<FundingEntity> funding2 = funding;
				Date now = new Date();
				String now_string = now.toString();
				Timestamp nowtime = service.getTimestamp2(now_string);

				// 펀딩멤버의 vote를 1(성공상태)로 설정
				fund_mem2.get().setVote(1);
				// 펀딩엔티티의 투표마감일, state, 정산마감일을 설정
				funding2.get().setVoteduedate(nowtime);
				funding2.get().setState(3);
				funding2.get().setSettlementduedate(service.plusdays(nowtime, 7));

				repo.save(funding2.get());
				fundingmemrepo.save(fund_mem2.get());
				result.put("result", "one_person_fund_finished");
			}

			else {
				// 참가자가 1이면서 펀딩의 state에 따라 처리하면 되는 부분
			}
		}
		return result;
	}

	@PostMapping("/checkgiveup")
	public Map<String, Object> checkgiveup(@RequestBody Map<String, String> request) throws ParseException {

		Map<String, Object> result = new HashMap<>();

		int funding_no = Integer.parseInt(request.get("fundingno"));
		int member_no = Integer.parseInt(request.get("memberno"));

		Optional<FundingMemberEntity> fund_mem = fundingmemrepo.findByFundingnoAndMemberno(funding_no, member_no);
		System.out.println("값" + fund_mem.get().isGiveup());

		result.put("checkgiveup", fund_mem.get().isGiveup());

		return result;
	}

}
