package com.askaribank;

import java.time.LocalDateTime;

public class AccountApplication {

    private final String applicationId;
    private String fullName;
    private String cnic;
    private String mobileNumber;
    private String email;
    private String city;
    private String accountType;
    private String status;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
    private String staffRemarks;

    public AccountApplication(String applicationId, String fullName, String cnic,
                              String mobileNumber, String email, String city,
                              String accountType, String status, LocalDateTime submittedAt,
                              LocalDateTime reviewedAt, String staffRemarks) {
        this.applicationId = applicationId;
        this.fullName = fullName;
        this.cnic = cnic;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.city = city;
        this.accountType = accountType;
        this.status = status;
        this.submittedAt = submittedAt;
        this.reviewedAt = reviewedAt;
        this.staffRemarks = staffRemarks;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCnic() {
        return cnic;
    }

    public void setCnic(String cnic) {
        this.cnic = cnic;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public String getStaffRemarks() {
        return staffRemarks;
    }

    public void setStaffRemarks(String staffRemarks) {
        this.staffRemarks = staffRemarks;
    }
}
