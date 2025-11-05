package com.tolerancia.Exchange.controller;

import com.tolerancia.Exchange.service.ExchangeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeoutException;

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
     * @return O valor em real ou um erro se o tempo de resposta for excedido.
     */
    @GetMapping("/convert")
    public ResponseEntity<Double> getExchangeRate() {
        try {
            double value = exchangeService.getExchangeRate();
            logger.info("Taxa de câmbio gerada com sucesso: R$ {}", value);
            return ResponseEntity.ok(value);
        } catch (TimeoutException e) {
            logger.warn("Erro ao obter taxa de câmbio: {}", e.getMessage());
            return ResponseEntity.status(504).build();
        } catch (Exception e) {
            logger.error("Erro inesperado ao obter a taxa de câmbio: ", e);
            return ResponseEntity.status(500).build();
        }
    }
}
