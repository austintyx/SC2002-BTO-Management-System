package main.controller;

import main.model.user.User;
import main.repository.UserRepository;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class PasswordController {
    private final UserRepository userRepository;
    
    // Regex: At least 8 chars, 1 uppercase, 1 lowercase, 1 digit, 1 special symbol
    private static final String PASSWORD_REGEX = 
        "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    public PasswordController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Validates password against security requirements.
     */
    public static boolean isPasswordValid(String password) {
        Matcher matcher = PASSWORD_PATTERN.matcher(password);
        return matcher.matches();
    }

    /**
     * Changes a user's password after validation.
     */
    public boolean changePassword(User user, String currentPassword, 
                                  String newPassword, String confirmPassword) {
        // Existing checks
        if (!user.getPassword().equals(currentPassword)) {
            return false;
        }
        if (!newPassword.equals(confirmPassword)) {
            return false;
        }
        
        // New: Validate password strength
        if (!isPasswordValid(newPassword)) {
            return false;
        }
        
        user.setPassword(newPassword);
        return userRepository.update(user);
    }
}
