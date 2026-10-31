package com.roomiemate.dto.room;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class InviteUserRequest {

    @NotNull(message = "User ID to invite cannot be null")
    private UUID userId;

    public InviteUserRequest() {}

    public InviteUserRequest(UUID userId) {
        this.userId = userId;
    }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public static InviteUserRequestBuilder builder() {
        return new InviteUserRequestBuilder();
    }

    public static class InviteUserRequestBuilder {
        private UUID userId;

        public InviteUserRequestBuilder userId(UUID userId) { this.userId = userId; return this; }

        public InviteUserRequest build() {
            return new InviteUserRequest(userId);
        }
    }
}
