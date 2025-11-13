# 🏦 The Fulköping Money Machine

This project is a **mocked ATM application** built in **Java** to practice **Test-Driven Development (TDD)**.  
The goal is to simulate an ATM where users can insert their card, verify their PIN, check their balance, deposit, and withdraw money.  
All communication with the bank is done via a mocked API, and all parts of the system are covered with **unit tests**.  
The project also includes a **simple terminal app** that allows you to interactively test the ATM functionality.

---

## ✨ Features

- 💳 Insert and eject bank cards  
- 🔐 PIN verification with lockout after 3 failed attempts  
- 💰 Check account balance  
- ➕ Deposit money  
- ➖ Withdraw money (with balance check)  
- 🧩 Mocked bank via `BankInterface` for testing  
- 🏷️ Static method to verify the bank's name  
- 🧾 Transaction logging for deposits and withdrawals  
- 🖥️ **Terminal app** to interactively test ATM functionality  

---

## 🧪 Testing & TDD

- Full **unit test coverage** (>80%) for all user flows  
- Tests written following the **TDD process** (Red → Green → Refactor)  
- **Mockito** is used to mock the bank API  
- Follows the **Arrange–Act–Assert (AAA)** pattern  
- **Nested test classes** for clear structure and grouping  
- Uses **@DisplayName**, **@BeforeEach**, and **@Tag("unit")**  

---

## 📁 Project Structure

- **model/** – Domain classes (`User`, `Transaction`)  
- **repository/** – Bank interface and mock implementation  
- **service/** – Core logic (`ATM`, `TransactionLogger`)  
- **test/** – Unit tests (`ATMTest`, `UserTest`, `TransactionTest`, `BankTest`, `TransactionLoggerTest`)  

---

## 🛠 Built With

- **Java 21**  
- **JUnit 5**  
- **Mockito**  
- **Maven**

---

## 🎯 Learning Goals

- Understand and apply **Test-Driven Development**  
- Mock external dependencies using Mockito  
- Write and structure unit tests with high readability  
- Refactor code based on tests  
- Build a simple **terminal app** for interactive testing  

---

## 🚀 Getting Started

### 1. Clone the project
```bash
git clone https://github.com/fridastephanie/java24-tdd-frida-hassel-fulkopingsbank.git
```
```bash
cd java24-tdd-frida-hassel-fulkopingsbank
```
### 2. Run the tests 
```bash
mvn clean test
```
### 3. Start the terminal app
```bash
mvn exec:java -Dexec.mainClass="org.example.moneymachine.App"
```
Follow the instructions in the terminal to interactively test the ATM functionality.
