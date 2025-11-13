package org.example.moneymachine.service;

import org.example.moneymachine.model.Transaction;
import org.example.moneymachine.model.User;
import org.example.moneymachine.repository.Bank;
import org.example.moneymachine.repository.BankInterface;
import org.junit.jupiter.api.*;

import org.mockito.MockedStatic;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("unit")
@DisplayName("ATM unit tests")
class ATMTest {

    private BankInterface mockBank;
    private User mockUser;
    private TransactionLogger mockLogger;
    private ATM atm;

    @BeforeEach
    void setUp() {
        // Arrange common mocks
        mockBank = mock(BankInterface.class);
        mockUser = mock(User.class);
        mockLogger = mock(TransactionLogger.class);
        atm = new ATM(mockBank, mockLogger);
        when(mockUser.getId()).thenReturn("user123");
    }

    /**
     * Helper to insert user card and setup mocks
     */
    private void insertUserCard() {
        // Arrange
        when(mockBank.getUserById("user123")).thenReturn(mockUser);
        when(mockBank.isCardLocked("user123")).thenReturn(false);
        // Act
        atm.insertCard("user123");
    }

    /**
     * Helper to assert user message and optionally current user
     */
    private void assertUserMessage(String expectedMessage, User expectedUser) {
        // Assert
        assertEquals(expectedMessage, atm.getUserMessage());
        assertEquals(expectedUser, atm.getCurrentUser());
    }

    @Nested
    @DisplayName("Card Operations")
    class CardOperations {

        @Test
        @DisplayName("insertCard returns false and sets message when card is locked")
        void insertCard_shouldReturnFalseWhenCardLocked() {
            // Arrange
            when(mockBank.isCardLocked("user123")).thenReturn(true);

            // Act
            boolean result = atm.insertCard("user123");

            // Assert
            assertFalse(result);
            assertUserMessage("Card is locked", null);
            verify(mockBank).isCardLocked("user123");
            verify(mockBank, never()).getUserById(anyString());
        }

        @Test
        @DisplayName("insertCard returns true and sets message when card accepted")
        void insertCard_shouldReturnTrueWhenCardAccepted() {
            // Arrange
            when(mockBank.isCardLocked("user123")).thenReturn(false);
            when(mockBank.getUserById("user123")).thenReturn(mockUser);

            // Act
            boolean result = atm.insertCard("user123");

            // Assert
            assertTrue(result);
            assertUserMessage("Card accepted", mockUser);
            verify(mockBank).getUserById("user123");
        }

        @Test
        @DisplayName("ejectCard succeeds when user inserted")
        void ejectCard_shouldSucceedWhenUserInserted() {
            // Arrange & Act
            insertUserCard(); // Already performs insertCard

            boolean result = atm.ejectCard();

            // Assert
            assertTrue(result);
            assertUserMessage("Card ejected", null);
        }

        @Test
        @DisplayName("ejectCard fails when no user inserted")
        void ejectCard_shouldFailWhenNoUser() {
            // Act
            boolean result = atm.ejectCard();

            // Assert
            assertFalse(result);
            assertUserMessage("No card inserted", null);
        }
    }

    @Nested
    @DisplayName("PIN Operations")
    class PinOperations {

        @Test
        @DisplayName("enterPin returns true for correct PIN")
        void enterPin_shouldReturnTrueForCorrectPin() {
            // Arrange
            insertUserCard();
            when(mockBank.validatePin("user123", "1234")).thenReturn(true);

            // Act
            boolean result = atm.enterPin("1234");

            // Assert
            assertTrue(result);
            assertUserMessage("PIN correct, you are logged in", mockUser);
        }

        @Test
        @DisplayName("enterPin returns false for incorrect PIN")
        void enterPin_shouldReturnFalseForIncorrectPin() {
            // Arrange
            insertUserCard();
            when(mockBank.validatePin("user123", "0000")).thenReturn(false);

            // Act
            boolean result = atm.enterPin("0000");

            // Assert
            assertFalse(result);
            assertUserMessage("PIN incorrect", mockUser);
        }

        @Test
        @DisplayName("enterPin returns false when card is locked")
        void enterPin_shouldReturnFalseWhenCardLocked() {
            // Arrange
            insertUserCard();
            when(mockBank.validatePin("user123", "0000")).thenReturn(false);
            when(mockBank.isCardLocked("user123")).thenReturn(true);

            // Act
            boolean result = atm.enterPin("0000");

            // Assert
            assertFalse(result);
            assertUserMessage("Card is locked", mockUser);
        }
    }

    @Nested
    @DisplayName("Balance Operations")
    class BalanceOperations {

        @Test
        @DisplayName("checkBalance returns balance when user inserted")
        void checkBalance_shouldReturnBalance() {
            // Arrange
            insertUserCard();
            when(mockUser.getBalance()).thenReturn(750.0);

            // Act
            double balance = atm.checkBalance();

            // Assert
            assertEquals(750.0, balance);
            assertUserMessage("Your balance is: 750.0", mockUser);
        }

        @Test
        @DisplayName("checkBalance returns 0 when no user inserted")
        void checkBalance_shouldReturnZeroWhenNoUser() {
            // Act
            double balance = atm.checkBalance();

            // Assert
            assertEquals(0.0, balance);
            assertUserMessage("No user inserted", null);
        }
    }

    @Nested
    @DisplayName("Transaction Operations")
    class TransactionOperations {

        @Test
        @DisplayName("deposit succeeds when user inserted")
        void deposit_shouldSucceedWhenUserInserted() {
            // Arrange
            insertUserCard();

            // Act
            atm.deposit(200.0);

            // Assert
            verify(mockUser).deposit(200.0);
            verify(mockLogger).logDeposit(mockUser, 200.0);
            assertUserMessage("Deposit successful: 200.0", mockUser);
        }

        @Test
        @DisplayName("deposit fails when no user inserted")
        void deposit_shouldFailWhenNoUser() {
            // Act
            atm.deposit(200.0);

            // Assert
            assertUserMessage("No user inserted, deposit failed", null);
        }

        @Test
        @DisplayName("withdraw succeeds with sufficient funds")
        void withdraw_shouldSucceedWithSufficientFunds() {
            // Arrange
            insertUserCard();
            when(mockUser.getBalance()).thenReturn(500.0);

            // Act
            boolean result = atm.withdraw(300.0);

            // Assert
            assertTrue(result);
            verify(mockUser).withdraw(300.0);
            verify(mockLogger).logWithdraw(mockUser, 300.0);
            assertUserMessage("Withdrawal successful: 300.0", mockUser);
        }

        @Test
        @DisplayName("withdraw fails with insufficient funds")
        void withdraw_shouldFailWithInsufficientFunds() {
            // Arrange
            insertUserCard();
            when(mockUser.getBalance()).thenReturn(100.0);

            // Act
            boolean result = atm.withdraw(200.0);

            // Assert
            assertFalse(result);
            verify(mockUser, never()).withdraw(anyDouble());
            assertUserMessage("Insufficient funds", mockUser);
        }

        @Test
        @DisplayName("withdraw fails when no user inserted")
        void withdraw_shouldFailWhenNoUser() {
            // Act
            boolean result = atm.withdraw(100.0);

            // Assert
            assertFalse(result);
            assertUserMessage("No card inserted", null);
        }

        @Test
        @DisplayName("getTransactionHistory returns all transactions")
        void getTransactionHistory_shouldReturnAllTransactions() {
            // Arrange
            insertUserCard();
            when(mockLogger.getTransactionsForUser(mockUser)).thenReturn(List.of(
                    new Transaction("user123", Transaction.Type.DEPOSIT, 200.0, LocalDateTime.now()),
                    new Transaction("user123", Transaction.Type.WITHDRAW, 100.0, LocalDateTime.now())
            ));

            // Act
            List<Transaction> history = atm.getTransactionHistory();

            // Assert
            assertEquals(2, history.size());
            assertEquals(Transaction.Type.DEPOSIT, history.get(0).getType());
            assertEquals(Transaction.Type.WITHDRAW, history.get(1).getType());
        }
    }

    @Nested
    @DisplayName("Bank Info Operations")
    class BankInfoOperations {

        @Test
        @DisplayName("getConnectedBankName returns bank name from static method")
        void getConnectedBankName_shouldReturnBankName() {
            // Arrange & Act
            try (MockedStatic<Bank> mockedBank = mockStatic(Bank.class)) {
                mockedBank.when(Bank::getBankName).thenReturn("Fulkoping MoneyMachine");

                String result = atm.getConnectedBankName();

                // Assert
                assertEquals("Fulkoping MoneyMachine", result);
                mockedBank.verify(Bank::getBankName);
            }
        }
    }
}
