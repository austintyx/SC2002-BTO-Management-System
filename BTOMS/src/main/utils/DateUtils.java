package main.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Utility class for date operations
 * @author Your Team
 * @version 1.0
 */
public class DateUtils {
    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
    
    /**
     * Converts a string to a Date object
     * @param dateStr The date string in dd/MM/yyyy format
     * @return The Date object, or null if parsing fails
     */
    public static Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        sdf.setLenient(false);
        
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }
    
    /**
     * Formats a Date object to a string in dd/MM/yyyy format
     * @param date The Date object to format
     * @return The formatted date string, or empty string if date is null
     */
    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }
    
    /**
     * Formats a Date object to a string in dd/MM/yyyy HH:mm:ss format
     * @param date The Date object to format
     * @return The formatted date-time string, or empty string if date is null
     */
    public static String formatDateTime(Date date) {
        if (date == null) {
            return "";
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
        return sdf.format(date);
    }
    
    /**
     * Checks if a date is between two other dates (inclusive)
     * @param date The date to check
     * @param startDate The start date
     * @param endDate The end date
     * @return true if the date is between start and end dates, false otherwise
     */
    public static boolean isDateBetween(Date date, Date startDate, Date endDate) {
        if (date == null || startDate == null || endDate == null) {
            return false;
        }
        
        return (date.equals(startDate) || date.after(startDate)) && 
               (date.equals(endDate) || date.before(endDate));
    }
    
    /**
     * Gets the current date with time set to 00:00:00
     * @return The current date
     */
    public static Date getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    /**
     * Checks if a project is currently open for application
     * @param openingDate The project opening date
     * @param closingDate The project closing date
     * @return true if the current date is between opening and closing dates, false otherwise
     */
    public static boolean isProjectOpen(Date openingDate, Date closingDate) {
        Date currentDate = new Date();
        return isDateBetween(currentDate, openingDate, closingDate);
    }

    public static boolean isDateRangeOverlapping(Date start1, Date end1, 
                                                 Date start2, Date end2) {
        if (start1 == null || end1 == null || start2 == null || end2 == null) {
            throw new IllegalArgumentException("Date parameters cannot be null");
        }
        return !end1.before(start2) && !start1.after(end2);
    }

    
}
