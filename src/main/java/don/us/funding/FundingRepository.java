package don.us.funding;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FundingRepository extends JpaRepository<FundingEntity, Integer>{


	List<FundingEntity> findBystartmemberno(int startmemberno);
}
