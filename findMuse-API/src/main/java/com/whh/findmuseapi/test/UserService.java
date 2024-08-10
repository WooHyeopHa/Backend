package com.whh.findmuseapi.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public UserDTO saveUser(UserDTO userDTO) {
        // DTO를 Entity로 변환
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        
        // 저장
        User savedUser = userRepository.save(user);
        
        // 저장된 Entity를 DTO로 변환
        UserDTO savedUserDTO = new UserDTO();
        savedUserDTO.setId(savedUser.getId());
        savedUserDTO.setName(savedUser.getName());
        savedUserDTO.setEmail(savedUser.getEmail());
        
        return savedUserDTO;
    }
}
