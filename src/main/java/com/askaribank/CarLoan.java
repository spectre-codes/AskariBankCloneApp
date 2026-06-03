package com.askaribank;

public class CarLoan extends Loan {

    public CarLoan(double principalAmount, int tenureMonths) {
        super("Car Loan", principalAmount, 15.0, tenureMonths);
    }

    @Override
    public double getMaximumAllowedAmount() {
        return 3000000;
    }

    @Override
    public double getMinimumRequiredSalary() {
        return 45000;
    }

    @Override
    public int getMinimumCreditScore() {
        return 650;
    }
}
