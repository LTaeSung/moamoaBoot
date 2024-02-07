package don.us.point;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

import jakarta.annotation.PostConstruct;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/point/point_history")
public class PointHistoryController {
	@Autowired
	private FundingHistoryRepository repo; //PointHistory 레포가 맞는거같은데???
	
	@Value("${iamport.key}")
    private String restApiKey;
    @Value("${iamport.secret}")
    private String restApiSecret;

    private IamportClient iamportClient;

    @PostConstruct
    public void init() {
        this.iamportClient = new IamportClient(restApiKey, restApiSecret);
    }
	
	@PostMapping("/chargeIamport/{imp_uid}")
    public IamportResponse<Payment> paymentByImpUid(@PathVariable("imp_uid") String imp_uid) throws IamportResponseException, IOException {
		//포인트 히스토리에 넣어주기를...여기서 해도 되나?
        return iamportClient.paymentByImpUid(imp_uid); //이거는 만약 안쓸거면 리턴 안해줘도 됨(애초에 이 함수명을 쓸 필요 x)
	}
}
