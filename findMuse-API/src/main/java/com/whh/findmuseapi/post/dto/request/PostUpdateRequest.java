package com.whh.findmuseapi.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * class: PostUpdateRequest.
 *
 * @author devminseo
 * @version 8/22/24
 */

@Getter
@NoArgsConstructor
public class PostUpdateRequest {
    @NotBlank(message = "아이디를 입력해 주세요.")
    @PositiveOrZero
    private Long userId;

    @NotBlank(message = "게시글 번호를 입력해 주세요.")
    @PositiveOrZero
    private Long postId;

    // 추후에 다시 길이 제한 걸어야 할 듯.
    @NotBlank(message = "제목을 입력해 주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해 주세요.")
    private String content;

    @NotBlank(message = "장소를 입력해 주세요.")
    private String place;

    @NotBlank(message = "마감일을 입력해 주세요.")
    private LocalDate endDate;
    // 클라이언트에서 문자열로 보낼지 포맷에 맞춰서 보낼지 상의후 타입을 선택해야할 것 같음.

    @NotBlank(message = "초대 인원을 입력해 주세요.")
    private int inviteCount;

    @NotBlank(message = "선호 연령을 입력해 주세요.")
    private String ages;

    @NotBlank(message = "전시회를 입력해 주세요.")
    private Long artId;

    @NotBlank(message = "태그를 입력해 주세요.")
    private List<String> tagList;
}
