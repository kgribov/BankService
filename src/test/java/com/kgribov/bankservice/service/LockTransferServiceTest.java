package com.kgribov.bankservice.service;

import com.kgribov.bankservice.model.Account;
import com.kgribov.bankservice.model.Transfer;
import com.kgribov.bankservice.repository.AccountNotFoundException;
import com.kgribov.bankservice.repository.AccountRepository;
import com.kgribov.bankservice.repository.TransferRepository;
import com.kgribov.bankservice.service.exception.NegativeAmountTransferException;
import com.kgribov.bankservice.service.exception.OverflowTransferException;
import com.kgribov.bankservice.service.exception.ShortOfMoneyTransferException;
import com.kgribov.bankservice.service.exception.TransferException;
import org.junit.Test;

import java.time.Clock;
import java.time.Instant;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LockTransferServiceTest {

    private final static String ACCOUNT_NOT_CHANGED_REASON =
            "Account should be changed, but it wasn't";

    private final Long fromId = 0L;
    private final Long toId = 1L;
    private final Long timestamp = Instant.now().toEpochMilli();

    @Test(expected = NegativeAmountTransferException.class)
    public void onNegativeAmountShouldReject() throws TransferException {
        Long fromBalance = 1000L;
        Long toBalance = 1000L;
        Integer amount = -100;

        AccountRepository accountRepository = createAccountRepo(fromBalance, toBalance);
        TransferService service = new LockTransferService(
                accountRepository,
                createTransferRepo(timestamp)
        );

        service.transfer(fromId, toId, amount);
    }

    @Test
    public void onPositiveAmountShouldAccept() throws AccountNotFoundException, TransferException {
        Long fromBalance = 1000L;
        Long toBalance = 1000L;
        Integer amount = 100;

        AccountRepository accountRepository = createAccountRepo(fromBalance, toBalance);
        TransferService service = new LockTransferService(
                accountRepository,
                createTransferRepo(timestamp)
        );

        Transfer transfer = service.transfer(fromId, toId, amount);

        String reason = "Transfer with positive amount should be accepted";
        Transfer expectedTransfer = transfer(0L, amount, timestamp);
        assertThat(reason, transfer, equalTo(expectedTransfer));

        assertThat(
                ACCOUNT_NOT_CHANGED_REASON,
                accountRepository.getAccount(fromId).getBalance(),
                equalTo(fromBalance - amount)
        );

        assertThat(
                ACCOUNT_NOT_CHANGED_REASON,
                accountRepository.getAccount(toId).getBalance(),
                equalTo(toBalance + amount)
        );
    }

    @Test(expected = ShortOfMoneyTransferException.class)
    public void onShortOfMoneyShouldReject() throws TransferException {
        Long fromBalance = 1000L;
        Long toBalance = 1000L;
        Integer amount = fromBalance.intValue() + 100;

        AccountRepository accountRepository = createAccountRepo(fromBalance, toBalance);
        TransferService service = new LockTransferService(
                accountRepository,
                createTransferRepo(timestamp)
        );

        service.transfer(fromId, toId, amount);
    }

    @Test(expected = OverflowTransferException.class)
    public void onOverflowShouldReject() throws TransferException {
        Long fromBalance = 1000L;
        Long toBalance = Account.ACCOUNT_MAX_BALANCE.longValue();
        Integer amount = 100;

        AccountRepository accountRepository = createAccountRepo(fromBalance, toBalance);
        TransferService service = new LockTransferService(
                accountRepository,
                createTransferRepo(timestamp)
        );

        service.transfer(fromId, toId, amount);
    }

    @Test(expected = TransferException.class)
    public void onNotFoundAccountShouldThrow() throws TransferException {
        Long notExistAccountId = 3L;

        Long fromBalance = 1000L;
        Long toBalance = 100L;
        Integer amount = 100;

        AccountRepository accountRepository = createAccountRepo(fromBalance, toBalance);
        TransferService service = new LockTransferService(
                accountRepository,
                createTransferRepo(timestamp)
        );

        service.transfer(notExistAccountId, toId, amount);
    }

    @Test
    public void secondTransferShouldBeAccepted() throws AccountNotFoundException, TransferException {
        Long fromBalance = 1000L;
        Long toBalance = 100L;
        Integer firstAmount = 100;
        Integer secondAmount = 200;

        AccountRepository accountRepository = createAccountRepo(fromBalance, toBalance);
        TransferService service = new LockTransferService(
                accountRepository,
                createTransferRepo(timestamp)
        );

        service.transfer(fromId, toId, firstAmount);

        Transfer transfer = service.transfer(fromId, toId, secondAmount);

        String reason = "If client is making second positive transfer - transfer should be accepted";
        Transfer expectedTransfer = transfer(1L, secondAmount, timestamp);
        assertThat(reason, transfer, equalTo(expectedTransfer));

        assertThat(
                ACCOUNT_NOT_CHANGED_REASON,
                accountRepository.getAccount(fromId).getBalance(),
                equalTo(fromBalance - firstAmount - secondAmount)
        );

        assertThat(
                ACCOUNT_NOT_CHANGED_REASON,
                accountRepository.getAccount(toId).getBalance(),
                equalTo(toBalance + firstAmount + secondAmount)
        );
    }

    private Transfer transfer(Long id, Integer amount, Long timestamp) {
        return new Transfer(id, fromId, toId, amount, timestamp);
    }

    private AccountRepository createAccountRepo(Long fromBalance, Long toBalance) {
        AccountRepository repository = new AccountRepository();
        repository.createAccount("Elon Musk", fromBalance);
        repository.createAccount("Bill Gates", toBalance);
        return repository;
    }

    private TransferRepository createTransferRepo(Long timestamp) {
        Clock clock = mock(Clock.class);
        when(clock.millis()).thenReturn(timestamp);
        return new TransferRepository(clock);
    }
}
