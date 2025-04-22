package main.model.user;

import java.util.Date;

import main.model.project.Project;
import main.utils.DateUtils;

/**
 * Represents an HDB Manager user in the BTO Management System.
 * <p>
 * An HDB Manager can create, edit, and delete BTO projects, manage officer registrations,
 * approve/reject BTO applications and withdrawals, generate reports, and reply to enquiries.
 * Each manager can only handle one project at a time, referenced by the project name.
 * </p>
 * 
 * @author Your Name
 * @version 1.0
 * @since 2025-04-16
 */
public class HDBManager implements User {
    private static final long serialVersionUID = 1L;

    /**
     * The NRIC of the manager (serves as user ID).
     */
    private String nric;

    /**
     * The name of the manager.
     */
    private String managerName;

    /**
     * The password for this manager's account.
     */
    private String password;

    /**
     * The age of the manager.
     */
    private int age;

    /**
     * The marital status of the manager.
     */
    private MaritalStatus maritalStatus;

    /**
     * The name of the project this manager is currently in charge of.
     * Only one project can be handled at a time.
     */
    private String handlingProjectName;

    /**
     * Constructs a new HDBManager with the given attributes.
     * 
     * @param nric          The NRIC of the manager.
     * @param managerName          The name of the manager.
     * @param password      The password for the manager's account.
     * @param age           The age of the manager.
     * @param maritalStatus The marital status of the manager.
     */
    public HDBManager(String nric, String managerName, String password, int age, MaritalStatus maritalStatus) {
        this.nric = nric;
        this.managerName = managerName;
        this.password = password;
        this.age = age;
        this.maritalStatus = maritalStatus;
        this.handlingProjectName = null;
    }

    /**
     * Returns the NRIC of the manager.
     * @return the manager's NRIC
     */
    @Override
    public String getID() {
        return nric;
    }

    /**
     * Returns the name of the manager.
     * @return the manager's name
     */
    @Override
    public String getName() {
        return managerName;
    }

    /**
     * Returns the password for this manager's account.
     * @return the manager's password
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Sets a new password for this manager's account.
     * @param password the new password
     */
    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the age of the manager.
     * @return the manager's age
     */
    @Override
    public int getAge() {
        return age;
    }

    /**
     * Returns the marital status of the manager.
     * @return the manager's marital status
     */
    @Override
    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    /**
     * Sets the name of the project this manager is in charge of.
     * @param projectName the project name to assign
     */
    public void setHandlingProjectName(String projectName) {
        this.handlingProjectName = projectName;
    }

    /**
     * Returns the name of the project this manager is in charge of.
     * @return the project name being handled, or null if none
     */
    public String getHandlingProjectName() {
        return handlingProjectName;
    }

    /**
     * Removes the project assignment from this manager (e.g., after project deletion).
     */
    public void clearHandlingProject() {
        this.handlingProjectName = null;
    }


    /**
     * Checks if the manager is handling a project during a given period.
     * <p>
     * This method checks if the given project (by name) is the one handled by this manager,
     * and if its application period overlaps with the specified date range.
     * </p>
     * 
     * @param project   The project to check.
     * @param startDate The start date of the period to check.
     * @param endDate   The end date of the period to check.
     * @return true if the manager is handling the project during the period; false otherwise.
     */
    public boolean isHandlingProjectDuringPeriod(Project project, Date startDate, Date endDate) {
        if (handlingProjectName == null || project == null) return false;
        if (!project.getProjectName().equals(handlingProjectName)) return false;
        return DateUtils.isDateRangeOverlapping(
            startDate, endDate,
            project.getOpeningDate(),
            project.getClosingDate()
        );
    }
}
