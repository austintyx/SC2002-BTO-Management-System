package main.model.user;

import main.model.project.Project;
import main.model.registration.RegistrationStatus;
import main.repository.ProjectRepository;
import main.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an HDB Officer user in the BTO Management System.
 * <p>
 * Extends Applicant capabilities with additional responsibilities for managing projects.
 * Can handle multiple projects with non-overlapping dates and process flat bookings.
 * </p>
 * 
 * @author Your Name
 * @version 2.0
 * @since 2025-04-17
 */
public class HDBOfficer extends Applicant {
    private static final long serialVersionUID = 1L;

    /** List of project names this officer is currently handling */
    private List<String> handlingProjects = new ArrayList<>();
    
    /** Registration status for project handling */
    private RegistrationStatus registrationStatus;

    /**
     * Constructs a new HDBOfficer with the given personal details.
     * 
     * @param nric Officer's NRIC
     * @param name Officer's name
     * @param password Officer's password
     * @param age Officer's age
     * @param maritalStatus Officer's marital status
     */
    public HDBOfficer(String nric, String name, String password, int age, MaritalStatus maritalStatus) {
        super(nric, name, password, age, maritalStatus);
        this.registrationStatus = null;
    }

    /**
     * Adds a project to the officer's handling list after date validation.
     * 
     * @param projectName Name of the project
     * @param projectRepository Project repository for date checks
     * @return true if added successfully, false if dates overlap
     */
    public boolean setHandlingProject(String projectName, ProjectRepository projectRepository) {
        Project newProject = projectRepository.findByName(projectName);
        if (newProject == null) return false;

        // Check date overlaps with existing projects
        for (String existingProjectName : handlingProjects) {
            Project existingProject = projectRepository.findByName(existingProjectName);
            if (existingProject != null && DateUtils.isDateRangeOverlapping(
                newProject.getOpeningDate(), 
                newProject.getClosingDate(),
                existingProject.getOpeningDate(), 
                existingProject.getClosingDate()
            )) {
                return false;
            }
        }
        
        handlingProjects.add(projectName);
        return true;
    }

    /**
     * Gets all projects this officer is handling.
     * @return List of project names (defensive copy)
     */
    public List<String> getHandlingProjects() {
        return new ArrayList<>(handlingProjects);
    }

    /**
     * Clears all project assignments and resets registration status.
     */
    public void clearProjectAssignment() {
        handlingProjects.clear();
        this.registrationStatus = null;
    }

    /**
     * Gets the registration status for project handling.
     * @return Current registration status
     */
    public RegistrationStatus getRegistrationStatus() {
        return registrationStatus;
    }

    /**
     * Sets the registration status for project handling.
     * @param status New registration status
     */
    public void setRegistrationStatus(RegistrationStatus status) {
        this.registrationStatus = status;
    }

    /**
     * Checks availability for a new project considering date overlaps.
     * 
     * @param newProject Project to check availability for
     * @param projectRepository Project repository access
     * @return true if available, false if conflicts exist
     */
    public boolean isAvailableForProject(Project newProject, ProjectRepository projectRepository) {
        return handlingProjects.stream()
            .map(projectRepository::findByName)
            .noneMatch(existingProject -> existingProject != null && 
                DateUtils.isDateRangeOverlapping(
                    newProject.getOpeningDate(), 
                    newProject.getClosingDate(),
                    existingProject.getOpeningDate(), 
                    existingProject.getClosingDate()
                ));
    }

    /**
     * Updates flat status for an applicant (officer-specific functionality).
     * 
     * @param applicantNRIC Applicant's NRIC
     * @param flatType Selected flat type
     */
    public void updateFlatStatus(String applicantNRIC, String flatType) {
        if (!handlingProjects.isEmpty() && registrationStatus == RegistrationStatus.APPROVED) {
            System.out.println("Updated flat status for " + applicantNRIC + " to " + flatType);
        } else {
            System.out.println("Not authorized to update flat status");
        }
    }
}
