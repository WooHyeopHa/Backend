package com.whh.findmusechatting.chat.repository;

import com.whh.findmusechatting.chat.entity.UserRoomStatus;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface UserRoomStatusRepository  extends ReactiveMongoRepository<UserRoomStatus, String> {
    Mono<UserRoomStatus> findByUserIdAndRoomId(String userId, String roomId);
}
