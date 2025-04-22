package main.repository;

import main.model.user.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserRepository extends FileBasedRepository<User, String> {
    private static final String CSV_FILE = "UserList.csv";
    private static final String DATA_FILE = "data/users.dat";

    public UserRepository() {
        super(DATA_FILE);

    }
    


    private void saveWithoutCheck(User user) {
        entities.add(user);
    }

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
    
    
    @Override
    public boolean save(User user) {
        if (findById(user.getID()) != null) {
            return false; // User with this NRIC already exists
        }
        entities.add(user);
        return saveToFile();
    }
    
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
    
    @Override
    public boolean delete(String nric) {
        boolean removed = entities.removeIf(user -> user.getID().equals(nric));
        if (removed) {
            return saveToFile();
        }
        return false;
    }
    
    @Override
    protected String getEntityId(User user) {
        return user.getID();
    }
    
    // Additional methods specific to User repository
    
    public User findByCredentials(String nric, String password) {
        return entities.stream()
                .filter(user -> user.getID().equals(nric) && user.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }
    
    public List<User> findByMaritalStatus(String maritalStatus) {
        return entities.stream()
                .filter(user -> user.getMaritalStatus().toString().equalsIgnoreCase(maritalStatus))
                .collect(Collectors.toList());
    }

     /**
     * Finds a user by their name.
     * @param name The name to search for
     * @return The first user matching the name, or null if none found
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
     * @param name The name to search for
     * @return List of users matching the name
     */
    public List<User> findAllByName(String name) {
        return entities.stream()
            .filter(user -> user.getName().equalsIgnoreCase(name))
            .collect(Collectors.toList());
    }
    
    public List<User> findByAgeGreaterThan(int age) {
        return entities.stream()
                .filter(user -> user.getAge() > age)
                .collect(Collectors.toList());
    }
    
    // Method to initialize users from a file (as mentioned in the assignment)
    public boolean initializeFromFile(String filePath) {
        // Implementation to read from text/excel file and populate users
        // This would depend on the format of your input file
        return false; // Placeholder
    }
}


