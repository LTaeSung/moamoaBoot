package don.us.funding;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RepaymentRepository extends JpaRepository<RepaymentEntity, Integer>{

}
