package com.whh.findmusechatting.common.util;

import com.whh.findmusechatting.chat.entity.File;
import com.whh.findmusechatting.chat.entity.FileDetail;
import com.whh.findmusechatting.chat.entity.constant.FileType;
import com.whh.findmusechatting.chat.repository.FileRepository;
import com.whh.findmusechatting.common.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.*;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;


@Service
@RequiredArgsConstructor
@Slf4j
public class S3Util {
    private final S3AsyncClient s3AsyncClient;
    private final FileRepository fileRepository;

    @Value("${aws.s3.bucket-name}")
    private String bucket;

    /**
     * 대용량 파일 멀티파트 업로드
     * @param filePart 업로드할 파일
     * @param fileType 파일 타입
     * @return 저장된 파일 정보
     */
    public Mono<File> uploadFile(FilePart filePart, String fileType) {
        String filename = URLEncoder.encode(filePart.filename(), StandardCharsets.UTF_8);
        final UUID key = UUID.randomUUID();
        final String keyString = key.toString();

        Map<String, String> metadata = Map.of(
                "filename", filename,
                "contentType", Optional.ofNullable(filePart.headers().getContentType())
                        .map(MediaType::toString)
                        .orElse("application/octet-stream"),
                "uploadDate", LocalDateTime.now().toString()
        );

        CreateMultipartUploadRequest createRequest = CreateMultipartUploadRequest.builder()
                .bucket(bucket)
                .key(keyString)
                .metadata(metadata)
                .build();

        return Mono.fromCompletionStage(s3AsyncClient.createMultipartUpload(createRequest))
                .flatMap(uploadResponse -> {
                    String uploadId = uploadResponse.uploadId();
                    AtomicInteger partNumber = new AtomicInteger(1);

                    return DataBufferUtils.join(filePart.content())
                            .flatMap(dataBuffer -> {
                                byte[] fileContent = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(fileContent);
                                DataBufferUtils.release(dataBuffer);

                                return uploadPart(bucket, keyString, uploadId, partNumber.get(), fileContent)
                                        .flatMap(part -> completeUpload(bucket, keyString, uploadId, part));
                            });
                })
                .flatMap(response -> createFileEntity(key, URLDecoder.decode(filename, StandardCharsets.UTF_8),
                        filePart.headers().getContentType(), fileType.toLowerCase()))
                .doOnSuccess(file -> log.info("파일 업로드 완료: {}", file.getName()))
                .doOnError(e -> log.error("파일 업로드 실패: {}", e.getMessage()))
                .onErrorMap(e -> e instanceof FileUploadFailedException ? e :
                        new ExecuteFailedException("파일 처리 중 오류 발생", e));
    }

    /**
     * S3에서 파일 삭제
     * @param fileName 삭제할 파일명
     * @return void
     */
    public Mono<Void> deleteFile(String fileName) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .build();

        return Mono.fromFuture(() -> s3AsyncClient.deleteObject(deleteObjectRequest))
                .then(fileRepository.deleteByFileDetailUrl(
                        String.format("https://%s.s3.amazonaws.com/%s", bucket, fileName)))
                .doOnSuccess(v -> log.info("파일 삭제 완료: {}", fileName))
                .doOnError(e -> log.error("파일 삭제 실패: {}", e.getMessage()))
                .onErrorMap(e -> new FileDeleteFailedException("파일 삭제 실패", e));
    }

    /**
     * S3에서 파일 다운로드
     * @param fileName 다운로드할 파일명
     * @return 파일 데이터 스트림
     */
    public Flux<ByteBuffer> downloadFile(String fileName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .build();

        return Mono.fromFuture(() -> s3AsyncClient.getObject(getObjectRequest, AsyncResponseTransformer.toBytes()))
                .flatMapMany(responseBytes -> Flux.just(responseBytes.asByteBuffer()))
                .doOnError(e -> log.error("파일 다운로드 실패: {}", e.getMessage()))
                .onErrorMap(e -> new FileDownloadFailedException("파일 다운로드 실패", e));
    }

    /**
     * S3에 파일 존재 여부 확인
     * @param fileName 확인할 파일명
     * @return 존재 여부
     */
    public Mono<Boolean> doesFileExist(String fileName) {
        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .build();

        return Mono.fromFuture(() -> s3AsyncClient.headObject(headObjectRequest))
                .map(response -> true)
                .onErrorResume(NoSuchKeyException.class, e -> Mono.just(false))
                .onErrorResume(e -> {
                    log.error("파일 존재 여부 확인 실패: {}", e.getMessage());
                    return Mono.error(new FileCheckFailedException("파일 존재 여부 확인 실패", e));
                });
    }

    /**
     * 파트 업로드 처리
     */
    private Mono<CompletedPart> uploadPart(String bucket, String key, String uploadId,
                                           int partNumber, byte[] content) {
        return Mono.fromCompletionStage(s3AsyncClient.uploadPart(
                        UploadPartRequest.builder()
                                .bucket(bucket)
                                .key(key)
                                .partNumber(partNumber)
                                .uploadId(uploadId)
                                .build(),
                        AsyncRequestBody.fromBytes(content)))
                .map(response -> CompletedPart.builder()
                        .partNumber(partNumber)
                        .eTag(response.eTag())
                        .build());
    }

    /**
     * 멀티파트 업로드 완료 처리
     */
    private Mono<CompleteMultipartUploadResponse> completeUpload(String bucket, String key,
                                                                 String uploadId, CompletedPart part) {
        CompleteMultipartUploadRequest completeRequest = CompleteMultipartUploadRequest.builder()
                .bucket(bucket)
                .key(key)
                .uploadId(uploadId)
                .multipartUpload(upload -> upload.parts(Collections.singletonList(part)))
                .build();

        return Mono.fromCompletionStage(s3AsyncClient.completeMultipartUpload(completeRequest));
    }

    /**
     * 파일 엔티티 생성 및 저장
     */
    private Mono<File> createFileEntity(UUID key, String fileName, MediaType mediaType, String fileType) {
        return Mono.just(File.create(
                        key,
                        fileName,
                        mediaType != null ? mediaType.toString() : "application/octet-stream",
                        FileDetail.create(
                                FileType.valueOf(fileType.toUpperCase()),
                                String.format("https://%s.s3.amazonaws.com/%s", bucket, key))
                ))
                .flatMap(fileRepository::save);
    }
}