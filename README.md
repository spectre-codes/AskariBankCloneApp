````markdown
# Desktop Banking System

A professional educational desktop banking application built with Java 17, JavaFX, Maven, and programmatic Java UI controllers.

This is a university OOP + JavaFX semester project. It uses Java Collections with simple CSV/text file handling. There is no database, no SQLite, no web backend, and no networking.

## Disclaimer

This project was developed for educational and academic purposes only as part of an Object-Oriented Programming semester project.

It is a generic desktop banking simulation and does not represent, imitate, or provide services on behalf of any real bank or financial institution.

No real financial transactions, banking network integration, or production-level security mechanisms are implemented.

This project is intended solely for learning, demonstration, and evaluation purposes.

## Features

- Secure banking login with customer and staff login modes
- Staff dashboard and customer dashboard
- Banking operations dashboard with summary cards
- Customer account creation
- Automatic account number generation
- Customer storage using `ArrayList<Customer>` and simple `customers.csv`
- Deposit, withdrawal, and account transfer processing
- Customer transfer by account number or registered mobile number
- Saved beneficiaries/payees for faster customer transfers
- Utility bill payments for electricity, gas, internet, and water
- 4-digit transaction PIN confirmation for customer debit actions
- Customer account lockout after 3 failed login attempts
- Staff customer search by name, CNIC, account number, or phone
- Staff account freeze/unfreeze, unlock, and failed-attempt reset controls
- Mobile top-up
- Customer card block/unblock and PIN change
- Staff card block/unblock
- Customer cheque book requests with staff dispatch/reject workflow
- Customer notification center for account and service updates
- Transaction history stored using `ArrayList<Transaction>`
- Simple transaction persistence using `transactions.csv`
- Loan application processing for Personal Loan, Home Loan, and Car Loan
- Loan approval and rejection with clear reasons
- In-memory loan application storage using `ArrayList<LoanApplication>`
- EMI calculator
- Amortization schedule table
- Bank statement screen with customer details, current balance, transaction history, and related loan applications
- Customer mini statement showing last 10 transactions
- Staff full statement export and customer mini statement export as CSV
- Simple audit log saved in `audit_log.txt`
- Pure programmatic JavaFX screens built entirely in Java

## OOP Concepts Used

- Classes and objects
- Encapsulation with private fields and getters/setters
- Abstract class: `Loan`
- Inheritance:
  - `PersonalLoan extends Loan`
  - `HomeLoan extends Loan`
  - `CarLoan extends Loan`
- Interface: `LoanProcessable`
- Polymorphism using `Loan` parent reference
- Custom exceptions:
  - `InvalidLoanAmountException`
  - `InsufficientSalaryException`
  - `AccountNotFoundException`
  - `InsufficientBalanceException`
- Collections:
  - `ArrayList<Customer>`
  - `ArrayList<LoanApplication>`
  - `ArrayList<Transaction>`
  - `ArrayList<Beneficiary>`
  - `ArrayList<ChequeBookRequest>`
  - `ArrayList<Notification>`
- Static members in `BankDataStore`
- JavaFX programmatic UI + Controller pattern
- TableView, ComboBox, TextField, Labels, event handlers, and scene switching

## How To Run

Make sure JDK 17+ and Maven are installed.

```bash
mvn clean javafx:run
````

The application entry point is:

```text
com.askaribank.AppLauncher
```

The internal package name remains from the original academic project structure and is not shown as public application branding.

## Demo Login

```text
Staff username: admin
Staff password: admin

Customer account: use a generated demo account
Customer CNIC: use the CNIC stored in the demo customer data
Customer PIN: use the demo PIN configured in the project
```

## Important Limitation

Customers, transactions, cards, beneficiaries, cheque requests, notifications, and audit logs are saved in simple files under the `data` folder.

Loan applications remain in memory for project simplicity.

This application is intended only as an educational desktop banking simulation.

```

