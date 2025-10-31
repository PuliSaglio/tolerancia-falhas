package com.tolerancia.IMD_Travel.service;

import com.tolerancia.IMD_Travel.domain.PurchaseResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class IMDTravelService {

    private final RestTemplate rest = new RestTemplate();
    private final String airlineUrl = "http://localhost:8081";
    private final String exchangeUrl = "http://localhost:8083";
    private final String fidelityUrl = "http://localhost:8082";

    public PurchaseResponse processTicketPurchase(Long flight, String day, Long user) {

        // Request 1 - consulta voo
        var flightData = rest.getForObject(airlineUrl + "/flight?flight=" + flight + "&day=" + day, Map.class);
        double valueUsd = Double.parseDouble(flightData.get("value").toString());

        // Request 2 - taxa de câmbio
        try {
            double rate = rest.getForObject(exchangeUrl + "/exchange", Double.class);

            // Request 3 - registrar venda
            Map sellResp = rest.postForObject(airlineUrl + "/sell",
                    Map.of("flight", flight, "day", day), Map.class);

            // Request 4 - fidelidade
            rest.postForEntity(fidelityUrl + "/bonus",
                    Map.of("user", user, "bonus", (int) Math.round(valueUsd)),
                    Void.class);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
