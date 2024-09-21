package com.whh.findmuseapi.user.util;

import com.whh.findmuseapi.common.constant.Infos;
import com.whh.findmuseapi.user.dto.request.UserProfileInformationRequest;
import com.whh.findmuseapi.user.dto.request.UserProfileLocationRequest;
import com.whh.findmuseapi.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "gender", source = "gender", qualifiedByName = "stringToGender")
    void updateUserFromProfileInformation(UserProfileInformationRequest userProfileInformationRequest, @MappingTarget User user);

    void updateUserFromProfileLocation(UserProfileLocationRequest userProfileLocationRequest, @MappingTarget User user);

    @Named("stringToGender")
    default Infos.Gender stringToGender(String gender) {
        return Infos.Gender.convertStringToGender(gender);
    }
}
