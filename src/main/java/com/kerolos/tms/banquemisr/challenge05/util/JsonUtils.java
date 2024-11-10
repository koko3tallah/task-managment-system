package com.kerolos.tms.banquemisr.challenge05.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonUtils {

    private JsonUtils() {
    }

    private static ObjectMapper jacksonObjectMapper;

    /**
     * Configure JSON converter.
     *
     * @return Configured {@link ObjectMapper}
     */
    public static ObjectMapper getJacksonObjectMapper() {
        if (jacksonObjectMapper == null) {
            jacksonObjectMapper = new ObjectMapper();
            jacksonObjectMapper.registerModule(new JavaTimeModule());
            jacksonObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            jacksonObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            jacksonObjectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            jacksonObjectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, false);
            jacksonObjectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
            jacksonObjectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            jacksonObjectMapper.configure(DeserializationFeature.ACCEPT_FLOAT_AS_INT, false);
            jacksonObjectMapper.setVisibility(jacksonObjectMapper.getSerializationConfig()
                    .getDefaultVisibilityChecker()
                    .withFieldVisibility(JsonAutoDetect.Visibility.NON_PRIVATE));
        }

        return jacksonObjectMapper;
    }
}
