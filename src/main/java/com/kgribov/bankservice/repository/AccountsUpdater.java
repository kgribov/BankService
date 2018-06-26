package com.kgribov.bankservice.repository;

import com.kgribov.bankservice.model.Account;

public interface AccountsUpdater<R>{

    UpdaterResult<R> updateAccounts(Account from, Account to) throws Exception;
}
