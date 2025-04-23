package main.utils.Password;

import main.model.user.User;
import main.repository.UserRepository;

/**
 * Service class for handling password-related operations for users.
 * <p>
 * This class provides methods to validate and change user passwords according to a specified
 * {@link PasswordPolicy}, and persists changes using the provided {@link UserRepository}.
 * </p>
 */
public class PasswordService {
    private final UserRepository userRepository;
    private final PasswordPolicy passwordPolicy;

    /**
     * Constructs a {@code PasswordService} with the given user repository and password policy.
     *
     * @param userRepository  the repository used to update user data
     * @param passwordPolicy  the policy used to validate new passwords
     */
    public PasswordService(UserRepository userRepository, PasswordPolicy passwordPolicy) {
        this.userRepository = userRepository;
        this.passwordPolicy = passwordPolicy;
    }

    /**
     * Attempts to change the password for the specified user.
     * <p>
     * The password change will only succeed if:
     * <ul>
     *   <li>The new password and confirmation password match</li>
     *   <li>The current password matches the user's existing password</li>
     *   <li>The new password satisfies the configured password policy</li>
     * </ul>
     * If all checks pass, the user's password is updated and persisted.
     * </p>
     *
     * @param user             the {@link User} whose password is to be changed
     * @param currentPassword  the user's current password
     * @param newPassword      the new password to set
     * @param confirmPassword  the confirmation of the new password
     * @return {@code true} if the password was successfully changed and persisted,
     *         {@code false} otherwise
     */
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

    /**
     * Returns the {@link PasswordPolicy} used by this service.
     *
     * @return the password policy
     */
    public PasswordPolicy getPasswordPolicy() {
        return passwordPolicy;
    }
}
