package seng302.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import seng302.model.person.Address;
import seng302.model.person.Clinician;
import seng302.model.person.LogEntry;

public class CLIClinicianCommandHandler {

  private AccountManager database;

  /**
   * Default constructor for the CLIClinicianCommandHandler
   *
   * @param database The database to initialize the class with
   */
  public CLIClinicianCommandHandler(AccountManager database) {
    this.database = database;
  }

  public ArrayList<String> delete(String target) {
    ArrayList<String> messages = new ArrayList<>();

    try {
      if (target.equals(Clinician.DEFAULT)) {
        messages.add("ERROR: Can't delete the default clinician");
        return messages;
      }

      ArrayList<Clinician> clinicians = database.getCliniciansArrayList();
      ArrayList<Clinician> found = new ArrayList<>();

      for (Clinician clinician : clinicians) {
        if (clinician.getUserName().equals(target)) {

          messages.add("Found account for staff id: " + target);
          try {
            clinician.setActive(false);
            LogEntry logEntry = new LogEntry(clinician, AccountManager.getCurrentUser(), "deleted",
                null, null);
            AccountManager.getSystemLog().add(logEntry);
            messages.add(logEntry.toString());
            database.getClinicians().remove(clinician.getUserName());

          } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
          }
          found.add(clinician);
        }
      }

      if (found.size() == 0) {
        messages.add("ERROR: No clinician found with that staff id");
      } else {
        messages.add("Clinician " + target
            + " deleted. Make sure to save the application to make the change permanent.");
        database.getClinicians().remove(found.get(0));  // Maybe make this a lot easier?
      }
      return messages;
    } catch (IndexOutOfBoundsException e) {
      messages.add("ERROR: No staff id provided");
    }

    return messages;
  }

  /**
   * An update method for updating clinicians
   *
   * @param trailingCommand The command string that includes the username (staff_id), object and
   * value Object refers to the attribute to update and value refers to the new value
   * @return The string sequences that refer to what the outcome of this method was
   */
  public ArrayList<String> update(String[] trailingCommand) {
    ArrayList<String> messages = new ArrayList<>();

    try {
      String username = trailingCommand[0];
      String object = trailingCommand[1];
      String value = trailingCommand[2];

      ArrayList<Clinician> clinicians = database.getCliniciansArrayList();

      boolean found = false;
      for (Clinician clinician : clinicians) {
        if (clinician.getUserName().equals(username)) {
          found = true;
          if (object.equalsIgnoreCase("id")) {
            messages.add(clinician.updateStaffId(AccountManager.getCurrentUser(), value));
          } else {
            messages.add(clinician.updateAttributes(AccountManager.getCurrentUser(), object,
                value));
          }
        }
      }

      if (!found) {
        messages.add("ERROR: No clinician with staff ID: " + username + " found. Enter 'help' for"
            + " more information");
      }

    } catch (IndexOutOfBoundsException e) {
      messages.add("ERROR: Insufficient information provided for profile update");
    }

    return messages;
  }

  /**
   * A create clinician method that creates a clinician with the specified values. The created
   * clinician
   *
   * @param command The entire command that was used to create the clinician so we can parse it for
   * its attribute values
   * @return An ArrayList of strings that specify what happened when trying to create the clinician
   */
  public ArrayList<String> create(String command) {
    ArrayList<String> messages = new ArrayList<>();

    try {
      Map<String, String> parameters = splitParameters(command);
      boolean valid = true;

      String givenName = parameters.get("givenname");
      if (givenName == null || givenName.equals("")) {
        messages.add("You did not specify a given name for the clinician.");
        valid = false;
      }

      String lastName = parameters.get("lastname");
      if (lastName == null || lastName.equals("")) {
        messages.add("You did not specify a last name for the clinician.");
        valid = false;
      }

      String staffID = parameters.get("staffid");
      if (givenName == null || staffID.equals("")) {
        messages.add("You did not specifiy the staff ID of the clinician.");
        valid = false;
      } else if (!Clinician.validateStaffIDIsInt(staffID)) {
        messages
            .add("The specified staff ID is invalid. It must be a whole number greater than 0.");
        valid = false;
      } else if (!database.checkStaffIDIsNotUsed(staffID)) {
        messages.add("The specified staff ID already exists in the database.");
        valid = false;
      }

      String password = parameters.get("password");
      if (password == null || password.equals("")) {
        messages.add("You did not specify a password for the clinician.");
        valid = false;
      } else if (!Clinician.validatePassword(password)) {
        messages.add("The specified password is invalid. It must contain between 6 and 30 " +
            "alphanumeric characters.");
        valid = false;
      }

      String workAddress = parameters.get("workaddress");
      if (workAddress == null || workAddress.equals("")) {
        messages.add("You did not specify the work address of the clinician.");
        valid = false;
      } else if (!Address.validateAlphanumericString(true, workAddress, 0, workAddress.length())) {
        messages.add("The address you specified is invalid. It must be an alphanumeric sequence.");
        valid = false;
      }

      String region = parameters.get("region");
      if (region == null || region.equals("")) {
        messages.add("You did not specify the region in which the clinician lives.");
        valid = false;
      }

      // Test for invalid parameters.
      ArrayList<String> keys = new ArrayList(parameters.keySet());
      for (String key : keys) {
        if (!key.equals("givenname") && !key.equals("lastname") && !key.equals("staffid") &&
            !key.equals("password") && !key.equals("workaddress") && !key.equals("region")) {
          messages.add("'" + key + "' is not a valid parameter for creating a clinician.");
          valid = false;
        }
      }

      // Create the new Clinician, or display an message indicating failure.
      if (valid) {
        Clinician clinician = new Clinician(givenName, lastName, workAddress, region, staffID,
            password);
        clinician.setMiddleName(""); // To prevent null values displaying.
        database.getClinicians().put(clinician.getUserName(), clinician);
        messages.add("Clinician created with name '" + givenName + " " + lastName +
            "', work address '" + workAddress + ", " + region + "', and staff ID '" + staffID
            + "'.");
      } else {
        messages.add("The new clinician was not added.");
      }
    } catch (IndexOutOfBoundsException e) {
      messages.add("ERROR");
      e.printStackTrace();
    }

    return messages;
  }

  /**
   * Handles a view command for a clinician. If the staff id is valid, then the clinician's
   * information is returned, otherwise a relevant error message will be returned.
   *
   * @param command The command entered on the command line without the "view" or "clinician"
   * keywords, e.g. command = "0" if the original command entered was "view clinician 0".
   * @return Returns a list of messages that are the result of the command. Message are in order of
   * occurrence i.e. the message that was added first is first in the list.
   */
  public ArrayList<String> view(String command) {
    String clinicianId = command.trim();
    ArrayList<String> messages = new ArrayList<>();
    //find the clinician
    ArrayList<Clinician> matches = new ArrayList<>();
    ArrayList<Clinician> accounts = database.getCliniciansArrayList();
    for (Clinician account : accounts) {
      if (account.getUserName().equals(clinicianId)) {
        matches.add(account);
      }
    }
    if (matches.size() == 0) {
      messages.add("ERROR: Staff ID " + clinicianId
          + " not found in database. Enter 'help view' for more information.");
    } else {
      Clinician target = matches.get(0);
      messages.add(target.toString());
    }
    return messages;
  }

  /**
   * Takes a String corresponding to a series of parameters ("key=value") and returns each key value
   * pair in a Map. If multiple values are assigned to the same key, the last assignment is used.
   *
   * @param information A String object representing a series of parameters.
   * @return The Map containing each key value pair.
   */
  private Map<String, String> splitParameters(String information) {

    LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
    List<String> splitInformation = Arrays.asList(information.split(" "));

    StringBuilder value = new StringBuilder();
    String key = null;

    for (String split : splitInformation) {

      if (split.contains("=")) {

        // Start building new key-value pair.
        List<String> splitComponents = Arrays.asList(split.split("="));

        if (key != null) {

          // Only record previous key-value pair if key is not null.
          parameters.put(key, value.toString());

        }

        key = splitComponents.get(0);
        value = new StringBuilder();

        if (splitComponents.size() > 1) {

          // Handle case where no value is specified.
          value.append(splitComponents.get(1));

        }


      } else {

        // If split does not contain equals, continue building value of
        // last key-value pair.
        value.append(" ");
        value.append(split);

      }

    }

    // Append last key-value pair.
    if (key != null) {
      parameters.put(key.toLowerCase(), value.toString());
    }
    return parameters;

  }

  /**
   * A shortcut method that acts as a `help all` command
   *
   * @return String the help text for all CLI methods relating to clinicians
   */
  public String help() {
    String message = "";
    message += ("\nUpdate Clinician Command\n");
    message += "----------\n";
    message += help("update");

    message += "==========\n";

    message += ("View Clinician Command\n");
    message += "----------\n";
    message += help("view");

    message += "==========\n";

    message += ("Create Clinician Command\n");
    message += "----------\n";
    message += help("create");

    message += "==========\n";

    message += ("Delete Clinician Command\n");
    message += ("Delete Clinician Command\n");
    message += "----------\n";
    message += help("delete");

    return message;
  }


  /**
   * The help method for the clinician which allows the user to query for specific help commands
   *
   * @param subHelp The specific help that the user is requesting information for
   * @return String The help string
   */
  public String help(String subHelp) {
    String message = "";
    subHelp = subHelp.toLowerCase();

    switch (subHelp) {
      case "all":
        message = help();
        break;

      case "update":
        message += ("The required input for updating a clinician: update clinician <staff_id> <object> <value>\n");
        message += ("For more detailed information about the separate terms, please consult the user_manual\n");
        break;

      case "view":
        message += "The required input for viewing a clinician: view clinician <staff_id>\n";
        message += "Views all the clinician information based on the staff id of the clinician to view\n";
        break;

      case "create":
        message += "The command for creating a clinician is: create staff givenname=<givenname> staffid=<staff_id> lastname=<last_name> password=<password> workaddress=<work_address> region=<region>\n";
        message += "<givenname> = The given name of the clinician\n";
        message += "<staff_id> = The unique staff id for the clinician\n";
        message += "<lastname> = The last name of the clinician\n";
        message += "<password> = The password the clinician uses when logging in\n";
        message += "<work_address> = The work address for the clinician\n";
        message += "<region> = The region for the clinician\n";
        break;

      case "delete":
        message += "The command for deleting a clinician is: delete staff <staff_id>\n";
        message += "There is no prompt and this will mark a clinician as inactive and remove them from the list of clinicians\n";
        break;
    }

    message += "==========\n";
    return message;
  }

}
