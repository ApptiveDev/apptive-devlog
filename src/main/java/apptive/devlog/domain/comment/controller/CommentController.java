package apptive.devlog.domain.comment.controller;

import apptive.devlog.common.response.success.CommonResponse;
import apptive.devlog.documentation.domain.comment.CommentCreateDoc;
import apptive.devlog.documentation.domain.comment.CommentDeleteDoc;
import apptive.devlog.documentation.domain.comment.CommentReadDoc;
import apptive.devlog.documentation.domain.comment.CommentUpdateDoc;
import apptive.devlog.documentation.tags.CommentDocumentation;
import apptive.devlog.domain.comment.dto.CommentDto;
import apptive.devlog.domain.comment.service.CommentService;
import apptive.devlog.domain.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("post/{postId}/comment")
@RequiredArgsConstructor
public class CommentController implements CommentDocumentation {
    private final CommentService commentService;

    @CommentCreateDoc
    @PostMapping
    public ResponseEntity<CommonResponse<CommentDto.Response>> create(@PathVariable String postId, @Valid @RequestBody CommentDto.Create dto, @RequestAttribute("user") User user) {
        CommentDto.Response response = commentService.create(postId, dto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.created(response));
    }

    @CommentReadDoc
    @GetMapping
    public ResponseEntity<CommonResponse<List<CommentDto.Response>>> read(@PathVariable String postId) {
        return ResponseEntity.ok().body(CommonResponse.ok(commentService.read(postId)));
    }

    @CommentUpdateDoc
    @PutMapping("/{commentId}")
    public ResponseEntity<CommonResponse<CommentDto.Response>> update(@PathVariable String postId, @PathVariable String commentId, @Valid @RequestBody CommentDto.Update dto, @RequestAttribute("user") User user) {
        CommentDto.Response response = commentService.update(commentId, dto, user);
        return ResponseEntity.ok().body(CommonResponse.ok(response));
    }

    @CommentDeleteDoc
    @DeleteMapping("/{commentId}")
    public ResponseEntity<CommonResponse<Void>> delete(@PathVariable String postId, @PathVariable String commentId, @RequestAttribute("user") User user) {
        commentService.delete(commentId, user);
        return ResponseEntity.noContent().build();
    }
}
