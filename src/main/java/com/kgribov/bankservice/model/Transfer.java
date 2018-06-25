package com.kgribov.bankservice.model;

import java.util.Objects;

public class Transfer {

    private final Long id;
    private final Long fromId;
    private final Long to;
    private final Integer amount;
    private final Long timestamp;
    private final Status status;

    public Transfer(Long id,
                    Long fromId,
                    Long to,
                    Integer amount,
                    Long timestamp,
                    Status status) {

        this.id = id;
        this.fromId = fromId;
        this.to = to;
        this.amount = amount;
        this.timestamp = timestamp;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getFromId() {
        return fromId;
    }

    public Long getTo() {
        return to;
    }

    public Integer getAmount() {
        return amount;
    }

    public Status getStatus() {
        return status;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public enum Status {
        ACCEPTED,
        REJECTED_BY_SHORT_OF_MONEY,
        REJECTED_BY_OVERFLOW,
        REJECTED_BY_NEGATIVE_AMOUNT
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transfer transfer = (Transfer) o;
        return Objects.equals(id, transfer.id) &&
                Objects.equals(fromId, transfer.fromId) &&
                Objects.equals(to, transfer.to) &&
                Objects.equals(amount, transfer.amount) &&
                Objects.equals(timestamp, transfer.timestamp) &&
                status == transfer.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fromId, to, amount, timestamp, status);
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "id=" + id +
                ", fromId=" + fromId +
                ", to=" + to +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                ", status=" + status +
                '}';
    }
}
