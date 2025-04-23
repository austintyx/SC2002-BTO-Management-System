package main.model.application;

/**
 * Enumeration representing the possible statuses of a BTO application.
 * <p>
 * Statuses:
 * <ul>
 *   <li>{@link #PENDING} - Entry status upon application; no decision made yet.</li>
 *   <li>{@link #SUCCESSFUL} - Application is successful; applicant is invited to make a flat booking.</li>
 *   <li>{@link #UNSUCCESSFUL} - Application is unsuccessful; applicant cannot book a flat for this application.</li>
 *   <li>{@link #BOOKED} - Applicant has successfully booked a flat after a successful application.</li>
 * </ul>
 * </p>
 * 
 * @author Your Name
 * @version 1.0
 * @since 2025-04-17
 */
public enum ApplicationStatus {
    NONE("None"),
    /**
     * Entry status upon application – No conclusive decision made about the outcome.
     */
    PENDING("Pending"),
    /**
     * Outcome of the application is successful; applicant is invited to make a flat booking.
     */
    SUCCESSFUL("Successful"),
    /**
     * Outcome of the application is unsuccessful; applicant cannot book a flat for this application.
     */
    UNSUCCESSFUL("Unsuccessful"),
    /**
     * Applicant has secured a unit after a successful application and completed a flat booking.
     */
    BOOKED("Booked"),

    PENDING_WITHDRAWAL("Pending Withdrawal"),

    WITHDRAWN("Withdrawn");

    private final String displayName;

    ApplicationStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the user-friendly display name of this status.
     * @return the display name
     */
    @Override
    public String toString() {
        return displayName;
    }

    /**
     * Returns the ApplicationStatus corresponding to the given string (case-insensitive).
     * @param text the string to parse (e.g., "Pending")
     * @return the corresponding ApplicationStatus
     * @throws IllegalArgumentException if no matching status is found
     */
    public static ApplicationStatus fromString(String text) {
        for (ApplicationStatus status : ApplicationStatus.values()) {
            if (status.displayName.equalsIgnoreCase(text) || status.name().equalsIgnoreCase(text)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No application status found for: " + text);
    }

    /**
     * Checks if this status represents a finalized application state.
     * @return true if status is WITHDRAWN, UNSUCCESSFUL, or BOOKED
     */
    public boolean isFinalized() {
        return this == WITHDRAWN || 
               this == UNSUCCESSFUL || 
               this == BOOKED;
    }
}
