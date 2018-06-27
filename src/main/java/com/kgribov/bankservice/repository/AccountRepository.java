package com.kgribov.bankservice.repository;

import com.kgribov.bankservice.model.Account;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class AccountRepository {

    private final AtomicLong idProvider = new AtomicLong();
    private final Map<Long, AtomicReference<Account>> accounts = new ConcurrentHashMap<>(10_000);

    /**
     * Create new account in repository
     * @param name account's name
     * @param startBalance balance of account
     * @return created account in repo
     */
    public Account createAccount(String name, Long startBalance) {
        Long id = idProvider.getAndIncrement();
        Account account = new Account(id, name, startBalance);
        accounts.put(id, new AtomicReference<>(account));

        return account;
    }

    /**
     * Returns account by id number
     * @param id account id
     * @return Account by id
     * @throws AccountNotFoundException account not found
     */
    public Account getAccount(Long id) throws AccountNotFoundException {
        AtomicReference<Account> account = getReference(id);
        return account.get();
    }

    /**
     * Method to update balances of two accounts using lock mechanism.
     * @param fromId id of first account
     * @param toId id of second account
     * @param accountsUpdater object to safely update two accounts and return user specific result
     * @param <R> result of your accountsUpdater
     * @return user's result based on processor result
     * @throws Exception which could throw user's updater
     */
    public<R> R lockAndUpdate(Long fromId, Long toId, AccountsUpdater<R> accountsUpdater) throws Exception {
        synchronized (getReference(Long.max(fromId, toId))) {
            synchronized (getReference(Long.min(fromId, toId))) {

                Account from = getReference(fromId).get();
                Account to = getReference(toId).get();

                UpdaterResult<R> result = accountsUpdater.updateAccounts(from, to);

                accounts.get(fromId).set(result.getUpdatedFrom());
                accounts.get(toId).set(result.getUpdatedTo());

                return result.getResult();
            }
        }
    }

    /**
     * Method to update balance of some account without blocking.
     * If someone changed account before your update, updateBalance will return false.
     * @param account account to update
     * @param newBalance new balance will be set
     * @return true if update was successful and false if not
     * @throws AccountNotFoundException account with id not found
     */
    public boolean updateBalance(Account account, Long newBalance) throws AccountNotFoundException {
        Account newAccount = new Account(account.getId(), account.getName(), newBalance);

        AtomicReference<Account> ref = getReference(account.getId());
        return ref.compareAndSet(account, newAccount);
    }

    private AtomicReference<Account> getReference(Long id) throws AccountNotFoundException {
        AtomicReference<Account> accountRef = accounts.get(id);
        if (accountRef == null) {
            throw new AccountNotFoundException(id);
        }
        return accountRef;
    }
}
