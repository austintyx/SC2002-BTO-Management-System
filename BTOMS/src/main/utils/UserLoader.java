package main.utils;

import main.model.user.*;
import java.io.IOException;
import java.util.*;

public class UserLoader {
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
