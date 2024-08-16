package com.whh.findmuseapi.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class Infos {
    @RequiredArgsConstructor
    public enum Gender {
        MEN("남자"),
        WOMEN("여자");
        private final String info;

    }
    @RequiredArgsConstructor
    public enum LoginType {
        APPLE("애플 로그인");

        private final String info;
    }

    @RequiredArgsConstructor
    public enum Ages {
        ALL("All"),
        TEENAGER("10대"),
        TWENTIES("20대"),
        THIRTIES("30대"),
        FOURTIES("40대"),
        REST("50+");

        private final String info;
    }

    @RequiredArgsConstructor
    public enum InvieteStatus {
        ACCESS("승인됨"),
        DENY("거절됨"),
        Wait("대기중");

        private final String info;
    }

    public enum Rating {

    }

    @RequiredArgsConstructor
    public enum ArtType {
        MUSICAL("뮤지컬"),
        EXHIBITION("전시회"),
        FILM_FESTIVAL("영화제"),
        CONCERT("콘서트");

        private final String info;

    }

    @RequiredArgsConstructor
    public enum AlarmType {
        ACTIVITY("활동"),
        GREETING("일정");

        private final String info;
    }
    
    @Getter
    @RequiredArgsConstructor
    public enum Role {
        GUEST("GUEST", "게스트"),          // 추가 정보 입력 전
        USER("USER", "일반 사용자");        // 추가 정보 입력 완
        
        private final String key;
        private final String title;
    }

}