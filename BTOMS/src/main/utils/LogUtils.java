package main.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for logging operations
 * @author Your Team
 * @version 1.0
 */
public class LogUtils {
    private static final String LOG_DIRECTORY = "logs";
    private static final String LOG_FILE = "bto_system.log";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Logs an informational message
     * @param message The message to log
     */
    public static void info(String message) {
        log("INFO", message);
    }
    
    /**
     * Logs a warning message
     * @param message The message to log
     */
    public static void warning(String message) {
        log("WARNING", message);
    }
    
    /**
     * Logs an error message
     * @param message The message to log
     */
    public static void error(String message) {
        log("ERROR", message);
    }
    
    /**
     * Logs a message with a specified level
     * @param level The log level
     * @param message The message to log
     */
    private static void log(String level, String message) {
        ensureLogDirectoryExists();
        
        String timestamp = DATE_FORMAT.format(new Date());
        String logEntry = String.format("[%s] [%s] %s", timestamp, level, message);
        
        System.out.println(logEntry);
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_DIRECTORY + File.separator + LOG_FILE, true))) {
            writer.write(logEntry);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }
    
    /**
     * Logs a user action for auditing
     * @param userNRIC The NRIC of the user
     * @param action The action performed
     * @param details Additional details about the action
     */
    public static void auditLog(String userNRIC, String action, String details) {
        String message = String.format("User %s performed action '%s': %s", userNRIC, action, details);
        info(message);
    }
    
    /**
     * Ensures that the log directory exists
     */
    private static void ensureLogDirectoryExists() {
        File logDir = new File(LOG_DIRECTORY);
        if (!logDir.exists()) {
            logDir.mkdir();
        }
    }
    
    /**
     * Clears the log file
     * @return true if the log file was cleared, false otherwise
     */
    public static boolean clearLog() {
        ensureLogDirectoryExists();
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_DIRECTORY + File.separator + LOG_FILE))) {
            writer.write("");
            return true;
        } catch (IOException e) {
            System.err.println("Error clearing log file: " + e.getMessage());
            return false;
        }
    }
}
