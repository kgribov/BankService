package com.kgribov.bankservice.service.exception;

public class OverflowTransferException extends TransferException {

    public OverflowTransferException(Long accountId) {
        super("Account with id=" + accountId + " reached limits");
    }
}
