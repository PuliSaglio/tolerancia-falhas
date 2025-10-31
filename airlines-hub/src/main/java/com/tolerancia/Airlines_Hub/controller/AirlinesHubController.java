package com.tolerancia.Airlines_Hub.controller;

import com.tolerancia.Airlines_Hub.model.Flight;
import com.tolerancia.Airlines_Hub.service.AirlinesHubService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AirlinesHubController {

    private static final Logger logger = LoggerFactory.getLogger(AirlinesHubController.class);
    private final AirlinesHubService airlinesHubService;

    public AirlinesHubController(AirlinesHubService airlinesHubService) {
        this.airlinesHubService = airlinesHubService;
    }

    /**
     * Recupera as informações de um voo em um dia específico.
     *
     * @param flight
     * @param day
     * @return
     */
    @GetMapping("/flight")
    public ResponseEntity<Flight> getFlight(@RequestParam Long flight, @RequestParam String day) {
        try {
            Flight flightData = airlinesHubService.getFlightInfo(flight, day);
            logger.info("Informações do voo recuperadas com sucesso: flight='{}', day='{}', value'{}'",
                    flightData.getFlightNumber(), flightData.getDay(), flightData.getValue());
            return ResponseEntity.ok(flightData);
        } catch (IllegalArgumentException e) {
            logger.warn("Erro ao buscar informações do voo: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Erro inesperado ao buscar informações do voo", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Processa a venda de um voo em um dia específico.
     *
     * @param flight
     * @param day
     * @return
     */
    @PostMapping("/sell")
    public ResponseEntity<Long> sellFlight(@RequestParam Long flight, @RequestParam String day) {
        try {
            Long transactionId = airlinesHubService.processSell(flight, day);
            logger.info("Venda realizada com sucesso: flight='{}', day='{}', transactionId='{}'",
                    flight, day, transactionId);
            return ResponseEntity.ok(transactionId);
        } catch (IllegalArgumentException e) {
            logger.warn("Erro ao processar venda: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Erro inesperado ao processar venda", e);
            return ResponseEntity.status(500).build();
        }
    }
}
