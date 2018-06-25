package com.kgribov.bankservice.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kgribov.bankservice.dto.CreateAccountDTO;
import com.kgribov.bankservice.dto.CreateTransferDTO;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class JsonParserTest {

    private final JsonParser jsonParser = new JsonParser();

    @Test
    public void onParseJsonWithAllFieldsReturnObject() throws IOException {
        String json = "{\"name\": \"Bill\", \"balance\":0 }";
        CreateAccountDTO dto = jsonParser.fromJson(json, CreateAccountDTO.class);

        CreateAccountDTO expected = new CreateAccountDTO("Bill", 0);
        assertThat("Parser should return correct account dto", dto, equalTo(expected));
    }

    @Test(expected = JsonProcessingException.class)
    public void onParseJsonWithNotAllFieldsThrow() throws IOException {
        String json = "{\"name\": \"Bill\" }";
        jsonParser.fromJson(json, CreateAccountDTO.class);
    }

    @Test
    public void onCamelCaseFieldsCreateCamelCaseJson() throws JsonProcessingException {
        CreateTransferDTO createTransferDTO = new CreateTransferDTO(1L, 2L, 3);

        String json = jsonParser.toJson(createTransferDTO);

        assertThat("Parser should produce json with underscore fields",
                json,
                equalTo("{\"fromId\":1,\"toId\":2,\"amount\":3}")
        );
    }

    @Test
    public void onCamelCaseJsonCreateCamelCaseFields() throws IOException {
        String json = "{\"fromId\":1,\"toId\":2,\"amount\":3}";

        CreateTransferDTO createTransferDTO = jsonParser.fromJson(json, CreateTransferDTO.class);

        CreateTransferDTO expectedDTO = new CreateTransferDTO(1L, 2L, 3);

        assertThat("Parser should create object with camel case fields",
                createTransferDTO,
                equalTo(expectedDTO));
    }
}
