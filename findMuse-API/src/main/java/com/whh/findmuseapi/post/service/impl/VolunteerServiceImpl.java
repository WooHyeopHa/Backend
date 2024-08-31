package com.whh.findmuseapi.post.service.impl;

import com.whh.findmuseapi.common.constant.Infos;
import com.whh.findmuseapi.post.dto.response.VolunteerListResponse;
import com.whh.findmuseapi.post.entity.Post;
import com.whh.findmuseapi.post.repository.VolunteerRepository;
import com.whh.findmuseapi.post.service.VolunteerService;
import com.whh.findmuseapi.user.entity.User;
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

    }
}
