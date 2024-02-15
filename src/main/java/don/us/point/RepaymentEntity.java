package don.us.point;

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
@Table(name = "repayment")
@ToString
public class RepaymentEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int no;
		
	@Column(name="funding_member_no")
	private int fundingmemberno;

	@Column(name="repay_count")
	private int repaycount;
}
