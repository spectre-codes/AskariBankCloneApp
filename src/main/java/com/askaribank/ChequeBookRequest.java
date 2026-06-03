package com.askaribank;

import java.time.LocalDate;

public class ChequeBookRequest {

    private String requestId;
    private String accountNumber;
    private String customerName;
    private int leaves;
    private LocalDate requestDate;
    private String status;

    public ChequeBookRequest(String requestId, String accountNumber, String customerName,
                             int leaves, LocalDate requestDate, String status) {
        this.requestId = requestId;
        this.accountNumber = accountNumber;
        this.customerName = customerName;
        this.leaves = leaves;
        this.requestDate = requestDate;
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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getLeaves() {
        return leaves;
    }

    public void setLeaves(int leaves) {
        this.leaves = leaves;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
