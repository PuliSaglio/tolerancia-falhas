package com.tolerancia.IMD_Travel.controller;

import com.tolerancia.Failure_Simulator.FailureManager;
import com.tolerancia.IMD_Travel.model.PurchaseResponse;
import com.tolerancia.IMD_Travel.service.IMDTravelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
public class IMDTravelController {

    private static final Logger logger = LoggerFactory.getLogger(IMDTravelController.class);
    private final IMDTravelService imdTravelService;

    private IMDTravelController(IMDTravelService imdTravelService) {
        this.imdTravelService = imdTravelService;

    }

    @PostMapping("/buyTicket")
    public ResponseEntity<?> buyTicket(@RequestParam Long flight, @RequestParam String day,
                                       @RequestParam Long user, @RequestParam boolean ft) {
        try {
            PurchaseResponse purchase = imdTravelService.processTicketPurchase(flight, day, user, ft);
            logger.info("Compra realizada com sucesso. Flight: {}, Day: {}, User: {}", flight, day, user);
            logger.info("Transaction ID: {}", purchase.getTransactionId());
            return ResponseEntity.ok(purchase.getTransactionId());
        } catch (IllegalArgumentException e) {
            logger.warn(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NoSuchElementException e) {
            logger.warn(e.getMessage());
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
          logger.error("Erro ao finalizar compra de passagem. {}", e.getMessage());
          return ResponseEntity.status(500).build();
        }
    }
}
