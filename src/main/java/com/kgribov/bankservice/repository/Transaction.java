package com.kgribov.bankservice.repository;

import com.kgribov.bankservice.model.Account;

public interface Transaction<R>{

    TransactionResult<R> processTransaction(Account from, Account to) throws AccountNotFoundException;
}
