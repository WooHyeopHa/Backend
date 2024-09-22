//package com.whh.findmuseapi.post.controller;
//
//import com.whh.findmuseapi.common.constant.ResponseCode;
//import com.whh.findmuseapi.common.util.ApiResponse;
//import com.whh.findmuseapi.post.dto.request.PostCreateRequest;
//import com.whh.findmuseapi.post.dto.request.PostUpdateRequest;
//import com.whh.findmuseapi.post.dto.response.PostListResponse;
//import com.whh.findmuseapi.post.dto.response.PostOneReadResponse;
//import com.whh.findmuseapi.post.service.PostService;
//import io.swagger.v3.oas.annotations.parameters.RequestBody;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * class: PostController.
// * 게시판 CRUD 컨트롤러 입니다.
// *
// * @author devminseo
// * @version 8/20/24
// */
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/post")
//public class PostController {
//
//    private final PostService postService;
//
//    /**
//     * 모집글 생성 엔드포인트
//     *
//     * @param createRequest 모집글 생성 요청 정보
//     * @return 생성된 모집글 정보
//     */
//    @PostMapping
//    public ApiResponse<?> createPost(@Valid @RequestBody PostCreateRequest createRequest) {
//        postService.createPost(createRequest);
//        return ApiResponse.createSuccessWithNoContent(ResponseCode.RESOURCE_CREATED);
//    }
//
//    /**
//     * 모집글 단일 조회 엔드포인트
//     *
//     * @param postId 모집글 ID
//     * @param userId 사용자 ID
//     * @return 모집글 정보
//     */
//    @GetMapping("/{postId}")
//    public ApiResponse<?>  readPost(
//            @PathVariable Long postId,
//            @RequestParam Long userId) {
//        PostOneReadResponse postResponse = postService.readPost(postId, userId);
//        return ApiResponse.createSuccess(ResponseCode.SUCCESS, postResponse);
//    }
//
//    /**
//     * 모집글 수정 엔드포인트
//     *
//     * @param updateRequest 모집글 수정 요청 정보
//     * @return 수정된 모집글 정보
//     */
//    @PutMapping
//    public ApiResponse<?>  updatePost(@Valid @RequestBody PostUpdateRequest updateRequest) {
//        postService.updatePost(updateRequest);
//        return ApiResponse.createSuccessWithNoContent(ResponseCode.SUCCESS);
//    }
//
//    /**
//     * 모집글 삭제 엔드포인트
//     *
//     * @param postId 모집글 ID
//     * @param userId 사용자 ID
//     * @return 삭제 결과
//     */
//    @DeleteMapping("/{postId}")
//    public ApiResponse<?>  deletePost(
//            @PathVariable Long postId,
//            @RequestParam Long userId) {
//        postService.deletePost(userId, postId);
//        return ApiResponse.createSuccessWithNoContent(ResponseCode.SUCCESS);
//    }
//
//    /**
//     * 모집글 목록 조회 엔드포인트
//     *
//     * @return 모집글 목록
//     */
//    @GetMapping("/list")
//    public ApiResponse<?>  getPostList() {
//        PostListResponse postList = postService.getPostList();
//        return ApiResponse.createSuccess(ResponseCode.SUCCESS, postList);
//    }
//
//}
