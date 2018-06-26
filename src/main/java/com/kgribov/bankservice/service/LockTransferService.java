package com.kgribov.bankservice.service;

import com.kgribov.bankservice.model.Account;
import com.kgribov.bankservice.model.Transfer;
import com.kgribov.bankservice.repository.AccountNotFoundException;
import com.kgribov.bankservice.repository.AccountRepository;
import com.kgribov.bankservice.repository.TransactionResult;
import com.kgribov.bankservice.repository.TransferRepository;

import static com.kgribov.bankservice.model.Transfer.Status.*;

/**
 * Lock-based implementation of TransferService, based on transaction method of AccountRepository
 */
public class LockTransferService implements TransferService {

    private final AccountRepository accountRepository;
    private final TransferRepository transferRepository;
    private final MetricService metricService;

    public LockTransferService(AccountRepository accountRepository,
                                   TransferRepository transferRepository) {
        this(accountRepository, transferRepository, new MetricService());
    }

    public LockTransferService(AccountRepository accountRepository,
                                   TransferRepository transferRepository,
                                   MetricService metricService) {
        this.accountRepository = accountRepository;
        this.transferRepository = transferRepository;
        this.metricService = metricService;
    }

    @Override
    public Transfer transfer(Long fromId, Long toId, Integer amount) throws AccountNotFoundException {
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
        return accountRepository.transaction(fromId, toId, (from, to) -> {
            if (from.getBalance() - amount < 0) {
                return new TransactionResult<>(
                    from,
                    to,
                    transferRepository.addTransfer(fromId, toId, amount, REJECTED_BY_SHORT_OF_MONEY)
                );
            }
            if (to.getBalance().longValue() + amount > Integer.MAX_VALUE) {
                return new TransactionResult<>(
                    from,
                    to,
                    transferRepository.addTransfer(fromId, toId, amount, REJECTED_BY_OVERFLOW)
                );
            }

            Account updatedFrom = new Account(fromId, from.getName(), from.getBalance() - amount);
            Account updatedTo = new Account(toId, to.getName(), to.getBalance() + amount);
            return new TransactionResult<>(
                updatedFrom,
                updatedTo,
                transferRepository.addTransfer(fromId, toId, amount, ACCEPTED)
            );
        });
    }
}
