package seng302.model.person;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.format.DateTimeFormatter;
import seng302.App;
import seng302.model.AccountManager;
import seng302.model.UserAttributeCollection;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Clinician extends User {

  public static String DEFAULT = "0";

  /**
   * A container for the log of the last error message for an attempted update performed on the
   * clinician.
   */
  private String updateMessage = "waiting for new message";


  /**
   * The constructor method for a Clinician (when creating a new Clinician).
   *
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

    super(givenName, null, lastName, new ContactDetails(
        new Address(workAddress, null, null, null, region, null, null)
        , null, null, null), staffID, password, creationDate, modifications);

  }


  /**
   * A constructor method for Clinician with a reduced number of parameters. The creation date and
   * modifications list are generated automatically.
   */
  public Clinician(String givenName, String lastName, String workAddress,
      String region, String staffID, String password) {
    super(givenName, null, lastName, new ContactDetails(
        new Address(workAddress, null, null, null, region, null, null)
        , null, null, null), staffID, password, LocalDateTime.now(), new ArrayList<LogEntry>());
  }


  public String getUpdateMessage() {
    return updateMessage;
  }


  public void setUpdateMessage(String updateMessage) {
    this.updateMessage = updateMessage;
  }

  /**
   * The constructor method for a Clinician (when creating a new Clinician) using json.
   *
   * @param givenName The given name of the Clinician.
   * @param middleName The middle name of the Clinician.
   * @param lastName The last name of the Clinician.
   * @param contacts The contact details of the Clinician.
   * @param staffID The staff ID of the Clinician.
   * @param password The password of the Clinician.
   * @param creationDate the date of creation for this clinician, a LocalDate object.
   * @param modifications An array list of LocalDate objects that record modifications to the
   * clinician.
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
      @JsonProperty("modifications") ArrayList<LogEntry> modifications) {
    super(givenName, middleName, lastName, contacts, staffID, password, creationDate,
        modifications);
  }


  /**
   * Checks if the given password string is alphanumeric and is between 6 and 30 characters
   * (inclusive) long.
   *
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
   *
   * @param id A string representation of an integer.
   * @return Returns 'true' if the given id can be parsed as an int and is greater than zero,
   * returns 'false' otherwise.
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
   *
   * @param value A string of the clinician's new given name.
   * @return A string log of the result of the operation.
   */
  public String updateGivenName(String value) {
    String message = "ERROR: Invalid value " + value
        + ". Given name must be between 1 and 50 alphabetical characters." +
        " Spaces, commas, apostrophes, and dashes are also allowed.\n";
    if (UserValidator.validateAlphanumericString(false, value, 1, 50)) {

      if (getFirstName() != null) {
        message = "updated";
      } else {
        message = "updated";
      }
      setFirstName(value);
    }
    return message;
  }


  /**
   * Updates the clinician's last name if the value is valid and logs the result.
   *
   * @param value A string of the clinician's new last name.
   * @return A string log of the result of the operation.
   */
  public String updateLastName(String value) {
    String message = "ERROR: Invalid value " + value
        + ". Last name must be between 1 and 50 alphabetical characters." +
        " Spaces, commas, apostrophes, and dashes are also allowed.\n";
    if (UserValidator.validateAlphanumericString(false, value, 1, 50)) {

      if (getLastName() != null) {
        message = "updated";
      } else {
        message = "updated";
      }
      setLastName(value);
    }
    return message;
  }


  /**
   * Updates the clinician's work address if the value is valid and logs the result.
   *
   * @param value A string of the clinician's new work address.
   * @return A string log of the result of the operation.
   */
  public String updateWorkAddress(String value) {
    String message = "ERROR: Invalid value " + value
        + ". Work address must be between 1 and 100 alphanumeric characters." +
        " Spaces, commas, apostrophes, and dashes are also allowed.\n";
    if (UserValidator.validateAlphanumericString(true, value, 1, 100)) {

      if (getContactDetails().getAddress().getStreetAddressLn1() != null) {
        message = "updated";
      } else {
        message = "updated";
      }
      getContactDetails().getAddress().updateStreetAddressLine1(value);
    }
    return message;
  }


  /**
   * Updates the clinician's region and logs the result.
   *
   * @param value A string of the clinician's new region.
   * @return A string log of the result of the operation.
   */
  public String updateRegion(String value) {
    return getContactDetails().getAddress().updateRegion(value);
  }

  public String updateAttributes(User modifyingAccount, String attribute, String value) {
    String message = "";
    String oldValue = "";
    String workAddress = getContactDetails().getAddress().getStreetAddressLn1();
    String region = getContactDetails().getAddress().getRegion();
    String givenName = getFirstName();
    String lastName = getLastName();
    // In clinician add an update method that handles the logs and everything else here
    // actually so that its easier to manage. Refer to updateAttributes in DonorReceiver
    switch (attribute.toLowerCase()) {
      case "givenname":
        oldValue = givenName;
        message = updateGivenName(value);
        break;
      case "lastname":
        oldValue = lastName;
        message = updateLastName(value);
        break;
      case "workaddress":
        oldValue = workAddress;
        message = updateWorkAddress(value);
        break;
      case "region":
        oldValue = region;
        message = updateRegion(value);
        break;
    }

    String substring = message.substring(0, 5);
    if (!substring.equals("ERROR")) {
      LogEntry logEntry = new LogEntry(this, modifyingAccount, attribute, oldValue, value);
      AccountManager.getSystemLog().add(logEntry);
      message = logEntry.toString();
    } else {
      updateMessage = message;
    }
    System.out.println(message);
    return message;
  }

  public String updateStaffId(User modifyingAccount, String newId) {
    if (getUserName().equals(newId)) {
      return "ERROR: This account already has that staff id";
    }
    if (!validateStaffIDIsInt(newId)) {
      return "ERROR: Invalid staff id";
    }
    for (Clinician clinician : App.getDatabase().getClinicians().values()) {
      if (clinician.getUserName().equals(newId)) {
        return "ERROR: There is already a clinician with that staff id";
      }
    }
    setUserName(newId);
    LogEntry logEntry = new LogEntry(this, modifyingAccount, "staff_id", getUserName(), newId);
    AccountManager.getSystemLog().add(logEntry);
    return logEntry.toString();
  }

  /**
   * Returns a string of the history of updates made to the clinician if there are any.
   *
   * @return a string of modification logs.
   */
  public String modificationsToString() {

    String logString = "Modification log:\n";
    if (getModifications().size() == 0) {
      logString += "No modifications have been recorded.\n";
    } else {
      for (LogEntry log : getModifications()) {
        logString += log.toString();
      }
    }
    return logString + "\n";
  }

  @Override
  public String toString() {
    String details = "Clinician Details:\n\n";
    details += "Name: " + this.fullName() + "\n";
    details += "Staff ID: " + this.getUserName() + "\n\n";
    details += "Contact Details:\n" + getContactDetails().toString() + "\n";
    details += String.format("Account created at: %s\n",
        getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    return details;
  }
}
