package main.utils.Password;

import java.util.regex.Pattern;


public class RegexPasswordPolicy implements PasswordPolicy {
    private static final String REGEX = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    private static final Pattern PATTERN = Pattern.compile(REGEX);
    
    @Override
    public boolean validate(String password) {
        return PATTERN.matcher(password).matches();
    }

    @Override
    public String getRequirements() {
        return "Password must have:\n- 8+ characters\n- 1 uppercase\n- 1 lowercase\n- 1 digit\n- 1 special symbol (@$!%*?&)";
    }

    @Override
    public String getErrorMessage() {
        return "Invalid password format";
    }
}