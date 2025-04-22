package main.utils;

import java.io.Console;
import java.util.Scanner;

/**
 * Utility class for console input and validation.
 * Provides methods for reading and validating user input from the console.
 * Used by boundary classes such as HDBManagerUI.
 * 
 * @author Your Name
 * @version 1.0
 * @since 2025-04-17
 */
public class ConsoleUtils {
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Reads an integer from the user within a specified range, with validation.
     * @param prompt The prompt to display to the user.
     * @param errorMessage The error message to display for invalid input.
     * @param min The minimum acceptable value (inclusive).
     * @param max The maximum acceptable value (inclusive).
     * @return The validated integer input.
     */
    public static int readIntWithValidation(String prompt, String errorMessage, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.println(errorMessage);
                }
            } catch (NumberFormatException e) {
                System.out.println(errorMessage);
            }
        }
    }

    /**
     * Reads a non-empty string from the user.
     * @param prompt The prompt to display.
     * @return The non-empty string entered by the user.
     */
    public static String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            } else {
                System.out.println("Input cannot be empty. Please try again.");
            }
        }
    }

    /**
     * Reads an optional string input from the user (may be empty).
     * @param prompt The prompt to display.
     * @return The string entered by the user (may be empty).
     */
    public static String readOptionalInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * Asks the user to confirm an action (Y/N).
     * @param prompt The confirmation prompt.
     * @return true if the user confirms (Y/Yes), false otherwise (N/No).
     */
    public static boolean confirmAction(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim().toLowerCase();
            if (value.equals("y") || value.equals("yes")) {
                return true;
            } else if (value.equals("n") || value.equals("no")) {
                return false;
            } else {
                System.out.println("Please enter Y or N.");
            }
        }
    }

    /**
     * Prompts the user to press Enter to continue.
     * @param message The message to display (default: "Press Enter to continue...").
     */
    public static void pressEnterToContinue(String message) {
        System.out.print(message);
        scanner.nextLine();
    }

    /**
     * Prompts the user to press Enter to continue (default message).
     */
    public static void pressEnterToContinue() {
        pressEnterToContinue("Press Enter to continue...");
    }

    /**
     * Displays a menu with numbered options and prompts the user for a choice.
     * Returns the index (starting from 0) of the selected option, or -1 if cancelled.
     *
     * @param title The menu title to display.
     * @param options The list of options to display.
     * @return The index of the selected option (0-based), or -1 if user cancels.
     */
    public static int displayMenu(String title, String[] options) {
        if (options == null || options.length == 0) {
            System.out.println("No options available.");
            return -1;
        }
        System.out.println("\n=== " + title + " ===");
        for (int i = 0; i < options.length; i++) {
            System.out.printf("%d. %s\n", i + 1, options[i]);
        }
        System.out.println("0. Cancel");

        while (true) {
            System.out.print("Enter your choice: ");
            String input = scanner.nextLine().trim();
            try {
                int choice = Integer.parseInt(input);
                if (choice == 0) {
                    return -1; // User chose to cancel
                }
                if (choice >= 1 && choice <= options.length) {
                    return choice - 1; // Return 0-based index
                }
                System.out.println("Invalid choice. Please enter a number between 1 and " + options.length + ", or 0 to cancel.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    /**
     * Displays a table header with column names and specified column widths.
     * @param headers Array of column header names.
     * @param widths Array of column widths (should match headers.length).
     */
    public static void displayTableHeader(String[] headers, int[] widths) {
        if (headers == null || widths == null || headers.length != widths.length) {
            throw new IllegalArgumentException("Headers and widths must be non-null and of same length.");
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < headers.length; i++) {
            sb.append(String.format("%-" + widths[i] + "s", headers[i]));
            if (i < headers.length - 1) sb.append(" ");
        }
        System.out.println(sb.toString());
        // Print separator line
        System.out.println("-".repeat(sb.length()));
    }

    /**
     * Displays a table row with values and specified column widths.
     * @param values Array of values for the row.
     * @param widths Array of column widths (should match values.length).
     */
    public static void displayTableRow(String[] values, int[] widths) {
        if (values == null || widths == null || values.length != widths.length) {
            throw new IllegalArgumentException("Values and widths must be non-null and of same length.");
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            sb.append(String.format("%-" + widths[i] + "s", values[i]));
            if (i < values.length - 1) sb.append(" ");
        }
        System.out.println(sb.toString());
    }

    public static String readPasswordMasked(String prompt) {
    Console console = System.console();
    if (console != null) {
        char[] passwordChars = console.readPassword(prompt);
        return new String(passwordChars);
    } else {
        System.out.print(prompt + " (input not hidden in IDE): ");
        return new Scanner(System.in).nextLine();
    }
}

/**
     * Reads non-empty input from user with a custom prompt
     * @param prompt The message to display to the user
     * @return Non-empty string input
     */
    public static String readNonEmptyInput(String prompt) {
        String input;
        do {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Input cannot be empty. Please try again.");
            }
        } while (input.isEmpty());
        return input;
    }

}
