package com.roomiemate.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomiemate.dto.user.UserSummaryDto;
import com.roomiemate.entity.Payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentResponse {
    private UUID id;
    private UUID roomId;
    private UserSummaryDto payer;
    private UserSummaryDto receiver;
    private BigDecimal amount;
    private String notes;
    private LocalDateTime createdAt;

    public PaymentResponse() {}

    public PaymentResponse(UUID id, UUID roomId, UserSummaryDto payer, UserSummaryDto receiver, BigDecimal amount, String notes, LocalDateTime createdAt) {
        this.id = id;
        this.roomId = roomId;
        this.payer = payer;
        this.receiver = receiver;
        this.amount = amount;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    @JsonProperty("_id")
    public UUID get_id() { return id; }

    public UUID getRoomId() { return roomId; }
    public void setRoomId(UUID roomId) { this.roomId = roomId; }

    public UserSummaryDto getPayer() { return payer; }
    public void setPayer(UserSummaryDto payer) { this.payer = payer; }

    public UserSummaryDto getReceiver() { return receiver; }
    public void setReceiver(UserSummaryDto receiver) { this.receiver = receiver; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static PaymentResponse fromEntity(Payment payment) {
        if (payment == null) return null;
        return new PaymentResponse(
                payment.getId(),
                payment.getRoom().getId(),
                UserSummaryDto.fromEntity(payment.getPayer()),
                UserSummaryDto.fromEntity(payment.getReceiver()),
                payment.getAmount(),
                payment.getNotes(),
                payment.getCreatedAt()
        );
    }

    public static PaymentResponseBuilder builder() {
        return new PaymentResponseBuilder();
    }

    public static class PaymentResponseBuilder {
        private UUID id;
        private UUID roomId;
        private UserSummaryDto payer;
        private UserSummaryDto receiver;
        private BigDecimal amount;
        private String notes;
        private LocalDateTime createdAt;

        public PaymentResponseBuilder id(UUID id) { this.id = id; return this; }
        public PaymentResponseBuilder roomId(UUID roomId) { this.roomId = roomId; return this; }
        public PaymentResponseBuilder payer(UserSummaryDto payer) { this.payer = payer; return this; }
        public PaymentResponseBuilder receiver(UserSummaryDto receiver) { this.receiver = receiver; return this; }
        public PaymentResponseBuilder amount(BigDecimal amount) { this.amount = amount; return this; }
        public PaymentResponseBuilder notes(String notes) { this.notes = notes; return this; }
        public PaymentResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public PaymentResponse build() {
            return new PaymentResponse(id, roomId, payer, receiver, amount, notes, createdAt);
        }
    }
}
