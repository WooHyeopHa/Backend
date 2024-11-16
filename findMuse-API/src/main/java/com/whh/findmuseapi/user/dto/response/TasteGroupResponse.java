package com.whh.findmuseapi.user.dto.response;

import java.util.List;

public record TasteGroupResponse(
        Long parentId,
        String parentName,
        List<TasteResponse> tastes) {

    public record TasteResponse(Long id, String name) {}
}

