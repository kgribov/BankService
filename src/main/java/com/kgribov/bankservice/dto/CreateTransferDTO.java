package com.kgribov.bankservice.dto;

import java.util.Objects;

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
        return Objects.equals(fromId, that.fromId) &&
                Objects.equals(toId, that.toId) &&
                Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {

        return Objects.hash(fromId, toId, amount);
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
