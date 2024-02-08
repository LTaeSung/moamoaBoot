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
		pointHistory.setMemberno(1); //현재 접속한 사람 memberno를 받아와 넣어주면 됨
		pointHistory.setAmount(100);
		repo.save(pointHistory);
		
		MemberEntity member = memberRepo.findById(pointHistory.getMemberno()).orElseThrow(RuntimeException::new);
		member.setPoint(member.getPoint() + pointHistory.getAmount());
		memberRepo.save(member);
	}
}
