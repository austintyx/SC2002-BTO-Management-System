package main.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import main.model.project.Project;
import main.model.user.Applicant;
import main.repository.UserRepository;
import main.model.application.Application;

/**
 * Utility class for generating reports
 * @author Your Team
 * @version 1.0
 */
public class ReportGenerator {
    /**
     * Generates a flat selection report for a project
     * @param projectId The project ID
     * @param projectName The project name
     * @param applicantData List of maps containing applicant data
     * @param filters Map of filters applied to the report
     * @return The path to the generated report file, or null if generation fails
     */
    public static String generateFlatSelectionReport(String projectId, String projectName, 
                                                   List<Map<String, Object>> applicantData,
                                                   Map<String, Object> filters) {
        ensureReportsDirectoryExists();
        
        String timestamp = DateUtils.formatDateTime(new Date()).replace("/", "-").replace(":", "-").replace(" ", "_");
        String filePath = "reports/flat_selection_" + projectId + "_" + timestamp + ".txt";
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Write report header
            writer.write("FLAT SELECTION REPORT");
            writer.newLine();
            writer.write("Project: " + projectName + " (" + projectId + ")");
            writer.newLine();
            writer.write("Generated on: " + DateUtils.formatDateTime(new Date()));
            writer.newLine();
            writer.newLine();
            
            // Write filters applied
            writer.write("Filters applied:");
            writer.newLine();
            if (filters != null && !filters.isEmpty()) {
                for (Map.Entry<String, Object> entry : filters.entrySet()) {
                    writer.write("  " + entry.getKey() + ": " + entry.getValue());
                    writer.newLine();
                }
            } else {
                writer.write("  None");
                writer.newLine();
            }
            writer.newLine();
            
            // Write column headers
            writer.write(String.format("%-15s %-30s %-5s %-15s %-10s", 
                                     "NRIC", "Name", "Age", "Marital Status", "Flat Type"));
            writer.newLine();
            writer.write(String.format("%-15s %-30s %-5s %-15s %-10s", 
                                     "---------------", "------------------------------", "-----", "---------------", "----------"));
            writer.newLine();
            
            // Write applicant data
            if (applicantData != null && !applicantData.isEmpty()) {
                for (Map<String, Object> data : applicantData) {
                    writer.write(String.format("%-15s %-30s %-5s %-15s %-10s", 
                                             data.get("nric"),
                                             data.get("name"),
                                             data.get("age"),
                                             data.get("maritalStatus"),
                                             data.get("flatType")));
                    writer.newLine();
                }
            } else {
                writer.write("No data available.");
                writer.newLine();
            }
            
            return filePath;
        } catch (IOException e) {
            System.out.println("Error generating report: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Generates a flat selection receipt for an applicant
     * @param applicantNRIC The applicant's NRIC
     * @param applicantName The applicant's name
     * @param applicantAge The applicant's age
     * @param maritalStatus The applicant's marital status
     * @param projectName The project name
     * @param neighborhood The project neighborhood
     * @param flatType The selected flat type
     * @return The path to the generated receipt file, or null if generation fails
     */
    public static String generateFlatSelectionReceipt(String applicantNRIC, String applicantName, 
                                                    int applicantAge, String maritalStatus,
                                                    String projectName, String neighborhood, 
                                                    String flatType) {
        ensureReportsDirectoryExists();
        
        String timestamp = DateUtils.formatDateTime(new Date()).replace("/", "-").replace(":", "-").replace(" ", "_");
        String filePath = "reports/receipt_" + applicantNRIC + "_" + timestamp + ".txt";
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Write receipt header
            writer.write("FLAT SELECTION RECEIPT");
            writer.newLine();
            writer.write("Generated on: " + DateUtils.formatDateTime(new Date()));
            writer.newLine();
            writer.write("Receipt ID: " + IDGenerator.generateReceiptId());
            writer.newLine();
            writer.newLine();
            
            // Write applicant details
            writer.write("APPLICANT DETAILS");
            writer.newLine();
            writer.write("Name: " + applicantName);
            writer.newLine();
            writer.write("NRIC: " + applicantNRIC);
            writer.newLine();
            writer.write("Age: " + applicantAge);
            writer.newLine();
            writer.write("Marital Status: " + maritalStatus);
            writer.newLine();
            writer.newLine();
            
            // Write project details
            writer.write("PROJECT DETAILS");
            writer.newLine();
            writer.write("Project Name: " + projectName);
            writer.newLine();
            writer.write("Neighborhood: " + neighborhood);
            writer.newLine();
            writer.newLine();
            
            // Write flat details
            writer.write("FLAT DETAILS");
            writer.newLine();
            writer.write("Flat Type: " + flatType);
            writer.newLine();
            writer.newLine();
            
            // Write footer
            writer.write("This receipt serves as proof of your flat selection.");
            writer.newLine();
            writer.write("Please keep this receipt for your records.");
            writer.newLine();
            
            return filePath;
        } catch (IOException e) {
            System.out.println("Error generating receipt: " + e.getMessage());
            return null;
        }
    }

    /**
     * Generates a formatted report of applicants with flat bookings.
     * 
     * @param project The project to generate the report for
     * @param applications List of applications to include
     * @param filters Map of filters to apply (e.g., "maritalStatus", "flatType")
     * @return Formatted report string
     */
    public static String generate(Project project, 
                                 List<Application> applications, 
                                 Map<String, Object> filters,
                                 UserRepository userRepository) {
        StringBuilder report = new StringBuilder();
        
        // Header
        report.append("Project Report: ").append(project.getProjectName()).append("\n");
        report.append("Neighborhood: ").append(project.getNeighborhood()).append("\n\n");
        report.append(String.format("%-20s %-15s %-5s %-10s %-15s\n", 
                                   "Applicant", "NRIC", "Age", "Status", "Flat Type"));
        report.append("------------------------------------------------------------\n");

        // Filtered applications
        applications.stream()
            .filter(app -> passesFilters(app, filters, userRepository))
            .forEach(app -> {
                Applicant applicant = (Applicant) userRepository.findById(app.getApplicantId());
                report.append(String.format("%-20s %-15s %-5d %-10s %-15s\n",
                    applicant.getName(),
                    applicant.getID(),
                    applicant.getAge(),
                    applicant.getMaritalStatus(),
                    app.getFlatType()));
            });

        return report.toString();
    }

    private static boolean passesFilters(Application app, 
                                    Map<String, Object> filters,
                                    UserRepository userRepository) {
    return filters.entrySet().stream().allMatch(entry -> {
        String key = entry.getKey();
        Object value = entry.getValue();
        
        // Fetch applicant from repository
        Applicant applicant = (Applicant)userRepository.findById(app.getApplicantId());
        if (applicant == null) return false; // Skip if applicant not found

        switch (key) {
            case "maritalStatus":
                return applicant.getMaritalStatus().toString().equalsIgnoreCase((String) value);
            case "flatType":
                return app.getFlatType().equalsIgnoreCase((String) value);
            case "ageMin":
                return applicant.getAge() >= (Integer) value;
            case "ageMax":
                return applicant.getAge() <= (Integer) value;
            default:
                return true; // Ignore unknown filters
        }
    });


    }
    
    /**
     * Ensures that the reports directory exists
     * @return true if the directory exists or was created, false otherwise
     */
    private static boolean ensureReportsDirectoryExists() {
        File reportsDir = new File("reports");
        if (!reportsDir.exists()) {
            return reportsDir.mkdir();
        }
        return true;
    }
}
