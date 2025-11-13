package org.example.moneymachine.service;

import org.example.moneymachine.model.Transaction;
import org.example.moneymachine.model.User;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("unit")
@DisplayName("TransactionLogger unit tests")
class TransactionLoggerTest {

    private TransactionLogger logger;
    private User mockUser;

    @BeforeEach
    void setUp() {
        logger = new TransactionLogger();
        mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn("user123");
    }

    /**
     * Helper method to reduce repetition of asserts
     */
    private void assertTransaction(Transaction t, String userId, Transaction.Type type, double amount) {
        assertEquals(userId, t.getUserId());
        assertEquals(type, t.getType());
        assertEquals(amount, t.getAmount());
        assertNotNull(t.getTimestamp());
    }

    @Nested
    @DisplayName("Deposit Transactions")
    class DepositTests {
        @Test
        @DisplayName("logDeposit stores a deposit transaction correctly")
        void logDeposit_shouldStoreDepositTransaction() {
            // Act
            logger.logDeposit(mockUser, 200.0);

            // Assert
            List<Transaction> transactions = logger.getTransactionsForUser(mockUser);
            assertEquals(1, transactions.size());
            assertTransaction(transactions.get(0), "user123", Transaction.Type.DEPOSIT, 200.0);
        }
    }

    @Nested
    @DisplayName("Withdraw Transactions")
    class WithdrawTests {
        @Test
        @DisplayName("logWithdraw stores a withdraw transaction correctly")
        void logWithdraw_shouldStoreWithdrawTransaction() {
            // Act
            logger.logWithdraw(mockUser, 500.0);

            // Assert
            List<Transaction> transactions = logger.getTransactionsForUser(mockUser);
            assertEquals(1, transactions.size());
            assertTransaction(transactions.get(0), "user123", Transaction.Type.WITHDRAW, 500.0);
        }
    }

    @Test
    @DisplayName("getTransactionsForUser returns transactions in order they were logged")
    void getTransactionsForUser_shouldReturnTransactionsInOrder() {
        // Arrange
        logger.logDeposit(mockUser, 100.0);
        logger.logWithdraw(mockUser, 50.0);
        logger.logDeposit(mockUser, 75.0);

        // Act
        List<Transaction> transactions = logger.getTransactionsForUser(mockUser);

        // Assert
        assertEquals(3, transactions.size());
        assertTransaction(transactions.get(0), "user123", Transaction.Type.DEPOSIT, 100.0);
        assertTransaction(transactions.get(1), "user123", Transaction.Type.WITHDRAW, 50.0);
        assertTransaction(transactions.get(2), "user123", Transaction.Type.DEPOSIT, 75.0);
    }

    @Test
    @DisplayName("getTransactionsForUser returns empty list for user with no transactions")
    void getTransactionsForUser_shouldReturnEmptyListIfNoTransactions() {
        // Act
        List<Transaction> transactions = logger.getTransactionsForUser(mockUser);

        // Assert
        assertNotNull(transactions);
        assertTrue(transactions.isEmpty());
    }

    @Test
    @DisplayName("getAllTransactions returns all transactions for all users")
    void getAllTransactions_shouldReturnAllTransactions() {
        // Arrange
        User user1 = mock(User.class);
        when(user1.getId()).thenReturn("user1");
        User user2 = mock(User.class);
        when(user2.getId()).thenReturn("user2");

        logger.logDeposit(user1, 100.0);
        logger.logWithdraw(user2, 50.0);

        // Act
        List<Transaction> allTransactions = logger.getAllTransactions();

        // Assert
        assertEquals(2, allTransactions.size());
        assertTransaction(allTransactions.get(0), "user1", Transaction.Type.DEPOSIT, 100.0);
        assertTransaction(allTransactions.get(1), "user2", Transaction.Type.WITHDRAW, 50.0);
    }
}