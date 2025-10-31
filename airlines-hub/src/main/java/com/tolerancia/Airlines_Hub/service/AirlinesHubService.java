package com.tolerancia.Airlines_Hub.service;

import com.tolerancia.Airlines_Hub.model.Flight;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AirlinesHubService {

    private static final AtomicLong idGenerator = new AtomicLong(1);
    private final Random random = new Random();

    /**
     * Recupera as informações de um voo em um dia específico.
     *
     * @param flight - número do voo
     * @param day    - dia do voo
     * @return informações do voo ou lança IllegalArgumentException se os parâmetros forem inválidos
     */
    public Flight getFlightInfo(Long flight, String day) {
        if (flight <= 0 || day == null || day.isBlank()) {
            throw new IllegalArgumentException("Parâmetros inválidos: flight = " + flight + ", day = " + day);
        }

        Flight flightDb = new Flight();

        double flightPrice = 100.0 + random.nextDouble() * 100.0; // Gera um preço aleatório entre 100.0 e 200.0
        flightPrice = Math.round(flightPrice * 100.0) / 100.0; // Arredonda para duas casas decimais

        flightDb.setFlightNumber(flight);
        flightDb.setDay(day);
        flightDb.setValue(flightPrice);

        return flightDb;
    }

    /**
     * Processa a venda de um voo em um dia específico.
     *
     * @param flight - número do voo
     * @param day    - dia da venda
     * @return um identificador único para a venda ou null se os parâmetros forem inválidos
     */

    public Long processSell(Long flight, String day) {
        if (flight <= 0 || day == null || day.isBlank()) {
            throw new IllegalArgumentException("Parâmetros inválidos: flight = " + flight + ", day = " + day);
        }

        return idGenerator.getAndIncrement();
    }
}
