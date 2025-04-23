package main.controller;

import java.util.List;
import main.model.application.Application;
import main.model.application.ApplicationStatus;
import main.model.project.Project;
import main.model.registration.RegistrationStatus;
import main.model.user.Applicant;
import main.model.user.HDBOfficer;
import main.model.user.User;
import main.repository.ApplicationRepository;
import main.repository.ProjectRepository;
import main.repository.UserRepository;
import main.utils.IDGenerator;

/**
 * Controller for application-related operations in the BTO Management System.
 * Handles applying, withdrawing, approving/rejecting applications, and flat selection.
 * Uses project name as the unique project identifier.
 * 
 * @author Your Name
 * @version 1.0
 * @since 2025-04-17
 */
public class ApplicationController {
    private final ApplicationRepository applicationRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    /**
     * Constructs an ApplicationController with required repositories.
     * @param applicationRepository Repository for applications
     * @param projectRepository Repository for projects
     * @param userRepository Repository for users
     */
    public ApplicationController(ApplicationRepository applicationRepository, 
                                ProjectRepository projectRepository,
                                UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    /**
     * Allows an applicant to apply for a BTO project.
     * @param applicantNRIC NRIC of the applicant
     * @param projectName Name of the project to apply for
     * @param flatType Type of flat being applied for
     * @return true if application succeeds, false otherwise
     */
    public boolean applyForProject(String userNRIC, String projectName, String flatType) {
    // Validate user exists and is eligible
    User user = userRepository.findById(userNRIC);
    if (!(user instanceof Applicant applicant)) {
        return false;
    }

    // Additional check for HDB Officers
    if (user instanceof HDBOfficer officer) {
        // Officers cannot apply for projects they're handling
        if (officer.getHandlingProjects().contains(projectName)) {
            System.out.println("Officers cannot apply for projects they handle");
            return false;
        }
    }

    // Check project and availability
    Project project = projectRepository.findByName(projectName);
    if (project == null || 
        !project.isVisible() || 
        project.getRemainingFlats().getOrDefault(flatType, 0) <= 0) {
        return false;
    }

    // Check eligibility (works for both Applicant and HDBOfficer)
    if (!applicant.isEligibleForFlatType(flatType)) {
        System.out.println("User not eligible for selected flat type");
        return false;
    }

    // Check existing applications
    if (hasActiveApplication(userNRIC)) {
        System.out.println("User already has an active application");
        return false;
    }

    // Create application
    String applicationId = IDGenerator.generateApplicationId();
    Application application = new Application(applicationId, userNRIC, projectName, userRepository);
    application.setFlatType(flatType);
    application.setStatus(ApplicationStatus.PENDING);

    boolean success = applicationRepository.save(application);

    if (success) {
        // Update user's applied project
        applicant.setAppliedProjectName(projectName);
        userRepository.update(applicant);
        
        // For officers: Check registration status
        if (user instanceof HDBOfficer officer) {
            if (officer.getRegistrationStatus() == RegistrationStatus.APPROVED) {
                System.out.println("Warning: Applying may affect current officer assignments");
            }
        }
    }
    
    return success;
}


    /**
     * Processes withdrawal request for an application.
     * @param applicantNRIC NRIC of the applicant
     * @param projectName Name of the project
     * @return true if withdrawal request succeeds, false otherwise
     */
    public boolean requestWithdrawal(String applicantNRIC, String projectName) {
        Application application = applicationRepository.findByApplicantAndProject(applicantNRIC, projectName);
        if (application == null || 
            application.getStatus().isFinalized() ||
            application.getStatus() == ApplicationStatus.PENDING_WITHDRAWAL) {
            return false;
        }
        application.setStatus(ApplicationStatus.PENDING_WITHDRAWAL);
        return applicationRepository.update(application);
    }
    

    /**
     * Approves a BTO application (Manager only).
     * @param applicationId ID of the application
     * @param managerNRIC NRIC of approving manager
     * @return true if approval succeeds, false otherwise
     */
    public boolean approveApplication(String applicationId, String managerNRIC) {
        Application application = applicationRepository.findById(applicationId);
        if (application == null || application.getStatus() != ApplicationStatus.PENDING) return false;

        Project project = projectRepository.findByName(application.getProjectName());
        if (project == null || !project.getManagerInCharge().equals(managerNRIC)) return false;

        // Check flat availability
        String flatType = application.getFlatType();
        if (project.getRemainingFlats().getOrDefault(flatType, 0) <= 0) return false;

        application.setStatus(ApplicationStatus.SUCCESSFUL);
        return applicationRepository.update(application);
    }

    /**
     * Rejects a BTO application (Manager only).
     * @param applicationId ID of the application
     * @param managerNRIC NRIC of rejecting manager
     * @return true if rejection succeeds, false otherwise
     */
    public boolean rejectApplication(String applicationId, String managerNRIC) {
        Application application = applicationRepository.findById(applicationId);
        if (application == null || application.getStatus() != ApplicationStatus.PENDING) return false;

        Project project = projectRepository.findByName(application.getProjectName());
        if (project == null || !project.getManagerInCharge().equals(managerNRIC)) return false;

        application.setStatus(ApplicationStatus.UNSUCCESSFUL);
        return applicationRepository.update(application);
    }

    /**
     * Approves a withdrawal request (Manager only).
     * @param applicationId ID of the application
     * @param managerNRIC NRIC of approving manager
     * @return true if approval succeeds, false otherwise
     */
    public boolean approveWithdrawal(String applicationId, String managerNRIC) {
        Application application = applicationRepository.findById(applicationId);
        if (application == null || 
            application.getStatus() != ApplicationStatus.PENDING_WITHDRAWAL) {
            return false;
        }

        Project project = projectRepository.findByName(application.getProjectName());
        if (project == null || !project.getManagerInCharge().equals(managerNRIC)) {
            return false;
        }

        // Handle flat inventory if booked
        if (application.getStatus() == ApplicationStatus.BOOKED) {
            project.incrementFlatCount(application.getFlatType());
            projectRepository.update(project);
        }

        application.setStatus(ApplicationStatus.WITHDRAWN);
        return applicationRepository.update(application);
    }

     /**
     * Retrieves all applications with pending withdrawal requests.
     * @return List of applications with pending withdrawal status
     */
    public List<Application> getPendingWithdrawalRequests() {
        return applicationRepository.findByStatus(ApplicationStatus.PENDING_WITHDRAWAL);
    }

    /**
     * Rejects a withdrawal request for an application. Only a manager of the project can reject.
     * @param applicationId The ID of the application to reject withdrawal for
     * @param managerNRIC The NRIC of the manager performing the rejection
     * @return true if rejection succeeds, false otherwise
     */
    public boolean rejectWithdrawal(String applicationId, String managerNRIC) {
        Application application = applicationRepository.findById(applicationId);
        if (application == null || 
            application.getStatus() != ApplicationStatus.PENDING_WITHDRAWAL) {
            return false;
        }

        // Check manager authorization
        Project project = projectRepository.findByName(application.getProjectName());
        if (project == null || !project.getManagerInCharge().equals(managerNRIC)) {
            return false;
        }

        // Revert to previous status before withdrawal request
        ApplicationStatus previousStatus = application.getPreviousStatus(); // Assume this method exists
        application.setStatus(previousStatus);
        return applicationRepository.update(application);
    }

    /**
     * Updates flat selection (Officer only).
     * @param officerNRIC NRIC of HDB Officer
     * @param applicantNRIC NRIC of applicant
     * @param projectName Name of the project
     * @param flatType Type of flat selected
     * @return true if update succeeds, false otherwise
     */
    public boolean updateFlatSelection(String officerNRIC, String applicantNRIC, 
                                      String projectName, String flatType) {
        Application application = applicationRepository.findByApplicantAndProject(applicantNRIC, projectName);
        if (application == null || application.getStatus() != ApplicationStatus.SUCCESSFUL) return false;

        Project project = projectRepository.findByName(projectName);
        if (project == null || !project.getOfficers().contains(officerNRIC)) return false;

        // Validate flat availability
        if (project.getRemainingFlats().getOrDefault(flatType, 0) <= 0) return false;

        // Update application and project
        application.setStatus(ApplicationStatus.BOOKED);
        application.setFlatType(flatType);
        project.decrementFlatCount(flatType);

        return applicationRepository.update(application) && projectRepository.update(project);
    }

    /**
     * Retrieves applications for a project.
     * @param projectName Name of the project
     * @return List of applications for the project
     */
    public List<Application> getApplicationsByProject(String projectName) {
        return applicationRepository.findByProject(projectName);
    }

    /**
     * Retrieves all applications submitted by a specific applicant.
     * @param applicantNRIC The NRIC of the applicant
     * @return List of applications submitted by the applicant
     */
    public List<Application> getApplicationsByApplicant(String applicantNRIC) {
        return applicationRepository.findByApplicant(applicantNRIC);
    }

    /**
     * Checks for active applications.
     * @param applicantNRIC NRIC of the applicant
     * @return true if applicant has active applications, false otherwise
     */
    public boolean hasActiveApplication(String applicantNRIC) {
        List<Application> applications = applicationRepository.findByApplicant(applicantNRIC);
        return applications.stream()
            .anyMatch(app -> !app.getStatus().isFinalized() && 
                            app.getStatus() != ApplicationStatus.WITHDRAWN);
    }

     /**
     * Checks for booked applications.
     * @param applicantNRIC NRIC of the applicant
     * @return true if applicant has active applications, false otherwise
     */
    public boolean hasBooked(String applicantNRIC) {
        List<Application> applications = applicationRepository.findByApplicant(applicantNRIC);
        return applications.stream()
            .anyMatch(app -> app.getStatus() == ApplicationStatus.BOOKED);
    }

}
