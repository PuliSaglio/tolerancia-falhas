package com.tolerancia.Fidelity.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FidelityService {

    // Mapa para armazenar os pontos dos usuários.
    private final Map<Long, Integer> userPoints = new HashMap<>();

    /**
     * Processa a adição de pontos de bônus para um usuário.
     *
     * @param user - Id do usuário
     * @param bonus  - pontos de bônus a serem adicionados
     * @throws IllegalArgumentException se os parâmetros forem inválidos
     */
    public void processBonusPoints(Long user, Integer bonus) {
        if (user <= 0 || bonus <= 0) {
            throw new IllegalArgumentException("Parâmetros inválidos: user='" + user + "', bonus=" + bonus);
        }

        // Adiciona os pontos de bônus ao usuário.
        userPoints.put(user, userPoints.getOrDefault(user, 0) + bonus);
    }
}
