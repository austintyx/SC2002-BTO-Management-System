package main.utils.Password;

import main.model.user.User;

/**
 * Interface defining the contract for password change functionality.
 * <p>
 * Provides a default implementation for changing a user's password, including user interaction,
 * password policy validation, and confirmation. The interface expects implementations to provide
 * a {@link PasswordService} for business logic and a {@link PasswordUI} for user interaction.
 * </p>
 */
public interface PasswordChanger {

    /**
     * Initiates the password change process for the specified user.
     * <p>
     * The process includes:
     * <ul>
     *   <li>Prompting the user for their current password and verifying it</li>
     *   <li>Displaying password policy requirements</li>
     *   <li>Prompting for a new password and confirmation</li>
     *   <li>Validating the new password against the policy</li>
     *   <li>Attempting to update the password via the provided {@link PasswordService}</li>
     *   <li>Providing success or error feedback via the {@link PasswordUI}</li>
     * </ul>
     * If the user cancels at any stage, the process is aborted.
     * </p>
     *
     * @param currentUser      the {@link User} whose password is to be changed
     * @param passwordService  the {@link PasswordService} handling password updates and validation
     * @param ui               the {@link PasswordUI} for user interaction
     */
    default void changePassword(User currentUser, PasswordService passwordService, PasswordUI ui) {
        ui.showMessage("\n===== Change Password =====");
        
        // 1. Verify current password
        String currentPassword = handleCurrentPassword(ui, currentUser);
        if (currentPassword == null) return;
    
        // 2. Get password policy requirements
        PasswordPolicy policy = passwordService.getPasswordPolicy();
        
        // 3. Get and validate new password with confirmation
        String newPassword;
        String confirmPassword;
        do {
            newPassword = handleNewPassword(ui, policy);
            if (newPassword == null) return;  // User cancelled
            
            confirmPassword = ui.readConfirmPassword();
            if (confirmPassword.isEmpty()) {
                ui.showMessage("Password change cancelled.");
                return;
            }
            
            if (!newPassword.equals(confirmPassword)) {
                ui.showError("Passwords do not match!");
            }
        } while (!newPassword.equals(confirmPassword));
    
        // 4. Attempt password change through service
        if (passwordService.changePassword(currentUser, currentPassword, newPassword, confirmPassword)) {
            ui.showSuccess();
        } else {
            ui.showError("Password change failed. Please check requirements.");
        }
    }

    /**
     * Handles prompting the user for their current password and verifies it against the user's stored password.
     * If the user cancels the operation (by entering an empty string), the process is aborted.
     *
     * @param ui   the {@link PasswordUI} for user interaction
     * @param user the {@link User} whose password is to be verified
     * @return the entered current password if correct, or {@code null} if cancelled
     */
    private String handleCurrentPassword(PasswordUI ui, User user) {
        while (true) {
            String input = ui.readCurrentPassword();
            if (input.isEmpty()) {
                ui.showMessage("Password change cancelled.");
                return null;
            }
            if (input.equals(user.getPassword())) {
                return input;
            }
            ui.showError("Incorrect current password. Please try again.");
        }
    }

    /**
     * Prompts the user for a new password, displays password policy requirements,
     * and validates the new password against the provided policy.
     * If the user cancels the operation (by entering an empty string), the process is aborted.
     *
     * @param ui     the {@link PasswordUI} for user interaction
     * @param policy the {@link PasswordPolicy} for validation
     * @return the valid new password entered by the user, or {@code null} if cancelled
     */
    private String handleNewPassword(PasswordUI ui, PasswordPolicy policy) {
        while (true) {
            ui.showMessage(policy.getRequirements());
            String newPassword = ui.readNewPassword();
            
            if (newPassword.isEmpty()) {
                ui.showMessage("Password change cancelled.");
                return null;
            }
            
            if (!policy.validate(newPassword)) {
                ui.showError(policy.getErrorMessage());
                continue;
            }
            
            return newPassword;
        }
    }
}
