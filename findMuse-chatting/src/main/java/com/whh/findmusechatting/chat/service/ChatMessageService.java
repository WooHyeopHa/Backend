package com.whh.findmusechatting.chat.service;

import com.whh.findmusechatting.chat.entity.ChatMessage;
import com.whh.findmusechatting.chat.repository.ChatMessageRepository;
import com.whh.findmusechatting.common.constant.MessageStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {
    private final ChatRoomService chatRoomService;
    private final ChatMessageRepository messageRepository;

    /**
     * 메세지를 저장하고 초기 상태를 RECEIVED로 설정합니다.
     */
    public Mono<ChatMessage> save(ChatMessage chatMessage) {
        chatMessage.setStatus(MessageStatus.RECEIVED);
        return messageRepository.save(chatMessage);
    }

    /**
     * 발신자와 수신자 간의 채팅 메시지를 검색하고 상태를 DELIVERED로 업데이트합니다.
     *
     * @param senderId   발신자 ID
     * @param receiverId 수신자 ID
     * @return ChatMessage의 Flux
     */
    public Flux<ChatMessage> findChatMessages(String senderId, String receiverId) {
        return chatRoomService.getChatId(senderId, receiverId, false)
                .flatMapMany(chatId -> messageRepository.findByChatId(chatId))
                .flatMap(message -> {
                    // 상태를 읽음으로 변경
                    if (message.getSenderId().equals(receiverId)
                            && message.getReceiverId().equals(senderId)
                            && message.getStatus().equals(MessageStatus.RECEIVED)) {
                        message.setStatus(MessageStatus.DELIVERED);
                        return messageRepository.save(message);
                    }
                    return Mono.just(message);
                })
                .doOnNext(next -> log.info("검색된 메시지: {}", next));
    }

    /**
     * 특정 발신자와 수신자에 대한 새로운 메시지 수를 계산합니다.
     *
     * @param senderId   발신자 ID
     * @param receiverId 수신자 ID
     * @return 새로운 메시지 수를 포함하는 Mono
     */
    public Mono<Long> countNewMessages(String senderId, String receiverId) {
        return messageRepository.countBySenderIdAndReceiverIdAndStatus(senderId, receiverId, MessageStatus.RECEIVED);
    }

    /**
     * ID로 메시지를 찾고 상태를 DELIVERED로 업데이트합니다.
     *
     * @param id 메시지 ID
     * @return ChatMessage를 포함하는 Mono
     */
    public Mono<ChatMessage> findById(String id) {
        return messageRepository.findById(id)
                .flatMap(chatMessage -> {
                    chatMessage.setStatus(MessageStatus.DELIVERED);
                    return messageRepository.save(chatMessage);
                })
                .doOnSuccess(message -> log.info("메시지 업데이트됨: {}", message));
    }
}
