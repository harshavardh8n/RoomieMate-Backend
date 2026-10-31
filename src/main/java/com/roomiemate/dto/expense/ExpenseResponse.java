package com.roomiemate.dto.expense;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomiemate.dto.user.UserSummaryDto;
import com.roomiemate.entity.Expense;
import com.roomiemate.entity.ExpenseCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ExpenseResponse {
    private UUID id;
    private String description;
    private BigDecimal amount;
    private ExpenseCategory category;
    private UUID roomId;
    private UserSummaryDto payer;
    private List<ExpenseSplitDto> splits = new ArrayList<>();
    private LocalDateTime createdAt;

    public ExpenseResponse() {}

    public ExpenseResponse(UUID id, String description, BigDecimal amount, ExpenseCategory category, UUID roomId, UserSummaryDto payer, List<ExpenseSplitDto> splits, LocalDateTime createdAt) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.roomId = roomId;
        this.payer = payer;
        this.splits = splits != null ? splits : new ArrayList<>();
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    @JsonProperty("_id")
    public UUID get_id() { return id; }

    @JsonProperty("expenseDesc")
    public String getExpenseDesc() { return description; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public ExpenseCategory getCategory() { return category; }
    public void setCategory(ExpenseCategory category) { this.category = category; }

    public UUID getRoomId() { return roomId; }
    public void setRoomId(UUID roomId) { this.roomId = roomId; }

    public UserSummaryDto getPayer() { return payer; }
    public void setPayer(UserSummaryDto payer) { this.payer = payer; }

    public List<ExpenseSplitDto> getSplits() { return splits; }
    public void setSplits(List<ExpenseSplitDto> splits) { this.splits = splits; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static ExpenseResponse fromEntity(Expense expense) {
        if (expense == null) return null;
        List<ExpenseSplitDto> splitDtos = expense.getSplits() != null
                ? expense.getSplits().stream().map(ExpenseSplitDto::fromEntity).collect(Collectors.toList())
                : new ArrayList<>();
        return new ExpenseResponse(
                expense.getId(),
                expense.getDescription(),
                expense.getAmount(),
                expense.getCategory(),
                expense.getRoom().getId(),
                UserSummaryDto.fromEntity(expense.getPayer()),
                splitDtos,
                expense.getCreatedAt()
        );
    }

    public static ExpenseResponseBuilder builder() {
        return new ExpenseResponseBuilder();
    }

    public static class ExpenseResponseBuilder {
        private UUID id;
        private String description;
        private BigDecimal amount;
        private ExpenseCategory category;
        private UUID roomId;
        private UserSummaryDto payer;
        private List<ExpenseSplitDto> splits = new ArrayList<>();
        private LocalDateTime createdAt;

        public ExpenseResponseBuilder id(UUID id) { this.id = id; return this; }
        public ExpenseResponseBuilder description(String description) { this.description = description; return this; }
        public ExpenseResponseBuilder amount(BigDecimal amount) { this.amount = amount; return this; }
        public ExpenseResponseBuilder category(ExpenseCategory category) { this.category = category; return this; }
        public ExpenseResponseBuilder roomId(UUID roomId) { this.roomId = roomId; return this; }
        public ExpenseResponseBuilder payer(UserSummaryDto payer) { this.payer = payer; return this; }
        public ExpenseResponseBuilder splits(List<ExpenseSplitDto> splits) {
            this.splits = splits != null ? splits : new ArrayList<>();
            return this;
        }
        public ExpenseResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public ExpenseResponse build() {
            return new ExpenseResponse(id, description, amount, category, roomId, payer, splits, createdAt);
        }
    }
}
