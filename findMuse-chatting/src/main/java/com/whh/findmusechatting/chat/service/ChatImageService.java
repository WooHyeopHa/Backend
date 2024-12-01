package com.whh.findmusechatting.chat.service;

import com.whh.findmusechatting.chat.dto.response.ChatRoomImagesResponse;
import com.whh.findmusechatting.chat.dto.response.ImageMessageResponse;
import com.whh.findmusechatting.chat.entity.ChatMessage;
import com.whh.findmusechatting.chat.entity.constant.MessageType;
import com.whh.findmusechatting.chat.repository.ChatMessageRepository;
import com.whh.findmusechatting.common.util.S3Util;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ChatImageService {

    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final MysqlUserService mysqlUserService;
    private final ChatMessageRepository messageRepository;
    private final S3Util s3Util;

    @Description("채팅방 이미지 메시지 조회")
    public Mono<ChatRoomImagesResponse> getChatRoomImages(String roomId, int page, int size) {
        Query query = new Query(Criteria.where("roomId").is(roomId)
                .and("messageType").is(MessageType.IMAGE))
                .with(Sort.by(Sort.Direction.DESC, "timestamp"))
                .with(PageRequest.of(page, size));

        Mono<Long> totalCount = reactiveMongoTemplate.count(query, ChatMessage.class);

        Flux<ImageMessageResponse> images = reactiveMongoTemplate.find(query, ChatMessage.class)
                .flatMap(message -> mysqlUserService.findUserInfoById(message.getSenderId(), message.getMessageType())
                        .map(userInfo -> ImageMessageResponse.from(message, userInfo)));

        return Mono.zip(images.collectList(), totalCount)
                .map(tuple -> new ChatRoomImagesResponse(tuple.getT1(), tuple.getT2()));
    }

    @Description("이미지 메시지 다운로드")
    public Mono<Void> downloadChatImage(String messageId, ServerHttpResponse response) {
        return messageRepository.findById(messageId)
                .filter(message -> message.getMessageType() == MessageType.IMAGE)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("이미지 메시지를 찾을 수 없습니다.")))
                .flatMap(message -> {
                    response.getHeaders().set(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + message.getImageDetails().getOriginalFileName() + "\"");
                    response.getHeaders().setContentType(MediaType.parseMediaType(message.getImageDetails().getContentType()));

                    return s3Util.downloadFile(extractKeyFromUrl(message.getContent()))
                            .map(byteBuffer -> response.bufferFactory().wrap(byteBuffer))  // ByteBuffer를 DataBuffer로 변환
                            .doOnDiscard(DataBuffer.class, DataBufferUtils.releaseConsumer())  // DataBuffer 해제
                            .as(response::writeWith);  // DataBuffer 스트림을 응답에 씀
                });
    }

    @Description("단일 이미지 메시지 삭제")
    public Mono<Void> deleteImageMessage(String messageId) {
        return messageRepository.findById(messageId)
                .filter(message -> message.getMessageType() == MessageType.IMAGE)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("이미지 메시지를 찾을 수 없습니다.")))
                .flatMap(message -> {
                    String key = extractKeyFromUrl(message.getContent());
                    return s3Util.deleteFile(key)
                            .then(messageRepository.deleteById(messageId));
                });
    }

    @Description("채팅방의 모든 이미지 메시지 삭제")
    public Mono<Void> deleteAllRoomImages(String roomId) {
        Query query = new Query(Criteria.where("roomId").is(roomId)
                .and("messageType").is(MessageType.IMAGE));

        return reactiveMongoTemplate.find(query, ChatMessage.class)
                .flatMap(message -> {
                    String key = extractKeyFromUrl(message.getContent());
                    return s3Util.deleteFile(key)
                            .then(messageRepository.deleteById(message.getId()));
                })
                .then();
    }

    private String extractKeyFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}
