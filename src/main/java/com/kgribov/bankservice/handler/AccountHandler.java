package com.kgribov.bankservice.handler;

import com.kgribov.bankservice.json.JsonParser;
import com.kgribov.bankservice.model.Account;
import com.kgribov.bankservice.repository.AccountNotFoundException;
import com.kgribov.bankservice.repository.AccountRepository;
import com.kgribov.bankservice.service.MetricService;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.Optional;

import static io.undertow.util.Headers.CONTENT_TYPE;
import static java.nio.charset.StandardCharsets.UTF_8;

public class AccountHandler implements HttpHandler {

    private final static Logger logger = LoggerFactory.getLogger(AccountHandler.class);
    private final static String ACCOUNT_ID_PARAM = "id";

    private final AccountRepository repository;
    private final MetricService metricService;
    private final JsonParser parser;

    public AccountHandler(AccountRepository repository,
                          MetricService metricService,
                          JsonParser parser) {
        this.repository = repository;
        this.metricService = metricService;
        this.parser = parser;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        try {
            Long accountId = parseId(Optional.ofNullable(exchange.getQueryParameters().get(ACCOUNT_ID_PARAM))
                    .map(Deque::peekFirst)
                    .orElseThrow(() -> new InvalidAccountIdException(null)));

            Account account = repository.getAccount(accountId);

            exchange.getResponseHeaders().put(CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(parser.toJson(account));

        } catch (AccountNotFoundException ex) {
            logger.error("Unable to get account by Id", ex);
            exchange.setStatusCode(404);
            exchange.getResponseSender().send(ex.getMessage(), UTF_8);

            metricService.incrementAccountNotFound();
        } catch (InvalidAccountIdException ex) {
            logger.error("Unable to parse account id", ex);
            exchange.setStatusCode(400);
            exchange.getResponseSender().send(ex.getMessage(), UTF_8);
        }
    }

    private Long parseId(String paramSt) throws InvalidAccountIdException {
        try {
            return Long.parseLong(paramSt);
        } catch (Exception ex) {
            throw new InvalidAccountIdException(paramSt);
        }
    }

    private static class InvalidAccountIdException extends Exception {

        InvalidAccountIdException(String invalidId) {
            super("[" + invalidId + "] is invalid id for account service");
        }
    }
}
