package com.whh.findmuseapi.user.service;

import com.whh.findmuseapi.common.Exception.CustomBadRequestException;
import com.whh.findmuseapi.user.dto.request.UserProfileInformationRequest;
import com.whh.findmuseapi.user.dto.request.UserProfileLocationRequest;
import com.whh.findmuseapi.user.dto.request.UserProfileTasteRequest;
import com.whh.findmuseapi.user.entity.Taste;
import com.whh.findmuseapi.user.entity.User;
import com.whh.findmuseapi.user.entity.UserTaste;
import com.whh.findmuseapi.user.repository.TasteRepository;
import com.whh.findmuseapi.user.repository.UserRepository;
import com.whh.findmuseapi.user.repository.UserTasteRepository;
import com.whh.findmuseapi.user.util.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TasteRepository tasteRepository;
    private final UserTasteRepository userTasteRepository;

    public void registerProfileInformation(User user, UserProfileInformationRequest userProfileInformationRequest) {
        UserMapper.INSTANCE.updateUserFromProfileInformation(userProfileInformationRequest, user);
        userRepository.save(user);
    }

    public void registerProfileLocation(User user, UserProfileLocationRequest userProfileLocationRequest) {
        UserMapper.INSTANCE.updateUserFromProfileLocation(userProfileLocationRequest, user);
        userRepository.save(user);
    }

    public void registerProfileTaste(User user, UserProfileTasteRequest userProfileTasteRequest) {
        userProfileTasteRequest.getTastes().stream()
                .forEach(tasteSelection -> {
                    Taste category = tasteRepository.findByName(tasteSelection.getCategory())
                            .orElseThrow(() -> new CustomBadRequestException("카테고리를 찾을 수 없습니다."));

                    tasteSelection.getSelections().stream()
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
