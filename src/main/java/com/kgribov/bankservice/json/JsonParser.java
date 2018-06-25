package com.kgribov.bankservice.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import java.io.IOException;

public class JsonParser {

    private final ObjectMapper mapper;

    public JsonParser() {
        mapper = new ObjectMapper()
            .enable(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES)
            .registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));
    }

    public<T> String toJson(T object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }

    public<T> T fromJson(String json, Class<T> valueType) throws IOException {
        return mapper.readValue(json, valueType);
    }
}
