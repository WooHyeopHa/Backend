package com.whh.findmuseapi.art.dto.response;

import com.whh.findmuseapi.art.entity.Art;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ArtRandomResponse {

    private String genre;
    private Long artId;
    private String poster;

    public static List<ArtRandomResponse> toDto(List<Art> artList) {
        return artList.stream()
                .map(a -> ArtRandomResponse.builder()
                            .artId(a.getId())
                            .genre(a.getArtType().getInfo())
                            .poster(a.getPoster()).build()).toList();
    }
}
