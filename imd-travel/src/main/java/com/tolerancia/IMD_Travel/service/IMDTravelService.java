package com.tolerancia.IMD_Travel.service;

import com.tolerancia.IMD_Travel.model.PurchaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;

@Service
public class IMDTravelService {

    private final RestTemplate rest = new RestTemplate();
    private static final Logger logger = LoggerFactory.getLogger(IMDTravelService.class);
    private static final String AIRLINES_URL = "http://airlines-hub:8084";
    private static final String EXCHANGE_URL = "http://exchange:8083";
    private static final String FIDELITY_URL = "http://fidelity:8082";

    // Armazena as últimas taxas de câmbio obtidas
    private static final Queue<Double> lastRates = new LinkedList<>();
    private static final int MAX_RATES = 10;

    public PurchaseResponse processTicketPurchase(Long flight, String day, Long user, boolean ft) {

        PurchaseResponse purchaseResponse = new PurchaseResponse();

        // Request 1 - consulta voo
        Map<String, Object> flightData = getFlightData(flight, day);

        Double valueUsd = Double.parseDouble(String.valueOf(flightData.get("value")));
        purchaseResponse.setValueDolar(valueUsd);
        Long flightNumber = Long.parseLong(String.valueOf(flightData.get("flightNumber")));
        purchaseResponse.setFlight(flightNumber);
        String dayInfo = String.valueOf(flightData.get("day"));
        purchaseResponse.setDay(dayInfo);

        // Request 2 - taxa de câmbio
        Double rate = getExchangeRate(ft);
        purchaseResponse.setRate(rate);

        // Request 3 - registrar venda
        Long transactionId = registerSale(flight, day, ft);
        purchaseResponse.setTransactionId(transactionId);

        // Request 4 - Fidelity
        registerBonus(user, valueUsd);

        return purchaseResponse;
    }

    private Map<String, Object> getFlightData(Long flight, String day) {
        try {
            ResponseEntity<Map<String, Object>> flightResp = rest.exchange(
                    String.format("%s/flight?flight=%s&day=%s", AIRLINES_URL, flight, day),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            if (!flightResp.getStatusCode().is2xxSuccessful() || flightResp.getBody() == null) {
                throw new NoSuchElementException("Voo não encontrado para os parâmetros fornecidos.");
            }

            return flightResp.getBody();

        } catch (HttpClientErrorException.BadRequest e  ) {
            throw new IllegalArgumentException("Parâmetros inválidos para consulta de voo.", e);
        } catch (HttpServerErrorException e) {
            throw new RuntimeException("Erro interno no serviço de voos.", e);
        }
    }

    private Double getExchangeRate(boolean ft) {
        Double rate = 0.0;

        try {
            ResponseEntity<Double> exchangeResp = rest.getForEntity(
                    String.format("%s/convert", EXCHANGE_URL),
                    Double.class
            );

            rate = exchangeResp.getBody();

            if (rate == null) {
                throw new IllegalStateException("Resposta válida do Exchange, mas body nulo.");
            }

            addRateToCache(rate);
            return rate;

        } catch (HttpServerErrorException | ResourceAccessException e ) {
            if (ft) { // Tolerância ativa
                rate = getAverageRate();
                logger.warn("[FT] Aplicando fallback: taxa média calculada = {}", rate);
                return rate;
            }
            throw new RuntimeException("Erro interno no serviço de câmbio.", e);
        } catch (Exception e) {
            logger.error("Erro inesperado ao obter taxa de câmbio.", e);
            throw e;
        }
    }

    private void addRateToCache(Double rate) {
        if (rate == null) return;
        if (lastRates.size() >= MAX_RATES) {
            lastRates.poll(); // Remove a taxa mais antiga
        }
        lastRates.offer(rate);
    }

    private Double getAverageRate() {
        if (lastRates.isEmpty()) return 5.0; // Valor padrão se não houver taxas armazenadas
        return lastRates.stream().mapToDouble(Double::doubleValue).average().orElse(5.0);
    }

    private Long registerSale(Long flight, String day, boolean ft) {
        try {
            RestTemplate restTemplate = this.rest;

            if (ft) {
                // Ativa tolerância: cria RestTemplate com timeout de 2s
                var factory = new SimpleClientHttpRequestFactory();
                factory.setConnectTimeout(2000);
                factory.setReadTimeout(2000);
                restTemplate = new RestTemplate(factory);
            }

            ResponseEntity<Long> sellResp = restTemplate.postForEntity(
                    String.format("%s/sell?flight=%s&day=%s", AIRLINES_URL, flight, day),
                    null,
                    Long.class
            );
    
            if (!sellResp.getStatusCode().is2xxSuccessful() || sellResp.getBody() == null) {
                throw new NoSuchElementException("Vôo não encontrado para os parâmetros fornecidos.");
            }

            return sellResp.getBody();
    
        } catch (HttpClientErrorException.BadRequest e) {
            throw new IllegalArgumentException("Parâmetros inválidos para registrar venda.", e);
        } catch (HttpServerErrorException e) {
            throw new RuntimeException("Erro interno no serviço de vôos ao registrar venda.", e);
        } catch (ResourceAccessException e) {
            if (ft) { // Tolerância ativa
                logger.info("[FT] Tolerância ativa: timeout de 2s ativado no Request 3");
                throw new RuntimeException("Venda cancelada devido à alta latência (>2s) no serviço AirlinesHub.", e);
            }
            throw e;
        }
    }

    private void registerBonus(Long user, double valueUsd) {
        try {
            int bonus = (int) Math.round(valueUsd);

            rest.postForEntity(
                    String.format("%s/bonus?user=%s&bonus=%d", FIDELITY_URL, user, bonus),
                    null,
                    Void.class
            );

        } catch (HttpClientErrorException.BadRequest e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (HttpServerErrorException e) {
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            logger.error("Erro inesperado ao registrar pontos de bônus na Fidelity.", e);
            throw e;
        }
    }


}
