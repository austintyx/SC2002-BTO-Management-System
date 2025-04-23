package main.repository;

import main.model.project.Project;
import main.utils.Loader.ProjectLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Repository class for managing Project entities, using project name as the unique identifier.
 */
public class ProjectRepository extends FileBasedRepository<Project, String> {
    private static final String CSV_FILE = "ProjectList.csv";
    private static final String DATA_FILE = "data/projects.dat";
    private final UserRepository userRepository;

    /**
     * Creates a ProjectRepository with dependency on UserRepository for manager lookups.
     * @param userRepository Repository to resolve manager names to NRICs
     */
    public ProjectRepository(UserRepository userRepository) {
        super(DATA_FILE);
        this.userRepository = userRepository;
        ensureDataDirectoryExists();
    }

    /**
     * Ensures the data directory exists for file persistence.
     */
    private void ensureDataDirectoryExists() {
        File dataDir = new File(DATA_FILE).getParentFile();
        if (dataDir != null && !dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    /**
     * Finds a project by its unique name.
     * @param name The project name to search for
     * @return The Project object if found, otherwise null
     */
    @Override
    public Project findById(String name) {
        return entities.stream()
                .filter(project -> project.getProjectName().equals(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Saves a project to the repository if it doesn't already exist.
     * @param project The Project to save
     * @return true if saved successfully, false if project already exists
     */
    @Override
    public boolean save(Project project) {
        if (findById(project.getProjectName()) != null) {
            return false; // Project with this name already exists
        }
        entities.add(project);
        return saveToFile();
    }

    /**
     * Updates an existing project in the repository.
     * @param project The Project with updated details
     * @return true if updated successfully, false if project not found
     */
    @Override
    public boolean update(Project project) {
        for (int i = 0; i < entities.size(); i++) {
            if (entities.get(i).getProjectName().equals(project.getProjectName())) {
                entities.set(i, project);
                return saveToFile();
            }
        }
        return false;
    }

    /**
     * Deletes a project by its name.
     * @param name The name of the project to delete
     * @return true if deleted successfully, false if project not found
     */
    @Override
    public boolean delete(String name) {
        boolean removed = entities.removeIf(project -> project.getProjectName().equals(name));
        if (removed) {
            return saveToFile();
        }
        return false;
    }

    /**
     * Retrieves the unique identifier (project name) for a Project entity.
     * @param project The Project object
     * @return The project name
     */
    @Override
    protected String getEntityId(Project project) {
        return project.getProjectName();
    }

    // Additional project-specific methods
    
    /**
     * Finds all projects managed by a specific manager.
     * @param managerNRIC The NRIC of the manager
     * @return List of projects managed by the specified manager
     */
    public List<Project> findByManager(String managerNRIC) {
        return entities.stream()
                .filter(project -> project.getManagerInCharge().equals(managerNRIC))
                .collect(Collectors.toList());
    }

    /**
     * Finds a project by its unique project name.
     *
     * @param projectName The unique name of the project to search for.
     * @return The Project object if found, otherwise null.
     */
    public Project findByName(String projectName) {
        if (projectName == null) return null;
        return entities.stream()
                .filter(project -> project.getProjectName().equals(projectName))
                .findFirst()
                .orElse(null);
    }


    /**
     * Retrieves all visible projects.
     * @return List of projects where visibility is enabled
     */
    public List<Project> findVisibleProjects() {
        return entities.stream()
                .filter(Project::isVisible)
                .collect(Collectors.toList());
    }

    /**
     * Filters projects based on specified criteria.
     * @param filters Map of filter criteria (e.g., neighborhood, flatType)
     * @return List of projects matching the filters
     */
    public List<Project> findByFilter(Map<String, Object> filters) {
        List<Project> filteredProjects = new ArrayList<>(entities);
        
        if (filters.containsKey("neighborhood")) {
            String neighborhoodFilter = (String) filters.get("neighborhood");
            filteredProjects = filteredProjects.stream()
                    .filter(p -> p.getNeighborhood().toString().equalsIgnoreCase(neighborhoodFilter))
                    .collect(Collectors.toList());
        }
        
        if (filters.containsKey("flatType")) {
            String flatType = (String) filters.get("flatType");
            filteredProjects = filteredProjects.stream()
                    .filter(p -> p.getFlatTypes().containsKey(flatType))
                    .collect(Collectors.toList());
        }
        
        return filteredProjects;
    }
}
