package main.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for file operations
 * @author Your Team
 * @version 1.0
 */
public class FileUtils {
    /**
     * Ensures that the data directory exists
     * @return true if the directory exists or was created, false otherwise
     */
    public static boolean ensureDataDirectoryExists() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            return dataDir.mkdir();
        }
        return true;
    }
    
    /**
     * Serializes an object to a file
     * @param obj The object to serialize
     * @param filePath The file path
     * @return true if serialization is successful, false otherwise
     */
    public static boolean serializeObject(Object obj, String filePath) {
        ensureDataDirectoryExists();
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(obj);
            return true;
        } catch (IOException e) {
            System.out.println("Error serializing object: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Deserializes an object from a file
     * @param filePath The file path
     * @return The deserialized object, or null if deserialization fails
     */
    public static Object deserializeObject(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error deserializing object: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Reads lines from a text file
     * @param filePath The file path
     * @return A list of lines from the file, or an empty list if reading fails
     */
    public static List<String> readLines(String filePath) {
        List<String> lines = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        
        return lines;
    }
    
    /**
     * Writes lines to a text file
     * @param lines The lines to write
     * @param filePath The file path
     * @return true if writing is successful, false otherwise
     */
    public static boolean writeLines(List<String> lines, String filePath) {
        ensureDataDirectoryExists();
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
            return false;
        }
    }
}
