package don.us.member;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.transaction.Transactional;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/member/payment")
public class PaymentController {
	@Autowired
	private PaymentRepository Payrepo;
	
	@Autowired
	private MemberRepository Memrepo;
	
	
	@PostMapping(value = "/add")
	public Map<String, Object> paymentAdd(@RequestBody Map<String, String> request) {
	    Map<String, Object> result = new HashMap<>();
		
	    int member_no = Integer.parseInt(request.get("memberno"));

	    int payment_type = Integer.parseInt(request.get("paymenttype"));
	    int company = Integer.parseInt(request.get("company"));
	    String account = request.get("account");
	    String valid_date = request.get("validdate");
	    String cvc = request.get("cvc");
	    

		// PaymentEntity가 이미 존재하는지 확인
	    if (Payrepo.findByMembernoAndAccount(member_no, account).isEmpty()) {
	        PaymentEntity payment = new PaymentEntity();
	        payment.setMemberno(member_no);
	        payment.setPaymenttype(payment_type);
	        payment.setCompany(company);
	        payment.setAccount(account);
	        payment.setValiddate(valid_date);
	        payment.setCvc(cvc);

	        // PaymentEntity가 존재하지 않을 때만 저장
	        Payrepo.save(payment);

	        result.put("result", "success");
	    } else {
	        result.put("result", "exists");
	    }

	    return result;
	}
	
	@Transactional
	@GetMapping("/delete")
	public Map paymentDelete(@RequestParam int member_no, String account){
		
		//Payrepo.findByMemberno(member_no);
		
		//member_no와 account 번호가 일치하는 결제수단 삭제
		int del_result = Payrepo.deletePay(member_no, account);
		System.out.println("삭제행 수 " + del_result);
		
		Map<String, Object> result = new HashMap<>();
		
		if(del_result==1) {
			//삭제 완료시 success 리턴
			result.put("result", "success");
			return result;
			
		} else {
			//삭제 실패시 fail 리턴
			result.put("result", "fail");
			return result;
		}
	}
	
	
	@GetMapping("/modify")
	public Map paymentUpdate(@RequestParam int no, int payment_type, int company, String account, String valid_date, String cvc){

		Map<String, Object> result = new HashMap<>();
		
		Payrepo.findById(no).ifPresentOrElse(
	            origin -> {
	                origin.setPaymenttype(payment_type);
	                origin.setCompany(company);
	                origin.setAccount(account);
	                origin.setValiddate(valid_date);
	                origin.setCvc(cvc);

	                Payrepo.save(origin);

	                result.put("result", "success");
	            },
	            () -> result.put("result", "fail")
	    );

	    return result;
	}
	
	@GetMapping("/list")
	public List<PaymentEntity> PaymentList (@RequestParam int member_no) {
		
		List<PaymentEntity> payment_list = Payrepo.findByMemberno(member_no);
		
		System.out.println("유저의 결제수단 리스트" + payment_list);
		
		
		return payment_list;
	}
	
}
