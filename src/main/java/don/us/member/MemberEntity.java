package don.us.member;

import java.sql.Timestamp;
import java.util.List;

import org.hibernate.annotations.CurrentTimestamp;
import org.springframework.web.multipart.MultipartFile;

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
@Table(name = "member")
@ToString(exclude = "payment")
public class MemberEntity {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int no;
   
   private String email;
   private String name;
   private String birthday;
   @CurrentTimestamp
   private Timestamp registerdate;
   private int point;
   private String photo;

   
//   @OneToMany
//   private List<PaymentEntity> payment;
   
}