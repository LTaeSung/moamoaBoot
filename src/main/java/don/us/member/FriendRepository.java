package don.us.member;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<FriendEntity, Integer>{
	public List<FriendEntity> findByMemberno(int memberno);
}
