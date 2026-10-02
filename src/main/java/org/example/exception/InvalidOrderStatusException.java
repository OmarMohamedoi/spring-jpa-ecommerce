package org.example.exception;

public class InvalidOrderStatusException extends RuntimeException {
    public InvalidOrderStatusException() {
        super("OrderStatus is invalid");
    }
}
