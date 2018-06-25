package com.kgribov.bankservice.model;

public class Stats {
    private final Long acceptedCount;
    private final Long rejectedByShortOfMoney;
    private final Long rejectedByOverflow;
    private final Long rejectedByNegativeAmount;

    private final Long totalTransfers;
    private final Long transfersCount;

    private final Long accountCount;

    private final Long notFoundAccount;

    private final Long failedAccountUpdate;

    public Stats(Long acceptedCount,
                 Long rejectedByShortOfMoney,
                 Long rejectedByOverflow,
                 Long rejectedByNegativeAmount,
                 Long totalTransfers,
                 Long transfersCount,
                 Long accountCount,
                 Long notFoundAccount,
                 Long failedAccountUpdate) {

        this.acceptedCount = acceptedCount;
        this.rejectedByShortOfMoney = rejectedByShortOfMoney;
        this.rejectedByOverflow = rejectedByOverflow;
        this.rejectedByNegativeAmount = rejectedByNegativeAmount;
        this.totalTransfers = totalTransfers;
        this.transfersCount = transfersCount;
        this.accountCount = accountCount;
        this.notFoundAccount = notFoundAccount;
        this.failedAccountUpdate = failedAccountUpdate;
    }

    public Long getAcceptedCount() {
        return acceptedCount;
    }

    public Long getRejectedByShortOfMoney() {
        return rejectedByShortOfMoney;
    }

    public Long getRejectedByOverflow() {
        return rejectedByOverflow;
    }

    public Long getRejectedByNegativeAmount() {
        return rejectedByNegativeAmount;
    }

    public Long getTotalTransfers() {
        return totalTransfers;
    }

    public Long getTransfersCount() {
        return transfersCount;
    }

    public Long getAccountCount() {
        return accountCount;
    }

    public Long getNotFoundAccount() {
        return notFoundAccount;
    }

    public Long getFailedAccountUpdate() {
        return failedAccountUpdate;
    }

    @Override
    public String toString() {
        return "Stats{" +
                "acceptedCount=" + acceptedCount +
                ", rejectedByShortOfMoney=" + rejectedByShortOfMoney +
                ", rejectedByOverflow=" + rejectedByOverflow +
                ", rejectedByNegativeAmount=" + rejectedByNegativeAmount +
                ", totalTransfers=" + totalTransfers +
                ", transfersCount=" + transfersCount +
                ", accountCount=" + accountCount +
                ", notFoundAccount=" + notFoundAccount +
                ", failedAccountUpdate=" + failedAccountUpdate +
                '}';
    }
}
