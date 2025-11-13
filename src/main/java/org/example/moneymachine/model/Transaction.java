package org.example.moneymachine.model;

import java.time.LocalDateTime;

public class Transaction {

    public enum Type {
        DEPOSIT, WITHDRAW
    }

    private final String userId;
    private final Type type;
    private final double amount;
    private final LocalDateTime timestamp;

    public Transaction(String userId, Type type, double amount, LocalDateTime timestamp) {
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public Type getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
