package main.boundary;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import main.controller.*;
import main.model.enquiry.Enquiry;
import main.model.project.Neighborhood;
import main.model.project.Project;
import main.model.registration.OfficerRegistration;
import main.model.user.HDBManager;
import main.model.user.User;
import main.repository.ApplicationRepository;
import main.repository.UserRepository;
import main.model.application.*;
import main.utils.Password.ConsolePasswordUI;
import main.utils.Password.PasswordChanger;
import main.utils.Password.PasswordService;
import main.utils.Password.PasswordUI;
import main.utils.Password.RegexPasswordPolicy;
import main.utils.ConsoleUtils;
import main.utils.DateUtils;
import main.utils.IDGenerator;

/**
 * Provides the user interface for HDB Managers to interact with the BTO Management System.
 * Uses project name as the unique project identifier.
 */
public class HDBManagerUI implements PasswordChanger{
    private final Scanner scanner;
    private final HDBManager currentManager;
    private final HDBManagerController hdbManagerController;
    private final ProjectController projectController;
    private final ApplicationController applicationController;
    private final EnquiriesController enquiriesController;
    private final OfficerRegistrationController officerRegistrationController;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final PasswordService passwordService;
    private final PasswordUI passwordUI;

    /**
     * Constructs an HDBManagerUI with required controllers and repositories.
     */
    public HDBManagerUI(HDBManager manager,
                        HDBManagerController hdbManagerController,
                        ProjectController projectController,
                        ApplicationController applicationController,
                        EnquiriesController enquiriesController,
                        OfficerRegistrationController officerRegistrationController,
                        UserRepository userRepository,
                        ApplicationRepository applicationRepository) {
        this.scanner = new Scanner(System.in);
        this.currentManager = manager;
        this.hdbManagerController = hdbManagerController;
        this.projectController = projectController;
        this.applicationController = applicationController;
        this.enquiriesController = enquiriesController;
        this.officerRegistrationController = officerRegistrationController;
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
        this.passwordService = new PasswordService(
            userRepository, 
            new RegexPasswordPolicy()
        );
        this.passwordUI = new ConsolePasswordUI();
    }

    /**
     * Displays the main menu and processes user choices.
     */
    public void managerUI() {
        int choice;
        do {
            System.out.println("\n===== HDB Manager Menu =====");
            System.out.println("1. Create New BTO Project");
            System.out.println("2. Edit Existing Project");
            System.out.println("3. Delete Project");
            System.out.println("4. Toggle Project Visibility");
            System.out.println("5. View All Projects");
            System.out.println("6. View My Projects");
            System.out.println("7. Manage HDB Officer Registrations");
            System.out.println("8. Manage BTO Applications");
            System.out.println("9. Manage Withdrawal Requests");
            System.out.println("10. Generate Reports");
            System.out.println("11. View Enquiries");
            System.out.println("12. Reply to Enquiries");
            System.out.println("13. Change Password");
            System.out.println("0. Logout");
            choice = ConsoleUtils.readIntWithValidation("Enter your choice: ", "Invalid choice", 0, 13);
            processMenuChoice(choice);
        } while (choice != 0);
    }

    /**
     * Processes the selected menu choice.
     * @param choice The menu option selected by the user.
     */
    private void processMenuChoice(int choice) {
        switch (choice) {
            case 1 -> createNewProject();
            case 2 -> editProject();
            case 3 -> deleteProject();
            case 4 -> toggleProjectVisibility();
            case 5 -> viewAllProjects();
            case 6 -> viewMyProjects();
            case 7 -> manageOfficerRegistrations();
            case 8 -> manageBTOApplications();
            case 9 -> manageWithdrawalRequests();
            case 10 -> generateReports();
            case 11 -> viewEnquiries();
            case 12 -> replyToEnquiries();
            case 13 -> changePassword(currentManager);
            case 0 -> System.out.println("Logging out...");
            default -> System.out.println("Invalid choice. Please try again.");
        }
    }

    /**
     * Allows the manager to create a new BTO project.
     */
    private void createNewProject() {
        System.out.println("\n===== Create New BTO Project =====");
        String projectName = ConsoleUtils.readNonEmptyString("\nEnter Project Name (or 0 to exit): ");
        if (projectName.equals("0")) return;

        Neighborhood neighborhoodEnum = null;
        while (neighborhoodEnum == null) {
            String neighborhoodInput = ConsoleUtils.readNonEmptyString("Enter Neighborhood: ");
            try {
                neighborhoodEnum = Neighborhood.fromString(neighborhoodInput);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid neighborhood.");
            }
        }
        Map<String, Integer> flatUnits = new HashMap<>();
        Map<String, Integer> flatPrices = new HashMap<>();
        collectFlatTypeDetails(flatUnits, flatPrices);

        // For opening date
        Date openingDate = updateDate("Opening", new Date());

        // For closing date
        Date closingDate;
        do {
            closingDate = updateDate("Closing", openingDate);
            if (closingDate.before(openingDate)) {
                System.out.println("Closing date must be after opening date!");
            }
        } while (closingDate.before(openingDate));


        int officerSlots = ConsoleUtils.readIntWithValidation(
            "Enter number of HDB Officer slots (1-10): ",
            "Invalid input. Enter between 1-10.", 1, 10
        );

        Project newProject = new Project(
            projectName,
            neighborhoodEnum,
            flatUnits,
            flatPrices,
            openingDate,
            closingDate,
            currentManager.getID(),
            currentManager.getName(),
            officerSlots
        );

        boolean success = hdbManagerController.createProject(newProject, currentManager.getID());
        System.out.println(success ? "Project created successfully!" : "Failed to create project.");
    }

    /**
     * Collects flat type details from user input.
     * @param flatUnits Map to store flat units
     * @param flatPrices Map to store flat prices
     */
    private void collectFlatTypeDetails(Map<String, Integer> flatUnits, Map<String, Integer> flatPrices) {
        System.out.println("\n===== 2-Room Flats =====");
        int units2 = ConsoleUtils.readIntWithValidation("Number of units: ", "Invalid number", 0, Integer.MAX_VALUE);
        int price2 = ConsoleUtils.readIntWithValidation("Price per unit (SGD): ", "Invalid price", 0, Integer.MAX_VALUE);
        flatUnits.put("2-Room", units2);
        flatPrices.put("2-Room", price2);

        System.out.println("\n===== 3-Room Flats =====");
        int units3 = ConsoleUtils.readIntWithValidation("Number of units: ", "Invalid number", 0, Integer.MAX_VALUE);
        int price3 = ConsoleUtils.readIntWithValidation("Price per unit (SGD): ", "Invalid price", 0, Integer.MAX_VALUE);
        flatUnits.put("3-Room", units3);
        flatPrices.put("3-Room", price3);
    }

    /**
     * Allows the manager to edit an existing project.
     */
    private void editProject() {
        List<Project> myProjects = projectController.getProjectsByManager(currentManager.getID());
        if (myProjects.isEmpty()) {
            System.out.println("You have no projects to edit.");
            return;
        }
        ProjectViewer.displayProjects(myProjects);
        String projectName = ConsoleUtils.readNonEmptyString("\nEnter Project Name to edit (or 0 to cancel): ");
        if (projectName.equals("0")) return;
        Project project = projectController.getProjectByName(projectName);
        if (project == null || !project.getManagerInCharge().equals(currentManager.getID())) {
            System.out.println("Invalid project selection.");
            return;
        }
        Map<String, Object> updates = collectProjectUpdates(project);
        boolean success = hdbManagerController.editProject(projectName, updates, currentManager.getID());
        System.out.println(success ? "\nProject updated successfully!" : "\nFailed to update project.");
    }

    /**
     * Collects project updates from user input.
     * @param existing The existing project
     * @return Map of updates
     */
    private Map<String, Object> collectProjectUpdates(Project existing) {
        Map<String, Object> updates = new HashMap<>();
    
        // Project Name
        System.out.print("New Project Name [" + existing.getProjectName() + "]: ");
        String name = scanner.nextLine().trim();
        if (!name.isEmpty()) updates.put("projectName", name);
    
        // Neighborhood
        Neighborhood neighborhood = inputNeighborhood(
            "New Neighborhood [" + existing.getNeighborhood() + "]: ",
            existing.getNeighborhood()
        );
        if (neighborhood != null) {
            updates.put("neighborhood", neighborhood);
        }
        
        // Flat Types and Prices
        System.out.println("\n=== Flat Type Updates ===");
        Map<String, Integer> newFlatTypes = new HashMap<>();
        Map<String, Integer> newFlatPrices = new HashMap<>();
        Map<String, Integer> newRemainingFlats = new HashMap<>();

        Map<String, Integer> existingFlatTypes = existing.getFlatTypes();
        Map<String, Integer> existingFlatPrices = existing.getFlatPrices();
        Map<String, Integer> existingRemainingFlats = existing.getRemainingFlats();

        for (String flatType : existingFlatTypes.keySet()) {
            int units = existingFlatTypes.get(flatType);
            Integer newUnits = inputInteger(
                "Units for " + flatType + " [" + units + "]: ",
                units
            );
            if (newUnits == null) {
                newUnits = units;
            }
            newFlatTypes.put(flatType, newUnits);

            // Update remaining flats: clamp to new total if needed
            int currentRemaining = existingRemainingFlats.getOrDefault(flatType, units);
            int adjustedRemaining = Math.min(currentRemaining, newUnits);
            newRemainingFlats.put(flatType, adjustedRemaining);

            int currentPrice = existingFlatPrices.get(flatType);
            Integer newPrice = inputInteger(
                "Price for " + flatType + " [" + currentPrice + "]: ",
                currentPrice
            );
            if (newPrice == null) {
                newPrice = currentPrice;
            }
            newFlatPrices.put(flatType, newPrice);
        }

        // Update the project
        if (!newFlatTypes.isEmpty()) updates.put("flatTypes", newFlatTypes);
        if (!newFlatPrices.isEmpty()) updates.put("flatPrices", newFlatPrices);
        if (!newRemainingFlats.isEmpty()) updates.put("remainingFlats", newRemainingFlats);


    
        // Dates
        System.out.println("\n=== Date Updates ===");
        Date newOpening = updateDate("Opening", existing.getOpeningDate());
        Date newClosing;

        do {
            newClosing = updateDate("Closing", existing.getClosingDate());
            if (newClosing.before(newOpening != null ? newOpening : existing.getOpeningDate())) {
                System.out.println("Closing date must be after opening date!");
            }
        } while (newClosing.before(newOpening != null ? newOpening : existing.getOpeningDate()));

        updates.put("openingDate", newOpening);
        updates.put("closingDate", newClosing);
    
        // Officer Slots
        // Officer Slots
        int currentOfficers = existing.getNumberOfOfficers();
        Integer newSlots;
        do {
            newSlots = inputInteger(
                "New Officer Slots [" + existing.getOfficerSlots() + "] (must be ≥ " + currentOfficers + "): ",
                existing.getOfficerSlots()
            );
            
            Integer proposedSlots = (newSlots != null) ? newSlots : existing.getOfficerSlots();
            
            if (proposedSlots >= currentOfficers) {
                break; // Valid input
            } else {
                System.out.println("Error: Cannot have fewer slots (" + proposedSlots + 
                                ") than existing officers (" + currentOfficers + ")");
                if (newSlots == null) {
                    // Force entry if existing slots are invalid
                    System.out.println("Current slots are invalid. You must enter a new value.");
                    newSlots = -1; // Reset to trigger re-prompt
                }
            }
        } while (true);

        if (newSlots != null) {
            updates.put("officerSlots", newSlots);
        }


    
        // Visibility
        // Visibility
        while (true) {
            System.out.print("Visible? (yes/no) [" + (existing.isVisible() ? "yes" : "no") + "]: ");
            String visibleInput = scanner.nextLine().trim().toLowerCase();
            if (visibleInput.isEmpty()) {
                // User pressed enter, keep current value (do not update)
                break;
            }
            if (visibleInput.equals("yes")) {
                updates.put("visible", true);
                break;
            } else if (visibleInput.equals("no")) {
                updates.put("visible", false);
                break;
            } else {
                System.out.println("Invalid input. Please enter 'yes' or 'no'.");
            }
        }

    
        return updates;
    }

    /**
     * Prompts the user to update a date value with input validation.
     * <p>
     * The user is shown the current date and can enter a new date in the format "dd/MM/yyyy".
     * If the user enters an empty string, the original date is kept.
     * The method validates the input format and, for "Closing" dates, ensures the new date is not in the past.
     * </p>
     *
     * @param dateType     the type of date being updated (e.g., "Opening", "Closing")
     * @param currentDate  the current date value to display and use as default
     * @return the updated date if provided and valid; otherwise, returns the original currentDate
     */
    private Date updateDate(String dateType, Date currentDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);
    
        while (true) {
            System.out.print(dateType + " Date (dd/MM/yyyy) [" + dateFormat.format(currentDate) + "]: ");
            String dateStr = scanner.nextLine().trim();
    
            // Allow keeping current date
            if (dateStr.isEmpty()) {
                return currentDate; // Return the original date, not null
            }
    
            // Regex check for basic format validation
            if (!dateStr.matches("^\\d{2}/\\d{2}/\\d{4}$")) {
                System.out.println("Invalid format! Please use dd/MM/yyyy.");
                continue;
            }
    
            try {
                Date parsedDate = dateFormat.parse(dateStr);
    
                // Business logic checks
                if (dateType.equals("Closing") && parsedDate.before(new Date())) {
                    System.out.println("Closing date cannot be in the past!");
                    continue;
                }
    
                return parsedDate;
            } catch (ParseException e) {
                System.out.println("Invalid date! Please enter a valid date in dd/MM/yyyy format.");
            }
        }
    }
    
    /**
     * Prompts the user to input an integer value with input validation.
     * <p>
     * The user is shown a prompt and can enter a new integer value.
     * If the user enters an empty string, {@code null} is returned to indicate the current value should be kept.
     * The method validates the input and displays an error message for invalid numbers.
     * </p>
     *
     * @param prompt        the prompt message to display to the user
     * @param currentValue  the current integer value to use as default if input is empty
     * @return the entered integer value, or {@code null} if the user input is empty
     */
    private Integer inputInteger(String prompt, int currentValue) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            
            // Allow empty input to keep current value
            if (input.isEmpty()) {
                return null;
            }
            
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format! Please enter a valid integer.");
            }
        }
    }
    
    /**
     * Prompts user for Neighborhood input until valid value is provided.
     * @param prompt The input prompt
     * @param current Current neighborhood value
     * @return New neighborhood or null if empty input
     */
    private Neighborhood inputNeighborhood(String prompt, Neighborhood current) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            
            // Allow empty input to keep current value
            if (input.isEmpty()) {
                return null;
            }
            
            // Case-insensitive match
            for (Neighborhood n : Neighborhood.values()) {
                if (n.toString().equalsIgnoreCase(input)) {
                    return n;
                }
            }
            
            System.out.println("Invalid neighborhood. Valid options: " 
                + Arrays.toString(Neighborhood.values()));
        }
    }



    /**
     * Allows the manager to delete a project.
     */
    private void deleteProject() {
        List<Project> myProjects = projectController.getProjectsByManager(currentManager.getID());
        if (myProjects.isEmpty()) {
            System.out.println("You have no projects to delete.");
            return;
        }
        ProjectViewer.displayProjects(myProjects);
        String projectName = ConsoleUtils.readNonEmptyString("\nEnter Project Name to delete (or 0 to exit): ");
        if (projectName.equals("0")) return;
        if (ConsoleUtils.confirmAction("Confirm deletion (Y/N)? ")) {
            boolean success = hdbManagerController.deleteProject(projectName, currentManager.getID());
            System.out.println(success ? "Project deleted successfully." : "Deletion failed.");
        }
    }

    /**
     * Allows the manager to toggle project visibility.
     */
    private void toggleProjectVisibility() {
        List<Project> myProjects = projectController.getProjectsByManager(currentManager.getID());
        ProjectViewer.displayProjectsWithVisibility(myProjects);
        String projectName = ConsoleUtils.readNonEmptyString("Enter Project Name (or 0 to exit): ");
        if (projectName.equals("0")) return;
        boolean success = hdbManagerController.toggleVisibility(projectName, currentManager.getID());
        System.out.println(success ? "Visibility toggled!" : "Failed: Not authorized or invalid project.");
    }

    /**
     * Displays all projects in the system.
     */
    private void viewAllProjects() {
        List<Project> allProjects = projectController.getAllProjects();
        ProjectViewer.displayProjectsWithVisibility(allProjects);
        String projectName = ConsoleUtils.readOptionalInput("\nEnter project name to view details (or 0 to return): ");
        if (!projectName.equals("0") && !projectName.isEmpty()) {
            Project project = projectController.getProjectByName(projectName);
            if (project != null) {
                ProjectViewer.displayProjectDetails(project, true);
                ConsoleUtils.pressEnterToContinue();
            } else {
                System.out.println("Project not found.");
            }
        }
    }

    /**
     * Displays all projects managed by the current manager.
     */
    private void viewMyProjects() {
        List<Project> myProjects = projectController.getProjectsByManager(currentManager.getID());
        ProjectViewer.displayProjectsWithVisibility(myProjects);
        String projectName = ConsoleUtils.readOptionalInput("\nEnter project name to view details (or 0 to return): ");
        if (!projectName.equals("0") && !projectName.isEmpty()) {
            Project project = projectController.getProjectByName(projectName);
            if (project != null) {
                ProjectViewer.displayProjectDetails(project, true);
                ConsoleUtils.pressEnterToContinue();
            } else {
                System.out.println("Project not found.");
            }
        }
    }

    /**
     * Allows the manager to manage officer registrations for a project.
     */
    private void manageOfficerRegistrations() {
        List<Project> myProjects = projectController.getProjectsByManager(currentManager.getID());
        ProjectViewer.displayProjects(myProjects);
    
        // Allow user to exit at project selection
        String projectName = ConsoleUtils.readNonEmptyString("\nEnter Project Name (or 0 to exit): ");
        if (projectName.equals("0")) return;
    
        List<OfficerRegistration> registrations = officerRegistrationController.getPendingRegistrationsByProject(projectName);
        OfficerRegistrationViewer.displayRegistrations(registrations);
        if (registrations == null || registrations.isEmpty()) {
            System.out.println("No registrations available.");
            ConsoleUtils.pressEnterToContinue();
            return;
        }
    
        // Allow user to exit at registration selection
        String regId = ConsoleUtils.readNonEmptyString("Enter Registration ID (or 0 to exit): ");
        if (regId.equals("0")) return;
    
        System.out.println("1. Approve\n2. Reject\n0. Cancel");
        int choice = ConsoleUtils.readIntWithValidation("Choice: ", "Invalid choice", 0, 2);
        if (choice == 0) return;
    
        if (choice == 1) {
            officerRegistrationController.approveRegistration(regId, currentManager.getID());
            System.out.println("Registration successfully approved!");
            ConsoleUtils.pressEnterToContinue();
        } else if (choice == 2) {
            String remarks = ConsoleUtils.readNonEmptyString("Enter rejection remarks (or 0 to cancel): ");
            if (remarks.equals("0")) return;
            officerRegistrationController.rejectRegistration(regId, currentManager.getID(), remarks);
        }
    }
    

    /**
     * Allows the manager to manage BTO applications for a project.
     */
    private void manageBTOApplications() {
        List<Project> myProjects = projectController.getProjectsByManager(currentManager.getID());
        ProjectViewer.displayProjects(myProjects);
    
        // Allow user to exit at project selection
        String projectName = ConsoleUtils.readNonEmptyString("\nEnter Project Name (or 0 to exit): ");
        if (projectName.equals("0")) return;
    
        List<Application> applications = hdbManagerController.getApplicationsByProject(projectName);
        ApplicationViewer.displayApplications(applications);
        if (applications.isEmpty()) {
            System.out.println("No applications found.");
            ConsoleUtils.pressEnterToContinue();
            return;
        }
    
        // Allow user to exit at application selection
        String appId = ConsoleUtils.readNonEmptyString("Enter Application ID (or 0 to exit): ");
        if (appId.equals("0")) return;
    
        System.out.println("1. Approve\n2. Reject\n0. Cancel");
        int choice = ConsoleUtils.readIntWithValidation("Choice: ", "Invalid choice", 0, 2);
        if (choice == 0) return;
    
        Application app = applicationRepository.findById(appId);
        if (app.isFinalized()) {
            System.out.println("Cannot modify finalized application!");
            return;
        }
    
        if (choice == 1) {
            hdbManagerController.approveApplication(appId, currentManager.getID());
            System.out.println("Project successfully approved!");
            ConsoleUtils.pressEnterToContinue();
        } else if (choice == 2) {
            hdbManagerController.rejectApplication(appId, currentManager.getID(), "Rejected by manager");
        }
    }
    
    
    /**
     * Allows the manager to manage withdrawal requests.
     */
    private void manageWithdrawalRequests() {
        List<Application> withdrawals = applicationController.getPendingWithdrawalRequests();
        if (withdrawals.isEmpty()) {
            System.out.println("\nNo pending withdrawal requests.");
            return; // Cut back to the menu
        }
        ApplicationViewer.displayApplicationsWithWithdrawal(withdrawals);
        String appId = ConsoleUtils.readNonEmptyString("Enter Application ID (or 0 to exit): ");
        if (appId.equals("0")) return;

        System.out.println("1. Approve\n2. Reject\n0. Cancel");
        int choice = ConsoleUtils.readIntWithValidation("Choice: ", "Invalid choice", 0, 2);
        if (choice == 0) return;
        if (choice == 1) {
            applicationController.approveWithdrawal(appId, currentManager.getID());
            System.out.println("\nWithdrawal approved!");
            ConsoleUtils.pressEnterToContinue();
        } else {
            applicationController.rejectWithdrawal(appId, currentManager.getID());
            System.out.println("\nWithdrawal rejected!");
            ConsoleUtils.pressEnterToContinue();
        }
    }

    /**
     * Allows the manager to generate reports for a project.
     */
    private void generateReports() {
        List<Project> myProjects = projectController.getProjectsByManager(currentManager.getID());
        ProjectViewer.displayProjects(myProjects);
        String projectName = ConsoleUtils.readNonEmptyString("\nEnter Project Name (or 0 to exit): ");
        if (projectName.equals("0")) return;
        Map<String, Object> filters = collectReportFilters();
        String report = projectController.generateProjectReport(projectName, filters);
        System.out.println("\n=== Report ===\n" + report);
        ConsoleUtils.pressEnterToContinue();
    }

    /**
     * Collects report filters from user input.
     * @return Map of filters
     */
    private Map<String, Object> collectReportFilters() {
        Map<String, Object> filters = new HashMap<>();
        String maritalStatus = ConsoleUtils.readOptionalInput("Filter by marital status (Married/Single): ");
        if (!maritalStatus.isEmpty()) {
            filters.put("maritalStatus", maritalStatus);
        }
        String flatType = ConsoleUtils.readOptionalInput("Filter by flat type (2-Room/3-Room): ");
        if (!flatType.isEmpty()) {
            filters.put("flatType", flatType);
        }
        return filters;
    }

    /**
     * Displays all enquiries in the system.
     */
    private boolean viewEnquiries() {
        List<Enquiry> allEnquiries = enquiriesController.getAllEnquiries();
        EnquiriesViewer.displayEnquiries(allEnquiries, userRepository);
        if (allEnquiries == null || allEnquiries.isEmpty()) {
            System.out.println("No enquiries available.");
            ConsoleUtils.pressEnterToContinue();
            return false;
        }
        return true;
    }

    /**
     * Allows the manager to reply to enquiries.
     */
    private void replyToEnquiries() {
        if (!viewEnquiries()) {
            // No enquiries to reply to, return to menu
            return;
        }
        String enquiryId = ConsoleUtils.readNonEmptyString("\nEnter Enquiry ID (or 0 to exit): ");
        if (enquiryId.equals("0")) return;
        String reply = ConsoleUtils.readNonEmptyString("Enter your reply (or 0 to return): ");
        if (reply.equals("0")) return;
        boolean success = enquiriesController.replyToEnquiry(enquiryId, currentManager.getID(), reply);
        System.out.println(success ? "Reply submitted." : "Failed to submit reply.");
        ConsoleUtils.pressEnterToContinue();
    }

    /**
     * Initiates the password change process for the specified user.
     * <p>
     * This method delegates the password change workflow to the default implementation
     * provided by the {@link PasswordChanger} interface, utilizing the configured
     * {@code passwordService} and {@code passwordUI} components for validation and user interaction.
     * </p>
     *
     * @param user the {@link User} whose password is to be changed
     */
    public void changePassword(User user) {
        PasswordChanger.super.changePassword(user, passwordService, passwordUI);
    }
}
