package com.example.aucison_service.service.s3;

import com.example.aucison_service.exception.AppException;
import com.example.aucison_service.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3Service {

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
    public void deleteFileFromS3Bucket(String fileName, String folderName) {
        String fileKey = folderName + "/" + fileName;
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build());
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

