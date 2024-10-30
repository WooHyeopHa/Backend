package com.whh.findmusechatting.chat.repository;


import com.whh.findmusechatting.chat.entity.ChatMessage;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ChatMessageRepository extends ReactiveMongoRepository<ChatMessage, String> {
    Flux<ChatMessage> findByRoomIdOrderByTimestampDesc(String roomId);
    Mono<Void> deleteByRoomId(String roomId);
}
