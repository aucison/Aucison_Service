package com.example.aucison_service.service.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.example.aucison_service.exception.AppException;
import com.example.aucison_service.exception.ErrorCode;
import com.example.aucison_service.service.product.ProductServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

@Service
public class S3Service {
    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);

    @Autowired
    private S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    @Value("${cloud.aws.region.static}")
    private String region;

    //파일 업로드 메소드
    public void uploadFileToS3Bucket(MultipartFile file, String folderName) {
        try {
            String fileName = folderName + "/" + file.getOriginalFilename();
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException e) {
            throw new AppException(ErrorCode.IMAGE_PROCESSING_FAIL);
        }
    }


    // 파일 삭제 메소드
    public void deleteFileFromS3Bucket(String fullUrl, String folderName) {
        try {
            // URL에서 객체 키 추출
            URI uri = new URI(fullUrl);
            String path = uri.getPath();
            String fileKey = path.substring(path.indexOf(folderName)); // "membersProfile/..." 형식으로 추출

            // S3 객체 삭제
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build());
        }  catch (URISyntaxException e) {
            logger.info("Invalid URL syntax: " + fullUrl);
        }   catch (AmazonServiceException e) {
            // AWS 서비스 측 오류 로그
            logger.info("AmazonServiceException: " + e.getErrorMessage());
            throw e;
        } catch (SdkClientException e) {
            // 클라이언트 측 오류 로그
            logger.info("SdkClientException: " + e.getMessage());
            throw e;
        }
    }

    // 파일 업로드하고 URL 반환 메소드
    public String uploadFileAndGetUrl(MultipartFile file, String folderName) {
        try {
            String fileName = folderName + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + fileName;
        } catch (IOException e) {
            throw new AppException(ErrorCode.IMAGE_PROCESSING_FAIL);
        }
    }
}

