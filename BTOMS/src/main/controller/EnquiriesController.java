package main.controller;

import main.model.enquiry.Enquiry;
import main.model.project.Project;
import main.model.user.Applicant;
import main.model.user.User;
import main.repository.EnquiryRepository;
import main.repository.ProjectRepository;
import main.repository.UserRepository;
import main.utils.IDGenerator;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for managing enquiries in the BTO Management System.
 * Handles creation, updating, deletion, and replying to enquiries.
 * Uses project name as the unique project identifier.
 * 
 * @author Your Name
 * @version 1.0
 * @since 2025-04-17
 */
public class EnquiriesController {
    private final EnquiryRepository enquiryRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    
    /**
     * Constructs an EnquiriesController with required repositories.
     * @param enquiryRepository Repository for enquiries
     * @param projectRepository Repository for projects
     * @param userRepository Repository for users
     */
    public EnquiriesController(EnquiryRepository enquiryRepository, 
                             ProjectRepository projectRepository,
                             UserRepository userRepository) {
        this.enquiryRepository = enquiryRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }
    
    /**
     * Creates a new enquiry for a project.
     * 
     * @param applicantId The NRIC of the applicant submitting the enquiry
     * @param projectName The name of the project being enquired about
     * @param enquiryText The content of the enquiry
     * @return true if the enquiry was created successfully, false otherwise
     */
    public boolean createEnquiry(String applicantId, String projectName, String enquiryText) {
        // Validate applicant exists and is an Applicant
        User user = userRepository.findById(applicantId);
        if (!(user instanceof Applicant)) {
            return false;
        }
        
        // Validate project exists
        Project project = projectRepository.findByName(projectName);
        if (project == null) {
            return false;
        }
        
        // Create and save enquiry
        String enquiryId = IDGenerator.generateEnquiryId();
        Enquiry enquiry = new Enquiry(enquiryId, applicantId, projectName, enquiryText);
        return enquiryRepository.save(enquiry);
    }
    
    /**
     * Updates an existing enquiry if it hasn't been replied to.
     * 
     * @param enquiryId The ID of the enquiry to update
     * @param applicantId The NRIC of the applicant making the update
     * @param newEnquiryText The updated enquiry content
     * @return true if the update was successful, false otherwise
     */
    public boolean updateEnquiry(String enquiryId, String applicantId, String newEnquiryText) {
        Enquiry enquiry = enquiryRepository.findById(enquiryId);
        if (enquiry == null || !enquiry.getApplicantId().equals(applicantId)) {
            return false;
        }
        if (enquiry.hasReply()) {
            return false;
        }
        enquiry.setEnquiryText(newEnquiryText);
        return enquiryRepository.update(enquiry);
    }
    
    /**
     * Deletes an enquiry if it hasn't been replied to.
     * 
     * @param enquiryId The ID of the enquiry to delete
     * @param applicantId The NRIC of the applicant requesting deletion
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteEnquiry(String enquiryId, String applicantId) {
        if (enquiryId == null || applicantId == null) return false;
        Enquiry enquiry = enquiryRepository.findById(enquiryId);
        if (enquiry == null) return false;
        if (enquiry.getApplicantId() == null || !enquiry.getApplicantId().equals(applicantId)) return false;
        if (enquiry.hasReply()) return false;
        return enquiryRepository.delete(enquiryId);
    }
    
    /**
     * Adds a reply to an enquiry from an authorized user.
     * 
     * @param enquiryId The ID of the enquiry to reply to
     * @param responderId The NRIC of the staff member replying
     * @param replyText The content of the reply
     * @return true if the reply was added successfully, false otherwise
     */
    public boolean replyToEnquiry(String enquiryId, String responderId, String replyText) {
        Enquiry enquiry = enquiryRepository.findById(enquiryId);
        if (enquiry == null) {
            return false;
        }

        if(enquiry.hasReply() == true){
            return false;
        }
    
        Project project = projectRepository.findByName(enquiry.getProjectName());
        if (project == null) {
            return false;
        }
    
        // Check responder authorization
        boolean isAuthorized = project.getManagerInCharge().equals(responderId) ||
            project.getOfficers().contains(responderId);
        if (!isAuthorized) {
            return false;
        }
    
        // Lookup responder name
        User responder = userRepository.findById(responderId);
        String responderName = responder != null ? responder.getName() : responderId;
    
        // Set reply and responder name
        enquiry.setReply(replyText, responderId);
        enquiry.setResponderName(responderName);
    
        return enquiryRepository.update(enquiry);
    }
    
    /**
     * Retrieves all enquiries for a specific project.
     * 
     * @param projectName The name of the project
     * @return List of enquiries for the specified project
     */
    public List<Enquiry> getEnquiriesByProject(String projectName) {
        return enquiryRepository.findByProject(projectName);
    }
    
    /**
     * Retrieves all enquiries submitted by a specific applicant.
     * 
     * @param applicantId The NRIC of the applicant
     * @return List of enquiries submitted by the applicant
     */
    public List<Enquiry> getEnquiriesByApplicant(String applicantId) {
        return enquiryRepository.findByApplicant(applicantId);
    }
    
    /**
     * Retrieves an enquiry by its ID.
     * 
     * @param enquiryId The ID of the enquiry
     * @return The Enquiry object or null if not found
     */
    public Enquiry getEnquiryById(String enquiryId) {
        return enquiryRepository.findById(enquiryId);
    }
    
    /**
     * Retrieves all unanswered enquiries across all projects.
     * 
     * @return List of unanswered enquiries
     */
    public List<Enquiry> getUnansweredEnquiries() {
        return enquiryRepository.findAll().stream()
                .filter(enquiry -> !enquiry.hasReply())
                .collect(Collectors.toList());
    }
    
    /**
     * Retrieves unanswered enquiries for a specific project.
     * 
     * @param projectName The name of the project
     * @return List of unanswered enquiries for the project
     */
    public List<Enquiry> getUnansweredEnquiriesByProject(String projectName) {
        return enquiryRepository.findByProject(projectName).stream()
                .filter(enquiry -> !enquiry.hasReply())
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all enquiries in the system.
     * @return List of all Enquiry objects.
     */
    public List<Enquiry> getAllEnquiries() {
        return enquiryRepository.findAll();
    }
}
