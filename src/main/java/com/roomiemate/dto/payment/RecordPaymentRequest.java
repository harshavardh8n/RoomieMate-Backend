package com.roomiemate.dto.payment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class RecordPaymentRequest {

    @NotNull(message = "Roommate ID to pay is required")
    private UUID roommateId;

    @NotNull(message = "Payment amount is required")
    @DecimalMin(value = "0.01", message = "Payment amount must be greater than zero")
    private BigDecimal amount;

    private String notes;

    public RecordPaymentRequest() {}

    public RecordPaymentRequest(UUID roommateId, BigDecimal amount, String notes) {
        this.roommateId = roommateId;
        this.amount = amount;
        this.notes = notes;
    }

    public UUID getRoommateId() { return roommateId; }
    public void setRoommateId(UUID roommateId) { this.roommateId = roommateId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public static RecordPaymentRequestBuilder builder() {
        return new RecordPaymentRequestBuilder();
    }

    public static class RecordPaymentRequestBuilder {
        private UUID roommateId;
        private BigDecimal amount;
        private String notes;

        public RecordPaymentRequestBuilder roommateId(UUID roommateId) { this.roommateId = roommateId; return this; }
        public RecordPaymentRequestBuilder amount(BigDecimal amount) { this.amount = amount; return this; }
        public RecordPaymentRequestBuilder notes(String notes) { this.notes = notes; return this; }

        public RecordPaymentRequest build() {
            return new RecordPaymentRequest(roommateId, amount, notes);
        }
    }
}
