package main.model.project;

/**
 * Enumeration of all supported HDB neighborhoods in the BTO Management System.
 * <p>
 * Each constant represents a valid neighborhood where a BTO project can be located.
 * The enum provides a user-friendly display name for each neighborhood and
 * supports parsing from string input.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>
 *     Neighborhood n = Neighborhood.fromString("Yishun");
 *     System.out.println(n); // prints "Yishun"
 * </pre>
 *
 * @author Your Name
 * @version 1.0
 * @since 2025-04-16
 */
public enum Neighborhood {
    /** Ang Mo Kio neighborhood. */
    ANG_MO_KIO("Ang Mo Kio"),
    /** Bukit Batok neighborhood. */
    BUKIT_BATOK("Bukit Batok"),
    /** Bishan neighborhood. */
    BISHAN("Bishan"),
    /** Bedok neighborhood. */
    BEDOK("Bedok"),
    /** Bukit Merah neighborhood. */
    BUKIT_MERAH("Bukit Merah"),
    /** Bukit Panjang neighborhood. */
    BUKIT_PANJANG("Bukit Panjang"),
    /** Bukit Timah neighborhood. */
    BUKIT_TIMAH("Bukit Timah"),
    /** Central neighborhood. */
    CENTRAL("Central"),
    /** Choa Chu Kang neighborhood. */
    CHOA_CHU_KANG("Choa Chu Kang"),
    /** Clementi neighborhood. */
    CLEMENTI("Clementi"),
    /** Geylang neighborhood. */
    GEYLANG("Geylang"),
    /** Hougang neighborhood. */
    HOUGANG("Hougang"),
    /** Jurong East neighborhood. */
    JURONG_EAST("Jurong East"),
    /** Jurong West neighborhood. */
    JURONG_WEST("Jurong West"),
    /** Kallang/Whampoa neighborhood. */
    KALLANG_WHAMPOA("Kallang/Whampoa"),
    /** Marine Parade neighborhood. */
    MARINE_PARADE("Marine Parade"),
    /** Punggol neighborhood. */
    PUNGGOL("Punggol"),
    /** Pasir Ris neighborhood. */
    PASIR_RIS("Pasir Ris"),
    /** Queenstown neighborhood. */
    QUEENSTOWN("Queenstown"),
    /** Sembawang neighborhood. */
    SEMBAWANG("Sembawang"),
    /** Sengkang neighborhood. */
    SENGKANG("Sengkang"),
    /** Serangoon neighborhood. */
    SERANGOON("Serangoon"),
    /** Tampines neighborhood. */
    TAMPINES("Tampines"),
    /** Tengah neighborhood. */
    TENGAH("Tengah"),
    /** Toa Payoh neighborhood. */
    TOA_PAYOH("Toa Payoh"),
    /** Woodlands neighborhood. */
    WOODLANDS("Woodlands"),
    /** Yishun neighborhood. */
    YISHUN("Yishun");

    /**
     * The display name of the neighborhood.
     */
    private final String displayName;

    /**
     * Constructs a Neighborhood enum constant with the given display name.
     * @param displayName The user-friendly name for the neighborhood.
     */
    Neighborhood(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the user-friendly display name of this neighborhood.
     * @return the display name of the neighborhood
     */
    @Override
    public String toString() {
        return displayName;
    }

    public static boolean isValid(String name) {
        for (Neighborhood n : Neighborhood.values()) {
            if (n.name().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the Neighborhood constant matching the given string (case-insensitive).
     * @param text The string to parse (e.g., "Yishun")
     * @return The corresponding Neighborhood enum constant
     * @throws IllegalArgumentException if no matching neighborhood is found
     */
    public static Neighborhood fromString(String text) {
        for (Neighborhood n : Neighborhood.values()) {
            if (n.displayName.equalsIgnoreCase(text)) {
                return n;
            }
        }
        throw new IllegalArgumentException("No neighborhood found for: " + text);
    }
}
