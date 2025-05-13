package apptive.devlog.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdatePostRequest {

    @NotBlank
    String title;

    @NotBlank
    String content;

    List<String> fileUrls = new ArrayList<>();

    public UpdatePostRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
