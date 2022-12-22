package org.socialtrade.cyclostickets.services.impl;

import java.net.http.HttpClient;

import org.socialtrade.cyclostickets.CyclosConfig;
import org.socialtrade.cyclostickets.utils.JsonBodyHelper;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Base class for services
 */
public abstract class BaseService {
    protected static HttpClient http = HttpClient.newHttpClient();

    @Autowired
    protected CyclosConfig cyclosConfig;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected JsonBodyHelper jsonBodyHelper;

}
