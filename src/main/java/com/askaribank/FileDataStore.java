package com.askaribank;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class FileDataStore {

    private static final File DATA_DIRECTORY = new File("data");
    private static final File CUSTOMERS_FILE = new File(DATA_DIRECTORY, "customers.csv");
    private static final File TRANSACTIONS_FILE = new File(DATA_DIRECTORY, "transactions.csv");
    private static final File CARDS_FILE = new File(DATA_DIRECTORY, "cards.csv");
    private static final File BENEFICIARIES_FILE = new File(DATA_DIRECTORY, "beneficiaries.csv");
    private static final File CHEQUE_REQUESTS_FILE = new File(DATA_DIRECTORY, "cheque_requests.csv");
    private static final File NOTIFICATIONS_FILE = new File(DATA_DIRECTORY, "notifications.csv");
    private static final File CARDLESS_CASH_FILE = new File(DATA_DIRECTORY, "cardless_cash.csv");
    private static final File ACCOUNT_APPLICATIONS_FILE = new File(DATA_DIRECTORY, "account_applications.csv");

    private FileDataStore() {
    }

    public static void loadAll() {
        BankDataStore.allCustomers.clear();
        BankDataStore.allTransactions.clear();
        BankDataStore.allCards.clear();
        BankDataStore.allBeneficiaries.clear();
        BankDataStore.allChequeBookRequests.clear();
        BankDataStore.allNotifications.clear();
        BankDataStore.allCardlessCashRequests.clear();
        BankDataStore.allAccountApplications.clear();

        loadCustomers();
        loadTransactions();
        loadCards();
        loadBeneficiaries();
        loadChequeRequests();
        loadNotifications();
        loadCardlessCashRequests();
        loadAccountApplications();
    }

    public static void saveAll() {
        if (!DATA_DIRECTORY.exists()) {
            DATA_DIRECTORY.mkdirs();
        }

        saveCustomers();
        saveTransactions();
        saveCards();
        saveBeneficiaries();
        saveChequeRequests();
        saveNotifications();
        saveCardlessCashRequests();
        saveAccountApplications();
    }

    private static void loadCustomers() {
        if (!CUSTOMERS_FILE.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(CUSTOMERS_FILE))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length < 9) {
                    continue;
                }

                Customer customer = new Customer(
                        parts[0],
                        parts[1],
                        parts[2],
                        parts[3],
                        parseDouble(parts[4]),
                        parts[5],
                        parts[6],
                        parts[7],
                        parseDate(parts[8]),
                        getPart(parts, 9, parts[7]),
                        parseInteger(getPart(parts, 10, "0")),
                        parseBoolean(getPart(parts, 11, "false")),
                        parseBoolean(getPart(parts, 12, "false")),
                        getPart(parts, 13, ""),
                        parseBoolean(getPart(parts, 14, "false")),
                        getPart(parts, 15, ""),
                        getPart(parts, 16, ""),
                        parseDouble(getPart(parts, 17, "100000")),
                        parseDouble(getPart(parts, 18, "50000")),
                        parseDouble(getPart(parts, 19, "10000")),
                        parseDouble(getPart(parts, 20, "30000")),
                        parseDouble(getPart(parts, 21, "0")),
                        parseDouble(getPart(parts, 22, "0")),
                        parseDouble(getPart(parts, 23, "0")),
                        parseDouble(getPart(parts, 24, "0")),
                        parseDate(getPart(parts, 25, String.valueOf(LocalDate.now()))),
                        parseBoolean(getPart(parts, 26, "true")));
                BankDataStore.allCustomers.add(customer);
            }
        } catch (IOException exception) {
            System.err.println("Customers could not be loaded: " + exception.getMessage());
        }
    }

    private static void loadTransactions() {
        if (!TRANSACTIONS_FILE.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(TRANSACTIONS_FILE))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length < 8) {
                    continue;
                }

                Transaction transaction = new Transaction(
                        parts[0],
                        parts[1],
                        parts[2],
                        parseTransactionType(parts[3]),
                        parseDouble(parts[4]),
                        parts[5],
                        parseDateTime(parts[6]),
                        parseDouble(parts[7]));
                BankDataStore.allTransactions.add(transaction);
            }
        } catch (IOException exception) {
            System.err.println("Transactions could not be loaded: " + exception.getMessage());
        }
    }

    private static void loadCards() {
        if (!CARDS_FILE.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(CARDS_FILE))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length < 5) {
                    continue;
                }

                BankCard card = new BankCard(
                        parts[0],
                        parts[1],
                        parts[2],
                        parseCardStatus(parts[3]),
                        parts[4]);
                BankDataStore.allCards.add(card);
            }
        } catch (IOException exception) {
            System.err.println("Cards could not be loaded: " + exception.getMessage());
        }
    }

    private static void loadBeneficiaries() {
        if (!BENEFICIARIES_FILE.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(BENEFICIARIES_FILE))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length < 6) {
                    continue;
                }

                BankDataStore.allBeneficiaries.add(new Beneficiary(
                        parts[0],
                        parts[1],
                        parts[2],
                        parts[3],
                        parts[4],
                        parts[5]));
            }
        } catch (IOException exception) {
            System.err.println("Beneficiaries could not be loaded: " + exception.getMessage());
        }
    }

    private static void loadChequeRequests() {
        if (!CHEQUE_REQUESTS_FILE.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(CHEQUE_REQUESTS_FILE))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length < 6) {
                    continue;
                }

                BankDataStore.allChequeBookRequests.add(new ChequeBookRequest(
                        parts[0],
                        parts[1],
                        parts[2],
                        parseInteger(parts[3]),
                        parseDate(parts[4]),
                        parts[5]));
            }
        } catch (IOException exception) {
            System.err.println("Cheque requests could not be loaded: " + exception.getMessage());
        }
    }

    private static void loadNotifications() {
        if (!NOTIFICATIONS_FILE.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(NOTIFICATIONS_FILE))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length < 5) {
                    continue;
                }

                BankDataStore.allNotifications.add(new Notification(
                        parts[0],
                        parts[1],
                        parts[2],
                        parseDateTime(parts[3]),
                        parseBoolean(parts[4])));
            }
        } catch (IOException exception) {
            System.err.println("Notifications could not be loaded: " + exception.getMessage());
        }
    }

    private static void loadCardlessCashRequests() {
        if (!CARDLESS_CASH_FILE.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(CARDLESS_CASH_FILE))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length < 7) {
                    continue;
                }

                BankDataStore.allCardlessCashRequests.add(new CardlessCashRequest(
                        parts[0],
                        parts[1],
                        parseDouble(parts[2]),
                        parts[3],
                        parseDateTime(parts[4]),
                        parseDateTime(parts[5]),
                        parts[6]));
            }
        } catch (IOException exception) {
            System.err.println("Cardless cash requests could not be loaded: " + exception.getMessage());
        }
    }

    private static void loadAccountApplications() {
        if (!ACCOUNT_APPLICATIONS_FILE.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(ACCOUNT_APPLICATIONS_FILE))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length < 11) {
                    continue;
                }

                BankDataStore.allAccountApplications.add(new AccountApplication(
                        parts[0],
                        parts[1],
                        parts[2],
                        parts[3],
                        parts[4],
                        parts[5],
                        parts[6],
                        parts[7],
                        parseDateTime(parts[8]),
                        parseNullableDateTime(parts[9]),
                        parts[10]));
            }
        } catch (IOException exception) {
            System.err.println("Account applications could not be loaded: " + exception.getMessage());
        }
    }

    private static void saveCustomers() {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(CUSTOMERS_FILE)))) {
            writer.println("customerFullName,cnicNumber,phoneNumber,accountNumber,accountBalance,accountType,cardType,atmPin,dateOpened,transactionPin,failedLoginAttempts,locked,frozen,raastId,raastLinked,email,address,dailyTransferLimit,dailyBillPaymentLimit,dailyTopUpLimit,dailyCardlessCashLimit,usedTransferToday,usedBillPaymentToday,usedTopUpToday,usedCardlessCashToday,lastLimitResetDate,hideBalanceByDefault");
            for (Customer customer : BankDataStore.allCustomers) {
                writer.println(csv(customer.getCustomerFullName()) + ","
                        + csv(customer.getCnicNumber()) + ","
                        + csv(customer.getPhoneNumber()) + ","
                        + csv(customer.getAccountNumber()) + ","
                        + customer.getAccountBalance() + ","
                        + csv(customer.getAccountType()) + ","
                        + csv(customer.getCardType()) + ","
                        + csv(customer.getAtmPin()) + ","
                        + customer.getDateOpened() + ","
                        + csv(customer.getTransactionPin()) + ","
                        + customer.getFailedLoginAttempts() + ","
                        + customer.isLocked() + ","
                        + customer.isFrozen() + ","
                        + csv(customer.getRaastId()) + ","
                        + customer.isRaastLinked() + ","
                        + csv(customer.getEmail()) + ","
                        + csv(customer.getAddress()) + ","
                        + customer.getDailyTransferLimit() + ","
                        + customer.getDailyBillPaymentLimit() + ","
                        + customer.getDailyTopUpLimit() + ","
                        + customer.getDailyCardlessCashLimit() + ","
                        + customer.getUsedTransferToday() + ","
                        + customer.getUsedBillPaymentToday() + ","
                        + customer.getUsedTopUpToday() + ","
                        + customer.getUsedCardlessCashToday() + ","
                        + customer.getLastLimitResetDate() + ","
                        + customer.isHideBalanceByDefault());
            }
        } catch (IOException exception) {
            System.err.println("Customers could not be saved: " + exception.getMessage());
        }
    }

    private static void saveTransactions() {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(TRANSACTIONS_FILE)))) {
            writer.println("transactionId,fromAccountNumber,toAccountNumber,transactionType,amount,description,transactionDateTime,balanceAfterTransaction");
            for (Transaction transaction : BankDataStore.allTransactions) {
                writer.println(csv(transaction.getTransactionId()) + ","
                        + csv(transaction.getFromAccountNumber()) + ","
                        + csv(transaction.getToAccountNumber()) + ","
                        + transaction.getTransactionType() + ","
                        + transaction.getAmount() + ","
                        + csv(transaction.getDescription()) + ","
                        + transaction.getTransactionDateTime() + ","
                        + transaction.getBalanceAfterTransaction());
            }
        } catch (IOException exception) {
            System.err.println("Transactions could not be saved: " + exception.getMessage());
        }
    }

    private static void saveCards() {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(CARDS_FILE)))) {
            writer.println("cardNumber,accountNumber,cardType,status,pin");
            for (BankCard card : BankDataStore.allCards) {
                writer.println(csv(card.getCardNumber()) + ","
                        + csv(card.getAccountNumber()) + ","
                        + csv(card.getCardType()) + ","
                        + card.getStatus() + ","
                        + csv(card.getPin()));
            }
        } catch (IOException exception) {
            System.err.println("Cards could not be saved: " + exception.getMessage());
        }
    }

    private static void saveBeneficiaries() {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(BENEFICIARIES_FILE)))) {
            writer.println("beneficiaryId,ownerAccountNumber,beneficiaryName,beneficiaryAccountNumber,beneficiaryMobileNumber,bankName");
            for (Beneficiary beneficiary : BankDataStore.allBeneficiaries) {
                writer.println(csv(beneficiary.getBeneficiaryId()) + ","
                        + csv(beneficiary.getOwnerAccountNumber()) + ","
                        + csv(beneficiary.getBeneficiaryName()) + ","
                        + csv(beneficiary.getBeneficiaryAccountNumber()) + ","
                        + csv(beneficiary.getBeneficiaryMobileNumber()) + ","
                        + csv(beneficiary.getBankName()));
            }
        } catch (IOException exception) {
            System.err.println("Beneficiaries could not be saved: " + exception.getMessage());
        }
    }

    private static void saveChequeRequests() {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(CHEQUE_REQUESTS_FILE)))) {
            writer.println("requestId,accountNumber,customerName,leaves,requestDate,status");
            for (ChequeBookRequest request : BankDataStore.allChequeBookRequests) {
                writer.println(csv(request.getRequestId()) + ","
                        + csv(request.getAccountNumber()) + ","
                        + csv(request.getCustomerName()) + ","
                        + request.getLeaves() + ","
                        + request.getRequestDate() + ","
                        + csv(request.getStatus()));
            }
        } catch (IOException exception) {
            System.err.println("Cheque requests could not be saved: " + exception.getMessage());
        }
    }

    private static void saveNotifications() {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(NOTIFICATIONS_FILE)))) {
            writer.println("notificationId,accountNumber,message,timestamp,read");
            for (Notification notification : BankDataStore.allNotifications) {
                writer.println(csv(notification.getNotificationId()) + ","
                        + csv(notification.getAccountNumber()) + ","
                        + csv(notification.getMessage()) + ","
                        + notification.getTimestamp() + ","
                        + notification.isRead());
            }
        } catch (IOException exception) {
            System.err.println("Notifications could not be saved: " + exception.getMessage());
        }
    }

    private static void saveCardlessCashRequests() {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(CARDLESS_CASH_FILE)))) {
            writer.println("requestId,accountNumber,amount,withdrawalCode,createdAt,expiresAt,status");
            for (CardlessCashRequest request : BankDataStore.allCardlessCashRequests) {
                writer.println(csv(request.getRequestId()) + ","
                        + csv(request.getAccountNumber()) + ","
                        + request.getAmount() + ","
                        + csv(request.getWithdrawalCode()) + ","
                        + request.getCreatedAt() + ","
                        + request.getExpiresAt() + ","
                        + csv(request.getStatus()));
            }
        } catch (IOException exception) {
            System.err.println("Cardless cash requests could not be saved: " + exception.getMessage());
        }
    }

    private static void saveAccountApplications() {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(ACCOUNT_APPLICATIONS_FILE)))) {
            writer.println("applicationId,fullName,cnic,mobileNumber,email,city,accountType,status,submittedAt,reviewedAt,staffRemarks");
            for (AccountApplication application : BankDataStore.allAccountApplications) {
                writer.println(csv(application.getApplicationId()) + ","
                        + csv(application.getFullName()) + ","
                        + csv(application.getCnic()) + ","
                        + csv(application.getMobileNumber()) + ","
                        + csv(application.getEmail()) + ","
                        + csv(application.getCity()) + ","
                        + csv(application.getAccountType()) + ","
                        + csv(application.getStatus()) + ","
                        + application.getSubmittedAt() + ","
                        + (application.getReviewedAt() == null ? "" : application.getReviewedAt()) + ","
                        + csv(application.getStaffRemarks()));
            }
        } catch (IOException exception) {
            System.err.println("Account applications could not be saved: " + exception.getMessage());
        }
    }

    private static String csv(String value) {
        if (value == null) {
            return "";
        }
        return value.replace(",", " ").replace("\n", " ").replace("\r", " ").trim();
    }

    private static double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    private static int parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    private static boolean parseBoolean(String value) {
        return "true".equalsIgnoreCase(value);
    }

    private static String getPart(String[] parts, int index, String defaultValue) {
        if (index >= parts.length) {
            return defaultValue;
        }
        return parts[index];
    }

    private static LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value);
        } catch (Exception exception) {
            return LocalDate.now();
        }
    }

    private static LocalDateTime parseDateTime(String value) {
        try {
            return LocalDateTime.parse(value);
        } catch (Exception exception) {
            return LocalDateTime.now();
        }
    }

    private static LocalDateTime parseNullableDateTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return LocalDateTime.parse(value);
        } catch (Exception exception) {
            return null;
        }
    }

    private static TransactionType parseTransactionType(String value) {
        try {
            return TransactionType.valueOf(value);
        } catch (IllegalArgumentException exception) {
            return TransactionType.DEPOSIT;
        }
    }

    private static CardStatus parseCardStatus(String value) {
        try {
            return CardStatus.valueOf(value);
        } catch (IllegalArgumentException exception) {
            return CardStatus.ACTIVE;
        }
    }
}
