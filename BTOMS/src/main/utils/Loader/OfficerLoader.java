package main.utils.Loader;

import main.model.user.HDBOfficer;
import main.model.user.MaritalStatus;
import main.utils.CsvUtils;

import java.io.IOException;
import java.util.*;

public class OfficerLoader {
    public static List<HDBOfficer> loadFromCsv(String fileName) throws IOException {
        List<HDBOfficer> officers = new ArrayList<>();
        List<String[]> rows = CsvUtils.readCsv(fileName);
        
        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);
            HDBOfficer officer = new HDBOfficer(
                row[1].trim(), // NRIC
                row[0].trim(), // Name
                row[4], // Password
                Integer.parseInt(row[2]), // Age
                MaritalStatus.valueOf(row[3].toUpperCase())
            );
            officers.add(officer);
        }
        return officers;
    }
}
