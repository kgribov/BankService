package com.kgribov.bankservice.service;

import com.kgribov.bankservice.model.Account;
import com.kgribov.bankservice.model.Transfer;
import com.kgribov.bankservice.repository.AccountNotFoundException;
import com.kgribov.bankservice.repository.AccountRepository;
import com.kgribov.bankservice.repository.TransferRepository;

import static com.kgribov.bankservice.model.Transfer.Status.*;

/**
 * Lock free implementation of transfer service, based on updateBalance method of AccountRepository
 */
public class LockFreeTransferService implements TransferService {

    private final AccountRepository accountRepository;
    private final TransferRepository transferRepository;
    private final MetricService metricService;

    public LockFreeTransferService(AccountRepository accountRepository,
                                   TransferRepository transferRepository) {
        this(accountRepository, transferRepository, new MetricService());
    }

    public LockFreeTransferService(AccountRepository accountRepository,
                                   TransferRepository transferRepository,
                                   MetricService metricService) {
        this.accountRepository = accountRepository;
        this.transferRepository = transferRepository;
        this.metricService = metricService;
    }

    public Transfer transfer(Long fromId,
                             Long toId,
                             Integer amount) throws AccountNotFoundException {

        if (amount < 0) {
            return transferRepository.addTransfer(
                    fromId, toId, amount, REJECTED_BY_NEGATIVE_AMOUNT
            );
        }

        Transfer transfer = processTransfer(fromId, toId, amount);
        metricService.collectTransfer(transfer.getStatus(), transfer.getAmount());
        return transfer;
    }

    private Transfer processTransfer(Long fromId, Long toId, Integer amount) throws AccountNotFoundException {
        if (!chargeAccount(fromId, amount)) {
            return transferRepository.addTransfer(
                fromId, toId, amount, REJECTED_BY_SHORT_OF_MONEY
            );
        }

        if (!rechargeAccount(toId, amount)) {
            rollbackCharge(fromId, amount);
            return transferRepository.addTransfer(
                fromId, toId, amount, REJECTED_BY_OVERFLOW
            );
        }

        return transferRepository.addTransfer(
                fromId, toId, amount, ACCEPTED
        );
    }

    private boolean chargeAccount(Long fromId, Integer amount) throws AccountNotFoundException {
        while (true) {
            Account from = accountRepository.getAccount(fromId);
            if (from.getBalance() - amount < 0) {
                break;
            } else {
                int newBalance = from.getBalance() - amount;
                if (accountRepository.updateBalance(from, newBalance)) {
                    return true;
                } else {
                    metricService.incrementFailedUpdate();
                }
            }
        }

        return false;
    }

    private boolean rechargeAccount(Long toId, Integer amount) throws AccountNotFoundException {
        while (true) {
            Account to = accountRepository.getAccount(toId);
            if (to.getBalance().longValue() + amount > Integer.MAX_VALUE) {
                break;
            } else {
                int newBalance = to.getBalance() + amount;
                if (accountRepository.updateBalance(to, newBalance)) {
                    return true;
                } else {
                    metricService.incrementFailedUpdate();
                }
            }
        }

        return false;
    }

    private void rollbackCharge(Long accountId, Integer amount) throws AccountNotFoundException {
        while (true) {
            Account account = accountRepository.getAccount(accountId);
            if (!accountRepository.updateBalance(account, account.getBalance() + amount)) {
                metricService.incrementFailedUpdate();
            } else {
                break;
            }
        }
    }
}
