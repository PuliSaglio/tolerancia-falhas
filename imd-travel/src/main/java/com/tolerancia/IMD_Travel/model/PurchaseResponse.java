package com.tolerancia.IMD_Travel.model;

public class PurchaseResponse {

    private Long flight;
    private Long user;
    private String day;
    private Double valueDolar;
    private Double valueReal;
    private Long transactionId;

    public PurchaseResponse(Long flight, Long user, String day, Double valueDolar, Double valueReal, Long transactionId) {
        this.flight = flight;
        this.user = user;
        this.day = day;
        this.valueDolar = valueDolar;
        this.valueReal = valueReal;
        this.transactionId = transactionId;
    }

    public PurchaseResponse() {

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

    public Double getValueReal() {
        return valueReal;
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

    public void setValueReal(Double valueReal) {
        this.valueReal = valueReal;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }
}
