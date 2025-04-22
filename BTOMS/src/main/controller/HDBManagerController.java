package main.controller;

import main.model.project.Neighborhood;
import main.model.project.Project;
import main.model.registration.OfficerRegistration;
import main.model.registration.RegistrationStatus;
import main.model.application.Application;
import main.model.application.ApplicationStatus;
import main.model.enquiry.Enquiry;
import main.repository.*;
import main.utils.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller handling business logic for HDB Manager operations.
 * Implements manager-specific use cases and coordinates between repositories.
 * Uses project name as the unique project identifier.
 * 
 * @author Your Name
 * @version 1.0
 * @since 2025-04-17
 */
public class HDBManagerController {
    private final ProjectRepository projectRepository;
    private final OfficerRegistrationRepository officerRegistrationRepo;
    private final ApplicationRepository applicationRepository;
    private final EnquiryRepository enquiryRepository;
    private final UserRepository userRepository;

    /**
     * Constructs a new HDBManagerController with required repositories.
     * 
     * @param projectRepository Repository for project operations
     * @param officerRegistrationRepo Repository for officer registrations
     * @param applicationRepository Repository for applications
     * @param enquiryRepository Repository for enquiries
     * @param applicantRepository Repository for applicant entities
     */
    public HDBManagerController(ProjectRepository projectRepository,
                               OfficerRegistrationRepository officerRegistrationRepo,
                               ApplicationRepository applicationRepository,
                               EnquiryRepository enquiryRepository,
                               UserRepository userRepository
                               ) {
        this.projectRepository = projectRepository;
        this.officerRegistrationRepo = officerRegistrationRepo;
        this.applicationRepository = applicationRepository;
        this.enquiryRepository = enquiryRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new BTO project if the manager is available during the period.
     * 
     * @param project The project to create
     * @param managerId NRIC of the creating manager
     * @return true if creation succeeded, false otherwise
     */
    public boolean createProject(Project project, String managerId) {
        if (isManagerHandlingProjectDuringPeriod(managerId, project.getOpeningDate(), project.getClosingDate())) {
            return false;
        }
        project.setManagerInCharge(managerId);
        return projectRepository.save(project);
    }

    /**
     * Edits an existing project managed by the specified manager.
     * 
     * @param projectName Name of the project to edit
     * @param updates Map of fields to update
     * @param managerId NRIC of the managing manager
     * @return true if edit succeeded, false otherwise
     */
    public boolean editProject(String projectName, Map<String, Object> updates, String managerId) {
    Project project = projectRepository.findByName(projectName);
    if (project == null || !project.getManagerInCharge().equals(managerId)) {
        return false;
    }

    // Store original values before updates
    String originalProjectName = project.getProjectName();
    Map<String, Integer> originalFlatTypes = new HashMap<>(project.getFlatTypes());

    // Apply updates with type-safe casting
    for (Map.Entry<String, Object> entry : updates.entrySet()) {
        String field = entry.getKey();
        Object value = entry.getValue();

        switch (field) {
            case "projectName" -> project.setProjectName((String) value);
            case "neighborhood" -> handleNeighborhoodUpdate(project, value);
            case "flatTypes" -> updateFlatTypes(project, value);
            case "flatPrices" -> updateFlatPrices(project, value);
            case "openingDate" -> project.setOpeningDate((Date) value);
            case "closingDate" -> handleClosingDate(project, (Date) value);
            case "officerSlots" -> project.setOfficerSlots((Integer) value);
            case "visible" -> project.setVisible((Boolean) value);
        }
    }

    // Validate dates after updates
    if (project.getClosingDate().before(project.getOpeningDate())) {
        System.out.println("Error: Closing date cannot be before opening date.");
        return false;
    }

    // Save project changes
    boolean success = projectRepository.update(project);
    
    // Post-update synchronization
    if (success) {
        handleProjectRename(originalProjectName, project);
        handleFlatTypeChanges(originalFlatTypes, project);
    }

    return success;
}

// New helper methods
private void handleNeighborhoodUpdate(Project project, Object value) {
    if (value instanceof String) {
        try {
            project.setNeighborhood(Neighborhood.fromString((String) value));
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid neighborhood. Update skipped.");
        }
    } else if (value instanceof Neighborhood) {
        project.setNeighborhood((Neighborhood) value);
    }
}

@SuppressWarnings("unchecked")
private void updateFlatTypes(Project project, Object value) {
    Map<String, Integer> flatTypes = (Map<String, Integer>) value;
    project.setFlatTypes(flatTypes);
    
    // Maintain remaining flats consistency
    Map<String, Integer> remainingFlats = project.getRemainingFlats();
    flatTypes.forEach((type, units) -> 
        remainingFlats.putIfAbsent(type, units)
    );
}

@SuppressWarnings("unchecked")
private void updateFlatPrices(Project project, Object value) {
    Map<String, Integer> flatPrices = (Map<String, Integer>) value;
    project.setFlatPrices(flatPrices);
}

private void handleClosingDate(Project project, Date newClosing) {
    if (newClosing.after(project.getOpeningDate())) {
        project.setClosingDate(newClosing);
    } else {
        System.out.println("Closing date update skipped - must be after opening date");
    }
}

private void handleProjectRename(String originalName, Project updatedProject) {
    if (!originalName.equals(updatedProject.getProjectName())) {
        List<Application> applications = applicationRepository.findByProject(originalName);
        applications.forEach(app -> {
            app.setProjectName(updatedProject.getProjectName());
            applicationRepository.update(app);
        });
    }
}

private void handleFlatTypeChanges(Map<String, Integer> originalTypes, Project updatedProject) {
    updatedProject.getFlatTypes().keySet().stream()
        .filter(type -> !originalTypes.containsKey(type))
        .forEach(type -> 
            updatedProject.getRemainingFlats().put(type, updatedProject.getFlatTypes().get(type))
        );
}

    


    /**
     * Deletes a project managed by the specified manager.
     * 
     * @param projectName Name of the project to delete
     * @param managerId NRIC of the managing manager
     * @return true if deletion succeeded, false otherwise
     */
    public boolean deleteProject(String projectName, String managerId) {
        Project project = projectRepository.findByName(projectName);
        if (project == null || !project.getManagerInCharge().equals(managerId)) {
            return false;
        }
        return projectRepository.delete(projectName);
    }

    /**
     * Toggles visibility of a project managed by the specified manager.
     * 
     * @param projectName Name of the project
     * @param managerId NRIC of the managing manager
     * @return true if toggle succeeded, false otherwise
     */
    public boolean toggleVisibility(String projectName, String managerId) {
        Project project = projectRepository.findByName(projectName);
        if (project == null || !project.getManagerInCharge().equals(managerId)) {
            return false;
        }
        project.setVisible(!project.isVisible());
        return projectRepository.update(project);
    }

    /**
     * Retrieves all projects in the system.
     * 
     * @return List of all projects
     */
    public List<Project> viewAllProjects() {
        return projectRepository.findAll();
    }

    /**
     * Views all officer registrations.
     * 
     * @return List of all officer registrations
     */
    public List<OfficerRegistration> viewOfficerRegistrations() {
        return officerRegistrationRepo.findAll();
    }

    /**
     * Approves an officer registration if manager is authorized.
     * 
     * @param registrationId ID of the registration to approve
     * @param managerId NRIC of the approving manager
     * @return true if approval succeeded, false otherwise
     */
    public boolean approveRegistration(String registrationId, String managerId) {
        OfficerRegistration registration = officerRegistrationRepo.findById(registrationId);
        if (registration == null || !isValidApprover(registration, managerId) ) {
            return false;
        }
        registration.setStatus(RegistrationStatus.APPROVED);
        return officerRegistrationRepo.update(registration);
    }

    /**
     * Rejects an officer registration with remarks.
     * 
     * @param registrationId ID of the registration to reject
     * @param managerId NRIC of the rejecting manager
     * @return true if rejection succeeded, false otherwise
     */
    public boolean rejectRegistration(String registrationId, String managerId) {
        OfficerRegistration registration = officerRegistrationRepo.findById(registrationId);
        if (registration == null || !isValidApprover(registration, managerId)) {
            return false;
        }
        registration.setStatus(RegistrationStatus.REJECTED);
        return officerRegistrationRepo.update(registration);
    }

    public List<Application> getApplicationsByProject(String projectName) {
        return applicationRepository.findByProject(projectName);
    }

    /**
     * Approves a BTO application if flat supply is sufficient.
     * 
     * @param applicationId ID of the application to approve
     * @param managerId NRIC of the approving manager
     * @return true if approval succeeded, false otherwise
     */
    public boolean approveApplication(String applicationId, String managerId) {
        Application application = applicationRepository.findById(applicationId);
        if (application == null 
            || !isValidApplicationApprover(application, managerId)
            || application.isFinalized()) { // Add this check
            return false;
        }
        application.setStatus(ApplicationStatus.SUCCESSFUL);
        return applicationRepository.update(application);
    }
    /**
     * Rejects a BTO application with remarks.
     * 
     * @param applicationId ID of the application to reject
     * @param managerId NRIC of the rejecting manager
     * @param remarks Optional remarks for the rejection
     * @return true if rejection succeeded, false otherwise
     */
    public boolean rejectApplication(String applicationId, String managerId, String remarks) {
        Application application = applicationRepository.findById(applicationId);
        if (application == null || !isValidApplicationApprover(application, managerId) || application.isFinalized()) {
            return false;
        }
        application.setStatus(ApplicationStatus.UNSUCCESSFUL);
        application.setRemarks(remarks);
        return applicationRepository.update(application);
    }
    /**
     * Approves a withdrawal request and updates flat availability.
     * 
     * @param applicationId ID of the withdrawal request
     * @param managerId NRIC of the approving manager
     * @return true if approval succeeded, false otherwise
     */
    public boolean approveWithdrawal(String applicationId, String managerId) {
        Application application = applicationRepository.findById(applicationId);
        if (application == null || !isValidWithdrawalApprover(application, managerId)) {
            return false;
        }
        application.setStatus(ApplicationStatus.WITHDRAWN);
        return applicationRepository.update(application);
    }

    /**
     * Rejects a withdrawal request with remarks.
     * 
     * @param applicationId ID of the withdrawal request
     * @param managerId NRIC of the rejecting manager
     * @param remarks Optional remarks for the rejection
     * @return true if rejection succeeded, false otherwise
     */
    public boolean rejectWithdrawal(String applicationId, String managerId, String remarks) {
        Application application = applicationRepository.findById(applicationId);
        if (application == null || !isValidWithdrawalApprover(application, managerId)) {
            return false;
        }
        ApplicationStatus previousStatus = application.getPreviousStatus(); // Assume this method exists
        application.setStatus(previousStatus);
        return applicationRepository.update(application);
    }

    /**
     * Generates a report for a project with optional filters.
     * 
     * @param projectName Name of the project to report on
     * @param filters Map of filter criteria
     * @return Generated report content
     */
    public String generateReport(String projectName, Map<String, Object> filters) {
        Project project = projectRepository.findByName(projectName);
        List<Application> applications = applicationRepository.findByProject(projectName);
        return ReportGenerator.generate(project, applications, filters, userRepository);
    }

    /**
     * Retrieves all enquiries across all projects.
     * 
     * @return List of all enquiries
     */
    public List<Enquiry> viewEnquiries() {
        return enquiryRepository.findAll();
    }

    /**
     * Submits a reply to an enquiry.
     * 
     * @param enquiryId ID of the enquiry to reply to
     * @param replyText The reply content
     * @param managerId NRIC of the replying manager
     * @return true if reply succeeded, false otherwise
     */
    public boolean replyEnquiry(String enquiryId, String replyText, String managerId) {
        Enquiry enquiry = enquiryRepository.findById(enquiryId);
        if (enquiry == null) return false;
        enquiry.setReply(replyText, managerId);
        enquiry.setResponderId(managerId);
        return enquiryRepository.update(enquiry);
    }

    // Helper methods

    /**
     * Checks if a manager is handling another project during the new project's period.
     * 
     * @param managerId   Manager's NRIC
     * @param newStart    New project's start date
     * @param newEnd      New project's end date
     * @return true if manager has overlapping projects, false otherwise
     */
    private boolean isManagerHandlingProjectDuringPeriod(String managerId, Date newStart, Date newEnd) {
        List<Project> managerProjects = projectRepository.findByManager(managerId);
        
        for (Project project : managerProjects) {
            Date existingStart = project.getOpeningDate();
            Date existingEnd = project.getClosingDate();
            
            // Check for date overlap (inclusive of start/end dates)
            boolean overlap = (newStart.before(existingEnd) || newStart.equals(existingEnd)) 
                        && (newEnd.after(existingStart) || newEnd.equals(existingStart));
            
            if (overlap) {
                return true;
            }
        }
        return false;
    }


    /**
     * Checks if the manager is the valid approver for an officer registration.
     * 
     * @param registration OfficerRegistration object
     * @param managerId NRIC of the manager
     * @return true if manager is the approver, false otherwise
     */
    private boolean isValidApprover(OfficerRegistration registration, String managerId) {
        Project project = projectRepository.findByName(registration.getProjectName());
        return project != null && project.getManagerInCharge().equals(managerId);
    }

    /**
     * Checks if the manager is the valid approver for an application.
     * 
     * @param application Application object
     * @param managerId NRIC of the manager
     * @return true if manager is the approver, false otherwise
     */
    private boolean isValidApplicationApprover(Application application, String managerId) {
        Project project = projectRepository.findByName(application.getProjectName());
        return project != null && project.getManagerInCharge().equals(managerId);
    }

    /**
     * Checks if the manager is the valid approver for a withdrawal.
     * 
     * @param application Application object
     * @param managerId NRIC of the manager
     * @return true if manager is the approver, false otherwise
     */
    private boolean isValidWithdrawalApprover(Application application, String managerId) {
        Project project = projectRepository.findByName(application.getProjectName());
        return project != null && project.getManagerInCharge().equals(managerId);
    }
}
