package don.us.funding;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface FundingMemberRepository extends JpaRepository<FundingMemberEntity, Integer>{
	//List<MemberEntity> findByNameContaining(String name);
	List<FundingMemberEntity> findByFundingno(int fund_no);

	List<FundingMemberEntity> findByMemberno(int member_no);
	
	@Query(value = "SELECT * FROM funding_member WHERE funding_no = ?1 AND giveup = false"
			, nativeQuery = true)
	public List<FundingMemberEntity> needPayFundMemberList(int funding_no);
	
	Optional<FundingMemberEntity> findByFundingnoAndMemberno(int funding_no, int member_no);
	
}
