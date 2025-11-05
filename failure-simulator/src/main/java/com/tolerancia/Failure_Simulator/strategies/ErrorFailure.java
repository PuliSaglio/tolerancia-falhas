package com.tolerancia.Failure_Simulator.strategies;

import com.tolerancia.Failure_Simulator.FailureStrategy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ErrorFailure implements FailureStrategy {
    @Override
    public ResponseEntity<?> apply(String endpointId) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
