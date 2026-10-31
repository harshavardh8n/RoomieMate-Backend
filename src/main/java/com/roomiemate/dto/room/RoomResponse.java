package com.roomiemate.dto.room;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomiemate.dto.user.UserSummaryDto;
import com.roomiemate.entity.Room;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {
    private UUID id;
    private String name;
    private UserSummaryDto admin;
    private List<UserSummaryDto> members;
    private LocalDateTime createdAt;

    @JsonProperty("_id")
    public UUID get_id() {
        return id;
    }

    public static RoomResponse fromEntity(Room room) {
        if (room == null) return null;
        return RoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .admin(UserSummaryDto.fromEntity(room.getAdmin()))
                .members(room.getMembers().stream()
                        .map(UserSummaryDto::fromEntity)
                        .collect(Collectors.toList()))
                .createdAt(room.getCreatedAt())
                .build();
    }
}

