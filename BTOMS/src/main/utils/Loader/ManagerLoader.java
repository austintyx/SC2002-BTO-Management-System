package main.utils.Loader;

import main.model.user.HDBManager;
import main.model.user.MaritalStatus;
import main.utils.CsvUtils;

import java.io.IOException;
import java.util.*;

/**
 * Utility class for loading {@link HDBManager} objects from a CSV file.
 * <p>
 * This class provides a method to parse a CSV file and construct a list of {@code HDBManager}
 * instances based on the file's contents.
 * </p>
 */
public class ManagerLoader {

    /**
     * Loads a list of {@link HDBManager} objects from the specified CSV file.
     * <p>
     * The CSV file is expected to have the following columns (in order):
     * <ul>
     *     <li>Name</li>
     *     <li>NRIC</li>
     *     <li>Age</li>
     *     <li>Marital Status</li>
     *     <li>Password</li>
     * </ul>
     * The first row is assumed to be a header and is skipped.
     * </p>
     *
     * @param fileName the path to the CSV file
     * @return a list of {@link HDBManager} objects loaded from the file
     * @throws IOException if an I/O error occurs while reading the file
     * @throws NumberFormatException if the age column cannot be parsed as an integer
     * @throws IllegalArgumentException if the marital status is invalid
     */
    public static List<HDBManager> loadFromCsv(String fileName) throws IOException {
        List<HDBManager> managers = new ArrayList<>();
        List<String[]> rows = CsvUtils.readCsv(fileName);
        
        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            HDBManager manager = new HDBManager(
                row[1].trim(), // NRIC
                row[0].trim(), // Name
                row[4], // Password
                Integer.parseInt(row[2]), // Age
                MaritalStatus.valueOf(row[3].toUpperCase())
            );
            managers.add(manager);
        }
        return managers;
    }
}
