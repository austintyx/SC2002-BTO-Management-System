package main.utils;

import java.util.Date;
import java.util.regex.Pattern;

/**
 * Utility class for validation operations
 * @author Your Team
 * @version 1.0
 */
public class ValidationUtils {

    /**
     * Validates project dates (opening and closing)
     * @param openingDate The opening date
     * @param closingDate The closing date
     * @return true if the dates are valid, false otherwise
     */
    public static boolean isValidProjectDates(Date openingDate, Date closingDate) {
        if (openingDate == null || closingDate == null) {
            return false;
        }
        
        // Opening date should be in the future
        Date today = DateUtils.getCurrentDate();
        if (openingDate.before(today)) {
            return false;
        }
        
        // Closing date should be after opening date
        return closingDate.after(openingDate);
    }
    
    /**
     * Validates flat types and counts
     * @param flatTypes The flat types and counts
     * @return true if the flat types and counts are valid, false otherwise
     */
    public static boolean isValidFlatTypes(java.util.Map<String, Integer> flatTypes) {
        if (flatTypes == null || flatTypes.isEmpty()) {
            return false;
        }
        
        // Check that only 2-Room and 3-Room are included
        for (String flatType : flatTypes.keySet()) {
            if (!flatType.equals("2-Room") && !flatType.equals("3-Room")) {
                return false;
            }
        }
        
        // Check that counts are positive
        for (Integer count : flatTypes.values()) {
            if (count == null || count <= 0) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Validates officer slots
     * @param officerSlots The number of officer slots
     * @return true if the officer slots are valid, false otherwise
     */
    public static boolean isValidOfficerSlots(int officerSlots) {
        return officerSlots > 0 && officerSlots <= 10;
    }
    
    /**
     * Validates applicant eligibility for a flat type
     * @param age The applicant's age
     * @param maritalStatus The applicant's marital status
     * @param flatType The flat type
     * @return true if the applicant is eligible, false otherwise
     */
    public static boolean isEligibleForFlatType(int age, String maritalStatus, String flatType) {
        if (flatType.equals("2-Room")) {
            // Singles 35 years and above or Married 21 years and above
            return (maritalStatus.equalsIgnoreCase("Single") && age >= 35) || 
                   (maritalStatus.equalsIgnoreCase("Married") && age >= 21);
        } else if (flatType.equals("3-Room")) {
            // Only Married 21 years and above
            return maritalStatus.equalsIgnoreCase("Married") && age >= 21;
        }
        return false;
    }
}
