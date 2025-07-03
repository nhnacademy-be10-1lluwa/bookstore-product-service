package com.nhnacademy.illuwa.infra.storage;
import com.nhnacademy.illuwa.infra.storage.exception.FileUploadFailedException;
import com.nhnacademy.illuwa.infra.storage.exception.InvalidFileFormatException;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
public class MinioStorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/gif", "image/webp");

    @PostConstruct
    public void init() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            log.info("Bucket '{}' 을(를) 생성했습니다.", bucket);
        } else {
            log.info("Bucket '{}' 이(가) 이미 존재합니다.", bucket);
        }
    }

    public MinioStorageService(@Value("${minio.url}") String url,
                               @Value("${minio.access-key}") String accessKey,
                               @Value("${minio.secret-key}") String secretKey) {
        this.minioClient = MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }

    public String uploadFile(String domain, Long memberId, MultipartFile file){
        try {
            validateImageExtension(file);
            validateImageContentType(file);

            // 도메인별로 분리해서 저장
            String extension = getExtension(file.getOriginalFilename());
            String objectName = domain.equalsIgnoreCase("Book")
                    ? String.format("%s/%s.%s", domain, UUID.randomUUID(), extension)
                    : String.format("%s/%s/%s.%s", domain, memberId, UUID.randomUUID(), extension);

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            String url = getPreSignedUrl(objectName);

            log.info("이미지 '{}' 을(를) 성공적으로 업로드 했습니다.", file.getOriginalFilename());

            return url;

        }
        catch (Exception e) {
            throw new FileUploadFailedException("파일 업로드에 실패했습니다.", e);
        }
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
                        .expiry(7, TimeUnit.DAYS)  // 7일
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
}