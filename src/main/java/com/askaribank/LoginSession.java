package com.askaribank;

public class LoginSession {

    private static Customer loggedInCustomer;
    private static boolean staffLoggedIn;

    private LoginSession() {
    }

    public static void loginStaff() {
        staffLoggedIn = true;
        loggedInCustomer = null;
    }

    public static void loginCustomer(Customer customer) {
        loggedInCustomer = customer;
        staffLoggedIn = false;
    }

    public static Customer getLoggedInCustomer() {
        return loggedInCustomer;
    }

    public static boolean isStaffLoggedIn() {
        return staffLoggedIn;
    }

    public static void clear() {
        loggedInCustomer = null;
        staffLoggedIn = false;
    }
}
