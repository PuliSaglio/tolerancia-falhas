package com.tolerancia.Airlines_Hub.service;

import com.tolerancia.Airlines_Hub.model.Flight;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class FlightService {

    private final Map<Long, Flight> flights = new HashMap<>();

    // Simulando um banco de dados com alguns voos pré-definidos
    public FlightService() {
        flights.put(1001L, new Flight(1001L, "2025-12-01", 1500.00));
        flights.put(1002L, new Flight(1002L, "2025-12-01", 2000.00));
        flights.put(1003L, new Flight(1003L, "2025-12-01", 2500.00));
        flights.put(1004L, new Flight(1004L, "2025-12-02", 1800.00));
        flights.put(1005L, new Flight(1005L, "2025-12-02", 2200.00));
    }

    public Flight getFlight(Long flightNumber, String day) {
        checkInputs(flightNumber, day);

        Flight flight = flights.get(flightNumber);
        if (flight == null) {
            throw new NoSuchElementException("Vôo não encontrado com id: " + flightNumber);
        }

        if (!flight.getDay().equals(day)) {
            throw new NoSuchElementException("Nenhum vôo encontrado para o dia: " + day);
        }

        return flight;
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
