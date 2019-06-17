package com.company.file_management;

public class CannotCheckoutException extends Exception {
    public CannotCheckoutException(String message) {
        super(message);
    }
}
