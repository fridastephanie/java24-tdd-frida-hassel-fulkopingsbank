package org.example.moneymachine.repository;

import org.example.moneymachine.model.User;

public interface BankInterface {
    public User getUserById(String id);
    public boolean isCardLocked(String userId);
    void lockCard(String userId);
    boolean validatePin(String userId, String pin);
}
