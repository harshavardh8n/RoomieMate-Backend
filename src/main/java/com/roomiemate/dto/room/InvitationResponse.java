package com.roomiemate.dto.room;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomiemate.dto.user.UserSummaryDto;
import com.roomiemate.entity.Invitation;
import com.roomiemate.entity.InvitationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationResponse {
    private UUID id;
    private UUID roomId;
    private String roomName;
    private UserSummaryDto sender;
    private UserSummaryDto receiver;
    private int roommatesCount;
    private InvitationStatus status;
    private LocalDateTime createdAt;

    @JsonProperty("_id")
    public UUID get_id() {
        return id;
    }

    @JsonProperty("roommates")
    public int getRoommates() {
        return roommatesCount;
    }

    @JsonProperty("Admin")
    public String getAdminName() {
        return sender != null ? sender.getName() : null;
    }

    public static InvitationResponse fromEntity(Invitation invitation) {
        if (invitation == null) return null;
        return InvitationResponse.builder()
                .id(invitation.getId())
                .roomId(invitation.getRoom().getId())
                .roomName(invitation.getRoom().getName())
                .sender(UserSummaryDto.fromEntity(invitation.getSender()))
                .receiver(UserSummaryDto.fromEntity(invitation.getReceiver()))
                .roommatesCount(invitation.getRoom().getMembers().size())
                .status(invitation.getStatus())
                .createdAt(invitation.getCreatedAt())
                .build();
    }
}

