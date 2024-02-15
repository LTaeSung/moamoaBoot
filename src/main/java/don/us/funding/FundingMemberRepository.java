package don.us.funding;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import don.us.member.MemberEntity;

public interface FundingMemberRepository extends JpaRepository<FundingMemberEntity, Integer>{
	//List<MemberEntity> findByNameContaining(String name);
	List<FundingMemberEntity> findByFundingno(int fund_no);

	List<FundingMemberEntity> findByMemberno(int member_no);
	
	
}
