package com.kgribov.bankservice.service.exception;

public class ShortOfMoneyTransferException extends TransferException {

    public ShortOfMoneyTransferException(Long accountId) {
        super("Not enough money for transfer on account: " + accountId);
    }
}
