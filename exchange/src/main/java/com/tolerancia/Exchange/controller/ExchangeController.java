package com.tolerancia.Exchange.controller;

import com.tolerancia.Exchange.service.ExchangeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExchangeController {

    private static final Logger logger = LoggerFactory.getLogger(ExchangeController.class);
    private final ExchangeService exchangeService;

    public ExchangeController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    /**
     * Endpoint para obter a taxa de câmbio do Dólar para o Real.
     *
     * @return A taxa de câmbio ou um erro se o tempo de resposta for excedido.
     */
    @GetMapping("/exchange")
    public ResponseEntity<Double> getExchangeRate() {
        try {
            long start = System.nanoTime();
            double rate = exchangeService.getExchangeRate();
            long elapsed = System.nanoTime() - start; // Tempo decorrido em nanosegundos
            double elapsedMillis = elapsed / 1_000_000.0; // Converte para milissegundos

            if (elapsedMillis > 1000) { // Tempo limite de 1 segundo
                logger.warn("Tempo de resposta excedido: {} ms", elapsedMillis);
                return ResponseEntity.status(504).build();
            }

            logger.info("Taxa de câmbio gerada com sucesso: {} em {} ms", rate, elapsedMillis);
            return ResponseEntity.ok(rate);

        } catch (Exception e) {
            logger.error("Erro ao obter a taxa de câmbio: ", e);
            return ResponseEntity.status(500).build();
        }
    }
}
