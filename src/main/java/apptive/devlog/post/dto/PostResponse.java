package apptive.devlog.post.dto;

import apptive.devlog.comment.dto.CommentResponse;
import apptive.devlog.domain.Post;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PostResponse {
    private Long id;
    private String author;

    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PostResponse(Post post, String author) {
        this.id = post.getId();
        this.author = author;
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
    }
}
