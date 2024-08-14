package com.whh.findmuseapi.user.service;

import com.whh.findmuseapi.ios.dto.OAuth2Request;
import com.whh.findmuseapi.user.entity.User;
import com.whh.findmuseapi.user.repository.UserrRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserrRepository userrRepository;
    
    @Transactional(readOnly = true)
    public User findByAccountId(String accountId) {
        return userrRepository.findByAccountId(accountId).orElseThrow();
    }
    
    @Transactional
    public User saveIfNewUser(OAuth2Request oAuth2Request) {
        User existingUser = findByAccountId(oAuth2Request.getAccountId());
        if (existingUser != null) {
            return existingUser;
        }
        
        User newUser = new User();
        newUser.setAccountId(oAuth2Request.getAccountId());
        newUser.setName(oAuth2Request.getName());
        newUser.setEmail(oAuth2Request.getEmail());
        
        return userrRepository.save(newUser);
    }
}
