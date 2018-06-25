package com.kgribov.bankservice.dto;

import java.util.Objects;

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
        CreateAccountDTO dto = (CreateAccountDTO) o;
        return Objects.equals(name, dto.name) &&
                Objects.equals(balance, dto.balance);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, balance);
    }

    @Override
    public String toString() {
        return "CreateAccountDTO{" +
                "name='" + name + '\'' +
                ", balance=" + balance +
                '}';
    }
}
