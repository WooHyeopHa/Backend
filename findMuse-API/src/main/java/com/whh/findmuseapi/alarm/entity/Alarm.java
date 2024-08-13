package com.whh.findmuseapi.alarm.entity;

import com.whh.findmuseapi.common.constant.Infos;
import com.whh.findmuseapi.common.constant.Infos.AlarmType;
import com.whh.findmuseapi.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Alarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id")
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createAt;
    private boolean readStatus;
    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
