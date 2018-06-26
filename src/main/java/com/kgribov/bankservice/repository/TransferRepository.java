package com.kgribov.bankservice.repository;

import com.kgribov.bankservice.model.Transfer;

import java.time.Clock;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class TransferRepository {

    private final AtomicLong idProvider = new AtomicLong();
    private final Map<Long, Transfer> transfers = new ConcurrentHashMap<>();

    private final Clock clock;

    public TransferRepository() {
        this( Clock.systemDefaultZone());
    }

    public TransferRepository(Clock clock) {
        this.clock = clock;
    }

    public Transfer addTransfer(Long fromId, Long toId, Integer amount) {
        Long timestamp = clock.millis();
        Long transferId = idProvider.getAndIncrement();

        Transfer transfer = new Transfer(transferId, fromId, toId, amount, timestamp);

        transfers.put(transferId, transfer);

        return transfer;
    }

    public Optional<Transfer> getTransfer(Long id) {
        Transfer transfer = transfers.get(id);
        return Optional.ofNullable(transfer);
    }
}
