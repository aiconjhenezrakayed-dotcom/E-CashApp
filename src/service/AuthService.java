package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import model.User;
import model.SavingsAccount;
import model.CheckingAccount;
import util.DatabaseHelper;

public class AuthService {
    private HashMap<String, User> userDatabase;
    private User currentUser;

    public AuthService() {
        userDatabase = new HashMap<>();
        loadUsersFromDatabase();
    }

    private void loadUsersFromDatabase() {
        String sql = "SELECT * FROM users";
        try (Connection conn = DatabaseHelper.connect(); 
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String username = rs.getString("username");
                double balance = rs.getDouble("balance");
                String type = rs.getString("account_type");

                if (type.equals("Savings")) {
                    userDatabase.put(username, new User(username, new SavingsAccount(balance)));
                } else {
                    userDatabase.put(username, new User(username, new CheckingAccount(balance)));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }

        // If database is empty (first run), create the default users
        if (userDatabase.isEmpty()) {
            createUser("admin", 1000.00, "Savings");
            createUser("john", 500.00, "Checking");
            createUser("mary", 2000.00, "Savings");
        }
    }

    private void createUser(String username, double balance, String type) {
        String sql = "INSERT INTO users(username, balance, account_type) VALUES(?,?,?)";
        try (Connection conn = DatabaseHelper.connect(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setDouble(2, balance);
            pstmt.setString(3, type);
            pstmt.executeUpdate();

            // Add to memory
            if (type.equals("Savings")) {
                userDatabase.put(username, new User(username, new SavingsAccount(balance)));
            } else {
                userDatabase.put(username, new User(username, new CheckingAccount(balance)));
            }
        } catch (SQLException e) {
            System.out.println("Error creating user: " + e.getMessage());
        }
    }

    public boolean login(String username, String password) {
        if (userDatabase.containsKey(username) && password.equals("1234")) {
            currentUser = userDatabase.get(username);
            return true; 
        }
        return false;
    }

    public User getCurrentUser() { return currentUser; }
    public User findUser(String username) { return userDatabase.get(username); }
    public void logout() { currentUser = null; }

    // Save the updated balance to the database
    public void updateBalanceInDB(User user) {
        String sql = "UPDATE users SET balance = ? WHERE username = ?";
        try (Connection conn = DatabaseHelper.connect(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, user.getAccount().getBalance());
            pstmt.setString(2, user.getUsername());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating balance: " + e.getMessage());
        }
    }
}
