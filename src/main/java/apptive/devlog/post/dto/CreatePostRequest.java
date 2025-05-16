package apptive.devlog.post.dto;

import apptive.devlog.fileupload.dto.UploadFileDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @Size(min = 3, max = 20, message = "제목은 3글자 이상 20글자 이하")
    String title;

    @Size(min = 3, message = "내용은 3글자 이상")
    String content;

    List<UploadFileDto> files = new ArrayList<>();

    public CreatePostRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
