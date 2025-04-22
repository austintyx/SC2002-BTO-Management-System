package main.utils;

import main.controller.PasswordController;
import main.model.user.User;
import main.repository.UserRepository;

public interface PasswordChange {
    default void changePassword(User currentUser, 
                               PasswordController passwordController,
                               UserRepository userRepository) {
        System.out.println("\n===== Change Password =====");
        
        // Verify current password
        String currentPassword;
        while (true) {
            currentPassword = ConsoleUtils.readPasswordMasked("Enter current password (press Enter to cancel): ");
            if (currentPassword.isEmpty()) {
                System.out.println("Password change cancelled.");
                return;
            }
            if (currentPassword.equals(currentUser.getPassword())) {
                break;
            }
            System.out.println("Incorrect current password. Please try again.");
        }

        // Get and validate new password
        String newPassword = "";
        String confirmPassword = "";
        while (true) {
            newPassword = ConsoleUtils.readPasswordMasked(
                "Enter new password (8+ chars, 1+ upper/lower/digit/special) or press Enter to cancel: "
            );
            
            if (newPassword.isEmpty()) {
                System.out.println("Password change cancelled.");
                return;
            }
            
            if (!PasswordController.isPasswordValid(newPassword)) {
                System.out.println("""
                    Password must have:
                    - 8+ characters
                    - 1 uppercase
                    - 1 lowercase
                    - 1 digit
                    - 1 special symbol (@$!%*?&)
                    """);
                continue;
            }
            
            confirmPassword = ConsoleUtils.readPasswordMasked("Confirm new password: ");
            if (!newPassword.equals(confirmPassword)) {
                System.out.println("Passwords do not match. Please try again.");
                continue;
            }
            
            break;
        }

        // Update password through controller
        boolean success = passwordController.changePassword(
            currentUser,
            currentPassword,
            newPassword,
            confirmPassword
        );

        if (success) {
            System.out.println("Password changed successfully!");
            userRepository.update(currentUser);
        } else {
            System.out.println("Password change failed. Please check requirements.");
        }
    }
}
