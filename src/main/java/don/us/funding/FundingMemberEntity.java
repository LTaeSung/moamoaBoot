package don.us.funding;

import java.sql.Timestamp;

import org.hibernate.annotations.CurrentTimestamp;

import don.us.member.MemberEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "funding_member")
@ToString
public class FundingMemberEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int no;
	
	@Column(name="payment_no")
	private int paymentno;
	
	@Column(name="member_no")
	private int memberno;
	
	@Column(name="funding_no")
	private int fundingno;
	
	@Column(name="funding_type")
	private int fundingtype;
	
	@Column(name="monthly_payment_amount")
	private int monthlypaymentamount;
	
	@Column(name="monthly_payment_date")
	private int monthlypaymentdate;
	
	@Column(name="total_pay_amount")
	private int totalpayamount;
	
	private boolean giveup;
	
	@CurrentTimestamp
	@Column(name="participation_date")
	private Timestamp participation_date;
	
	private int vote;
}
