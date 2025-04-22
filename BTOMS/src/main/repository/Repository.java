package main.repository;

import java.util.List;

/**
 * A generic repository interface that defines basic CRUD operations for entity persistence.
 * <p>
 * This interface abstracts the data access layer and allows for different implementations
 * (e.g., file-based, database) to provide storage and retrieval of entities.
 * </p>
 *
 * @param <T>  The type of entity managed by the repository.
 * @param <ID> The type of the unique identifier for the entity.
 *
 * <p>
 * <b>Typical usage in the BTO Management System:</b>
 * <ul>
 *   <li>ProjectRepository manages Project entities.</li>
 *   <li>UserRepository manages User entities.</li>
 *   <li>ApplicationRepository manages Application entities.</li>
 *   <li>EnquiryRepository manages Enquiry entities.</li>
 * </ul>
 * </p>
 * 
 * @author Your Name
 * @version 1.0
 * @since 2025-04-16
 */
public interface Repository<T, ID> {
    /**
     * Finds an entity by its unique identifier.
     *
     * @param id The unique identifier of the entity.
     * @return The entity with the given ID, or {@code null} if not found.
     */
    T findById(ID id);

    /**
     * Returns a list of all entities managed by this repository.
     *
     * @return A list containing all entities.
     */
    List<T> findAll();

    /**
     * Saves a new entity to the repository.
     *
     * @param entity The entity to save.
     * @return {@code true} if the entity was saved successfully, {@code false} otherwise.
     */
    boolean save(T entity);

    /**
     * Updates an existing entity in the repository.
     *
     * @param entity The entity with updated information.
     * @return {@code true} if the update was successful, {@code false} otherwise.
     */
    boolean update(T entity);

    /**
     * Deletes an entity from the repository by its unique identifier.
     *
     * @param id The unique identifier of the entity to delete.
     * @return {@code true} if the entity was deleted successfully, {@code false} otherwise.
     */
    boolean delete(ID id);
}
