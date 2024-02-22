package don.us.point;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FundingHistoryRepository extends JpaRepository<FundingHistoryEntity, Integer>{
	
	List<FundingHistoryEntity> findByMembernoOrderByTransactiondateDesc(int memberno);
	
	@Query(value = "SELECT SUM(amount) FROM fund_transaction_history "
			+ "WHERE direction = false AND DATE_FORMAT(transaction_date, '%Y-%m') = DATE_FORMAT(NOW(), '%Y-%m');"
			, nativeQuery = true)
	public BigDecimal getMonthlyPayStatistics();
}
