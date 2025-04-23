package main.repository;

import main.model.registration.OfficerRegistration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository class for managing officer registrations to projects.
 * Uses project name as the unique project identifier.
 */
public class OfficerRegistrationRepository extends FileBasedRepository<OfficerRegistration, String> {
    
    /**
     * Initializes the repository with the specified data file.
     */
    public OfficerRegistrationRepository() {
        super("data/officer_registrations.dat");
    }
    
    /**
     * Finds a registration by its unique ID.
     * @param id The registration ID to search for
     * @return The OfficerRegistration object if found, otherwise null
     */
    @Override
    public OfficerRegistration findById(String id) {
        return entities.stream()
                .filter(reg -> reg.getRegistrationId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Saves a new officer registration.
     * @param registration The OfficerRegistration to save
     * @return true if saved successfully, false if registration ID exists
     */
    @Override
    public boolean save(OfficerRegistration registration) {
        if (findById(registration.getRegistrationId()) != null) {
            return false;
        }
        entities.add(registration);
        return saveToFile();
    }
    
    /**
     * Updates an existing officer registration.
     * @param registration The updated OfficerRegistration object
     * @return true if updated successfully, false if not found
     */
    @Override
    public boolean update(OfficerRegistration registration) {
        for (int i = 0; i < entities.size(); i++) {
            if (entities.get(i).getRegistrationId().equals(registration.getRegistrationId())) {
                entities.set(i, registration);
                return saveToFile();
            }
        }
        return false;
    }
    
    /**
     * Deletes a registration by ID.
     * @param id The registration ID to delete
     * @return true if deleted successfully, false if not found
     */
    @Override
    public boolean delete(String id) {
        boolean removed = entities.removeIf(reg -> reg.getRegistrationId().equals(id));
        if (removed) {
            return saveToFile();
        }
        return false;
    }
    
    /**
     * Gets the unique ID for an entity.
     * @param registration The OfficerRegistration object
     * @return The registration ID
     */
    @Override
    protected String getEntityId(OfficerRegistration registration) {
        return registration.getRegistrationId();
    }
    
    // Additional methods
    
    /**
     * Finds registration by officer NRIC and project name.
     * @param officerNRIC The officer's NRIC
     * @param projectName The project name
     * @return Matching OfficerRegistration or null
     */
    public OfficerRegistration findByOfficerAndProject(String officerNRIC, String projectName) {
        return entities.stream()
                .filter(reg -> reg.getOfficerNRIC().equals(officerNRIC) && 
                              reg.getProjectName().equals(projectName))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Finds all registrations for an officer.
     * @param officerNRIC The officer's NRIC
     * @return List of OfficerRegistrations for the officer
     */
    public List<OfficerRegistration> findByOfficer(String officerNRIC) {
        return entities.stream()
                .filter(reg -> reg.getOfficerNRIC().equals(officerNRIC))
                .collect(Collectors.toList());
    }
    
    /**
     * Finds all registrations for a project.
     * @param projectName The project name
     * @return List of OfficerRegistrations for the project
     */
    public List<OfficerRegistration> findByProjectName(String projectName) {
        return entities.stream()
                .filter(reg -> reg.getProjectName().equals(projectName))
                .collect(Collectors.toList());
    }
    
    /**
     * Finds registrations by status.
     * @param status The status to filter by (e.g., "Pending")
     * @return List of OfficerRegistrations with matching status
     */
    public List<OfficerRegistration> findByStatus(String status) {
        return entities.stream()
                .filter(reg -> reg.getStatus().equals(status))
                .collect(Collectors.toList());
    }
    
    /**
     * Retrieves all pending registrations.
     * @return List of pending OfficerRegistrations
     */
    public List<OfficerRegistration> findPendingRegistrations() {
        return findByStatus("Pending");
    }
}
