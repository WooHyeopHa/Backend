package com.whh.findmuseapi.art.openApi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ArtInfoResponse {

    @JsonProperty("db")
    private List<Id> idList;

    @NoArgsConstructor
    @Getter
    public static class Id {

        @JsonProperty("mt20id")
        private String artId;
    }
}
