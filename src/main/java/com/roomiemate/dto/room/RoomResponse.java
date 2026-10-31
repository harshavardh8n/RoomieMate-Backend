package com.roomiemate.dto.room;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomiemate.dto.user.UserSummaryDto;
import com.roomiemate.entity.Room;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RoomResponse {
    private UUID id;
    private String name;
    private UserSummaryDto admin;
    private List<UserSummaryDto> members = new ArrayList<>();
    private LocalDateTime createdAt;

    public RoomResponse() {}

    public RoomResponse(UUID id, String name, UserSummaryDto admin, List<UserSummaryDto> members, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.admin = admin;
        this.members = members != null ? members : new ArrayList<>();
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    @JsonProperty("_id")
    public UUID get_id() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public UserSummaryDto getAdmin() { return admin; }
    public void setAdmin(UserSummaryDto admin) { this.admin = admin; }

    public List<UserSummaryDto> getMembers() { return members; }
    public void setMembers(List<UserSummaryDto> members) { this.members = members; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static RoomResponse fromEntity(Room room) {
        if (room == null) return null;
        List<UserSummaryDto> memberDtos = room.getMembers() != null
                ? room.getMembers().stream().map(UserSummaryDto::fromEntity).collect(Collectors.toList())
                : new ArrayList<>();
        return new RoomResponse(room.getId(), room.getName(), UserSummaryDto.fromEntity(room.getAdmin()), memberDtos, room.getCreatedAt());
    }

    public static RoomResponseBuilder builder() {
        return new RoomResponseBuilder();
    }

    public static class RoomResponseBuilder {
        private UUID id;
        private String name;
        private UserSummaryDto admin;
        private List<UserSummaryDto> members = new ArrayList<>();
        private LocalDateTime createdAt;

        public RoomResponseBuilder id(UUID id) { this.id = id; return this; }
        public RoomResponseBuilder name(String name) { this.name = name; return this; }
        public RoomResponseBuilder admin(UserSummaryDto admin) { this.admin = admin; return this; }
        public RoomResponseBuilder members(List<UserSummaryDto> members) {
            this.members = members != null ? members : new ArrayList<>();
            return this;
        }
        public RoomResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public RoomResponse build() {
            return new RoomResponse(id, name, admin, members, createdAt);
        }
    }
}
