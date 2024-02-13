package don.us.point;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.siot.IamportRestClient.IamportClient;

import don.us.member.MemberEntity;
import don.us.member.MemberRepository;
import jakarta.annotation.PostConstruct;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/point/point_history")
public class PointHistoryController {
	@Autowired
	private PointHistoryRepository repo;
	
	@Autowired
	private MemberRepository memberRepo;
	
	@Value("${iamport.key}")
    private String restApiKey;
    @Value("${iamport.secret}")
    private String restApiSecret;

    private IamportClient iamportClient;

    @PostConstruct
    public void init() {
        this.iamportClient = new IamportClient(restApiKey, restApiSecret);
    }
	
    @Transactional
	@PostMapping("/chargeIamport")
    public void paymentByImpUid(
    		@RequestParam(value="imp_uid") String imp_uid, 
    		@RequestParam(value="merchant_uid") String merchant_uid, 
    		@RequestParam(value="paid_amount") int paid_amount,
    		@RequestParam(value="buyer_no") int buyer_no) throws Exception {
    	System.out.println("확인 : "+imp_uid+" "+merchant_uid+" "+paid_amount+" "+buyer_no);
		PointHistoryEntity pointHistory = new PointHistoryEntity();
		pointHistory.setMemberno(buyer_no);
		pointHistory.setAmount(paid_amount);
		//그 외 시간 등은 erd에 맞춰서
		repo.save(pointHistory);
		
		MemberEntity member = memberRepo.findById(pointHistory.getMemberno()).orElseThrow(RuntimeException::new);
		member.setPoint(member.getPoint() + pointHistory.getAmount());
		memberRepo.save(member);
		
		//트랜잭션이 롤백됐을 때의 결제취소 조건..을 어떻게 처리해야 하는지 모르겠음;
	}
}
