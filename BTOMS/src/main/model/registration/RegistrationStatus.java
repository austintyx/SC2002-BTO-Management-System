package main.model.registration;

/**
 * Enumeration representing the possible statuses of an HDB Officer's registration
 * to join a project team in the BTO Management System.
 * <p>
 * Statuses:
 * <ul>
 *   <li>{@link #PENDING} - Registration has been submitted and is awaiting approval by the HDB Manager.</li>
 *   <li>{@link #APPROVED} - Registration has been approved by the HDB Manager. The officer is now part of the project team.</li>
 *   <li>{@link #REJECTED} - Registration has been rejected by the HDB Manager.</li>
 * </ul>
 * </p>
 * 
 * Usage example:
 * <pre>
 *     RegistrationStatus status = RegistrationStatus.PENDING;
 *     if (status == RegistrationStatus.APPROVED) {
 *         // Officer is approved
 *     }
 * </pre>
 * 
 * @author Your Name
 * @version 1.0
 * @since 2025-04-17
 */
public enum RegistrationStatus {
    /**
     * No registration submitted
     */
    NONE("None"),
    /**
     * Registration has been submitted and is awaiting approval by the HDB Manager.
     */
    PENDING("Pending"),
    /**
     * Registration has been approved by the HDB Manager.
     */
    APPROVED("Approved"),
    /**
     * Registration has been rejected by the HDB Manager.
     */
    REJECTED("Rejected");

    private final String displayName;

    RegistrationStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the user-friendly display name of this registration status.
     * @return the display name
     */
    @Override
    public String toString() {
        return displayName;
    }

    /**
     * Returns the RegistrationStatus corresponding to the given string (case-insensitive).
     * @param text the string to parse (e.g., "Pending")
     * @return the corresponding RegistrationStatus
     * @throws IllegalArgumentException if no matching status is found
     */
    public static RegistrationStatus fromString(String text) {
        for (RegistrationStatus status : RegistrationStatus.values()) {
            if (status.displayName.equalsIgnoreCase(text) || status.name().equalsIgnoreCase(text)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No registration status found for: " + text);
    }
}
