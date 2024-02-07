package don.us.member;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Integer>{

	Optional<MemberEntity> findByEmail(String user_email);


}
