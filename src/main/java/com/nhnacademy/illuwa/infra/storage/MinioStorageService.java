package com.nhnacademy.illuwa.infra.storage;
import com.nhnacademy.illuwa.infra.storage.exception.InvalidFileFormatException;
import io.minio.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class MinioStorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/gif", "image/webp");


    public MinioStorageService(@Value("${minio.url}") String url,
                               @Value("${minio.access-key}") String accessKey,
                               @Value("${minio.secret-key}") String secretKey) {
        this.minioClient = MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }

    public String uploadFile(String domain, Long memberId, MultipartFile file) throws Exception {
        validateImageExtension(file);
        validateImageContentType(file);
        String extension = getExtension(file.getOriginalFilename());

        String objectName = String.format("%s/%s/%s.%s", domain, memberId, UUID.randomUUID(), extension);

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );

        return objectName;
    }

    public void deleteFile(String objectName) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .build()
        );
    }

    public String getPreSignedUrl(String objectName) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucket)
                        .object(objectName)
                        .expiry(60 * 60)  // 1시간
                        .build()
        );
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new InvalidFileFormatException("파일 이름에 확장자가 없습니다.");
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }

    private void validateImageExtension(MultipartFile file) {
        String extension = getExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new InvalidFileFormatException("지원하지 않는 이미지 형식입니다: " + extension);
        }
    }

    private void validateImageContentType(MultipartFile file) {
        String contentType = file.getContentType();
        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new InvalidFileFormatException("지원하지 않는 이미지 콘텐츠 타입입니다: " + contentType);
        }
    }

    public String uploadBookImageFile(MultipartFile bookImageFile) {
            try {
                // 버킷이 존재하는지 확인하고, 없으면 생성
                boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
                if (!found) {
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                    log.info("Bucket '{}' 생성 ", bucket);
                } else {
                    log.info("Bucket '{}' 이미 존재", bucket);
                }

                // 파일 이름이 겹치지 않도록 UUID를 사용해 고유한 파일 이름 생성
                String originalFilename = bookImageFile.getOriginalFilename();
                String bookImageFileName = UUID.randomUUID().toString() + "-" + originalFilename;

                // 파일 업로드
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucket)
                                .object(bookImageFileName)
                                .stream(bookImageFile.getInputStream(), bookImageFile.getSize(), -1)
                                .contentType(bookImageFile.getContentType())
                                .build()
                );

                log.info("'{}' 성공적으로 업로드 '{}' --> bucket 이름 : '{}'.", bookImageFile.getOriginalFilename(), bookImageFileName, bucket);

                return bookImageFileName;

            } catch (Exception e) {
                log.error("MinIO에 업로드 중 에러 발생", e);
                throw new RuntimeException("파일 업로드에 실패했습니다.", e);
            }
        }
    }
