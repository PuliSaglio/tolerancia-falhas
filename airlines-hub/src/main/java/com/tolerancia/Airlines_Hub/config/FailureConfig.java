package com.tolerancia.Airlines_Hub.config;

import com.tolerancia.Failure_Simulator.FailureManager;
import com.tolerancia.Failure_Simulator.FailureSpec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;


@Configuration
public class FailureConfig {
    @Bean
    public FailureManager failureManager() {
        Map<String, FailureSpec> failureSpecs = Map.of(
                "/flight", new FailureSpec("Omission", 0.2, 0)
        );


        return new FailureManager(failureSpecs, null);
    }

}
