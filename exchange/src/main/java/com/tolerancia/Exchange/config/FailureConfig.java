package com.tolerancia.Exchange.config;

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
                "/convert", new FailureSpec("Error", 0.1, 5)
        );

        Map<String, FailureStrategy> failureStrategies = Map.of(
                //implementar ErrorFailure
                "Error", new ErrorFailure()
        );

        return new FailureManager(failureSpecs, failureStrategies);
    }

}