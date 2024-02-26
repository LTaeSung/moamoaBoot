package don.us.admin;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {
	@Autowired
	private StatisticRepository repo;
	
	
	List<Map> getRankOrderByJoinedFundNumber(){
		return repo.getRankOrderByJoinedFundNumber();
	}
	List<Map> getRankOrderByTotalPayAmount(){
		return repo.getRankOrderByTotalPayAmount();
	}
	List<Map> getRankOrderByTotalGet(){
		return repo.getRankOrderByTotalGet();
	}
}
