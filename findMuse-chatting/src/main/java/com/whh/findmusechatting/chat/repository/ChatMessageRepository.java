package com.whh.findmusechatting.chat.repository;

import com.whh.findmusechatting.chat.entity.ChatMessage;
import com.whh.findmusechatting.common.constant.MessageStatus;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChatMessageRepository extends ReactiveMongoRepository<ChatMessage, String> {
    Mono<Long> countBySenderIdAndReceiverIdAndStatus(String senderId, String receiverId, MessageStatus status);
    Flux<ChatMessage> findByChatId(String chatId);
}
