package org.example.exception;

public class OrderDoesntExistException extends RuntimeException {

    public OrderDoesntExistException() {
        super("Doesn't exist");
    }
}
