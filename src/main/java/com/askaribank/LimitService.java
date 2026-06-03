package com.askaribank;

import java.time.LocalDate;

final class LimitService {

    private LimitService() {
    }

    static void resetDailyUsageIfNeeded(Customer customer) {
        if (customer == null) {
            return;
        }

        LocalDate today = LocalDate.now();
        if (customer.getLastLimitResetDate() == null || customer.getLastLimitResetDate().isBefore(today)) {
            customer.setUsedTransferToday(0);
            customer.setUsedBillPaymentToday(0);
            customer.setUsedTopUpToday(0);
            customer.setUsedCardlessCashToday(0);
            customer.setLastLimitResetDate(today);
        }
    }

    static void useTransferLimit(Customer customer, double amount) {
        resetDailyUsageIfNeeded(customer);
        validateLimit(amount, customer.getUsedTransferToday(), customer.getDailyTransferLimit(),
                "daily transfer limit");
        customer.setUsedTransferToday(customer.getUsedTransferToday() + amount);
    }

    static void useBillPaymentLimit(Customer customer, double amount) {
        resetDailyUsageIfNeeded(customer);
        validateLimit(amount, customer.getUsedBillPaymentToday(), customer.getDailyBillPaymentLimit(),
                "daily bill payment limit");
        customer.setUsedBillPaymentToday(customer.getUsedBillPaymentToday() + amount);
    }

    static void useTopUpLimit(Customer customer, double amount) {
        resetDailyUsageIfNeeded(customer);
        validateLimit(amount, customer.getUsedTopUpToday(), customer.getDailyTopUpLimit(),
                "daily mobile top-up limit");
        customer.setUsedTopUpToday(customer.getUsedTopUpToday() + amount);
    }

    static void useCardlessCashLimit(Customer customer, double amount) {
        resetDailyUsageIfNeeded(customer);
        validateLimit(amount, customer.getUsedCardlessCashToday(), customer.getDailyCardlessCashLimit(),
                "daily cardless cash limit");
        customer.setUsedCardlessCashToday(customer.getUsedCardlessCashToday() + amount);
    }

    private static void validateLimit(double amount, double usedToday, double dailyLimit, String limitName) {
        if (usedToday + amount > dailyLimit) {
            throw new IllegalArgumentException("This transaction exceeds your " + limitName
                    + ". Remaining limit is PKR " + String.format("%.2f", Math.max(0, dailyLimit - usedToday)) + ".");
        }
    }
}
