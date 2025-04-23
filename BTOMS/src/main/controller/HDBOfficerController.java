package main.controller;

import main.model.project.Project;
import main.model.user.HDBOfficer;
import main.model.application.Application;
import main.model.application.ApplicationStatus;
import main.repository.*;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HDBOfficerController {
    private final ApplicationRepository applicationRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;


    public HDBOfficerController(ProjectRepository projectRepository,
                               ApplicationRepository applicationRepository,
                               UserRepository userRepository
                               ) {
        this.projectRepository = projectRepository;
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
    }


    /**
     * Books a flat for a successful application and updates remaining units.
     * 
     * @param applicationId ID of the successful application
     * @param officerId NRIC of the handling officer
     * @param flatType The flat type being booked
     * @return true if booking succeeded, false otherwise
     */
    public boolean bookFlat(String applicationId, String officerId, String flatType) {
        Application application = applicationRepository.findById(applicationId);
        if (application == null || application.getStatus() != ApplicationStatus.SUCCESSFUL) {
            return false;
        }

        // Ensure application is in SUCCESSFUL state
        if (application.getStatus() != ApplicationStatus.SUCCESSFUL) {
            return false;
        }

        Project project = projectRepository.findByName(application.getProjectName());
        HDBOfficer officer = (HDBOfficer) userRepository.findById(officerId);
        
        // Validate officer assignment
        if (project == null || officer == null || 
            !officer.getHandlingProjects().contains(project.getProjectName())) {
            return false;
        }

        // Get current remaining count
        int remaining = project.getRemainingFlats().getOrDefault(flatType, 0);
        
        if (remaining <= 0) {
            return false;
        }


        // Update application
        application.setStatus(ApplicationStatus.BOOKED);
        application.setFlatType(flatType);
        
        // Update project inventory
        project.updateRemainingFlats(flatType, remaining - 1);

        // Persist changes
        return applicationRepository.update(application) && projectRepository.update(project);
    }

    /**
     * Generates a formatted receipt for a flat selection based on the application details.
     * <p>
     * The receipt includes the applicant's ID, application ID, project name, selected flat type,
     * and the current booking date and time.
     * </p>
     *
     * @param applicationId the unique identifier of the application
     * @param projectName   the name of the project for which the flat is booked
     * @param flatType      the type of flat selected by the applicant
     * @return a formatted string representing the flat selection receipt
     */
    public String generateFlatSelectionReceipt(String applicationId, String projectName, String flatType) {
    Application application = applicationRepository.findById(applicationId);
    
    return String.format(
        "Applicant: %s (%s)\nProject: %s\nFlat Type: %s\nBooked On: %s",
        application.getApplicantId(),
        application.getApplicationId(),
        projectName,
        flatType,
        new SimpleDateFormat("dd MMM yyyy HH:mm").format(new Date())
    );
}
}
