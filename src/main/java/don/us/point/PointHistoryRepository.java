package don.us.point;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PointHistoryRepository extends JpaRepository<PointHistoryEntity, Integer>{
	
	List<PointHistoryEntity> findByMembernoOrderByTransactiondateDesc(int memberno);
}
