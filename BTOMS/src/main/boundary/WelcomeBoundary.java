package main.boundary;

import main.controller.*;
import main.model.user.*;
import main.repository.*;
import main.utils.ConsoleUtils;

import java.util.Scanner;

/**
 * Boundary class that starts the BTO Management System.
 * Handles user login and routes to the appropriate user interface.
 * 
 * @author Your Name
 * @version 1.0
 * @since 2025-04-17
 */
public class WelcomeBoundary {
    private final Scanner scanner = new Scanner(System.in);

    // Repositories (assume these are initialized with data)
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ApplicationRepository applicationRepository;
    private final OfficerRegistrationRepository officerRegistrationRepository;
    private final EnquiryRepository enquiryRepository;
    
    // Controllers (assume these are initialized with the repositories)
    private final ProjectController projectController;
    private final ApplicationController applicationController;
    private final EnquiriesController enquiriesController;
    private final OfficerRegistrationController officerRegistrationController;
    private final HDBManagerController hdbManagerController;
    private final HDBOfficerController hdbOfficerController;

    /**
     * Constructs the WelcomeBoundary with all required repositories and controllers.
     */
    public WelcomeBoundary(UserRepository userRepository,
                           ProjectRepository projectRepository,
                           ApplicationRepository applicationRepository,
                           OfficerRegistrationRepository officerRegistrationRepository,
                           EnquiryRepository enquiryRepository,
                           ProjectController projectController,
                           ApplicationController applicationController,
                           EnquiriesController enquiriesController,
                           OfficerRegistrationController officerRegistrationController,
                           HDBManagerController hdbManagerController,
                           HDBOfficerController hdbOfficerController) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.applicationRepository = applicationRepository;
        this.officerRegistrationRepository = officerRegistrationRepository;
        this.enquiryRepository = enquiryRepository;
        this.projectController = projectController;
        this.applicationController = applicationController;
        this.enquiriesController = enquiriesController;
        this.officerRegistrationController = officerRegistrationController;
        this.hdbManagerController = hdbManagerController;
        this.hdbOfficerController = hdbOfficerController;
    }

    /**
     * Starts the BTO Management System.
     */
    public void start() {
        System.out.println("==========================================");
        System.out.println(" Welcome to the BTO Management System!");
        System.out.println("==========================================");

        while (true) {
            System.out.println("\nPlease login to continue.");
            String nric = ConsoleUtils.readNonEmptyString("Enter NRIC: ");
            String password = ConsoleUtils.readPasswordMasked("Enter Password: ");

            User user = userRepository.findById(nric);
            if (user == null) {
                System.out.println("User not found. Please try again.");
                continue;
            }
            if (!user.getPassword().equals(password)) {
                System.out.println("Incorrect password. Please try again.");
                continue;
            }

            // Successful login
            System.out.println("Login successful! Welcome, " + user.getName() + ".");

            // Route to correct UI based on user type
            if (user instanceof HDBManager manager) {
                HDBManagerUI managerUI = new HDBManagerUI(
                        manager, hdbManagerController, projectController, applicationController, enquiriesController, officerRegistrationController, userRepository, applicationRepository);
                managerUI.managerUI();
            } else if (user instanceof HDBOfficer officer) {
                HDBOfficerUI officerUI = new HDBOfficerUI(
                        officer, projectController, applicationController, enquiriesController, officerRegistrationController, hdbOfficerController, userRepository);
                officerUI.officerUI();
            } else if (user instanceof Applicant applicant) {
                ApplicantUI applicantUI = new ApplicantUI(
                        applicant, projectController, applicationController, enquiriesController, userRepository);
                applicantUI.applicantUI();
            } else {
                System.out.println("Unknown user type. Exiting.");
                break;
            }

            // After logout, ask if they want to login again
            if (!ConsoleUtils.confirmAction("\nWould you like to login as another user? (Y/N): ")) {
                System.out.println("Thank you for using the BTO Management System. Goodbye!");
                break;
            }
        }
    }
}
