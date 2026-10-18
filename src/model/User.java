package model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private Account account; 
    private List<Transaction> transactionHistory; // NEW: The log list

    public User(String username, Account account) {
        this.username = username;
        this.account = account;
        this.transactionHistory = new ArrayList<>(); // Initialize the list
    }

    public String getUsername() { return username; }
    public Account getAccount() { return account; }
    
    // NEW: Methods to handle logs
    public void addTransaction(Transaction t) {
        transactionHistory.add(t);
    }

    public List<Transaction> getTransactionHistory() {
        return transactionHistory;
    }
}