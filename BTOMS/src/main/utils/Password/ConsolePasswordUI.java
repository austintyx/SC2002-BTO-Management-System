package main.utils.Password;

import main.utils.ConsoleUtils;

// ConsolePasswordUI.java
public class ConsolePasswordUI implements PasswordUI {
    @Override
    public String readCurrentPassword() {
        return ConsoleUtils.readPasswordMasked("Enter current password (press Enter to cancel): ");
    }

    @Override
    public String readNewPassword() {
        return ConsoleUtils.readPasswordMasked("Enter new password or press Enter to cancel: ");
    }

    @Override
    public String readConfirmPassword() {
        return ConsoleUtils.readPasswordMasked("Confirm new password: ");
    }

    @Override
    public void showSuccess() {
        System.out.println("Password changed successfully!");
    }

    @Override
    public void showError(String message) {
        System.out.println("Error: " + message);
    }

    @Override
    public void showMessage(String message) {
        System.out.println(message);
    }
}
