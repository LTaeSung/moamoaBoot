package don.us.funding;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FundingMemberRepository extends JpaRepository<FundingMemberEntity, Integer>{
	//List<MemberEntity> findByNameContaining(String name);
	List<FundingMemberEntity> findByFundingno(int fund_no);

	List<FundingMemberEntity> findByMembernoOrderByInviteddate(int member_no);
	
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
			order by f.state desc,
				f.fundingduedate asc
		""")
	public List<Map> getJoinedFundingList_OnGoing(String member_no);
	
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
				m.memberno = %?1%
				and
				f.state = 4
			order by settlementDueDate desc
		""")
	public List<Map> getJoinedFundingList_End(String member_no);
	
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
				m.fundingno = %?1%
				and
				m.memberno = %?2%
		""")
	public Map getMyFundInfo(String fund_no, String member_no);
	
	@Query(value = "SELECT * FROM funding_member"
			+ " WHERE participation_date IS NULL"
			+ " AND DATE_ADD(invited_date, INTERVAL 7 DAY) < NOW()"
			, nativeQuery = true)
	public List<FundingMemberEntity> getDontAcceptRefuseInWeekMemberList();
	
	@Query(value = "SELECT * FROM funding_member"
			+ " WHERE funding_no = ?1"
			+ " AND giveup = false"
			, nativeQuery = true)
	public List<FundingMemberEntity> getCompleteMemberList(int fundingno);
	
	@Query(value = "SELECT * FROM funding_member"
			+ " WHERE funding_no = ?1"
			+ " AND giveup = false"
			+ " AND vote = 0"
			, nativeQuery = true)
	public List<FundingMemberEntity> needVoteFundMemberList(int funding_no);
	
	@Query(value = "SELECT * FROM funding_member"
			+ " WHERE funding_no = ?1"
			+ " AND vote = 1"
			, nativeQuery = true)
	public List<FundingMemberEntity> getsuccessFundMemberList(int funding_no);
	
	@Query(value = "SELECT * FROM funding_member"
			+ " WHERE funding_no = ?1"
			+ " AND giveup = false"
			+ " AND settlement_amount IS NULL"
			, nativeQuery = true)
	public List<FundingMemberEntity> needSettlementFundMemberList(int funding_no);
	
	@Query(value = "SELECT * FROM funding_member"
			+ " WHERE member_no = ?1"
			+ " AND giveup = 0"
			, nativeQuery = true)
	public List<FundingMemberEntity> getNotGaveupFund(int member_no);
	
	@Query(value = "SELECT COUNT(no) AS total_success FROM funding_member WHERE vote = 1", nativeQuery = true)
	public int getTotalSuccess();
	
	@Query(value="""
			select
				m.no as fundingMemberNo,
				m.memberno as memberNo,
				m.fundingno as fundingNo,
				m.startmemberno as startMemberNo,
				m.startmembername as startMemberName,
				m.fundtitle as fundTitle,
				m.photo as photo,
				m.paymentno as paymentNo,
				m.fundingtype as fundingType,
				m.monthlypaymentdate as monthlyPaymentDate,
				m.monthlypaymentamount as monthlyPaymentAmmount,
				m.totalpayamount as myPayAmount,
				m.inviteddate as invitedDate,
				m.participationdate as participationDate,
				m.giveup as giveup,
				m.vote as vote,
				m.settlementamount as settlementAmount,
				m.willsettlementamount as willSettlementAmount,
				f.fundingduedate as fundingDueDate
			from 
				FundingMemberEntity m join FundingEntity f
			    on m.fundingno = f.no
			where 
				m.memberno = %?1%
		""")
	public List<Map> getInvitedFundinglist(int member_no);

}
