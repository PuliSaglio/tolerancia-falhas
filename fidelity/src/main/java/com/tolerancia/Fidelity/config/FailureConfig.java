package com.tolerancia.Fidelity.config;

import com.tolerancia.Failure_Simulator.FailureManager;
import com.tolerancia.Failure_Simulator.FailureSpec;
import com.tolerancia.Failure_Simulator.FailureStrategy;
import com.tolerancia.Failure_Simulator.strategies.ErrorFailure;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;


@Configuration
public class FailureConfig {
    @Bean
    public FailureManager failureManager() {
        Map<String, FailureSpec> failureSpecs = Map.of(
                "/bonus", new FailureSpec("Crash", 0.02, 0)
        );

        return new FailureManager(failureSpecs, null);
    }

}
