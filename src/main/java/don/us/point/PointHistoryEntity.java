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
@Table(name = "point_transaction_history")
@ToString
public class PointHistoryEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int no;
	
	@Column(name="member_no")
	private int memberno;
	
	@Column(name="fund_no")
	private int fundingno;
	
	@CreationTimestamp
	@Column(name="transaction_date")
	private Timestamp transactiondate;
	
	private int amount;
	
	private boolean direction;
	
	private boolean success;
	
	@Column(name="merchant_uid")
	private String merchantuid;
	
	@Column(name="imp_uid")
	private String impuid;
	
	private int bank;
	
	private String account;
}
