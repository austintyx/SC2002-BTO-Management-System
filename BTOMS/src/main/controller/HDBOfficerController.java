package main.controller;

import main.model.project.Neighborhood;
import main.model.project.Project;
import main.model.registration.OfficerRegistration;
import main.model.registration.RegistrationStatus;
import main.model.user.HDBOfficer;
import main.model.application.Application;
import main.model.application.ApplicationStatus;
import main.model.enquiry.Enquiry;
import main.repository.*;
import main.utils.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    public String generateFlatSelectionReceipt(String applicationId, String projectName, String flatType) {
    Application application = applicationRepository.findById(applicationId);
    Project project = projectRepository.findByName(projectName);
    
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
