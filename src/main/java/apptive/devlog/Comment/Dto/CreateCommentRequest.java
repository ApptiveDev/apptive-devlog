package apptive.devlog.Comment.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateCommentRequest {
    private String content;
    private Long parentId;

    public CreateCommentRequest(String content) {
        this.content = content;
    }

    public CreateCommentRequest(String content, Long parentId) {
        this.content = content;
        this.parentId = parentId;
    }
} 