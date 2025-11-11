package com.tolerancia.IMD_Travel.service;

import com.tolerancia.IMD_Travel.model.PurchaseResponse;
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
public class IMDTravelService {

    private final RestTemplate rest = new RestTemplate();
    private static final String AIRLINES_URL = "http://airlines-hub:8084";

    public PurchaseResponse processTicketPurchase(Long flight, String day, Long user, boolean ft) {

        PurchaseResponse purchaseResponse = new PurchaseResponse();

        // Request 1 - consulta voo
        Map<String, Object> flightData = getFlightData(flight, day);

        Double valueUsd = Double.parseDouble(String.valueOf(flightData.get("value")));
        purchaseResponse.setValueDolar(valueUsd);
        Long flightNumber = Long.parseLong(String.valueOf(flightData.get("flightNumber")));
        purchaseResponse.setFlight(flightNumber);
        String dayInfo = String.valueOf(flightData.get("day"));
        purchaseResponse.setDay(dayInfo);

        // Request 2 - taxa de câmbio
        String exchangeUrl = "http://exchange:8083";
        Double rate = rest.getForObject(exchangeUrl +"/convert", Double.class);
        purchaseResponse.setRate(rate);

        // Request 3 - registrar venda
        ResponseEntity<Long> sellResp = rest.postForEntity(
                String.format("%s/sell?flight=%s&day=%s", AIRLINES_URL, flight, day),
                null,
                Long.class
        );

        purchaseResponse.setTransactionId(sellResp.getBody());

        // Request 4 - Fidelity
        String fidelityUrl = "http://fidelity:8082";
        rest.postForEntity(
                String.format("%s/bonus?user=%s&bonus=%d", fidelityUrl, user,
                        (int) Math.round(purchaseResponse.getValueDolar())),
                null,
                Void.class
        );

        return purchaseResponse;
    }

    private Map<String, Object> getFlightData(Long flight, String day) {
        try {
            ResponseEntity<Map<String, Object>> flightResp = rest.exchange(
                    String.format("%s/flight?flight=%s&day=%s", AIRLINES_URL, flight, day),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            if (!flightResp.getStatusCode().is2xxSuccessful() || flightResp.getBody() == null) {
                throw new NoSuchElementException("Voo não encontrado para os parâmetros fornecidos.");
            }

            return flightResp.getBody();
        } catch (HttpClientErrorException.BadRequest e) {
            throw new IllegalArgumentException("Parâmetros inválidos para consulta de voo.", e);
        } catch (HttpServerErrorException e) {
            throw new RuntimeException("Erro interno no serviço de voos.", e);
        }
    }
}
