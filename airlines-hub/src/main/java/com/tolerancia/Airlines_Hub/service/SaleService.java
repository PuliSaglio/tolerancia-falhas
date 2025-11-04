package com.tolerancia.Airlines_Hub.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class SaleService {

    private final FlightService flightService;
    private static final AtomicLong idGenerator = new AtomicLong(1);

    public SaleService(FlightService flightService) {
        this.flightService = flightService;
    }

    public Long processSell(Long flightNumber, String day) {
        checkInputs(flightNumber, day);

        var flight = flightService.getFlight(flightNumber, day);
        if (flight == null) {
            throw new NoSuchElementException("Vôo não encontrado: flight='" +
                    flightNumber + "', day='" + day + "'");
        }

        return idGenerator.getAndIncrement();
    }

    private void checkInputs(Long flightNumber, String day) {
        if (flightNumber == null || flightNumber <= 0) {
            throw new IllegalArgumentException("Número do vôo inválido: " + flightNumber);
        }
        if (day == null || day.isBlank()) {
            throw new IllegalArgumentException("Dia inválido: " + day);
        }
    }
}
