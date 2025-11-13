package org.example.moneymachine;

import org.example.moneymachine.model.User;
import org.example.moneymachine.repository.Bank;
import org.example.moneymachine.repository.BankInterface;
import org.example.moneymachine.service.ATM;
import org.example.moneymachine.service.TransactionLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class App {

    private static final Scanner scanner = new Scanner(System.in);
    private static ATM atm;

    public static void main(String[] args) {
        initializeATM();
        showBankName();

        if (insertCard() && enterPin()) {
            showMenu();
        }

        exitATM();
    }

    /**
     * Initialize ATM with bank and user data
     */
    private static void initializeATM() {
        Map<String, User> users = new HashMap<>();
        users.put("user1", new User("user1", "1234", 1000.0));
        users.put("user2", new User("user2", "5678", 500.0));

        BankInterface bank = new Bank(users);
        TransactionLogger logger = new TransactionLogger();
        atm = new ATM(bank, logger);
    }

    /**
     * Print the bank's welcome message
     */
    private static void showBankName() {
        System.out.println("Welcome to " + atm.getConnectedBankName());
    }

    /**
     * Prompt user to insert card and verify existence and lock status
     */
    private static boolean insertCard() {
        System.out.print("Insert card (enter user ID): ");
        String cardNumber = scanner.nextLine();
        boolean success = atm.insertCard(cardNumber);
        System.out.println(atm.getUserMessage());
        return success;
    }

    /**
     * Prompt user to enter PIN until correct or card is locked
     */
    private static boolean enterPin() {
        boolean success = false;

        while (!success) {
            String pin = readPin();
            success = handlePinAttempt(pin);
            if (atm.getUserMessage().contains("locked")) break;
        }

        return success;
    }

    /**
     * Read PIN input from user
     */
    private static String readPin() {
        System.out.print("Enter PIN: ");
        return scanner.nextLine();
    }

    /**
     * Handle a single PIN attempt and display relevant message
     */
    private static boolean handlePinAttempt(String pin) {
        boolean success = atm.enterPin(pin);
        System.out.println(atm.getUserMessage());

        if (atm.getUserMessage().contains("locked")) {
            System.out.println("Your card has been locked due to too many failed attempts.");
        } else if (!success) {
            System.out.println("Try again.");
        }

        return success;
    }

    /**
     * Display the main ATM menu and handle user choices
     */
    private static void showMenu() {
        boolean running = true;

        while (running) {
            printMenuOptions();
            int choice = readMenuChoice();

            switch (choice) {
                case 1 -> checkBalance();
                case 2 -> withdraw();
                case 3 -> deposit();
                case 4 -> showTransactionHistory();
                case 5 -> {
                    ejectCard();
                    running = false;
                }
                default -> System.out.println("Invalid option");
            }
        }
    }

    /**
     * Print available menu options to the console
     */
    private static void printMenuOptions() {
        System.out.println("\n--- ATM Menu ---");
        System.out.println("1. Check balance");
        System.out.println("2. Withdraw");
        System.out.println("3. Deposit");
        System.out.println("4. View transaction history");
        System.out.println("5. Eject card / Exit");
        System.out.print("Choose option: ");
    }

    /**
     * Read user's menu selection as an integer
     */
    private static int readMenuChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input, please enter a number.");
            return -1;
        }
    }

    /**
     * Prompt user to enter an amount (double)
     */
    private static double readAmount(String prompt) {
        System.out.print(prompt);
        try {
            return Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number, using 0.");
            return 0;
        }
    }

    /**
     * Display current user's balance
     */
    private static void checkBalance() {
        atm.checkBalance();
        System.out.println(atm.getUserMessage());
    }

    /**
     * Withdraw a specified amount from user's account
     */
    private static void withdraw() {
        double amount = readAmount("Enter amount to withdraw: ");
        atm.withdraw(amount);
        System.out.println(atm.getUserMessage());
    }

    /**
     * Deposit a specified amount to user's account
     */
    private static void deposit() {
        double amount = readAmount("Enter amount to deposit: ");
        atm.deposit(amount);
        System.out.println(atm.getUserMessage());
    }

    /**
     * Show transaction history for current user
     */
    private static void showTransactionHistory() {
        var history = atm.getTransactionHistory();
        if (history.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            System.out.println("--- Transaction History ---");
            for (var t : history) {
                System.out.printf("%s | %s | %.2f%n",
                        t.getTimestamp(),
                        t.getType(),
                        t.getAmount());
            }
        }
    }

    /**
     * Eject the current card from ATM and clear session
     */
    private static void ejectCard() {
        atm.ejectCard();
        System.out.println(atm.getUserMessage());
    }

    /**
     * Exit the ATM application safely
     */
    private static void exitATM() {
        ejectCard();
        System.out.println("Thank you for using " + atm.getConnectedBankName());
        scanner.close();
    }
}