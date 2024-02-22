package don.us.board;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import don.us.funding.FundingCommentEntity;

public interface BoardRepository extends JpaRepository<BoardEntity, Integer>{
	List<BoardEntity> findByBoardtype(boolean boardtype);
}
