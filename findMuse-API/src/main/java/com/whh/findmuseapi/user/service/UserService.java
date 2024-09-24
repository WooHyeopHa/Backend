package com.whh.findmuseapi.user.service;

import com.whh.findmuseapi.common.Exception.CustomBadRequestException;
import com.whh.findmuseapi.user.dto.request.UserProfile;
import com.whh.findmuseapi.user.dto.request.UserProfileTasteRequest;
import com.whh.findmuseapi.user.dto.response.NicknameDuplicationResponse;
import com.whh.findmuseapi.user.entity.Taste;
import com.whh.findmuseapi.user.entity.User;
import com.whh.findmuseapi.user.entity.UserTaste;
import com.whh.findmuseapi.user.repository.TasteRepository;
import com.whh.findmuseapi.user.repository.UserRepository;
import com.whh.findmuseapi.user.repository.UserTasteRepository;
import com.whh.findmuseapi.user.util.UserMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TasteRepository tasteRepository;
    private final UserTasteRepository userTasteRepository;

    @Description("사용자 닉네임 설정")
    public void registerProfileNickname(User user, UserProfile.NicknameRequest nicknameRequest) {
        if (userRepository.existsByNickname(nicknameRequest.nickname())) throw new CustomBadRequestException("존재하는 닉네임입니다.");
        user.setNickname(nicknameRequest.nickname());
    }

    @Description("사용자 닉네임 중복 조회")
    public NicknameDuplicationResponse checkNicknameDuplication(User user, UserProfile.NicknameRequest nicknameRequest) {
        return NicknameDuplicationResponse.builder()
                .isDuplicated(userRepository.existsByNickname(nicknameRequest.nickname()))
                .build();
    }

    @Description("사용자 정보 설정")
    public void registerProfileInformation(User user, UserProfile.InformationRequest informationRequest) {
        UserMapper.INSTANCE.updateUserFromProfileInformation(informationRequest, user);
        user.authorizeUser();
    }

    @Description("사용자 위치 설정")
    public void registerProfileLocation(User user, UserProfile.LocationRequest locationRequest) {
        UserMapper.INSTANCE.updateUserFromProfileLocation(locationRequest, user);
    }

    @Description("사용자 취향 설정")
    public void registerProfileTaste(User user, UserProfileTasteRequest userProfileTasteRequest) {
        userProfileTasteRequest.tastes().stream()
                .flatMap(tasteSelection -> {
                    Taste category = tasteRepository.findByName(tasteSelection.category())
                            .orElseThrow(() -> new CustomBadRequestException("카테고리를 찾을 수 없습니다."));

                    return tasteSelection.selections().stream()
                            .map(selection -> tasteRepository.findByNameAndParent(selection, category)
                                    .orElseThrow(() -> new CustomBadRequestException("취향을 찾을 수 없습니다.")))
                            .map(taste -> UserTaste.builder()
                                    .user(user)
                                    .taste(taste)
                                    .build());
                })
                .forEach(userTaste -> user.getUserTastes().add(userTaste));
    }


    @Description("사용자 취향 수정")
    public void updateProfileTaste(User user, UserProfileTasteRequest userProfileTasteRequest) {
        user.getUserTastes().clear(); // List<UserTaste> 필드를 비워줌으로써 고아 객체 자동 삭제.
        registerProfileTaste(user, userProfileTasteRequest);
    }
}
