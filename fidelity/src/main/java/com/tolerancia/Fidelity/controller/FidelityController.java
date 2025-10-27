package com.tolerancia.Fidelity.controller;

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

    private final FidelityService fidelityService;
    private static final Logger logger = LoggerFactory.getLogger(FidelityController.class);

    public FidelityController(FidelityService fidelityService) {
        this.fidelityService = fidelityService;
    }

    /**
     * Endpoint para adicionar pontos de bônus a um usuário.
     *
     * @param userId  - Id do usuário
     * @param bonus - pontos de bônus a serem adicionados
     * @return ResponseEntity indicando o sucesso ou falha da operação
     */
    @PostMapping("/bonus")
    public ResponseEntity<Void> addBonusPoints(@RequestParam Long userId, @RequestParam Integer bonus) {
        try {
            fidelityService.processBonusPoints(userId, bonus);
            logger.info("Bônus adicionado com sucesso: user='{}', bonus={}", userId, bonus);
            return ResponseEntity.noContent().build();
        }

        catch (IllegalArgumentException e) {
            logger.warn("Erro ao adicionar pontos de bônus: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }

        catch (Exception e){
            logger.error("Erro inesperado ao adicionar pontos", e);
            return ResponseEntity.status(500).build();
        }
    }
}
