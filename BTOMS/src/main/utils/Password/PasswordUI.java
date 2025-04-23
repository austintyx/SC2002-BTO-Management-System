package main.utils.Password;

// PasswordUI.java
public interface PasswordUI {
    String readCurrentPassword();
    String readNewPassword();
    String readConfirmPassword();
    void showSuccess();
    void showError(String message);
    void showMessage(String message);
}

