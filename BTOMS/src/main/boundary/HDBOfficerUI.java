package main.boundary;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import main.controller.ApplicationController;
import main.controller.EnquiriesController;
import main.controller.HDBOfficerController;
import main.controller.OfficerRegistrationController;
import main.controller.ProjectController;
import main.model.application.Application;
import main.model.application.ApplicationStatus;
import main.model.enquiry.Enquiry;
import main.model.project.Project;
import main.model.registration.OfficerRegistration;
import main.model.user.HDBOfficer;
import main.model.user.MaritalStatus;
import main.model.user.User;
import main.repository.UserRepository;
import main.utils.ConsoleUtils;
import main.utils.LogUtils;
import main.utils.ReportGenerator;
import main.utils.UserLookupService;
import main.utils.Password.ConsolePasswordUI;
import main.utils.Password.PasswordChanger;
import main.utils.Password.PasswordService;
import main.utils.Password.PasswordUI;
import main.utils.Password.RegexPasswordPolicy;

/**
 * Boundary class for HDB Officer user interface.
 * Handles all user interactions for HDB Officers.
 * Uses project name as the unique project identifier.
 * 
 * @author Your Team
 * @version 1.0
 */
public class HDBOfficerUI implements PasswordChanger,UserLookupService{
    private final Scanner scanner;
    private final HDBOfficer currentOfficer;
    private final ProjectController projectController;
    private final ApplicationController applicationController;
    private final EnquiriesController enquiriesController;
    private final OfficerRegistrationController officerRegistrationController;
    private final HDBOfficerController hdbOfficerController;
    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final PasswordUI passwordUI;

    /**
     * Constructs an HDBOfficerUI with required controllers and repositories.
     */
    public HDBOfficerUI(HDBOfficer officer,
                        ProjectController projectController,
                        ApplicationController applicationController,
                        EnquiriesController enquiriesController,
                        OfficerRegistrationController officerRegistrationController,
                        HDBOfficerController hdbOfficerController,
                        UserRepository userRepository) {
        this.scanner = new Scanner(System.in);
        this.currentOfficer = officer;
        this.projectController = projectController;
        this.applicationController = applicationController;
        this.enquiriesController = enquiriesController;
        this.officerRegistrationController = officerRegistrationController;
        this.hdbOfficerController = hdbOfficerController;
        this.userRepository = userRepository;
        this.passwordService = new PasswordService(
            userRepository, 
            new RegexPasswordPolicy()
        );
        this.passwordUI = new ConsolePasswordUI();
    }

    /**
     * Displays the main menu and processes user choices.
     */
    public void officerUI() {
        int choice;
        do {
            System.out.println("\n===== HDB Officer Menu =====");
            System.out.println("Welcome, " + currentOfficer.getName() + "!");
            
            List<String> handlingProjects = currentOfficer.getHandlingProjects();
            if (handlingProjects != null && !handlingProjects.isEmpty()) {
                System.out.println("Handling Projects: " + String.join(", ", handlingProjects));
            } else {
                System.out.println("You are not handling any projects.");
            }
            System.out.println("1. View Available BTO Projects");
            System.out.println("2. Register for a Project");
            System.out.println("3. View Registration Status");
            System.out.println("4. Manage Flat Selection");
            System.out.println("5. View Project Enquiries");
            System.out.println("6. Reply to Enquiries");
            System.out.println("7. Generate Flat Selection Receipt");
            System.out.println("8. Apply for BTO Project (as Applicant)");
            System.out.println("9. View My Application");
            System.out.println("10. Request Application Withdrawal");
            System.out.println("11. Change Password");
            System.out.println("0. Logout");
            choice = ConsoleUtils.readIntWithValidation("Enter your choice: ", "Invalid input.", 0, 11);
            processMenuChoice(choice);
        } while (choice != 0);
    }

    /**
     * Processes the selected menu choice.
     * @param choice The menu option selected by the user.
     */
    private void processMenuChoice(int choice) {
        switch (choice) {
            case 1 -> viewAvailableProjects();
            case 2 -> registerForProject();
            case 3 -> viewRegistrationStatus();
            case 4 -> manageFlatSelection();
            case 5 -> viewProjectEnquiries();
            case 6 -> replyToEnquiries();
            case 7 -> generateFlatSelectionReceipt();
            case 8 -> applyForProject();
            case 9 -> viewMyApplication();
            case 10 -> requestWithdrawal();
            case 11 -> changePassword(currentOfficer);
            case 0 -> {
                System.out.println("Logging out...");
                LogUtils.auditLog(currentOfficer.getID(), "Logout", "HDB Officer logged out");
            }
            default -> System.out.println("Invalid choice. Please try again.");
        }
    }

    /**
     * Displays all available BTO projects.
     */
    private void viewAvailableProjects() {
        System.out.println("\n===== Available BTO Projects =====");
        List<Project> projects = projectController.getHandlingProjects(currentOfficer.getID());
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
            } else {
                System.out.println("Project not found.");
            }
            ConsoleUtils.pressEnterToContinue();
        }
    }

    /**
     * Allows officer to register for a project by project name.
     */
    private void registerForProject() {
        System.out.println("\n===== Register for a Project =====");
        List<Project> availableProjects = projectController.getAllProjects().stream()
            .filter(p -> p.isVisible() && p.getRemainingOfficerSlots() > 0)
            .toList();
        if (availableProjects.isEmpty()) {
            System.out.println("No projects are currently available for registration.");
            return;
        }
        ProjectViewer.displayProjects(availableProjects);
        String projectName = ConsoleUtils.readOptionalInput("\nEnter project name to register (or 0 to cancel): ");
        if (projectName.equals("0") || projectName.isEmpty()) return;
        boolean success = officerRegistrationController.registerOfficerForProject(currentOfficer.getID(), projectName);
        if (success) {
            System.out.println("Registration submitted successfully! Waiting for approval.");
            LogUtils.auditLog(currentOfficer.getID(), "Register", "Registered for project " + projectName);
        } else {
            System.out.println("Failed to register for the project. Check eligibility and slots.");
        }

        
    }

    /**
     * Views the registration status for the officer.
     */
    private void viewRegistrationStatus() {
        System.out.println("\n===== Registration Status =====");
        List<OfficerRegistration> registrations = officerRegistrationController.getRegistrationsByOfficer(currentOfficer.getID());
        if (registrations.isEmpty()) {
            System.out.println("You have not registered for any projects.");
            ConsoleUtils.pressEnterToContinue();
            return;
        }
        List<Project> projects = projectController.getAllProjects();
        OfficerRegistrationViewer.displayRegistrationsWithProjectNames(registrations, projects);
        if (registrations == null || registrations.isEmpty()) {
            System.out.println("No registrations available.");
            ConsoleUtils.pressEnterToContinue();
            return;
        }else{
            ConsoleUtils.pressEnterToContinue();
        }
    }

    /**
     * Allows the officer to manage flat selection for applicants.
     */
    private void manageFlatSelection() {
    System.out.println("\n===== Manage Flat Selection =====");
    List<String> handlingProjects = currentOfficer.getHandlingProjects();
    
    if (handlingProjects.isEmpty()) {
        System.out.println("You are not handling any projects.");
        return;
    }
    
    // Let officer choose project
    System.out.println("Select a project:");
    for (int i = 0; i < handlingProjects.size(); i++) {
        System.out.printf("%d. %s\n", i+1, handlingProjects.get(i));
    }
    
    int projectChoice = ConsoleUtils.readIntWithValidation(
        "Enter choice: ", 
        "Invalid input", 
        1, 
        handlingProjects.size()
    );
    String selectedProject = handlingProjects.get(projectChoice - 1);
    
    // Get project details
    Project project = projectController.getProjectByName(selectedProject);
    if (project == null) {
        System.out.println("Project not found.");
        return;
    }
    
    // Get successful applications for this project
    List<Application> applications = applicationController.getApplicationsByProject(selectedProject)
        .stream()
        .filter(app -> app.getStatus() == ApplicationStatus.SUCCESSFUL)
        .toList();
    
    if (applications.isEmpty()) {
        System.out.println("No successful applications for this project.");
        return;
    }
    
    ApplicationViewer.displayApplications(applications);
    
    // Get applicant NRIC
    String applicantNRIC = ConsoleUtils.readNonEmptyInput("Enter Applicant NRIC: ");
    
    // Filter applications for this applicant and project
    List<Application> applicantApplications = applications.stream()
        .filter(app -> app.getApplicantId().equals(applicantNRIC))
        .toList();
    
    if (applicantApplications.isEmpty()) {
        System.out.println("No successful applications found for this applicant.");
        return;
    }
    
    // Let officer choose application
    System.out.println("Select application:");
    for (int i = 0; i < applicantApplications.size(); i++) {
        Application app = applicantApplications.get(i);
        System.out.printf("%d. %s (Applied: %s)\n", 
            i+1, 
            app.getApplicationId(), 
            new SimpleDateFormat("dd MMM yyyy").format(app.getApplicationDate())
        );
    }
    
    int appChoice = ConsoleUtils.readIntWithValidation(
        "Enter choice: ", 
        "Invalid selection", 
        1, 
        applicantApplications.size()
    );
    Application application = applicantApplications.get(appChoice - 1);
    
    // Get flat type with validation
    String flatType;
    while (true) {
        flatType = ConsoleUtils.readNonEmptyInput("Enter Flat Type (2-Room/3-Room): ");
        if (!flatType.equals(application.getFlatType())) {
            System.out.println("Invalid flat type. Please enter the flat type matching the application: " + application.getFlatType());
            continue;
        }
        break;
    }
    
    // Check flat availability
    int remaining = project.getRemainingFlats().getOrDefault(flatType, 0);
    if (remaining <= 0) {
        System.out.println("No available " + flatType + " units.");
        return;
    }
    
    // Update application and project
    boolean success = hdbOfficerController.bookFlat(
        application.getApplicationId(),
        currentOfficer.getID(),
        flatType
    );
    
    if (success) {
        System.out.println("Flat booked successfully!");
        System.out.println("Remaining " + flatType + " units: " + (remaining - 1));
        
        // Generate and display receipt
        String receipt = hdbOfficerController.generateFlatSelectionReceipt(
            application.getApplicationId(),
            project.getProjectName(),
            flatType
        );
        System.out.println("\n=== Booking Receipt ===");
        System.out.println(receipt);
    } else {
        System.out.println("Booking failed. Check availability.");
    }
}

    
    

    /**
     * Views all enquiries for the project the officer is handling.
     */
    private void viewProjectEnquiries() {
        System.out.println("\n===== View Project Enquiries =====");
        if (currentOfficer.getHandlingProjects() == null) {
            System.out.println("You are not currently handling any project.");
            return;
        }
        Project project = projectController.getAllProjects().stream()
            .filter(p -> p.getOfficers().contains(currentOfficer.getID()))
            .findFirst().orElse(null);
        if (project == null) {
            System.out.println("Project information not found.");
            return;
        }
        List<Enquiry> enquiries = enquiriesController.getEnquiriesByProject(project.getProjectName());
        if (enquiries.isEmpty()) {
            System.out.println("No enquiries found for this project.");
            return;
        }
        EnquiriesViewer.displayProjectEnquiries(enquiries, project, userRepository);
        String enquiryId = ConsoleUtils.readOptionalInput("\nEnter enquiry ID to view details (or 0 to return): ");
        if (!enquiryId.equals("0") && !enquiryId.isEmpty()) {
            Enquiry enquiry = enquiriesController.getEnquiryById(enquiryId);
            if (enquiry != null) {
                EnquiriesViewer.displayEnquiryDetails(enquiry, project, userRepository);
            } else {
                System.out.println("Enquiry not found.");
            }
            ConsoleUtils.pressEnterToContinue();
        }
    }

    /**
     * Allows the officer to reply to unanswered enquiries.
     */
    private void replyToEnquiries() {
        System.out.println("\n===== Reply to Enquiries =====");
        if (currentOfficer.getHandlingProjects() == null) {
            System.out.println("You are not currently handling any project.");
            return;
        }
        Project project = projectController.getAllProjects().stream()
            .filter(p -> p.getOfficers().contains(currentOfficer.getID()))
            .findFirst().orElse(null);
        if (project == null) {
            System.out.println("Project information not found.");
            return;
        }
        List<Enquiry> enquiries = enquiriesController.getUnansweredEnquiriesByProject(project.getProjectName());
        if (enquiries.isEmpty()) {
            System.out.println("No unanswered enquiries found for this project.");
            return;
        }
        EnquiriesViewer.displayUnansweredEnquiries(enquiries, userRepository);
        String enquiryId = ConsoleUtils.readOptionalInput("\nEnter enquiry ID to reply (or 0 to cancel): ");
        if (enquiryId.equals("0") || enquiryId.isEmpty()) return;
        Enquiry enquiry = enquiries.stream().filter(e -> e.getEnquiryId().equals(enquiryId)).findFirst().orElse(null);
        if (enquiry == null) {
            System.out.println("Enquiry not found.");
            return;
        }
        System.out.print("Enter your reply: ");
        String replyText = scanner.nextLine();
        if (replyText.trim().isEmpty()) {
            System.out.println("Reply cannot be empty.");
            return;
        }
        boolean success = enquiriesController.replyToEnquiry(enquiryId, currentOfficer.getID(), replyText);
        if (success) {
            System.out.println("Reply submitted successfully!");
            LogUtils.auditLog(currentOfficer.getID(), "Reply", "Replied to enquiry " + enquiryId);
        } else {
            System.out.println("Failed to submit reply. Please try again.");
        }
    }

    /**
     * Generates a flat selection receipt for an applicant.
     */
    private void generateFlatSelectionReceipt() {
        System.out.println("\n===== Generate Flat Selection Receipt =====");
        if (currentOfficer.getHandlingProjects() == null) {
            System.out.println("You are not currently handling any project.");
            return;
        }
        Project project = projectController.getAllProjects().stream()
            .filter(p -> p.getOfficers().contains(currentOfficer.getID()))
            .findFirst().orElse(null);
        if (project == null) {
            System.out.println("Project information not found.");
            return;
        }
        String applicantNRIC;
        while (true) {
            applicantNRIC = ConsoleUtils.readNonEmptyString("Enter applicant's NRIC (or 0 to exit): ").trim();
            if (applicantNRIC.equals("0")) return;
            // Singapore NRIC format: S1234567A (case-insensitive)
            if (applicantNRIC.matches("^[STFGstfg]\\d{7}[A-Za-z]$")) {
                break; // Valid NRIC, exit loop
            } else {
                System.out.println("Invalid NRIC format! Please enter a valid NRIC (e.g., S1234567A).");
            }
        }

        Application application = applicationController.getApplicationsByApplicant(applicantNRIC).stream()
            .filter(app -> app.getProjectName().equals(project.getProjectName()))
            .findFirst().orElse(null);
        if (application == null || application.getStatus() != ApplicationStatus.BOOKED) {
            System.out.println("This applicant has not booked a flat yet.");
            return;
        }
        // Fetch applicant details (assume ApplicantRepository is accessible if needed)
        String applicantName = lookupUserNameByNRIC(userRepository,applicantNRIC); 
        int applicantAge = lookupAgeByNRIC(userRepository, applicantNRIC);
        String maritalStatus = lookupMaritalStatusByNRIC(userRepository, applicantNRIC);
        String receipt = ReportGenerator.generateFlatSelectionReceipt(
            applicantNRIC, applicantName, applicantAge, maritalStatus,
            project.getProjectName(), project.getNeighborhood().toString(), application.getFlatType());
        System.out.println("\n=== Flat Selection Receipt ===\n" + receipt);
        LogUtils.auditLog(currentOfficer.getID(), "Receipt", "Generated receipt for applicant " + applicantNRIC);
    }

    /**
     * Allows the officer to apply for a project as an applicant.
     */
    private void applyForProject() {
        System.out.println("\n===== Apply for BTO Project (as Applicant) =====");
        if (currentOfficer.getHandlingProjects() != null && currentOfficer.getAppliedProjectName() != null) {
            System.out.println("You have already applied for a project as an applicant.");
            return;
        }
        
        List<Project> projects = projectController.getVisibleProjects();
        if (projects.isEmpty()) {
            System.out.println("No projects are currently available.");
            return;
        }
        List<Project> availableProjects = projects.stream()
            .filter(p -> !p.getOfficers().contains(currentOfficer.getID()))
            .toList();
        if (availableProjects.isEmpty()) {
            System.out.println("No projects available for application.");
            return;
        }
        ProjectViewer.displayEligibleProjects(availableProjects, currentOfficer.getAge(), currentOfficer.getMaritalStatus());
        String projectName = ConsoleUtils.readOptionalInput("\nEnter project name to apply (or 0 to cancel): ");
        if (projectName.equals("0") || projectName.isEmpty()) return;
        Project project = projectController.getProjectByName(projectName);
        if (project == null) {
            System.out.println("Project not found.");
            return;
        }
        if (project.getOfficers().contains(currentOfficer.getID())) {
            System.out.println("You cannot apply for a project you are handling as an HDB Officer.");
            return;
        }

        List<OfficerRegistration> pendingRegistrations = officerRegistrationController.getPendingRegistrationsByProject(projectName);
        List<OfficerRegistration> registrations = pendingRegistrations.stream()
            .filter(of -> of.getOfficerNRIC().contains(currentOfficer.getID()))
            .toList();
        if (!registrations.isEmpty()) {
            System.out.println("You cannot apply for a project you are registering as an HDB Officer.");
            return;
        }
        Map<String, Integer> flatTypes = project.getFlatTypes();
        Map<String, Integer> remainingFlats = project.getRemainingFlats();
        List<String> eligibleFlatTypes = flatTypes.keySet().stream()
            .filter(ft -> isEligibleForFlatType(currentOfficer.getAge(), currentOfficer.getMaritalStatus(), ft))
            .toList();
        if (eligibleFlatTypes.isEmpty()) {
            System.out.println("No eligible flat types for you in this project.");
            return;
        }
        System.out.println("\nSelect Flat Type:");
        for (int i = 0; i < eligibleFlatTypes.size(); i++) {
            String ft = eligibleFlatTypes.get(i);
            System.out.printf("%d. %s (%d remaining)\n", i + 1, ft, remainingFlats.get(ft));
        }
        int flatTypeChoice = ConsoleUtils.readIntWithValidation("Select Flat Type: ", "Invalid choice.", 1, eligibleFlatTypes.size()) - 1;
        String selectedFlatType = eligibleFlatTypes.get(flatTypeChoice);
        boolean success = applicationController.applyForProject(currentOfficer.getID(), projectName, selectedFlatType);
        if (success) {
            System.out.println("Application submitted successfully!");
            LogUtils.auditLog(currentOfficer.getID(), "Apply", "Applied for project " + projectName + " with flat type " + selectedFlatType);
        } else {
            System.out.println("Failed to submit application. Please try again.");
        }
    }

    /**
     * Displays the applicant's current application details.
     */
    private void viewMyApplication() {
        System.out.println("\n===== My Application =====");
        List<Application> applications = applicationController.getApplicationsByApplicant(currentOfficer.getID());
        
        if (applications.isEmpty()) {
            System.out.println("No application found.");
            return;
        }
        
        // Display latest application
        Application application = applications.get(applications.size() - 1);
        Project project = projectController.getProjectByName(application.getProjectName());
        ApplicationViewer.displayApplicationDetails(application, project, currentOfficer);
        ConsoleUtils.pressEnterToContinue();
    }
    

    /**
     * Handles application withdrawal requests.
     */
    private void requestWithdrawal() {
        System.out.println("\n===== Request Application Withdrawal =====");
        
        // Get applications from repository
        List<Application> applications = applicationController.getApplicationsByApplicant(currentOfficer.getID());
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
                currentOfficer.getID(), 
                application.getProjectName()
            );
        } catch (Exception e) {
            // Optionally log the exception: e.printStackTrace();
            success = false;
        }
        
        if (success) {
            System.out.println("Withdrawal request submitted successfully!");
            LogUtils.auditLog(currentOfficer.getID(), "Withdrawal", 
                "Requested withdrawal from " + application.getProjectName());
        } else {
            System.out.println("Failed to submit withdrawal request.");
        }
    }

    /**
     * Helper method to check if a user is eligible for a flat type based on age and marital status.
     * @param age The user's age
     * @param maritalStatus The user's marital status
     * @param flatType The flat type to check
     * @return true if eligible, false otherwise
     */
    private boolean isEligibleForFlatType(int age, MaritalStatus maritalStatus, String flatType) {
        if (flatType.equals("2-Room")) {
            return (maritalStatus == MaritalStatus.SINGLE && age >= 35) ||
                   (maritalStatus == MaritalStatus.MARRIED && age >= 21);
        } else if (flatType.equals("3-Room")) {
            return maritalStatus == MaritalStatus.MARRIED && age >= 21;
        }
        return false;
    }

    public void changePassword(User user) {
        PasswordChanger.super.changePassword(user, passwordService, passwordUI);
    }
}
