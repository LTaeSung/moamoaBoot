package don.us.funding;

import java.sql.Timestamp;

import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
@Table(name = "funding_comment")
@ToString
public class FundingCommentEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int no;
	
	@Column(name="funding_no")
	private int fundingno;
	
	private String name;
	
	private String contents;
	
	private String photo;
	
	@CurrentTimestamp
	@Column(name="regist_date")
	private Timestamp registdate;
	
	@UpdateTimestamp
	@Column(name="update_date")
	private Timestamp updatedate;
}