package main.model.project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a Build-To-Order (BTO) project in the BTO Management System.
 * A project contains information such as its name, neighborhood, flat types,
 * application period, assigned manager, officer slots, and related applications and enquiries.
 * 
 * @author Your Name
 * @version 1.0
 * @since 2025-04-16
 */
public class Project implements Serializable {
    private static final long serialVersionUID = 1L;
    
    
    /**
     * The name of this project.
     */
    private String projectName;
    
    /**
     * The neighborhood where this project is located.
     */
    private Neighborhood neighborhood;
    
    /**
     * Maps flat type (e.g., "2-Room", "3-Room") to the total number of units.
     */
    private Map<String, Integer> flatTypes;

     /**
     * Maps flat type (e.g., "2-Room") to its price.
     */
    private Map<String, Integer> flatPrices;
    
    /**
     * Maps flat type to the number of remaining units available.
     */
    private Map<String, Integer> remainingFlats;
    
    /**
     * The application opening date for this project.
     */
    private Date appOpenDate;
    
    /**
     * The application closing date for this project.
     */
    private Date appCloseDate;
    
    /**
     * The NRIC of the HDB Manager in charge of this project.
     */
    private String managerInCharge;

    private String managerDisplayName; // New field for display name
    
    /**
     * The total number of HDB Officer slots available for this project.
     */
    private int officerSlots;
    
    /**
     * The number of remaining HDB Officer slots available.
     */
    private int remainingOfficerSlots;
    
    /**
     * The list of NRICs of HDB Officers assigned to this project.
     */
    private Map<String, String> officerDetails; // Maps NRIC -> Name
    
    /**
     * Indicates whether this project is visible to applicants.
     */
    private boolean visible;
    
    /**
     * The list of application IDs associated with this project.
     */
    private List<String> applications;
    
    /**
     * The list of enquiry IDs associated with this project.
     */
    private List<String> enquiries;

    
    
    /**
     * Constructs a new Project with the specified details.
     *
     * @param projectName      The name of the project.
     * @param neighborhood     The neighborhood where the project is located.
     * @param flatTypes        The mapping of flat types to total units.
     * @param appOpenDate      The application opening date.
     * @param appCloseDate     The application closing date.
     * @param managerInCharge  The NRIC of the HDB Manager in charge.
     * @param managerDisplayName  The name of the HDB Manager in charge.
     * @param officerSlots     The total number of officer slots.
     */
    public Project(String projectName, Neighborhood neighborhood,
                  Map<String, Integer> flatUnits, Map<String, Integer> flatPrices,
                  Date openingDate, Date closingDate, 
                  String managerNRIC, String managerDisplayName, int officerSlots) {
        this.projectName = projectName;
        this.neighborhood = neighborhood;
        this.flatTypes = new HashMap<>(flatUnits);
        this.flatPrices = new HashMap<>(flatPrices);
        this.appOpenDate = openingDate;
        this.appCloseDate = closingDate;
        this.managerInCharge = managerNRIC;          // Store NRIC
        this.managerDisplayName = managerDisplayName;       // Store name
        this.officerSlots = officerSlots;
        this.remainingFlats = flatUnits != null ? new HashMap<>(flatUnits) : new HashMap<>();
        // Other initialization
        this.officerDetails = new HashMap<>();

    this.remainingOfficerSlots = officerSlots;
    }

    /**
     * Returns the name of this project.
     * @return the project name
     */
    public String getProjectName() {
        return projectName;
    }
    
    /**
     * Sets the name of this project.
     * @param projectName the new project name
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    
    /**
     * Returns the neighborhood of this project.
     * @return the neighborhood
     */
    public Neighborhood getNeighborhood() {
        return neighborhood;
    }
    
    /**
     * Sets the neighborhood of this project.
     * @param neighborhood the new neighborhood
     */
    public void setNeighborhood(Neighborhood neighborhood) {
        this.neighborhood = neighborhood;
    }
    
    /**
     * Returns a copy of the map of flat types and their total units.
     * @return a map of flat types to total units
     */
    public Map<String, Integer> getFlatTypes() {
        return new HashMap<>(flatTypes);
    }
    
    /**
     * Sets the flat types and their total units for this project.
     * Also resets the remaining flats to the new totals.
     * @param flatTypes the new map of flat types to total units
     */
    public void setFlatTypes(Map<String, Integer> flatTypes) {
        this.flatTypes = new HashMap<>(flatTypes);
        this.remainingFlats = new HashMap<>(flatTypes);
    }
    
    // Add getter for prices
    public Map<String, Integer> getFlatPrices() {
        return new HashMap<>(flatPrices);
    }

    // Add setter for prices
    public void setFlatPrices(Map<String, Integer> flatPrices) {
        this.flatPrices = flatPrices;
    }
    
    /**
     * Returns a copy of the map of flat types and their remaining units.
     * @return a map of flat types to remaining units
     */
    public Map<String, Integer> getRemainingFlats() {
        return new HashMap<>(remainingFlats);
    }
    
    /**
     * Updates the remaining units for a specific flat type.
     * @param flatType the flat type to update
     * @param count the new remaining count
     */
    public void updateRemainingFlats(String flatType, int newCount) {
        remainingFlats.put(flatType, newCount); // Direct update without existence check
    }
    
    /**
     * Returns the application opening date for this project.
     * @return the application opening date
     */
    public Date getOpeningDate() {
        return appOpenDate;
    }
    
    /**
     * Sets the application opening date for this project.
     * @param appOpenDate the new opening date
     */
    public void setOpeningDate(Date appOpenDate) {
        this.appOpenDate = appOpenDate;
    }
    
    /**
     * Returns the application closing date for this project.
     * @return the application closing date
     */
    public Date getClosingDate() {
        return appCloseDate;
    }
    
    /**
     * Sets the application closing date for this project.
     * @param appCloseDate the new closing date
     */
    public void setClosingDate(Date appCloseDate) {
        this.appCloseDate = appCloseDate;
    }
    
    /**
     * Returns the NRIC of the HDB Manager in charge of this project.
     * @return the manager's NRIC
     */
    public String getManagerInCharge() {
        return managerInCharge;
    }

    /**
     * Sets the NRIC of the HDB Manager in charge of this project.
     * @param managerInCharge the new HDB Manager NRIC
     */
    public void setManagerInCharge(String managerInCharge) {
        this.managerInCharge = managerInCharge;
    }

     // Add getter for display name
     public String getManagerDisplayName() {
        return managerDisplayName;
    }

    
    /**
     * Returns the total number of HDB Officer slots for this project.
     * @return the total officer slots
     */
    public int getOfficerSlots() {
        return officerSlots;
    }
    
    /**
     * Sets the total number of HDB Officer slots for this project.
     * Updates the remaining officer slots accordingly.
     * @param officerSlots the new total officer slots
     */
    public void setOfficerSlots(int officerSlots) {
        this.officerSlots = officerSlots;
        if (this.officerDetails == null) {
            this.officerDetails = new HashMap<>();
        }
        this.remainingOfficerSlots = officerSlots - this.officerDetails.size();
    }
    
    
    /**
     * Returns the number of remaining HDB Officer slots.
     * @return the remaining officer slots
     */
    public int getRemainingOfficerSlots() {
        return remainingOfficerSlots;
    }
    
    /**
     * Returns a copy of the list of NRICs of officers assigned to this project.
     * @return a list of officer NRICs
     */
    public List<String> getOfficers() {
        return new ArrayList<>(officerDetails.keySet());
    }
    
    
    /**
     * Adds an officer to the project with both NRIC and name.
     * @param officerNRIC NRIC of the officer
     * @param officerName Name of the officer
     * @return true if officer was added, false if slots are full
     */
    public boolean addOfficer(String officerNRIC, String officerName) {
        if (remainingOfficerSlots <= 0 || officerDetails.containsKey(officerNRIC)) {
            return false;
        }
        
        officerDetails.put(officerNRIC, officerName);
        remainingOfficerSlots--;
        return true;
    }

    /**
     * Adds an officer to the project with NRIC only.
     * @param officerNRIC NRIC of the officer
     * @return true if officer was added, false if slots are full
     */
    public boolean addOfficer(String officerNRIC) {
        // Check if officer already exists or no slots left
        if (remainingOfficerSlots <= 0 || officerDetails.containsKey(officerNRIC)) {
            return false;
        }
        
        // Add officer with default name "Unknown"
        officerDetails.put(officerNRIC, "Unknown");
        remainingOfficerSlots--;
        return true;
    }
    
    
     /**
     * Removes an officer from the project.
     * @param officerNRIC NRIC of the officer to remove
     * @return true if officer was removed, false otherwise
     */
    public boolean removeOfficer(String officerNRIC) {
        if (officerDetails.remove(officerNRIC) != null) {
            remainingOfficerSlots++;
            return true;
        }
        return false;
    }

    /**
     * Gets the full officer details map.
     * @return Map from officer NRICs to names
     */
    public Map<String, String> getOfficerDetails() {
        return new HashMap<>(officerDetails); // Return a copy for encapsulation
    }
    
    /**
     * Gets the name of an officer by NRIC.
     * @param officerNRIC Officer's NRIC
     * @return Officer's name or null if not found
     */
    public String getOfficerName(String officerNRIC) {
        return officerDetails.get(officerNRIC);
    }
    
    /**
     * Returns whether this project is visible to applicants.
     * @return true if visible, false otherwise
     */
    public boolean isVisible() {
        return visible;
    }
    
    /**
     * Sets the visibility of this project to applicants.
     * @param visible true to make the project visible, false otherwise
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    /**
     * Adds an application to this project.
     * @param applicationId the ID of the application to add
     */
    public void addApplication(String applicationId) {
        applications.add(applicationId);
    }
    
    /**
     * Returns a copy of the list of application IDs associated with this project.
     * @return a list of application IDs
     */
    public List<String> getApplications() {
        return new ArrayList<>(applications);
    }
    
    /**
     * Removes an application from this project.
     * @param applicationId the ID of the application to remove
     */
    public void removeApplication(String applicationId) {
        applications.remove(applicationId);
    }
    
    /**
     * Adds an enquiry to this project.
     * @param enquiryId the ID of the enquiry to add
     */
    public void addEnquiry(String enquiryId) {
        enquiries.add(enquiryId);
    }
    
    /**
     * Returns a copy of the list of enquiry IDs associated with this project.
     * @return a list of enquiry IDs
     */
    public List<String> getEnquiries() {
        return new ArrayList<>(enquiries);
    }
    
    /**
     * Removes an enquiry from this project.
     * @param enquiryId the ID of the enquiry to remove
     */
    public void removeEnquiry(String enquiryId) {
        enquiries.remove(enquiryId);
    }
    
    /**
     * Decrements the remaining flat count for a given flat type if available.
     * @param flatType the flat type to decrement
     * @return true if the count was decremented, false otherwise
     */
    public boolean decrementFlatCount(String flatType) {
        if (remainingFlats.containsKey(flatType) && remainingFlats.get(flatType) > 0) {
            remainingFlats.put(flatType, remainingFlats.get(flatType) - 1);
            return true;
        }
        return false;
    }
    
    /**
     * Increments the remaining flat count for a given flat type if not exceeding the total.
     * @param flatType the flat type to increment
     * @return true if the count was incremented, false otherwise
     */
    public boolean incrementFlatCount(String flatType) {
        if (remainingFlats.containsKey(flatType) && 
            remainingFlats.get(flatType) < flatTypes.get(flatType)) {
            remainingFlats.put(flatType, remainingFlats.get(flatType) + 1);
            return true;
        }
        return false;
    }
    
    /**
     * Checks if the project is currently open for application.
     * @return true if the current date is within the application period, false otherwise
     */
    public boolean isOpen() {
        Date now = new Date();
        return now.after(appOpenDate) && now.before(appCloseDate);
    }

    
}
