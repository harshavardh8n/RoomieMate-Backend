package com.roomiemate.dto.room;

import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreateRoomRequest {

    @NotBlank(message = "Room name cannot be blank")
    private String name;

    private List<UUID> invitedMemberIds = new ArrayList<>();

    public CreateRoomRequest() {}

    public CreateRoomRequest(String name, List<UUID> invitedMemberIds) {
        this.name = name;
        this.invitedMemberIds = invitedMemberIds != null ? invitedMemberIds : new ArrayList<>();
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<UUID> getInvitedMemberIds() { return invitedMemberIds; }
    public void setInvitedMemberIds(List<UUID> invitedMemberIds) { this.invitedMemberIds = invitedMemberIds; }

    public static CreateRoomRequestBuilder builder() {
        return new CreateRoomRequestBuilder();
    }

    public static class CreateRoomRequestBuilder {
        private String name;
        private List<UUID> invitedMemberIds = new ArrayList<>();

        public CreateRoomRequestBuilder name(String name) { this.name = name; return this; }
        public CreateRoomRequestBuilder invitedMemberIds(List<UUID> invitedMemberIds) {
            this.invitedMemberIds = invitedMemberIds != null ? invitedMemberIds : new ArrayList<>();
            return this;
        }

        public CreateRoomRequest build() {
            return new CreateRoomRequest(name, invitedMemberIds);
        }
    }
}
