package don.us.admin;

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
@Table(name = "main_total")
@ToString
public class MainTotalEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int no;
		
	@Column(name="total_challenge")
	private int totalchallenge;

	@Column(name="total_success")
	private int totalsuccess;
	
	@Column(name="total_money")
	private int totalmoney;
}
