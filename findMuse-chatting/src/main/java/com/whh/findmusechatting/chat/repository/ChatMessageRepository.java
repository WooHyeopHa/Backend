package com.whh.findmusechatting.chat.repository;


import com.whh.findmusechatting.chat.entity.ChatMessage;
import java.time.LocalDateTime;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ChatMessageRepository extends ReactiveMongoRepository<ChatMessage, String> {
    Flux<ChatMessage> findByRoomIdOrderByTimestampAsc(String roomId);
    Flux<ChatMessage> findByRoomIdOrderByTimestampDesc(String roomId);
    Mono<Void> deleteByRoomId(String roomId);
    
    @Query("{ 'roomId': ?0, 'timestamp': { $gt: ?1 }, 'senderId': { $ne: ?2 } }")
    Mono<Long> countUnreadMessages(String roomId, LocalDateTime lastReadTimestamp, String userId);
    
}
