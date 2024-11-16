package com.whh.findmuseapi.user.controller;

import com.whh.findmuseapi.art.dto.response.ArtHistoryResponse;
import com.whh.findmuseapi.art.dto.response.ArtListResponse;
import com.whh.findmuseapi.common.constant.ResponseCode;
import com.whh.findmuseapi.common.util.ApiResponse;
import com.whh.findmuseapi.review.dto.ReviewResponse;
import com.whh.findmuseapi.user.dto.response.MyInfo;
import com.whh.findmuseapi.user.dto.response.TasteGroupResponse;
import com.whh.findmuseapi.user.entity.User;
import com.whh.findmuseapi.user.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class UserInfoController {
    private final UserInfoService userInfoService;

    @Operation(summary = "마이페이지 : 메인 화면")
    @GetMapping("/main")
    public ApiResponse<MyInfo> getMyInfo(@AuthenticationPrincipal User user) {
        return ApiResponse.createSuccess(ResponseCode.SUCCESS, userInfoService.getMyInfo(user.getId()));
    }

    @Operation(summary = "마이페이지 : 보고싶어요 리스트 조회")
    @GetMapping("/art-likes")
    public ApiResponse<ArtListResponse> getMyArtLikeList(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String artTypeInfo
    ) {
        return ApiResponse.createSuccess(ResponseCode.SUCCESS, userInfoService.getMyArtLikeList(user, artTypeInfo));
    }

    @Operation(summary = "마이페이지 : 상대방 보고싶어요 리스트 조회")
    @GetMapping("/art-likes/{userId}")
    public ApiResponse<ArtListResponse> getMyArtLikeList(
            @PathVariable long userId,
            @RequestParam(required = false) String artTypeInfo
    ) {
        return ApiResponse.createSuccess(ResponseCode.SUCCESS, userInfoService.getUserLikeList(userId, artTypeInfo));
    }

    @Operation(summary = "마이페이지 : 관람했어요 리스트 조회")
    @GetMapping("/art-history")
    public ApiResponse<List<ArtHistoryResponse>> getMyArtHistory(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String artTypeInfo
    ) {
        return ApiResponse.createSuccess(ResponseCode.SUCCESS, userInfoService.getMyArtHistory(user, artTypeInfo));
    }

    @Operation(summary = "마이페이지 : 상대방 관람했어요 리스트 조회")
    @GetMapping("/art-history/{userId}")
    public ApiResponse<List<ArtHistoryResponse>> getUserArtHistory(
            @PathVariable long userId,
            @RequestParam(required = false) String artTypeInfo
    ) {
        return ApiResponse.createSuccess(ResponseCode.SUCCESS, userInfoService.getUserArtHistory(userId, artTypeInfo));
    }

    @Operation(summary = "마이페이지 : 행사 리뷰 조회")
    @GetMapping("/reviews")
    public ApiResponse<List<ReviewResponse>> getMyReview(
            @AuthenticationPrincipal User user,
            @RequestParam String creteria
    ) {
        return ApiResponse.createSuccess(ResponseCode.SUCCESS, userInfoService.getMyReview(user, creteria));
    }

    @Operation(summary = "마이페이지 : 상대방 행사 리뷰 조회")
    @GetMapping("/reviews/{userId}")
    public ApiResponse<List<ReviewResponse>> getUserReview(
            @PathVariable long userId,
            @RequestParam String creteria
    ) {
        return ApiResponse.createSuccess(ResponseCode.SUCCESS, userInfoService.getUserReview(userId, creteria));
    }

    @Operation(summary = "마이페이지 : 사용자 취향 조회")
    @GetMapping("/tastes/{userId}")
    public ApiResponse<List<TasteGroupResponse>> getUserTastes(@PathVariable long userId) {
        return ApiResponse.createSuccess(ResponseCode.SUCCESS, userInfoService.getUserTastes(userId));
    }

    @Operation(summary = "마이페이지 : 사용자 레벨 조회")
    @GetMapping("/level")
    public ApiResponse<MyInfo.UserLevelResponse> getMyUserLevel(@AuthenticationPrincipal User user) {
        return ApiResponse.createSuccess(ResponseCode.SUCCESS, userInfoService.getMyUserLevel(user));
    }
}
