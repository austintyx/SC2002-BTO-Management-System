package main.boundary;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import main.controller.ApplicationController;
import main.controller.EnquiriesController;
import main.controller.ProjectController;
import main.controller.PasswordController;
import main.model.application.Application;
import main.model.application.ApplicationStatus;
import main.model.enquiry.Enquiry;
import main.model.project.Project;
import main.model.user.Applicant;
import main.repository.UserRepository;
import main.utils.PasswordChange;
import main.utils.ConsoleUtils;
import main.utils.LogUtils;

/**
 * Boundary class for Applicant user interface.
 * Handles all user interactions for applicants in the BTO Management System.
 * Uses project name as the unique project identifier.
 * 
 * @author Your Team
 * @version 1.0
 */
public class ApplicantUI implements PasswordChange{
    private final Scanner scanner;
    private final Applicant currentApplicant;
    private final ProjectController projectController;
    private final ApplicationController applicationController;
    private final EnquiriesController enquiriesController;
    private final PasswordController passwordController;
    private final UserRepository userRepository;

    /**
     * Constructs an ApplicantUI with required dependencies.
     * 
     * @param applicant The logged-in applicant
     * @param projectController Project controller instance
     * @param applicationController Application controller instance
     * @param enquiriesController Enquiries controller instance
     * @param passwordController Password controller instance
     * @param userRepository User repository instance
     */
    public ApplicantUI(Applicant applicant,
                      ProjectController projectController,
                      ApplicationController applicationController,
                      EnquiriesController enquiriesController,
                      PasswordController passwordController,
                      UserRepository userRepository) {
        this.scanner = new Scanner(System.in);
        this.currentApplicant = applicant;
        this.projectController = projectController;
        this.applicationController = applicationController;
        this.enquiriesController = enquiriesController;
        this.passwordController = passwordController;
        this.userRepository = userRepository;
    }

    /**
     * Displays the main menu and handles user input.
     */
    public void applicantUI() {
        int choice = 0;
        do {
            System.out.println("\n===== Applicant Menu =====");
            System.out.println("Welcome, " + currentApplicant.getName() + "!");
            System.out.println("1. View Available BTO Projects");
            System.out.println("2. Apply for BTO Project");
            System.out.println("3. View My Application");
            System.out.println("4. Request Application Withdrawal");
            System.out.println("5. Manage Enquiries");
            System.out.println("6. Change Password");
            System.out.println("0. Logout");
            choice = ConsoleUtils.readIntWithValidation("Enter your choice: ", "Invalid input.", 0, 6);
            menuChoice(choice);
        } while (choice != 0);
    }

    /**
     * Processes menu selection.
     * @param choice The selected menu option
     */
    private void menuChoice(int choice) {
        switch (choice) {
            case 1 -> viewAvailableProjects();
            case 2 -> applyForProject();
            case 3 -> viewMyApplication();
            case 4 -> requestWithdrawal();
            case 5 -> manageEnquiries();
            case 6 -> changePassword(currentApplicant, passwordController, userRepository);
            case 0 -> {
                System.out.println("Logging out...");
                LogUtils.auditLog(currentApplicant.getID(), "Logout", "Applicant logged out");
            }
            default -> System.out.println("Invalid choice. Please try again.");
        }
    }

    /**
     * Displays available BTO projects that match the applicant's eligibility.
     */
    private void viewAvailableProjects() {
        System.out.println("\n===== Available BTO Projects =====");
        List<Project> projects = projectController.getVisibleProjects();
        if (projects.isEmpty()) {
            System.out.println("No projects are currently available.");
            return;
        }
        ProjectViewer.displayProjects(projects);
        String projectName = ConsoleUtils.readOptionalInput("\nEnter project name to view details (or 0 to return): ");
        if (!projectName.equals("0") && !projectName.isEmpty()) {
            Project project = projectController.getProjectByName(projectName);
            if (project != null) {
                ProjectViewer.displayProjectDetails(project, true);
                ConsoleUtils.pressEnterToContinue();
            } else {
                System.out.println("Project not found.");
            }
        }
    }

    /**
     * Handles the BTO project application process.
     */
    private void applyForProject() {
        System.out.println("\n===== Apply for BTO Project =====");
        if (applicationController.hasActiveApplication(currentApplicant.getID())) {
            System.out.println("You already have an active application.");
            return;
        }

        List<Project> projects = projectController.getVisibleProjects();
        if (projects.isEmpty()) {
            System.out.println("No projects are currently available.");
            return;
        }

        ProjectViewer.displayProjects(projects);
        String projectName = ConsoleUtils.readOptionalInput("\nEnter project name to apply (or 0 to cancel): ");
        if (projectName.equals("0") || projectName.isEmpty()) return;

        Project project = projectController.getProjectByName(projectName);
        if (project == null) {
            System.out.println("Project not found.");
            return;
        }

        Map<String, Integer> flatTypes = project.getFlatTypes();
        String[] flatTypeOptions = flatTypes.keySet().stream()
            .map(flatType -> {
                boolean eligible = currentApplicant.isEligibleForFlatType(flatType);
                int remaining = project.getRemainingFlats().getOrDefault(flatType, 0);
                return String.format("%s (%d remaining) %s", 
                    flatType, remaining, eligible ? "" : "[Not Eligible]");
            })
            .toArray(String[]::new);

        int choice = ConsoleUtils.displayMenu("Select Flat Type", flatTypeOptions);
        if (choice == -1) return;

        String selectedFlatType = (String) flatTypes.keySet().toArray()[choice];
        boolean success = applicationController.applyForProject(
            currentApplicant.getID(),
            projectName,
            selectedFlatType
        );

        if (success) {
            System.out.println("Application submitted successfully!");
            LogUtils.auditLog(currentApplicant.getID(), "Apply", 
                "Applied for project " + projectName + " (" + selectedFlatType + ")");
        } else {
            System.out.println("Application failed. Check eligibility and availability.");
        }
    }

    /**
     * Displays the applicant's current application details.
     */
    private void viewMyApplication() {
        System.out.println("\n===== My Application =====");
        List<Application> applications = applicationController.getApplicationsByApplicant(currentApplicant.getID());
        
        if (applications.isEmpty()) {
            System.out.println("No application found.");
            return;
        }
        
        // Display latest application
        Application application = applications.get(applications.size() - 1);
        Project project = projectController.getProjectByName(application.getProjectName());
        ApplicationViewer.displayApplicationDetails(application, project, currentApplicant);
        ConsoleUtils.pressEnterToContinue();
    }
    

    /**
     * Handles application withdrawal requests.
     */
    private void requestWithdrawal() {
        System.out.println("\n===== Request Application Withdrawal =====");
        
        // Get applications from repository
        List<Application> applications = applicationController.getApplicationsByApplicant(currentApplicant.getID());
        if (applications.isEmpty()) {
            System.out.println("You have not applied for any project.");
            return;
        }
        
        Application application = applications.get(0);
        
        if (application.getStatus() == ApplicationStatus.PENDING_WITHDRAWAL) {
            System.out.println("You already have a pending withdrawal request.");
            return;
        }
        
        if (!ConsoleUtils.confirmAction("Are you sure you want to request withdrawal? (Y/N): ")) {
            System.out.println("Withdrawal request cancelled.");
            return;
        }
        
        boolean success = false;
        try {
            success = applicationController.requestWithdrawal(
                currentApplicant.getID(), 
                application.getProjectName()
            );
        } catch (Exception e) {
            // Optionally log the exception: e.printStackTrace();
            success = false;
        }
        
        if (success) {
            System.out.println("Withdrawal request submitted successfully!");
            LogUtils.auditLog(currentApplicant.getID(), "Withdrawal", 
                "Requested withdrawal from " + application.getProjectName());
        } else {
            System.out.println("Failed to submit withdrawal request.");
        }
    }
    
    

    /**
     * Manages enquiry-related operations.
     */
    private void manageEnquiries() {
        int choice;
        do {
            System.out.println("\n===== Manage Enquiries =====");
            System.out.println("1. Create New Enquiry");
            System.out.println("2. View My Enquiries");
            System.out.println("3. Edit Enquiry");
            System.out.println("4. Delete Enquiry");
            System.out.println("0. Back to Main Menu");
            choice = ConsoleUtils.readIntWithValidation("Enter your choice: ", "Invalid input.", 0, 4);
            switch (choice) {
                case 1 -> createEnquiry();
                case 2 -> viewMyEnquiries();
                case 3 -> editEnquiry();
                case 4 -> deleteEnquiry();
                case 0 -> {}
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 0);
    }

    /**
     * Creates a new enquiry for a project.
     */
    private void createEnquiry() {
        System.out.println("\n===== Create New Enquiry =====");
        List<Project> projects = projectController.getVisibleProjects();
        ProjectViewer.displayProjects(projects);
        
        String projectName = ConsoleUtils.readOptionalInput("\nEnter project name for enquiry (or 0 to cancel): ");
        if (projectName.equals("0") || projectName.isEmpty()) return;
        
        Project project = projectController.getProjectByName(projectName);
        if (project == null) {
            System.out.println("Project not found.");
            return;
        }
        
        String enquiryText = ConsoleUtils.readNonEmptyString("Enter your enquiry: ");
        boolean success = enquiriesController.createEnquiry(
            currentApplicant.getID(), 
            projectName, 
            enquiryText
        );
        
        if (success) {
            System.out.println("Enquiry submitted successfully!");
            LogUtils.auditLog(currentApplicant.getID(), "Enquiry", 
                "Created enquiry for " + projectName);
        } else {
            System.out.println("Failed to submit enquiry.");
        }
    }

    /**
     * Displays the applicant's enquiries.
     */
    private void viewMyEnquiries() {
        System.out.println("\n===== My Enquiries =====");
        List<Enquiry> enquiries = enquiriesController.getEnquiriesByApplicant(currentApplicant.getID());
        EnquiriesViewer.displayApplicantEnquiries(enquiries, currentApplicant.getID(), userRepository);
        
        String enquiryId = ConsoleUtils.readOptionalInput("\nEnter enquiry ID to view details (or 0 to return): ");
        if (!enquiryId.equals("0") && !enquiryId.isEmpty()) {
            Enquiry enquiry = enquiriesController.getEnquiryById(enquiryId);
            if (enquiry != null) {
                Project project = projectController.getProjectByName(enquiry.getProjectName());
                EnquiriesViewer.displayEnquiryDetails(enquiry, project, userRepository);
                ConsoleUtils.pressEnterToContinue();
            } else {
                System.out.println("Enquiry not found.");
            }
        }
    }

    /**
     * Edits an existing enquiry.
     */
    private void editEnquiry() {
        System.out.println("\n===== Edit Enquiry =====");
        List<Enquiry> enquiries = enquiriesController.getEnquiriesByApplicant(currentApplicant.getID());
        EnquiriesViewer.displayApplicantEnquiries(enquiries, currentApplicant.getID(), userRepository);
        
        String enquiryId = ConsoleUtils.readOptionalInput("\nEnter enquiry ID to edit (or 0 to cancel): ");
        if (enquiryId.equals("0") || enquiryId.isEmpty()) return;
        
        Enquiry enquiry = enquiriesController.getEnquiryById(enquiryId);
        if (enquiry == null || enquiry.hasReply()) {
            System.out.println("Cannot edit this enquiry.");
            return;
        }
        
        String newText = ConsoleUtils.readNonEmptyString("Enter new enquiry text: ");
        boolean success = enquiriesController.updateEnquiry(
            enquiryId, 
            currentApplicant.getID(), 
            newText
        );
        
        if (success) {
            System.out.println("Enquiry updated successfully!");
            LogUtils.auditLog(currentApplicant.getID(), "Enquiry", "Updated " + enquiryId);
        } else {
            System.out.println("Failed to update enquiry.");
        }
    }

    /**
     * Deletes an existing enquiry.
     */
    private void deleteEnquiry() {
        System.out.println("\n===== Delete Enquiry =====");
        List<Enquiry> enquiries = enquiriesController.getEnquiriesByApplicant(currentApplicant.getID());
        EnquiriesViewer.displayApplicantEnquiries(enquiries, currentApplicant.getID(), userRepository);
        
        String enquiryId = ConsoleUtils.readOptionalInput("\nEnter enquiry ID to delete (or 0 to cancel): ");
        if (enquiryId.equals("0") || enquiryId.isEmpty()) return;
        
        if (!ConsoleUtils.confirmAction("Are you sure you want to delete this enquiry? (Y/N): ")) {
            System.out.println("Deletion cancelled.");
            return;
        }
        
        boolean success = enquiriesController.deleteEnquiry(enquiryId, currentApplicant.getID());
        if (success) {
            System.out.println("Enquiry deleted successfully!");
            LogUtils.auditLog(currentApplicant.getID(), "Enquiry", "Deleted " + enquiryId);
        } else {
            System.out.println("Failed to delete enquiry.");
        }
    }


}
