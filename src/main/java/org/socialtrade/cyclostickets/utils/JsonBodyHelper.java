package org.socialtrade.cyclostickets.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpResponse.BodySubscriber;
import java.net.http.HttpResponse.BodySubscribers;
import java.net.http.HttpResponse.ResponseInfo;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Factory class for {@link java.net.http.HttpRequest.BodyPublisher}s and {@link java.net.http.HttpResponse.BodyHandler}s using JSON
 */
public class JsonBodyHelper {

    private final ObjectMapper objectMapper;

    public JsonBodyHelper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Creates a new {@link BodyPublisher} which writes the given object as JSON
     */
    public BodyPublisher publisher(Object object) {
        return BodyPublishers.ofInputStream(() -> {
            try {
                var bytes = objectMapper.writeValueAsBytes(object);
                return new ByteArrayInputStream(bytes);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });
    }

    /**
     * Creates a new {@link BodyHandler} which reads a JSON body as the given type
     */
    public <T> BodyHandler<T> handler(Class<T> type) {
        return new BodyHandler<T>() {
            @Override
            public BodySubscriber<T> apply(ResponseInfo response) {
                var sub = BodyHandlers.ofInputStream().apply(response);
                return BodySubscribers.mapping(sub, is -> {
                    try {
                        return objectMapper.readValue(is, type);
                    } catch (IOException e) {
                        throw new IllegalStateException(e);
                    }
                });
            }
        };
    }
}
