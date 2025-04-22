package main.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for password operations
 * @author Your Team
 * @version 1.0
 */
public class PasswordUtils {
    private static final int SALT_LENGTH = 16;
    
    /**
     * Hashes a password using SHA-256 with a random salt
     * @param password The password to hash
     * @return The hashed password with salt, or null if hashing fails
     */
    public static String hashPassword(String password) {
        try {
            // Generate a random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            
            // Hash the password with the salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            
            // Combine salt and hashed password
            byte[] combined = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hashedPassword, 0, combined, salt.length, hashedPassword.length);
            
            // Encode as Base64
            return Base64.getEncoder().encodeToString(combined);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error hashing password: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Verifies a password against a hashed password
     * @param password The password to verify
     * @param hashedPassword The hashed password to verify against
     * @return true if the password matches, false otherwise
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        try {
            // Decode from Base64
            byte[] combined = Base64.getDecoder().decode(hashedPassword);
            
            // Extract salt
            byte[] salt = new byte[SALT_LENGTH];
            System.arraycopy(combined, 0, salt, 0, SALT_LENGTH);
            
            // Hash the password with the extracted salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedInput = md.digest(password.getBytes());
            
            // Compare the hashed input with the stored hash
            for (int i = 0; i < hashedInput.length; i++) {
                if (hashedInput[i] != combined[SALT_LENGTH + i]) {
                    return false;
                }
            }
            
            return true;
        } catch (Exception e) {
            System.out.println("Error verifying password: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Validates password strength
     * @param password The password to validate
     * @return true if the password is strong enough, false otherwise
     */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasLetter = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else {
                hasSpecial = true;
            }
        }
        
        return hasLetter && hasDigit && hasSpecial;
    }
    
    /**
     * Generates a random password
     * @param length The length of the password
     * @return A random password
     */
    public static String generateRandomPassword(int length) {
        if (length < 8) {
            length = 8;
        }
        
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        
        return sb.toString();
    }
}
