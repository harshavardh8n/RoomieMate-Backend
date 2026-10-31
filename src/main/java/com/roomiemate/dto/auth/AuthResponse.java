package com.roomiemate.dto.auth;

import com.roomiemate.dto.user.UserSummaryDto;

import java.util.UUID;

public class AuthResponse {
    private String token;
    private UserSummaryDto user;
    private UUID roomId;

    public AuthResponse() {}

    public AuthResponse(String token, UserSummaryDto user, UUID roomId) {
        this.token = token;
        this.user = user;
        this.roomId = roomId;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public UserSummaryDto getUser() { return user; }
    public void setUser(UserSummaryDto user) { this.user = user; }

    public UUID getRoomId() { return roomId; }
    public void setRoomId(UUID roomId) { this.roomId = roomId; }

    public static AuthResponseBuilder builder() {
        return new AuthResponseBuilder();
    }

    public static class AuthResponseBuilder {
        private String token;
        private UserSummaryDto user;
        private UUID roomId;

        public AuthResponseBuilder token(String token) { this.token = token; return this; }
        public AuthResponseBuilder user(UserSummaryDto user) { this.user = user; return this; }
        public AuthResponseBuilder roomId(UUID roomId) { this.roomId = roomId; return this; }

        public AuthResponse build() {
            return new AuthResponse(token, user, roomId);
        }
    }
}
