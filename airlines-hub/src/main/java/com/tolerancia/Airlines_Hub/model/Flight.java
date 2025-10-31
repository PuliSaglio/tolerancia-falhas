package com.tolerancia.Airlines_Hub.model;

public class Flight {

    private Long flightNumber;
    private String day;
    private Double value;

    public Flight(Long flightNumber, String day, double value) {
        this.flightNumber = flightNumber;
        this.day = day;
        this.value = value;
    }

    public Flight() {

    }

    public Long getFlightNumber() {
        return flightNumber;
    }

    public String getDay() {
        return day;
    }

    public double getValue() {
        return value;
    }

    public void setFlightNumber(Long flightNumber) {
        this.flightNumber = flightNumber;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
