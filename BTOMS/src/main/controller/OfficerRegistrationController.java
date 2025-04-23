package main.controller;

import java.util.List;
import java.util.stream.Collectors;

import main.model.project.Project;
import main.model.registration.OfficerRegistration;
import main.model.registration.RegistrationStatus;
import main.model.user.HDBOfficer;
import main.model.user.User;
import main.model.application.Application;
import main.repository.OfficerRegistrationRepository;
import main.repository.ProjectRepository;
import main.repository.UserRepository;
import main.utils.DateUtils;
import main.repository.ApplicationRepository;

/**
 * Controller for managing HDB Officer registrations to projects.
 * Uses project name as the unique project identifier.
 * Handles registration, approval, rejection, and cancellation.
 * 
 * @author Your Name
 * @version 1.0
 * @since 2025-04-20
 */
public class OfficerRegistrationController {
    private OfficerRegistrationRepository officerRegistrationRepository;
    private ProjectRepository projectRepository;
    private UserRepository userRepository;
    private ApplicationRepository applicationRepository;
    
    /**
     * Constructs an OfficerRegistrationController with the required repositories.
     * @param officerRegistrationRepository The officer registration repository
     * @param projectRepository The project repository
     * @param userRepository The user repository
     * @param applicationRepository The application repository
     */
    public OfficerRegistrationController(OfficerRegistrationRepository officerRegistrationRepository, 
                                       ProjectRepository projectRepository,
                                       UserRepository userRepository,
                                       ApplicationRepository applicationRepository) {
        this.officerRegistrationRepository = officerRegistrationRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
    }
    
    /**
     * Registers an HDB Officer for a project.
     * 
     * @param officerNRIC The NRIC of the officer
     * @param projectName The name of the project
     * @return true if registration is successful, false otherwise
     */
    public boolean registerOfficerForProject(String officerNRIC, String projectName) {
        // Check if the officer exists
        User user = userRepository.findById(officerNRIC);
        if (user == null || !(user instanceof HDBOfficer)) {
            return false;
        }
        
        HDBOfficer officer = (HDBOfficer) user;
        
        
        // Check if the project exists
        Project project = projectRepository.findByName(projectName);
        if (project == null) {
            return false;
        }
        
        // Check if the officer has applied for this project as an applicant
        Application application = applicationRepository.findByApplicantAndProject(officerNRIC, projectName);
        if (application != null) {
            return false;
        }
        
        // Check if the officer is already registered for another project in the same period
        for (String existingProjectName : officer.getHandlingProjects()) {
            Project existingProject = projectRepository.findByName(existingProjectName);
            if (existingProject != null && DateUtils.isDateRangeOverlapping(
                    project.getOpeningDate(), project.getClosingDate(),
                    existingProject.getOpeningDate(), existingProject.getClosingDate())) {
                System.out.println("\n");
                System.out.println("Officer has overlapping project: " + existingProjectName);
                return false;
            }
        }
        
        // Check if there are available slots
        if (project.getRemainingOfficerSlots() <= 0) {
            return false;
        }


        
        // Create and save the registration
        String registrationId = generateRegistrationId();
        OfficerRegistration registration = new OfficerRegistration(registrationId, officerNRIC, projectName);
        registration.setStatus(RegistrationStatus.PENDING);
        
        // Update officer's status
        officer.setRegistrationStatus(RegistrationStatus.PENDING);
        
        // Save all changes
        return officerRegistrationRepository.save(registration) && 
               userRepository.update(officer);
    }
    
    /**
     * Generates a unique registration ID.
     * 
     * @return A unique registration ID
     */
    private String generateRegistrationId() {
        return "REG" + System.currentTimeMillis();
    }
    

    
    /**
     * Approves an officer registration.
     * 
     * @param registrationId The ID of the registration
     * @param managerNRIC The NRIC of the approving manager
     * @return true if approval is successful, false otherwise
     */
    public boolean approveRegistration(String registrationId, String managerNRIC) {
    OfficerRegistration registration = officerRegistrationRepository.findById(registrationId);
    if (registration == null || 
        registration.getStatus() != RegistrationStatus.PENDING) {
        return false;
    }

    Project project = projectRepository.findByName(registration.getProjectName());
    HDBOfficer officer = (HDBOfficer) userRepository.findById(registration.getOfficerNRIC());

    // Check for date overlaps using DateUtils
    for (String existingProjectName : officer.getHandlingProjects()) {
        Project existingProject = projectRepository.findByName(existingProjectName);
        if (existingProject != null && DateUtils.isDateRangeOverlapping(
            project.getOpeningDate(), 
            project.getClosingDate(),
            existingProject.getOpeningDate(), 
            existingProject.getClosingDate()
        )) {
            System.out.println("Officer has overlapping project: " + existingProjectName);
            return false;
        }
    }
    
        // Update officer's details
        officer.setHandlingProject(project.getProjectName(), projectRepository);
        officer.setRegistrationStatus(RegistrationStatus.APPROVED);
        userRepository.update(officer); // Save changes
    
        // Update project
        project.addOfficer(officer.getID(), officer.getName());
        projectRepository.update(project);
    
        // Update registration status
        registration.setStatus(RegistrationStatus.APPROVED);
        return officerRegistrationRepository.update(registration);
    }
    
    
    /**
     * Rejects an officer registration.
     * 
     * @param registrationId The ID of the registration
     * @param managerNRIC The NRIC of the rejecting manager
     * @param remarks Optional remarks for the rejection
     * @return true if rejection is successful, false otherwise
     */
    public boolean rejectRegistration(String registrationId, String managerNRIC, String remarks) {
        // Find the registration
        OfficerRegistration registration = officerRegistrationRepository.findById(registrationId);
        if (registration == null || !registration.isPending()) {
            return false;
        }
        
        // Find the project
        Project project = projectRepository.findByName(registration.getProjectName());
        if (project == null || !project.getManagerInCharge().equals(managerNRIC)) {
            return false;
        }
        
        // Find the officer
        User user = userRepository.findById(registration.getOfficerNRIC());
        if (user == null || !(user instanceof HDBOfficer)) {
            return false;
        }
        
        HDBOfficer officer = (HDBOfficer) user;
        
        // Update registration status
        registration.setStatus(RegistrationStatus.REJECTED);
        if (remarks != null && !remarks.isEmpty()) {
            registration.setRemarks(remarks);
        }
        // Update officer's record
        officer.setRegistrationStatus(RegistrationStatus.REJECTED);
        
        // Save all changes
        return officerRegistrationRepository.update(registration) && 
               userRepository.update(officer);
    }
    
    /**
     * Gets all registrations for a project.
     * 
     * @param projectName The name of the project
     * @return A list of registrations for the project
     */
    public List<OfficerRegistration> getRegistrationsByProject(String projectName) {
        return officerRegistrationRepository.findByProjectName(projectName);
    }
    
    /**
     * Gets pending registrations for a project.
     * 
     * @param projectName The name of the project
     * @return A list of pending registrations for the project
     */
    public List<OfficerRegistration> getPendingRegistrationsByProject(String projectName) {
        List<OfficerRegistration> projectRegistrations = officerRegistrationRepository.findByProjectName(projectName);
        return projectRegistrations.stream()
                .filter(OfficerRegistration::isPending)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets all registrations for an officer.
     * 
     * @param officerNRIC The NRIC of the officer
     * @return A list of registrations for the officer
     */
    public List<OfficerRegistration> getRegistrationsByOfficer(String officerNRIC) {
        return officerRegistrationRepository.findByOfficer(officerNRIC);
    }
    
    /**
     * Gets the registration status for an officer and project.
     * 
     * @param officerNRIC The NRIC of the officer
     * @param projectName The name of the project
     * @return The registration status, or null if not found
     */
    public String getRegistrationStatus(String officerNRIC, String projectName) {
        OfficerRegistration registration = officerRegistrationRepository.findByOfficerAndProject(officerNRIC, projectName);
        return registration != null ? registration.getStatus().toString() : null;
    }
    
    /**
     * Cancels a pending registration.
     * 
     * @param officerNRIC The NRIC of the officer
     * @param projectName The name of the project
     * @return true if cancellation is successful, false otherwise
     */
    public boolean cancelRegistration(String officerNRIC, String projectName) {
        // Find the registration
        OfficerRegistration registration = officerRegistrationRepository.findByOfficerAndProject(officerNRIC, projectName);
        if (registration == null || !registration.isPending()) {
            return false;
        }
        
        // Find the officer
        User user = userRepository.findById(officerNRIC);
        if (user == null || !(user instanceof HDBOfficer)) {
            return false;
        }
        
        HDBOfficer officer = (HDBOfficer) user;
        
        // Reset officer's registration status
        officer.setRegistrationStatus(null);
        
        // Delete the registration
        return officerRegistrationRepository.delete(registration.getRegistrationId()) && 
               userRepository.update(officer);
    }
}
