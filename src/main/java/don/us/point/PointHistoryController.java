package don.us.point;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
    
    @GetMapping("/mypoint")
    public int memberpoint(@RequestParam(value="member_no") int member_no) {
    	MemberEntity member = memberRepo.findById(member_no).orElseThrow();
    	return member.getPoint();
    }
    
    @GetMapping("/mypointHistory")
    public List<PointHistoryEntity> pointHistory(@RequestParam(value="member_no") int member_no) {
    	List<PointHistoryEntity> pointList = repo.findByMembernoOrderByTransactiondateDesc(member_no);
    	return pointList;
    }
	
    @Transactional
	@PostMapping("/chargeIamport")
    public String paymentByImpUid(
    		@RequestParam(value="imp_uid") String imp_uid, 
    		@RequestParam(value="merchant_uid") String merchant_uid, 
    		@RequestParam(value="paid_amount") int paid_amount,
    		@RequestParam(value="buyer_no") int buyer_no) throws Exception {

    	String result = "success";
    	
		PointHistoryEntity pointHistory = new PointHistoryEntity();
		pointHistory.setMemberno(buyer_no);
		pointHistory.setAmount(paid_amount);
		pointHistory.setMerchantuid(merchant_uid);
		pointHistory.setImpuid(imp_uid);
		//그 외 시간 등은 erd에 맞춰서
		repo.save(pointHistory);
			
		MemberEntity member = memberRepo.findById(pointHistory.getMemberno()).orElseThrow(RuntimeException::new);
		member.setPoint(member.getPoint() + pointHistory.getAmount());
		memberRepo.save(member);
			
		return result;
	}
    
    @PostMapping("/cancleIamport")
    public void cancleByImpUid(@RequestParam(value="imp_uid") String imp_uid) throws IOException {
    	String accessToken = getToken();
    	refundRequest(accessToken, imp_uid, "서버 등록 오류");
    }
    
    @Transactional
    @PostMapping("/payBack")
    public String payBack(@RequestParam(value="member_no") int member_no, @RequestParam(value="amount") int amount,
    		@RequestParam(value="merchant_id") String merchant_id) {
    	String result = "success";
    	
		PointHistoryEntity pointHistory = new PointHistoryEntity();
		pointHistory.setMemberno(member_no);
		pointHistory.setAmount(amount);
		pointHistory.setBank(Integer.parseInt(merchant_id.split("_")[0]));
		pointHistory.setAccount(merchant_id.split("_")[1]);
		pointHistory.setMerchantuid(merchant_id);
		pointHistory.setDirection(true);
		//그 외 시간 등은 erd에 맞춰서
		repo.save(pointHistory);
			
		MemberEntity member = memberRepo.findById(pointHistory.getMemberno()).orElseThrow(RuntimeException::new);
		member.setPoint(member.getPoint() - pointHistory.getAmount());
		memberRepo.save(member);
			
		return result;	
    }
    
    
    
    
    
    
    private String getToken() throws IOException {
    	URL url = new URL("https://api.iamport.kr/users/getToken");
    	HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
    	
    	conn.setRequestMethod("POST");
    	conn.setRequestProperty("Content-Type", "application/json");
    	conn.setRequestProperty("Accept", "application/json");
    	conn.setDoOutput(true);
    	
    	JsonObject json = new JsonObject();
    	json.addProperty("imp_key", restApiKey);
    	json.addProperty("imp_secret", restApiSecret);
    	
    	// 출력 스트림으로 해당 conn에 요청
    	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
    	bw.write(json.toString()); // json 객체를 문자열 형태로 HTTP 요청 본문에 추가
    	bw.flush();
    	bw.close();
    	
    	// 입력 스트림으로 conn 요청에 대한 응답 반환
    	BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    	Gson gson = new Gson(); // 응답 데이터를 자바 객체로 변환
    	String response = gson.fromJson(br.readLine(), Map.class).get("response").toString();
    	String accessToken = gson.fromJson(response, Map.class).get("access_token").toString();
    	br.close(); // BufferedReader 종료
    	
    	conn.disconnect();
    	
    	return accessToken;
    }
    private void refundRequest(String accessToken, String imp_uid, String reason) throws IOException {
        URL url = new URL("https://api.iamport.kr/payments/cancel");
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
 
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Authorization", accessToken);
        conn.setDoOutput(true);
 
        JsonObject json = new JsonObject();
        json.addProperty("imp_uid", imp_uid);
        json.addProperty("reason", reason);
 
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
        bw.write(json.toString());
        bw.flush();
        bw.close();
 
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        br.close();
        conn.disconnect();
    }
}
