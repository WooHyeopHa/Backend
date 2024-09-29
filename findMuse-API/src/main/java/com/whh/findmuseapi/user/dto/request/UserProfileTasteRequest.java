package com.whh.findmuseapi.user.dto.request;

import java.util.List;

public record UserProfileTasteRequest(List<TasteSelection> tastes) {

    public record TasteSelection(String category, List<String> selections) {
    }
}
