package com.askaribank;

public class HomeLoan extends Loan {

    public HomeLoan(double principalAmount, int tenureMonths) {
        super("Home Loan", principalAmount, 12.0, tenureMonths);
    }

    @Override
    public double getMaximumAllowedAmount() {
        return 10000000;
    }

    @Override
    public double getMinimumRequiredSalary() {
        return 60000;
    }

    @Override
    public int getMinimumCreditScore() {
        return 700;
    }
}
