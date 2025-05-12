package apptive.devlog.fileupload.service;

import apptive.devlog.domain.UploadFile;
import apptive.devlog.fileupload.dto.UploadFileDto;
import apptive.devlog.fileupload.exception.FileUploadException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UploadService {

    private final AmazonS3Client s3Client;

    @Value("${s3.bucket}")
    private String bucket;


    public UploadFileDto upload(MultipartFile file)  {

        String fileName = changeFileName();

        ObjectMetadata metaData = new ObjectMetadata();
        metaData.setContentType(file.getContentType());
        metaData.setContentLength(file.getSize());
        try {
            s3Client.putObject(bucket, fileName, file.getInputStream(), metaData);
        } catch (IOException e) {
            throw new FileUploadException("파일 업로드 실패");
        }
        String url = s3Client.getUrl(bucket, fileName).toString();

        return new UploadFileDto(fileName, url);
    }

    public void deleteFiles(List<UploadFile> files) {
        for (UploadFile file : files) {
            s3Client.deleteObject(bucket, file.getFileName());
        }
    }


    private String changeFileName() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

}
