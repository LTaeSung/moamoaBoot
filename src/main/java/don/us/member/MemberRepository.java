package don.us.member;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Integer>{

	Optional<MemberEntity> findByEmail(String user_email);

	//멤버 추가 - 멤버 검색 기능 구현(02 - 08 신정훈)
	List<MemberEntity> findByName(String name);
	
}
