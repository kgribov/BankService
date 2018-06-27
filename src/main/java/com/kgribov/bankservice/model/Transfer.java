package com.kgribov.bankservice.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Transfer {

    private final Long id;
    private final Long fromId;
    private final Long toId;
    private final Integer amount;
    private final Long timestamp;

    public Transfer(Long id,
                    Long fromId,
                    Long toId,
                    Integer amount,
                    Long timestamp) {

        this.id = id;
        this.fromId = fromId;
        this.toId = toId;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
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

    public Long getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Transfer transfer = (Transfer) o;

        return new EqualsBuilder()
                .append(id, transfer.id)
                .append(fromId, transfer.fromId)
                .append(toId, transfer.toId)
                .append(amount, transfer.amount)
                .append(timestamp, transfer.timestamp)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(fromId)
                .append(toId)
                .append(amount)
                .append(timestamp)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "id=" + id +
                ", fromId=" + fromId +
                ", toId=" + toId +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                '}';
    }
}
