package com.askaribank;

public class LoanApplication implements LoanProcessable {

    private String applicantName;
    private String linkedAccountNumber;
    private Loan loan;
    private double monthlySalary;
    private int creditScore;
    private String rejectionReason;

    public LoanApplication(String applicantName, String linkedAccountNumber, Loan loan,
                           double monthlySalary, int creditScore) {
        this.applicantName = applicantName;
        this.linkedAccountNumber = linkedAccountNumber;
        this.loan = loan;
        this.monthlySalary = monthlySalary;
        this.creditScore = creditScore;
        this.rejectionReason = "";
    }

    @Override
    public void processApplication() throws InvalidLoanAmountException, InsufficientSalaryException, AccountNotFoundException {
        if (BankDataStore.findCustomerByAccountNumber(linkedAccountNumber) == null) {
            throw new AccountNotFoundException("Account not found for account number " + linkedAccountNumber + ".");
        }

        if (loan.getPrincipalAmount() <= 0) {
            throw new InvalidLoanAmountException("Invalid loan amount. Amount must be greater than zero.");
        }

        if (loan.getPrincipalAmount() > loan.getMaximumAllowedAmount()) {
            throw new InvalidLoanAmountException("Loan amount exceeds allowed limit for "
                    + loan.getLoanType() + ". Maximum allowed amount is PKR "
                    + String.format("%.0f", loan.getMaximumAllowedAmount()) + ".");
        }

        if (monthlySalary < loan.getMinimumRequiredSalary()) {
            throw new InsufficientSalaryException("Salary too low for " + loan.getLoanType()
                    + ". Minimum monthly salary required is PKR "
                    + String.format("%.0f", loan.getMinimumRequiredSalary()) + ".");
        }

        if (creditScore < loan.getMinimumCreditScore()) {
            reject("Credit score too low for " + loan.getLoanType()
                    + ". Minimum required score is " + loan.getMinimumCreditScore() + ".");
            return;
        }

        approve();
    }

    @Override
    public void approve() {
        if (!"APPROVED".equals(loan.getApprovalStatus())) {
            BankDataStore.totalLoansIssued++;
        }
        loan.approve();
        rejectionReason = "";
    }

    @Override
    public void reject(String reason) {
        loan.reject();
        rejectionReason = reason;
    }

    @Override
    public double calculateMonthlyInstallment() {
        return loan.calculateMonthlyInstallment();
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getLinkedAccountNumber() {
        return linkedAccountNumber;
    }

    public void setLinkedAccountNumber(String linkedAccountNumber) {
        this.linkedAccountNumber = linkedAccountNumber;
    }

    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }

    public double getMonthlySalary() {
        return monthlySalary;
    }

    public void setMonthlySalary(double monthlySalary) {
        this.monthlySalary = monthlySalary;
    }

    public int getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(int creditScore) {
        this.creditScore = creditScore;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getLoanId() {
        return loan.getLoanId();
    }

    public String getLoanType() {
        return loan.getLoanType();
    }

    public double getLoanAmount() {
        return loan.getPrincipalAmount();
    }

    public double getAnnualInterestRate() {
        return loan.getAnnualInterestRate();
    }

    public int getTenureMonths() {
        return loan.getTenureMonths();
    }

    public int getTenureYears() {
        return loan.getTenureMonths() / 12;
    }

    public String getStatus() {
        return loan.getApprovalStatus();
    }

    public double getMonthlyInstallment() {
        return calculateMonthlyInstallment();
    }
}
