package org.example.moneymachine.repository;

import org.example.moneymachine.model.User;

import java.util.HashMap;
import java.util.Map;

public class Bank implements BankInterface {
    private static final String BANK_NAME = "Fulkoping MoneyMachine";
    private Map<String, User> users = new HashMap<>();

    public Bank(Map<String, User> users) {
        this.users = users;
    }

    /**
     * Returns the User object for the given userId.
     * Returns null if the user does not exist.
     */
    @Override
    public User getUserById(String id) {
        return users.get(id);
    }

    /**
     * Validates the PIN for the given userId.
     * Returns true if correct, false if incorrect or card is locked.
     * Handles incrementing failed attempts and locking the card after 3 failed tries.
     */
    @Override
    public boolean validatePin(String userId, String pin) {
        User user = users.get(userId);
        if (user == null || user.isLocked()) return false;

        if (isCorrectPin(user, pin)) {
            resetFailedAttempts(user);
            return true;
        }

        handleFailedPin(user);
        return false;
    }

    /**
     * Returns true if the users card is locked, false otherwise.
     * Returns false if the user does not exist.
     */
    @Override
    public boolean isCardLocked(String userId) {
        User user = users.get(userId);
        return user != null && user.isLocked();
    }

    /**
     * Locks the users card for the given userId.
     * Does nothing if the user does not exist.
     */
    @Override
    public void lockCard(String userId) {
        User user = users.get(userId);
        if (user != null) {
            user.lockCard();
        }
    }

    /**
     * Returns the banks name.
     */
    public static String getBankName() {
        return BANK_NAME;
    }

    /**
     * Helper: Checks if the given PIN matches the users PIN.
     */
    private boolean isCorrectPin(User user, String pin) {
        return user.getPin().equals(pin);
    }

    /**
     * Helper: Resets the users failed PIN attempts counter.
     */
    private void resetFailedAttempts(User user) {
        user.resetFailedAttempts();
    }

    /**
     * Helper: Handles a failed PIN attempt by incrementing the counter.
     * Locks the card if failed attempts reach 3.
     */
    private void handleFailedPin(User user) {
        user.incrementFailedAttempts();
        if (user.getFailedAttempts() >= 3) {
            user.lockCard();
        }
    }
}
