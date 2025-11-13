package com.tolerancia.Exchange.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeoutException;

@Service
public class ExchangeService {

    private final Random random = new Random();
    private static final int TIMEOUT_MS = 1000;

    /**
     * Realiza a obtenção da taxa de câmbio do Dólar para o Real.
     *
     * @return A taxa de câmbio desejada.
     */
    public double getExchangeRate() throws TimeoutException {
        long start = System.nanoTime();
        double rate = 5.0 + random.nextDouble();
        long elapsedNano = System.nanoTime() - start; // Tempo decorrido em nanosegundos
        double elapsedMillis = elapsedNano / 1_000_000.0; // Converte para milissegundos

        // Verifica se o tempo de resposta excedeu o limite
        if (elapsedMillis > TIMEOUT_MS) {
            throw new TimeoutException("Tempo de resposta excedido: " + elapsedMillis + "ms");
        }

        return Math.round(rate * 100.0) / 100.0;
    }
}
