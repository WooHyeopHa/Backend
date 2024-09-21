package com.whh.findmuseapi.user.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class UserProfileTasteRequest {
    private List<TasteSelection> tastes;

    @Getter
    @Setter
    public static class TasteSelection {
        private String category;
        private List<String> selections;
    }
}
