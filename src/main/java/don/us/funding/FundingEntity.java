package don.us.funding;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import org.hibernate.annotations.CurrentTimestamp;

import don.us.member.MemberEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "funding")
@ToString(exclude = "comment")
public class FundingEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int no;
	
	@Column(name="start_member_no")
	private int startmemberno;
	
	@Column(name="start_member_name")
	private String startmembername;
	
	@Column(name="funding_type")
	private int fundingtype;
	
	private String title;
	
	private String description;
	
	private String photo;
	
	@CurrentTimestamp
	@Column(name="start_date")
	private Timestamp startdate;
	
	@Column(name="funding_due_date")
	private Timestamp fundingduedate;
	
	@Column(name="vote_due_date")
	private Timestamp voteduedate;
	
	@Column(name="settlement_due_date")
	private Timestamp settlementduedate;
	
	private int candidate;
	
	@Column(name="expected_payment_amount")
	private int expectedpaymentamount;
	
	@Column(name="collected_point")
	private int collectedpoint;
	
	@Column(name="goal_amount")
	private int goalamount;
	
	@Column(name="monthly_payment_amount")
	private int monthlypaymentamount;
	
	@Column(name="monthly_payment_date")
	private String monthlypaymentdate;
	
	private int state;
	
	@Column(name="complete_interest")
	private int completeinterest;
	
}
