package org.example.moneymachine.repository;

import org.example.moneymachine.model.User;

public interface BankInterface {
    User getUserById(String id);
    boolean isCardLocked(String userId);
    void lockCard(String userId);
    boolean validatePin(String userId, String pin);
}
