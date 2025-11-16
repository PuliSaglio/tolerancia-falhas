package com.tolerancia.Exchange.controller;

import com.tolerancia.Exchange.service.ExchangeService;
import com.tolerancia.Failure_Simulator.FailureManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeoutException;

@RestController
public class ExchangeController {

    private static final Logger logger = LoggerFactory.getLogger(ExchangeController.class);
    private final ExchangeService exchangeService;
    private final FailureManager failureManager;

    public ExchangeController(ExchangeService exchangeService, FailureManager failureManager) {
        this.exchangeService = exchangeService;
        this.failureManager = failureManager;
    }

    /**
     * Endpoint para obter a taxa de câmbio do Dólar para o Real.
     *
     * @return O valor em real ou um erro se o tempo de resposta for excedido.
     */
    @GetMapping("/convert")
    public ResponseEntity<?> getExchangeRate() {
        ResponseEntity<?> failResponse = failureManager.errorFailure("/convert");
        if(failResponse != null) return failResponse;

        try {
            double value = exchangeService.getExchangeRate();
            return ResponseEntity.ok(value);
        } catch (TimeoutException e) {
            logger.warn("Erro ao obter taxa de câmbio - {}", e.getMessage());
            return ResponseEntity.status(504).build();
        } catch (Exception e) {
            logger.error("Erro inesperado ao obter a taxa de câmbio ", e);
            return ResponseEntity.status(500).build();
        }
    }

}
