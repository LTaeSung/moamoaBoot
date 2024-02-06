package don.us.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "payment")
@ToString
public class PaymentEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int no;
	
	@Column(name="member_no")
	private int memberno;
	
	@Column(name="payment_type")
	private int paymenttype;
	
	private int company;
	
	private String account;
	
	@Column(name="valid_date")
	private String validdate;
	
	private String cvc;
}
