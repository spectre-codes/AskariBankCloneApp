package com.askaribank;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class BankDataStore {

    public static String bankName = "Askari Bank";
    public static final String appName = "Askari Bank Desktop Banking System";
    public static ArrayList<Customer> allCustomers = new ArrayList<>();
    public static ArrayList<LoanApplication> allLoanApplications = new ArrayList<>();
    public static ArrayList<Transaction> allTransactions = new ArrayList<>();
    public static ArrayList<BankCard> allCards = new ArrayList<>();
    public static ArrayList<Beneficiary> allBeneficiaries = new ArrayList<>();
    public static ArrayList<ChequeBookRequest> allChequeBookRequests = new ArrayList<>();
    public static ArrayList<Notification> allNotifications = new ArrayList<>();
    public static ArrayList<CardlessCashRequest> allCardlessCashRequests = new ArrayList<>();
    public static ArrayList<AccountApplication> allAccountApplications = new ArrayList<>();
    public static int totalLoansIssued = 0;

    private static int accountNumberCounter = 1;
    private static int loanNumberCounter = 1;
    private static int transactionNumberCounter = 1;
    private static int cardNumberCounter = 1;
    private static int beneficiaryNumberCounter = 1;
    private static int chequeRequestNumberCounter = 1;
    private static int notificationNumberCounter = 1;
    private static int cardlessCashNumberCounter = 1;
    private static boolean initialized = false;

    private BankDataStore() {
    }

    public static void initialize() {
        if (initialized) {
            return;
        }

        FileDataStore.loadAll();
        if (allCustomers.isEmpty()) {
            allTransactions.clear();
            allCards.clear();
            createDemoData();
        }
        updateCountersFromData();
        ensureCardsForAllCustomers();
        updateCountersFromData();
        FileDataStore.saveAll();
        initialized = true;
    }

    public static String generateAccountNumber() {
        return String.format("ASK-2026-%05d", accountNumberCounter++);
    }

    public static String generateLoanId() {
        return String.format("LOAN-%05d", loanNumberCounter++);
    }

    public static String generateTransactionId() {
        return String.format("TXN-%05d", transactionNumberCounter++);
    }

    public static String generateCardNumber() {
        return String.format("58942026%08d", cardNumberCounter++);
    }

    public static String generateBeneficiaryId() {
        return String.format("BEN-%05d", beneficiaryNumberCounter++);
    }

    public static String generateChequeRequestId() {
        return String.format("CHQ-%05d", chequeRequestNumberCounter++);
    }

    public static String generateNotificationId() {
        return String.format("NOTIF-%05d", notificationNumberCounter++);
    }

    public static String generateCardlessCashRequestId() {
        return String.format("CASH-%05d", cardlessCashNumberCounter++);
    }

    public static Customer findCustomerByAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            return null;
        }

        for (Customer customer : allCustomers) {
            if (customer.getAccountNumber().equalsIgnoreCase(accountNumber.trim())) {
                return customer;
            }
        }
        return null;
    }

    public static Customer findCustomerByCnic(String cnicNumber) {
        if (cnicNumber == null || cnicNumber.trim().isEmpty()) {
            return null;
        }

        String cleanedCnic = cleanDigits(cnicNumber);
        for (Customer customer : allCustomers) {
            if (cleanDigits(customer.getCnicNumber()).equals(cleanedCnic)) {
                return customer;
            }
        }
        return null;
    }

    public static Customer findCustomerByPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return null;
        }

        String cleanedPhoneNumber = cleanDigits(phoneNumber);
        for (Customer customer : allCustomers) {
            if (cleanDigits(customer.getPhoneNumber()).equals(cleanedPhoneNumber)) {
                return customer;
            }
        }
        return null;
    }

    public static Customer findCustomerByRaastId(String raastId) {
        if (raastId == null || raastId.trim().isEmpty()) {
            return null;
        }

        String cleanedRaastId = cleanDigits(raastId);
        for (Customer customer : allCustomers) {
            if (customer.isRaastLinked() && cleanDigits(customer.getRaastId()).equals(cleanedRaastId)) {
                return customer;
            }
        }
        return null;
    }

    public static Customer findCustomerByAccountOrCnic(String loginId) {
        Customer customer = findCustomerByAccountNumber(loginId);
        if (customer != null) {
            return customer;
        }
        return findCustomerByCnic(loginId);
    }

    public static BankCard findCardByAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            return null;
        }

        for (BankCard card : allCards) {
            if (card.getAccountNumber().equalsIgnoreCase(accountNumber.trim())) {
                return card;
            }
        }
        return null;
    }

    public static BankCard createDefaultCardForCustomer(Customer customer) {
        if (customer == null) {
            return null;
        }

        BankCard existingCard = findCardByAccountNumber(customer.getAccountNumber());
        if (existingCard != null) {
            return existingCard;
        }

        BankCard card = new BankCard(generateCardNumber(), customer.getAccountNumber(),
                customer.getCardType(), CardStatus.ACTIVE, customer.getAtmPin());
        allCards.add(card);
        return card;
    }

    public static void ensureCardsForAllCustomers() {
        for (Customer customer : allCustomers) {
            createDefaultCardForCustomer(customer);
        }
    }

    public static ArrayList<Transaction> getTransactionsForAccount(String accountNumber) {
        ArrayList<Transaction> transactionsForAccount = new ArrayList<>();
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            return transactionsForAccount;
        }

        String cleanedAccountNumber = accountNumber.trim();
        for (int index = allTransactions.size() - 1; index >= 0; index--) {
            Transaction transaction = allTransactions.get(index);
            if (belongsToAccount(transaction, cleanedAccountNumber)) {
                transactionsForAccount.add(transaction);
            }
        }
        return transactionsForAccount;
    }

    public static double getTotalBankBalance() {
        double totalBalance = 0;
        for (Customer customer : allCustomers) {
            totalBalance += customer.getAccountBalance();
        }
        return totalBalance;
    }

    public static int getTotalTransactions() {
        return allTransactions.size();
    }

    private static boolean belongsToAccount(Transaction transaction, String accountNumber) {
        return switch (transaction.getTransactionType()) {
            case DEPOSIT, TRANSFER_IN, RAAST_TRANSFER_IN, LOAN_DISBURSEMENT ->
                    accountNumber.equals(transaction.getToAccountNumber());
            case WITHDRAWAL, TRANSFER_OUT, MOBILE_TOPUP, BILL_PAYMENT, MTAG_PAYMENT,
                 CARDLESS_CASH, TICKET_PURCHASE, DONATION, RAAST_TRANSFER_OUT ->
                    accountNumber.equals(transaction.getFromAccountNumber());
        };
    }

    public static long getApprovedLoanCount() {
        return allLoanApplications.stream()
                .filter(application -> "APPROVED".equals(application.getStatus()))
                .count();
    }

    public static long getRejectedLoanCount() {
        return allLoanApplications.stream()
                .filter(application -> "REJECTED".equals(application.getStatus()))
                .count();
    }

    public static long getPendingLoanCount() {
        return allLoanApplications.stream()
                .filter(application -> "PENDING".equals(application.getStatus()))
                .count();
    }

    public static long getBlockedCardCount() {
        return allCards.stream()
                .filter(card -> card.getStatus() == CardStatus.BLOCKED)
                .count();
    }

    private static void createDemoData() {
        Customer firstCustomer = new Customer("Ali Raza", "3520212345671", "03001234567",
                generateAccountNumber(), 50000, "Savings", "Visa", "1234", LocalDate.now());
        Customer secondCustomer = new Customer("Sana Khan", "3520212345672", "03007654321",
                generateAccountNumber(), 75000, "Current", "Mastercard", "4321", LocalDate.now());

        allCustomers.add(firstCustomer);
        allCustomers.add(secondCustomer);
        createDefaultCardForCustomer(firstCustomer);
        createDefaultCardForCustomer(secondCustomer);

        LocalDateTime now = LocalDateTime.now();
        allTransactions.add(new Transaction(generateTransactionId(), "", firstCustomer.getAccountNumber(),
                TransactionType.DEPOSIT, firstCustomer.getAccountBalance(), "Demo opening balance",
                now, firstCustomer.getAccountBalance()));
        allTransactions.add(new Transaction(generateTransactionId(), "", secondCustomer.getAccountNumber(),
                TransactionType.DEPOSIT, secondCustomer.getAccountBalance(), "Demo opening balance",
                now, secondCustomer.getAccountBalance()));
    }

    private static String cleanDigits(String value) {
        if (value == null) {
            return "";
        }
        return value.replaceAll("\\D", "");
    }

    private static void updateCountersFromData() {
        int highestAccountNumber = 0;
        int highestTransactionNumber = 0;
        int highestLoanNumber = 0;
        int highestCardNumber = 0;
        int highestBeneficiaryNumber = 0;
        int highestChequeRequestNumber = 0;
        int highestNotificationNumber = 0;
        int highestCardlessCashNumber = 0;

        for (Customer customer : allCustomers) {
            highestAccountNumber = Math.max(highestAccountNumber, readNumberAfterLastDash(customer.getAccountNumber()));
        }

        for (Transaction transaction : allTransactions) {
            highestTransactionNumber = Math.max(highestTransactionNumber, readTrailingNumber(transaction.getTransactionId()));
        }

        for (LoanApplication application : allLoanApplications) {
            highestLoanNumber = Math.max(highestLoanNumber, readTrailingNumber(application.getLoanId()));
        }

        for (BankCard card : allCards) {
            highestCardNumber = Math.max(highestCardNumber, readTrailingNumber(card.getCardNumber()));
        }

        for (Beneficiary beneficiary : allBeneficiaries) {
            highestBeneficiaryNumber = Math.max(highestBeneficiaryNumber,
                    readTrailingNumber(beneficiary.getBeneficiaryId()));
        }

        for (ChequeBookRequest request : allChequeBookRequests) {
            highestChequeRequestNumber = Math.max(highestChequeRequestNumber,
                    readTrailingNumber(request.getRequestId()));
        }

        for (Notification notification : allNotifications) {
            highestNotificationNumber = Math.max(highestNotificationNumber,
                    readTrailingNumber(notification.getNotificationId()));
        }

        for (CardlessCashRequest request : allCardlessCashRequests) {
            highestCardlessCashNumber = Math.max(highestCardlessCashNumber,
                    readTrailingNumber(request.getRequestId()));
        }

        accountNumberCounter = Math.max(accountNumberCounter, highestAccountNumber + 1);
        transactionNumberCounter = Math.max(transactionNumberCounter, highestTransactionNumber + 1);
        loanNumberCounter = Math.max(loanNumberCounter, highestLoanNumber + 1);
        cardNumberCounter = Math.max(cardNumberCounter, highestCardNumber + 1);
        beneficiaryNumberCounter = Math.max(beneficiaryNumberCounter, highestBeneficiaryNumber + 1);
        chequeRequestNumberCounter = Math.max(chequeRequestNumberCounter, highestChequeRequestNumber + 1);
        notificationNumberCounter = Math.max(notificationNumberCounter, highestNotificationNumber + 1);
        cardlessCashNumberCounter = Math.max(cardlessCashNumberCounter, highestCardlessCashNumber + 1);
    }

    private static int readTrailingNumber(String value) {
        if (value == null || value.isEmpty()) {
            return 0;
        }

        String digits = value.replaceAll("\\D", "");
        if (digits.length() > 8) {
            digits = digits.substring(digits.length() - 8);
        }

        try {
            return Integer.parseInt(digits);
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    private static int readNumberAfterLastDash(String value) {
        if (value == null || value.isEmpty()) {
            return 0;
        }

        int dashIndex = value.lastIndexOf('-');
        String numberText = dashIndex >= 0 ? value.substring(dashIndex + 1) : value;
        try {
            return Integer.parseInt(numberText.replaceAll("\\D", ""));
        } catch (NumberFormatException exception) {
            return 0;
        }
    }
}
