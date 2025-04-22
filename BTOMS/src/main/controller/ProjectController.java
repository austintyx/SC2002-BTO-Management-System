package main.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import main.model.project.Project;
import main.model.project.Neighborhood;
import main.model.user.HDBManager;
import main.model.user.HDBOfficer;
import main.model.user.User;
import main.model.application.Application;
import main.model.enquiry.Enquiry;
import main.model.application.ApplicationStatus;
import main.repository.ApplicationRepository;
import main.repository.EnquiryRepository;
import main.repository.ProjectRepository;
import main.repository.UserRepository;
import main.utils.ReportGenerator;
import main.utils.DateUtils;

/**
 * Controller for project-related operations in the BTO Management System.
 * Handles creation, editing, deletion, and filtering of projects.
 * Uses project name as the unique project identifier.
 * 
 * @author Your Name
 * @version 1.0
 * @since 2025-04-17
 */
public class ProjectController {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final EnquiryRepository enquiryRepository;

    /**
     * Constructs a ProjectController with all required repositories.
     * @param projectRepository Project repository
     * @param userRepository User repository
     * @param applicationRepository Application repository
     * @param applicantRepository Applicant repository
     * @param enquiryRepository Enquiry repository
     */
    public ProjectController(ProjectRepository projectRepository,
                            UserRepository userRepository,
                            ApplicationRepository applicationRepository,
                            EnquiryRepository enquiryRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
        this.enquiryRepository = enquiryRepository;
    }

    /**
     * Gets a project by its name.
     * @param projectName Name of the project
     * @return Project object or null if not found
     */
    public Project getProjectByName(String projectName) {
        return projectRepository.findByName(projectName);
    }

    /**
     * Gets all projects.
     * @return List of all projects
     */
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    /**
     * Gets all projects managed by a specific manager.
     * @param managerNRIC Manager's NRIC
     * @return List of projects managed by the manager
     */
    public List<Project> getProjectsByManager(String managerNRIC) {
        List<Project> allProjects = projectRepository.findAll();
        List<Project> managerProjects = new ArrayList<>();

        for (Project project : allProjects) {
            if (project.getManagerInCharge().equals(managerNRIC)) {
                managerProjects.add(project);
            }
        }
        return managerProjects;
    }


    /**
     * Retrieves projects that are both visible and currently open.
     * @return List of available projects
     */
    public List<Project> getVisibleProjects() {
        Date currentDate = new Date();
        return projectRepository.findAll().stream()
            .filter(project -> 
                project.isVisible() && 
                !currentDate.before(project.getOpeningDate()) && 
                !currentDate.after(project.getClosingDate())
            )
            .collect(Collectors.toList());
    }

    /**
 * Gets projects that an officer is handling (regardless of visibility) 
 * combined with all visible projects.
 * @param officerNRIC Officer's NRIC
 * @return Combined list of handling projects and visible projects
 */
public List<Project> getHandlingProjects(String officerNRIC) {
    Set<String> projectNames = new HashSet<>();
    List<Project> result = new ArrayList<>();

    // Get officer's handling projects
    User user = userRepository.findById(officerNRIC);
    if (user instanceof HDBOfficer) {
        HDBOfficer officer = (HDBOfficer) user;
        for (String projectName : officer.getHandlingProjects()) {
            Project project = projectRepository.findByName(projectName);
            if (project != null && projectNames.add(project.getProjectName())) {
                result.add(project);
            }
        }
    }

    // Add visible projects not already in the list
    getVisibleProjects().stream()
        .filter(p -> projectNames.add(p.getProjectName()))
        .forEach(result::add);

    return result;
}



    /**
     * Checks if a manager is handling another project during the specified period.
     * @param managerNRIC Manager's NRIC
     * @param startDate Start date
     * @param endDate End date
     * @return true if manager is handling another project in the period, false otherwise
     */
    private boolean isManagerHandlingProjectDuringPeriod(String managerNRIC,
                                                        Date startDate, Date endDate) {
        List<Project> projects = getProjectsByManager(managerNRIC);

        for (Project project : projects) {
            boolean startsBeforeEnd = startDate.before(project.getClosingDate());
            boolean endsAfterStart = endDate.after(project.getOpeningDate());

            if (startsBeforeEnd && endsAfterStart) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets projects filtered by various criteria.
     * @param filters Map of filter criteria (e.g., neighborhood, flatType, status)
     * @return List of filtered projects
     */
    public List<Project> getProjectsByFilter(Map<String, Object> filters) {
        List<Project> allProjects = projectRepository.findAll();
        List<Project> filteredProjects = new ArrayList<>();

        for (Project project : allProjects) {
            boolean match = true;

            for (Map.Entry<String, Object> filter : filters.entrySet()) {
                String key = filter.getKey();
                Object value = filter.getValue();

                switch (key) {
                    case "neighborhood":
                        if (!project.getNeighborhood().toString().equalsIgnoreCase((String) value)) {
                            match = false;
                        }
                        break;
                    case "flatType":
                        if (!project.getFlatTypes().containsKey((String) value)) {
                            match = false;
                        }
                        break;
                    case "status":
                        boolean isOpen = DateUtils.isDateBetween(new Date(),
                            project.getOpeningDate(), project.getClosingDate());
                        if ("open".equalsIgnoreCase((String) value) && !isOpen) match = false;
                        if ("closed".equalsIgnoreCase((String) value) && isOpen) match = false;
                        break;
                }

                if (!match) break;
            }

            if (match) {
                filteredProjects.add(project);
            }
        }

        return filteredProjects;
    }

    /**
     * Generates a formatted report of applicants for a given project, with filters.
     * @param projectName The name of the project.
     * @param filters Map of filters to apply (e.g., "maritalStatus", "flatType").
     * @return The formatted report as a String.
     */
    public String generateProjectReport(String projectName, Map<String, Object> filters) {
        // Get the actual Project object
        Project project = projectRepository.findByName(projectName);
        if (project == null) {
            return "Error: Project not found";
        }
        
        List<Application> applications = applicationRepository.findByProjectAndStatus(
            projectName, 
            ApplicationStatus.BOOKED
        );
        
        return ReportGenerator.generate(
            project,      // Single Project object
            applications, 
            filters, 
            userRepository
        );
    }
    
    
}