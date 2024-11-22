package com.whh.findmusechatting.appointment;

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
@Document(collection = "appointments")
public class Appointment {
    @Id
    private String id;
    private String roomId;
    private String creatorId;
    private String location;
    private LocalDateTime appointmentTime;
    private List<NotificationSetting> notificationSettings;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NotificationSetting {
        private NotificationSchedule schedule;
        private LocalDateTime notificationTime;
        private boolean isNotified;
    }
}
