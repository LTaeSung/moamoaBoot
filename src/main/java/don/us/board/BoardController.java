package don.us.board;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/board")
public class BoardController {
	@Autowired
	private BoardRepository repo;
	
	//게시글 전체보기
	@GetMapping("/list")
	public List index (Model model) {
		
		List <BoardEntity>  boardEntityList = repo.findAll();
		
		return boardEntityList;
	}
	
	//게시글 상세보기
	 @GetMapping("/list/{no}")
	    public ResponseEntity<BoardEntity> show(@PathVariable int no) {
	        Optional<BoardEntity> optionalBoardEntity = repo.findById(no);
	        return optionalBoardEntity.map(ResponseEntity::ok).orElseGet(() -> 
	        													ResponseEntity.notFound().build());
		
	}
}
