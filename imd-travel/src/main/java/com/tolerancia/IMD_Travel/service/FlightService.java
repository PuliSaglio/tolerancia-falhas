package com.tolerancia.IMD_Travel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class FlightService {

    private final RestTemplate rest;

    @Value("${airlines.url:http://airlines-hub:8084}")
    private String airlinesUrl;

    public FlightService(RestTemplate rest) {
        this.rest = rest;
    }

    public Map<String, Object> getFlight(Long flight, String day) {
        try {
            ResponseEntity<Map<String, Object>> resp = rest.exchange(
                    String.format("%s/flight?flight=%s&day=%s", airlinesUrl, flight, day),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                throw new NoSuchElementException("Voo não encontrado.");
            }

            return resp.getBody();

        } catch (HttpClientErrorException.BadRequest e) {
            throw new IllegalArgumentException("Parâmetros inválidos.", e);
        } catch (HttpServerErrorException e) {
            throw new RuntimeException("Erro no serviço de voos.", e);
        }
    }
}
