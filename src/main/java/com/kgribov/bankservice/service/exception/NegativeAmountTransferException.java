package com.kgribov.bankservice.service.exception;

public class NegativeAmountTransferException extends TransferException {

    public NegativeAmountTransferException(Integer amount) {
        super("Unable to make transfer with negative amount: " + amount);
    }
}
