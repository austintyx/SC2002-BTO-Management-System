package main.utils.Loader;

import main.model.user.*;
import main.utils.CsvUtils;

import java.io.IOException;
import java.util.*;

/**
 * Utility class for loading {@link User} objects of various types from a CSV file.
 * <p>
 * This class provides a method to parse a CSV file and construct a list of {@code User}
 * instances (including {@link Applicant}, {@link HDBOfficer}, and {@link HDBManager}) based on the file's contents.
 * </p>
 */
public class UserLoader {

    /**
     * Loads a list of {@link User} objects from the specified CSV file.
     * <p>
     * The CSV file is expected to have the following columns (in order):
     * <ul>
     *     <li>User Type (e.g., "APPLICANT", "HDB_OFFICER", "HDB_MANAGER")</li>
     *     <li>NRIC</li>
     *     <li>Name</li>
     *     <li>Age</li>
     *     <li>Marital Status</li>
     *     <li>Password</li>
     * </ul>
     * The first row is assumed to be a header and is skipped.
     * If a row cannot be parsed, an error is printed and the row is skipped.
     * </p>
     *
     * @param fileName the path to the CSV file
     * @return a list of {@link User} objects loaded from the file
     * @throws IOException if an I/O error occurs while reading the file
     */
    public static List<User> loadFromCsv(String fileName) throws IOException {
        List<User> users = new ArrayList<>();
        List<String[]> rows = CsvUtils.readCsv(fileName);

        // Skip header row
        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            try {
                User user = parseUserRow(row);
                if (user != null) users.add(user);
            } catch (Exception e) {
                System.err.println("Error parsing user row " + (i + 1) + ": " + e.getMessage());
            }
        }
        return users;
    }

    /**
     * Parses a single row from the CSV file and constructs the appropriate {@link User} subclass.
     *
     * @param row an array of strings representing a single row from the CSV file
     * @return a {@link User} object corresponding to the row data, or {@code null} if the user type is unknown
     * @throws Exception if the row data is invalid or cannot be parsed
     */
    private static User parseUserRow(String[] row) throws Exception {
        String userType = row[0].trim().toUpperCase();
        String nric = row[1].trim();
        String name = row[2].trim();
        int age = Integer.parseInt(row[3].trim());
        MaritalStatus status = MaritalStatus.valueOf(row[4].trim().toUpperCase());
        String password = row[5].trim();

        switch (userType) {
            case "APPLICANT":
                return new Applicant(nric, name, password, age, status);
            case "HDB_OFFICER":
                return new HDBOfficer(nric, name, password, age, status);
            case "HDB_MANAGER":
                return new HDBManager(nric, name, password, age, status);
            default:
                throw new IllegalArgumentException("Unknown user type: " + userType);
        }
    }
}
