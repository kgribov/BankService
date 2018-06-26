package com.kgribov.bankservice.handler;

import com.kgribov.bankservice.dto.CreateAccountDTO;
import com.kgribov.bankservice.json.JsonParser;
import com.kgribov.bankservice.model.Account;
import com.kgribov.bankservice.repository.AccountRepository;
import com.kgribov.bankservice.service.MetricService;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static io.undertow.util.Headers.CONTENT_TYPE;

public class CreateAccountHandler implements HttpHandler {

    private final static Logger logger = LoggerFactory.getLogger(CreateAccountHandler.class);

    private final AccountRepository repository;
    private final MetricService metricService;
    private final JsonParser parser;

    public CreateAccountHandler(AccountRepository repository,
                                MetricService metricService,
                                JsonParser parser) {
        this.repository = repository;
        this.metricService = metricService;
        this.parser = parser;
    }

    @Override
    public void handleRequest(HttpServerExchange httpExchange) throws Exception {
        httpExchange.getRequestReceiver().receiveFullString((exchange, json) -> {
            try {
                CreateAccountDTO dto = parser.fromJson(json, CreateAccountDTO.class);

                Account account = repository.createAccount(dto.getName(), dto.getBalance().longValue());

                metricService.incrementAccountCount();

                exchange.setStatusCode(200);
                exchange.getResponseHeaders().put(CONTENT_TYPE, "application/json");
                exchange.getResponseSender().send(parser.toJson(account));

            } catch (IOException ex) {
                String errorMessage = "Unable to parse json: [" + json + "]";
                logger.error(errorMessage, ex);
                exchange.setStatusCode(400);
                exchange.getResponseSender().send(errorMessage);
            }
        });
    }
}
