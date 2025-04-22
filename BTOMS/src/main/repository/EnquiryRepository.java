package main.repository;

import main.model.enquiry.Enquiry;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Iterator;

/**
 * Repository class for managing Enquiry entities.
 * Uses project name as the unique identifier for project-related operations.
 * 
 * @author Your Name
 * @version 1.0
 * @since 2025-04-20
 */
public class EnquiryRepository extends FileBasedRepository<Enquiry, String> {
    
    /**
     * Constructs the EnquiryRepository with the default data file.
     */
    public EnquiryRepository() {
        super("data/enquiries.dat");
    }
    
    /**
     * Finds an enquiry by its unique enquiry ID.
     * @param id The enquiry ID to search for.
     * @return The Enquiry object if found, otherwise null.
     */
    @Override
    public Enquiry findById(String id) {
        return entities.stream()
                .filter(enquiry -> enquiry.getEnquiryId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Saves a new enquiry to the repository.
     * @param enquiry The Enquiry object to save.
     * @return true if saved successfully, false if enquiry ID already exists.
     */
    @Override
    public boolean save(Enquiry enquiry) {
        if (findById(enquiry.getEnquiryId()) != null) {
            return false; // Enquiry with this ID already exists
        }
        entities.add(enquiry);
        return saveToFile();
    }
    
    /**
     * Updates an existing enquiry in the repository.
     * @param enquiry The Enquiry object with updated details.
     * @return true if updated successfully, false if enquiry not found.
     */
    @Override
    public boolean update(Enquiry enquiry) {
        for (int i = 0; i < entities.size(); i++) {
            if (entities.get(i).getEnquiryId().equals(enquiry.getEnquiryId())) {
                entities.set(i, enquiry);
                return saveToFile();
            }
        }
        return false; // Enquiry not found
    }
    
    /**
     * Deletes an enquiry by its enquiry ID.
     * @param id The enquiry ID to delete.
     * @return true if deleted successfully, false if not found.
     */
    @Override
    public boolean delete(String id) {
        if (id == null) return false;
        Iterator<Enquiry> iter = entities.iterator();
        while (iter.hasNext()) {
            Enquiry e = iter.next();
            if (e.getEnquiryId().equals(id)) {
                iter.remove();
                return saveToFile();
            }
        }
        return false;
    }

    /**
     * Retrieves the unique ID for an Enquiry entity.
     * @param enquiry The Enquiry object.
     * @return The enquiry ID.
     */
    @Override
    protected String getEntityId(Enquiry enquiry) {
        return enquiry.getEnquiryId();
    }
    
    // Additional methods specific to Enquiry repository

    /**
     * Finds all enquiries submitted by a specific applicant.
     * @param applicantNRIC The NRIC of the applicant.
     * @return List of Enquiry objects submitted by the applicant.
     */
    public List<Enquiry> findByApplicant(String applicantNRIC) {
        return entities.stream()
                .filter(enquiry -> enquiry.getApplicantId().equals(applicantNRIC))
                .collect(Collectors.toList());
    }
    
    /**
     * Finds all enquiries for a specific project by project name.
     * @param projectName The name of the project.
     * @return List of Enquiry objects for the specified project.
     */
    public List<Enquiry> findByProject(String projectName) {
        return entities.stream()
                .filter(enquiry -> enquiry.getProjectName().equals(projectName))
                .collect(Collectors.toList());
    }
    
    /**
     * Finds all enquiries that have not been replied to.
     * @return List of unanswered Enquiry objects.
     */
    public List<Enquiry> findUnansweredEnquiries() {
        return entities.stream()
                .filter(enquiry -> enquiry.getReplyText() == null || enquiry.getReplyText().isEmpty())
                .collect(Collectors.toList());
    }
}
