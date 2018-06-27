package com.kgribov.bankservice.service;

import com.kgribov.bankservice.model.Stats;

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

    public void incrementAcceptedTransfers(Integer amount) {
        acceptedCount.incrementAndGet();
        totalTransfersAmounts.addAndGet(amount);
        transfersCount.incrementAndGet();
    }

    public void incrementOverflowTransfers() {
        rejectedByOverflowCount.incrementAndGet();
    }

    public void incrementShortOfMoneyTransfers() {
        rejectedByShortOfMoneyCount.incrementAndGet();
    }

    public void incrementNegativeAmountTransfers() {
        rejectedByNegativeAmountCount.incrementAndGet();
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
