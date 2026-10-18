# E-CashApp 💳

A robust, console-based banking simulation application built in Java. This project was developed as a portfolio piece to demonstrate core Java concepts, Object-Oriented Programming (OOP), and database integration.

## 🚀 Features
* **User Authentication:** Secure login system for multiple users.
* **Account Types (OOP):** Demonstrates Inheritance and Polymorphism with `SavingsAccount` and `CheckingAccount`.
* **Banking Operations:** 
  * Check Balance
  * Cash-In (Deposit)
  * Fund Transfer (between users)
* **Transaction History:** Logs all deposits and transfers with timestamps.
* **Data Persistence (JDBC):** Uses SQLite to save user balances and transaction logs, ensuring data survives application restarts.

## 🛠️ Technologies Used
* **Java:** Core language (OOP, Collections, Exception Handling).
* **JDBC (SQLite):** Database connection and SQL queries.
* **Git & GitHub:** Version control and Agile project management (Issues & Kanban).

## 💻 How to Run

Since this project uses an external JDBC driver, you must include the `.jar` file in the classpath when compiling and running.

1. Clone the repository:
   ```bash
   git clone https://github.com/aiconjhenezrakayed-dotcom/E-CashApp.git
   cd E-CashApp/src
