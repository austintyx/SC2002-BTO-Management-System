package main.utils.Password;

/**
 * Interface for defining password validation policies.
 * <p>
 * Implementations specify the rules for password validity, provide a description of the requirements,
 * and supply error messages for invalid passwords.
 * </p>
 */
public interface PasswordPolicy {
    /**
     * Validates the provided password against the policy rules.
     *
     * @param password the password to validate
     * @return {@code true} if the password meets the policy requirements, {@code false} otherwise
     */
    boolean validate(String password);

    /**
     * Returns a human-readable description of the password requirements.
     *
     * @return the requirements string
     */
    String getRequirements();

    /**
     * Returns an error message describing why a password is invalid.
     *
     * @return the error message string
     */
    String getErrorMessage();
}
