package com.kgribov.bankservice.service;

import com.kgribov.bankservice.model.Account;
import com.kgribov.bankservice.model.Transfer;
import com.kgribov.bankservice.repository.AccountRepository;
import com.kgribov.bankservice.repository.UpdaterResult;
import com.kgribov.bankservice.repository.TransferRepository;
import com.kgribov.bankservice.service.exception.NegativeAmountTransferException;
import com.kgribov.bankservice.service.exception.OverflowTransferException;
import com.kgribov.bankservice.service.exception.ShortOfMoneyTransferException;
import com.kgribov.bankservice.service.exception.TransferException;

/**
 * Lock-based implementation of TransferService, based on lockAndUpdate method of AccountRepository
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
    public Transfer transfer(Long fromId, Long toId, Integer amount) throws TransferException {
        if (amount < 0) {
            metricService.incrementNegativeAmountTransfers();
            throw new NegativeAmountTransferException(amount);
        }

        Transfer transfer = processTransfer(fromId, toId, amount);
        metricService.incrementAcceptedTransfers();
        return transfer;
    }

    private Transfer processTransfer(Long fromId, Long toId, Integer amount) throws TransferException {
        try {
            Transfer transfer = accountRepository.lockAndUpdate(fromId, toId, (from, to) -> {
                Long updatedFromBalance = from.getBalance() - amount;
                Long updatedToBalance = to.getBalance() + amount;

                if (notEnoughMoneyForTransfer(updatedFromBalance)) {
                    metricService.incrementShortOfMoneyTransfers();
                    throw new ShortOfMoneyTransferException(fromId);
                }

                if (outOfLimit(updatedToBalance)) {
                    metricService.incrementOverflowTransfers();
                    throw new OverflowTransferException(toId);
                }

                Account updatedFrom = new Account(fromId, from.getName(), updatedFromBalance);
                Account updatedTo = new Account(toId, to.getName(), updatedToBalance);

                return new UpdaterResult<>(
                    updatedFrom,
                    updatedTo,
                    transferRepository.addTransfer(fromId, toId, amount)
                );
            });

            metricService.incrementAcceptedTransfers();
            return transfer;
        } catch (TransferException e) {
            throw e;
        } catch (Exception e) {
            throw new TransferException(e);
        }
    }

    private boolean outOfLimit(Long newBalance) {
        return newBalance > Account.ACCOUNT_MAX_BALANCE;
    }

    private boolean notEnoughMoneyForTransfer(Long newBalance) {
        return newBalance < 0;
    }
}
