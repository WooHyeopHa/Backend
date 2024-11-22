package com.whh.findmusechatting.appointment;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record AppointmentResponse(
        String id,
        String roomId,
        String creatorId,
        String location,
        LocalDateTime appointmentTime,
        List<Appointment.NotificationSetting> notificationSettings,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static AppointmentResponse from(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .roomId(appointment.getRoomId())
                .creatorId(appointment.getCreatorId())
                .location(appointment.getLocation())
                .appointmentTime(appointment.getAppointmentTime())
                .notificationSettings(appointment.getNotificationSettings())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .build();
    }
}
