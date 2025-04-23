package main.utils;

import main.repository.UserRepository;
import main.model.user.User;

public interface UserLookupService {
    default String lookupUserNameByNRIC(UserRepository userRepository, String nric) {
        User user = userRepository.findByNRIC(nric.trim());
        if (user == null) {
            throw new IllegalArgumentException("User not found for NRIC: " + nric);
        }
        return user.getName();
    }

    // New age lookup method
    default int lookupAgeByNRIC(UserRepository userRepository, String nric) {
        User user = userRepository.findByNRIC(nric.trim());
        if (user == null) {
            throw new IllegalArgumentException("User not found for NRIC: " + nric);
        }
        return user.getAge();  // Assumes User has getAge() method
    }

    // New marital status lookup method 
    default String lookupMaritalStatusByNRIC(UserRepository userRepository, String nric) {
        User user = userRepository.findByNRIC(nric.trim());
        if (user == null) {
            throw new IllegalArgumentException("User not found for NRIC: " + nric);
        }
        return user.getMaritalStatus().toString();  // Assumes User has getMaritalStatus() method
    }
}
