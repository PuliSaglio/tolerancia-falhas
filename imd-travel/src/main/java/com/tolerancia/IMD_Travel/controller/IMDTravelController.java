package com.tolerancia.IMD_Travel.controller;

import com.tolerancia.IMD_Travel.domain.PurchaseResponse;
import com.tolerancia.IMD_Travel.service.IMDTravelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IMDTravelController {

    private static final Logger logger = LoggerFactory.getLogger(IMDTravelController.class);
    private final IMDTravelService imdTravelService;

    private IMDTravelController(IMDTravelService imdTravelService){
        this.imdTravelService = imdTravelService;
    }

    @PostMapping("/buyTicket")
    public ResponseEntity <PurchaseResponse> buyTicket(Long flight, String day, Long user) {
        try{
            PurchaseResponse purchase = imdTravelService.processTicketPurchase(flight, day, user);
            logger.info("Compra realizada com sucesso: Flight {}, Day {}, User {}", flight, day, user);
            return ResponseEntity.ok(purchase);
        } catch (Exception e) {
            logger.error("Erro inesperado ao processar a compra");
            throw new RuntimeException(e);
        }
    }
}
