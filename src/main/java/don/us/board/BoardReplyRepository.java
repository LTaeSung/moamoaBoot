package don.us.board;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardReplyRepository extends JpaRepository<BoardReplyEntity, Integer>{
	Page<BoardReplyEntity> findByBoardno(int boardno, Pageable pageable);
	public void deleteByBoardno(int boardno);
}