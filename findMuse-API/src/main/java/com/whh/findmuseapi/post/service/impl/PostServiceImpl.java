package com.whh.findmuseapi.post.service.impl;

import com.whh.findmuseapi.art.entity.Art;
import com.whh.findmuseapi.art.repository.ArtRepository;
import com.whh.findmuseapi.common.Exception.NotFoundException;
import com.whh.findmuseapi.common.Exception.UnAuthorizationException;
import com.whh.findmuseapi.common.constant.Infos;
import com.whh.findmuseapi.post.dto.request.PostCreateRequest;
import com.whh.findmuseapi.post.dto.response.PostReadResponse;
import com.whh.findmuseapi.post.entity.Post;
import com.whh.findmuseapi.post.entity.PostTag;
import com.whh.findmuseapi.post.entity.Tag;
import com.whh.findmuseapi.post.entity.Volunteer;
import com.whh.findmuseapi.post.repository.PostRepository;
import com.whh.findmuseapi.post.repository.PostTagRepository;
import com.whh.findmuseapi.post.repository.TagRepository;
import com.whh.findmuseapi.post.repository.VolunteerRepository;
import com.whh.findmuseapi.post.service.PostService;
import com.whh.findmuseapi.user.entity.User;
import com.whh.findmuseapi.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * class: PostServiceImpl.
 *
 * @author devminseo
 * @version 8/20/24
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ArtRepository artRepository;
    private final TagRepository tagRepository;
    private final VolunteerRepository volunteerRepository;
    private final PostTagRepository postTagRepository;


    /**
     * 1. 회원 유효성 검증
     * 2. 전시회 유효성 검증
     * 3. 태그 유효성 검증
     *
     * @param createRequest 게시물 생성 정보
     */
    @Override
    @Transactional
    public void createPost(PostCreateRequest createRequest) {
        User user = userRepository.findById(createRequest.getUserId())
                .orElseThrow(() -> new NotFoundException("회원: " + createRequest.getUserId()));
        Art art = artRepository.findArtByTitle(createRequest.getArtTitle())
                .orElseThrow(() -> new NotFoundException("전시회: " + createRequest.getArtTitle()));

        List<Tag> tagList = createRequest.getTagList().stream()
                .map(tagName -> tagRepository.findByName(tagName)
                        .orElseThrow(() -> new NotFoundException("태그: " + tagName)))
                .toList();

        Post post = Post.builder()
                .title(createRequest.getTitle())
                .content(createRequest.getContent())
                .place(createRequest.getPlace())
                .endDate(createRequest.getEndDate())
                .inviteCount(createRequest.getInviteCount())
                .ages(Infos.Ages.valueOf(createRequest.getAges()))
                .art(art)
                .user(user)
                .build();

        List<PostTag> postTagList = tagList.stream().map(tag -> PostTag.builder().post(post).tag(tag).build()).toList();

        post.updateTagList(postTagList);
    }

    @Override
    @Transactional
    public PostReadResponse readPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException("모집글: " + postId));
        post.updateCount();

        return PostReadResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .place(post.getPlace())
                .createDate(post.getCreateDate())
                .endDate(post.getEndDate())
                .inviteCount(post.getInviteCount())
                .viewCount(post.getViewCount())
                .ages(post.getAges().name())
                .artName(post.getArt().getTitle())
                .userId(post.getUser().getId())
                .volunteeredList(post.getVolunteeredList().stream()
                        .map(volunteer -> volunteer.getUser().getId())
                        .toList())
                .tagList(post.getTagList().stream()
                        .map(tag -> tag.getTag().getName())
                        .toList())
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deletePost(Long userId, Long postId) {
        User writer = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("회원: " + userId));
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException("게시글: " + postId));

        if (!post.getUser().getId().equals(writer.getId())) {
            // 예외들 일단 임시방편
            throw new UnAuthorizationException("게시글");
        }

        List<Volunteer> volunteers = post.getVolunteeredList();
        volunteerRepository.deleteAll(volunteers);

        List<PostTag> tags = post.getTagList();
        postTagRepository.deleteAll(tags);

        postRepository.delete(post);
    }

}
