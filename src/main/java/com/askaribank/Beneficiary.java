package com.askaribank;

public class Beneficiary {

    private String beneficiaryId;
    private String ownerAccountNumber;
    private String beneficiaryName;
    private String beneficiaryAccountNumber;
    private String beneficiaryMobileNumber;
    private String bankName;

    public Beneficiary(String beneficiaryId, String ownerAccountNumber, String beneficiaryName,
                       String beneficiaryAccountNumber, String beneficiaryMobileNumber, String bankName) {
        this.beneficiaryId = beneficiaryId;
        this.ownerAccountNumber = ownerAccountNumber;
        this.beneficiaryName = beneficiaryName;
        this.beneficiaryAccountNumber = beneficiaryAccountNumber;
        this.beneficiaryMobileNumber = beneficiaryMobileNumber;
        this.bankName = bankName;
    }

    public String getBeneficiaryId() {
        return beneficiaryId;
    }

    public void setBeneficiaryId(String beneficiaryId) {
        this.beneficiaryId = beneficiaryId;
    }

    public String getOwnerAccountNumber() {
        return ownerAccountNumber;
    }

    public void setOwnerAccountNumber(String ownerAccountNumber) {
        this.ownerAccountNumber = ownerAccountNumber;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public String getBeneficiaryAccountNumber() {
        return beneficiaryAccountNumber;
    }

    public void setBeneficiaryAccountNumber(String beneficiaryAccountNumber) {
        this.beneficiaryAccountNumber = beneficiaryAccountNumber;
    }

    public String getBeneficiaryMobileNumber() {
        return beneficiaryMobileNumber;
    }

    public void setBeneficiaryMobileNumber(String beneficiaryMobileNumber) {
        this.beneficiaryMobileNumber = beneficiaryMobileNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    @Override
    public String toString() {
        String target = beneficiaryAccountNumber == null || beneficiaryAccountNumber.isBlank()
                ? beneficiaryMobileNumber
                : beneficiaryAccountNumber;
        return beneficiaryName + " - " + target;
    }
}
