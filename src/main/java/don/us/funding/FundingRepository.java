package don.us.funding;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FundingRepository extends JpaRepository<FundingEntity, Integer>{

	
	@Query(value = "SELECT *"
			+ " FROM funding"
			+ " WHERE state = 1"
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
	public List<FundingEntity> findBystartmembernoAndfundingduedate(int start_member_no, LocalDate currentDate);
	
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
	
	@Query(value = "SELECT COUNT(no) AS total_challenge FROM funding", nativeQuery = true)
	public int getTotalChallenge();
	
	@Query(value = "SELECT SUM(collected_point) AS total_money FROM funding" , nativeQuery = true)
	public int getTotalMoney();
	
	//여기서부터 통계 쿼리문
	@Query(value = "SELECT AVG(monthly_payment_amount) FROM funding WHERE state != 0;"
			, nativeQuery = true)
	public BigDecimal getAvgMonthlyPayAmountStatistics();
	
	@Query(value = "SELECT AVG(collected_point) FROM funding WHERE state = 4;"
			, nativeQuery = true)
	public BigDecimal getAvgMonthlyCollectedStatistics();
	
	@Query(value = "SELECT COUNT(no) FROM funding "
			+ "WHERE DATE_FORMAT(start_date, '%Y-%m') = DATE_FORMAT(NOW(), '%Y-%m');"
			, nativeQuery = true)
	public BigDecimal getMonthlyNewFundStatistics();

}
