package com.kgribov.bankservice.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CreateTransferDTO {

    private final Long fromId;
    private final Long toId;
    private final Integer amount;

    public CreateTransferDTO(Long fromId, Long toId, Integer amount) {
        this.fromId = fromId;
        this.toId = toId;
        this.amount = amount;
    }

    public Long getFromId() {
        return fromId;
    }

    public Long getToId() {
        return toId;
    }

    public Integer getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CreateTransferDTO that = (CreateTransferDTO) o;

        return new EqualsBuilder()
                .append(fromId, that.fromId)
                .append(toId, that.toId)
                .append(amount, that.amount)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(fromId)
                .append(toId)
                .append(amount)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "CreateTransferDTO{" +
                "fromId=" + fromId +
                ", toId=" + toId +
                ", amount=" + amount +
                '}';
    }
}
