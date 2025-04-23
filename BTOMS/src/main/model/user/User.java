package main.model.user;

import java.io.Serializable;

/**
 * Represents a generic user in the BTO Management System.
 * <p>
 * This interface defines the basic information and behaviors common to all user types,
 * including Applicants, HDB Officers, and HDB Managers. All users have an NRIC, name,
 * password, age, and marital status. Implementing classes must also be serializable
 * for file-based persistence.
 * </p>
 *
 * <p>
 * <b>Assignment context:</b>  
 * <ul>
 *   <li>Applicants can view and apply for projects, manage applications, and submit enquiries.</li>
 *   <li>HDB Officers can register for projects, manage flat selections, and generate receipts.</li>
 *   <li>HDB Managers can create, edit, and delete projects, manage officer registrations, and generate reports.</li>
 * </ul>
 * </p>
 *
 * @author Your Name
 * @version 1.0
 * @since 2025-04-16
 */
public interface User extends Serializable {

    /**
     * Returns the unique identifier (NRIC) of this user.
     * @return the NRIC of the user
     */
    String getID();

    /**
     * Returns the name of this user.
     * @return the user's name
     */
    String getName();

    /**
     * Returns the password for this user's account.
     * @return the user's password
     */
    String getPassword();

    /**
     * Sets a new password for this user's account.
     * @param password the new password
     */
    void setPassword(String password);

    /**
     * Returns the age of this user.
     * @return the user's age
     */
    int getAge();

    /**
     * Returns the marital status of this user.
     * @return the user's marital status
     */
    MaritalStatus getMaritalStatus();
}
