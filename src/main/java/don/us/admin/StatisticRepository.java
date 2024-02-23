package don.us.admin;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import don.us.funding.FundingEntity;

public interface StatisticRepository extends JpaRepository<FundingEntity, Integer>{
	@Query(value = """
			select 
				member_no, 
				(select email from member where no = member_no) as email, 
			    count(no) as joinedFund, 
			    sum(total_pay_amount) as total_pay_amount, 
			    sum(will_settlement_amount) as total_get_amount
			from funding_member 
			where participation_date is not null 
			group by member_no
			order by joinedFund desc;
			"""
			, nativeQuery = true)
	public List<Map> getRankOrderByJoinedFundNumber();
	
	@Query(value = """
			select 
				member_no, 
				(select email from member where no = member_no) as email, 
			    count(no) as joinedFund, 
			    sum(total_pay_amount) as total_pay_amount, 
			    sum(will_settlement_amount) as total_get_amount
			from funding_member 
			where participation_date is not null 
			group by member_no
			order by total_pay_amount desc;
			"""
			, nativeQuery = true)
	public List<Map> getRankOrderByTotalPayAmount();
	
	@Query(value = """
			select 
				member_no, 
				(select email from member where no = member_no) as email, 
			    count(no) as joinedFund, 
			    sum(total_pay_amount) as total_pay_amount, 
			    sum(will_settlement_amount) as total_get_amount
			from funding_member 
			where participation_date is not null 
			group by member_no
			order by total_get_amount desc;
			"""
			, nativeQuery = true)
	public List<Map> getRankOrderByTotalGet();
}
