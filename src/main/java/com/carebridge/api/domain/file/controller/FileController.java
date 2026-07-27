package com.carebridge.api.domain.file.controller;

import com.carebridge.api.domain.file.dto.response.FileUploadResponse;
import com.carebridge.api.global.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final S3Uploader s3Uploader;

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "dirName", defaultValue = "missions") String dirName) {

        String uploadedUrl = s3Uploader.upload(file, dirName);

        return ResponseEntity.ok(new FileUploadResponse(uploadedUrl));
    }
}