package com.tolerancia.IMD_Travel.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayDeque;
import java.util.Deque;

@Service
public class ExchangeService {

    private final RestTemplate rest;
    private static final Logger logger = LoggerFactory.getLogger(ExchangeService.class);

    @Value("${exchange.url:http://exchange:8083}")
    private String exchangeUrl;

    private final Deque<Double> lastRates = new ArrayDeque<>(10);

    private static final double DEFAULT_RATE = 5.0;

    public ExchangeService(RestTemplate rest) {
        this.rest = rest;
    }

    public Double getRate(boolean ft) {

        try {
            Double rate = rest.getForObject(exchangeUrl + "/convert", Double.class);

            storeRate(rate);
            return rate;

        } catch (Exception e) {

            if (!ft) {
                throw e;
            }

            Double fallback = averageLastRates();

            if (fallback == null) {
                logger.warn("[FT] Falha e histórico vazio. Usando taxa padrão: {}", DEFAULT_RATE);
                return DEFAULT_RATE;
            }

            logger.warn("[FT] Falha na requisição. Usando média dos últimos {} valores: {}", lastRates.size(), fallback);

            return fallback;
        }
    }

    private void storeRate(Double rate) {
        if (rate == null) return;

        if (lastRates.size() == 10) {
            lastRates.removeFirst();
        }
        lastRates.addLast(rate);
    }

    private Double averageLastRates() {
        if (lastRates.isEmpty()) return null;

        return lastRates.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(DEFAULT_RATE);
    }
}