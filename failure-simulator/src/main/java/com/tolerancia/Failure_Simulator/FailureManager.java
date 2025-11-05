package com.tolerancia.Failure_Simulator;

import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class FailureManager {
    private final Map<String, ActiveFailure> activeFailures = new ConcurrentHashMap<>();
    private final Map<String, FailureSpec> specs;
    private final Map<String, FailureStrategy> strategies;

    public FailureManager(Map<String, FailureSpec> specs, Map<String, FailureStrategy> strategies ) {
        this.specs = specs;
        this.strategies = strategies;
    }

    public ResponseEntity<?> maybeFail(String endpointId) {
        FailureSpec spec = specs.get(endpointId);
        if (spec == null) return null;

        ActiveFailure currentFailure = activeFailures.get(endpointId);

        FailureStrategy strategy = strategies.get(spec.type());
        if (strategy == null) throw new IllegalStateException("No strategy for type: " + spec.type());

        Instant now = Instant.now();

        if(currentFailure != null) {
            if(now.isBefore(currentFailure.expiresAt())) {
                return strategy.apply(endpointId);
            }else{
                activeFailures.remove(endpointId);
            }
        }

        if(ThreadLocalRandom.current().nextDouble() < spec.probability()) {
            if(spec.durationSeconds() > 0 ) {
                activeFailures.put(endpointId, new ActiveFailure(spec.type(), now.plusSeconds(spec.durationSeconds())));
            }
            return strategy.apply(endpointId);
        }
        return null; // sem falhas
    }
}
