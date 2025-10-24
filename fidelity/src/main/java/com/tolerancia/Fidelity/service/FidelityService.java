package com.tolerancia.Fidelity.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FidelityService {

    // Mapa para armazenar os pontos dos usuários.
    private final Map<String, Integer> userPoints = new HashMap<>();

    /**
     * Adiciona pontos de bônus para um usuário específico.
     *
     * @param user - nome do usuário
     * @param bonus - pontos de bônus a serem adicionados
     * @return true se os pontos foram adicionados com sucesso, false caso contrário
     */
    public boolean addBonusPoints(String user, int bonus) {
        if (user == null || user.isBlank() || bonus <= 0) {
            return false; // Validação para entradas inválidas.
        }

        // Adiciona os pontos de bônus ao usuário.
        userPoints.put(user, userPoints.getOrDefault(user, 0) + bonus);
        return true;
    }
}
