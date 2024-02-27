package don.us.board;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/board")
public class BoardController {
	@Autowired
	private BoardRepository repo;
	
	//공지사항 게시글 전체보기
	@GetMapping("/list/notice")
	public List notice (Model model) {
		
		List <BoardEntity>  boardEntityList = repo.findByBoardtypeOrderByRegistdateDesc(false);
		return boardEntityList;
	}
	
	//qna 게시글 전체보기
	@GetMapping("/list/qna")
	public List qna (Model model) {
		
		List <BoardEntity>  boardEntityList = repo.findByBoardtypeOrderByRegistdateDesc(true);
		return boardEntityList;
	}
	
	
	// 게시글 상세보기
	@GetMapping("/detail")
	public ResponseEntity<BoardEntity> show(@RequestParam int no) {
	    Optional<BoardEntity> optionalBoardEntity = repo.findById(no);
	    return optionalBoardEntity.map(ResponseEntity::ok).orElseGet(() -> 
	        ResponseEntity.notFound().build());
	}
	
    // qna등록
    @PostMapping("/add")
    public ResponseEntity<BoardEntity> addQna(@RequestBody BoardEntity board) {
    	board.setRegistdate(new Timestamp(System.currentTimeMillis()));
        System.out.println("board: " + board);
        Sort sort = Sort.by(Sort.Direction.DESC, "registdate");
        BoardEntity savedQna = repo.save(board);
        return new ResponseEntity<>(savedQna, HttpStatus.CREATED);
    }

    // qna수정
    @PutMapping("/update")
    public ResponseEntity<BoardEntity> updateQna(
            @RequestParam int no, @RequestBody BoardEntity updateQna) {
        Optional<BoardEntity> optionalBoardEntity = repo.findById(no);
        if (!optionalBoardEntity.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        BoardEntity existingReply = optionalBoardEntity.get();
        existingReply.setTitle(updateQna.getTitle());
        existingReply.setContents(updateQna.getContents());
        existingReply.setUpdatedate(new Timestamp(System.currentTimeMillis()));

        BoardEntity savedReply = repo.save(existingReply);
        return ResponseEntity.ok(savedReply);
    }

    // qna삭제
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteQna(@RequestParam int no) {
        Optional<BoardEntity> optionalBoardEntity = repo.findById(no);
        if (!optionalBoardEntity.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        BoardEntity existingReply = optionalBoardEntity.get();
        repo.delete(existingReply);
        return ResponseEntity.noContent().build();
    }
}
