package apptive.devlog.Comment.Controller;

import apptive.devlog.Global.Response.Result.ResultCode;
import apptive.devlog.Global.Response.Result.ResultResponse;
import apptive.devlog.Member.Domain.Member;
import apptive.devlog.Comment.Dto.CreateCommentRequest;
import apptive.devlog.Comment.Service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<?> createComment(
            @PathVariable Long postId,
            @RequestBody CreateCommentRequest request,
            @AuthenticationPrincipal Member member) {
        Long commentId = commentService.createComment(postId, request, member);
        final ResultResponse response = new ResultResponse(ResultCode.COMMENT_SUCCESS,new CommentIdResponse(commentId));
        return new ResponseEntity<>(response,response.getStatus());
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal Member member) {
        commentService.deleteComment(postId, commentId, member);
        final ResultResponse response = new ResultResponse(ResultCode.COMMENT_DELETE_SUCCESS,null);
        return new ResponseEntity<>(response,response.getStatus());
    }

    private record CommentIdResponse(Long commentId) {}
} 