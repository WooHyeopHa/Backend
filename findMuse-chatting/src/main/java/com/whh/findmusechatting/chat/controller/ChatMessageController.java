package com.whh.findmusechatting.chat.controller;

import com.whh.findmusechatting.chat.entity.ChatNotification;
import com.whh.findmusechatting.chat.entity.ChatMessage;
import com.whh.findmusechatting.chat.service.ChatMessageService;
import com.whh.findmusechatting.chat.service.ChatRoomService;
import io.rsocket.exceptions.RejectedSetupException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.invocation.MethodArgumentResolutionException;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatMessageController  {

    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;

    //현재 접속자를 보기위한 임시 Map
    private Map<Object, RSocketRequester> CLIENT = new HashMap<>();

    private Map<Object, Sinks.Many<ChatNotification>> notificationSinks = new ConcurrentHashMap<>();

    @ConnectMapping
    public void connect(RSocketRequester requester, @Payload Object payload){
        log.info("dataMimeType {}", requester.dataMimeType());
        log.info("metadataMimeType  {}", requester.metadataMimeType());
        log.info("Object payload  {} " , payload);
        requester.rsocket()
                .onClose()
                .doFirst(() -> {
                    System.out.println("*******  ConnectMapping ******* ::: " + payload);
                    CLIENT.put(payload, requester);
                })
                .doFinally(consumer -> {
                    System.out.println("******* Connect  close *******  :: " + payload);
                    CLIENT.remove(payload);
                }).subscribe();
    }

    @MessageExceptionHandler(MethodArgumentResolutionException.class)
    public Mono<String> coonectError(MethodArgumentResolutionException ex, RSocketRequester requester) {
        requester.rsocket().dispose();
        log.error("An error occurred while processing the message: {}", ex.getMessage());
        return Mono.error(new RejectedSetupException("An error occurred while processing the message"));
    }

    /**
     * RequestRespnse
     * @param chatMessage
     * @return 추후 API Result로 Client에 전달
     */
    @MessageMapping("chat.sendMessage")
    public Mono<ChatMessage> sendMessage(@Payload ChatMessage chatMessage) {
        if (chatMessage.getSenderId() == null) {
            return Mono.error(new Exception("SenderId null"));
        }
        if (chatMessage.getReceiverId() == null) {
            return Mono.error(new Exception("receiverID null"));
        }

        log.info("sendMessage  진입함  sender : {}   receiver : {}", chatMessage.getSenderId(), chatMessage.getReceiverId());

        // 채팅방 ID를 가져오기
        Mono<String> chatId = chatRoomService.getChatId(chatMessage.getSenderId(), chatMessage.getReceiverId(), true);
        return chatId.flatMap(id -> {
            chatMessage.setChatId(id);

            // 메시지를 데이터베이스에 저장
            return chatMessageService.save(chatMessage)
                    .doOnNext(savedMessage -> {
                        // 수신자에게 알림 보내기
                        Sinks.Many<ChatNotification> sinks = notificationSinks.get(savedMessage.getReceiverId());
                        if (sinks != null) {
                            log.info("Sinks for ReceiverID {}: {}", savedMessage.getReceiverId(), sinks);
                            // 알림을 생성하여 Sinks에 전송
                            ChatNotification notification = ChatNotification.builder()
                                    .senderName(chatMessage.getSenderName())
                                    .senderId(chatMessage.getSenderId())
                                    .id(savedMessage.getId())
                                    .build();
                            sinks.tryEmitNext(notification);
                        } else {
                            log.info("save Sinks null  ReceiverID : {}", savedMessage.getReceiverId());
                        }
                    });
        });
    }

    // 알림 스트림 (RequestStream)
    @MessageMapping("chat.message")
    public Flux<ChatNotification> notificationStream(@Payload Object payload) {
        log.info("알림 접속 완료: 수신자 ID: {}", payload);
        if (payload == null) {
            return Flux.empty();
        }

        // 알림 Sink가 없으면 새로 생성
        notificationSinks.computeIfAbsent(payload, key -> {
            log.info("RequestStream connected for key: {}", key);
            return Sinks.many().multicast().onBackpressureBuffer();
        });

        Sinks.Many<ChatNotification> sinks = notificationSinks.get(payload);

        // Flux를 반환하여 구독자가 메시지를 받을 수 있도록 함
        return sinks.asFlux()
                .doOnNext(next -> log.info("Stream next -> {}", next))
                .doOnCancel(() -> {
                    log.info("Stream canceled for payload: {}", payload);
                    notificationSinks.remove(payload);
                });
    }

    @GetMapping(value = "/messages/{senderId}/{receiverId}")
    public Flux<ChatMessage> findChatMessages(@PathVariable String senderId, @PathVariable String receiverId){
        return chatMessageService.findChatMessages(senderId,receiverId);
    }


    @GetMapping("/messages/{senderId}/{receiverId}/count")
    public Mono<Long> countNewMessages(@PathVariable String senderId, @PathVariable String receiverId){
        return chatMessageService.countNewMessages(senderId, receiverId);
    }

    @GetMapping("/messages/{id}")
    public Mono<ChatMessage> findMessage(@PathVariable String id){
        return chatMessageService.findById(id);
    }


    //접속 상태 확인
    @GetMapping("/connect/{username}")
    public Mono<Boolean> connectByUsername(@PathVariable("username") String username){
        return Mono.just(CLIENT.containsKey(username) );
    }

    @GetMapping("/connectAll")
    public Flux<Object> connectByAll(){
        return Flux.fromIterable(CLIENT.keySet());
    }

}
