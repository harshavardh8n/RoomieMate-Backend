package com.roomiemate.dto.expense;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BalanceSummaryResponse {
    private UUID roomId;
    private UUID currentUserId;
    private BigDecimal userTotalNetBalance;
    private List<RoommateBalanceDto> roommateBalances = new ArrayList<>();

    public BalanceSummaryResponse() {}

    public BalanceSummaryResponse(UUID roomId, UUID currentUserId, BigDecimal userTotalNetBalance, List<RoommateBalanceDto> roommateBalances) {
        this.roomId = roomId;
        this.currentUserId = currentUserId;
        this.userTotalNetBalance = userTotalNetBalance;
        this.roommateBalances = roommateBalances != null ? roommateBalances : new ArrayList<>();
    }

    public UUID getRoomId() { return roomId; }
    public void setRoomId(UUID roomId) { this.roomId = roomId; }

    public UUID getCurrentUserId() { return currentUserId; }
    public void setCurrentUserId(UUID currentUserId) { this.currentUserId = currentUserId; }

    public BigDecimal getUserTotalNetBalance() { return userTotalNetBalance; }
    public void setUserTotalNetBalance(BigDecimal userTotalNetBalance) { this.userTotalNetBalance = userTotalNetBalance; }

    @JsonProperty("userNetBalance")
    public BigDecimal getUserNetBalance() { return userTotalNetBalance; }

    public List<RoommateBalanceDto> getRoommateBalances() { return roommateBalances; }
    public void setRoommateBalances(List<RoommateBalanceDto> roommateBalances) { this.roommateBalances = roommateBalances; }

    public static BalanceSummaryResponseBuilder builder() {
        return new BalanceSummaryResponseBuilder();
    }

    public static class BalanceSummaryResponseBuilder {
        private UUID roomId;
        private UUID currentUserId;
        private BigDecimal userTotalNetBalance;
        private List<RoommateBalanceDto> roommateBalances = new ArrayList<>();

        public BalanceSummaryResponseBuilder roomId(UUID roomId) { this.roomId = roomId; return this; }
        public BalanceSummaryResponseBuilder currentUserId(UUID currentUserId) { this.currentUserId = currentUserId; return this; }
        public BalanceSummaryResponseBuilder userTotalNetBalance(BigDecimal userTotalNetBalance) { this.userTotalNetBalance = userTotalNetBalance; return this; }
        public BalanceSummaryResponseBuilder roommateBalances(List<RoommateBalanceDto> roommateBalances) {
            this.roommateBalances = roommateBalances != null ? roommateBalances : new ArrayList<>();
            return this;
        }

        public BalanceSummaryResponse build() {
            return new BalanceSummaryResponse(roomId, currentUserId, userTotalNetBalance, roommateBalances);
        }
    }
}
