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

import java.net.URL;

@RestController
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;


    // Multipart 기반
    @PostMapping("/file/v1/upload")
    public ResponseEntity<UploadFileDto> uploadV1(@RequestParam MultipartFile file) {
        UploadFileDto response = uploadService.upload(file);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Pre-Signed 기반
    @PostMapping("/file/v2/upload")
    public ResponseEntity<UploadFileDto> uploadV2(@RequestParam String fileName) {

        UploadFileDto response = uploadService.getPresignedURL(fileName);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
