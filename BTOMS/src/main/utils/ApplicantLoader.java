package main.utils;

import main.model.user.Applicant;
import main.model.user.MaritalStatus;
import java.io.IOException;
import java.util.*;

public class ApplicantLoader {
    public static List<Applicant> loadFromCsv(String fileName) throws IOException {
        List<Applicant> applicants = new ArrayList<>();
        List<String[]> rows = CsvUtils.readCsv(fileName);
        
        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            Applicant applicant = new Applicant(
                row[1], // NRIC
                row[0], // Name
                row[4], // Password
                Integer.parseInt(row[2]), // Age
                MaritalStatus.valueOf(row[3].toUpperCase())
            );
            applicants.add(applicant);
        }
        return applicants;
    }
}
