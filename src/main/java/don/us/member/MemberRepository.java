package don.us.member;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<MemberEntity, Integer>{

	Optional<MemberEntity> findByEmail(String user_email);

	//멤버 추가 - 멤버 검색 기능 구현(02 - 08 신정훈)
	List<MemberEntity> findByNameContaining(String name);
	
	@Modifying
	@Query("DELETE FROM MemberEntity WHERE no = ?1")
	int deleteMember(int no);

	
}
