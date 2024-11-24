package com.whh.findmuseapi.common.constant;

import com.whh.findmuseapi.common.exception.CBadRequestException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;

public class Infos {
    @Getter
    @RequiredArgsConstructor
    public enum Gender {
        MEN("남성"),
        WOMEN("여성");
        private final String info;

        public static Gender convertStringToGender(String info) {
            return Arrays.stream(Gender.values())
                    .filter(gender -> gender.info.equals(info))
                    .findFirst()
                    .orElseThrow(() -> new CBadRequestException("유효하지 않은 성별 값이 입력되었습니다."));
        }
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
    @Getter
    public enum ArtType {
        MUSICAL_DRAMA("뮤지컬/연극"),
        EXHIBITION("전시회"),
        DANCE_CLASSIC("무용/클래식"),
        CONCERT("콘서트");

        private final String info;
        public static ArtType convert(String info){
            for (ArtType value : ArtType.values()) {
                if (value.getInfo().equals(info)) {
                    return value;
                }
            }
            throw new CBadRequestException("일치하는 장르가 없습니다. 다시 요청해주세요");
        }

    }

    @Getter
    @RequiredArgsConstructor
    public enum ReviewSortType {
        LATEST("최신순"),
        POPULAR("인기순");

        private final String description;

        public static ReviewSortType fromString(String value) {
            for (ReviewSortType type : ReviewSortType.values()) {
                if (type.getDescription().equals(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Invalid ReviewSortType: " + value);
        }
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
        private final String name;
    }

    @Getter
    @RequiredArgsConstructor
    public enum ResourceUrl {
        PROFILE_IMAGE("/user/profile/");

        private final String url;
    }

}