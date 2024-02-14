package don.us.point;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FundingHistoryRepository extends JpaRepository<FundingHistoryEntity, Integer>{
	
	List<FundingHistoryEntity> findByMembernoOrderByTransactiondateDesc(int memberno);
}
