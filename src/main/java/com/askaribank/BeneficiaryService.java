package com.askaribank;

import java.util.ArrayList;

public class BeneficiaryService {

    public ArrayList<Beneficiary> getBeneficiariesForOwner(String ownerAccountNumber) {
        ArrayList<Beneficiary> beneficiaries = new ArrayList<>();
        if (ownerAccountNumber == null || ownerAccountNumber.isBlank()) {
            return beneficiaries;
        }

        for (Beneficiary beneficiary : BankDataStore.allBeneficiaries) {
            if (ownerAccountNumber.equalsIgnoreCase(beneficiary.getOwnerAccountNumber())) {
                beneficiaries.add(beneficiary);
            }
        }
        return beneficiaries;
    }

    public Beneficiary addBeneficiary(Customer owner, String name, String accountNumber,
                                      String mobileNumber, String bankName) {
        if (owner == null) {
            throw new IllegalArgumentException("No customer is logged in.");
        }

        String cleanedName = clean(name);
        String cleanedAccountNumber = clean(accountNumber);
        String cleanedMobileNumber = clean(mobileNumber);
        String cleanedBankName = clean(bankName);

        if (cleanedName.isEmpty()) {
            throw new IllegalArgumentException("Beneficiary name is required.");
        }

        if (cleanedAccountNumber.isEmpty() && cleanedMobileNumber.isEmpty()) {
            throw new IllegalArgumentException("Enter beneficiary account number or mobile number.");
        }

        if (!cleanedAccountNumber.isEmpty()) {
            Customer account = BankDataStore.findCustomerByAccountNumber(cleanedAccountNumber);
            if (account == null) {
                throw new IllegalArgumentException("Beneficiary account number was not found.");
            }

            if (owner.getAccountNumber().equalsIgnoreCase(cleanedAccountNumber)) {
                throw new IllegalArgumentException("You cannot add your own account as a beneficiary.");
            }
        }

        for (Beneficiary beneficiary : getBeneficiariesForOwner(owner.getAccountNumber())) {
            if (!cleanedAccountNumber.isEmpty()
                    && cleanedAccountNumber.equalsIgnoreCase(beneficiary.getBeneficiaryAccountNumber())) {
                throw new IllegalArgumentException("This beneficiary account is already saved.");
            }

            if (!cleanedMobileNumber.isEmpty()
                    && cleanedMobileNumber.equals(beneficiary.getBeneficiaryMobileNumber())) {
                throw new IllegalArgumentException("This beneficiary mobile number is already saved.");
            }
        }

        Beneficiary beneficiary = new Beneficiary(
                BankDataStore.generateBeneficiaryId(),
                owner.getAccountNumber(),
                cleanedName,
                cleanedAccountNumber,
                cleanedMobileNumber,
                cleanedBankName.isEmpty() ? BankDataStore.bankName : cleanedBankName
        );
        BankDataStore.allBeneficiaries.add(beneficiary);
        FileDataStore.saveAll();
        AuditLogger.log("BENEFICIARY_ADDED", owner.getAccountNumber() + " saved " + cleanedName);
        return beneficiary;
    }

    public void deleteBeneficiary(Beneficiary beneficiary) {
        if (beneficiary == null) {
            throw new IllegalArgumentException("Please select a beneficiary.");
        }

        BankDataStore.allBeneficiaries.remove(beneficiary);
        FileDataStore.saveAll();
        AuditLogger.log("BENEFICIARY_DELETED", beneficiary.getOwnerAccountNumber()
                + " removed " + beneficiary.getBeneficiaryName());
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
