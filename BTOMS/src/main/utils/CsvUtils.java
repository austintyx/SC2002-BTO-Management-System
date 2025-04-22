package main.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CsvUtils {
    /**
     * Reads a CSV file from the classpath and returns its lines as a list of string arrays.
     * @param fileName Name of the CSV file (e.g., "ProjectList.csv")
     * @return List of String arrays, each representing a row
     */
    public static List<String[]> readCsv(String fileName) throws IOException {
        List<String[]> records = new ArrayList<>();
        
        // Check if resource exists
        
        InputStream is = CsvUtils.class.getClassLoader().getResourceAsStream(fileName);
        if (is == null) {
            throw new IOException("File not found in resources: " + fileName);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = parseCsvLine(line);
                records.add(values);
            }
        }
        return records;
    }

    /**
     * Parses a CSV line, handling quoted values.
     */
    private static String[] parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder buffer = new StringBuilder();

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                values.add(buffer.toString().trim());
                buffer.setLength(0);
            } else {
                buffer.append(c);
            }
        }
        values.add(buffer.toString().trim());
        return values.toArray(new String[0]);
    }
}
