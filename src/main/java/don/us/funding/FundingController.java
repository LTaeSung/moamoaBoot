package don.us.funding;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
	
	@Value("${realPath.registed_img_path}")
	private String registed_img_path;
	
	@PostMapping("/regist")
	public void makeFund(@RequestParam Map map, @RequestParam(name = "file", required = false) MultipartFile photo) {
//		System.out.println("map: " + map);
		System.out.println("photo: " + photo);
		FundingEntity fund = new FundingEntity();

		fund.setStartmemberno(Integer.valueOf((String) (map.get("member_no"))));
		fund.setTitle((String)map.get("title"));
		//마감일 추가해야함
		fund.setDescription((String)map.get("description"));
		fund.setMonthlypaymentamount(Integer.valueOf((String) (map.get("monthly_payment_amount"))));
		fund.setMonthlypaymentdate((String)map.get("monthly_payment_date"));
		
		String dueDateString = (String)map.get("dueDate");
		SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z", java.util.Locale.ENGLISH);
		try {
			Date date = inputFormat.parse(dueDateString);
            Timestamp timestamp = new Timestamp(date.getTime());
            fund.setFundingduedate(timestamp);
		}catch(ParseException e) {
			e.printStackTrace();
		}

		if(photo != null) {
			FileNameVO fvo = fileController.upload(photo, registed_img_path);
			fund.setPhoto(fvo.getSaved_filename());
		}
		
		
		repo.save(fund);
		System.out.println("fund: " + fund);
		System.out.println("map: " + map);

//		// 임시로 payment_no를 1로 설정
		int payment_no = 1;
		FundingMemberEntity me = service.makeFundingMemberEntity(fund, fund.getStartmemberno());
		me.setPaymentno(payment_no);
		me.setParticipation_date(new Timestamp(System.currentTimeMillis()));
		service.inviteMember(fund, me);
		
		if(map.get("memberList") != null) {
			List<String> memberList = Arrays.asList(((String)map.get("memberList")).split(","));
			System.out.println("memberList: " + memberList);
			
			
			for(String i : memberList) {
				int member_no = Integer.valueOf(i);
				service.inviteMember(fund, service.makeFundingMemberEntity(fund, member_no));
			}
		}

	}
	@GetMapping("/list")
	public List index (Model model) {

		List <FundingEntity>  fundingEntityList = repo.findAll();

		return fundingEntityList;
	}

	@GetMapping("/list/{no}")
		public ResponseEntity<FundingEntity> show(@PathVariable int no) {
			Optional<FundingEntity> optionalFundingEntity = repo.findById(no);
			return optionalFundingEntity.map(ResponseEntity::ok).orElseGet(() ->
					ResponseEntity.notFound().build());
		}

}
