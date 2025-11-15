package com.tolerancia.IMD_Travel.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.NoSuchElementException;

@Service
public class SalesService {

    @Value("${airlines.url:http://airlines-hub:8084}")
    private String airlinesUrl;

    private final RestTemplate defaultRest;

    private static final Logger logger = LoggerFactory.getLogger(IMDTravelService.class);


    public SalesService(RestTemplate defaultRest) {
        this.defaultRest = defaultRest;
    }

    public Long registerSale(Long flight, String day, boolean ft) {

        RestTemplate rest = defaultRest;

        if (ft) {
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(2000);
            factory.setReadTimeout(2000);
            rest = new RestTemplate(factory);
            logger.info("[FT] Tolerância ativa: timeout de 2s configurado no Request 3");
        }

        try {
            ResponseEntity<Long> resp = rest.postForEntity(
                    String.format("%s/sell?flight=%s&day=%s", airlinesUrl, flight, day),
                    null,
                    Long.class
            );

            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                throw new NoSuchElementException("Venda não registrada.");
            }

            return resp.getBody();

        } catch (HttpClientErrorException.BadRequest e) {
            throw new IllegalArgumentException("Parâmetros inválidos.", e);

        } catch (HttpServerErrorException e) {
            throw new RuntimeException("Erro no serviço Airlines ao registrar venda.", e);

        } catch (ResourceAccessException e) {
            if (ft) {
                throw new RuntimeException("Venda cancelada por latência (>2s).", e);
            }
            throw e;
        }
    }
}

