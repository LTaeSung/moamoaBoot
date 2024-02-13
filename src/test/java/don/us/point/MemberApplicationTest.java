package don.us.point;


import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import don.us.member.MemberEntity;
import don.us.member.MemberRepository;
import lombok.extern.java.Log;

@Log
@SpringBootTest
@EnableTransactionManagement
public class MemberApplicationTest {
	@Autowired
	private PointHistoryRepository repo;
	
	@Autowired
	private MemberRepository memberRepo;
	
	@Transactional
	@Test
	public void chargeTest() throws RuntimeException {
		try {
			PointHistoryEntity pointHistory = new PointHistoryEntity();
			pointHistory.setMemberno(0);
			pointHistory.setAmount(1);
			pointHistory.setMerchantuid("1707803442643_7");
			pointHistory.setImpuid("imp_552443415375");
			//그 외 시간 등은 erd에 맞춰서
			repo.save(pointHistory);
			
			MemberEntity member = memberRepo.findById(pointHistory.getMemberno()).orElseThrow(RuntimeException::new);
			member.setPoint(member.getPoint() + pointHistory.getAmount());
			memberRepo.save(member);
		} catch(Exception e) {
			System.out.println("취소요청");
			throw new RuntimeException();
		}
		
	}
	
	
	
}
