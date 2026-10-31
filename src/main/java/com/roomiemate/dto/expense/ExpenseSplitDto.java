package com.roomiemate.dto.expense;

import com.roomiemate.entity.ExpenseSplit;

import java.math.BigDecimal;
import java.util.UUID;

public class ExpenseSplitDto {
    private UUID userId;
    private String userName;
    private BigDecimal amount;

    public ExpenseSplitDto() {}

    public ExpenseSplitDto(UUID userId, String userName, BigDecimal amount) {
        this.userId = userId;
        this.userName = userName;
        this.amount = amount;
    }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public static ExpenseSplitDto fromEntity(ExpenseSplit split) {
        if (split == null) return null;
        return new ExpenseSplitDto(split.getUser().getId(), split.getUser().getName(), split.getAmount());
    }

    public static ExpenseSplitDtoBuilder builder() {
        return new ExpenseSplitDtoBuilder();
    }

    public static class ExpenseSplitDtoBuilder {
        private UUID userId;
        private String userName;
        private BigDecimal amount;

        public ExpenseSplitDtoBuilder userId(UUID userId) { this.userId = userId; return this; }
        public ExpenseSplitDtoBuilder userName(String userName) { this.userName = userName; return this; }
        public ExpenseSplitDtoBuilder amount(BigDecimal amount) { this.amount = amount; return this; }

        public ExpenseSplitDto build() {
            return new ExpenseSplitDto(userId, userName, amount);
        }
    }
}
