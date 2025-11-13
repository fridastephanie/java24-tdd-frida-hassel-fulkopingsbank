package org.example.moneymachine.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
@DisplayName("User unit tests")
class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("user123", "1234", 1000.0);
    }

    @Test
    @DisplayName("constructor should initialize fields correctly")
    void constructor_shouldInitializeFields() {
        // Act
        User newUser = new User("u1", "4321", 500.0);

        // Assert
        assertEquals("u1", newUser.getId());
        assertEquals("4321", newUser.getPin());
        assertEquals(500.0, newUser.getBalance());
        assertEquals(0, newUser.getFailedAttempts());
        assertFalse(newUser.isLocked());
    }

    @Nested
    @DisplayName("Balance Operations")
    class BalanceOperationsTests {

        @Test
        @DisplayName("deposit should increase balance")
        void deposit_shouldIncreaseBalance() {
            // Act
            user.deposit(500.0);

            // Assert
            assertEquals(1500.0, user.getBalance());
        }

        @Test
        @DisplayName("withdraw should decrease balance")
        void withdraw_shouldDecreaseBalance() {
            // Act
            user.withdraw(300.0);

            // Assert
            assertEquals(700.0, user.getBalance());
        }
    }

    @Nested
    @DisplayName("Failed Attempts and Locking")
    class FailedAttemptsAndLockingTests {

        @Test
        @DisplayName("failed attempts increment correctly")
        void failedAttempts_shouldIncrement() {
            // Assert initial state
            assertEquals(0, user.getFailedAttempts());

            // Act
            user.incrementFailedAttempts();

            // Assert
            assertEquals(1, user.getFailedAttempts());
        }

        @Test
        @DisplayName("failed attempts reset correctly")
        void failedAttempts_shouldReset() {
            // Arrange
            user.incrementFailedAttempts();
            assertEquals(1, user.getFailedAttempts());

            // Act
            user.resetFailedAttempts();

            // Assert
            assertEquals(0, user.getFailedAttempts());
        }

        @Test
        @DisplayName("lockCard sets isLocked to true")
        void lockCard_shouldLockUser() {
            // Act
            user.lockCard();

            // Assert
            assertTrue(user.isLocked());
        }
    }
}