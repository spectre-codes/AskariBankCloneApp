package com.askaribank;

import java.time.LocalDateTime;

public class AccountApplicationService {

    public AccountApplication submitApplication(String fullName, String cnic, String mobileNumber,
                                                String email, String city, String accountType) {
        String cleanedFullName = clean(fullName);
        String cleanedCnic = cleanDigits(cnic);
        String cleanedMobileNumber = cleanDigits(mobileNumber);
        String cleanedEmail = clean(email);
        String cleanedCity = clean(city);
        String cleanedAccountType = clean(accountType);

        if (cleanedFullName.isEmpty() || cleanedCnic.isEmpty() || cleanedMobileNumber.isEmpty()
                || cleanedEmail.isEmpty() || cleanedCity.isEmpty() || cleanedAccountType.isEmpty()) {
            throw new IllegalArgumentException("Please complete all application fields.");
        }

        if (!cleanedCnic.matches("\\d{13}")) {
            throw new IllegalArgumentException("CNIC must contain exactly 13 digits.");
        }

        if (!cleanedMobileNumber.matches("\\d{10,12}")) {
            throw new IllegalArgumentException("Mobile number must contain 10 to 12 digits.");
        }

        if (BankDataStore.findCustomerByCnic(cleanedCnic) != null) {
            throw new IllegalArgumentException("An active customer already exists for this CNIC.");
        }

        if (BankDataStore.findCustomerByPhoneNumber(cleanedMobileNumber) != null) {
            throw new IllegalArgumentException("An active customer already exists for this mobile number.");
        }

        for (AccountApplication application : BankDataStore.allAccountApplications) {
            if ("PENDING".equals(application.getStatus())
                    && cleanedCnic.equals(cleanDigits(application.getCnic()))) {
                throw new IllegalArgumentException("A pending application already exists for this CNIC.");
            }
        }

        AccountApplication application = new AccountApplication(
                generateApplicationId(),
                cleanedFullName,
                cleanedCnic,
                cleanedMobileNumber,
                cleanedEmail,
                cleanedCity,
                cleanedAccountType,
                "PENDING",
                LocalDateTime.now(),
                null,
                ""
        );

        BankDataStore.allAccountApplications.add(application);
        FileDataStore.saveAll();
        AuditLogger.log("ACCOUNT_APPLICATION_SUBMITTED", application.getApplicationId()
                + " for " + cleanedCnic);
        return application;
    }

    public void approve(AccountApplication application, String remarks) {
        review(application, "APPROVED", remarks);
    }

    public void reject(AccountApplication application, String remarks) {
        review(application, "REJECTED", remarks);
    }

    private void review(AccountApplication application, String status, String remarks) {
        if (application == null) {
            throw new IllegalArgumentException("Please select an application request.");
        }

        application.setStatus(status);
        application.setReviewedAt(LocalDateTime.now());
        application.setStaffRemarks(clean(remarks));
        FileDataStore.saveAll();
        AuditLogger.log("ACCOUNT_APPLICATION_" + status, application.getApplicationId());
    }

    private String generateApplicationId() {
        int highestNumber = 0;
        for (AccountApplication application : BankDataStore.allAccountApplications) {
            highestNumber = Math.max(highestNumber, readTrailingNumber(application.getApplicationId()));
        }
        return String.format("APP-%05d", highestNumber + 1);
    }

    private int readTrailingNumber(String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }

        try {
            return Integer.parseInt(value.replaceAll("\\D", ""));
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private String cleanDigits(String value) {
        return clean(value).replaceAll("\\D", "");
    }
}
