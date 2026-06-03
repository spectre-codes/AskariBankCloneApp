package com.askaribank;

public class PersonalLoan extends Loan {

    public PersonalLoan(double principalAmount, int tenureMonths) {
        super("Personal Loan", principalAmount, 18.0, tenureMonths);
    }

    @Override
    public double getMaximumAllowedAmount() {
        return 500000;
    }

    @Override
    public double getMinimumRequiredSalary() {
        return 30000;
    }

    @Override
    public int getMinimumCreditScore() {
        return 600;
    }
}
