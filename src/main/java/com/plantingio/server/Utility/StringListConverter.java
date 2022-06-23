package com.plantingio.server.Utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.List;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    private static ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
    }

    @Override
    public String convertToDatabaseColumn(List<String> data) {
        if (data == null)
            return null;

        try {
            return mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }

    }

    @Override
    public List<String> convertToEntityAttribute(String s) {
        if (s == null)
            return null;

        try {
          return mapper.readValue(s, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

}
