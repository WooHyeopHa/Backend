package com.whh.findmuseapi.user.service;

import com.whh.findmuseapi.common.constant.Infos;
import com.whh.findmuseapi.common.exception.CBadRequestException;
import com.whh.findmuseapi.common.util.S3Uploader;
import com.whh.findmuseapi.user.dto.request.UserProfile;
import com.whh.findmuseapi.user.dto.response.NicknameDuplicationResponse;
import com.whh.findmuseapi.user.entity.Taste;
import com.whh.findmuseapi.user.entity.User;
import com.whh.findmuseapi.user.entity.UserTaste;
import com.whh.findmuseapi.user.repository.TasteRepository;
import com.whh.findmuseapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TasteRepository tasteRepository;

    private final S3Uploader s3Uploader;

    @Description("사용자 닉네임 설정")
    @Transactional
    public void registerProfileNickname(User user, UserProfile.NicknameRequest nicknameRequest) {
        if (nicknameRequest.nickname().isEmpty() || nicknameRequest.nickname() == null) throw new CBadRequestException("닉네임을 입력해주세요.");
        if (userRepository.existsByNickname(nicknameRequest.nickname())) throw new CBadRequestException("존재하는 닉네임입니다.");

        user.updateNickname(nicknameRequest.nickname());
        user.authorizeUser();
    }

    @Description("사용자 닉네임 중복 조회")
    @Transactional(readOnly = true)
    public NicknameDuplicationResponse checkNicknameDuplication(String nickname) {
        if (nickname == null || nickname.isEmpty()) throw new CBadRequestException("닉네임은 비어있을 수 없습니다.");
        return NicknameDuplicationResponse.builder()
                .isDuplicated(userRepository.existsByNickname(nickname))
                .build();
    }

    @Description("사용자 정보 설정")
    @Transactional
    public void registerProfileInformation(User user, UserProfile.InformationRequest informationRequest, MultipartFile profileImage) throws IOException {
        user.updateInformation(
                validateBirthYear(informationRequest.birthYear()),
                Infos.Gender.convertStringToGender(informationRequest.gender())
        );


        if (profileImage != null) {
            Optional.ofNullable(user.getProfileImageUrl())
                    .ifPresent(s3Uploader::deleteFile);

            String newImageUrl = s3Uploader.upload(
                    profileImage,
                    Infos.ResourceUrl.PROFILE_IMAGE.getUrl() + user.getEmail()
            );
            user.updateProfileImageUrl(newImageUrl);
        }

        userRepository.save(user);
        log.info("사용자 정보 설정 완료 : {}", user.getId());
    }

    @Description("사용자 위치 설정")
    @Transactional
    public void registerProfileLocation(User user, UserProfile.LocationRequest locationRequest) {
        user.updateLocation(locationRequest.location());
        userRepository.save(user);
    }

    @Description("사용자 취향 설정")
    @Transactional
    public void registerProfileTaste(Long userId, UserProfile.TasteRequest userProfileTasteRequest) {
        User user = userRepository.findUserWithTastesById(userId)
                .orElseThrow(() -> new CBadRequestException("사용자를 찾을 수 없습니다."));

        userProfileTasteRequest.tastes().stream()
                .flatMap(tasteSelection -> {
                    Taste category = tasteRepository.findByName(tasteSelection.category())
                            .orElseThrow(() -> new CBadRequestException("카테고리를 찾을 수 없습니다."));

                    return tasteSelection.selections().stream()
                            .map(selection -> tasteRepository.findByNameAndParent(selection, category)
                                    .orElseThrow(() -> new CBadRequestException("취향을 찾을 수 없습니다.")))
                            .map(taste -> UserTaste.builder()
                                    .user(user)
                                    .taste(taste)
                                    .build());
                })
                .forEach(userTaste -> user.getUserTastes().add(userTaste));

        // 사용자 취향의 이름을 출력
        user.getUserTastes().stream()
                .map(userTaste -> userTaste.getTaste().getName())
                .collect(Collectors.toList())
                .forEach(log::info);
    }

    @Description("사용자 취향 수정")
    @Transactional
    public void updateProfileTaste(Long userId, UserProfile.TasteRequest userProfileTasteRequest) {
        User user = userRepository.findUserWithTastesById(userId)
                        .orElseThrow(() -> new CBadRequestException("사용자를 찾을 수 없습니다."));
        user.getUserTastes().clear();

        registerProfileTaste(userId, userProfileTasteRequest);
    }

    @Description("프로필 편집")
    @Transactional
    public void updateProfile(User user, UserProfile.ChangeRequest changeRequest, MultipartFile profileImage) throws IOException {
        registerProfileNickname(user, changeRequest.nicknameRequest());
        registerProfileLocation(user, changeRequest.locationRequest());
        registerProfileInformation(user, changeRequest.informationRequest(), profileImage);
        user.updateProfile(changeRequest.comment(), changeRequest.showStatus());
        userRepository.save(user);

        log.info("사용자 닉네임 : {}, 위치 : {}, 생년 : {}, 성별 : {}, 한줄소개 : {}, 활동내역 공개 여부 : {}, 프로필 이미지 Url : {}",
                user.getNickname(), user.getLocation(), user.getBirthYear(), user.getGender().getInfo(), user.getComment(), user.isShowStatus(), user.getProfileImageUrl());
    }

    private Integer validateBirthYear(String integer) {
        if (integer == null || integer.isEmpty()) {
            throw new CBadRequestException("생년월일 값을 전달하지 않았습니다.");
        }
        if (!integer.matches("\\d+")) {
            throw new CBadRequestException("생년월일에 문자열이 포함되어 있습니다.");
        }
        Integer integerValue = Integer.valueOf(integer);
        if (integerValue > LocalDate.now().getYear() || integerValue < 1900) {
            throw new CBadRequestException("생년월일을 정확하게 입력해주세요.");
        }
        return integerValue;
    }
}
