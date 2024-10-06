package com.whh.findmuseapi.user.dto.request;

import java.util.List;

public record UserProfile() {

    public record NicknameRequest(String nickname) {}

    public record LocationRequest(String location) {}

    public record InformationRequest(
            String birthYear,
            String gender) {}

    public record TasteRequest(List<TasteRequest.TasteSelection> tastes) {
        public record TasteSelection(
                String category,
                 List<String> selections
        ) {}
    }

    public record ChangeRequest(
            NicknameRequest nicknameRequest,
            LocationRequest locationRequest,
            InformationRequest informationRequest,
            String comment,
            boolean showStatus
    ) {}
}
