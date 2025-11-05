package com.tolerancia.Failure_Simulator;

import org.springframework.http.ResponseEntity;

public interface FailureStrategy {
    ResponseEntity<?> apply(String endpointId);
}