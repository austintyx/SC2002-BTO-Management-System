package main.utils.Password;

/**
 * Interface for user interaction during password operations.
 * <p>
 * Implementations provide methods for reading password input and displaying messages to the user.
 * </p>
 */
public interface PasswordUI {
    /**
     * Prompts the user to enter their current password.
     *
     * @return the current password entered by the user
     */
    String readCurrentPassword();

    /**
     * Prompts the user to enter a new password.
     *
     * @return the new password entered by the user
     */
    String readNewPassword();

    /**
     * Prompts the user to confirm their new password.
     *
     * @return the confirmation password entered by the user
     */
    String readConfirmPassword();

    /**
     * Displays a success message to the user.
     */
    void showSuccess();

    /**
     * Displays an error message to the user.
     *
     * @param message the error message to display
     */
    void showError(String message);

    /**
     * Displays a generic message to the user.
     *
     * @param message the message to display
     */
    void showMessage(String message);
}
