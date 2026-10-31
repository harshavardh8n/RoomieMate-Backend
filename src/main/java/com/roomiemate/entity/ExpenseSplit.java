package com.roomiemate.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "expense_splits")
public class ExpenseSplit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id", nullable = false)
    @JsonIgnore
    private Expense expense;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "rooms"})
    private User user;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    public ExpenseSplit() {}

    public ExpenseSplit(UUID id, Expense expense, User user, BigDecimal amount) {
        this.id = id;
        this.expense = expense;
        this.user = user;
        this.amount = amount;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Expense getExpense() { return expense; }
    public void setExpense(Expense expense) { this.expense = expense; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExpenseSplit that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public static ExpenseSplitBuilder builder() {
        return new ExpenseSplitBuilder();
    }

    public static class ExpenseSplitBuilder {
        private UUID id;
        private Expense expense;
        private User user;
        private BigDecimal amount;

        public ExpenseSplitBuilder id(UUID id) { this.id = id; return this; }
        public ExpenseSplitBuilder expense(Expense expense) { this.expense = expense; return this; }
        public ExpenseSplitBuilder user(User user) { this.user = user; return this; }
        public ExpenseSplitBuilder amount(BigDecimal amount) { this.amount = amount; return this; }

        public ExpenseSplit build() {
            return new ExpenseSplit(id, expense, user, amount);
        }
    }
}
