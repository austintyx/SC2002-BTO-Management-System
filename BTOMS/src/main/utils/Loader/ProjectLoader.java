package main.utils.Loader;

import main.model.project.Project;
import main.model.registration.RegistrationStatus;
import main.model.user.HDBOfficer;
import main.model.user.User;
import main.repository.ProjectRepository;
import main.repository.UserRepository;
import main.utils.CsvUtils;
import main.model.project.Neighborhood;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Utility class for loading projects from CSV files.
 */
public class ProjectLoader {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yy");

    /**
     * Loads projects from a CSV file, using project name as the unique identifier.
     * 
     * @param fileName      The path to the CSV file
     * @param userRepository Repository for manager NRIC lookups
     * @return List of parsed Project objects
     * @throws IOException If there's an error reading the CSV file
     */
    public static List<Project> loadFromCsv(String fileName, UserRepository userRepository, ProjectRepository projectRepository) throws IOException {
        List<Project> projects = new ArrayList<>();
        List<String[]> rows = CsvUtils.readCsv(fileName);
        
        // Skip header row
        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            try {
                Project project = parseProjectRow(row, userRepository, projectRepository);
                projects.add(project);
            } catch (Exception e) {
                System.err.println("Error parsing row " + (i + 1) + ": " + e.getMessage());
            }
        }
        return projects;
    }

    /**
     * Parses a single CSV row into a Project object.
     * 
     * @param row            CSV row data
     * @param userRepository Repository for manager lookups
     * @return Parsed Project object
     * @throws Exception If any parsing error occurs
     */
    private static Project parseProjectRow(String[] row, UserRepository userRepository, ProjectRepository projectRepository) throws Exception {
        String projectName = row[0];
        Neighborhood neighborhood = Neighborhood.fromString(row[1]);

        Map<String, Integer> flatTypes = new HashMap<>();
        Map<String, Integer> flatPrices = new HashMap<>();
        
        // First flat type (columns 2-4: type, units, price)
        String type1 = row[2];
        int units1 = Integer.parseInt(row[3]);
        int price1 = Integer.parseInt(row[4]);
        flatTypes.put(type1, units1);
        flatPrices.put(type1, price1);

        // Second flat type (columns 5-7: type, units, price)
        if (row.length > 7 && !row[5].isEmpty()) {
            String type2 = row[5];
            int units2 = Integer.parseInt(row[6]);
            int price2 = Integer.parseInt(row[7]);
            flatTypes.put(type2, units2);
            flatPrices.put(type2, price2);
        }

        // Parse dates (format: dd/MM/yy)
        Date openingDate = DATE_FORMAT.parse(row[8]);
        Date closingDate = DATE_FORMAT.parse(row[9]);

        String managerName = row[10];
        String managerNRIC = lookupManagerNRIC(userRepository, managerName);

        int officerSlots = Integer.parseInt(row[11]);
        

        Project project = new Project(
            projectName, 
            neighborhood,
            flatTypes,
            flatPrices,
            openingDate, 
            closingDate,
            managerNRIC,
            managerName,
            officerSlots
        );
        project.setVisible(false);

        // Save project FIRST before processing officers
    projectRepository.save(project);

    // Add officers (column 12)
    if (row.length > 12 && !row[12].isEmpty()) {
        Arrays.stream(row[12].split(","))
              .map(String::trim)
              .filter(s -> !s.isEmpty())
              .forEach(officerName -> {
                  try {
                      User user = userRepository.findByName(officerName);
                      if (user instanceof HDBOfficer officer) {
                          boolean success = officer.setHandlingProject(
                              project.getProjectName(), 
                              projectRepository
                          );
                          
                          if (success) {
                              // Add officer to project
                              project.addOfficer(officer.getID(), officer.getName());
                              
                              // Save updated officer state
                              userRepository.update(officer); // <-- Add this line
                          } else {
                              System.err.println("Skipping officer " + officerName + 
                                                " - date conflict with existing assignments");
                          }
                      }
                  } catch (Exception e) {
                      System.err.println("Error processing officer: " + officerName + " - " + e.getMessage());
                  }
              });
    }

    // Update project with officers
    projectRepository.update(project);
    return project;
    }

    /**
     * Looks up a manager's NRIC by name.
     * 
     * @param userRepository User repository for lookup
     * @param managerName    Name of the manager to find
     * @return Manager's NRIC
     * @throws IllegalArgumentException If manager not found
     */
    private static String lookupManagerNRIC(UserRepository userRepository, String managerName) {
        User manager = userRepository.findByName(managerName.trim());
        if (manager == null) {
            throw new IllegalArgumentException("Manager not found: " + managerName);
        }
        return manager.getID();
    }

    /**
 * Looks up an officer's NRIC by name.
 * 
 * @param userRepository User repository for lookup
 * @param officerName    Name of the officer to find
 * @return Officer's NRIC
 * @throws IllegalArgumentException If officer not found
 */
private static String lookupOfficerNRIC(UserRepository userRepository, String officerName) {
    User officer = userRepository.findByName(officerName.trim());
    if (officer == null) {
        System.err.println("Warning: Officer not found: " + officerName);
        return ""; // Return empty string or handle as appropriate
    }
    return officer.getID();
}

}
