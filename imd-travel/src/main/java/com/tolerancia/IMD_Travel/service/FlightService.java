package com.tolerancia.IMD_Travel.service;

import com.tolerancia.IMD_Travel.controller.IMDTravelController;
import com.tolerancia.IMD_Travel.exception.FlightServiceInternalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FlightService {

    private final RestTemplate rest;
    private final Map<String, Map<String, Object>> flightCache = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(FlightService.class);

    @Value("${airlines.url:http://airlines-hub:8084}")
    private String airlinesUrl;

    public FlightService(RestTemplate rest) {
        this.rest = rest;
    }

    public Map<String, Object> getFlight(Long flight, String day, boolean ft) {

        if (!ft) {
            Map<String, Object> resp = getFlightData(flight, day, ft);
            flightCache.put(flight + "_" + day, resp); // Atualiza o cache
            return resp;
        }

        int attempts = 0;

        while (attempts < 3) {
            try {
                Map<String, Object> resp = getFlightData(flight, day, ft);
                logger.warn("Tentativa " + (attempts + 1) + " bem-sucedida.");
                flightCache.put(flight + "_" + day, resp); // Atualiza o cache
                return resp;

            } catch (ResourceAccessException | FlightServiceInternalException e) {
                attempts++;
                logger.warn("[FT] Tentativa " + attempts + " falhou. Motivo: " + e.getMessage());
            }
        }

        // Fallback em cache
        logger.info("[FT] Todas as tentativas falharam. Verificando cache...");
        Map<String, Object> cached = flightCache.get(flight + "_" + day);

        if (cached != null) {
            logger.info("[FT] Cache encontrado. Retornando fallback.");
            return cached;
        }

        throw new RuntimeException("Falha por omissão — sem cache disponível.");
    }


    private Map<String, Object> getFlightData(Long flight, String day, boolean ft) {
        try {
            RestTemplate restTemplate = this.rest;

            if (ft) {
                // Ativa tolerância: cria RestTemplate com timeout de 5s
                var factory = new SimpleClientHttpRequestFactory();
                factory.setConnectTimeout(5000);
                factory.setReadTimeout(5000);
                restTemplate = new RestTemplate(factory);
                logger.info("[FT] Tolerância ativa: timeout de 5s configurado no Request 1");
            }

            ResponseEntity<Map<String, Object>> resp = restTemplate.exchange(
                    String.format("%s/flight?flight=%s&day=%s", airlinesUrl, flight, day),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                throw new NoSuchElementException("Voo não encontrado.");
            }

            return resp.getBody();

        } catch (HttpClientErrorException.BadRequest e) {
            throw new IllegalArgumentException("Parâmetros inválidos.", e);
        } catch (HttpServerErrorException | ResourceAccessException e) {
            if (ft) { // Tolerância ativa
                throw new FlightServiceInternalException("Erro interno no serviço de voos.", e);
            }
            throw e;
        }
    }
}
