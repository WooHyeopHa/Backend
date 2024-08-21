package com.whh.findmuseapi.post.service.impl;

import com.whh.findmuseapi.art.entity.Art;
import com.whh.findmuseapi.art.repository.ArtRepository;
import com.whh.findmuseapi.common.Exception.NotFoundException;
import com.whh.findmuseapi.common.constant.Infos;
import com.whh.findmuseapi.post.dto.request.PostCreateRequest;
import com.whh.findmuseapi.post.entity.Post;
import com.whh.findmuseapi.post.entity.PostTag;
import com.whh.findmuseapi.post.entity.Tag;
import com.whh.findmuseapi.post.repository.TagRepository;
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
    private final UserRepository userRepository;
    private final ArtRepository artRepository;
    private final TagRepository tagRepository;


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
}
