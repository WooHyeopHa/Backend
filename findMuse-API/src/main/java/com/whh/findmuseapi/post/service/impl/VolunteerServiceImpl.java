package com.whh.findmuseapi.post.service.impl;

import com.whh.findmuseapi.common.Exception.NotFoundException;
import com.whh.findmuseapi.common.Exception.UnAuthorizationException;
import com.whh.findmuseapi.common.constant.Infos;
import com.whh.findmuseapi.post.dto.response.VolunteerListResponse;
import com.whh.findmuseapi.post.dto.response.VolunteerReadResponse;
import com.whh.findmuseapi.post.entity.Post;
import com.whh.findmuseapi.post.entity.Volunteer;
import com.whh.findmuseapi.post.repository.PostRepository;
import com.whh.findmuseapi.post.repository.VolunteerRepository;
import com.whh.findmuseapi.post.service.VolunteerService;
import com.whh.findmuseapi.user.entity.User;
import com.whh.findmuseapi.user.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * class: VolunteerServiceImpl.
 *
 * @author devminseo
 * @version 8/24/24
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VolunteerServiceImpl implements VolunteerService {
    private final VolunteerRepository volunteerRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getInvitedCount(Post post) {
        return volunteerRepository.countByPostIdAndStatusAndActiveStatus(post.getId(), Infos.InvieteStatus.ACCESS,
                false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean volunteerCheck(User user, Post post) {
        return volunteerRepository.existsByUserAndPost(user, post);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VolunteerListResponse getVolunteerList(Long postId,Long userId) {
        User writer = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("회원: " + userId));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("게시글: " + postId));

        if (!post.getUser().getId().equals(writer.getId())) {
            throw new UnAuthorizationException("게시글");
        }
        List<Volunteer> waitingVolunteers = volunteerRepository.findByPostIdAndActiveStatusTrueAndStatus(postId,
                Infos.InvieteStatus.Wait);

        List<Volunteer> participationVolunteers = volunteerRepository.findByPostIdAndActiveStatusTrueAndStatusNot(postId,
                Infos.InvieteStatus.Wait);

        List<VolunteerReadResponse> waitingList = waitingVolunteers.stream()
                .map(VolunteerReadResponse::toDto)
                .collect(Collectors.toList());

        List<VolunteerReadResponse> participationList = participationVolunteers.stream()
                .map(VolunteerReadResponse::toDto)
                .collect(Collectors.toList());

        return VolunteerListResponse.toDto(participationList, waitingList);
    }
}
