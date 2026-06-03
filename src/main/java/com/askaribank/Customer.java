package com.askaribank;

import java.time.LocalDate;

public class Customer {

    private String customerFullName;
    private String cnicNumber;
    private String phoneNumber;
    private String accountNumber;
    private double accountBalance;
    private String accountType;
    private String cardType;
    private String atmPin;
    private LocalDate dateOpened;
    private String transactionPin;
    private int failedLoginAttempts;
    private boolean locked;
    private boolean frozen;
    private String raastId;
    private boolean raastLinked;
    private String email;
    private String address;
    private double dailyTransferLimit;
    private double dailyBillPaymentLimit;
    private double dailyTopUpLimit;
    private double dailyCardlessCashLimit;
    private double usedTransferToday;
    private double usedBillPaymentToday;
    private double usedTopUpToday;
    private double usedCardlessCashToday;
    private LocalDate lastLimitResetDate;
    private boolean hideBalanceByDefault;

    public Customer(String customerFullName, String cnicNumber, String phoneNumber,
                    String accountNumber, double accountBalance, String accountType,
                    String cardType, String atmPin, LocalDate dateOpened) {
        this(customerFullName, cnicNumber, phoneNumber, accountNumber, accountBalance,
                accountType, cardType, atmPin, dateOpened, atmPin, 0, false, false);
    }

    public Customer(String customerFullName, String cnicNumber, String phoneNumber,
                    String accountNumber, double accountBalance, String accountType,
                    String cardType, String atmPin, LocalDate dateOpened, String transactionPin,
                    int failedLoginAttempts, boolean locked, boolean frozen) {
        this.customerFullName = customerFullName;
        this.cnicNumber = cnicNumber;
        this.phoneNumber = phoneNumber;
        this.accountNumber = accountNumber;
        this.accountBalance = accountBalance;
        this.accountType = accountType;
        this.cardType = cardType;
        this.atmPin = atmPin;
        this.dateOpened = dateOpened;
        this.transactionPin = isValidPin(transactionPin) ? transactionPin : atmPin;
        this.failedLoginAttempts = Math.max(0, failedLoginAttempts);
        this.locked = locked;
        this.frozen = frozen;
        this.raastId = "";
        this.raastLinked = false;
        this.email = "";
        this.address = "";
        this.dailyTransferLimit = 100000;
        this.dailyBillPaymentLimit = 50000;
        this.dailyTopUpLimit = 10000;
        this.dailyCardlessCashLimit = 30000;
        this.usedTransferToday = 0;
        this.usedBillPaymentToday = 0;
        this.usedTopUpToday = 0;
        this.usedCardlessCashToday = 0;
        this.lastLimitResetDate = LocalDate.now();
        this.hideBalanceByDefault = true;
    }

    public Customer(String customerFullName, String cnicNumber, String phoneNumber,
                    String accountNumber, double accountBalance, String accountType,
                    String cardType, String atmPin, LocalDate dateOpened, String transactionPin,
                    int failedLoginAttempts, boolean locked, boolean frozen,
                    String raastId, boolean raastLinked, String email, String address,
                    double dailyTransferLimit, double dailyBillPaymentLimit, double dailyTopUpLimit,
                    double dailyCardlessCashLimit, double usedTransferToday, double usedBillPaymentToday,
                    double usedTopUpToday, double usedCardlessCashToday, LocalDate lastLimitResetDate) {
        this(customerFullName, cnicNumber, phoneNumber, accountNumber, accountBalance, accountType,
                cardType, atmPin, dateOpened, transactionPin, failedLoginAttempts, locked, frozen);
        this.raastId = cleanText(raastId);
        this.raastLinked = raastLinked && !this.raastId.isBlank();
        this.email = cleanText(email);
        this.address = cleanText(address);
        this.dailyTransferLimit = positiveOrDefault(dailyTransferLimit, 100000);
        this.dailyBillPaymentLimit = positiveOrDefault(dailyBillPaymentLimit, 50000);
        this.dailyTopUpLimit = positiveOrDefault(dailyTopUpLimit, 10000);
        this.dailyCardlessCashLimit = positiveOrDefault(dailyCardlessCashLimit, 30000);
        this.usedTransferToday = Math.max(0, usedTransferToday);
        this.usedBillPaymentToday = Math.max(0, usedBillPaymentToday);
        this.usedTopUpToday = Math.max(0, usedTopUpToday);
        this.usedCardlessCashToday = Math.max(0, usedCardlessCashToday);
        this.lastLimitResetDate = lastLimitResetDate == null ? LocalDate.now() : lastLimitResetDate;
    }

    public Customer(String customerFullName, String cnicNumber, String phoneNumber,
                    String accountNumber, double accountBalance, String accountType,
                    String cardType, String atmPin, LocalDate dateOpened, String transactionPin,
                    int failedLoginAttempts, boolean locked, boolean frozen,
                    String raastId, boolean raastLinked, String email, String address,
                    double dailyTransferLimit, double dailyBillPaymentLimit, double dailyTopUpLimit,
                    double dailyCardlessCashLimit, double usedTransferToday, double usedBillPaymentToday,
                    double usedTopUpToday, double usedCardlessCashToday, LocalDate lastLimitResetDate,
                    boolean hideBalanceByDefault) {
        this(customerFullName, cnicNumber, phoneNumber, accountNumber, accountBalance, accountType,
                cardType, atmPin, dateOpened, transactionPin, failedLoginAttempts, locked, frozen,
                raastId, raastLinked, email, address, dailyTransferLimit, dailyBillPaymentLimit,
                dailyTopUpLimit, dailyCardlessCashLimit, usedTransferToday, usedBillPaymentToday,
                usedTopUpToday, usedCardlessCashToday, lastLimitResetDate);
        this.hideBalanceByDefault = hideBalanceByDefault;
    }

    public String getCustomerFullName() {
        return customerFullName;
    }

    public void setCustomerFullName(String customerFullName) {
        this.customerFullName = customerFullName;
    }

    public String getCnicNumber() {
        return cnicNumber;
    }

    public void setCnicNumber(String cnicNumber) {
        this.cnicNumber = cnicNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getAtmPin() {
        return atmPin;
    }

    public void setAtmPin(String atmPin) {
        this.atmPin = atmPin;
    }

    public LocalDate getDateOpened() {
        return dateOpened;
    }

    public void setDateOpened(LocalDate dateOpened) {
        this.dateOpened = dateOpened;
    }

    public String getTransactionPin() {
        return transactionPin;
    }

    public void setTransactionPin(String transactionPin) {
        this.transactionPin = transactionPin;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = Math.max(0, failedLoginAttempts);
    }

    public void incrementFailedLoginAttempts() {
        failedLoginAttempts++;
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean getLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public boolean getFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    public String getRaastId() {
        return raastId;
    }

    public void setRaastId(String raastId) {
        this.raastId = cleanText(raastId);
    }

    public boolean isRaastLinked() {
        return raastLinked;
    }

    public boolean getRaastLinked() {
        return raastLinked;
    }

    public void setRaastLinked(boolean raastLinked) {
        this.raastLinked = raastLinked;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = cleanText(email);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = cleanText(address);
    }

    public double getDailyTransferLimit() {
        return dailyTransferLimit;
    }

    public void setDailyTransferLimit(double dailyTransferLimit) {
        this.dailyTransferLimit = positiveOrDefault(dailyTransferLimit, this.dailyTransferLimit);
    }

    public double getDailyBillPaymentLimit() {
        return dailyBillPaymentLimit;
    }

    public void setDailyBillPaymentLimit(double dailyBillPaymentLimit) {
        this.dailyBillPaymentLimit = positiveOrDefault(dailyBillPaymentLimit, this.dailyBillPaymentLimit);
    }

    public double getDailyTopUpLimit() {
        return dailyTopUpLimit;
    }

    public void setDailyTopUpLimit(double dailyTopUpLimit) {
        this.dailyTopUpLimit = positiveOrDefault(dailyTopUpLimit, this.dailyTopUpLimit);
    }

    public double getDailyCardlessCashLimit() {
        return dailyCardlessCashLimit;
    }

    public void setDailyCardlessCashLimit(double dailyCardlessCashLimit) {
        this.dailyCardlessCashLimit = positiveOrDefault(dailyCardlessCashLimit, this.dailyCardlessCashLimit);
    }

    public double getUsedTransferToday() {
        return usedTransferToday;
    }

    public void setUsedTransferToday(double usedTransferToday) {
        this.usedTransferToday = Math.max(0, usedTransferToday);
    }

    public double getUsedBillPaymentToday() {
        return usedBillPaymentToday;
    }

    public void setUsedBillPaymentToday(double usedBillPaymentToday) {
        this.usedBillPaymentToday = Math.max(0, usedBillPaymentToday);
    }

    public double getUsedTopUpToday() {
        return usedTopUpToday;
    }

    public void setUsedTopUpToday(double usedTopUpToday) {
        this.usedTopUpToday = Math.max(0, usedTopUpToday);
    }

    public double getUsedCardlessCashToday() {
        return usedCardlessCashToday;
    }

    public void setUsedCardlessCashToday(double usedCardlessCashToday) {
        this.usedCardlessCashToday = Math.max(0, usedCardlessCashToday);
    }

    public LocalDate getLastLimitResetDate() {
        return lastLimitResetDate;
    }

    public void setLastLimitResetDate(LocalDate lastLimitResetDate) {
        this.lastLimitResetDate = lastLimitResetDate == null ? LocalDate.now() : lastLimitResetDate;
    }

    public boolean isHideBalanceByDefault() {
        return hideBalanceByDefault;
    }

    public boolean getHideBalanceByDefault() {
        return hideBalanceByDefault;
    }

    public void setHideBalanceByDefault(boolean hideBalanceByDefault) {
        this.hideBalanceByDefault = hideBalanceByDefault;
    }

    public String getAccountStatus() {
        if (locked && frozen) {
            return "Locked / Frozen";
        }

        if (locked) {
            return "Locked";
        }

        if (frozen) {
            return "Frozen";
        }

        return "Active";
    }

    private boolean isValidPin(String pin) {
        return pin != null && pin.matches("\\d{4}");
    }

    private String cleanText(String value) {
        return value == null ? "" : value.trim();
    }

    private double positiveOrDefault(double value, double defaultValue) {
        return value > 0 ? value : defaultValue;
    }

    @Override
    public String toString() {
        return accountNumber + " - " + customerFullName;
    }
}
