package com.kgribov.bankservice.handler;

import com.kgribov.bankservice.dto.CreateTransferDTO;
import com.kgribov.bankservice.json.JsonParser;
import com.kgribov.bankservice.model.Transfer;
import com.kgribov.bankservice.model.Transfer.Status;
import com.kgribov.bankservice.repository.AccountNotFoundException;
import com.kgribov.bankservice.service.FreeLockTransferService;
import com.kgribov.bankservice.service.TransferService;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static io.undertow.util.Headers.CONTENT_TYPE;

public class CreateTransferHandler implements HttpHandler {

    private final static Logger logger = LoggerFactory.getLogger(CreateTransferHandler.class);

    private final TransferService service;
    private final JsonParser parser;

    public CreateTransferHandler(TransferService service, JsonParser parser) {
        this.service = service;
        this.parser = parser;
    }

    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        httpServerExchange.getRequestReceiver().receiveFullString(
            (exchange, json) -> {
                try {
                    CreateTransferDTO createTransferDTO = parser.fromJson(json, CreateTransferDTO.class);
                    Transfer transfer = service.transfer(
                        createTransferDTO.getFromId(),
                        createTransferDTO.getToId(),
                        createTransferDTO.getAmount()
                    );

                    if (transfer.getStatus() != Status.ACCEPTED) {
                        exchange.setStatusCode(406);
                    }
                    exchange.getResponseHeaders().put(CONTENT_TYPE, "application/json");
                    exchange.getResponseSender().send(parser.toJson(transfer));

                } catch (IOException e) {
                    String errorMessage = "Unable to parse json [" + json + "]";
                    logger.error(errorMessage, e);
                    exchange.setStatusCode(400);
                    exchange.getResponseSender().send(errorMessage);

                } catch (AccountNotFoundException e) {
                    logger.error("Unable to get account by Id", e);
                    exchange.setStatusCode(404);
                    exchange.getResponseSender().send(e.getMessage());
                }
            }
        );
    }
}
