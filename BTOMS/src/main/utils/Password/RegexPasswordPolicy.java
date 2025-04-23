package main.utils.Password;

import java.util.regex.Pattern;

/**
 * Implementation of the {@link PasswordPolicy} interface that validates passwords using a regular expression.
 * <p>
 * This policy enforces the following password requirements:
 * <ul>
 *   <li>At least 8 characters</li>
 *   <li>At least one uppercase letter</li>
 *   <li>At least one lowercase letter</li>
 *   <li>At least one digit</li>
 *   <li>At least one special symbol (@$!%*?&)</li>
 * </ul>
 * </p>
 */
public class RegexPasswordPolicy implements PasswordPolicy {
    private static final String REGEX = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    /**
     * Validates the provided password against the regular expression policy.
     *
     * @param password the password to validate
     * @return {@code true} if the password matches the policy, {@code false} otherwise
     */
    @Override
    public boolean validate(String password) {
        return PATTERN.matcher(password).matches();
    }

    /**
     * Returns a human-readable description of the password requirements enforced by this policy.
     *
     * @return the requirements string
     */
    @Override
    public String getRequirements() {
        return "Password must have:\n- 8+ characters\n- 1 uppercase\n- 1 lowercase\n- 1 digit\n- 1 special symbol (@$!%*?&)";
    }

    /**
     * Returns the error message to display when a password fails validation.
     *
     * @return the error message string
     */
    @Override
    public String getErrorMessage() {
        return "Invalid password format";
    }
}
