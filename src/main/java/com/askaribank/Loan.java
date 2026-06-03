package com.askaribank;

import java.time.LocalDate;

public abstract class Loan {

    private String loanId;
    private String loanType;
    private double principalAmount;
    private double annualInterestRate;
    private int tenureMonths;
    private String approvalStatus;
    private LocalDate applicationDate;

    public Loan(String loanType, double principalAmount, double annualInterestRate, int tenureMonths) {
        this.loanId = BankDataStore.generateLoanId();
        this.loanType = loanType;
        this.principalAmount = principalAmount;
        this.annualInterestRate = annualInterestRate;
        this.tenureMonths = tenureMonths;
        this.approvalStatus = "PENDING";
        this.applicationDate = LocalDate.now();
    }

    public double calculateMonthlyInstallment() {
        return EmiCalculationUtil.calculateMonthlyInstallment(principalAmount, annualInterestRate, tenureMonths);
    }

    public void approve() {
        this.approvalStatus = "APPROVED";
    }

    public void reject() {
        this.approvalStatus = "REJECTED";
    }

    public abstract double getMaximumAllowedAmount();

    public abstract double getMinimumRequiredSalary();

    public abstract int getMinimumCreditScore();

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public double getPrincipalAmount() {
        return principalAmount;
    }

    public void setPrincipalAmount(double principalAmount) {
        this.principalAmount = principalAmount;
    }

    public double getAnnualInterestRate() {
        return annualInterestRate;
    }

    public void setAnnualInterestRate(double annualInterestRate) {
        this.annualInterestRate = annualInterestRate;
    }

    public int getTenureMonths() {
        return tenureMonths;
    }

    public void setTenureMonths(int tenureMonths) {
        this.tenureMonths = tenureMonths;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public LocalDate getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDate applicationDate) {
        this.applicationDate = applicationDate;
    }
}
