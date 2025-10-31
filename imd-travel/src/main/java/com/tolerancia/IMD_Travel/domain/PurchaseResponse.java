package com.tolerancia.IMD_Travel.domain;

public class PurchaseResponse {

    private Long transactionId;

    public PurchaseResponse(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }
}
