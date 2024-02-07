package don.us.member;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@CrossOrigin(origins = { "*" })
@RestController
@RequestMapping("/member")
public class MemberController {
	@Autowired
	private MemberRepository repo;

	private final String CLIENT_ID = "CdK5qEW_eg3VAa_uRt9l";
	private final String CLIENT_SECRET = "56H_05YtBY";
	private final String REDIRECT_URI = "http://localhost:8090/login/naver";

	@Autowired
	private NaverLogin naverLogin;

	/*
	 * @GetMapping(value = "/naver") public void naver(String code){
	 * System.out.println("code: " + code);
	 * 
	 * }
	 */

	@PostMapping("devlogin")
	public Map<String, String> myLogin(@RequestBody Map<String, String> map) {
		System.out.println(map);
		System.out.println(map.get("name"));
		String input_email = map.get("name");
		Map<String, String> result = new HashMap<>();
		if(repo.findByEmail(input_email).isPresent()/*map.get("name").equals("myID")*/) {
			MemberEntity target = repo.findByEmail(input_email).get();
			
			System.out.println("ㅎㅇ");
			
			result.put("result", "success");
			result.put("no", String.valueOf(target.getNo()));
            result.put("email", target.getEmail());
		}else {
			result.put("result", "fail");
		}
		return result;
	}
	
	@GetMapping(value = "/login")
	public Map<String, String> getAccessToken(@RequestParam String code) {
		Map<String, String> result = new HashMap<>();
		// Naver OAuth 2.0 Token Endpoint URL
		String tokenUrl = "https://nid.naver.com/oauth2.0/token";

		// Request Headers 설정
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		// Request Body 설정
		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
		requestBody.add("grant_type", "authorization_code");
		requestBody.add("client_id", CLIENT_ID);
		requestBody.add("client_secret", CLIENT_SECRET);
		requestBody.add("code", code);
		requestBody.add("redirect_uri", REDIRECT_URI);

		// RestTemplate 객체 생성
		RestTemplate restTemplate = new RestTemplate();

		// POST 요청 및 응답 받기
		ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, requestBody, String.class);

		// 응답 내용 (JSON 형태의 문자열) - 액세스 토큰이 들어있음
		String responseBody = response.getBody();

		System.out.println(responseBody);

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(responseBody);

			// access_token 값을 추출
			String accessToken = jsonNode.path("access_token").asText();

			// 여기서 accessToken 변수에 추출된 access_token 값이 들어갑니다.
			System.out.println("Access Token: " + accessToken);

			// 토큰 값을 사용하여 NaverLogin 클래스의 get_token 메서드 호출
			String user_data = naverLogin.get_token(accessToken);

			System.out.println("유저정보요 ㅇㅇㅇ: " + user_data);
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(user_data);
			JSONObject responseObject = (JSONObject) jsonObject.get("response");
			
			String user_email = responseObject.get("email").toString();
			System.out.println("user_email: " + user_email);

			if(repo.findByEmail(user_email).isPresent()) {
				MemberEntity target = repo.findByEmail(user_email).get();
				System.out.print("타겟"+target);
				
                result.put("result", "success");
                result.put("no", String.valueOf(target.getNo()));
                result.put("email", target.getEmail());
				
			} else {
				System.out.println("유저없음");
				result.put("result", "fail");

				
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
//	@GetMapping("/login/success")
//    public String loginSuccess(HttpServletRequest request) {
//        // 세션에서 사용자 정보 가져오기
//        HttpSession session = request.getSession();
//        String userName = (String) session.getAttribute("user_name");
//        String userEmail = (String) session.getAttribute("user_email");
//
//        System.out.println("세션에있는 이름:" + userName);
//        System.out.println("세션에 이메일: " + userEmail);
//        return "redirect:/localhost:3000/board/list";
//    }

}