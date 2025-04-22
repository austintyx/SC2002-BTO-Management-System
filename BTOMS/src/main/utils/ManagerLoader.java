package main.utils;

import main.model.user.HDBManager;
import main.model.user.MaritalStatus;
import java.io.IOException;
import java.util.*;

public class ManagerLoader {
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
