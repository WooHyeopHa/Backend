package com.whh.findmuseapi.user.service;

import com.whh.findmuseapi.common.Exception.CustomBadRequestException;
import com.whh.findmuseapi.user.dto.request.UserProfile;
import com.whh.findmuseapi.user.dto.request.UserProfileTasteRequest;
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
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TasteRepository tasteRepository;
    private final UserTasteRepository userTasteRepository;

    public void registerProfileInformation(User user, UserProfile.InformationRequest informationRequest) {
        UserMapper.INSTANCE.updateUserFromProfileInformation(informationRequest, user);
        user.authorizeUser();
        userRepository.save(user);
    }

    public void registerProfileLocation(User user, UserProfile.LocationRequest locationRequest) {
        UserMapper.INSTANCE.updateUserFromProfileLocation(locationRequest, user);
        userRepository.save(user);
    }

    public void registerProfileTaste(User user, UserProfileTasteRequest userProfileTasteRequest) {
        userProfileTasteRequest.tastes().stream()
                .forEach(tasteSelection -> {
                    Taste category = tasteRepository.findByName(tasteSelection.category())
                            .orElseThrow(() -> new CustomBadRequestException("카테고리를 찾을 수 없습니다."));

                    tasteSelection.selections().stream()
                            .map(selection -> tasteRepository.findByNameAndParent(selection, category)
                                    .orElseThrow(() -> new CustomBadRequestException("취향을 찾을 수 없습니다.")))
                            .map(taste -> {
                                UserTaste userTaste = UserTaste.builder()
                                        .user(user)
                                        .taste(taste)
                                        .build();
                                return userTaste;
                            })
                            .forEach(userTasteRepository::save);
                });
    }

    public void updateProfileTaste(User user, UserProfileTasteRequest userProfileTasteRequest) {
        userTasteRepository.deleteByUser(user);
        registerProfileTaste(user, userProfileTasteRequest);
    }
}
