package main.boundary;

import java.util.List;
import main.model.project.Project;
import main.model.registration.OfficerRegistration;
import main.model.registration.RegistrationStatus;
import main.model.user.HDBOfficer;
import main.utils.ConsoleUtils;
import main.utils.DateUtils;

/**
 * Boundary class for displaying officer registration information.
 * Centralizes all officer registration viewing functionality across different user interfaces.
 * @author Your Team
 * @version 1.2
 */
public class OfficerRegistrationViewer {

    /**
     * Displays a list of officer registrations in a formatted table.
     * @param registrations List of OfficerRegistration objects to display
     */
    public static void displayRegistrations(List<OfficerRegistration> registrations) {
        int[] widths = {20, 15, 30, 15, 20};
        System.out.println("\n");
        String[] headers = {"Reg. ID", "Officer NRIC", "Project Name", "Status", "Date"};
        ConsoleUtils.displayTableHeader(headers, widths);

        for (OfficerRegistration registration : registrations) {
            String[] values = {
                registration.getRegistrationId(),
                registration.getOfficerNRIC(),
                registration.getProjectName(),
                registration.getStatus() != null ? registration.getStatus().toString() : "N/A",
                DateUtils.formatDate(registration.getRegistrationDate())
            };
            ConsoleUtils.displayTableRow(values, widths);
        }
    }

    /**
     * Displays registrations with project name information.
     * @param registrations List of OfficerRegistration objects to display
     * @param projects List of Project objects for name lookup
     */
    public static void displayRegistrationsWithProjectNames(List<OfficerRegistration> registrations, List<Project> projects) {

        int[] widths = {20, 15, 30, 15, 20};
        String[] headers = {"Reg. ID", "Officer NRIC", "Project Name", "Status", "Date"};
        ConsoleUtils.displayTableHeader(headers, widths);

        for (OfficerRegistration registration : registrations) {
            String projectName = registration.getProjectName();
            String[] values = {
                registration.getRegistrationId(),
                registration.getOfficerNRIC(),
                projectName,
                registration.getStatus() != null ? registration.getStatus().toString() : "N/A",
                DateUtils.formatDate(registration.getRegistrationDate())
            };
            ConsoleUtils.displayTableRow(values, widths);
        }
    }

    /**
     * Displays detailed information about a specific registration.
     * @param registration The OfficerRegistration object to display
     * @param officer The associated HDBOfficer object
     * @param project The associated Project object
     */
    public static void displayRegistrationDetails(OfficerRegistration registration, HDBOfficer officer, Project project) {
        if (registration == null) {
            System.out.println("Registration not found.");
            return;
        }
        System.out.println("\n===== Registration Details =====");
        System.out.println("Registration ID: " + registration.getRegistrationId());

        if (officer != null) {
            System.out.println("\nOfficer Information:");
            System.out.println("NRIC: " + officer.getID());
            System.out.println("Name: " + officer.getName());
            System.out.println("Age: " + officer.getAge());
        } else {
            System.out.println("Officer NRIC: " + registration.getOfficerNRIC());
        }

        if (project != null) {
            System.out.println("\nProject Information:");
            System.out.println("Project Name: " + project.getProjectName());
            System.out.println("Neighborhood: " + project.getNeighborhood());
            System.out.println("Opening Date: " + DateUtils.formatDate(project.getOpeningDate()));
            System.out.println("Closing Date: " + DateUtils.formatDate(project.getClosingDate()));
            System.out.println("Officer Slots: " + project.getRemainingOfficerSlots() +
                               " remaining out of " + project.getOfficerSlots());
        } else {
            System.out.println("Project Name: " + registration.getProjectName());
        }

        System.out.println("\nRegistration Status:");
        System.out.println("Status: " + (registration.getStatus() != null ? registration.getStatus() : "N/A"));
        System.out.println("Registration Date: " + DateUtils.formatDate(registration.getRegistrationDate()));
        System.out.println("Last Updated: " + DateUtils.formatDate(registration.getStatusUpdateDate()));

        if (registration.getRemarks() != null && !registration.getRemarks().isEmpty()) {
            System.out.println("\nRemarks:");
            System.out.println(registration.getRemarks());
        }
    }

    /**
     * Displays pending registrations for a specific project.
     * @param registrations List of OfficerRegistration objects to display
     * @param project The Project object associated with the registrations
     */
    public static void displayPendingRegistrations(List<OfficerRegistration> registrations, Project project) {
        if (registrations == null || registrations.isEmpty()) {
            System.out.println("No registrations available for this project.");
            return;
        }
        System.out.println("\n===== Pending Registrations for Project: " + project.getProjectName() + " =====");
        List<OfficerRegistration> pendingRegistrations = new java.util.ArrayList<>();
        for (OfficerRegistration registration : registrations) {
            if (registration.getStatus() == RegistrationStatus.PENDING) {
                pendingRegistrations.add(registration);
            }
        }
        if (pendingRegistrations.isEmpty()) {
            System.out.println("No pending registrations found.");
            return;
        }
        displayRegistrations(pendingRegistrations);
    }

    /**
     * Displays registrations for a specific officer.
     * @param registrations List of OfficerRegistration objects to display
     * @param officerNRIC The NRIC of the officer
     */
    public static void displayOfficerRegistrations(List<OfficerRegistration> registrations, String officerNRIC) {
        if (registrations == null || registrations.isEmpty()) {
            System.out.println("No registrations available for this officer.");
            return;
        }
        System.out.println("\n===== Registrations for Officer: " + officerNRIC + " =====");
        displayRegistrations(registrations);
    }

    /**
     * Displays a form for approving or rejecting a registration.
     * @param registration The OfficerRegistration object to approve or reject
     */
    public static void displayApprovalForm(OfficerRegistration registration) {
        if (registration == null) {
            System.out.println("Registration not found.");
            return;
        }
        System.out.println("\n===== Registration Approval =====");
        System.out.println("Registration ID: " + registration.getRegistrationId());
        System.out.println("Officer NRIC: " + registration.getOfficerNRIC());
        System.out.println("Project Name: " + registration.getProjectName());
        System.out.println("Registration Date: " + DateUtils.formatDate(registration.getRegistrationDate()));

        if (registration.getStatus() != RegistrationStatus.PENDING) {
            System.out.println("\nThis registration has already been processed.");
            System.out.println("Status: " + registration.getStatus());
            if (registration.getRemarks() != null && !registration.getRemarks().isEmpty()) {
                System.out.println("Remarks: " + registration.getRemarks());
            }
        } else {
            System.out.println("\nCurrent Status: Pending");
            System.out.println("\nOptions:");
            System.out.println("1. Approve Registration");
            System.out.println("2. Reject Registration");
            System.out.println("0. Cancel");
        }
    }
}
