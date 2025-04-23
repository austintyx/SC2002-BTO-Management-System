package main.boundary;

import java.util.List;
import main.model.enquiry.Enquiry;
import main.model.project.Project;
import main.model.user.User;
import main.repository.UserRepository;
import main.utils.ConsoleUtils;
import main.utils.DateUtils;

/**
 * Boundary class for displaying enquiry information.
 * Centralizes all enquiry viewing functionality across different user interfaces.
 * Uses project name as the unique project identifier.
 * 
 * @author Your Team
 * @version 1.1
 */
public class EnquiriesViewer {

    /**
     * Displays a list of enquiries in a formatted table.
     * @param enquiries List of Enquiry objects to display
     * @param userRepository UserRepository for applicant name lookup
     */
    public static void displayEnquiries(List<Enquiry> enquiries, UserRepository userRepository) {

        int[] widths = {20, 30, 15, 10, 15};
        System.out.println("\n");
        String[] headers = {"Enquiry ID", "Enquiry Text", "Date", "Replied", "Applicant"};
        ConsoleUtils.displayTableHeader(headers, widths);

        for (Enquiry enquiry : enquiries) {
            String applicantName = getApplicantName(enquiry.getApplicantId(), userRepository);
            String[] values = {
                enquiry.getEnquiryId(),
                enquiry.getEnquiryText(),
                DateUtils.formatDate(enquiry.getEnquiryDate()),
                enquiry.hasReply() ? "Yes" : "No",
                applicantName
            };
            ConsoleUtils.displayTableRow(values, widths);
        }
    }

    /**
     * Displays unanswered enquiries in a formatted table.
     * @param enquiries List of Enquiry objects to display
     * @param userRepository UserRepository for applicant name lookup
     */
    public static void displayUnansweredEnquiries(List<Enquiry> enquiries, UserRepository userRepository) {
        if (enquiries == null || enquiries.isEmpty()) {
            System.out.println("No enquiries available.");
            return;
        }

        System.out.println("\n===== Unanswered Enquiries =====");
        List<Enquiry> unansweredEnquiries = new java.util.ArrayList<>();
        for (Enquiry enquiry : enquiries) {
            if (!enquiry.hasReply()) {
                unansweredEnquiries.add(enquiry);
            }
        }

        if (unansweredEnquiries.isEmpty()) {
            System.out.println("No unanswered enquiries found.");
            return;
        }

        displayEnquiries(unansweredEnquiries, userRepository);
    }

    /**
     * Displays detailed information about a specific enquiry.
     * @param enquiry The Enquiry object to display
     * @param project The associated Project object
     * @param userRepository UserRepository for applicant name lookup
     */
    public static void displayEnquiryDetails(Enquiry enquiry, Project project, UserRepository userRepository) {
        if (enquiry == null) {
            System.out.println("Enquiry not found.");
            return;
        }

        String applicantName = getApplicantName(enquiry.getApplicantId(), userRepository);

        System.out.println("\n===== Enquiry Details =====");
        System.out.println("Enquiry ID: " + enquiry.getEnquiryId());
        System.out.println("Applicant: " + applicantName);

        if (project != null) {
            System.out.println("Project: " + project.getProjectName() + " (" + project.getNeighborhood() + ")");
        } else {
            System.out.println("Project: " + enquiry.getProjectName());
        }

        System.out.println("Date: " + DateUtils.formatDate(enquiry.getEnquiryDate()));
        System.out.println("\nEnquiry:");
        System.out.println(enquiry.getEnquiryText());

        if (enquiry.hasReply()) {
            System.out.println("\nReply from: " + enquiry.getResponderName());
            System.out.println("Reply Date: " + DateUtils.formatDate(enquiry.getReplyDate()));
            System.out.println("Reply:");
            System.out.println(enquiry.getReplyText());
        } else {
            System.out.println("\nNo reply yet.");
        }
    }

    /**
     * Displays enquiries for a specific project.
     * @param enquiries List of Enquiry objects to display
     * @param project The Project object associated with the enquiries
     * @param userRepository UserRepository for applicant name lookup
     */
    public static void displayProjectEnquiries(List<Enquiry> enquiries, Project project, UserRepository userRepository) {
        if (enquiries == null || enquiries.isEmpty()) {
            System.out.println("No enquiries available for this project.");
            return;
        }
        System.out.println("\n===== Enquiries for Project: " + project.getProjectName() + " =====");
        displayEnquiries(enquiries, userRepository);
    }

    /**
     * Displays enquiries for a specific applicant.
     * @param enquiries List of Enquiry objects to display
     * @param applicantNRIC The NRIC of the applicant
     * @param userRepository UserRepository for applicant name lookup
     */
    public static void displayApplicantEnquiries(List<Enquiry> enquiries, String applicantNRIC, UserRepository userRepository) {
        if (enquiries == null || enquiries.isEmpty()) {
            System.out.println("No enquiries available for this applicant.");
            return;
        }
        System.out.println("\n===== Enquiries for Applicant: " + applicantNRIC + " =====");
        displayEnquiries(enquiries, userRepository);
    }

    /**
     * Displays a form for replying to an enquiry.
     * @param enquiry The Enquiry object to reply to
     * @param userRepository UserRepository for applicant name lookup
     */
    public static void displayReplyForm(Enquiry enquiry, UserRepository userRepository) {
        if (enquiry == null) {
            System.out.println("Enquiry not found.");
            return;
        }

        String applicantName = getApplicantName(enquiry.getApplicantId(), userRepository);

        System.out.println("\n===== Reply to Enquiry =====");
        System.out.println("Enquiry ID: " + enquiry.getEnquiryId());
        System.out.println("From: " + applicantName);
        System.out.println("Date: " + DateUtils.formatDate(enquiry.getEnquiryDate()));
        System.out.println("\nEnquiry:");
        System.out.println(enquiry.getEnquiryText());

        if (enquiry.hasReply()) {
            System.out.println("\nThis enquiry has already been replied to.");
            System.out.println("Reply from: " + enquiry.getResponderName());
            System.out.println("Reply Date: " + DateUtils.formatDate(enquiry.getReplyDate()));
            System.out.println("Reply:");
            System.out.println(enquiry.getReplyText());
        } else {
            System.out.println("\nEnter your reply:");
        }
    }

    /**
     * Helper method to get applicant name from UserRepository using NRIC.
     * @param applicantId The NRIC of the applicant
     * @param userRepository The user repository for lookup
     * @return Applicant's name if found, else NRIC
     */
    private static String getApplicantName(String applicantId, UserRepository userRepository) {
        User user = userRepository.findById(applicantId);
        return user != null ? user.getName() : applicantId;
    }
}
