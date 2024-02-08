package don.us.point;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;

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
    public void paymentByImpUid() throws IamportResponseException, IOException {
		PointHistoryEntity pointHistory = new PointHistoryEntity();
		pointHistory.setMemberno(1); //현재 접속한 사람 memberno를 받아와 넣어주기
		pointHistory.setAmount(100); //이것도 프론트에서 받아와야 함(결제된 금액만큼)
		//그 외 시간 등은 erd에 맞춰서
		repo.save(pointHistory);
		
		//결제되었으니 해당 멤버의 포인트 증가시켜주기
		MemberEntity member = memberRepo.findById(pointHistory.getMemberno()).orElseThrow(RuntimeException::new);
		member.setPoint(member.getPoint() + pointHistory.getAmount());
		memberRepo.save(member);
		
		//결제취소
	}
}
