package main.repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * An abstract generic repository class for file-based persistence using Java serialization.
 * <p>
 * Provides base CRUD operations and handles persistence through object serialization.
 * Subclasses must implement entity-specific ID handling via {@link #getEntityId(Object)}.
 * </p>
 *
 * @param <T>  The type of entity managed by this repository
 * @param <ID> The type of unique identifier for the entity
 * 
 * @author Your Name
 * @version 1.0
 * @since 2025-04-16
 */
public abstract class FileBasedRepository<T, ID> implements Repository<T, ID> {
    
    /**
     * In-memory list of entities managed by this repository.
     * Initialized from persistent storage on construction.
     */
    protected List<T> entities;

    /**
     * Absolute filesystem path for storing serialized entities.
     */
    protected final String filePath;

    /**
     * Constructs a new repository with specified persistence location.
     * <p>
     * Initializes entities from file if available, otherwise starts empty.
     * Ensures parent directories exist for the data file.
     * </p>
     *
     * @param filePath absolute path to the data file (e.g., "data/projects.dat")
     */
    public FileBasedRepository(String filePath) {
        this.filePath = filePath;
        this.entities = loadFromFile();
        ensureDataDirectoryExists();
    }

    /**
     * Ensures parent directories for the data file exist.
     * Creates directories if they don't exist.
     */
    private void ensureDataDirectoryExists() {
        File dataDir = new File(filePath).getParentFile();
        if (dataDir != null && !dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    /**
     * Loads entities from the persistence file.
     * <p>
     * Safe to call multiple times - reloads data from disk.
     * </p>
     *
     * @return list of deserialized entities, or empty list if file doesn't exist or deserialization fails
     * @throws ClassNotFoundException if serialized class versions mismatch (logged but not thrown)
     */
    @SuppressWarnings("unchecked")
    protected List<T> loadFromFile() {
        File file = new File(filePath);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<T>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading data from " + filePath + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Persists current entity state to disk.
     * <p>
     * Overwrites existing file contents completely.
     * </p>
     *
     * @return true if save succeeded, false if any I/O error occurred
     * @throws SecurityException if write permissions are insufficient
     */
    protected boolean saveToFile() {
        try {
            ensureDataDirectoryExists();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
                oos.writeObject(entities);
                return true;
            }
        } catch (IOException e) {
            System.err.println("Error saving data to " + filePath + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves a defensive copy of all entities.
     *
     * @return new ArrayList containing all current entities
     */
    @Override
    public List<T> findAll() {
        return new ArrayList<>(entities);
    }

    /**
     * Template method for entity ID extraction.
     * <p>
     * Must be implemented by concrete repositories to provide entity-specific
     * identifier retrieval logic.
     * </p>
     *
     * @param entity the entity to extract ID from
     * @return unique identifier for the entity
     * @throws NullPointerException if entity is null
     */
    protected abstract ID getEntityId(T entity);
}
