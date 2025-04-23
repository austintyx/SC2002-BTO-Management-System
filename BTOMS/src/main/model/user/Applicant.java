package main.model.user;

import main.model.application.ApplicationStatus;

/**
 * Represents an applicant in the BTO Management System.
 * <p>
 * An applicant can view and apply for BTO projects, view their application status,
 * request withdrawal, and manage their own enquiries.
 * </p>
 * 
 * @author Your Name
 * @version 1.0
 * @since 2025-04-17
 */
public class Applicant implements User {
    private static final long serialVersionUID = 1L;

    /**
     * NRIC of the applicant (serves as unique user ID).
     */
    private String nric;

    /**
     * Name of the applicant.
     */
    private String name;

    /**
     * Password for this applicant's account.
     */
    private String password;

    /**
     * Age of the applicant.
     */
    private int age;

    /**
     * Marital status of the applicant.
     */
    private MaritalStatus maritalStatus;

    /**
     * Name of the project the applicant has applied for.
     */
    private String appliedProjectName;

    /**
     * Withdrawal status of the applicant's application.
     */
    private ApplicationStatus applicationStatus;

    /**
     * Constructs a new Applicant with the given personal details.
     * 
     * @param nric Applicant's NRIC
     * @param name Applicant's name
     * @param password Applicant's password
     * @param age Applicant's age
     * @param maritalStatus Applicant's marital status
     */
    public Applicant(String nric, String name, String password, int age, MaritalStatus maritalStatus) {
        this.nric = nric;
        this.name = name;
        this.password = password;
        this.age = age;
        this.maritalStatus = maritalStatus;
        this.appliedProjectName = null;
        this.applicationStatus = ApplicationStatus.NONE;
    }

    /**
     * Returns the NRIC of the applicant.
     * @return Applicant's NRIC
     */
    @Override
    public String getID() {
        return nric;
    }

    /**
     * Returns the name of the applicant.
     * @return Applicant's name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns the password for this applicant's account.
     * @return Applicant's password
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Sets a new password for this applicant's account.
     * @param password The new password
     */
    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the age of the applicant.
     * @return Applicant's age
     */
    @Override
    public int getAge() {
        return age;
    }

    /**
     * Returns the marital status of the applicant.
     * @return Applicant's marital status
     */
    @Override
    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    /**
     * Sets the marital status of the applicant.
     * @param maritalStatus The new marital status
     */
    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    /**
     * Returns the name of the project the applicant has applied for.
     * @return Project name, or null if not applied
     */
    public String getAppliedProjectName() {
        return appliedProjectName;
    }

    /**
     * Sets the name of the project the applicant has applied for.
     * @param appliedProjectName The project name
     */
    public void setAppliedProjectName(String appliedProjectName) {
        this.appliedProjectName = appliedProjectName;
    }

    /**
     * Returns the withdrawal status of the applicant's application.
     * @return Withdrawal status
     */
    public ApplicationStatus getStatus() {
        return applicationStatus;
    }

    /**
     * Sets the withdrawal status of the applicant's application.
     * @param withdrawalStatus The new withdrawal status
     */
    public void setStatus(ApplicationStatus applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

        /**
     * Checks if the applicant is eligible for the given flat type.
     * <p>
     * - Singles, 35 years old and above, can ONLY apply for 2-Room.<br>
     * - Married, 21 years old and above, can apply for any flat types (2-Room or 3-Room).
     * </p>
     * @param flatType The flat type to check ("2-Room" or "3-Room")
     * @return true if eligible, false otherwise
     */
    public boolean isEligibleForFlatType(String flatType) {
        if (flatType == null) return false;
        switch (flatType) {
            case "2-Room":
                if (maritalStatus == MaritalStatus.SINGLE && age >= 35) {
                    return true;
                }
                if (maritalStatus == MaritalStatus.MARRIED && age >= 21) {
                    return true;
                }
                return false;
            case "3-Room":
                return maritalStatus == MaritalStatus.MARRIED && age >= 21;
            default:
                return false;
        }
    }

}
