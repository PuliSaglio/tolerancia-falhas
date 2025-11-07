package com.tolerancia.Airlines_Hub.controller;

import com.tolerancia.Airlines_Hub.model.Flight;
import com.tolerancia.Airlines_Hub.service.FlightService;
import com.tolerancia.Airlines_Hub.service.SaleService;
import com.tolerancia.Failure_Simulator.FailureManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.NoSuchElementException;

@RestController
public class AirlinesHubController {

    private static final Logger logger = LoggerFactory.getLogger(AirlinesHubController.class);
    private final FlightService flightService;
    private final SaleService saleService;
    private FailureManager failureManager;

    public AirlinesHubController(FlightService flightService, SaleService saleService, FailureManager failureManager) {
        this.flightService = flightService;
        this.saleService = saleService;
        this.failureManager = failureManager;
    }

    /**
     * Recupera as informações de um voo em um dia específico.
     *
     * @param flight
     * @param day
     * @return
     */
    @GetMapping("/flight")
    public DeferredResult<ResponseEntity<?>> getFlight(@RequestParam Long flight, @RequestParam String day) {

        DeferredResult<ResponseEntity<?>> deferred = new DeferredResult<>(6000000L);

        if (failureManager.omissionFailure("/flight")) {
            logger.info("entrou no omission deferred='{}'", deferred);
            return deferred;
        }

        try {
            Flight flightData = flightService.getFlight(flight, day);
            logger.info("Informações do voo recuperadas com sucesso: flight='{}', day='{}', value'{}'",
                    flightData.getFlightNumber(), flightData.getDay(), flightData.getValue());
            deferred.setResult(new ResponseEntity<>(flightData, HttpStatus.OK));
            return deferred;

        } catch (IllegalArgumentException e) {
            logger.warn("Erro ao buscar informações do voo: {}", e.getMessage());
            deferred.setResult(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
            return deferred;
        } catch (NoSuchElementException e) {
            logger.warn("Falha na localização do võo: {}", e.getMessage());
            deferred.setResult(new ResponseEntity<>(HttpStatus.NO_CONTENT));
            return deferred;
        } catch (Exception e) {
            logger.error("Erro inesperado ao buscar informações do voo", e);
            deferred.setResult(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
            return deferred;
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
    	
    	if (failureManager.timeFailure("/bonus")) {
            try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    	
        try {
            Long transactionId = saleService.processSell(flight, day);
            logger.info("Venda em processo: flight='{}', day='{}', transactionId='{}'",
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
