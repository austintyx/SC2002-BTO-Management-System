package main.model.enquiry;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents an enquiry submitted by an applicant regarding a BTO project.
 * Stores information about the applicant, project, enquiry content, and any reply.
 *
 * @author Your Name
 * @version 1.0
 * @since 2025-04-20
 */
public class Enquiry implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Unique identifier for this enquiry.
     */
    private String enquiryId;

    /**
     * NRIC of the applicant who submitted the enquiry.
     */
    private String applicantId;

    /**
     * Name of the applicant (optional, for display).
     */
    private String applicantName;

    /**
     * Name of the project the enquiry is about.
     */
    private String projectName;

    /**
     * The text content of the enquiry.
     */
    private String enquiryText;

    /**
     * The date and time when the enquiry was created.
     */
    private Date enquiryDate;

    /**
     * The reply text (if any) to this enquiry.
     */
    private String replyText;

    /**
     * Name of the responder (manager or officer) who replied.
     */
    private String responderName;

    /**
     * NRIC of the responder (manager or officer) who replied.
     */
    private String responderId;

    /**
     * The date and time when the enquiry was replied to.
     */
    private Date replyDate;

    /**
     * Constructs a new Enquiry object.
     *
     * @param enquiryId    Unique enquiry ID
     * @param applicantId  NRIC of the applicant
     * @param projectName  Name of the project (unique project identifier)
     * @param enquiryText  The enquiry content
     */
    public Enquiry(String enquiryId, String applicantId, String projectName, String enquiryText) {
        this.enquiryId = enquiryId;
        this.applicantId = applicantId;
        this.projectName = projectName;
        this.enquiryText = enquiryText;
        this.enquiryDate = new Date();
    }

    /** @return Unique enquiry ID */
    public String getEnquiryId() {
        return enquiryId;
    }

    /** @return NRIC of the applicant */
    public String getApplicantId() {
        return applicantId;
    }

    /** @return Name of the applicant (optional, for display) */
    public String getApplicantName() {
        return applicantName;
    }

    /** @param applicantName Name of the applicant (for display) */
    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    /** @return Name of the project this enquiry is about */
    public String getProjectName() {
        return projectName;
    }

    /** @return The enquiry text */
    public String getEnquiryText() {
        return enquiryText;
    }

    /** @param enquiryText The updated enquiry text */
    public void setEnquiryText(String enquiryText) {
        this.enquiryText = enquiryText;
    }

    /** @return Date and time of enquiry creation */
    public Date getEnquiryDate() {
        return enquiryDate;
    }

    /** @return The reply text, or null if not replied */
    public String getReplyText() {
        return replyText;
    }

    /**
     * Sets the reply to this enquiry.
     * @param replyText The reply content
     * @param responderId The NRIC of the responder
     */
    public void setReply(String replyText, String responderId) {
        this.replyText = replyText;
        this.responderId = responderId;
        this.replyDate = new Date();
    }

    /** @return NRIC of the responder */
    public String getResponderId() {
        return responderId;
    }

    /** @return Name of the responder */
    public String getResponderName() {
        return responderName;
    }

    /** @param responderName Name of the responder */
    public void setResponderName(String responderName) {
        this.responderName = responderName;
    }

    /** @param responderId NRIC of the responder */
    public void setResponderId(String responderId) {
        this.responderId = responderId;
    }

    /** @return Date and time when the reply was made */
    public Date getReplyDate() {
        return replyDate;
    }

    /**
     * Checks if this enquiry has been replied to.
     * @return true if there is a reply, false otherwise
     */
    public boolean hasReply() {
        return replyText != null && !replyText.isEmpty();
    }
}
