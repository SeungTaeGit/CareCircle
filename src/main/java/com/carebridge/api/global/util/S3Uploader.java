package com.carebridge.api.global.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Uploader {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile multipartFile, String dirName) {
        String originalFileName = multipartFile.getOriginalFilename();
        String uniqueFileName = dirName + "/" + UUID.randomUUID() + "_" + originalFileName;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        try {
            amazonS3Client.putObject(bucket, uniqueFileName, multipartFile.getInputStream(), metadata);
        } catch (IOException e) {
            log.error("S3 파일 업로드 중 에러 발생: {}", e.getMessage());
            throw new RuntimeException("파일 업로드에 실패했습니다.");
        }

        return amazonS3Client.getUrl(bucket, uniqueFileName).toString();
    }
}