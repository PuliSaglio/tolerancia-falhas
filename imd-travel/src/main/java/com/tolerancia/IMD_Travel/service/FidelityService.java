package com.tolerancia.IMD_Travel.service;

import com.tolerancia.IMD_Travel.model.FidelityTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class FidelityService {

    private static final Logger logger = LoggerFactory.getLogger(FidelityService.class);

    private final BlockingQueue<FidelityTask> queue = new LinkedBlockingQueue<>();

    @Value("${fidelity.url:http://fidelity:8082}")
    private String fidelityUrl;

    private final RestTemplate rest;

    public FidelityService(RestTemplate rest) {
        this.rest = rest;
    }

    // Chamado pelo IMDTravelService
    public void enqueueBonus(Long user, double usdValue) {
        int bonus = (int) Math.round(usdValue);
        queue.add(new FidelityTask(user, bonus));
        logger.info("[Fidelity] Task adicionada à fila → user={}, bonus={}", user, bonus);
    }

    // Agendado a cada 30s
    @Scheduled(fixedDelay = 30000)
    public void processQueue() {
        FidelityTask task;

        if (queue.isEmpty()) {
            logger.debug("[Fidelity] Fila vazia, nada para processar.");
            return;
        }

        logger.info("[Fidelity] Iniciando processamento da fila (tamanho={})", queue.size());

        while ((task = queue.poll()) != null) {
            try {
                logger.info("[Fidelity] Enviando bônus → user={}, bonus={}", task.user(), task.bonus());

                rest.postForEntity(
                        String.format("%s/bonus?user=%s&bonus=%d",
                                fidelityUrl, task.user(), task.bonus()),
                        null,
                        Void.class
                );

                logger.info("[Fidelity] Sucesso ao aplicar bônus → user={}, bonus={}",
                        task.user(), task.bonus());

            } catch (Exception e) {
                logger.error("[Fidelity] Falha ao enviar bônus → user={}, bonus={} | Erro: {}",
                        task.user(), task.bonus(), e.getMessage());

                queue.add(task); // devolve para a fila

                logger.warn("[Fidelity] Task reenfileirada devido a falha → user={}, bonus={}",
                        task.user(), task.bonus());

                break; // Para de tentar até a próxima execução agendada
            }
        }
    }
}

