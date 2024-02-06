package don.us.board;

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
@Table(name = "board_reply")
@ToString
public class BoardReplyEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int no;
	
	@Column(name="board_no")
	private int boardno;
	
	private String writer;
	private String contents;
	
	@CurrentTimestamp
	@Column(name="regist_date")
	private Timestamp registdate;
	
	@UpdateTimestamp
	@Column(name="update_date")
	private Timestamp updatedate;
}
