package main.boundary;

import main.model.project.Project;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class for displaying project information in the CLI.
 * Provides methods to display project lists, project details, and projects with visibility status.
 * 
 * @author Your Name
 * @version 1.0
 * @since 2025-04-17
 */
public class ProjectViewer {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    /**
     * Displays a list of projects in a tabular format.
     * @param projects List of Project objects to display
     */
    public static void displayProjects(List<Project> projects) {
        displayProjects(projects, false);
    }

    /**
     * Displays a list of projects, optionally showing visibility status.
     * @param projects List of Project objects to display
     * @param showVisibility Whether to show the visibility column
     */
    public static void displayProjects(List<Project> projects, boolean showVisibility) {
        if (projects == null || projects.isEmpty()) {
            System.out.println("No projects to display.");
            return;
        }
        String header = String.format("%-20s %-15s %-20s %-12s %-12s %-10s",
                "Project Name", "Neighborhood", "Manager", "Open Date", "Close Date", "Officers");
        if (showVisibility) {
            header += "  Visibility";
        }
        System.out.println(header);
        System.out.println("-".repeat(header.length()));

        for (Project project : projects) {
            String managerName = project.getManagerDisplayName();
            if (managerName == null || managerName.isEmpty()) {
                managerName = project.getManagerInCharge(); // Fallback to whatever is stored
            }
            String row = String.format(" %-20s %-15s %-20s %-12s %-12s %-10d",
                    project.getProjectName(),
                    project.getNeighborhood().toString(),
                    managerName,
                    dateFormat.format(project.getOpeningDate()),
                    dateFormat.format(project.getClosingDate()),
                    project.getOfficerSlots());
            if (showVisibility) {
                row += String.format("  %-10s", project.isVisible() ? "ON" : "OFF");
            }
            System.out.println(row);
        }
    }

    /**
     * Displays detailed information about a single project.
     * @param project The Project object to display
     * @param showOfficerList Whether to display the list of officers for the project
     */
    public static void displayProjectDetails(Project project, boolean showOfficerList) {
        if (project == null) {
            System.out.println("Project not found.");
            return;
        }
        System.out.println("\n===== Project Details =====");
        System.out.println("Project Name     : " + project.getProjectName());
        System.out.println("Neighborhood     : " + project.getNeighborhood());
        System.out.println("Manager In-Charge: " + project.getManagerDisplayName());
        System.out.println("Open Date        : " + dateFormat.format(project.getOpeningDate()));
        System.out.println("Close Date       : " + dateFormat.format(project.getClosingDate()));
        System.out.println("Visibility       : " + (project.isVisible() ? "ON" : "OFF"));
        System.out.println("Officer Slots    : " + project.getOfficerSlots());
        System.out.println("Flat Types       :");
        project.getFlatTypes().forEach((type, count) ->
                System.out.println("  - " + type + ": " + count + " units"));
        System.out.println("Remaining Flats  :");
        project.getRemainingFlats().forEach((type, count) ->
                System.out.println("  - " + type + ": " + count + " units"));
        if (showOfficerList) {
            List<String> officerNames = new ArrayList<>(project.getOfficerDetails().values());
            if (officerNames.isEmpty()) {
                System.out.println("Officers         : None assigned");
            } else {
                System.out.println("Officers         : " + String.join(", ", officerNames));
            }
        }
    }



    /**
     * Displays a list of projects with their visibility status.
     * @param projects List of Project objects
     */
    public static void displayProjectsWithVisibility(List<Project> projects) {
        displayProjects(projects, true);
    }

    /**
     * Displays only projects the user is eligible to apply for, based on age and marital status.
     * @param projects List of Project objects
     * @param age User's age
     * @param maritalStatus User's marital status (as enum)
     */
    public static void displayEligibleProjects(List<Project> projects, int age, Enum<?> maritalStatus) {
        System.out.println("\n===== Eligible BTO Projects =====");
        System.out.printf("%-10s %-20s %-15s %-12s %-12s %-15s\n",
                "Proj ID", "Project Name", "Neighborhood", "Open Date", "Close Date", "Flat Types");
        System.out.println("----------------------------------------------------------------------------------");
        for (Project project : projects) {
            StringBuilder eligibleFlatTypes = new StringBuilder();
            for (String flatType : project.getFlatTypes().keySet()) {
                if (isEligibleForFlatType(age, maritalStatus, flatType)) {
                    eligibleFlatTypes.append(flatType).append(" ");
                }
            }
            if (eligibleFlatTypes.length() > 0) {
                System.out.printf(" %-20s %-15s %-12s %-12s %-15s\n",
                        project.getProjectName(),
                        project.getNeighborhood().toString(),
                        dateFormat.format(project.getOpeningDate()),
                        dateFormat.format(project.getClosingDate()),
                        eligibleFlatTypes.toString().trim());
            }
        }
    }

    /**
     * Helper method to check if a user is eligible for a flat type.
     * @param age User's age
     * @param maritalStatus User's marital status (as enum)
     * @param flatType The flat type to check eligibility for
     * @return true if eligible, false otherwise
     */
    private static boolean isEligibleForFlatType(int age, Enum<?> maritalStatus, String flatType) {
        String status = maritalStatus.toString();
        if (flatType.equals("2-Room")) {
            return (status.equalsIgnoreCase("Single") && age >= 35)
                    || (status.equalsIgnoreCase("Married") && age >= 21);
        } else if (flatType.equals("3-Room")) {
            return status.equalsIgnoreCase("Married") && age >= 21;
        }
        return false;
    }
}
