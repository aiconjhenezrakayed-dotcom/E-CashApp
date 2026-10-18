package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHelper {
    // This creates a file named "bank.db" in your main project folder
    private static final String URL = "jdbc:sqlite:bank.db";

    public static Connection connect() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
            return null;
        }
    }

    // This method creates the tables if they don't exist yet
    public static void initializeDatabase() {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "username TEXT PRIMARY KEY, " +
                "balance REAL, " +
                "account_type TEXT" +
                ");";

        String createTransactionsTable = "CREATE TABLE IF NOT EXISTS transactions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT, " +
                "description TEXT, " +
                "amount REAL, " +
                "timestamp TEXT" +
                ");";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createTransactionsTable);
        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }
}