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

    /**
     * IMDTravel chama isso quando um pagamento em USD ocorre.
     * Primeiro tentamos enviar imediatamente.
     * Se falhar → adiciona na fila.
     */
    public void enqueueBonus(Long user, double usdValue,boolean ft) {
        int bonus = (int) Math.round(usdValue);

        try {
            rest.postForEntity(
                    String.format("%s/bonus?user=%s&bonus=%d",
                            fidelityUrl, user, bonus),
                    null,
                    Void.class
            );

        } catch (Exception e) {
            logger.error("[Fidelity] Falha no envio imediato → user={}, bonus={}, erro={}",
                    user, bonus, e.getMessage());

            if(!ft) throw e;

            queue.add(new FidelityTask(user, bonus, true));

            logger.warn("[Fidelity] Task adicionada à fila como fallback → user={}, bonus={}",
                    user, bonus);
        }
    }

    /**
     * Executado a cada 30s.
     * Processa tasks pendentes que falharam no envio imediato.
     */
    @Scheduled(fixedDelay = 30000)
    public void processQueue() {
        if (queue.isEmpty()) {
            logger.debug("[Fidelity] Fila vazia, nada a processar.");
            return;
        }

        logger.info("[Fidelity] Processando fila pendente (tamanho={})", queue.size());

        FidelityTask task;
        while ((task = queue.poll()) != null) {

            if (!task.ft()) {
                logger.error("[Fidelity] ERRO: Task encontrada na fila com ft=false → user={}, bonus={}",
                        task.user(), task.bonus());
                // descarta a task
                continue;
            }

            try {
                logger.info("[Fidelity] Tentando reenviar → user={}, bonus={}",
                        task.user(), task.bonus());

                rest.postForEntity(
                        String.format("%s/bonus?user=%s&bonus=%d",
                                fidelityUrl, task.user(), task.bonus()),
                        null,
                        Void.class
                );

                logger.info("[Fidelity] Reenvio bem-sucedido → user={}, bonus={}",
                        task.user(), task.bonus());

            } catch (Exception e) {

                logger.error("[Fidelity] Falha ao reenviar → user={}, bonus={}, erro={}",
                        task.user(), task.bonus(), e.getMessage());

                // devolve pra fila para tentar depois
                queue.add(task);

                logger.warn("[Fidelity] Task reenfileirada → user={}, bonus={}",
                        task.user(), task.bonus());

                // para tudo até a próxima rodada
                break;
            }
        }
    }
}
