package com.roomiemate.dto.room;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomiemate.dto.user.UserSummaryDto;
import com.roomiemate.entity.Invitation;
import com.roomiemate.entity.InvitationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class InvitationResponse {
    private UUID id;
    private UUID roomId;
    private String roomName;
    private UserSummaryDto sender;
    private UserSummaryDto receiver;
    private int roommatesCount;
    private InvitationStatus status;
    private LocalDateTime createdAt;

    public InvitationResponse() {}

    public InvitationResponse(UUID id, UUID roomId, String roomName, UserSummaryDto sender, UserSummaryDto receiver, int roommatesCount, InvitationStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.roomId = roomId;
        this.roomName = roomName;
        this.sender = sender;
        this.receiver = receiver;
        this.roommatesCount = roommatesCount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    @JsonProperty("_id")
    public UUID get_id() { return id; }

    public UUID getRoomId() { return roomId; }
    public void setRoomId(UUID roomId) { this.roomId = roomId; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public UserSummaryDto getSender() { return sender; }
    public void setSender(UserSummaryDto sender) { this.sender = sender; }

    public UserSummaryDto getReceiver() { return receiver; }
    public void setReceiver(UserSummaryDto receiver) { this.receiver = receiver; }

    public int getRoommatesCount() { return roommatesCount; }
    public void setRoommatesCount(int roommatesCount) { this.roommatesCount = roommatesCount; }

    @JsonProperty("roommates")
    public int getRoommates() { return roommatesCount; }

    @JsonProperty("Admin")
    public String getAdminName() { return sender != null ? sender.getName() : null; }

    public InvitationStatus getStatus() { return status; }
    public void setStatus(InvitationStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static InvitationResponse fromEntity(Invitation invitation) {
        if (invitation == null) return null;
        return new InvitationResponse(
                invitation.getId(),
                invitation.getRoom().getId(),
                invitation.getRoom().getName(),
                UserSummaryDto.fromEntity(invitation.getSender()),
                UserSummaryDto.fromEntity(invitation.getReceiver()),
                invitation.getRoom().getMembers() != null ? invitation.getRoom().getMembers().size() : 0,
                invitation.getStatus(),
                invitation.getCreatedAt()
        );
    }

    public static InvitationResponseBuilder builder() {
        return new InvitationResponseBuilder();
    }

    public static class InvitationResponseBuilder {
        private UUID id;
        private UUID roomId;
        private String roomName;
        private UserSummaryDto sender;
        private UserSummaryDto receiver;
        private int roommatesCount;
        private InvitationStatus status;
        private LocalDateTime createdAt;

        public InvitationResponseBuilder id(UUID id) { this.id = id; return this; }
        public InvitationResponseBuilder roomId(UUID roomId) { this.roomId = roomId; return this; }
        public InvitationResponseBuilder roomName(String roomName) { this.roomName = roomName; return this; }
        public InvitationResponseBuilder sender(UserSummaryDto sender) { this.sender = sender; return this; }
        public InvitationResponseBuilder receiver(UserSummaryDto receiver) { this.receiver = receiver; return this; }
        public InvitationResponseBuilder roommatesCount(int roommatesCount) { this.roommatesCount = roommatesCount; return this; }
        public InvitationResponseBuilder status(InvitationStatus status) { this.status = status; return this; }
        public InvitationResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public InvitationResponse build() {
            return new InvitationResponse(id, roomId, roomName, sender, receiver, roommatesCount, status, createdAt);
        }
    }
}
