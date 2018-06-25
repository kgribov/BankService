package com.kgribov.bankservice;

public class Application {

    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) {

        ApplicationServer server = new ApplicationServer(parsePort(args));

        server.start();
    }

    private static int parsePort(String[] args) {
        if (args != null && args.length > 0) {
            String portSt = args[0];
            try {
                return Integer.parseInt(portSt);
            } catch (Exception ex) {
                throw new RuntimeException("Unable to parse port number: " + portSt);
            }
        } else {
            return DEFAULT_PORT;
        }
    }
}
