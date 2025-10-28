package com.tolerancia.Exchange.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class ExchangeService {

    // Variável Random para gerar valores aleatórios
    private final Random random = new Random();

    /**
     * Simula a obtenção da taxa de câmbio do Dólar para o Real.
     *
     * @return A taxa de câmbio desejada.
     */
    public double getExchangeRate() {
        // Gera um valor aleatório entre 5.0 e 6.0
        double rate = 5.0 + random.nextDouble();
        return Math.round(rate * 100.0) / 100.0; // Arredonda para 2 casas decimais
    }
}
