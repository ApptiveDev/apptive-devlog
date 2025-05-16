package apptive.devlog.post.dto;

import apptive.devlog.fileupload.dto.UploadFileDto;
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

    String title;

    String content;

    List<UploadFileDto> files = new ArrayList<>();

    public UpdatePostRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
