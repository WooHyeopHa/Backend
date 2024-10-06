package com.whh.findmuseapi.post.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * class: VolunteerMyPageListResponse.
 *
 * @author devminseo
 * @version 8/31/24
 */
@Getter
@Builder
public class VolunteerMyPageListResponse {
    private List<VolunteerReadResponse> approvalVolunteers;
    private List<VolunteerReadResponse> waitingVolunteers;
    private List<VolunteerReadResponse> refusalVolunteers;

    public static VolunteerMyPageListResponse toDto(List<VolunteerReadResponse> approvalVolunteers,
                                                    List<VolunteerReadResponse> waitingList,
                                                    List<VolunteerReadResponse> refusalVolunteers) {

        return VolunteerMyPageListResponse.builder()
                .approvalVolunteers(approvalVolunteers)
                .waitingVolunteers(waitingList)
                .refusalVolunteers(refusalVolunteers)
                .build();
    }
}
