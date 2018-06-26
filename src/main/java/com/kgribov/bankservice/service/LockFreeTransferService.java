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
                             Integer amount) throws TransferException {
        if (amount < 0) {
            metricService.incrementNegativeAmountTransfers();
            throw new NegativeAmountTransferException(amount);
        }

        Transfer transfer = processTransfer(fromId, toId, amount);
        metricService.incrementAcceptedTransfers();
        return transfer;
    }

    private Transfer processTransfer(Long fromId, Long toId, Integer amount) throws TransferException {
        updateBalance(fromId, amount, debitUpdater());

        try {
            updateBalance(toId, amount, creditUpdater());

        } catch (TransferException ex) {
            updateBalance(fromId, amount, creditUpdater(false));
            throw ex;
        }

        return transferRepository.addTransfer(fromId, toId, amount);
    }

    private BalanceUpdater debitUpdater()  {
        return (account, amount) -> {
            Long newBalance = account.getBalance() - amount;
            if (notEnoughMoneyForTransfer(newBalance)) {
                metricService.incrementShortOfMoneyTransfers();
                throw new ShortOfMoneyTransferException(account.getId());
            }
            return newBalance;
        };
    }

    private BalanceUpdater creditUpdater() {
        return creditUpdater(true);
    }

    private BalanceUpdater creditUpdater(boolean checkLimit) {
        return (account, amount) -> {
            Long newBalance = account.getBalance() + amount;
            if (checkLimit && outOfLimit(newBalance)) {
                metricService.incrementOverflowTransfers();
                throw new OverflowTransferException(account.getId());
            }
            return newBalance;
        };
    }

    private void updateBalance(Long id, Integer amount, BalanceUpdater updater) throws TransferException {
        try {
            while (true) {
                Account to = accountRepository.getAccount(id);
                Long newBalance = updater.updateBalance(to, amount);

                if (accountRepository.updateBalance(to, newBalance)) {
                    break;
                } else {
                    metricService.incrementFailedUpdate();
                }
            }

        } catch (AccountNotFoundException ex) {
            throw new TransferException(ex);
        }
    }

    private boolean outOfLimit(Long newBalance) {
        return newBalance > Account.ACCOUNT_MAX_BALANCE;
    }

    private boolean notEnoughMoneyForTransfer(Long newBalance) {
        return newBalance < 0;
    }

    private interface BalanceUpdater {
        Long updateBalance(Account account, Integer amount) throws TransferException;
    }
}
