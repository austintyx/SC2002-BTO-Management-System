package main.boundary;

import main.model.application.Application;
import main.model.application.ApplicationStatus;
import main.model.project.Project;
import main.model.user.Applicant;
import main.utils.ConsoleUtils;
import main.utils.DateUtils;

import java.util.List;

/**
 * Boundary class for displaying application information.
 * Centralizes application viewing functionality across user interfaces.
 */
public class ApplicationViewer {
    
    /**
     * Displays a list of applications in a formatted table.
     * @param applications List of Application objects to display
     */
    public static void displayApplications(List<Application> applications) {
    
        // Corrected header widths to match 4 columns
        System.out.println("\n");
        String[] headers = {"ID", "Applicant", "Project", "Status"};
        int[] widths = {10, 25, 25, 20};
        
        ConsoleUtils.displayTableHeader(headers, widths);
        
        for (Application app : applications) {
            // Ensure exactly 4 values per row
            String[] row = {
                app.getApplicationId(),
                app.getApplicantId(),
                app.getProjectName(),
                app.getStatus().toString()
            };
            ConsoleUtils.displayTableRow(row, widths);
        }
    }
    

    /**
     * Displays detailed information about a specific application.
     * @param application The Application object to display
     * @param project The associated Project object
     * @param applicant The associated Applicant object
     */
    public static void displayApplicationDetails(Application application, 
                                                Project project, 
                                                Applicant applicant) {
        System.out.println("\n=== Application Details ===");
        System.out.println("ID: " + application.getApplicationId());
        System.out.println("Status: " + application.getStatus());
        if (application.getRemarks() != null) {
            System.out.println("\nRemarks: " + application.getRemarks());
        }

        System.out.println("\nApplicant Details:");
        System.out.println("Name: " + applicant.getName());
        System.out.println("Age: " + applicant.getAge());
        System.out.println("Marital Status: " + applicant.getMaritalStatus());
        
        System.out.println("\nProject Details:");
        System.out.println("Name: " + project.getProjectName());
        System.out.println("Neighborhood: " + project.getNeighborhood());
        System.out.println("Flat Type: " + 
            (application.getFlatType() != null ? application.getFlatType() : "Not selected"));
        
        System.out.println("\nDates:");
        System.out.println("Applied: " + DateUtils.formatDate(application.getApplicationDate()));
        System.out.println("Last Updated: " + 
            DateUtils.formatDate(application.getStatusUpdateDate()));
        
    }

    /**
     * Displays applications with withdrawal status information.
     * @param applications List of applications with withdrawal requests
     */
    public static void displayWithdrawalRequests(List<Application> applications) {
        if (applications.isEmpty()) {
            System.out.println("No withdrawal requests.");
            return;
        }
        
        String[] headers = {"Application ID", "Applicant", "Project", "Withdrawal Status"};
        int[] widths = {15, 20, 20, 20};
        
        ConsoleUtils.displayTableHeader(headers, widths);
        
        for (Application app : applications) {
            String[] values = {
                app.getApplicationId(),
                app.getApplicantId(),
                app.getStatus().toString()
            };
            ConsoleUtils.displayTableRow(values, widths);
        }
    }

    /**
     * Displays applications ready for flat booking.
     * @param applications List of successful applications
     */
    public static void displayBookingEligible(List<Application> applications) {
        if (applications.isEmpty()) {
            System.out.println("No applications ready for booking.");
            return;
        }
        
        String[] headers = {"Application ID", "Applicant", "Preferred Flat Type"};
        int[] widths = {20, 25, 20};
        
        ConsoleUtils.displayTableHeader(headers, widths);
        
        for (Application app : applications) {
            String[] values = {
                app.getApplicationId(),
                app.getApplicantId(),
                app.getFlatType() != null ? app.getFlatType() : "Not selected"
            };
            ConsoleUtils.displayTableRow(values, widths);
        }
    }

    /**
     * Displays application status transitions.
     * @param oldStatus Previous application status
     * @param newStatus Updated application status
     */
    public static void displayStatusChange(ApplicationStatus oldStatus, 
                                          ApplicationStatus newStatus) {
        System.out.println("\nStatus updated: " 
            + oldStatus + " → " + newStatus);
    }

    /**
    * Displays a list of applications with withdrawal status information in a formatted table.
    * @param applications List of Application objects to display
    */
    public static void displayApplicationsWithWithdrawal(List<Application> applications) {
    
        // Define table structure for withdrawal-specific view
        String[] headers = {"App ID", "Project", "Applicant", "Status", "Withdrawal Reason"};
        int[] widths = {12, 25, 25, 20, 40};  // Must match headers length
        
        ConsoleUtils.displayTableHeader(headers, widths);
        
        for (Application app : applications) {
            String[] row = {
                app.getApplicationId(),
                app.getProjectName(),
                app.getApplicantId(),
                app.getStatus().toString(),
                app.getWithdrawalReason() != null ? app.getWithdrawalReason() : "N/A"
            };
            ConsoleUtils.displayTableRow(row, widths);
        }
    }
    

}
