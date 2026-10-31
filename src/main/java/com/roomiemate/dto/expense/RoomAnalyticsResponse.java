package com.roomiemate.dto.expense;

import com.roomiemate.dto.user.UserSummaryDto;
import com.roomiemate.entity.ExpenseCategory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RoomAnalyticsResponse {
    private UUID roomId;
    private BigDecimal totalExpenses;
    private long totalExpenseCount;
    private Map<ExpenseCategory, BigDecimal> spendingByCategory = new HashMap<>();
    private List<MemberSpendingDto> memberSpending = new ArrayList<>();

    public RoomAnalyticsResponse() {}

    public RoomAnalyticsResponse(UUID roomId, BigDecimal totalExpenses, long totalExpenseCount, Map<ExpenseCategory, BigDecimal> spendingByCategory, List<MemberSpendingDto> memberSpending) {
        this.roomId = roomId;
        this.totalExpenses = totalExpenses;
        this.totalExpenseCount = totalExpenseCount;
        this.spendingByCategory = spendingByCategory != null ? spendingByCategory : new HashMap<>();
        this.memberSpending = memberSpending != null ? memberSpending : new ArrayList<>();
    }

    public UUID getRoomId() { return roomId; }
    public void setRoomId(UUID roomId) { this.roomId = roomId; }

    public BigDecimal getTotalExpenses() { return totalExpenses; }
    public void setTotalExpenses(BigDecimal totalExpenses) { this.totalExpenses = totalExpenses; }

    public long getTotalExpenseCount() { return totalExpenseCount; }
    public void setTotalExpenseCount(long totalExpenseCount) { this.totalExpenseCount = totalExpenseCount; }

    public Map<ExpenseCategory, BigDecimal> getSpendingByCategory() { return spendingByCategory; }
    public void setSpendingByCategory(Map<ExpenseCategory, BigDecimal> spendingByCategory) { this.spendingByCategory = spendingByCategory; }

    public List<MemberSpendingDto> getMemberSpending() { return memberSpending; }
    public void setMemberSpending(List<MemberSpendingDto> memberSpending) { this.memberSpending = memberSpending; }

    public static class MemberSpendingDto {
        private UserSummaryDto user;
        private BigDecimal totalPaid;
        private BigDecimal totalShareOwed;
        private BigDecimal netPosition;

        public MemberSpendingDto() {}

        public MemberSpendingDto(UserSummaryDto user, BigDecimal totalPaid, BigDecimal totalShareOwed, BigDecimal netPosition) {
            this.user = user;
            this.totalPaid = totalPaid;
            this.totalShareOwed = totalShareOwed;
            this.netPosition = netPosition;
        }

        public UserSummaryDto getUser() { return user; }
        public void setUser(UserSummaryDto user) { this.user = user; }

        public BigDecimal getTotalPaid() { return totalPaid; }
        public void setTotalPaid(BigDecimal totalPaid) { this.totalPaid = totalPaid; }

        public BigDecimal getTotalShareOwed() { return totalShareOwed; }
        public void setTotalShareOwed(BigDecimal totalShareOwed) { this.totalShareOwed = totalShareOwed; }

        public BigDecimal getNetPosition() { return netPosition; }
        public void setNetPosition(BigDecimal netPosition) { this.netPosition = netPosition; }

        public static MemberSpendingDtoBuilder builder() {
            return new MemberSpendingDtoBuilder();
        }

        public static class MemberSpendingDtoBuilder {
            private UserSummaryDto user;
            private BigDecimal totalPaid;
            private BigDecimal totalShareOwed;
            private BigDecimal netPosition;

            public MemberSpendingDtoBuilder user(UserSummaryDto user) { this.user = user; return this; }
            public MemberSpendingDtoBuilder totalPaid(BigDecimal totalPaid) { this.totalPaid = totalPaid; return this; }
            public MemberSpendingDtoBuilder totalShareOwed(BigDecimal totalShareOwed) { this.totalShareOwed = totalShareOwed; return this; }
            public MemberSpendingDtoBuilder netPosition(BigDecimal netPosition) { this.netPosition = netPosition; return this; }

            public MemberSpendingDto build() {
                return new MemberSpendingDto(user, totalPaid, totalShareOwed, netPosition);
            }
        }
    }

    public static RoomAnalyticsResponseBuilder builder() {
        return new RoomAnalyticsResponseBuilder();
    }

    public static class RoomAnalyticsResponseBuilder {
        private UUID roomId;
        private BigDecimal totalExpenses;
        private long totalExpenseCount;
        private Map<ExpenseCategory, BigDecimal> spendingByCategory = new HashMap<>();
        private List<MemberSpendingDto> memberSpending = new ArrayList<>();

        public RoomAnalyticsResponseBuilder roomId(UUID roomId) { this.roomId = roomId; return this; }
        public RoomAnalyticsResponseBuilder totalExpenses(BigDecimal totalExpenses) { this.totalExpenses = totalExpenses; return this; }
        public RoomAnalyticsResponseBuilder totalExpenseCount(long totalExpenseCount) { this.totalExpenseCount = totalExpenseCount; return this; }
        public RoomAnalyticsResponseBuilder spendingByCategory(Map<ExpenseCategory, BigDecimal> spendingByCategory) {
            this.spendingByCategory = spendingByCategory != null ? spendingByCategory : new HashMap<>();
            return this;
        }
        public RoomAnalyticsResponseBuilder memberSpending(List<MemberSpendingDto> memberSpending) {
            this.memberSpending = memberSpending != null ? memberSpending : new ArrayList<>();
            return this;
        }

        public RoomAnalyticsResponse build() {
            return new RoomAnalyticsResponse(roomId, totalExpenses, totalExpenseCount, spendingByCategory, memberSpending);
        }
    }
}
