package main.utils.Password;

import main.utils.ConsoleUtils;

/**
 * Console-based implementation of the {@link PasswordUI} interface.
 * <p>
 * This class provides user interaction for password-related operations via the console,
 * including reading the current, new, and confirmation passwords, and displaying messages or errors.
 * </p>
 */
public class ConsolePasswordUI implements PasswordUI {

    /**
     * Prompts the user to enter their current password, masking the input.
     *
     * @return the current password entered by the user, or an empty string if cancelled
     */
    @Override
    public String readCurrentPassword() {
        return ConsoleUtils.readPasswordMasked("Enter current password (press Enter to cancel): ");
    }

    /**
     * Prompts the user to enter a new password, masking the input.
     *
     * @return the new password entered by the user, or an empty string if cancelled
     */
    @Override
    public String readNewPassword() {
        return ConsoleUtils.readPasswordMasked("Enter new password or press Enter to cancel: ");
    }

    /**
     * Prompts the user to confirm their new password, masking the input.
     *
     * @return the confirmation password entered by the user
     */
    @Override
    public String readConfirmPassword() {
        return ConsoleUtils.readPasswordMasked("Confirm new password: ");
    }

    /**
     * Displays a success message indicating the password was changed successfully.
     */
    @Override
    public void showSuccess() {
        System.out.println("Password changed successfully!");
    }

    /**
     * Displays an error message to the user.
     *
     * @param message the error message to display
     */
    @Override
    public void showError(String message) {
        System.out.println("Error: " + message);
    }

    /**
     * Displays a generic message to the user.
     *
     * @param message the message to display
     */
    @Override
    public void showMessage(String message) {
        System.out.println(message);
    }
}
