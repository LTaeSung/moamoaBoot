package don.us.member;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.transaction.Transactional;

public interface FriendRepository extends JpaRepository<FriendEntity, Integer>{
	

	public void deleteByFriendNo(Integer friend_no);
	

	public List<FriendEntity> findByMemberno(int member_no);
	



}
