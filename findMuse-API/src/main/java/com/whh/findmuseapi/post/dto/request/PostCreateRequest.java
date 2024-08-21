package com.whh.findmuseapi.post.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * class: PostCreateRequest.
 *
 * @author devminseo
 * @version 8/20/24
 */

@Getter
@NoArgsConstructor
public class PostCreateRequest {
    @NotNull(message = "아이디를 입력해 주세요.")
    @PositiveOrZero
    private Long userId;

    @NotNull(message = "제목을 입력해 주세요.")
    private String title;

    @NotNull(message = "내용을 입력해 주세요.")
    private String content;

    @NotNull(message = "장소를 입력해 주세요.")
    private String place;

    @NotNull(message = "마감일을 입력해 주세요.")
    private LocalDate endDate;
    // 클라이언트에서 문자열로 보낼지 포맷에 맞춰서 보낼지 상의후 타입을 선택해야할 것 같음.

    @NotNull(message = "초대 인원을 입력해 주세요.")
    private int inviteCount;

    @NotNull(message = "선호 연령을 입력해 주세요.")
    private String ages;

    @NotNull(message = "전시회를 입력해 주세요.")
    private String artTitle;

    private List<String> tagList;
}
