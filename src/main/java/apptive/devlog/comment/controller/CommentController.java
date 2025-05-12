package apptive.devlog.comment.controller;

import apptive.devlog.comment.dto.CommentRequest;
import apptive.devlog.comment.dto.CommentResponse;
import apptive.devlog.comment.service.CommentService;
import apptive.devlog.member.dto.MemberDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/me")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/post/{id}/comment")
    public ResponseEntity<CommentResponse> createComment(@Valid @RequestBody CommentRequest comment,
                                                            @PathVariable Long id,
                                                            @AuthenticationPrincipal MemberDetails member) {
        CommentResponse response = commentService.saveComment(comment, id, member.getUsername());


        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/post/{postId}/comment/{commentId}")
    public ResponseEntity<CommentResponse> createReComment(@Valid @RequestBody CommentRequest comment,
                                                               @PathVariable Long postId, @PathVariable Long commentId,
                                                               @AuthenticationPrincipal MemberDetails member) {
        CommentResponse response = commentService.saveReComment(comment, postId, commentId, member.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/comment/{id}")
    public ResponseEntity<Map<String, String>> deleteComment(@PathVariable Long id,
                                                             @AuthenticationPrincipal MemberDetails member) {
        commentService.deleteComment(id, member.getUsername());
        
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Map.of("message", "댓글이 삭제되었습니다."));
    }


    @PatchMapping("/comment/{id}")
    public ResponseEntity<Map<String, String>> updateComment(@Valid @RequestBody CommentRequest request, @PathVariable Long id,
                                           @AuthenticationPrincipal MemberDetails member) {
        commentService.updateComment(request, id, member.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "댓글이 수정되었습니다"));
    }
}
