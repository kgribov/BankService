package com.kgribov.bankservice.handler;

import com.kgribov.bankservice.json.JsonParser;
import com.kgribov.bankservice.service.MetricService;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

import static io.undertow.util.Headers.CONTENT_TYPE;

public class StatsHandler implements HttpHandler {

    private final MetricService metricService;
    private final JsonParser parser;

    public StatsHandler(MetricService metricService, JsonParser parser) {
        this.metricService = metricService;
        this.parser = parser;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        exchange.getResponseHeaders().put(CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(
            parser.toJson(metricService.getStats())
        );
    }
}
