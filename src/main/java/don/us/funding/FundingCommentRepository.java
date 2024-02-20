package don.us.funding;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


public interface FundingCommentRepository extends JpaRepository<FundingCommentEntity, Integer>{
	 List<FundingCommentEntity> findByfundingnoOrderByRegistdate(int fundingno);
	public void deleteByfundingno(int fundingno);
}
