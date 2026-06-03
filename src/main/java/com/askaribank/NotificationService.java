package com.askaribank;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class NotificationService {

    public static void addNotification(String accountNumber, String message) {
        if (accountNumber == null || accountNumber.isBlank() || message == null || message.isBlank()) {
            return;
        }

        BankDataStore.allNotifications.add(new Notification(
                BankDataStore.generateNotificationId(),
                accountNumber.trim(),
                message.trim(),
                LocalDateTime.now(),
                false
        ));
        FileDataStore.saveAll();
    }

    public static ArrayList<Notification> getNotificationsForAccount(String accountNumber) {
        ArrayList<Notification> notifications = new ArrayList<>();
        if (accountNumber == null || accountNumber.isBlank()) {
            return notifications;
        }

        for (int index = BankDataStore.allNotifications.size() - 1; index >= 0; index--) {
            Notification notification = BankDataStore.allNotifications.get(index);
            if (accountNumber.equalsIgnoreCase(notification.getAccountNumber())) {
                notifications.add(notification);
            }
        }
        return notifications;
    }

    public static long getUnreadCount(String accountNumber) {
        long count = 0;
        for (Notification notification : getNotificationsForAccount(accountNumber)) {
            if (!notification.isRead()) {
                count++;
            }
        }
        return count;
    }

    public static void markAllRead(String accountNumber) {
        for (Notification notification : BankDataStore.allNotifications) {
            if (accountNumber != null && accountNumber.equalsIgnoreCase(notification.getAccountNumber())) {
                notification.setRead(true);
            }
        }
        FileDataStore.saveAll();
    }
}
