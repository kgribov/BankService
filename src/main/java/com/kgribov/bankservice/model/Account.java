package com.kgribov.bankservice.model;

import java.util.Objects;

public class Account {

    private final Long id;
    private final String name;
    private final Integer balance;

    public Account(Long id, String name, Integer balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    public Long getId() {
        return id;
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
        Account account = (Account) o;
        return Objects.equals(id, account.id) &&
                Objects.equals(name, account.name) &&
                Objects.equals(balance, account.balance);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, balance);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", balance=" + balance +
                '}';
    }
}
