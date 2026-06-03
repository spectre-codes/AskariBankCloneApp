package com.askaribank;

import java.util.ArrayList;

public class EmiCalculationUtil {

    private EmiCalculationUtil() {
    }

    public static double calculateMonthlyInstallment(double principalAmount, double annualInterestRate, int tenureMonths) {
        if (principalAmount <= 0 || tenureMonths <= 0) {
            return 0;
        }

        double monthlyRate = annualInterestRate / 12 / 100;
        if (monthlyRate == 0) {
            return principalAmount / tenureMonths;
        }

        double power = Math.pow(1 + monthlyRate, tenureMonths);
        return principalAmount * monthlyRate * power / (power - 1);
    }

    public static ArrayList<AmortizationRow> generateAmortizationSchedule(double principalAmount,
                                                                           double annualInterestRate,
                                                                           int tenureMonths) {
        ArrayList<AmortizationRow> schedule = new ArrayList<>();
        double monthlyRate = annualInterestRate / 12 / 100;
        double monthlyEmi = calculateMonthlyInstallment(principalAmount, annualInterestRate, tenureMonths);
        double remainingBalance = principalAmount;

        for (int month = 1; month <= tenureMonths; month++) {
            double interestPaid = remainingBalance * monthlyRate;
            double principalPaid = monthlyEmi - interestPaid;

            if (month == tenureMonths || principalPaid > remainingBalance) {
                principalPaid = remainingBalance;
            }

            remainingBalance -= principalPaid;
            if (remainingBalance < 0.01) {
                remainingBalance = 0;
            }

            schedule.add(new AmortizationRow(month, principalPaid, interestPaid, remainingBalance));
        }

        return schedule;
    }
}
