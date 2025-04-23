package main.model.user;

/**
 * Enumeration for Marital Status in the BTO Management System.
 * Provides display names and parsing from string.
 * 
 * <p>Possible values are:
 * <ul>
 *   <li>{@link #MARRIED}</li>
 *   <li>{@link #SINGLE}</li>
 * </ul>
 * </p>
 * 
 * @author Your Name
 * @version 1.0
 * @since 2025-04-16
 */
public enum MaritalStatus {
    /**
     * Married status.
     */
    MARRIED("Married"),
    /**
     * Single status.
     */
    SINGLE("Single");

    /**
     * The display name for this marital status.
     */
    private final String displayName;

    /**
     * Constructs a MaritalStatus with the given display name.
     * @param displayName the display name for the status
     */
    MaritalStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the display name of this marital status.
     * @return the display name
     */
    @Override
    public String toString() {
        return displayName;
    }

    /**
     * Returns the MaritalStatus corresponding to the given string (case-insensitive).
     * @param text the text to parse
     * @return the corresponding MaritalStatus
     * @throws IllegalArgumentException if no match is found
     */
    public static MaritalStatus fromString(String text) {
        for (MaritalStatus n : MaritalStatus.values()) {
            if (n.displayName.equalsIgnoreCase(text)) {
                return n;
            }
        }
        throw new IllegalArgumentException("No marital status found for: " + text);
    }
}
