package com.whh.findmuseapi.post.service.impl;

import com.whh.findmuseapi.common.exception.AlreadyExistException;
import com.whh.findmuseapi.common.exception.NotFoundException;
import com.whh.findmuseapi.common.exception.UnAuthorizationException;
import com.whh.findmuseapi.common.constant.Infos;
import com.whh.findmuseapi.post.dto.response.VolunteerMyPageListResponse;
import com.whh.findmuseapi.post.dto.response.VolunteerPostListResponse;
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
    public boolean volunteerCheck(User user, Post post) {
        return volunteerRepository.existsByUserAndPost(user, post);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VolunteerPostListResponse getPostVolunteerList(Long postId, Long userId) {
        User writer = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("회원: " + userId));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("게시글: " + postId));

        checkWriter(writer, post);

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

        return VolunteerPostListResponse.toDto(participationList, waitingList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VolunteerMyPageListResponse getMyPageVolunteerList(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("회원: " + userId);
        }

        List<Volunteer> approvalVolunteers = volunteerRepository.findByUserIdAndStatusAndActiveStatusTrue(userId,
                Infos.InvieteStatus.Wait);
        List<Volunteer> waitingVolunteers = volunteerRepository.findByUserIdAndStatusAndActiveStatusTrue(userId,
                Infos.InvieteStatus.ACCESS);
        List<Volunteer> refusalVolunteers = volunteerRepository.findByUserIdAndStatusAndActiveStatusTrue(userId,
                Infos.InvieteStatus.DENY);

        List<VolunteerReadResponse> approvalList = approvalVolunteers.stream()
                .map(VolunteerReadResponse::toDto)
                .collect(Collectors.toList());
        List<VolunteerReadResponse> waitingList = waitingVolunteers.stream()
                .map(VolunteerReadResponse::toDto)
                .collect(Collectors.toList());

        List<VolunteerReadResponse> refusalList = refusalVolunteers.stream()
                .map(VolunteerReadResponse::toDto)
                .collect(Collectors.toList());

        return VolunteerMyPageListResponse.toDto(approvalList, waitingList, refusalList);
    }


    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void acceptVolunteer(Long postId, Long writer, Long targetId) {
        User user = userRepository.findById(writer)
                .orElseThrow(() -> new NotFoundException("회원: " + writer));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("게시글: " + postId));

        checkWriter(user, post);

        Volunteer targetUser = volunteerRepository.findById(targetId).orElseThrow(()-> new NotFoundException(
                "지원자: " + targetId));

        targetUser.updateStatus(Infos.InvieteStatus.ACCESS);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void refusalVolunteer(Long postId, Long writer, Long targetId) {
        User user = userRepository.findById(writer)
                .orElseThrow(() -> new NotFoundException("회원: " + writer));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("게시글: " + postId));

        checkWriter(user, post);

        Volunteer targetUser = volunteerRepository.findById(targetId).orElseThrow(()-> new NotFoundException(
                "지원자: " + targetId));

        targetUser.updateStatus(Infos.InvieteStatus.DENY);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void applyVolunteer(Long postId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("회원: " + userId));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("게시글: " + postId));

        if (volunteerCheck(user, post)) {
            throw new AlreadyExistException("지원자: " + userId);
        }

        Volunteer volunteer = Volunteer.toEntity(post, user);

        volunteerRepository.save(volunteer);

    }

    /**
     * {@inheritDoc}
     * 지원자 유효성 검증.
     */
    @Transactional
    @Override
    public void cancelVolunteer(Long volunteerId, Long userId) {
        Volunteer volunteer = volunteerRepository.findById(volunteerId).orElseThrow(()-> new NotFoundException(
                "지원자: " + volunteerId));

        if (!volunteer.getUser().getId().equals(userId)) {
            throw new UnAuthorizationException("지원자");
        }

        volunteer.updateActiveStatus();
    }

    public void checkWriter(User user, Post post) {
        if (!post.getUser().getId().equals(user.getId())) {
            throw new UnAuthorizationException("게시글");
        }
    }
}
