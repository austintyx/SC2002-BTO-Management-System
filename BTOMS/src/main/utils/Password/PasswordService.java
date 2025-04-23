package main.utils.Password;

import main.model.user.User;
import main.repository.UserRepository;

// PasswordService.java
public class PasswordService {
    private final UserRepository userRepository;
    private final PasswordPolicy passwordPolicy;

    public PasswordService(UserRepository userRepository, PasswordPolicy passwordPolicy) {
        this.userRepository = userRepository;
        this.passwordPolicy = passwordPolicy;
    }

    public boolean changePassword(User user, String currentPassword, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            return false;
        }

        if (!user.getPassword().equals(currentPassword)) {
            return false;
        }
        
        if (!passwordPolicy.validate(newPassword)) {
            return false;
        }
        
        user.setPassword(newPassword);
        return userRepository.update(user);
    }
    
    public PasswordPolicy getPasswordPolicy() {
        return passwordPolicy;
    }
}

