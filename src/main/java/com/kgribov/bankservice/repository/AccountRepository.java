package com.kgribov.bankservice.repository;

import com.kgribov.bankservice.model.Account;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class AccountRepository {

    private final AtomicLong idProvider = new AtomicLong();
    private final Map<Long, AtomicReference<Account>> accounts = new ConcurrentHashMap<>(10_000);

    public Account createAccount(String name, Integer startBalance) {
        Long id = idProvider.getAndIncrement();
        Account account = new Account(id, name, startBalance);
        accounts.put(id, new AtomicReference<>(account));

        return account;
    }

    public Account getAccount(Long id) throws AccountNotFoundException {
        AtomicReference<Account> account = accounts.get(id);
        if (account == null) {
            throw new AccountNotFoundException(id);
        }
        return account.get();
    }

    public boolean updateBalance(Account account, Integer newBalance) throws AccountNotFoundException {
        if (!accounts.containsKey(account.getId())) {
            throw new AccountNotFoundException(account.getId());
        }
        Account newAccount = new Account(account.getId(), account.getName(), newBalance);

        AtomicReference<Account> ref = accounts.get(account.getId());
        return ref.compareAndSet(account, newAccount);
    }
}
