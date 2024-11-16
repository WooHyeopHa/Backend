package com.whh.findmuseapi.user.service;

import com.whh.findmuseapi.user.entity.BlackUser;
import com.whh.findmuseapi.user.entity.User;
import com.whh.findmuseapi.user.repository.BlackUserRepository;
import com.whh.findmuseapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlackUserService {

    private final UserRepository userRepository;
    private final BlackUserRepository blackUserRepository;

    @Description("사용자 차단하기")
    @Transactional
    public void blockUser(User user, long targetUserId) {
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("대상 사용자가 존재하지 않습니다."));

        boolean alreadyBlocked = blackUserRepository.existsByUserAndTargetUser(user, targetUser);
        if (alreadyBlocked) {
            throw new RuntimeException("이미 이 사용자를 차단했습니다.");
        }

        BlackUser blackUser = BlackUser.builder()
                .user(user)
                .targetUser(targetUser)
                .build();

        blackUserRepository.save(blackUser);
    }

    @Description("차단한 사용자 조회")
    @Transactional(readOnly = true)
    public List<User> getBlockedUsers(User user) {
        List<BlackUser> blackUsers = blackUserRepository.findAllByUser(user);

        return blackUsers.stream()
                .map(BlackUser::getTargetUser)
                .toList();
    }

    @Description("사용자 차단 해제")
    @Transactional
    public void unblockUser(User user, long targetUserId) {
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("대상 사용자가 존재하지 않습니다."));

        BlackUser blackUser = blackUserRepository.findByUserAndTargetUser(user, targetUser)
                .orElseThrow(() -> new RuntimeException("차단된 사용자가 아닙니다."));

        blackUserRepository.delete(blackUser);
    }

}
