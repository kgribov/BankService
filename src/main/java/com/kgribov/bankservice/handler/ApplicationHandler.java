package com.kgribov.bankservice.handler;

import com.kgribov.bankservice.json.JsonParser;
import com.kgribov.bankservice.repository.AccountRepository;
import com.kgribov.bankservice.repository.TransferRepository;
import com.kgribov.bankservice.service.FreeLockTransferService;
import com.kgribov.bankservice.service.MetricService;
import com.kgribov.bankservice.service.TransferService;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.RedirectHandler;

import java.nio.file.Files;
import java.nio.file.Paths;


public class ApplicationHandler {

    private static final String DOC_URL = "https://app.swaggerhub.com/apis/kirilkadurilka/bank-service/1.0.0";

    public static RoutingHandler create() {
        JsonParser parser = new JsonParser();
        MetricService metricService = new MetricService();

        AccountRepository accountRepository = new AccountRepository();
        TransferRepository transferRepository = new TransferRepository();

        TransferService transferService = new FreeLockTransferService(accountRepository, transferRepository, metricService);

        return Handlers.routing()
                .get("/account/{id}", new AccountHandler(accountRepository, metricService, parser))

                .post("/account", new CreateAccountHandler(accountRepository, metricService, parser))

                .get("/transfer/{id}", new TransferHandler(transferRepository, parser))

                .post("/transfer", new CreateTransferHandler(transferService, parser))

                .get("/stats", new StatsHandler(metricService, parser))

                .get("/doc", new RedirectHandler(DOC_URL))

                .get("/swagger.yaml", swaggerFile());
    }

    private static HttpHandler swaggerFile() {
        Class clazz = ApplicationHandler.class;
        String path = clazz.getClassLoader().getResource("swagger/swagger.yaml").getPath();
        return (exchange) -> exchange.getResponseSender().send(new String(Files.readAllBytes(Paths.get(path))));
    }
}
