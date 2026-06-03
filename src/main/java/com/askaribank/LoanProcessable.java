package com.askaribank;

public interface LoanProcessable {

    void processApplication() throws InvalidLoanAmountException, InsufficientSalaryException, AccountNotFoundException;

    void approve();

    void reject(String reason);

    double calculateMonthlyInstallment();
}
