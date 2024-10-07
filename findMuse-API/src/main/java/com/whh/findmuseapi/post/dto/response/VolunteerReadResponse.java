package com.whh.findmuseapi.post.dto.response;

import com.whh.findmuseapi.common.constant.Infos;
import com.whh.findmuseapi.post.entity.Volunteer;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

/**
 * class: VolunteerReadResponse.
 *
 * @author devminseo
 * @version 8/31/24
 */
@Getter
@Builder
public class VolunteerReadResponse {
    private Long id;
    private Infos.InvieteStatus status;
    private LocalDate createDate;
    private Long userId;
    private Long postId;

    public static VolunteerReadResponse toDto(Volunteer volunteer) {
        return VolunteerReadResponse.builder()
                .id(volunteer.getId())
                .status(volunteer.getStatus())
                .createDate(volunteer.getCreateDate())
                .userId(volunteer.getUser().getId())
                .postId(volunteer.getPost().getId())
                .build();
    }

}
