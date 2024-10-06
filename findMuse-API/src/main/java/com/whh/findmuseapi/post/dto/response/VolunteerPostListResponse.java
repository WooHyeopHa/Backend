package com.whh.findmuseapi.post.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * class: VolunteerListResponse.
 *
 * @author devminseo
 * @version 8/31/24
 */
@Getter
@Builder
public class VolunteerPostListResponse {
    private List<VolunteerReadResponse> participationVolunteers;
    private List<VolunteerReadResponse> waitingVolunteers;

    public static VolunteerPostListResponse toDto(List<VolunteerReadResponse> participationList,List<VolunteerReadResponse> waitingList) {
        return VolunteerPostListResponse.builder()
                .participationVolunteers(participationList)
                .waitingVolunteers(waitingList)
                .build();
    }

}
