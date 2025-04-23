package main.utils;

import main.repository.UserRepository;
import main.model.user.User;

/**
 * Service interface for looking up user information based on NRIC.
 * <p>
 * Provides default methods to retrieve a user's name, age, and marital status
 * from a {@link UserRepository} using the user's NRIC.
 * </p>
 */
public interface UserLookupService {

    /**
     * Looks up the name of a user by their NRIC.
     *
     * @param userRepository the repository to search for the user
     * @param nric the NRIC of the user to look up
     * @return the name of the user
     * @throws IllegalArgumentException if no user is found for the given NRIC
     */
    default String lookupUserNameByNRIC(UserRepository userRepository, String nric) {
        User user = userRepository.findByNRIC(nric.trim());
        if (user == null) {
            throw new IllegalArgumentException("User not found for NRIC: " + nric);
        }
        return user.getName();
    }

    /**
     * Looks up the age of a user by their NRIC.
     *
     * @param userRepository the repository to search for the user
     * @param nric the NRIC of the user to look up
     * @return the age of the user
     * @throws IllegalArgumentException if no user is found for the given NRIC
     */
    default int lookupAgeByNRIC(UserRepository userRepository, String nric) {
        User user = userRepository.findByNRIC(nric.trim());
        if (user == null) {
            throw new IllegalArgumentException("User not found for NRIC: " + nric);
        }
        return user.getAge();  // Assumes User has getAge() method
    }

    /**
     * Looks up the marital status of a user by their NRIC.
     *
     * @param userRepository the repository to search for the user
     * @param nric the NRIC of the user to look up
     * @return the marital status of the user as a string
     * @throws IllegalArgumentException if no user is found for the given NRIC
     */
    default String lookupMaritalStatusByNRIC(UserRepository userRepository, String nric) {
        User user = userRepository.findByNRIC(nric.trim());
        if (user == null) {
            throw new IllegalArgumentException("User not found for NRIC: " + nric);
        }
        return user.getMaritalStatus().toString();  // Assumes User has getMaritalStatus() method
    }
}
