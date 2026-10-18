package service;

import model.User;

public class AuthService {
    // Placeholder for login logic
    public boolean login(String username, String password) {
        return true; 
    }

    public User getUserProfile(String username) {
        return new User(username, 1000.00); 
    }
}
