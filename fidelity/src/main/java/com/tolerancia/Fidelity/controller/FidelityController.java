package com.tolerancia.Fidelity.controller;

import com.tolerancia.Failure_Simulator.FailureManager;
import com.tolerancia.Fidelity.service.FidelityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FidelityController {

    private static final Logger logger = LoggerFactory.getLogger(FidelityController.class);
    private final FidelityService fidelityService;
    private FailureManager failureManager;

    public FidelityController(FidelityService fidelityService, FailureManager failureManager) {
        this.fidelityService = fidelityService;
        this.failureManager = failureManager;
    }

    /**
     * Endpoint para adicionar pontos de bônus a um usuário.
     *
     * @param user  - Id do usuário
     * @param bonus - pontos de bônus a serem adicionados
     * @return ResponseEntity indicando o sucesso ou falha da operação
     */
    @PostMapping("/bonus")
    public ResponseEntity<Void> addBonusPoints(@RequestParam Long user, @RequestParam Integer bonus) {
    	//Checa por crash failure
    	if(failureManager.crashFailure("/bonus")) {
    		Runtime.getRuntime().halt(0);
    	}
    	
        try {
            fidelityService.processBonusPoints(user, bonus);
            logger.info("Bônus adicionado com sucesso: user='{}', bonus={}", user, bonus);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.warn("Erro ao adicionar pontos de bônus - {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e){
            logger.error("Erro inesperado ao adicionar pontos", e);
            return ResponseEntity.status(500).build();
        }
    }
}
