package don.us.funding;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CurrentTimestamp;

import don.us.member.MemberEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "funding")
@ToString(exclude = "comments")
public class FundingEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int no;
	
	@Column(name="start_member_no")
	private int startmemberno;
	
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
	
	private int expected_payment_amount;
	
	private int collected_point;
	
	private int goal_amount;
	
	private int monthly_payment_amount;
	
	private int monthly_payment_date;
	
	private int state;
	
	private int complete_interest;
	
	@OneToMany
	private List<FundingCommentEntity> comments;
	
	
	public void setFundingduedate(String currentTimestampToString) {
		String target = currentTimestampToString + " 23:59:59";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		dateFormat.setLenient(false);// 날짜와 시간을 엄격하게 확인
		try {
			Date stringToDate = dateFormat.parse(target);
			Timestamp stringToTimestamp = new Timestamp(stringToDate.getTime());
			fundingduedate = stringToTimestamp;
		} catch (ParseException e) {
			e.printStackTrace();
			fundingduedate = null;
		}
	}
}
