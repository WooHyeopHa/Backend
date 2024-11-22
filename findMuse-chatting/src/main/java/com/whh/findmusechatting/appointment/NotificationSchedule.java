package com.whh.findmusechatting.appointment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationSchedule {
    ONE_DAY(1),
    THREE_DAYS(3),
    ONE_WEEK(7);

    private final int days;
}
