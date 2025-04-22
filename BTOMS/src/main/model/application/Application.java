package main.model.application;

import java.io.Serializable;
import java.util.Date;


/**
 * Represents a BTO application submitted by an applicant.
 * <p>
 * Tracks the application lifecycle from submission through to approval/booking,
 * and handles withdrawal requests. Maintains associations with the applicant
 * and project through their unique identifiers.
 * </p>
 * 
 * @author Your Name
 * @version 1.0
 * @since 2025-04-17
 */
public class Application implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /** Unique identifier for this application */
    private String applicationId;
    
    /** Applicant associated with this application (by NRIC) */
    private String applicantId;
    
    /** Project Name this application is for */
    private String projectName;
    
    /** Current status of the application */
    private ApplicationStatus status; // "Pending", "Successful", "Unsuccessful", "Booked"
    
    /** Flat type selected during booking */
    private String flatType;
    
    /** Date when the application was submitted */
    private Date applicationDate;
    
    /** Date when the status was last updated */
    private Date statusUpdateDate;
    
    private ApplicationStatus previousStatus;

    private String remarks;

    private String withdrawalReason;

    /**
     * Constructs a new application with default pending status.
     * 
     * @param applicationId Unique application ID
     * @param applicantId NRIC of the applicant
     * @param projectName Name of the applied project
     */
    public Application(String applicationId, String applicantId, String projectName) {
        this.applicationId = applicationId;
        this.applicantId = applicantId;
        this.projectName = projectName;
        this.status = ApplicationStatus.PENDING;
        this.applicationDate = new Date();
        this.statusUpdateDate = new Date();
        this.remarks = null;
        this.withdrawalReason =null;
    }

    /**
     * Returns the unique application ID.
     * @return Application ID
     */
    public String getApplicationId() {
        return applicationId;
    }

    /**
     * Returns the applicant's NRIC.
     * @return Applicant ID (NRIC)
     */
    public String getApplicantId() {
        return applicantId;
    }

    /**
     * Returns the project ID this application is for.
     * @return Project ID
     */
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName){
        this.projectName = projectName;
    }
    /**
     * Returns the current application status.
     * @return One of: "Pending", "Successful", "Unsuccessful", "Booked"
     */
    public ApplicationStatus getStatus() {
        return status;
    }


    /**
     * Sets the application status with proper state transitions.
     * @param newStatus The new status to set
     * @throws IllegalStateException if transitioning from a finalized state
     */
    public void setStatus(ApplicationStatus newStatus) {
        // Store current status as previous before any changes
        this.previousStatus = this.status;

        // Existing validation logic remains
        if (this.status == ApplicationStatus.UNSUCCESSFUL || 
            this.status == ApplicationStatus.BOOKED) {
            if (newStatus != this.status) {
                throw new IllegalStateException("Cannot modify finalized application");
            }
        }

        if (this.status == ApplicationStatus.SUCCESSFUL && newStatus == ApplicationStatus.BOOKED) {
            this.status = newStatus;
            return;
        }

        if (this.status == ApplicationStatus.SUCCESSFUL && newStatus != ApplicationStatus.BOOKED) {
            throw new IllegalStateException("Cannot revert from SUCCESSFUL to " + newStatus);
        }

        this.status = newStatus;
    }

    

    /**
     * Checks if the application is in a finalized state.
     * @return true if status is WITHDRAWN, UNSUCCESSFUL, or BOOKED
     */
    public boolean isFinalized() {
        return status == ApplicationStatus.WITHDRAWN ||
               status == ApplicationStatus.UNSUCCESSFUL ||
               status == ApplicationStatus.BOOKED;
    }
    
    /**
     * Returns the selected flat type (if booked).
     * @return Flat type or null if not booked
     */
    public String getFlatType() {
        return flatType;
    }

    /**
     * Sets the flat type for booked applications.
     * @param flatType The flat type (2-Room/3-Room)
     */
    public void setFlatType(String flatType) {
        this.flatType = flatType;
    }

    /**
     * Returns the application submission date.
     * @return Date of application
     */
    public Date getApplicationDate() {
        return applicationDate;
    }

    /**
     * Returns the last status update date.
     * @return Date of last status change
     */
    public Date getStatusUpdateDate() {
        return statusUpdateDate;
    }

    /**
     * Checks if there's a pending withdrawal request.
     * @return true if withdrawal is pending approval
     */
    public boolean isWithdrawalPending() {
        return "Pending Withdrawal".equals(status);
    }

    /**
     * Checks if withdrawal was approved.
     * @return true if withdrawal is approved
     */
    public boolean isWithdrawalApproved() {
        return "Withdrawn".equals(status);
    }


    /**
     * Initiates a withdrawal request.
     * @return true if request was successfully submitted
     */
    public boolean requestWithdrawal() {
        if (status == null) {
            status = ApplicationStatus.PENDING_WITHDRAWAL;
            return true;
        }
        return false;
    }


    public void setRemarks(String remarks){
        this.remarks = remarks;
    }

    public String getRemarks(){
        return remarks;
    }

    /**
     * Gets the previous application status before current state.
     * @return Previous ApplicationStatus or null if never changed
     */
    public ApplicationStatus getPreviousStatus() {
        return this.previousStatus;
    }

    /**
     * Gets the withdrawal reason for this application.
     * @return The withdrawal reason, or null if not set.
     */
    public String getWithdrawalReason() {
        return withdrawalReason;
    }

    /**
     * Sets the withdrawal reason for this application.
     * @param reason The reason for withdrawal.
     */
    public void setWithdrawalReason(String reason) {
        this.withdrawalReason = reason;
    }


}
