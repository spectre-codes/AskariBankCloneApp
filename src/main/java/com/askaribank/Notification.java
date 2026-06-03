package com.askaribank;

import java.time.LocalDateTime;

public class Notification {

    private String notificationId;
    private String accountNumber;
    private String message;
    private LocalDateTime timestamp;
    private boolean read;

    public Notification(String notificationId, String accountNumber, String message,
                        LocalDateTime timestamp, boolean read) {
        this.notificationId = notificationId;
        this.accountNumber = accountNumber;
        this.message = message;
        this.timestamp = timestamp;
        this.read = read;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return read;
    }

    public boolean getRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
