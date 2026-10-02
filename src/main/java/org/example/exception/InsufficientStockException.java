package org.example.exception;

public class InsufficientStockException extends Exception{
    public InsufficientStockException() {
        super("Insufficient stock");
    }
}
