package com.kgribov.bankservice.service;

import com.kgribov.bankservice.model.Transfer;
import com.kgribov.bankservice.repository.AccountNotFoundException;

public interface TransferService {

    Transfer transfer(Long fromId, Long toId, Integer amount) throws AccountNotFoundException;
}
