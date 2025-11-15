package com.tolerancia.IMD_Travel.model;

import java.util.Map;

public class PurchaseResponse {

    private Long flight;
    private Long user;
    private String day;
    private Double valueDolar;
    private Double rate;
    private Long transactionId;

    public PurchaseResponse(Long flight, Long user, String day, Double valueDolar, Double rate, Long transactionId) {
        this.flight = flight;
        this.user = user;
        this.day = day;
        this.valueDolar = valueDolar;
        this.rate = rate;
        this.transactionId = transactionId;
    }

    public PurchaseResponse() { }

    public static PurchaseResponse of(Map<String, Object> flightData, Double rate, Long transactionId) {

        Long flightNumber = Long.parseLong(String.valueOf(flightData.get("flightNumber")));
        String day = String.valueOf(flightData.get("day"));
        Double valueUsd = Double.parseDouble(String.valueOf(flightData.get("value")));

        return new PurchaseResponse(
                flightNumber,
                null, // user não vem do serviço externo, é setado depois se quiser
                day,
                valueUsd,
                rate,
                transactionId
        );
    }

    public Long getFlight() {
        return flight;
    }

    public Long getUser() {
        return user;
    }

    public String getDay() {
        return day;
    }

    public Double getValueDolar() {
        return valueDolar;
    }

    public Double getRate() {
        return rate;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setFlight(Long flight) {
        this.flight = flight;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setValueDolar(Double valueDolar) {
        this.valueDolar = valueDolar;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }
}
