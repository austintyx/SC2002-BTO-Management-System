package main.repository;

import main.model.user.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository class for managing {@link User} entities.
 * <p>
 * Provides CRUD operations and various search methods for users,
 * backed by a file-based persistence mechanism.
 * </p>
 * <p>
 * Data is stored in a file specified by {@code DATA_FILE}. Users are uniquely identified by their NRIC.
 * </p>
 */
public class UserRepository extends FileBasedRepository<User, String> {
    private static final String CSV_FILE = "UserList.csv";
    private static final String DATA_FILE = "data/users.dat";

    /**
     * Constructs a new {@code UserRepository} with file-based storage.
     */
    public UserRepository() {
        super(DATA_FILE);
    }

    /**
     * Adds a user entity to the repository without performing duplicate or validation checks.
     *
     * @param user the {@link User} object to add
     */
    private void saveWithoutCheck(User user) {
        entities.add(user);
    }

    /**
     * Finds and returns a user by their unique identifier (NRIC).
     *
     * @param id the NRIC or unique identifier of the user
     * @return the {@link User} with the specified ID, or {@code null} if not found
     */
    @Override
    public User findById(String id) {
        return entities.stream()
            .filter(user -> {
                String userId = user.getID();
                return userId != null && userId.equals(id); // Null-safe check
            })
            .findFirst()
            .orElse(null);
    }

    /**
     * Saves a new user to the repository if a user with the same NRIC does not already exist.
     *
     * @param user the {@link User} to save
     * @return {@code true} if the user was saved successfully, {@code false} if a duplicate exists
     */
    @Override
    public boolean save(User user) {
        if (findById(user.getID()) != null) {
            return false; // User with this NRIC already exists
        }
        entities.add(user);
        return saveToFile();
    }

    /**
     * Updates an existing user in the repository by replacing the user with the same NRIC.
     *
     * @param user the {@link User} with updated information
     * @return {@code true} if the update was successful, {@code false} if the user was not found
     */
    @Override
    public boolean update(User user) {
        for (int i = 0; i < entities.size(); i++) {
            if (entities.get(i).getID().equals(user.getID())) {
                entities.set(i, user);
                return saveToFile();
            }
        }
        return false; // User not found
    }

    /**
     * Deletes a user from the repository by their NRIC.
     *
     * @param nric the NRIC of the user to delete
     * @return {@code true} if the user was deleted and the repository was saved, {@code false} otherwise
     */
    @Override
    public boolean delete(String nric) {
        boolean removed = entities.removeIf(user -> user.getID().equals(nric));
        if (removed) {
            return saveToFile();
        }
        return false;
    }

    /**
     * Retrieves the unique identifier (NRIC) for the given user entity.
     *
     * @param user the {@link User} entity
     * @return the NRIC of the user
     */
    @Override
    protected String getEntityId(User user) {
        return user.getID();
    }

    /**
     * Finds a user by their NRIC and password credentials.
     *
     * @param nric the NRIC of the user
     * @param password the password of the user
     * @return the {@link User} matching the credentials, or {@code null} if not found
     */
    public User findByCredentials(String nric, String password) {
        return entities.stream()
                .filter(user -> user.getID().equals(nric) && user.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    /**
     * Finds all users with the specified marital status.
     *
     * @param maritalStatus the marital status to search for
     * @return a list of users matching the marital status
     */
    public List<User> findByMaritalStatus(String maritalStatus) {
        return entities.stream()
                .filter(user -> user.getMaritalStatus().toString().equalsIgnoreCase(maritalStatus))
                .collect(Collectors.toList());
    }

    /**
     * Finds a user by their NRIC.
     *
     * @param nric the NRIC to search for
     * @return the {@link User} with the specified NRIC, or {@code null} if not found
     */
    public User findByNRIC(String nric) {
        if (nric == null) return null;
        String trimmedNRIC = nric.trim();
        return entities.stream()
            .filter(user -> user.getID() != null && user.getID().trim().equalsIgnoreCase(trimmedNRIC))
            .findFirst()
            .orElse(null);
    }

    /**
     * Finds a user by their name.
     *
     * @param name the name to search for
     * @return the first {@link User} matching the name, or {@code null} if none found
     */
    public User findByName(String name) {
        if (name == null) return null;
        String trimmedName = name.trim();
        return entities.stream()
            .filter(user -> user.getName() != null && user.getName().trim().equalsIgnoreCase(trimmedName))
            .findFirst()
            .orElse(null);
    }

    /**
     * Finds all users with a matching name.
     *
     * @param name the name to search for
     * @return list of users matching the name
     */
    public List<User> findAllByName(String name) {
        return entities.stream()
            .filter(user -> user.getName().equalsIgnoreCase(name))
            .collect(Collectors.toList());
    }

    /**
     * Finds all users whose age is greater than the specified value.
     *
     * @param age the age threshold
     * @return list of users older than the specified age
     */
    public List<User> findByAgeGreaterThan(int age) {
        return entities.stream()
                .filter(user -> user.getAge() > age)
                .collect(Collectors.toList());
    }

    /**
     * Initializes users from a file.
     * <p>
     * Reads user data from the specified file and populates the repository.
     * The implementation depends on the file format.
     * </p>
     *
     * @param filePath the path to the input file
     * @return {@code true} if initialization was successful, {@code false} otherwise
     */
    public boolean initializeFromFile(String filePath) {
        // Implementation to read from text/excel file and populate users
        // This would depend on the format of your input file
        return false; // Placeholder
    }
}
