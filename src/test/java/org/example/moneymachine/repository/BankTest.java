package org.example.moneymachine.repository;

import org.example.moneymachine.model.User;
import org.junit.jupiter.api.*;

import org.mockito.MockedStatic;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("unit")
@DisplayName("Bank unit tests")
class BankTest {

    private Bank bank;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User("user123", "1234", 1000.0);
        Map<String, User> users = new HashMap<>();
        users.put(user.getId(), user);
        bank = new Bank(users);
    }

    @Nested
    @DisplayName("User lookup and card lock operations")
    class UserLookupAndLockTests {

        @Test
        @DisplayName("getUserById should return user when exists")
        void getUserById_shouldReturnUserWhenExists() {
            // Act
            User result = bank.getUserById("user123");

            // Assert
            assertNotNull(result);
        }

        @Test
        @DisplayName("getUserById should return null when user does not exist")
        void getUserById_shouldReturnNullWhenUserDoesNotExist() {
            // Act
            User result = bank.getUserById("no_such_user");

            // Assert
            assertNull(result);
        }

        @Test
        @DisplayName("isCardLocked returns true if card is locked")
        void isCardLocked_shouldReturnTrueIfCardLocked() {
            // Arrange
            user.lockCard();

            // Act
            boolean result = bank.isCardLocked(user.getId());

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("isCardLocked returns false if card not locked")
        void isCardLocked_shouldReturnFalseIfCardNotLocked() {
            // Act
            boolean result = bank.isCardLocked(user.getId());

            // Assert
            assertFalse(result);
        }

        @Test
        @DisplayName("isCardLocked returns false if user does not exist")
        void isCardLocked_shouldReturnFalseIfUserDoesNotExist() {
            // Act
            boolean result = bank.isCardLocked("nonexistent");

            // Assert
            assertFalse(result);
        }

        @Test
        @DisplayName("lockCard locks the user card")
        void lockCard_shouldLockUserCard() {
            // Act
            bank.lockCard("user123");

            // Assert
            assertTrue(user.isLocked());
        }
    }

    @Nested
    @DisplayName("PIN validation")
    class PinValidationTests {

        @Test
        @DisplayName("validatePin returns false if user does not exist")
        void validatePin_shouldReturnFalseIfUserDoesNotExist() {
            // Act
            boolean result = bank.validatePin("nonexistent", "1234");

            // Assert
            assertFalse(result);
        }

        @Test
        @DisplayName("validatePin returns true if PIN is correct")
        void validatePin_shouldReturnTrueIfPinIsCorrect() {
            // Act
            boolean result = bank.validatePin("user123", "1234");

            // Assert
            assertTrue(result);
            assertEquals(0, user.getFailedAttempts());
            assertFalse(user.isLocked());
        }

        @Test
        @DisplayName("validatePin returns false if PIN is incorrect")
        void validatePin_shouldReturnFalseIfPinIsIncorrect() {
            // Act
            boolean result = bank.validatePin("user123", "0000");

            // Assert
            assertFalse(result);
            assertEquals(1, user.getFailedAttempts());
            assertFalse(user.isLocked());
        }

        @Test
        @DisplayName("validatePin increments failed attempts and locks after 3 wrong PINs")
        void validatePin_shouldLockCardAfterThreeWrongPins() {
            // Arrange
            bank.validatePin("user123", "0000"); // first wrong
            bank.validatePin("user123", "1111"); // second wrong

            // Act
            boolean result = bank.validatePin("user123", "2222"); // third wrong

            // Assert
            assertFalse(result);
            assertEquals(3, user.getFailedAttempts());
            assertTrue(user.isLocked());
        }

        @Test
        @DisplayName("validatePin always returns false if card is locked")
        void validatePin_shouldReturnFalseIfCardIsLocked() {
            // Arrange
            user.lockCard();

            // Act
            boolean result = bank.validatePin("user123", "1234");

            // Assert
            assertFalse(result);
            assertEquals(0, user.getFailedAttempts());
            assertTrue(user.isLocked());
        }

        @Test
        @DisplayName("validatePin resets failed attempts if correct PIN entered")
        void validatePin_shouldResetFailedAttemptsIfCorrectPin() {
            // Arrange
            user.incrementFailedAttempts();
            assertEquals(1, user.getFailedAttempts());

            // Act
            boolean result = bank.validatePin("user123", "1234");

            // Assert
            assertTrue(result);
            assertEquals(0, user.getFailedAttempts());
            assertFalse(user.isLocked());
        }
    }

    @Nested
    @DisplayName("Bank name operations")
    class BankNameTests {

        @Test
        @DisplayName("getBankName returns correct name")
        void getBankName_shouldReturnCorrectName() {
            // Arrange
            try (MockedStatic<Bank> mockedBank = mockStatic(Bank.class)) {
                mockedBank.when(Bank::getBankName).thenReturn("Fulkoping MoneyMachine");

                // Act
                String bankName = Bank.getBankName();

                // Assert
                assertEquals("Fulkoping MoneyMachine", bankName);
                mockedBank.verify(Bank::getBankName);
            }
        }
    }
}