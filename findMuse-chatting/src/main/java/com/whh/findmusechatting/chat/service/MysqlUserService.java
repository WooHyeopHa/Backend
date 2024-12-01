package com.whh.findmusechatting.chat.service;

import com.whh.findmusechatting.chat.dto.response.ChatMessageResponse;
import com.whh.findmusechatting.chat.dto.response.ChatMessageResponse.UserInfo;
import com.whh.findmusechatting.chat.entity.constant.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class MysqlUserService {

    private final DatabaseClient databaseClient;

    public Mono<ChatMessageResponse.UserInfo> findUserInfoById(String userId, MessageType messageType) {
        if (messageType == MessageType.SYSTEM || messageType == MessageType.APPOINTMENT) {
            return Mono.just(UserInfo.getSystemInfo());
        }
        return databaseClient.sql("SELECT nickname, profile_image_url FROM user WHERE user_id = ?")
                .bind(0, userId)
                .map((row, metadata) -> new ChatMessageResponse.UserInfo(
                        row.get("nickname", String.class),
                        row.get("profile_image_url", String.class)
                ))
                .one()
                .onErrorResume(e -> {
                    MysqlUserService.log.info("Error retrieving user info: " + e.getMessage());
                    return Mono.empty();
                });
    }
}
