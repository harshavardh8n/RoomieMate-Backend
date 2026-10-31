package com.roomiemate.service;

import com.roomiemate.dto.user.UserSummaryDto;
import com.roomiemate.entity.User;
import com.roomiemate.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<UserSummaryDto> searchUsers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return userRepository.findAll().stream()
                    .limit(20)
                    .map(UserSummaryDto::fromEntity)
                    .collect(Collectors.toList());
        }

        String trimmed = query.trim();
        return userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(trimmed, trimmed)
                .stream()
                .map(UserSummaryDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserSummaryDto> getIdleUsers(UUID currentUserId) {
        return userRepository.findIdleUsers(currentUserId)
                .stream()
                .map(UserSummaryDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserSummaryDto> getUsersNotInRoom(UUID roomId, UUID currentUserId) {
        return userRepository.findUsersNotInRoom(roomId, currentUserId)
                .stream()
                .map(UserSummaryDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserSummaryDto getUserById(UUID userId) {
        return userRepository.findById(userId)
                .map(UserSummaryDto::fromEntity)
                .orElse(null);
    }
}

