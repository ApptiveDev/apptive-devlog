package apptive.devlog.fileupload.service;

import apptive.devlog.domain.UploadFile;
import apptive.devlog.fileupload.dto.UploadFileDto;
import apptive.devlog.fileupload.exception.FileUploadException;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;
@Profile("!test")
@Service
@Transactional
@RequiredArgsConstructor
public class UploadService {

    private final AmazonS3Client s3Client;

    @Value("${s3.bucket}")
    private String bucket;


    public UploadFileDto upload(MultipartFile file)  {

        String fileName = file.getName();
        String serverFileName = changeFileName(fileName);

        ObjectMetadata metaData = new ObjectMetadata();
        metaData.setContentType(file.getContentType());
        metaData.setContentLength(file.getSize());
        try {
            s3Client.putObject(bucket, serverFileName, file.getInputStream(), metaData);
        } catch (IOException e) {
            throw new FileUploadException("파일 업로드 실패");
        }
        String url = s3Client.getUrl(bucket, serverFileName).toString();

        return new UploadFileDto(fileName, serverFileName, url);
    }

    public UploadFileDto getPresignedURL(String fileName) {

        String serverFileName = changeFileName(fileName);
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 5; // 5분
        expiration.setTime(expTimeMillis);
        String url = s3Client.generatePresignedUrl(bucket, serverFileName, expiration, HttpMethod.PUT).toString();


        return new UploadFileDto(fileName, serverFileName, url);
    }

    public void deleteFiles(List<UploadFile> files) {
        for (UploadFile file : files) {
            s3Client.deleteObject(bucket, file.getServerFileName());
        }
    }


    private String changeFileName(String originalFileName) {
        String ext = originalFileName.substring(originalFileName.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString();
        return uuid + ext;
    }

}
