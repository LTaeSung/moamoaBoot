package don.us.funding;

import java.sql.Timestamp;
import java.text.ParseException;
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
		fund.setStartmembername((String)map.get("member_name"));
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
			FileNameVO fvo = fileController.upload(photo, registed_img_path);
			fund.setPhoto(fvo.getSaved_filename());
		}
		
		repo.save(fund);

//		// 임시로 payment_no를 1로 설정
		int payment_no = 1;
		
		

		service.inviteMembers(fund, (String)map.get("memberList"), payment_no);
	}

	@GetMapping("/host") 
	public List<FundingEntity> myFunding(@RequestParam("start_member_no") int start_member_no) {
		
		List <FundingEntity> myFundinglist = repo.findBystartmemberno(start_member_no);
		
		return myFundinglist;
	}

	@GetMapping("/list/test")
	public List funding (Model model) {
		List <FundingEntity>  fundingEntityList = repo.findAll();
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
	
	@PostMapping("/giveup")
	public Map<String, Object> giveup(@RequestBody Map<String, String> request) {
		
		Map<String, Object> result = new HashMap<>();
		
		int funding_no = Integer.parseInt(request.get("fundingno"));
		int member_no = Integer.parseInt(request.get("memberno"));
		
		
		Optional<FundingMemberEntity> megiveup = fundingmemrepo.findByFundingnoAndMemberno(funding_no, member_no);
		System.out.println("미깁업" + megiveup);
		if(megiveup.isPresent()) {
			if(megiveup.get().isGiveup() == false) {
				FundingMemberEntity fundingmem = new FundingMemberEntity();
				megiveup.get().setGiveup(true);
//				fundingmem.setFundingno(funding_no);
//				fundingmem.setMemberno(member_no);
				fundingmemrepo.save(megiveup.get());
				result.put("giveup", megiveup.get().isGiveup());
				System.out.println(megiveup.get().isGiveup());
	
				result.put("result", "success");
			} else {
				System.out.println("이미 giveup이 1이야");
				result.put("giveup", megiveup.get().isGiveup());
				result.put("result", "success");
				
			}
		} else {
			result.put("result", "fail");
		}
		
		
		
		
		return result;
		
	}
}
