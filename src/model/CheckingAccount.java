package model;

public class CheckingAccount extends Account {
    public CheckingAccount(double initialBalance) {
        super(initialBalance);
    }

    @Override
    public String toString() {
        return "Checking Account";
    }
}