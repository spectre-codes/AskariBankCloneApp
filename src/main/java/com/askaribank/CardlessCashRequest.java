package com.askaribank;

import java.time.LocalDateTime;

public class CardlessCashRequest {

    private String requestId;
    private String accountNumber;
    private double amount;
    private String withdrawalCode;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private String status;

    public CardlessCashRequest(String requestId, String accountNumber, double amount,
                               String withdrawalCode, LocalDateTime createdAt,
                               LocalDateTime expiresAt, String status) {
        this.requestId = requestId;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.withdrawalCode = withdrawalCode;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.status = status;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getWithdrawalCode() {
        return withdrawalCode;
    }

    public void setWithdrawalCode(String withdrawalCode) {
        this.withdrawalCode = withdrawalCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
