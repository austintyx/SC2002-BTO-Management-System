package main.utils.Password;

// PasswordPolicy.java
public interface PasswordPolicy {
    boolean validate(String password);
    String getRequirements();
    String getErrorMessage();
}

