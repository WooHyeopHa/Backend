package com.whh.findmusechatting.chat.repository;

import com.whh.findmusechatting.chat.entity.ChatMessage;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatNotificationRepository extends ReactiveMongoRepository<ChatMessage, String> {
}
