package com.kgribov.bankservice.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CreateAccountDTO {

    private final String name;
    private final Integer balance;

    public CreateAccountDTO(String name, Integer balance) {
        this.name = name;
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public Integer getBalance() {
        return balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CreateAccountDTO that = (CreateAccountDTO) o;

        return new EqualsBuilder()
                .append(name, that.name)
                .append(balance, that.balance)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(balance)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "CreateAccountDTO{" +
                "name='" + name + '\'' +
                ", balance=" + balance +
                '}';
    }
}
