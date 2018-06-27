package com.kgribov.bankservice.service;

import com.kgribov.bankservice.model.Transfer;
import com.kgribov.bankservice.service.exception.TransferException;

/**
 * A service for money transfers between account.
 * There are two implementations:
 * 1) LockTransferService - based on synchronized methods in lockAndUpdate method of AccountRepository class
 * 2) LockFreeTransferService - based on AtomicReference and CAS operation in updateBalance method
 */
public interface TransferService {

    /**
     * Transfer some amount of money from one account to another
     * @param fromId account id transfer from
     * @param toId account id transfer to
     * @param amount amount of money to transfer
     * @return transfer object (successful or not)
     * @throws TransferException failed to process transfer
     */
    Transfer transfer(Long fromId, Long toId, Integer amount) throws TransferException;
}
