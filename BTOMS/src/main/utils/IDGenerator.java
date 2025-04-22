package main.utils;

import java.util.UUID;

/**
 * Utility class for generating unique IDs
 * @author Your Team
 * @version 1.0
 */
public class IDGenerator {
    /**
     * Generates a unique project ID
     * @return A unique project ID
     */
    public static String generateProjectId() {
        return "PROJ-" + System.currentTimeMillis() + "-" + getRandomSuffix();
    }
    
    /**
     * Generates a unique application ID
     * @return A unique application ID
     */
    public static String generateApplicationId() {
        return "APP-" + System.currentTimeMillis() + "-" + getRandomSuffix();
    }
    
    /**
     * Generates a unique enquiry ID
     * @return A unique enquiry ID
     */
    public static String generateEnquiryId() {
        return "ENQ-" + System.currentTimeMillis() + "-" + getRandomSuffix();
    }
    
    /**
     * Generates a unique registration ID
     * @return A unique registration ID
     */
    public static String generateRegistrationId() {
        return "REG-" + System.currentTimeMillis() + "-" + getRandomSuffix();
    }
    
    /**
     * Generates a unique receipt ID
     * @return A unique receipt ID
     */
    public static String generateReceiptId() {
        return "REC-" + System.currentTimeMillis() + "-" + getRandomSuffix();
    }
    
    /**
     * Generates a random suffix for IDs
     * @return A random 4-character suffix
     */
    private static String getRandomSuffix() {
        return UUID.randomUUID().toString().substring(0, 4);
    }
}
