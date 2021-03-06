package com.kgribov.bankservice.repository;

import com.kgribov.bankservice.model.Account;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;

public class AccountRepositoryTest {

    @Test
    public void onCreateReturnAccountWithNewId() {
        AccountRepository repository = new AccountRepository();
        Long firstId = repository.createAccount("Bill", 0L).getId();
        Long secondId = repository.createAccount("Gates", 0L).getId();

        assertThat("Repository should create account with different id", firstId, not(secondId));
    }

    @Test
    public void onGetShouldReturnExistAccount() throws AccountNotFoundException {
        AccountRepository repository = new AccountRepository();
        String accountName = "Bill";
        Long accountBalance = 0L;
        Long accountId = repository.createAccount("Bill", 0L).getId();

        Account account = repository.getAccount(accountId);

        assertThat(
            "Repository should return existing account",
                account,
                equalTo(new Account(accountId, accountName, accountBalance))
        );
    }

    @Test(expected = AccountNotFoundException.class)
    public void onGetNonExistAccShouldThrow() throws AccountNotFoundException {
        AccountRepository repository = new AccountRepository();

        Long notExistAccId = 1L;
        Account notExistAccount = repository.getAccount(notExistAccId);
    }

    @Test
    public void onUpdateShouldChangeBalance() throws AccountNotFoundException {
        AccountRepository repository = new AccountRepository();
        String accountName = "Bill";
        Long accountId = repository.createAccount(accountName, 0L).getId();

        Long newBalance = 1000L;
        repository.updateBalance(repository.getAccount(accountId), newBalance);

        assertThat(
            "Repository should change balance on update",
                repository.getAccount(accountId),
                equalTo(new Account(accountId, accountName, newBalance))
        );
    }

    @Test
    public void onConcurrentUpdateShouldNotChangeBalance() throws AccountNotFoundException {
        AccountRepository repository = new AccountRepository();
        String accountName = "Bill";
        Long accountId = repository.createAccount(accountName, 0L).getId();

        Account account = repository.getAccount(accountId);

        Long firstChangeBalance = 1000L;
        repository.updateBalance(account, firstChangeBalance);

        Long secondChangeBalance = 10L;
        repository.updateBalance(account, secondChangeBalance);

        assertThat(
            "Repository should ignore change on old data",
            repository.getAccount(accountId),
            equalTo(new Account(accountId, accountName, firstChangeBalance))
        );
    }
}
