package com.kgribov.bankservice.service;

import com.kgribov.bankservice.model.Transfer;
import com.kgribov.bankservice.repository.AccountNotFoundException;
import com.kgribov.bankservice.repository.AccountRepository;
import com.kgribov.bankservice.repository.TransferRepository;
import org.junit.Test;

import java.time.Clock;
import java.time.Instant;

import static com.kgribov.bankservice.model.Transfer.Status.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TransferServiceTest {

    private final static String ACCOUNT_CHANGED_REASON =
            "Account should be the same, but it was changed after transfer";
    private final static String ACCOUNT_NOT_CHANGED_REASON =
            "Account should be changed, but it wasn't";

    private final Long fromId = 0L;
    private final Long toId = 1L;
    private final Long timestamp = Instant.now().toEpochMilli();

    @Test
    public void onNegativeAmountShouldReject() throws AccountNotFoundException {
        Integer fromBalance = 1000;
        Integer toBalance = 1000;
        Integer amount = -100;

        AccountRepository accountRepository = createAccountRepo(fromBalance, toBalance);
        TransferService service = new TransferService(
            accountRepository,
            createTransferRepo(timestamp)
        );

        Transfer transfer = service.transfer(fromId, toId, amount);

        String reason = "Transfer with negative amount should be rejected";
        Transfer expectedTransfer = transfer(0L, amount, timestamp, REJECTED_BY_NEGATIVE_AMOUNT);
        assertThat(reason, transfer, equalTo(expectedTransfer));

        assertThat(
            ACCOUNT_CHANGED_REASON,
            accountRepository.getAccount(fromId).getBalance(),
            equalTo(fromBalance)
        );

        assertThat(
            ACCOUNT_CHANGED_REASON,
            accountRepository.getAccount(toId).getBalance(),
            equalTo(toBalance)
        );
    }

    @Test
    public void onPositiveAmountShouldAccept() throws AccountNotFoundException {
        Integer fromBalance = 1000;
        Integer toBalance = 1000;
        Integer amount = 100;

        AccountRepository accountRepository = createAccountRepo(fromBalance, toBalance);
        TransferService service = new TransferService(
            accountRepository,
            createTransferRepo(timestamp)
        );

        Transfer transfer = service.transfer(fromId, toId, amount);

        String reason = "Transfer with positive amount should be accepted";
        Transfer expectedTransfer = transfer(0L, amount, timestamp, ACCEPTED);
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

    @Test
    public void onShortOfMoneyShouldReject() throws AccountNotFoundException {
        Integer fromBalance = 1000;
        Integer toBalance = 1000;
        Integer amount = fromBalance + 100;

        AccountRepository accountRepository = createAccountRepo(fromBalance, toBalance);
        TransferService service = new TransferService(
            accountRepository,
            createTransferRepo(timestamp)
        );

        Transfer transfer = service.transfer(fromId, toId, amount);

        String reason = "If client don't have enough money - transfer should be rejected";
        Transfer expectedTransfer = transfer(0L, amount, timestamp, REJECTED_BY_SHORT_OF_MONEY);
        assertThat(reason, transfer, equalTo(expectedTransfer));

        assertThat(
                ACCOUNT_CHANGED_REASON,
                accountRepository.getAccount(fromId).getBalance(),
                equalTo(fromBalance)
        );

        assertThat(
                ACCOUNT_CHANGED_REASON,
                accountRepository.getAccount(toId).getBalance(),
                equalTo(toBalance)
        );
    }

    @Test
    public void onOverflowShouldReject() throws AccountNotFoundException {
        Integer fromBalance = 1000;
        Integer toBalance = Integer.MAX_VALUE;
        Integer amount = 100;

        AccountRepository accountRepository = createAccountRepo(fromBalance, toBalance);
        TransferService service = new TransferService(
            accountRepository,
            createTransferRepo(timestamp)
        );

        Transfer transfer = service.transfer(fromId, toId, amount);

        String reason = "If client have too much money on bill - transfer should be rejected";
        Transfer expectedTransfer = transfer(0L, amount, timestamp, REJECTED_BY_OVERFLOW);
        assertThat(reason, transfer, equalTo(expectedTransfer));

        assertThat(
                ACCOUNT_CHANGED_REASON,
                accountRepository.getAccount(fromId).getBalance(),
                equalTo(fromBalance)
        );

        assertThat(
                ACCOUNT_CHANGED_REASON,
                accountRepository.getAccount(toId).getBalance(),
                equalTo(toBalance)
        );
    }

    @Test(expected = AccountNotFoundException.class)
    public void onNotFoundAccountShouldThrow() throws AccountNotFoundException {
        Long notExistAccountId = 3L;

        Integer fromBalance = 1000;
        Integer toBalance = 100;
        Integer amount = 100;

        AccountRepository accountRepository = createAccountRepo(fromBalance, toBalance);
        TransferService service = new TransferService(
            accountRepository,
            createTransferRepo(timestamp)
        );

        service.transfer(notExistAccountId, toId, amount);
    }

    @Test
    public void secondTransferShouldBeAccepted() throws AccountNotFoundException {
        Integer fromBalance = 1000;
        Integer toBalance = 100;
        Integer firstAmount = 100;
        Integer secondAmount = 200;

        AccountRepository accountRepository = createAccountRepo(fromBalance, toBalance);
        TransferService service = new TransferService(
            accountRepository,
            createTransferRepo(timestamp)
        );

        service.transfer(fromId, toId, firstAmount);

        Transfer transfer = service.transfer(fromId, toId, secondAmount);

        String reason = "If client is making second positive transfer - transfer should be accepted";
        Transfer expectedTransfer = transfer(1L, secondAmount, timestamp, ACCEPTED);
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

    private Transfer transfer(Long id, Integer amount, Long timestamp, Transfer.Status status) {
        return new Transfer(id, fromId, toId, amount, timestamp, status);
    }

    private AccountRepository createAccountRepo(Integer fromBalance, Integer toBalance) {
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
