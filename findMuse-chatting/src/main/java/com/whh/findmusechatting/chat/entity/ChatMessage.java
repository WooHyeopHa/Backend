package com.whh.findmusechatting.chat.entity;

import com.whh.findmusechatting.appointment.Appointment;
import com.whh.findmusechatting.chat.dto.request.CreateChatMessageRequest;
import com.whh.findmusechatting.chat.entity.constant.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "messages")
public class ChatMessage {
    @Id
    private String id;
    private String roomId;
    private String senderId;
    private String content;
    private MessageType messageType;
    private LocalDateTime timestamp;

    @Builder.Default
    private ImageDetails imageDetails = null;

    @Builder.Default
    private AppointmentDetails appointmentDetails = null;


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ImageDetails {
        private String originalFileName;
        private String contentType;
        private long fileSize;
        private int width;
        private int height;
        private String thumbnailUrl;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AppointmentDetails {
        private String id;
        private String location;
        private LocalDateTime appointmentTime;
        private List<Appointment.NotificationSetting> notificationSettings;

        public static AppointmentDetails from(Appointment appointment) {
            return AppointmentDetails.builder()
                    .id(appointment.getId())
                    .location(appointment.getLocation())
                    .appointmentTime(appointment.getAppointmentTime())
                    .notificationSettings(appointment.getNotificationSettings())
                    .build();
        }
    }

    public static ChatMessage of(CreateChatMessageRequest request) {
        return ChatMessage.builder()
                .roomId(request.roomId())
                .senderId(request.senderId())
                .content(request.content())
                .messageType(MessageType.CHAT)
                .timestamp(LocalDateTime.now())
                .build();
    }
}