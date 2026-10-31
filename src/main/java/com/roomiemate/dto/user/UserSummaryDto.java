package com.roomiemate.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomiemate.entity.User;

import java.util.UUID;

public class UserSummaryDto {
    private UUID id;
    private String name;
    private String email;

    public UserSummaryDto() {}

    public UserSummaryDto(UUID id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    @JsonProperty("_id")
    public UUID get_id() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public static UserSummaryDto fromEntity(User user) {
        if (user == null) return null;
        return new UserSummaryDto(user.getId(), user.getName(), user.getEmail());
    }

    public static UserSummaryDtoBuilder builder() {
        return new UserSummaryDtoBuilder();
    }

    public static class UserSummaryDtoBuilder {
        private UUID id;
        private String name;
        private String email;

        public UserSummaryDtoBuilder id(UUID id) { this.id = id; return this; }
        public UserSummaryDtoBuilder name(String name) { this.name = name; return this; }
        public UserSummaryDtoBuilder email(String email) { this.email = email; return this; }

        public UserSummaryDto build() {
            return new UserSummaryDto(id, name, email);
        }
    }
}
