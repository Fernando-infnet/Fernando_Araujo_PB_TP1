package com.example.monolith.config;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;

import com.example.monolith.exception.RemoteServiceException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.Response;
import feign.codec.ErrorDecoder;

public class FeignErrorConfiguration {
    @Bean
    ErrorDecoder transactionServiceErrorDecoder(ObjectMapper mapper) {
        return (methodKey, response) -> new RemoteServiceException(
                HttpStatus.resolve(response.status()), readMessage(response, mapper));
    }

    private String readMessage(Response response, ObjectMapper mapper) {
        if (response.body() == null) return "O microsserviço de transações rejeitou a operação";
        try (InputStream body = response.body().asInputStream()) {
            JsonNode json = mapper.readTree(body);
            return json.path("message").asText("O microsserviço de transações rejeitou a operação");
        } catch (IOException ignored) {
            return "Não foi possível ler a resposta do microsserviço de transações";
        }
    }
}
