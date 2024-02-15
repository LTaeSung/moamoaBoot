package don.us.funding;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FundingRepository extends JpaRepository<FundingEntity, Integer>{


	List<FundingEntity> findBystartmemberno(int startmemberno);
	
		
	@Query("SELECT f FROM FundingEntity f WHERE f.startmemberno = :start_member_no AND f.fundingduedate > :currentDate")
	List<FundingEntity> findBystartmembernoAndfundingduedate(int start_member_no, LocalDate currentDate);

}
