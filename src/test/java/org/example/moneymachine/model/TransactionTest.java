package org.example.moneymachine.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
@DisplayName("Transaction unit tests")
class TransactionTest {

    private Transaction depositTransaction;
    private Transaction withdrawTransaction;
    private final String userId = "user123";
    private final double amount = 100.0;
    private LocalDateTime timestamp;

    @BeforeEach
    void setUp() {
        timestamp = LocalDateTime.now();
        depositTransaction = new Transaction(userId, Transaction.Type.DEPOSIT, amount, timestamp);
        withdrawTransaction = new Transaction(userId, Transaction.Type.WITHDRAW, amount, timestamp);
    }

    @Test
    @DisplayName("constructor should store fields correctly for deposit")
    void constructor_shouldStoreFieldsCorrectlyForDeposit() {
        // Act & Assert
        assertEquals(userId, depositTransaction.getUserId());
        assertEquals(Transaction.Type.DEPOSIT, depositTransaction.getType());
        assertEquals(amount, depositTransaction.getAmount());
        assertEquals(timestamp, depositTransaction.getTimestamp());
    }

    @Test
    @DisplayName("constructor should store fields correctly for withdraw")
    void constructor_shouldStoreFieldsCorrectlyForWithdraw() {
        // Act & Assert
        assertEquals(userId, withdrawTransaction.getUserId());
        assertEquals(Transaction.Type.WITHDRAW, withdrawTransaction.getType());
        assertEquals(amount, withdrawTransaction.getAmount());
        assertEquals(timestamp, withdrawTransaction.getTimestamp());
    }

    @Test
    @DisplayName("transactions of different types can be distinguished")
    void transactions_shouldDifferentiateTypes() {
        // Act
        Transaction.Type depositType = depositTransaction.getType();
        Transaction.Type withdrawType = withdrawTransaction.getType();

        // Assert
        assertNotEquals(depositType, withdrawType);
    }
}