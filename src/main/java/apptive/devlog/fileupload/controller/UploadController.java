package apptive.devlog.fileupload.controller;

import apptive.devlog.fileupload.dto.UploadFileDto;
import apptive.devlog.fileupload.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;


    @PostMapping("/file/upload")
    public ResponseEntity<UploadFileDto> upload(@RequestParam MultipartFile file) {
        UploadFileDto response = uploadService.upload(file);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
