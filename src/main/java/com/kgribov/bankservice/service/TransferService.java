package com.kgribov.bankservice.service;

import com.kgribov.bankservice.model.Transfer;
import com.kgribov.bankservice.repository.AccountNotFoundException;

public interface TransferService {

    /**
     * Transfer some amount of money from one account to another
     * @param fromId account id transfer from
     * @param toId account id transfer to
     * @param amount amount of money to transfer
     * @return transfer object (successful or not)
     * @throws AccountNotFoundException account not found exception
     */
    Transfer transfer(Long fromId, Long toId, Integer amount) throws AccountNotFoundException;
}
