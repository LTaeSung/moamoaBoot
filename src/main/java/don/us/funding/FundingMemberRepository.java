package don.us.funding;

import java.util.List;
import java.util.Optional;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FundingMemberRepository extends JpaRepository<FundingMemberEntity, Integer>{
	//List<MemberEntity> findByNameContaining(String name);
	List<FundingMemberEntity> findByFundingno(int fund_no);

	List<FundingMemberEntity> findByMemberno(int member_no);
	
	@Query(value = "SELECT * FROM funding_member WHERE funding_no = ?1 AND giveup = false"
			, nativeQuery = true)
	public List<FundingMemberEntity> needPayFundMemberList(int funding_no);

//	Optional<FundingMemberEntity> findByFundingnoAndMemberno(int funding_no, int member_no);
	FundingMemberEntity findByFundingnoAndMemberno(int funding_no, int member_no);

	
	@Query(value="""
			select
				m.no as fundingMemberNo,
				f.no as fundingNo,
				f.title as fundingTitle,
				m.monthlypaymentdate as monthlyPaymentDate,
				m.monthlypaymentamount as monthlyPaymentAmmount,
	
				f.photo as photo,
				
				m.totalpayamount as myPayAmount,
				f.collectedpoint as totalPayAmount,
				
				f.startdate as startDate,
				f.fundingduedate as fundingDueDate,
				f.voteduedate as voteDueDate,
				f.settlementduedate as settlementDueDate,
				
				m.participationdate as participationDate,
				m.giveup as giveup,
				m.vote as vote,
				m.settlementamount as settlementAmount,
				f.state as state
			from 
				FundingMemberEntity m join FundingEntity f
			    on m.fundingno = f.no
			where 
				m.memberno = %?1%
				and
				f.state != 4
			order by f.state desc
		""")
	public List<Map> getJoinedFundingList_OnGoing(String member_no);
}
