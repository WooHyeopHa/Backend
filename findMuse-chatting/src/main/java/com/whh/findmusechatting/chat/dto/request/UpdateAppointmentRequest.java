package com.whh.findmusechatting.chat.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.whh.findmusechatting.appointment.NotificationSchedule;

import java.time.LocalDateTime;
import java.util.List;

public record UpdateAppointmentRequest(
        String location,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime appointmentTime,
        List<NotificationSchedule> notificationSchedules
) {
}
