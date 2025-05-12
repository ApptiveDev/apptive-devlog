package apptive.devlog.post.dto;

import apptive.devlog.fileupload.dto.UploadFileDto;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreatePostRequest {

    @NotBlank
    String title;

    @NotBlank
    String content;

    List<UploadFileDto> files = new ArrayList<>();

    public CreatePostRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
