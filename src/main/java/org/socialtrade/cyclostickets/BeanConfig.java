package org.socialtrade.cyclostickets;

import org.socialtrade.cyclostickets.utils.JsonBodyHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Configuration for additional beans
 */
@Configuration
public class BeanConfig {

    @Bean
    ObjectMapper objectMapper() {
        var objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Include.NON_ABSENT);

        // Enable javax.time module
        objectMapper.registerModule(new JavaTimeModule());

        // Set the serialization configuration
        var serializationConfig = objectMapper.getSerializationConfig()
            .with(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)
            .without(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.setConfig(serializationConfig);

        // Set the de-serialization configuration
        var deserializationConfig = objectMapper.getDeserializationConfig()
            .with(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
            .with(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .with(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
            .without(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.setConfig(deserializationConfig);

        return objectMapper;
    }

    @Bean JsonBodyHelper jsonBodyHelper() {
        return new JsonBodyHelper(objectMapper());
    }
}
