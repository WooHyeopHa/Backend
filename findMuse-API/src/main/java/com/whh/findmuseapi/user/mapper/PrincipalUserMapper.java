package com.whh.findmuseapi.user.mapper;

import com.whh.findmuseapi.user.entity.PrincipalUser;
import com.whh.findmuseapi.user.entity.User;
import java.util.Collection;
import java.util.Collections;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Mapper
public interface PrincipalUserMapper {
    PrincipalUserMapper INSTANCE = Mappers.getMapper(PrincipalUserMapper.class);
    
    @Mappings({
        @Mapping(target = "email", source = "email"),
        @Mapping(target = "password", ignore = true),
        @Mapping(target = "authorities", expression = "java(getDefaultAuthorities())"),
    })
    PrincipalUser toPrincipalUser(User member);
    
    @Named("getDefaultAuthorities")
    default Collection<SimpleGrantedAuthority> getDefaultAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }
}
