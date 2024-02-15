package don.us.funding;

import java.util.List;
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
	
	
	
	String queryForOnGoingFunding = """
			select
				m.no as fundingMemberNo,
				f.no as fundingNo,
				f.title as fundingTitle,
				m.monthlypaymentdate as monthlyPaymentDate,
				m.monthlypaymentamount as monthlyPaymentAmmount,

				
				m.totalpayamount as myPayAmount,
				f.collectedpoint as totalPayAmount,
				
				f.fundingduedate as fundingDueDate,
				f.voteduedate as voteDueDate,
				f.settlementduedate as settlementDueDate,
				
				m.giveup as giveup,
				m.vote as vote,
				m.settlementamount as settlementAmount
			from 
				FundingMemberEntity m join FundingEntity f
			    on m.fundingno = f.no
			where 
				m.memberno = %?1%
				and
				f.state != 4
			order by f.state desc
			""";
	@Query(value=queryForOnGoingFunding)
	public List<Map> getJoinedFundingList_OnGoing(String member_no);
}
