package model;

public class SavingsAccount extends Account {
    public SavingsAccount(double initialBalance) {
        super(initialBalance); 
    }

    @Override
    public String toString() {
        return "Savings Account";
    }
}