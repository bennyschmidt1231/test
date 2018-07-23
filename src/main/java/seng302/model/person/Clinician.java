package seng302.model.person;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import seng302.model.UserAttributeCollection;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Clinician extends User {

    public static String DEFAULT = "0";

    /**
     * A container for the log of the last error message for an attempted update performed on the clinician.
     */
    private String updateMessage = "waiting for new message";


    /**
     * The constructor method for a Clinician (when creating a new Clinician).
     * @param givenName The given name of the Clinician.
     * @param lastName The last name of the Clinician.
     * @param staffID The staff ID of the Clinician.
     * @param workAddress The work address of the Clinician.
     * @param region The region of the Clinician.
     * @param password The password of the Clinician.
     * @param modifications modifications of the Clinician
     * @param creationDate creation date of the Clincian
     */
    public Clinician(String givenName,
                     String lastName,
                     String workAddress,
                     String region,
                     String staffID,
                     String password,
                     LocalDateTime creationDate,
                     ArrayList<LogEntry> modifications) {

        super(givenName, null,lastName, new ContactDetails(
                new Address(workAddress, null, null, null, region, null, null)
                ,null,null,null), staffID, password, creationDate, modifications);

    }


    public String getUpdateMessage() {
        return updateMessage;
    }


    public void setUpdateMessage(String updateMessage) {
        this.updateMessage = updateMessage;
    }

   /**
     * The constructor method for a Clinician (when creating a new Clinician) using json.
     * @param givenName The given name of the Clinician.
     * @param middleName The middle name of the Clinician.
     * @param lastName The last name of the Clinician.
     * @param contacts The contact details of the Clinician.
     * @param staffID The staff ID of the Clinician.
     * @param password The password of the Clinician.
     * @param creationDate the date of creation for this clinician, a LocalDate object.
     * @param modifications An array list of LocalDate objects that record modifications to the clinician.
     */
    @JsonCreator
    public Clinician(
            //inherited from Person
            @JsonProperty("givenName") String givenName,
            @JsonProperty("middleName") String middleName,
            @JsonProperty("lastName") String lastName,
            @JsonProperty("contactDetails") ContactDetails contacts,

            //inherited from User
            @JsonProperty("username") String staffID,
            @JsonProperty("password") String password,
            @JsonProperty("creationDate") LocalDateTime creationDate,
            @JsonProperty("modifications") ArrayList<LogEntry> modifications)
    {
        super(givenName, middleName, lastName, contacts, staffID, password,  creationDate, modifications);
    }


    /**
     * Checks if the given password string is alphanumeric and is between 6 and 30 characters (inclusive) long.
     * @param password password to check
     * @return boolean of password orrect format
     */
    public static boolean validatePassword(String password) {
        try {
            String regularExpression = "^[a-zA-Z0-9]{6,30}$";
            Pattern pattern = Pattern.compile(regularExpression);
            Matcher matcher = pattern.matcher(password);
            return matcher.matches();
        } catch (NullPointerException e) {
            return false;
        }
    }


    /**
     * Checks if the given id can be successfully parsed as an integer and is greater than zero.
     * @param id A string representation of an integer.
     * @return Returns 'true' if the given id can be parsed as an int and is greater than zero, returns 'false' otherwise.
     */
    public static boolean validateStaffIDIsInt(String id) {
        try {
            int i = Integer.parseInt(id);
            return i > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    /**
     * Updates the clinician's given name if the value is valid and logs the result.
     * @param value A string of the clinician's new given name.
     * @return A string log of the result of the operation.
     */
    public String updateGivenName(String value) {
        String message = "ERROR: Invalid value " + value + ". Given name must be between 1 and 50 alphabetical characters." +
                " Spaces, commas, apostrophes, and dashes are also allowed.\n";
        if (UserAttributeCollection.validateAlphanumericString(false, value, 1, 50)) {

            if(getFirstName() != null) { message = "updated"; }
            else { message = "updated"; }
            setFirstName(value);
        }
        return message;
    }


    /**
     * Updates the clinician's last name if the value is valid and logs the result.
     * @param value A string of the clinician's new last name.
     * @return A string log of the result of the operation.
     */
    public String updateLastName(String value) {
        String message = "ERROR: Invalid value " + value + ". Last name must be between 1 and 50 alphabetical characters." +
                " Spaces, commas, apostrophes, and dashes are also allowed.\n";
        if (UserAttributeCollection.validateAlphanumericString(false, value, 1, 50)) {

            if(getLastName() != null) { message = "updated"; }
            else { message = "updated"; }
            setLastName(value);
        }
        return message;
    }


    /**
     * Updates the clinician's work address if the value is valid and logs the result.
     * @param value A string of the clinician's new work address.
     * @return A string log of the result of the operation.
     */
    public String updateWorkAddress(String value) {
        String message = "ERROR: Invalid value " + value + ". Work address must be between 1 and 100 alphanumeric characters." +
                " Spaces, commas, apostrophes, and dashes are also allowed.\n";
        if (UserAttributeCollection.validateAlphanumericString(true, value, 1, 100)) {

            if(getContactDetails().getAddress().getStreetAddressLn1() != null) { message = "updated"; }
            else { message = "updated"; }
            getContactDetails().getAddress().updateStreetAddressLine1(value);
        }
        return message;
    }


    /**
     * Updates the clinician's region and logs the result.
     * @param value A string of the clinician's new region.
     * @return A string log of the result of the operation.
     */
    public String updateRegion(String value) {
        String message;
        if(getContactDetails().getAddress().getRegion() != null) { message = "updated"; }
        else { message = "updated"; }
        getContactDetails().getAddress().updateRegion(value);
        return message;
    }

    /**
     * Returns a string of the history of updates made to the clinician if there are any.
     * @return a string of modification logs.
     */
    public String modificationsToString(){

        String logString = "Modification log:\n";
        if(getModifications().size() == 0) {
            logString += "No modifications have been recorded.\n";
        }
        else {
            for (LogEntry log : getModifications()) {
                logString += log.toString();
            }
        }
        return logString + "\n";
    }
}
