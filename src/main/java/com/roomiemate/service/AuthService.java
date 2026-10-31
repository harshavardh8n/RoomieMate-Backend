package com.roomiemate.service;

import com.roomiemate.config.JwtService;
import com.roomiemate.dto.auth.AuthResponse;
import com.roomiemate.dto.auth.LoginRequest;
import com.roomiemate.dto.auth.RegisterRequest;
import com.roomiemate.dto.user.UserSummaryDto;
import com.roomiemate.entity.Room;
import com.roomiemate.entity.User;
import com.roomiemate.exception.ConflictException;
import com.roomiemate.exception.UnauthorizedException;
import com.roomiemate.repository.RoomRepository;
import com.roomiemate.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, RoomRepository roomRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email " + request.getEmail() + " is already registered.");
        }

        User user = User.builder()
                .name(request.getName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser);

        return AuthResponse.builder()
                .token(token)
                .user(UserSummaryDto.fromEntity(savedUser))
                .roomId(null)
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password.");
        }

        String token = jwtService.generateToken(user);

        // Find primary room (first room if member of any)
        List<Room> rooms = roomRepository.findAllByMemberId(user.getId());
        UUID primaryRoomId = rooms.isEmpty() ? null : rooms.get(0).getId();

        return AuthResponse.builder()
                .token(token)
                .user(UserSummaryDto.fromEntity(user))
                .roomId(primaryRoomId)
                .build();
    }
}

