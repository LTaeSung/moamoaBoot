package don.us.point;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

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
@Table(name = "fund_transaction_history")
@ToString
public class FundingHistoryEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int no;
	
	@Column(name="member_no")
	private int memberno;
	
	@Column(name="funding_no")
	private int fundingno;
	
	@CreationTimestamp
	@Column(name="transaction_date")
	private Timestamp transactiondate;
	
	private int amount;
	
	private boolean direction;
}
