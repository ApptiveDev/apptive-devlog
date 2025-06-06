package apptive.devlog.Post.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdatePostRequest {
    private String title;
    private String content;

    public UpdatePostRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }
} 