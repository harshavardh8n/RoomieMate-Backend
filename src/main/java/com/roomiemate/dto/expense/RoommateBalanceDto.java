package com.roomiemate.dto.expense;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

public class RoommateBalanceDto {
    private UUID userId;
    private String name;
    private String email;
    private BigDecimal netBalance;

    public RoommateBalanceDto() {}

    public RoommateBalanceDto(UUID userId, String name, String email, BigDecimal netBalance) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.netBalance = netBalance;
    }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public BigDecimal getNetBalance() { return netBalance; }
    public void setNetBalance(BigDecimal netBalance) { this.netBalance = netBalance; }

    @JsonProperty("netWithUser")
    public BigDecimal getNetWithUser() { return netBalance; }

    public static RoommateBalanceDtoBuilder builder() {
        return new RoommateBalanceDtoBuilder();
    }

    public static class RoommateBalanceDtoBuilder {
        private UUID userId;
        private String name;
        private String email;
        private BigDecimal netBalance;

        public RoommateBalanceDtoBuilder userId(UUID userId) { this.userId = userId; return this; }
        public RoommateBalanceDtoBuilder name(String name) { this.name = name; return this; }
        public RoommateBalanceDtoBuilder email(String email) { this.email = email; return this; }
        public RoommateBalanceDtoBuilder netBalance(BigDecimal netBalance) { this.netBalance = netBalance; return this; }

        public RoommateBalanceDto build() {
            return new RoommateBalanceDto(userId, name, email, netBalance);
        }
    }
}
