package com.kgribov.bankservice;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.Test;

import java.net.InetAddress;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ApplicationServerIntegrationTest {

    @Test
    public void onCreateNewAccountShouldReturnAccountWithId() throws Exception {
        testOnServer(address -> {
            String createAccountJson = "{ \"name\": \"Bill\", \"balance\": 1000 }";
            HttpResponse<String> response = Unirest
                .post(toAddress(address, "/account"))
                .body(createAccountJson)
                .asString();

            String expectedJson = "{\"id\":0,\"name\":\"Bill\",\"balance\":1000}";

            assertThat(response.getStatus(), equalTo(200));
            assertThat(response.getBody(), equalTo(expectedJson));
        });
    }

    @Test
    public void onCreateAccountWithInvalidJsonReturnBadRequest() throws Exception {
        testOnServer(address -> {
            String invalidJson = "invalid json";
            HttpResponse<String> response = Unirest
                    .post(toAddress(address, "/account"))
                    .body(invalidJson)
                    .asString();

            assertThat(response.getStatus(), equalTo(400));
        });
    }

    @Test
    public void onGetAccountByNotExistIdReturnNotFound() throws Exception {
        testOnServer(address -> {
            Long notExistId = 1L;
            HttpResponse<String> response = Unirest
                .get(toAddress(address, "/account/" + notExistId))
                .asString();

            assertThat(response.getStatus(), equalTo(404));
        });
    }

    @Test
    public void onGetAccountWithInvalidIdReturnBadRequest() throws Exception {
        testOnServer(address -> {
            String invalidId = "invalid id";
            HttpResponse<String> response = Unirest
                    .get(toAddress(address, "/account/" + invalidId))
                    .asString();

            assertThat(response.getStatus(), equalTo(400));
        });
    }

    @Test
    public void onGetAccountShouldReturnAccountJson() throws Exception {
        testOnServer(address -> {
            createAccount("Bill", 100, address);

            String accountId = "0";
            HttpResponse<String> response = Unirest
                    .get(toAddress(address, "/account/" + accountId))
                    .asString();

            String expectedJson = "{\"id\":0,\"name\":\"Bill\",\"balance\":100}";
            assertThat(response.getStatus(), equalTo(200));
            assertThat(response.getBody(), equalTo(expectedJson));
        });
    }

    @Test
    public void onCreateNewTransferShouldReturnTransferJson() throws Exception {
        testOnServer(address -> {
            createAccount("Bill", 1000, address);
            createAccount("Gates", 1000, address);

            String createTransferJson = "{ \"fromId\": 0, \"toId\": 1, \"amount\": 1}";
            HttpResponse<JsonNode> response = Unirest
                    .post(toAddress(address, "/transfer"))
                    .body(createTransferJson)
                    .asJson();

            assertThat(response.getStatus(), equalTo(200));

            assertThat(response.getBody().getObject().get("id"), equalTo(0));
            assertThat(response.getBody().getObject().get("amount"), equalTo(1));
            assertThat(response.getBody().getObject().get("fromId"), equalTo(0));
            assertThat(response.getBody().getObject().get("toId"), equalTo(1));
        });
    }

    @Test
    public void onCreateTransferWithInvalidJsonShouldReturnBadRequest() throws Exception {
        testOnServer(address -> {
            String createTransferJson = "{ \"fromId\": 0, \"InvalidKey\": 1, \"amount\": 1}";
            HttpResponse<String> response = Unirest
                    .post(toAddress(address, "/transfer"))
                    .body(createTransferJson)
                    .asString();

            assertThat(response.getStatus(), equalTo(400));
        });
    }

    @Test
    public void onCreateNotAcceptedTransferShouldReturnNotAcceptable() throws Exception {
        testOnServer(address -> {
            createAccount("Bill", 1000, address);
            createAccount("Gates", 1000, address);

            String createTransferJson = "{ \"fromId\": 0, \"toId\": 1, \"amount\": 10000}";
            HttpResponse<String> response = Unirest
                    .post(toAddress(address, "/transfer"))
                    .body(createTransferJson)
                    .asString();

            assertThat(response.getStatus(), equalTo(406));
        });
    }

    @Test
    public void onCreateTransferWithNotFoundAccountShouldReturnNotFound() throws Exception {
        testOnServer(address -> {
            String createTransferJson = "{ \"fromId\": 0, \"toId\": 1, \"amount\": 10000}";
            HttpResponse<String> response = Unirest
                    .post(toAddress(address, "/transfer"))
                    .body(createTransferJson)
                    .asString();

            assertThat(response.getStatus(), equalTo(406));
        });
    }

    private void testOnServer(AddressConsumer consumer) throws Exception {
        ApplicationServer server = new ApplicationServer(0, InetAddress.getLocalHost().getHostAddress());
        try {
            server.start();

            consumer.accept(server.getUrl());
        } finally {
            server.stop();
        }
    }

    private interface AddressConsumer {
        void accept(String address) throws UnirestException;
    }

    private String toAddress(String address, String path) {
        return  address + path;
    }

    private void createAccount(String name, int balance, String address) throws UnirestException {
        Unirest
            .post(address + "/account")
            .body("{ \"name\": \"" + name + "\", \"balance\": " + balance + "}")
            .asString();
    }
}
