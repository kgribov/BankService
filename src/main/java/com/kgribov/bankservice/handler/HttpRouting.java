package com.kgribov.bankservice.handler;

import com.kgribov.bankservice.json.JsonParser;
import com.kgribov.bankservice.repository.AccountRepository;
import com.kgribov.bankservice.repository.TransferRepository;
import com.kgribov.bankservice.service.LockFreeTransferService;
import com.kgribov.bankservice.service.MetricService;
import com.kgribov.bankservice.service.TransferService;
import io.undertow.Handlers;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.RedirectHandler;

public class HttpRouting {

    private static final String DOC_URL = "https://app.swaggerhub.com/apis/kirilkadurilka/bank-service/1.0.0";

    public static RoutingHandler createRouting() {
        JsonParser parser = new JsonParser();
        MetricService metricService = new MetricService();

        AccountRepository accountRepository = new AccountRepository();
        TransferRepository transferRepository = new TransferRepository();

        TransferService transferService = new LockFreeTransferService(accountRepository, transferRepository, metricService);

        return Handlers.routing()
                .get("/account/{id}", new AccountHandler(accountRepository, metricService, parser))

                .post("/account", new CreateAccountHandler(accountRepository, metricService, parser))

                .get("/transfer/{id}", new TransferHandler(transferRepository, parser))

                .post("/transfer", new CreateTransferHandler(transferService, parser))

                .get("/stats", new StatsHandler(metricService, parser))

                .get("/doc", new RedirectHandler(DOC_URL));
    }
}
