package com.whh.findmusechatting.chat.repository;

import com.whh.findmusechatting.chat.entity.ChatRoom;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ChatRoomRepository extends ReactiveMongoRepository<ChatRoom, String> {
    Mono<ChatRoom> findBySenderIdAndReceiverId(String senderId, String receiverId);
}
