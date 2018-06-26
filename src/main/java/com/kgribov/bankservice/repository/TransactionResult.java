package com.kgribov.bankservice.repository;

import com.kgribov.bankservice.model.Account;

import java.util.Objects;

public class TransactionResult<R> {

    private final Account updatedFrom;
    private final Account updatedTo;
    private final R result;

    public TransactionResult(Account updatedFrom, Account updatedTo, R result) {
        this.updatedFrom = updatedFrom;
        this.updatedTo = updatedTo;
        this.result = result;
    }

    public Account getUpdatedFrom() {
        return updatedFrom;
    }

    public Account getUpdatedTo() {
        return updatedTo;
    }

    public R getResult() {
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionResult<?> result1 = (TransactionResult<?>) o;
        return Objects.equals(updatedFrom, result1.updatedFrom) &&
                Objects.equals(updatedTo, result1.updatedTo) &&
                Objects.equals(result, result1.result);
    }

    @Override
    public int hashCode() {

        return Objects.hash(updatedFrom, updatedTo, result);
    }

    @Override
    public String toString() {
        return "TransactionResult{" +
                "updatedFrom=" + updatedFrom +
                ", updatedTo=" + updatedTo +
                ", result=" + result +
                '}';
    }
}
