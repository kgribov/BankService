package com.kgribov.bankservice.service;

import com.kgribov.bankservice.model.Stats;
import com.kgribov.bankservice.model.Transfer;

import java.util.concurrent.atomic.AtomicLong;

public class MetricService {

    private final AtomicLong acceptedCount = new AtomicLong();
    private final AtomicLong rejectedByShortOfMoneyCount = new AtomicLong();
    private final AtomicLong rejectedByOverflowCount = new AtomicLong();
    private final AtomicLong rejectedByNegativeAmountCount = new AtomicLong();

    private final AtomicLong totalTransfersAmounts = new AtomicLong();
    private final AtomicLong transfersCount = new AtomicLong();

    private final AtomicLong accountsCount = new AtomicLong();

    private final AtomicLong notFoundAccount = new AtomicLong();

    private final AtomicLong failedAccountUpdate = new AtomicLong();

    public void collectTransfer(Transfer.Status status, Integer amount) {
        transfersCount.incrementAndGet();
        collectStatus(status);
        addAmount(amount);
    }

    private void collectStatus(Transfer.Status status) {
        switch (status) {
            case ACCEPTED:
                acceptedCount.incrementAndGet();
                break;
            case REJECTED_BY_OVERFLOW:
                rejectedByOverflowCount.incrementAndGet();
                break;
            case REJECTED_BY_SHORT_OF_MONEY:
                rejectedByShortOfMoneyCount.incrementAndGet();
                break;
            case REJECTED_BY_NEGATIVE_AMOUNT:
                rejectedByNegativeAmountCount.incrementAndGet();
                break;
        }
    }

    private void addAmount(Integer amount) {
        totalTransfersAmounts.addAndGet(amount);
    }

    public void incrementAccountCount() {
        accountsCount.incrementAndGet();
    }

    public void incrementFailedUpdate() {
        this.failedAccountUpdate.incrementAndGet();
    }

    public void incrementAccountNotFound() {
        this.notFoundAccount.incrementAndGet();
    }

    public Stats getStats() {
        return new Stats(
            acceptedCount.get(),
            rejectedByShortOfMoneyCount.get(),
            rejectedByOverflowCount.get(),
            rejectedByNegativeAmountCount.get(),
            totalTransfersAmounts.get(),
            transfersCount.get(),
            accountsCount.get(),
            notFoundAccount.get(),
            failedAccountUpdate.get());
    }
}
