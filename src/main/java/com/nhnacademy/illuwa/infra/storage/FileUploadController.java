package com.nhnacademy.illuwa.infra.storage;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileUploadController {

    private final MinioStorageService minioStorageService;
    private final MinioClient minioClient;
    private final String bucket = "1lluwa";  // 필요 시 config에서 주입 가능

    // 파일 업로드
    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestPart MultipartFile file) throws Exception {
        String fileName = minioStorageService.uploadFile(file);
        return ResponseEntity.ok("Uploaded: " + fileName);
    }

    // 저장된 이미지 Presigned URL 반환 (제한된 시간 동안 접근 가능)
    @GetMapping("/image-url/{objectName}")
    public ResponseEntity<String> getImageUrl(@PathVariable String objectName) throws Exception {
        String url = minioStorageService.getPresignedUrl(objectName);
        return ResponseEntity.ok(url);
    }

    // 저장된 이미지 직접 다운로드/표시
    @GetMapping("/{objectName}")
    public ResponseEntity<byte[]> getImage(@PathVariable String objectName) throws Exception {
        System.out.println("Request for object: " + objectName);
        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .build())) {

            byte[] imageBytes = stream.readAllBytes();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + objectName + "\"")
                    .contentType(MediaType.IMAGE_PNG)
                    .body(imageBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }
}