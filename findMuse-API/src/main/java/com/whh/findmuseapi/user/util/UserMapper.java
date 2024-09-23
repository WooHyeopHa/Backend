package com.whh.findmuseapi.user.util;

import com.whh.findmuseapi.common.Exception.CustomBadRequestException;
import com.whh.findmuseapi.common.constant.Infos;
import com.whh.findmuseapi.user.dto.request.UserProfile;
import com.whh.findmuseapi.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

@Mapper()
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "gender", source = "gender", qualifiedByName = "stringToGender")
    @Mapping(target = "birthYear", source = "birthYear", qualifiedByName = "stringToInteger")
    void updateUserFromProfileInformation(UserProfile.InformationRequest userProfileInformationRequest, @MappingTarget User user);

    void updateUserFromProfileLocation(UserProfile.LocationRequest userProfileLocationRequest, @MappingTarget User user);

    @Named("stringToGender")
    default Infos.Gender stringToGender(String gender) {
        return Infos.Gender.convertStringToGender(gender);
    }

    @Named("stringToInteger")
    default Integer stringToInteger(String integer) {
        if (integer == null || integer.isEmpty()) {
            throw new CustomBadRequestException("생년월일 값을 전달하지 않았습니다.");
        }
        if (!integer.matches("\\d+")) {
            throw new CustomBadRequestException("생년월일에 문자열이 포함되어 있습니다.");
        }
        Integer integerValue = Integer.valueOf(integer);
        if (integerValue > LocalDate.now().getYear() || integerValue < 1900) {
            throw new CustomBadRequestException("생년월일을 정확하게 입력해주세요.");
        }
        return integerValue;
    }
}
