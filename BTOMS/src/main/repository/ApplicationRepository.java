package main.repository;

import main.model.application.Application;
import main.model.application.ApplicationStatus;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository class for managing {@link Application} entities with file-based persistence.
 * Provides CRUD operations and application-specific queries.
 * Uses project name as the unique project identifier.
 * 
 * @author Your Name
 * @version 1.0
 * @since 2025-04-17
 */
public class ApplicationRepository extends FileBasedRepository<Application, String> {

    /**
     * Constructs an ApplicationRepository with the default data file path.
     */
    public ApplicationRepository() {
        super("data/applications.dat");
    }

    /**
     * Finds an application by its unique ID.
     * @param id The application ID.
     * @return The Application with the given ID, or {@code null} if not found.
     */
    @Override
    public Application findById(String id) {
        return entities.stream()
                .filter(app -> app.getApplicationId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Saves a new application to the repository.
     * @param application The application to save.
     * @return {@code true} if the application was saved successfully, {@code false} if an application with the same ID already exists.
     */
    @Override
    public boolean save(Application application) {
        if (findById(application.getApplicationId()) != null) {
            return false; // Application with this ID already exists
        }
        entities.add(application);
        return saveToFile();
    }

    /**
     * Updates an existing application in the repository.
     * @param application The application with updated information.
     * @return {@code true} if the update was successful, {@code false} if the application was not found.
     */
    @Override
    public boolean update(Application application) {
        entities.removeIf(a -> a.getApplicationId().equals(application.getApplicationId()));
        boolean added = entities.add(application);
        if (added) {
            return saveToFile(); // <-- persist changes after update
        }
        return false;
    }

    /**
     * Deletes an application from the repository by its unique ID.
     * @param id The application ID.
     * @return {@code true} if the application was deleted successfully, {@code false} otherwise.
     */
    @Override
    public boolean delete(String id) {
        boolean removed = entities.removeIf(app -> app.getApplicationId().equals(id));
        if (removed) {
            return saveToFile();
        }
        return false;
    }

    /**
     * Returns the unique identifier for the given application.
     * @param application The application entity.
     * @return The application ID.
     */
    @Override
    protected String getEntityId(Application application) {
        return application.getApplicationId();
    }

    /**
     * Finds an application by applicant NRIC and project name.
     * @param applicantNRIC The NRIC of the applicant.
     * @param projectName The name of the project.
     * @return The Application if found, or {@code null} otherwise.
     */
    public Application findByApplicantAndProject(String applicantNRIC, String projectName) {
        return entities.stream()
            .filter(app -> app.getApplicantId().equals(applicantNRIC) && 
                          app.getProjectName().equals(projectName))
            .reduce((first, second) -> second)  // Keep the last element
            .orElse(null);
    }
    

    /**
     * Finds all applications submitted by a specific applicant.
     * @param applicantNRIC The NRIC of the applicant.
     * @return List of Applications submitted by the applicant.
     */
    public List<Application> findByApplicant(String applicantNRIC) {
        return entities.stream()
                .filter(app -> app.getApplicantId().equals(applicantNRIC))
                .collect(Collectors.toList());
    }

    /**
     * Finds all applications for a specific project by project name.
     * @param projectName The name of the project.
     * @return List of Applications for the project.
     */
    public List<Application> findByProject(String projectName) {
        return entities.stream()
                .filter(app -> app.getProjectName().equals(projectName))
                .collect(Collectors.toList());
    }

    /**
     * Finds all applications with a specific status.
     * @param status The application status to filter by.
     * @return List of Applications with the given status.
     */
    public List<Application> findByStatus(ApplicationStatus status) {
        return entities.stream()
                .filter(app -> app.getStatus() == status)
                .collect(Collectors.toList());
    }

    /**
     * Finds applications by withdrawal status.
     * @param status The withdrawal status to filter by
     * @return List of applications with the specified status
     */
    public List<Application> findByWithdrawalStatus(ApplicationStatus status) {
        return entities.stream()
                .filter(app -> app.getStatus() == status.PENDING_WITHDRAWAL)
                .collect(Collectors.toList());
    }

    public boolean batchUpdate(List<Application> applications) {
        try {
            applications.forEach(app -> {
                entities.removeIf(a -> a.getApplicationId().equals(app.getApplicationId()));
                entities.add(app);
            });
            return saveToFile(); // <-- persist batch updates
        } catch (Exception e) {
            return false;
        }
    }

     /**
     * Finds applications by project name and status.
     * @param projectName The name of the project
     * @param status The application status to filter by
     * @return List of matching applications
     */
    public List<Application> findByProjectAndStatus(String projectName, ApplicationStatus status) {
        return entities.stream()
            .filter(app -> 
                app.getProjectName().equals(projectName) && 
                app.getStatus() == status
            )
            .collect(Collectors.toList());
    }
    
    
    
}
