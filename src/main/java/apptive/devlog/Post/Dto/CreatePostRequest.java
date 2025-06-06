package apptive.devlog.Post.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreatePostRequest {
    private String title;
    private String content;

    public CreatePostRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }
} 