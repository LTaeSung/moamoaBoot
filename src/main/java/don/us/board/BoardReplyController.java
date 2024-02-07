package don.us.board;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.transaction.Transactional;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/board/reply")
public class BoardReplyController {
	@Autowired
	private BoardReplyRepository repo;
	
	   // 댓글 조회
    @GetMapping("/list")
    public ResponseEntity<Page<BoardReplyEntity>> getRepliesByBoardNo(
            @RequestParam("boardno") int boardno, Pageable pageable) {
        Page<BoardReplyEntity> replies = repo.findByBoardno(boardno, pageable);
        return ResponseEntity.ok(replies);
    }

    // 댓글 등록
    @PostMapping("/add")
    public ResponseEntity<BoardReplyEntity> addReply(@RequestBody BoardReplyEntity reply) {
        reply.setRegistdate(new Timestamp(System.currentTimeMillis()));
        System.out.println("reply: " + reply);
        BoardReplyEntity savedReply = repo.save(reply);
        return new ResponseEntity<>(savedReply, HttpStatus.CREATED);
    }

    // 댓글 수정
    @PutMapping("/update/{no}")
    public ResponseEntity<BoardReplyEntity> updateReply(
    		@RequestParam int no, @RequestBody BoardReplyEntity updatedReply) {
        BoardReplyEntity existingReply = repo.findById(no).orElse(null);
        if (existingReply == null) {
            return ResponseEntity.notFound().build();
        }
        existingReply.setContents(updatedReply.getContents());
        existingReply.setUpdatedate(new Timestamp(System.currentTimeMillis()));
        BoardReplyEntity savedReply = repo.save(existingReply);
        return ResponseEntity.ok(savedReply);
    }

    // 댓글 삭제
    @DeleteMapping("/delete/{no}")
    public ResponseEntity<Void> deleteReply(@RequestParam int no) {
        BoardReplyEntity existingReply = repo.findById(no).orElse(null);
        if (existingReply == null) {
            return ResponseEntity.notFound().build();
        }
        repo.delete(existingReply);
        return ResponseEntity.noContent().build();
    }

}



