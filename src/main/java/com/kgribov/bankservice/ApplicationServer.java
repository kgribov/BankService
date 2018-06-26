package com.kgribov.bankservice;

import com.kgribov.bankservice.handler.HttpRouting;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.RoutingHandler;

public class ApplicationServer {

    private final Undertow server;

    public ApplicationServer(int port, String host) {
        this.server = createServer(port, host);
    }

    public ApplicationServer(int port) {
        this(port, "0.0.0.0");
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop();
    }

    public String getUrl() {
        return "http:/" + server.getListenerInfo().get(0).getAddress().toString();
    }

    private Undertow createServer(int port, String host) {
        RoutingHandler appHandler = HttpRouting.createRouting();

        HttpHandler limitedHandler = Handlers.requestLimitingHandler(
                50_000,
                100_000,
                appHandler);

        return Undertow.builder()
                .addHttpListener(port, host)
                .setHandler(limitedHandler)
                .setWorkerThreads(6)
                .setIoThreads(6)
                .build();
    }
}
