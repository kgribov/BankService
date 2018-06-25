package com.kgribov.bankservice.handler;

import com.kgribov.bankservice.json.JsonParser;
import com.kgribov.bankservice.model.Transfer;
import com.kgribov.bankservice.repository.TransferRepository;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.Optional;

import static io.undertow.util.Headers.CONTENT_TYPE;

public class TransferHandler implements HttpHandler {

    private final static Logger logger = LoggerFactory.getLogger(TransferHandler.class);
    private final static String TRANSFER_ID_PARAM = "id";

    private final TransferRepository repository;
    private final JsonParser parser;

    public TransferHandler(TransferRepository repository, JsonParser parser) {
        this.repository = repository;
        this.parser = parser;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        try {
            Long transferId = parseId(Optional.ofNullable(exchange.getQueryParameters().get(TRANSFER_ID_PARAM))
                    .map(Deque::peekFirst)
                    .orElseThrow(() -> new InvalidTransferIdException(null)));

            Optional<Transfer> transfer = repository.getTransfer(transferId);

            if (transfer.isPresent()) {
                exchange.getResponseHeaders().put(CONTENT_TYPE, "application/json");
                exchange.getResponseSender().send(parser.toJson(transfer.get()));
                logger.debug("New transfer - " + transfer);

            } else {
                String message = "Unable to get transfer with id=[" + transferId + "]";
                logger.debug(message);

                exchange.setStatusCode(404);
                exchange.getResponseSender().send(message);
            }
        } catch (InvalidTransferIdException ex) {
            logger.error(ex.getMessage(), ex);
            exchange.setStatusCode(400);
            exchange.getResponseSender().send(ex.getMessage());
        }
    }

    private Long parseId(String paramSt) throws InvalidTransferIdException {
        try {
            return Long.parseLong(paramSt);
        } catch (Exception ex) {
            throw new InvalidTransferIdException(paramSt);
        }
    }

    private static class InvalidTransferIdException extends Exception {
        InvalidTransferIdException(String id) {
            super("[" + id + "] is invalid Id for transfer service");
        }
    }
}
