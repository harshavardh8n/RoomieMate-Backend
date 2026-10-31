package com.roomiemate.dto.payment;

import com.roomiemate.dto.user.UserSummaryDto;

import java.math.BigDecimal;

public class SimplifiedSettlementDto {
    private UserSummaryDto fromUser;
    private UserSummaryDto toUser;
    private BigDecimal amount;

    public SimplifiedSettlementDto() {}

    public SimplifiedSettlementDto(UserSummaryDto fromUser, UserSummaryDto toUser, BigDecimal amount) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.amount = amount;
    }

    public UserSummaryDto getFromUser() { return fromUser; }
    public void setFromUser(UserSummaryDto fromUser) { this.fromUser = fromUser; }

    public UserSummaryDto getToUser() { return toUser; }
    public void setToUser(UserSummaryDto toUser) { this.toUser = toUser; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public static SimplifiedSettlementDtoBuilder builder() {
        return new SimplifiedSettlementDtoBuilder();
    }

    public static class SimplifiedSettlementDtoBuilder {
        private UserSummaryDto fromUser;
        private UserSummaryDto toUser;
        private BigDecimal amount;

        public SimplifiedSettlementDtoBuilder fromUser(UserSummaryDto fromUser) { this.fromUser = fromUser; return this; }
        public SimplifiedSettlementDtoBuilder toUser(UserSummaryDto toUser) { this.toUser = toUser; return this; }
        public SimplifiedSettlementDtoBuilder amount(BigDecimal amount) { this.amount = amount; return this; }

        public SimplifiedSettlementDto build() {
            return new SimplifiedSettlementDto(fromUser, toUser, amount);
        }
    }
}
