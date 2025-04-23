package main.utils.Password;

import main.model.user.User;

// PasswordChanger.java
public interface PasswordChanger {
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
