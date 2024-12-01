package com.whh.findmusechatting.chat.repository;

import com.whh.findmusechatting.chat.entity.ChatRoom;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ChatRoomRepository extends ReactiveMongoRepository<ChatRoom, String> {
    @Query("{ 'participants.id': ?0 }")
    Flux<ChatRoom> findByParticipantsContaining(String userId);
}
