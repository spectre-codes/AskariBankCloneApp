package com.askaribank;

public class AmortizationRow {

    private int monthNumber;
    private double principalPaid;
    private double interestPaid;
    private double remainingBalance;

    public AmortizationRow(int monthNumber, double principalPaid, double interestPaid, double remainingBalance) {
        this.monthNumber = monthNumber;
        this.principalPaid = principalPaid;
        this.interestPaid = interestPaid;
        this.remainingBalance = remainingBalance;
    }

    public int getMonthNumber() {
        return monthNumber;
    }

    public void setMonthNumber(int monthNumber) {
        this.monthNumber = monthNumber;
    }

    public double getPrincipalPaid() {
        return principalPaid;
    }

    public void setPrincipalPaid(double principalPaid) {
        this.principalPaid = principalPaid;
    }

    public double getInterestPaid() {
        return interestPaid;
    }

    public void setInterestPaid(double interestPaid) {
        this.interestPaid = interestPaid;
    }

    public double getRemainingBalance() {
        return remainingBalance;
    }

    public void setRemainingBalance(double remainingBalance) {
        this.remainingBalance = remainingBalance;
    }
}
