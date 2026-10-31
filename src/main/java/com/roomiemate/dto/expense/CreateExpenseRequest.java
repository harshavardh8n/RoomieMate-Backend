package com.roomiemate.dto.expense;

import com.roomiemate.entity.ExpenseCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreateExpenseRequest {

    @NotBlank(message = "Expense description cannot be blank")
    private String description;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    private ExpenseCategory category = ExpenseCategory.OTHER;

    private List<UUID> splittersIds = new ArrayList<>();

    private List<CustomSplitItem> customSplits = new ArrayList<>();

    public CreateExpenseRequest() {}

    public CreateExpenseRequest(String description, BigDecimal amount, ExpenseCategory category, List<UUID> splittersIds, List<CustomSplitItem> customSplits) {
        this.description = description;
        this.amount = amount;
        this.category = category != null ? category : ExpenseCategory.OTHER;
        this.splittersIds = splittersIds != null ? splittersIds : new ArrayList<>();
        this.customSplits = customSplits != null ? customSplits : new ArrayList<>();
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public ExpenseCategory getCategory() { return category; }
    public void setCategory(ExpenseCategory category) { this.category = category; }

    public List<UUID> getSplittersIds() { return splittersIds; }
    public void setSplittersIds(List<UUID> splittersIds) { this.splittersIds = splittersIds; }

    public List<CustomSplitItem> getCustomSplits() { return customSplits; }
    public void setCustomSplits(List<CustomSplitItem> customSplits) { this.customSplits = customSplits; }

    public static class CustomSplitItem {
        private UUID userId;
        private BigDecimal amount;

        public CustomSplitItem() {}

        public CustomSplitItem(UUID userId, BigDecimal amount) {
            this.userId = userId;
            this.amount = amount;
        }

        public UUID getUserId() { return userId; }
        public void setUserId(UUID userId) { this.userId = userId; }

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
    }

    public static CreateExpenseRequestBuilder builder() {
        return new CreateExpenseRequestBuilder();
    }

    public static class CreateExpenseRequestBuilder {
        private String description;
        private BigDecimal amount;
        private ExpenseCategory category = ExpenseCategory.OTHER;
        private List<UUID> splittersIds = new ArrayList<>();
        private List<CustomSplitItem> customSplits = new ArrayList<>();

        public CreateExpenseRequestBuilder description(String description) { this.description = description; return this; }
        public CreateExpenseRequestBuilder amount(BigDecimal amount) { this.amount = amount; return this; }
        public CreateExpenseRequestBuilder category(ExpenseCategory category) { this.category = category; return this; }
        public CreateExpenseRequestBuilder splittersIds(List<UUID> splittersIds) {
            this.splittersIds = splittersIds != null ? splittersIds : new ArrayList<>();
            return this;
        }
        public CreateExpenseRequestBuilder customSplits(List<CustomSplitItem> customSplits) {
            this.customSplits = customSplits != null ? customSplits : new ArrayList<>();
            return this;
        }

        public CreateExpenseRequest build() {
            return new CreateExpenseRequest(description, amount, category, splittersIds, customSplits);
        }
    }
}
