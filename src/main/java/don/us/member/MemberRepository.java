package don.us.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Integer>{

	Optional<MemberEntity> findByNameAndEmail(String user_name, String user_email);

}
