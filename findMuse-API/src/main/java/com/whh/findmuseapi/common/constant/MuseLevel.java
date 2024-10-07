package com.whh.findmuseapi.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MuseLevel {
    LEVEL_1(1, "티켓 수집에 눈 뜬 무대 밖 관객", 1, 1),
    LEVEL_2(2, "호기심 가득한 문화예술 뮤즈", 3, 3),
    LEVEL_3(3, "리듬에 몸을 맡기는 열정적인 뮤즈", 7, 5),
    LEVEL_4(4, "안목 있는 문화예술 컬렉터 뮤즈", 20, 10),
    LEVEL_5(5, "영감의 원천이 되는 문화예술 마스터", 30, 15);

    private final int level;
    private final String description;
    private final int viewCount;
    private final int findMuseCount;

    public static MuseLevel determineLevel(int viewCount, int findMuseCount) {
        MuseLevel determinedLevel = LEVEL_1;

        for (MuseLevel level : values()) {
            if (viewCount >= level.getViewCount() && findMuseCount >= level.getFindMuseCount()) {
                determinedLevel = level;
            }
        }
        return determinedLevel;
    }
}

