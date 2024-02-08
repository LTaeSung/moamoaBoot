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
		//해당 멤버의 포인트 업데이트 처리 필요
		//멤버번호 가져오고(아예 처음 리액트에서 멤버번호 받아오고 바로 생성해도 될듯)
		MemberEntity member = memberRepo.findById(1).orElseThrow();
		//결제된 금액이랑 더해서 업데이트
		member.setPoint(member.getPoint() + pointHistory.getAmount());
		memberRepo.save(member);
	}
}
