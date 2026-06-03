package com.askaribank;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class TransactionService {

    public Transaction deposit(String accountNumber, double amount, String description)
            throws AccountNotFoundException {
        validateAmount(amount);

        Customer customer = findCustomer(accountNumber);
        double newBalance = customer.getAccountBalance() + amount;
        customer.setAccountBalance(newBalance);

        Transaction transaction = new Transaction(
                BankDataStore.generateTransactionId(),
                "",
                customer.getAccountNumber(),
                TransactionType.DEPOSIT,
                amount,
                cleanDescription(description, "Cash deposit"),
                LocalDateTime.now(),
                newBalance);

        BankDataStore.allTransactions.add(transaction);
        saveAndLog("DEPOSIT", "Deposit into " + customer.getAccountNumber() + " amount PKR " + amount);
        return transaction;
    }

    public Transaction withdraw(String accountNumber, double amount, String description)
            throws AccountNotFoundException, InsufficientBalanceException {
        validateAmount(amount);

        Customer customer = findCustomer(accountNumber);
        validateDebitAllowed(customer);
        if (customer.getAccountBalance() < amount) {
            throw new InsufficientBalanceException("Insufficient balance. Current balance is PKR "
                    + String.format("%.2f", customer.getAccountBalance()) + ".");
        }

        double newBalance = customer.getAccountBalance() - amount;
        customer.setAccountBalance(newBalance);

        Transaction transaction = new Transaction(
                BankDataStore.generateTransactionId(),
                customer.getAccountNumber(),
                "",
                TransactionType.WITHDRAWAL,
                amount,
                cleanDescription(description, "Cash withdrawal"),
                LocalDateTime.now(),
                newBalance);

        BankDataStore.allTransactions.add(transaction);
        saveAndLog("WITHDRAWAL", "Withdrawal from " + customer.getAccountNumber() + " amount PKR " + amount);
        return transaction;
    }

    public Transaction transfer(String fromAccountNumber, String toAccountNumber, double amount, String description)
            throws AccountNotFoundException, InsufficientBalanceException {
        validateAmount(amount);

        if (fromAccountNumber != null && fromAccountNumber.equals(toAccountNumber)) {
            throw new IllegalArgumentException("Sender and receiver account numbers must be different.");
        }

        Customer sender = findCustomer(fromAccountNumber);
        Customer receiver = findCustomer(toAccountNumber);
        validateDebitAllowed(sender);

        if (sender.getAccountNumber().equalsIgnoreCase(receiver.getAccountNumber())) {
            throw new IllegalArgumentException("Sender and receiver account numbers must be different.");
        }

        if (sender.getAccountBalance() < amount) {
            throw new InsufficientBalanceException("Insufficient balance. Current balance is PKR "
                    + String.format("%.2f", sender.getAccountBalance()) + ".");
        }
        LimitService.useTransferLimit(sender, amount);

        double senderBalance = sender.getAccountBalance() - amount;
        double receiverBalance = receiver.getAccountBalance() + amount;
        sender.setAccountBalance(senderBalance);
        receiver.setAccountBalance(receiverBalance);

        String transactionDescription = cleanDescription(description, "Account transfer");
        LocalDateTime transactionTime = LocalDateTime.now();

        Transaction transferOut = new Transaction(
                BankDataStore.generateTransactionId(),
                sender.getAccountNumber(),
                receiver.getAccountNumber(),
                TransactionType.TRANSFER_OUT,
                amount,
                transactionDescription,
                transactionTime,
                senderBalance);

        Transaction transferIn = new Transaction(
                BankDataStore.generateTransactionId(),
                sender.getAccountNumber(),
                receiver.getAccountNumber(),
                TransactionType.TRANSFER_IN,
                amount,
                transactionDescription,
                transactionTime,
                receiverBalance);

        BankDataStore.allTransactions.add(transferOut);
        BankDataStore.allTransactions.add(transferIn);
        saveAndLog("TRANSFER", "Transfer from " + sender.getAccountNumber()
                + " to " + receiver.getAccountNumber() + " amount PKR " + amount);
        NotificationService.addNotification(sender.getAccountNumber(), "Transfer of PKR "
                + String.format("%.2f", amount) + " to " + receiver.getAccountNumber() + " was successful.");
        NotificationService.addNotification(receiver.getAccountNumber(), "You received PKR "
                + String.format("%.2f", amount) + " from " + sender.getAccountNumber() + ".");
        return transferOut;
    }

    public Transaction mobileTopUp(String accountNumber, String mobileNumber, String network, double amount)
            throws AccountNotFoundException, InsufficientBalanceException {
        validateAmount(amount);

        if (mobileNumber == null || mobileNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Please enter a mobile number.");
        }

        if (network == null || network.trim().isEmpty()) {
            throw new IllegalArgumentException("Please select a mobile network.");
        }

        Customer customer = findCustomer(accountNumber);
        validateDebitAllowed(customer);
        if (customer.getAccountBalance() < amount) {
            throw new InsufficientBalanceException("Insufficient balance. Current balance is PKR "
                    + String.format("%.2f", customer.getAccountBalance()) + ".");
        }
        LimitService.useTopUpLimit(customer, amount);

        double newBalance = customer.getAccountBalance() - amount;
        customer.setAccountBalance(newBalance);

        String cleanedMobileNumber = mobileNumber.trim();
        String cleanedNetwork = network.trim();
        Transaction transaction = new Transaction(
                BankDataStore.generateTransactionId(),
                customer.getAccountNumber(),
                cleanedNetwork + " " + cleanedMobileNumber,
                TransactionType.MOBILE_TOPUP,
                amount,
                "Mobile top-up for " + cleanedMobileNumber,
                LocalDateTime.now(),
                newBalance);

        BankDataStore.allTransactions.add(transaction);
        saveAndLog("MOBILE_TOPUP", "Mobile top-up from " + customer.getAccountNumber()
                + " to " + cleanedMobileNumber + " amount PKR " + amount);
        NotificationService.addNotification(customer.getAccountNumber(), "Mobile top-up of PKR "
                + String.format("%.2f", amount) + " for " + cleanedMobileNumber + " was successful.");
        return transaction;
    }

    public Transaction billPayment(String accountNumber, String billType, String billerName,
                                   String consumerNumber, double amount)
            throws AccountNotFoundException, InsufficientBalanceException {
        validateAmount(amount);

        if (billType == null || billType.trim().isEmpty()) {
            throw new IllegalArgumentException("Please select a bill type.");
        }

        if (billerName == null || billerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Please enter biller name.");
        }

        if (consumerNumber == null || consumerNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Please enter consumer number.");
        }

        Customer customer = findCustomer(accountNumber);
        validateDebitAllowed(customer);
        if (customer.getAccountBalance() < amount) {
            throw new InsufficientBalanceException("Insufficient balance. Current balance is PKR "
                    + String.format("%.2f", customer.getAccountBalance()) + ".");
        }
        LimitService.useBillPaymentLimit(customer, amount);

        double newBalance = customer.getAccountBalance() - amount;
        customer.setAccountBalance(newBalance);
        String description = billType.trim() + " bill payment - " + billerName.trim()
                + " (" + consumerNumber.trim() + ")";

        Transaction transaction = new Transaction(
                BankDataStore.generateTransactionId(),
                customer.getAccountNumber(),
                billerName.trim(),
                TransactionType.BILL_PAYMENT,
                amount,
                description,
                LocalDateTime.now(),
                newBalance);

        BankDataStore.allTransactions.add(transaction);
        saveAndLog("BILL_PAYMENT", "Bill payment from " + customer.getAccountNumber()
                + " amount PKR " + amount + " to " + billerName.trim());
        NotificationService.addNotification(customer.getAccountNumber(), "Bill payment of PKR "
                + String.format("%.2f", amount) + " to " + billerName.trim() + " was successful.");
        return transaction;
    }

    public Transaction raastTransfer(String fromAccountNumber, String raastOrMobileNumber, double amount,
                                     String description)
            throws AccountNotFoundException, InsufficientBalanceException {
        Customer receiver = BankDataStore.findCustomerByRaastId(raastOrMobileNumber);
        if (receiver == null) {
            receiver = BankDataStore.findCustomerByPhoneNumber(raastOrMobileNumber);
        }

        if (receiver == null) {
            throw new AccountNotFoundException("Raast/mobile receiver was not found.");
        }

        return transferWithTypes(fromAccountNumber, receiver.getAccountNumber(), amount,
                cleanDescription(description, "Raast transfer"),
                TransactionType.RAAST_TRANSFER_OUT, TransactionType.RAAST_TRANSFER_IN, "RAAST_TRANSFER");
    }

    public Transaction customerPayment(String accountNumber, String payee, TransactionType transactionType,
                                       double amount, String description, String auditAction,
                                       String notificationMessage)
            throws AccountNotFoundException, InsufficientBalanceException {
        validateAmount(amount);

        Customer customer = findCustomer(accountNumber);
        validateDebitAllowed(customer);
        if (customer.getAccountBalance() < amount) {
            throw new InsufficientBalanceException("Insufficient balance. Current balance is PKR "
                    + String.format("%.2f", customer.getAccountBalance()) + ".");
        }

        double newBalance = customer.getAccountBalance() - amount;
        customer.setAccountBalance(newBalance);
        Transaction transaction = new Transaction(
                BankDataStore.generateTransactionId(),
                customer.getAccountNumber(),
                cleanDescription(payee, ""),
                transactionType,
                amount,
                cleanDescription(description, transactionType.name().replace("_", " ")),
                LocalDateTime.now(),
                newBalance);

        BankDataStore.allTransactions.add(transaction);
        saveAndLog(auditAction, auditAction + " from " + customer.getAccountNumber()
                + " amount PKR " + amount);
        NotificationService.addNotification(customer.getAccountNumber(), notificationMessage);
        return transaction;
    }

    public Transaction cardlessCashWithdrawal(String accountNumber, double amount, String description)
            throws AccountNotFoundException, InsufficientBalanceException {
        validateAmount(amount);

        Customer customer = findCustomer(accountNumber);
        validateDebitAllowed(customer);
        if (customer.getAccountBalance() < amount) {
            throw new InsufficientBalanceException("Insufficient balance. Current balance is PKR "
                    + String.format("%.2f", customer.getAccountBalance()) + ".");
        }
        LimitService.useCardlessCashLimit(customer, amount);

        double newBalance = customer.getAccountBalance() - amount;
        customer.setAccountBalance(newBalance);
        Transaction transaction = new Transaction(
                BankDataStore.generateTransactionId(),
                customer.getAccountNumber(),
                "Cardless Cash",
                TransactionType.CARDLESS_CASH,
                amount,
                cleanDescription(description, "Cardless cash withdrawal"),
                LocalDateTime.now(),
                newBalance);

        BankDataStore.allTransactions.add(transaction);
        saveAndLog("CARDLESS_CASH", "Cardless cash withdrawal from " + customer.getAccountNumber()
                + " amount PKR " + amount);
        NotificationService.addNotification(customer.getAccountNumber(), "Cardless cash withdrawal of PKR "
                + String.format("%.2f", amount) + " was completed.");
        return transaction;
    }

    public ArrayList<Transaction> getTransactionsForAccount(String accountNumber) {
        return BankDataStore.getTransactionsForAccount(accountNumber);
    }

    private Customer findCustomer(String accountNumber) throws AccountNotFoundException {
        Customer customer = BankDataStore.findCustomerByAccountNumber(accountNumber);
        if (customer == null) {
            throw new AccountNotFoundException("Account not found. Please enter a valid account number.");
        }
        return customer;
    }

    private Transaction transferWithTypes(String fromAccountNumber, String toAccountNumber, double amount,
                                          String description, TransactionType outgoingType,
                                          TransactionType incomingType, String auditAction)
            throws AccountNotFoundException, InsufficientBalanceException {
        validateAmount(amount);

        if (fromAccountNumber != null && fromAccountNumber.equals(toAccountNumber)) {
            throw new IllegalArgumentException("Sender and receiver account numbers must be different.");
        }

        Customer sender = findCustomer(fromAccountNumber);
        Customer receiver = findCustomer(toAccountNumber);
        validateDebitAllowed(sender);

        if (sender.getAccountNumber().equalsIgnoreCase(receiver.getAccountNumber())) {
            throw new IllegalArgumentException("Sender and receiver account numbers must be different.");
        }

        if (sender.getAccountBalance() < amount) {
            throw new InsufficientBalanceException("Insufficient balance. Current balance is PKR "
                    + String.format("%.2f", sender.getAccountBalance()) + ".");
        }
        LimitService.useTransferLimit(sender, amount);

        double senderBalance = sender.getAccountBalance() - amount;
        double receiverBalance = receiver.getAccountBalance() + amount;
        sender.setAccountBalance(senderBalance);
        receiver.setAccountBalance(receiverBalance);

        LocalDateTime transactionTime = LocalDateTime.now();
        Transaction transferOut = new Transaction(
                BankDataStore.generateTransactionId(),
                sender.getAccountNumber(),
                receiver.getAccountNumber(),
                outgoingType,
                amount,
                description,
                transactionTime,
                senderBalance);

        Transaction transferIn = new Transaction(
                BankDataStore.generateTransactionId(),
                sender.getAccountNumber(),
                receiver.getAccountNumber(),
                incomingType,
                amount,
                description,
                transactionTime,
                receiverBalance);

        BankDataStore.allTransactions.add(transferOut);
        BankDataStore.allTransactions.add(transferIn);
        saveAndLog(auditAction, auditAction + " from " + sender.getAccountNumber()
                + " to " + receiver.getAccountNumber() + " amount PKR " + amount);
        NotificationService.addNotification(sender.getAccountNumber(), "Transfer of PKR "
                + String.format("%.2f", amount) + " to " + receiver.getAccountNumber() + " was successful.");
        NotificationService.addNotification(receiver.getAccountNumber(), "You received PKR "
                + String.format("%.2f", amount) + " from " + sender.getAccountNumber() + ".");
        return transferOut;
    }

    private void validateAmount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
    }

    private void validateDebitAllowed(Customer customer) {
        if (customer.isLocked()) {
            throw new IllegalArgumentException("Account is locked. Please contact branch staff.");
        }

        if (customer.isFrozen()) {
            throw new IllegalArgumentException("Your account is temporarily restricted. Please contact branch staff.");
        }
    }

    private String cleanDescription(String description, String defaultDescription) {
        if (description == null || description.trim().isEmpty()) {
            return defaultDescription;
        }
        return description.trim();
    }

    private void saveAndLog(String action, String detail) {
        FileDataStore.saveAll();
        AuditLogger.log(action, detail);
    }
}
