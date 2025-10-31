package com.tolerancia.Exchange.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeoutException;

@Service
public class ExchangeService {

    // Variável Random para gerar valores aleatórios
    private final Random random = new Random();
    private static final int TIMEOUT_MS = 1000;

    /**
     * Realiza a obtenção da taxa de câmbio do Dólar para o Real.
     *
     * @return A taxa de câmbio desejada.
     */
    public double getExchangeRate() throws TimeoutException {
        long start = System.nanoTime();
        double rate = 5.0 + random.nextDouble(); // Gera um valor aleatório entre 5.0 e 6.0
        long elapsedNano = System.nanoTime() - start; // Tempo decorrido em nanosegundos
        double elapsedMillis = elapsedNano / 1_000_000.0; // Converte para milissegundos

        // Verifica se o tempo de resposta excedeu o limite
        if (elapsedMillis > TIMEOUT_MS) {
            throw new IllegalStateException("Tempo de resposta excedido: " + elapsedMillis + "ms");
        }

        return Math.round(rate * 100.0) / 100.0; // Arredonda para 2 casas decimais
    }
}
