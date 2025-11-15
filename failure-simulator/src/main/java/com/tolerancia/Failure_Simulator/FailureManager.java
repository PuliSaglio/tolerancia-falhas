package com.tolerancia.Failure_Simulator;

import com.tolerancia.Failure_Simulator.Exceptions.OmissionFailureException;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class FailureManager {

    private static final Logger logger = LoggerFactory.getLogger(FailureManager.class);

    private final Map<String, ActiveFailure> activeFailures = new ConcurrentHashMap<>();
    private final Map<String, FailureSpec> specs;
    private final Map<String, FailureStrategy> strategies;

    public FailureManager(Map<String, FailureSpec> specs, Map<String, FailureStrategy> strategies ) {
        this.specs = specs;
        this.strategies = strategies;
    }

    public ResponseEntity<?> errorFailure(String endpointId) {
        FailureSpec spec = specs.get(endpointId);
        if (spec == null) return null;

        ActiveFailure currentFailure = activeFailures.get(endpointId);

        FailureStrategy strategy = strategies.get(spec.type());
        if (strategy == null) throw new IllegalStateException("No strategy for type: " + spec.type());

        Instant now = Instant.now();

        if(currentFailure != null) {
            if(now.isBefore(currentFailure.expiresAt())) {
                logger.warn("[FAILURE ACTIVE] {} - Tipo: {}, expira em: {}",
                        endpointId, currentFailure.type(), currentFailure.expiresAt());
                return strategy.apply(endpointId);
            }else{
                activeFailures.remove(endpointId);
            }
        }

        if(ThreadLocalRandom.current().nextDouble() < spec.probability()) {
            if(spec.durationSeconds() > 0 ) {
                activeFailures.put(endpointId, new ActiveFailure(spec.type(), now.plusSeconds(spec.durationSeconds())));
            }
            logger.error("[FAILURE STARTED] {} - Tipo: {}, duração: {}s, probabilidade: {}",
                    endpointId, spec.type(), spec.durationSeconds(), spec.probability());
            return strategy.apply(endpointId);
        }
        return null; // sem falhas
    }
    
    public boolean timeFailure(String endpointId) {
        FailureSpec spec = specs.get(endpointId);

        ActiveFailure currentFailure = activeFailures.get(endpointId);

        Instant now = Instant.now();
        
        if(currentFailure != null) {
            if(now.isBefore(currentFailure.expiresAt())) {
                logger.warn("[TIME FAILURE ACTIVE] {} - expira em {}", endpointId, currentFailure.expiresAt());
                return true;
            }else{
                activeFailures.remove(endpointId);
            }
        }
        
        if (ThreadLocalRandom.current().nextDouble() < spec.probability()) {
        	activeFailures.put(endpointId, new ActiveFailure(spec.type(), now.plusSeconds(spec.durationSeconds())));
            logger.error("[TIME FAILURE STARTED] {} - duração: {}s, probabilidade: {}",
                    endpointId, spec.durationSeconds(), spec.probability());
            return true;
        }
        return false;
    }

    public boolean omissionFailure(String endpointId) {
        FailureSpec spec = specs.get(endpointId);

        if (ThreadLocalRandom.current().nextDouble() < spec.probability()) {
            logger.error("[OMISSION FAILURE] {} - Omissão de resposta!", endpointId);
            return true;
        }
        return false;
    }
    
    public boolean crashFailure(String endpointId) {
        FailureSpec spec = specs.get(endpointId);

        if (ThreadLocalRandom.current().nextDouble() < spec.probability()) {
            logger.error("[CRASH FAILURE] {} - Encerrando aplicação!", endpointId);
            return true;
        }
        return false;
    }

}
