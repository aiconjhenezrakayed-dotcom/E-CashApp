import java.util.Scanner;
import java.util.List;
import service.AuthService;
import model.User;
import model.Transaction;
import util.DatabaseHelper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        // Initialize the database tables on startup
        DatabaseHelper.initializeDatabase();

        Scanner scanner = new Scanner(System.in);
        AuthService authService = new AuthService();
        boolean isRunning = true;

        System.out.println("Hello E-CashApp");

        while (isRunning) {
            System.out.println("\n================================");
            System.out.println("Welcome to the E-CashApp Console!");
            System.out.println("================================");
            System.out.println("1. Login");
            System.out.println("2. Exit");
            System.out.print("Please enter your choice (1 or 2): ");

            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                System.out.print("Enter username: ");
                String username = scanner.nextLine();
                System.out.print("Enter password (use 1234): ");
                String password = scanner.nextLine();

                if (authService.login(username, password)) {
                    System.out.println("Login successful!");
                    showDashboard(scanner, authService); 
                } else {
                    System.out.println("Invalid credentials.");
                }

            } else if (choice.equals("2")) {
                System.out.println("Exiting E-CashApp. Goodbye!");
                isRunning = false;
            } else {
                System.out.println("Invalid choice.");
            }
        }
        scanner.close(); 
    }

    private static void showDashboard(Scanner scanner, AuthService authService) {
        User user = authService.getCurrentUser();
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n--- BANKING DASHBOARD ---");
            System.out.println("Account Type: " + user.getAccount().toString()); 
            System.out.println("1. Check Balance");
            System.out.println("2. Cash-In (Deposit)");
            System.out.println("3. Transfer");
            System.out.println("4. Logout");
            System.out.println("5. View Transaction History");
            System.out.print("Choose an option: ");
            
            String dashChoice = scanner.nextLine();

            switch (dashChoice) {
                case "1":
                    System.out.println("Current balance: PHP " + user.getAccount().getBalance());
                    break;
                case "2":
                    System.out.print("Enter amount to cash-in: PHP ");
                    double depositAmount = Double.parseDouble(scanner.nextLine());
                    if (depositAmount > 0) {
                        user.getAccount().deposit(depositAmount);
                        authService.updateBalanceInDB(user); // SAVE TO DB
                        
                        Transaction t = new Transaction("Cash-In", depositAmount);
                        user.addTransaction(t);
                        saveTransactionToDB(user.getUsername(), t);
                        
                        System.out.println("Deposited PHP " + depositAmount);
                        System.out.println("New Balance: PHP " + user.getAccount().getBalance());
                    } else {
                        System.out.println("Invalid amount.");
                    }
                    break;
                case "3":
                    System.out.print("Enter recipient username: ");
                    String recipientName = scanner.nextLine();
                    User recipient = authService.findUser(recipientName);

                    if (recipient == null || recipient.getUsername().equals(user.getUsername())) {
                        System.out.println("Invalid recipient.");
                        break;
                    }

                    System.out.print("Enter amount to transfer: PHP ");
                    double transferAmount = Double.parseDouble(scanner.nextLine());
                    
                    if (user.getAccount().withdraw(transferAmount)) {
                        recipient.getAccount().deposit(transferAmount);
                        
                        // SAVE BOTH USERS TO DB
                        authService.updateBalanceInDB(user);
                        authService.updateBalanceInDB(recipient);
                        
                        // Save logs to DB
                        Transaction sent = new Transaction("Sent to " + recipientName, -transferAmount);
                        Transaction received = new Transaction("Received from " + user.getUsername(), transferAmount);
                        
                        user.addTransaction(sent);
                        recipient.addTransaction(received);
                        
                        saveTransactionToDB(user.getUsername(), sent);
                        saveTransactionToDB(recipient.getUsername(), received);
                        
                        System.out.println("Successfully transferred PHP " + transferAmount + " to " + recipientName);
                        System.out.println("Your new balance: PHP " + user.getAccount().getBalance());
                    } else {
                        System.out.println("Invalid amount or insufficient funds.");
                    }
                    break;
                case "4":
                    System.out.println("Logging out...");
                    authService.logout();
                    loggedIn = false;
                    break;
                case "5":
                    viewHistory(user);
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void viewHistory(User user) {
        List<Transaction> history = user.getTransactionHistory();
        System.out.println("\n--- TRANSACTION HISTORY ---");
        
        if (history.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        System.out.printf("%-30s | %-12s | %-20s%n", "Type/Description", "Amount (PHP)", "Date & Time");
        System.out.println("-----------------------------------------------------------------------");
        
        for (Transaction t : history) {
            String timeStr = t.getTimestamp().toString().replace("T", " ").substring(0, 19);
            System.out.printf("%-30s | %-12.2f | %-20s%n", t.getType(), t.getAmount(), timeStr);
        }
        System.out.println("-----------------------------------------------------------------------");
    }

    private static void saveTransactionToDB(String username, Transaction t) {
        String sql = "INSERT INTO transactions(username, description, amount, timestamp) VALUES(?,?,?,?)";
        try (Connection conn = DatabaseHelper.connect(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, t.getType());
            pstmt.setDouble(3, t.getAmount());
            pstmt.setString(4, t.getTimestamp().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error saving transaction: " + e.getMessage());
        }
    }
}