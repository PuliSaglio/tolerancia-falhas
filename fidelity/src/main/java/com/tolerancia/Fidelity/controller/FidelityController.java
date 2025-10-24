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
@RequestMapping("/fidelity")
public class FidelityController {

    private final FidelityService fidelityService;
    private static final Logger logger = LoggerFactory.getLogger(FidelityController.class);

    public FidelityController(FidelityService fidelityService) {
        this.fidelityService = fidelityService;
    }

    /**
     * Endpoint para adicionar pontos de bônus a um usuário.
     *
     * @param user  - nome do usuário
     * @param bonus - pontos de bônus a serem adicionados
     * @return ResponseEntity indicando o sucesso ou falha da operação
     */
    @PostMapping("/bonus")
    public ResponseEntity<Void> addBonusPoints(@RequestParam String user, @RequestParam int bonus) {
        try {
            if (user == null || user.isBlank() || bonus <= 0) {
                logger.warn("Tentativa de adicionar pontos de bônus com parâmetros inválidos: user='{}', bonus={}", user, bonus);
                return ResponseEntity.badRequest().build();
            }

            boolean success = fidelityService.addBonusPoints(user, bonus);
            if (success) {
                logger.info("Pontos de bônus adicionados com sucesso: user='{}', bonus={}", user, bonus);
                return ResponseEntity.ok().build();
            } else {
                logger.error("Falha ao adicionar pontos");
                return ResponseEntity.status(500).build();
            }

        } catch (Exception e){
            logger.error("Erro inesperado ao adicionar pontos");
            return ResponseEntity.status(500).build();
        }
    }
}
