package apptive.devlog.post.dto;

import apptive.devlog.comment.dto.CommentResponse;
import apptive.devlog.domain.Post;
import apptive.devlog.fileupload.dto.UploadFileDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PostWithCommentResponse {

    private Long id;
    private String author;

    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CommentResponse> comments;
    private int totalPages;

    public PostWithCommentResponse(Post post, List<CommentResponse> comments, int totalPages) {
        this.id = post.getId();
        this.author = post.getMember().getNickname();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
        this.comments = comments;
        this.totalPages = totalPages;
    }
}
