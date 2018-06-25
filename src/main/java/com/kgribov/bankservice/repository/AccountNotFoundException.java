package com.kgribov.bankservice.repository;

public class AccountNotFoundException extends Exception {

    public AccountNotFoundException(Long notFoundId) {
        super("Account with id=" + notFoundId + " is not found");
    }
}
