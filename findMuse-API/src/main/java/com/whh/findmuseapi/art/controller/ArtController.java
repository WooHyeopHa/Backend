package com.whh.findmuseapi.art.controller;

import com.whh.findmuseapi.art.dto.response.ArtHomeResponse;
import com.whh.findmuseapi.art.dto.ArtLikeRequest;
import com.whh.findmuseapi.art.dto.response.ArtListResponse;
import com.whh.findmuseapi.art.dto.response.ArtOneResponse;
import com.whh.findmuseapi.art.service.ArtService;
import com.whh.findmuseapi.common.constant.ResponseCode;
import com.whh.findmuseapi.common.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class ArtController {

    private final ArtService artService;

    /**
     * 날짜, 장르, 정렬 필터 기준 문화예술 전체 불러오기
     */
    @GetMapping("/art/list/condition/{userId}")
    public ApiResponse<ArtListResponse> getArtByCondition(@PathVariable Long userId, @RequestParam String date, @RequestParam List<String> genre, @RequestParam String sort) {
        ArtListResponse response = artService.getArtByCondition(userId, date, genre, sort);
        return ApiResponse.createSuccess(ResponseCode.SUCCESS, response);
    }

    /**
     * 취향 장르별 랜덤 3개 불러오기 - 홈화면
     */
    @GetMapping("/art/home/{userId}")
    public ApiResponse<ArtHomeResponse> getArtByHome(@PathVariable Long userId) {
        ArtHomeResponse response = artService.getArtByHome(userId);
        return ApiResponse.createSuccess(ResponseCode.SUCCESS, response);
    }

    /**
     * 문화예술 개별 상세조회
     */
    @GetMapping("/art/one/{artId}/{userId}")
    public ApiResponse<ArtOneResponse> getArtOne(@PathVariable Long artId, @PathVariable Long userId) {
        ArtOneResponse response = artService.getArtInfoOne(artId, userId);
        return ApiResponse.createSuccess(ResponseCode.SUCCESS, response);
    }

    /**
     * 문화예술 좋아요
     */
    @PostMapping("art/like")
    public ApiResponse<?> markLike(@RequestBody ArtLikeRequest artLikeRequest) {
        artService.markLike(artLikeRequest);
        return ApiResponse.createSuccessWithNoContent(ResponseCode.SUCCESS);
    }
}

