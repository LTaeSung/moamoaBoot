package don.us.member;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

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
@Table(name = "friend")
@ToString
public class FriendEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int no;
	
	@Column(name="member_no")
	private int memberno;
	
	
	@ManyToOne
	@JoinColumn(name="friend_no")
	private MemberEntity friend;
	
	// 친구를 추가한다... 
	
	@Column(name="friend_date")
	@CreationTimestamp
	private Timestamp frienddate;
}
