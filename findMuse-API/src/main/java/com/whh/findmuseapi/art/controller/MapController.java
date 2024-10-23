package com.whh.findmuseapi.art.controller;

import com.whh.findmuseapi.art.dto.MapResponse;
import com.whh.findmuseapi.art.service.ArtService;
import com.whh.findmuseapi.common.constant.ResponseCode;
import com.whh.findmuseapi.common.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MapController {

    private final ArtService artService;

    @GetMapping("/map/info")
    public ApiResponse<MapResponse> getMapInfo() {
        MapResponse mapResponse = artService.getMapInfo();
        return ApiResponse.createSuccess(ResponseCode.SUCCESS, mapResponse);
    }

}
