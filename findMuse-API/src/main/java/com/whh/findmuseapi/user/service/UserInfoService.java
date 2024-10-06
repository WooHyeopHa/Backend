package com.whh.findmuseapi.user.service;

import com.whh.findmuseapi.art.entity.ArtHistory;
import com.whh.findmuseapi.art.entity.ArtLike;
import com.whh.findmuseapi.art.repository.ArtHistoryRepository;
import com.whh.findmuseapi.art.repository.ArtLikeRepository;
import com.whh.findmuseapi.art.repository.ArtRepository;
import com.whh.findmuseapi.user.dto.response.MyInfo;
import com.whh.findmuseapi.user.entity.User;
import com.whh.findmuseapi.user.repository.UserRepository;
import com.whh.findmuseapi.user.util.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserInfoService {

    private final ArtRepository artRepository;
    private final ArtLikeRepository artLikeRepository;
    private final ArtHistoryRepository artHistoryRepository;
    private final UserRepository userRepository;

    @Description("마이페이지 메인 화면")
    @Transactional(readOnly = true)
    public MyInfo getMyInfo(User user) {
        User userWithAssociations = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));

        List<ArtHistory> artHistories = artHistoryRepository.findByUserId(userWithAssociations.getId());
        List<ArtLike> artLikes = artLikeRepository.findByUserId(userWithAssociations.getId());

        return UserMapper.toMyInfo(userWithAssociations, artRepository.count(), artLikes, artHistories);
    }
}
