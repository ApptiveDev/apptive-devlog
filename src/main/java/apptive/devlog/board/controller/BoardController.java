package apptive.devlog.board.controller;

import apptive.devlog.auth.entity.User;
import apptive.devlog.auth.service.UserService;
import apptive.devlog.board.entity.Comment;
import apptive.devlog.board.entity.Post;
import apptive.devlog.board.service.CommentService;
import apptive.devlog.board.service.PostService;
import apptive.devlog.board.service.ReplyService;
import apptive.devlog.mail.controller.MailController;
import apptive.devlog.mail.service.MailService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardController {
    private final PostService postService;
    private final CommentService commentService;
    private final ReplyService replyService;
    private final MailService mailService;
    private final UserService userService;

    // 게시물 생성
    @PostMapping("/posts")
    public ResponseEntity<Post> createPost(@RequestBody Post post, HttpServletRequest request) {
        // 게시물 생성
        Post createdPost = postService.create(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost); // 201
    }

    // 게시물 조회
    @GetMapping("/posts/{id}")
    public ResponseEntity<Post> getPost(@PathVariable Long id){
        Post post = postService.get(id);
        return ResponseEntity.ok(post); // 200
    }

    // 유효한 게시물 목록 조회
    @GetMapping("/posts")
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postService.getNotDeleted();
        return ResponseEntity.ok(posts); // 200
    }

    // 게시물 수정
    @PutMapping("/posts/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody Post updatedPost){
        Post post = postService.update(id, updatedPost);
        return ResponseEntity.ok(post); // 200
    }

    // 게시물 삭제
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id){
        postService.delete(id);
        return ResponseEntity.noContent().build(); // 200
    }

    // 댓글 생성
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<Comment> createComment(@PathVariable Long postId, @RequestBody Comment comment){
        // 게시물에 댓글 연결 후 댓글 알림
        Comment createdComment = commentService.createAndNotify(postId, comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment); // 201
    }

    // 게시물 댓글 목록 조회

    // commentService.getValidCommentByPostId() 사용
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<Comment>> getCommentsForPost(@PathVariable Long postId) {
        List<Comment> comments = commentService.getValidCommentByPostId(postId);
        if(comments.isEmpty()) {
            return ResponseEntity.noContent().build(); // 404
        }

        return ResponseEntity.ok(comments); // 200 ok
    }

}
