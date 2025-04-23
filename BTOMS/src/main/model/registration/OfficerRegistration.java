package main.model.registration;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a registration request for an HDB Officer to join a project team.
 */
public class OfficerRegistration implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String registrationId;
    private String officerNRIC;
    private String projectName;  // Changed from projectId to projectName
    private RegistrationStatus status;
    private Date registrationDate;
    private Date statusUpdateDate;
    private String remarks;

    /**
     * Creates a new OfficerRegistration instance.
     * 
     * @param registrationId Unique registration ID
     * @param officerNRIC NRIC of the officer applying
     * @param projectName Name of the project to join
     */
    public OfficerRegistration(String registrationId, String officerNRIC, String projectName) {
        this.registrationId = registrationId;
        this.officerNRIC = officerNRIC;
        this.projectName = projectName;
        this.status = RegistrationStatus.NONE;
        this.registrationDate = new Date();
        this.statusUpdateDate = new Date();
    }

    // Getters and setters with JavaDoc

    /** @return Unique registration ID */
    public String getRegistrationId() {
        return registrationId;
    }

    /** @return NRIC of the applying officer */
    public String getOfficerNRIC() {
        return officerNRIC;
    }

    /** @return Name of the project being applied to */
    public String getProjectName() {  // Renamed from getProjectId
        return projectName;
    }

    /** @return Current status of the registration */
    public RegistrationStatus getStatus() {
        return status;
    }

    /**
     * Updates the registration status and sets the status update date.
     * @param status New status for the registration
     */
    public void setStatus(RegistrationStatus status) {
        this.status = status;
        this.statusUpdateDate = new Date();
    }

    /** @return Date when the registration was created */
    public Date getRegistrationDate() {
        return registrationDate;
    }

    /** @return Last status update date */
    public Date getStatusUpdateDate() {
        return statusUpdateDate;
    }

    /** @return Manager's remarks (if any) */
    public String getRemarks() {
        return remarks;
    }

    /**
     * Sets manager's remarks for this registration.
     * @param remarks Comments from the manager
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    // Status check methods

    /** @return true if registration is pending review */
    public boolean isPending() {
        return status == RegistrationStatus.PENDING;
    }

    /** @return true if registration was approved */
    public boolean isApproved() {
        return status == RegistrationStatus.APPROVED;
    }

    /** @return true if registration was rejected */
    public boolean isRejected() {
        return status == RegistrationStatus.REJECTED;
    }
}
