package org.example.exception;

public class DuplicateCustomerException extends RuntimeException {
    public DuplicateCustomerException() {
        super("Duplicate customer exception");
    }
}
