package com.kgribov.bankservice.repository;

import com.kgribov.bankservice.model.Account;

public interface AccountsUpdater<R>{

    /**
     * Make update on two accounts safely and return some result
     * @param from account
     * @param to account
     * @return updater result with new Accounts and user specific value
     * @throws Exception
     */
    UpdaterResult<R> updateAccounts(Account from, Account to) throws Exception;
}
