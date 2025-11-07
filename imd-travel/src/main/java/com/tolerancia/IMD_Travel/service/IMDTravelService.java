package com.tolerancia.IMD_Travel.service;

import com.tolerancia.IMD_Travel.model.PurchaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class IMDTravelService {

    private final RestTemplate rest = new RestTemplate();

    public PurchaseResponse processTicketPurchase(Long flight, String day, Long user) {

        PurchaseResponse purchaseResponse = new PurchaseResponse();

        try {
            // Request 1 - consulta voo
            String airlinesUrl = "http://airlines-hub:8084";
            var flightData = rest.getForObject(
                    String.format("%s/flight?flight=%s&day=%s", airlinesUrl, flight, day),
                    Map.class
            );

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
                    String.format("%s/sell?flight=%s&day=%s", airlinesUrl, flight, day),
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
