package com.whh.findmusechatting.appointment;

import com.whh.findmusechatting.chat.dto.request.CreateAppointmentRequest;
import com.whh.findmusechatting.chat.dto.request.UpdateAppointmentRequest;
import com.whh.findmusechatting.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final ChatService chatService;

    @Description("약속 생성")
    public Mono<AppointmentResponse> createAppointment(CreateAppointmentRequest request) {
        List<Appointment.NotificationSetting> notificationSettings = request.notificationSchedules().stream()
                .map(schedule -> Appointment.NotificationSetting.builder()
                        .schedule(schedule)
                        .notificationTime(request.appointmentTime().minusDays(schedule.getDays()))
                        .isNotified(false)
                        .build())
                .toList();

        Appointment appointment = Appointment.builder()
                .roomId(request.roomId())
                .creatorId(request.creatorId())
                .location(request.location())
                .appointmentTime(request.appointmentTime())
                .notificationSettings(notificationSettings)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return appointmentRepository.save(appointment)
                .doOnSuccess(chatService::sendAppointmentMessage)
                .map(AppointmentResponse::from);
    }

    @Description("약속 수정")
    public Mono<AppointmentResponse> updateAppointment(String roomId, UpdateAppointmentRequest request) {
        return appointmentRepository.findByRoomId(roomId)
                .flatMap(appointment -> {
                    appointment.setLocation(request.location());
                    appointment.setAppointmentTime(request.appointmentTime());
                    appointment.setUpdatedAt(LocalDateTime.now());

                    List<Appointment.NotificationSetting> newSetting = request.notificationSchedules().stream()
                            .map(schedule -> Appointment.NotificationSetting.builder()
                                    .schedule(schedule)
                                    .notificationTime(request.appointmentTime().minusDays(schedule.getDays()))
                                    .isNotified(false)
                                    .build())
                            .toList();

                    appointment.setNotificationSettings(newSetting);

                    return appointmentRepository.save(appointment)
                            .doOnSuccess(chatService::sendAppointmentMessage);
                })
                .map(AppointmentResponse::from);
    }


    @Description("약속 삭제")
    public Mono<Void> deleteAppointment(String roomId) {
        return appointmentRepository.deleteByRoomId(roomId)
                .flatMap(appointment -> {
                    return appointmentRepository.deleteByRoomId(roomId);
                });
    }

    @Description("채팅방의 약속 조회")
    public Mono<AppointmentResponse> getAppointment(String roomId) {
        return appointmentRepository.findByRoomId(roomId)
                .map(AppointmentResponse::from);
    }
}
