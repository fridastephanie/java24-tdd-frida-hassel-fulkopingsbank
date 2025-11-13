package org.example.moneymachine.service;

import org.example.moneymachine.model.Transaction;
import org.example.moneymachine.model.User;
import org.example.moneymachine.repository.Bank;
import org.example.moneymachine.repository.BankInterface;

import java.util.Collections;
import java.util.List;

public class ATM {
    private final BankInterface bank;
    private final TransactionLogger transactionLogger;
    private User currentUser;
    private String userMessage;

    public ATM(BankInterface bank, TransactionLogger transactionLogger) {
        this.bank = bank;
        this.transactionLogger = transactionLogger;
    }

    /**
     * Attempts to insert a card for the given userId.
     * Returns false if the card is locked, otherwise true if the user exists.
     */
    public boolean insertCard(String userId) {
        if (bank.isCardLocked(userId)) {
            userMessage = "Card is locked";
            return false;
        }
        currentUser = bank.getUserById(userId);
        if (currentUser != null) {
            userMessage = "Card accepted";
            return true;
        } else {
            userMessage = "User not found";
            return false;
        }
    }

    /**
     * Verifies the PIN via Bank.
     * Returns true if correct, Bank handles failed attempts and locking.
     */
    public boolean enterPin(String pin) {
        if (currentUser == null) {
            userMessage = "No card inserted";
            return false;
        }

        boolean valid = bank.validatePin(currentUser.getId(), pin);
        if (valid) {
            userMessage = "PIN correct, you are logged in";
        } else {
            if (bank.isCardLocked(currentUser.getId())) {
                userMessage = "Card is locked";
            } else {
                userMessage = "PIN incorrect";
            }
        }
        return valid;
    }

    /**
     * Returns the balance of the current user.
     * Returns 0 if no user is inserted.
     */
    public double checkBalance() {
        if (currentUser == null) {
            userMessage = "No user inserted";
            return 0;
        }
        double balance = currentUser.getBalance();
        userMessage = "Your balance is: " + balance;
        return balance;
    }

    /**
     * Deposits an amount to the current user's account.
     * Does nothing if no user is inserted.
     */
    public void deposit(double amount) {
        if (currentUser != null) {
            currentUser.deposit(amount);
            transactionLogger.logDeposit(currentUser, amount);
            userMessage = "Deposit successful: " + amount;
        } else {
            userMessage = "No user inserted, deposit failed";
        }
    }

    /**
     * Attempts to withdraw an amount from the current user's account.
     * Returns true if successful, otherwise false.
     */
    public boolean withdraw(double amount) {
        if (currentUser == null) {
            userMessage = "No card inserted";
            return false;
        }
        if (currentUser.getBalance() >= amount) {
            currentUser.withdraw(amount);
            transactionLogger.logWithdraw(currentUser, amount);
            userMessage = "Withdrawal successful: " + amount;
            return true;
        } else {
            userMessage = "Insufficient funds";
            return false;
        }
    }

    /**
     * Returns the full transaction history (deposits and withdrawals)
     * for the current user.
     * Returns an empty list if no user is inserted.
     */
    public List<Transaction> getTransactionHistory() {
        if (currentUser == null) return Collections.emptyList();
        return transactionLogger.getTransactionsForUser(currentUser);
    }

    /**
     * Ejects the card and clears the current user.
     * Returns true if there was a user to eject, false otherwise.
     */
    public boolean ejectCard() {
        if (currentUser == null) {
            userMessage = "No card inserted";
            return false;
        }
        currentUser = null;
        userMessage = "Card ejected";
        return true;
    }

    /**
     * Returns the name of the connected bank.
     */
    public String getConnectedBankName() {
        return Bank.getBankName();
    }

    /**
     * Returns the last message for the user (status of last action)
     */
    public String getUserMessage() {
        return userMessage;
    }

    /**
     * Package-private getter for testing
     */
    User getCurrentUser() {
        return currentUser;
    }
}