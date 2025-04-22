package main;

import java.io.IOException;
import java.util.List;
import main.boundary.WelcomeBoundary;
import main.controller.*;
import main.model.project.Project;
import main.model.user.HDBManager;
import main.model.user.HDBOfficer;
import main.model.user.Applicant;
import main.utils.ProjectLoader;
import main.utils.ManagerLoader;
import main.utils.OfficerLoader;
import main.utils.ApplicantLoader;
import main.repository.*;

/**
 * Main entry point for the BTO Management System application.
 * Initializes the system components and starts the application.
 */
public class Main {
    /**
     * Main method that initializes the system and starts the application.
     * 
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            // Initialize repositories
            UserRepository userRepository = new UserRepository();
            ProjectRepository projectRepository = new ProjectRepository(userRepository);
            ApplicationRepository applicationRepository = new ApplicationRepository();
            OfficerRegistrationRepository officerRegistrationRepository = new OfficerRegistrationRepository();
            EnquiryRepository enquiryRepository = new EnquiryRepository();

            // Load data from CSV files
            List<HDBManager> managers = ManagerLoader.loadFromCsv("ManagerList.csv");
            List<HDBOfficer> officers = OfficerLoader.loadFromCsv("OfficerList.csv");
            List<Applicant> applicants = ApplicantLoader.loadFromCsv("ApplicantList.csv");

            // Save users to repository
            managers.forEach(userRepository::save);
            officers.forEach(userRepository::save);
            applicants.forEach(userRepository::save);

            // Load and save projects using project names as unique identifiers
            if (projectRepository.findAll().isEmpty()) {
                List<Project> projects = ProjectLoader.loadFromCsv("ProjectList.csv", userRepository, projectRepository);
                projects.forEach(projectRepository::save);
            }

            // Initialize controllers with dependencies
            ProjectController projectController = new ProjectController(
                projectRepository, 
                userRepository,
                applicationRepository, 
                enquiryRepository
            );
            
            ApplicationController applicationController = new ApplicationController(
                applicationRepository,
                projectRepository,
                userRepository
            );
            
            EnquiriesController enquiriesController = new EnquiriesController(
                enquiryRepository,
                projectRepository,
                userRepository
            );
            
            OfficerRegistrationController officerRegistrationController = new OfficerRegistrationController(
                officerRegistrationRepository,
                projectRepository,
                userRepository,
                applicationRepository
            );
            
            HDBManagerController hdbManagerController = new HDBManagerController(
                projectRepository,
                officerRegistrationRepository,
                applicationRepository,
                enquiryRepository,
                userRepository
            );

            HDBOfficerController hdbOfficerController = new HDBOfficerController(
                projectRepository,
                applicationRepository,
                userRepository
            );

            PasswordController passwordController = new PasswordController(userRepository);

            // Initialize and start the UI
            WelcomeBoundary welcomeBoundary = new WelcomeBoundary(
                userRepository,
                projectRepository,
                applicationRepository,
                officerRegistrationRepository,
                enquiryRepository,
                projectController,
                applicationController,
                enquiriesController,
                officerRegistrationController,
                hdbManagerController,
                hdbOfficerController,
                passwordController
            );
            
            welcomeBoundary.start();
            
        } catch (IOException e) {
            System.err.println("Critical error: Failed to load initial data.");
            System.out.println("Classpath: " + System.getProperty("java.class.path"));
            e.printStackTrace();
            System.exit(1);
        }
    }
}
