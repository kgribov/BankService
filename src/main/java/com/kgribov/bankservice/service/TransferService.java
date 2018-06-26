package com.kgribov.bankservice.service;

import com.kgribov.bankservice.model.Transfer;
import com.kgribov.bankservice.service.exception.TransferException;

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
