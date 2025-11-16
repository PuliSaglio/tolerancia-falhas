package com.tolerancia.IMD_Travel.service;

import com.tolerancia.IMD_Travel.model.PurchaseResponse;
import org.springframework.stereotype.Service;

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

        var flightData = flightService.getFlight(flight, day, ft);
        var rate = exchangeService.getRate(ft);
        var saleId = salesService.registerSale(flight, day, ft);

        PurchaseResponse response = PurchaseResponse.of(flightData, rate, saleId);

        // Request 4 é assíncrono
        fidelityService.enqueueBonus(user, response.getValueDolar(), ft);

        return response;
    }
}

