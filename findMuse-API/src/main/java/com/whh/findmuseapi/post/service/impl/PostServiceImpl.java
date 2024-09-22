//package com.whh.findmuseapi.post.service.impl;
//
//import com.whh.findmuseapi.common.constant.Infos;
//import com.whh.findmuseapi.art.entity.Art;
//import com.whh.findmuseapi.art.repository.ArtRepository;
//import com.whh.findmuseapi.common.exception.NotFoundException;
//import com.whh.findmuseapi.common.exception.UnAuthorizationException;
//import com.whh.findmuseapi.post.dto.request.PostCreateRequest;
//import com.whh.findmuseapi.post.dto.request.PostUpdateRequest;
//import com.whh.findmuseapi.post.dto.response.PostListReadResponse;
//import com.whh.findmuseapi.post.dto.response.PostListResponse;
//import com.whh.findmuseapi.post.dto.response.PostOneReadResponse;
//import com.whh.findmuseapi.post.entity.Post;
//import com.whh.findmuseapi.post.entity.PostTag;
//import com.whh.findmuseapi.post.entity.Tag;
//import com.whh.findmuseapi.post.entity.Volunteer;
//import com.whh.findmuseapi.post.repository.PostRepository;
//import com.whh.findmuseapi.post.repository.PostTagRepository;
//import com.whh.findmuseapi.post.repository.TagRepository;
//import com.whh.findmuseapi.post.repository.VolunteerRepository;
//import com.whh.findmuseapi.post.service.PostService;
//import com.whh.findmuseapi.post.service.VolunteerService;
//import com.whh.findmuseapi.user.entity.User;
//import com.whh.findmuseapi.user.repository.UserRepository;
//import java.util.List;
//import java.util.stream.Collectors;
//import lombok.RequiredArgsConstructor;
//import org.hibernate.validator.internal.util.stereotypes.Lazy;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
///**
// * class: PostServiceImpl.
// *
// * @author devminseo
// * @version 8/20/24
// */
//@Service
//@Transactional(readOnly = true)
//@RequiredArgsConstructor
//public class PostServiceImpl implements PostService {
//    private final PostRepository postRepository;
//    private final UserRepository userRepository;
//    private final ArtRepository artRepository;
//    private final TagRepository tagRepository;
//    private final VolunteerRepository volunteerRepository;
//    @Lazy
//    private final VolunteerService volunteerService;
//    private final PostTagRepository postTagRepository;
//
//
//    /**
//     * 1. 회원 유효성 검증
//     * 2. 전시회 유효성 검증
//     * 3. 태그 유효성 검증
//     *
//     * @param createRequest 게시물 생성 정보
//     */
//    @Override
//    @Transactional
//    public void createPost(PostCreateRequest createRequest) {
//        User user = userRepository.findById(createRequest.getUserId())
//                .orElseThrow(() -> new NotFoundException("회원: " + createRequest.getUserId()));
//        Art art = artRepository.findById(createRequest.getArtId())
//                .orElseThrow(() -> new NotFoundException("전시회: " + createRequest.getArtId()));
//
//        List<Tag> tagList = createRequest.getTagList().stream()
//                .map(tagName -> tagRepository.findByName(tagName)
//                        .orElseThrow(() -> new NotFoundException("태그: " + tagName)))
//                .toList();
//
//        Post post = Post.toEntity(createRequest, art, user);
//
//        List<PostTag> postTagList = tagList.stream().map(tag -> PostTag.builder().post(post).tag(tag).build()).toList();
//
//        post.getTagList().addAll(postTagList);
//
//        postRepository.save(post);
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    @Transactional(timeout = 5)
//    public PostOneReadResponse readPost(Long postId, Long userId) {
//        Post post = postRepository.findWithPessimisticLockById(postId).orElseThrow(() -> new NotFoundException("모집글: " + postId));
//        post.viewCountPlusOne();
//
//        boolean isWriter = userId.equals(post.getUser().getId());
//
//        //TODO
//        // 모집글 조회시 내가 신청 했는지 안했는지 정보는 모집 신청 버튼 클릭시 정보 전달 (isWriter를 통해 내가 작성한 글 판단) <- 따로 API
//        // 모집글 조회시 필요한 유저의 정보 전달
//
//        int invitedCount = Math.toIntExact(volunteerService.getInvitedCount(post));
//
//        return PostOneReadResponse.toDto(post, invitedCount, isWriter);
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    @Transactional
//    public void updatePost(PostUpdateRequest updateRequest) {
//        User writer = userRepository.findById(updateRequest.getUserId())
//                .orElseThrow(() -> new NotFoundException("회원: " + updateRequest.getUserId()));
//        Post post = postRepository.findById(updateRequest.getPostId())
//                .orElseThrow(() -> new NotFoundException("게시글: " + updateRequest.getPostId()));
//
//        checkWriter(writer, post);
//
//        Art art = artRepository.findById(updateRequest.getArtId())
//                .orElseThrow(() -> new NotFoundException("전시회: " + updateRequest.getArtId()));
//
//        List<Tag> tagList = updateRequest.getTagList().stream()
//                .map(tagName -> tagRepository.findByName(tagName)
//                        .orElseThrow(() -> new NotFoundException("태그: " + tagName)))
//                .toList();
//
//        // 나중에 개선사항(아직은 태그의 수가 많지 않아서 부하가 딱히 없을 것으로 예상)
//        postTagRepository.deleteAllByPost(post);
//
//        List<PostTag> postTagList = tagList.stream().map(tag -> PostTag.builder().post(post).tag(tag).build()).toList();
//
//        post.updatePost(updateRequest, art, postTagList);
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    @Transactional
//    public void deletePost(Long userId, Long postId) {
//        User writer = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("회원: " + userId));
//        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException("게시글: " + postId));
//
//        checkWriter(writer, post);
//
//        List<Volunteer> volunteers = post.getVolunteeredList();
//        volunteerRepository.deleteAll(volunteers);
//
//        List<PostTag> tags = post.getTagList();
//        postTagRepository.deleteAll(tags);
//
//        postRepository.delete(post);
//    }
//
//    @Transactional
//    @Override
//    public void checkWriter(User user, Post post) {
//        if (!post.getUser().getId().equals(user.getId())) {
////            throw new UnAuthorizationException("게시글");
//        }
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public PostListResponse getPostList() {
//        List<Post> list = postRepository.findAllByOrderByCreateDateDesc();
//
//        List<PostListReadResponse> postList = list.stream()
//                .map(PostListReadResponse::toDto)
//                .collect(Collectors.toList());
//
//        return PostListResponse.toDto(postList);
//
//    }
//
//}
