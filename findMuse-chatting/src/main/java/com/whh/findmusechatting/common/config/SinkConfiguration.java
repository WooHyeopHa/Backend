package com.whh.findmusechatting.common.config;

import com.whh.findmusechatting.chat.dto.response.ChatMessageResponse;
import com.whh.findmusechatting.chat.entity.ChatMessage;
import com.whh.findmusechatting.chat.entity.ChatNotification;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Sinks;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class SinkConfiguration {

    @Bean
    public Map<String, Sinks.Many<ChatMessage>> messagesSinks() {
        return new HashMap<>(); // ChatMessage sink를 저장할 Map
    }

    @Bean
    public Map<String, Sinks.Many<ChatNotification>> notificationSinks() {
        return new HashMap<>(); // ChatNotification sink를 저장할 Map
    }

    // 특정 방의 메시지 sink를 가져오거나 생성하는 메서드
    public Sinks.Many<ChatMessage> getOrCreateMessageSink(Map<String, Sinks.Many<ChatMessage>> sinks, String roomId) {
        return sinks.computeIfAbsent(roomId, id -> Sinks.many().multicast().onBackpressureBuffer());
    }

    // 특정 사용자의 알림 sink를 가져오거나 생성하는 메서드
    public Sinks.Many<ChatNotification> getOrCreateNotificationSink(Map<String, Sinks.Many<ChatNotification>> sinks, String userId) {
        return sinks.computeIfAbsent(userId, id -> Sinks.many().multicast().onBackpressureBuffer());
    }
}
