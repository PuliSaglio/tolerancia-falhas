package com.tolerancia.IMD_Travel.service;

import com.tolerancia.IMD_Travel.controller.IMDTravelController;
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

import java.util.Map;
import java.util.NoSuchElementException;

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
        var rate = exchangeService.getRate();
        var saleId = salesService.registerSale(flight, day, ft);

        PurchaseResponse response = PurchaseResponse.of(flightData, rate, saleId);

        // Request 4 é assíncrono
        fidelityService.enqueueBonus(user, response.getValueDolar());

        return response;
    }
}

