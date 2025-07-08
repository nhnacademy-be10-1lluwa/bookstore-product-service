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
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileUploadController {

    private final MinioStorageService minioStorageService;
    private final MinioClient minioClient;
    private final String bucket = "1lluwa"; // config 로 주입해도 좋음

    // 다중 파일 업로드
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<UploadResponse>> upload(@RequestPart("file") List<MultipartFile> files,
                                                       @RequestHeader("X-USER-ID") Long memberId){

        List<UploadResponse> responses = new ArrayList<>();

        for (MultipartFile file : files) {
            String url = minioStorageService.uploadReviewImage(memberId, file);
            responses.add(new UploadResponse(file.getOriginalFilename(), url));
        }

        return ResponseEntity.ok(responses);
    }

    // 파일 삭제
    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestParam String img) throws Exception {
        minioStorageService.deleteFile(img);
        return ResponseEntity.noContent().build();
    }

    // pre-signed URL 반환
    @GetMapping("/url")
    public ResponseEntity<String> getPreSignedUrl(@RequestParam String img) throws Exception {
        String url = minioStorageService.getPreSignedUrl(img);
        return ResponseEntity.ok(url);
    }

    // 파일 직접 다운로드 또는 표시
    @GetMapping("/show")
    public ResponseEntity<byte[]> getImage(@RequestParam String img){
        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(img)
                        .build()))
        {
            byte[] bytes = stream.readAllBytes();
            String contentType = URLConnection.guessContentTypeFromName(img);
            if (contentType == null) contentType = "application/octet-stream";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + img + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(bytes);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}