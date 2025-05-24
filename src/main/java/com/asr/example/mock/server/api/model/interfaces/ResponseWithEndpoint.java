package com.asr.example.mock.server.api.model.interfaces;

public interface ResponseWithEndpoint {
    String getEndpoint();

    String getHttpMethod();

    String getResponseBody();

    String getResponseHeaders();

    String getResponseStatus();

    String getEndpointId();

    String getResponseId();
}
