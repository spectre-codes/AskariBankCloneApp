package com.askaribank;

import java.time.LocalDate;
import java.util.ArrayList;

public class ChequeBookRequestService {

    public ChequeBookRequest submitRequest(Customer customer, int leaves) {
        if (customer == null) {
            throw new IllegalArgumentException("No customer is logged in.");
        }

        if (leaves != 25 && leaves != 50 && leaves != 100) {
            throw new IllegalArgumentException("Please select 25, 50, or 100 leaves.");
        }

        ChequeBookRequest request = new ChequeBookRequest(
                BankDataStore.generateChequeRequestId(),
                customer.getAccountNumber(),
                customer.getCustomerFullName(),
                leaves,
                LocalDate.now(),
                "PENDING"
        );
        BankDataStore.allChequeBookRequests.add(request);
        FileDataStore.saveAll();
        AuditLogger.log("CHEQUE_REQUESTED", request.getRequestId() + " for " + customer.getAccountNumber());
        NotificationService.addNotification(customer.getAccountNumber(),
                "Cheque book request " + request.getRequestId() + " was submitted.");
        return request;
    }

    public ArrayList<ChequeBookRequest> getRequestsForAccount(String accountNumber) {
        ArrayList<ChequeBookRequest> requests = new ArrayList<>();
        if (accountNumber == null || accountNumber.isBlank()) {
            return requests;
        }

        for (ChequeBookRequest request : BankDataStore.allChequeBookRequests) {
            if (accountNumber.equalsIgnoreCase(request.getAccountNumber())) {
                requests.add(request);
            }
        }
        return requests;
    }

    public void markDispatched(ChequeBookRequest request) {
        updateStatus(request, "DISPATCHED", "Cheque book request dispatched.");
    }

    public void reject(ChequeBookRequest request) {
        updateStatus(request, "REJECTED", "Cheque book request rejected.");
    }

    private void updateStatus(ChequeBookRequest request, String status, String notification) {
        if (request == null) {
            throw new IllegalArgumentException("Please select a cheque request.");
        }

        request.setStatus(status);
        FileDataStore.saveAll();
        AuditLogger.log("CHEQUE_" + status, request.getRequestId() + " for " + request.getAccountNumber());
        NotificationService.addNotification(request.getAccountNumber(), notification + " Ref: " + request.getRequestId());
    }
}
