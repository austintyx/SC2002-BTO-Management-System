package main.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Utility class for validating user inputs
 * @author Your Team
 * @version 1.0
 */
public class InputValidator {
    private static final String NRIC_PATTERN = "^[ST]\\d{7}[A-Z]$";
    private static final String DATE_FORMAT = "dd/MM/yyyy";
    
    /**
     * Validates if the input string is a valid NRIC
     * @param nric The NRIC to validate
     * @return true if the NRIC is valid, false otherwise
     */
    public static boolean isValidNRIC(String nric) {
        if (nric == null || nric.isEmpty()) {
            return false;
        }
        return Pattern.matches(NRIC_PATTERN, nric);
    }
    
    /**
     * Validates if the input string is a valid date in dd/MM/yyyy format
     * @param dateStr The date string to validate
     * @return true if the date is valid, false otherwise
     */
    public static boolean isValidDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return false;
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        sdf.setLenient(false);
        
        try {
            sdf.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
    
    /**
     * Validates if the end date is after the start date
     * @param startDate The start date
     * @param endDate The end date
     * @return true if end date is after start date, false otherwise
     */
    public static boolean isValidDateRange(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return false;
        }
        return endDate.after(startDate);
    }
    
    /**
     * Validates if the input string is a valid integer
     * @param intStr The integer string to validate
     * @return true if the string is a valid integer, false otherwise
     */
    public static boolean isValidInteger(String intStr) {
        if (intStr == null || intStr.isEmpty()) {
            return false;
        }
        
        try {
            Integer.parseInt(intStr);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validates if the input string is a valid flat type
     * @param flatType The flat type to validate
     * @return true if the flat type is valid, false otherwise
     */
    public static boolean isValidFlatType(String flatType) {
        if (flatType == null || flatType.isEmpty()) {
            return false;
        }
        return flatType.equals("2-Room") || flatType.equals("3-Room");
    }
    
    /**
     * Validates if the input string is a valid marital status
     * @param status The marital status to validate
     * @return true if the status is valid, false otherwise
     */
    public static boolean isValidMaritalStatus(String status) {
        if (status == null || status.isEmpty()) {
            return false;
        }
        return status.equalsIgnoreCase("Single") || status.equalsIgnoreCase("Married");
    }
}
