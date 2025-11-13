package org.example.moneymachine.service;

import org.example.moneymachine.model.Transaction;
import org.example.moneymachine.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Comparator;

public class TransactionLogger {

    final Map<String, List<Transaction>> userTransactions = new HashMap<>();

    /**
     * Logs a deposit transaction for a given user with a timestamp.
     */
    public void logDeposit(User user, double amount) {
        Transaction transaction = new Transaction(user.getId(), Transaction.Type.DEPOSIT, amount, LocalDateTime.now());
        addTransaction(user.getId(), transaction);
    }

    /**
     * Logs a withdraw transaction for a given user with a timestamp.
     */
    public void logWithdraw(User user, double amount) {
        Transaction transaction = new Transaction(user.getId(), Transaction.Type.WITHDRAW, amount, LocalDateTime.now());
        addTransaction(user.getId(), transaction);
    }

    /**
     * Returns all transactions for all users.
     */
    public List<Transaction> getAllTransactions() {
        List<Transaction> all = new ArrayList<>();
        for (List<Transaction> txs : userTransactions.values()) {
            all.addAll(txs);
        }
        return all;
    }

    /**
     * Returns all transactions for a specific user, sorted by timestamp ascending.
     */
    public List<Transaction> getTransactionsForUser(User user) {
        List<Transaction> transactions = new ArrayList<>(userTransactions.getOrDefault(user.getId(), new ArrayList<>()));
        transactions.sort(Comparator.comparing(Transaction::getTimestamp));
        return transactions;
    }

    /**
     * Adds a transaction to the user's list in the map.
     */
    private void addTransaction(String userId, Transaction transaction) {
        userTransactions.computeIfAbsent(userId, k -> new ArrayList<>()).add(transaction);
    }
}
