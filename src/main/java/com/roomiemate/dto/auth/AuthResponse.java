package com.roomiemate.dto.auth;

import com.roomiemate.dto.user.UserSummaryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private UserSummaryDto user;
    private UUID roomId; // Primary room ID if user is already in a room
}

