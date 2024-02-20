package don.us.funding;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import don.us.board.BoardEntity;
import don.us.board.BoardReplyEntity;


@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/funding/comment")
public class FundingCommentController {

	@Autowired
	private FundingCommentRepository repo;
	
	// 게시글 상세보기
	@GetMapping("/list")
	public ResponseEntity<List<FundingCommentEntity>> show(@RequestParam int fundingno) {
	    List<FundingCommentEntity> commentEntities = repo.findByfundingnoOrderByRegistdate(fundingno);
	    
	    if (!commentEntities.isEmpty()) {
	        return ResponseEntity.ok(commentEntities);
	    } else {
	        return ResponseEntity.notFound().build();
	    }
	}

	//댓글등록
    @PostMapping("/add")
    public ResponseEntity<FundingCommentEntity> addReply(@RequestBody FundingCommentEntity reply) {
        reply.setRegistdate(new Timestamp(System.currentTimeMillis()));
        System.out.println("reply: " + reply);
        FundingCommentEntity savedReply = repo.save(reply);
        return new ResponseEntity<>(savedReply, HttpStatus.CREATED);
    }
    
	//댓글수정
    @PutMapping("/update")
    public ResponseEntity<FundingCommentEntity> updateReply(
    		@RequestParam int no, @RequestBody FundingCommentEntity updatedReply) {
    	FundingCommentEntity existingReply = repo.findById(no).orElse(null);
        if (existingReply == null) {
            return ResponseEntity.notFound().build();
        }
        existingReply.setContents(updatedReply.getContents());
        existingReply.setUpdatedate(new Timestamp(System.currentTimeMillis()));
        FundingCommentEntity savedReply = repo.save(existingReply);
        return ResponseEntity.ok(savedReply);
    }
    
	//댓글삭제
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteReply(@RequestParam int no) {
    	FundingCommentEntity existingReply = repo.findById(no).orElse(null);
        if (existingReply == null) {
            return ResponseEntity.notFound().build();
        }
        repo.delete(existingReply);
        return ResponseEntity.noContent().build();
    }
}
