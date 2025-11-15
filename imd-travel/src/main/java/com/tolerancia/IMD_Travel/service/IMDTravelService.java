package com.tolerancia.IMD_Travel.service;

import com.tolerancia.IMD_Travel.model.PurchaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;

@Service
public class IMDTravelService {

    private final FlightService flightService;
    private final ExchangeService exchangeService;
    private final SalesService salesService;
    private final FidelityService fidelityService;

    public IMDTravelService(
            FlightService flightService,
            ExchangeService exchangeService,
            SalesService salesService,
            FidelityService fidelityService
    ) {
        this.flightService = flightService;
        this.exchangeService = exchangeService;
        this.salesService = salesService;
        this.fidelityService = fidelityService;
    }

    public PurchaseResponse processTicketPurchase(Long flight, String day, Long user, boolean ft) {

        var flightData = flightService.getFlight(flight, day);
        var rate = exchangeService.getRate(ft);
        var saleId = salesService.registerSale(flight, day, ft);

        PurchaseResponse response = PurchaseResponse.of(flightData, rate, saleId);

        // Request 4 é assíncrono
        fidelityService.enqueueBonus(user, response.getValueDolar(), ft);

        return response;
    }

    private void registerBonus(Long user, double valueUsd) {
        try {
            int bonus = (int) Math.round(valueUsd);

            rest.postForEntity(
                    String.format("%s/bonus?user=%s&bonus=%d", FIDELITY_URL, user, bonus),
                    null,
                    Void.class
            );

        } catch (HttpClientErrorException.BadRequest e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (HttpServerErrorException e) {
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            logger.error("Erro inesperado ao registrar pontos de bônus na Fidelity.", e);
            throw e;
        }
    }


}

