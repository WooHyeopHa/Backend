package com.whh.findmusechatting.appointment;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
public interface AppointmentRepository extends ReactiveMongoRepository<Appointment, String> {
    Mono<Appointment> findByRoomId(String roomId);
    Mono<Void> deleteByRoomId(String roomId);
    Flux<Appointment> findByAppointmentTimeBetween(LocalDateTime start, LocalDateTime end);
}
