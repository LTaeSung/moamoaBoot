package don.us.funding;

import java.util.List;
import java.util.Map;
import java.sql.Date;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FundingRepository extends JpaRepository<FundingEntity, Integer>{

	
	@Query(value = "SELECT *"
			+ " FROM funding"
			+ " WHERE state = 0" //나중에 1로 바꿔줘야함!!!
			+ " AND NOW() <= funding_due_date"
			+ " AND monthly_payment_date = DATE_FORMAT(NOW(),'%d')"
			, nativeQuery = true)
	public List<FundingEntity> needPayFundList();

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
				m.startmemberno = %?1%
				and
				m.memberno = %?1%
				and
				f.state != 4
			order by f.state desc
		""")
	public List<Map> getHostFundingList_OnGoing(String member_no);
	
	@Query(value="""
			select
				f.no as fundingNo,
				f.title as fundingTitle,
	
				f.photo as photo,
				
				m.totalpayamount as myPayAmount,
				m.settlementamount as settlementAmount,
				
				f.settlementduedate as settlementDueDate,
				
				m.giveup as giveup,
				m.vote as vote
			from 
				FundingMemberEntity m join FundingEntity f
			    on m.fundingno = f.no
			where 
				m.startmemberno = %?1%
				and
				m.memberno = %?1%
				and
				f.state = 4
			order by settlementDueDate desc
		""")
	public List<Map> getHostFundingList_End(String member_no);
		
	@Query("SELECT f FROM FundingEntity f WHERE f.startmemberno = :start_member_no AND f.fundingduedate > :currentDate")
	List<FundingEntity> findBystartmembernoAndfundingduedate(int start_member_no, LocalDate currentDate);
	
	@Query(value = "SELECT *"
			+ " FROM funding"
			+ " WHERE state = 1"
			+ " AND NOW() > funding_due_date"
			, nativeQuery = true)
	public List<FundingEntity> getFundingDueList();

	@Query(value = "SELECT *"
			+ " FROM funding"
			+ " WHERE state = 2"
			+ " AND NOW() > vote_due_date"
			, nativeQuery = true)
	public List<FundingEntity> getVoteDueList();
	
	@Query(value = "SELECT *"
			+ " FROM funding"
			+ " WHERE state = 3"
			+ " AND NOW() > settlement_due_date"
			, nativeQuery = true)
	public List<FundingEntity> getSettlementDueList();
}
