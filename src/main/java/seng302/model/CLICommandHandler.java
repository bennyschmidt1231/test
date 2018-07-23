package seng302.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;
import seng302.App;
import seng302.model.person.DonorReceiver;
import seng302.model.person.LogEntry;
import seng302.model.person.User;

public class CLICommandHandler {

  private AccountManager database;

  public CLICommandHandler() {
    database = App.getDatabase();
  }

  /**
   * Attempts to match the target string, starting at the beginning of the string, against the regex.
   * @param target The string being inspected for a pattern.
   * @param regex The regular expression to search for in the string.
   * @return True if the regular expression is matched, False if it is not matched.
   */
  private boolean isLookingAtPattern(String target, String regex) {
    return Pattern.compile(regex).matcher(target).lookingAt();
  }

  /**
   * Routes the commands to the appropriate handlers.
   * @param command Command entered by user
   * @return Returns a list of messages that are the result of the command. Message are in
   * order of occurrence i.e. the message that was added first is first in the list.
   */
  public ArrayList<String> commandControl(String command) {
    ArrayList<String> messages = new ArrayList<>();
    String createRegex = "^(?i)create";
    String viewRegex = "^(?i)view";
    String deleteRegex = "^(?i)delete";
    String updateRegex = "^(?i)update";
    String saveRegex = "^(?i)save";
    String importRegex = "^(?i)import";
    String helpRegex = "^(?i)help";
    if (isLookingAtPattern(command, createRegex)) {
      command = command.replaceFirst(createRegex, "").trim();
      messages = create(command);
    } else if (isLookingAtPattern(command, viewRegex)) {
      command = command.replaceFirst(viewRegex, "").trim();
      String nameRegex = "^(?i)name";
      String nhiRegex = "^(?i)nhi";
      String donorsRegex = "^(?i)donors";
      if (isLookingAtPattern(command, nameRegex)) {
        String[] names = command.split(" ");
        String[] trailing = Arrays.copyOfRange(names, 1, names.length);
        try {
          messages = issueView(trailing[0], trailing[1], trailing[2]);
        } catch (IndexOutOfBoundsException e) {
          messages.add("ERROR: Names and/or Object not given");
        }
      } else if (isLookingAtPattern(command, nhiRegex))  {
        String[] nhi = command.split(" ");
        String[] trailing = Arrays.copyOfRange(nhi, 1, nhi.length);
        try {
          messages = issueView(trailing[0], trailing[1]);
        } catch (IndexOutOfBoundsException e) {
          messages.add("ERROR: NHI and/or Object not given");
        }
      } else if (isLookingAtPattern(command, donorsRegex)) {
        String[] donors = command.split(" ");
        String[] trailing = Arrays.copyOfRange(donors, 1, donors.length);
        try {
          messages = issueView("donors", trailing[0]);
        } catch (IndexOutOfBoundsException e) {
          messages.add("ERROR: NHI and/or Object not given");
        }
      } else {
        messages.add("Invalid option for viewing. Required field is either Name or NHI");
      }
    } else if (isLookingAtPattern(command, deleteRegex)) {
      String[] donors = command.split(" ");
      String[] trailing = Arrays.copyOfRange(donors, 1, donors.length);
      try {
        messages = issueDelete(trailing[0]);
      } catch (IndexOutOfBoundsException e) {
        messages.add("ERROR: NHI not given for deletion");
      }
    } else if (isLookingAtPattern(command, updateRegex)) {
      String[] update = command.split(" ");
      String[] trailing = Arrays.copyOfRange(update, 1, update.length);
      messages = update(trailing);
    } else if (isLookingAtPattern(command, saveRegex)) {
      messages.add(database.exporter());
    } else if (isLookingAtPattern(command, importRegex)) {
      messages.add(database.importer());
    } else if (isLookingAtPattern(command, helpRegex)) {
      String[] help = command.split(" ");
      String[] trailing = Arrays.copyOfRange(help, 1, help.length);
      try {
        messages.add(help(trailing[0]));
      } catch (IndexOutOfBoundsException e) {
        messages.add("ERROR: Type of help must be specified. Valid "
            + "options are: all, create, update, view, import, save, delete, launch."
            + " For example, 'help all' gives help with all commands.");
      }
    } else {
      messages.add("Invalid command. Please try again.");
    }
    return messages;
  }

  /**
   * Takes a string and attempts to convert it to LocalDate format, which is then returned.
   *
   * @param DOB The date of birth to be converted
   * @return A localdate variable of the provided string
   */
  public LocalDate convertToLocalDate(String DOB) {
    LocalDate localDate;
    try {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
      localDate = LocalDate.parse(DOB, formatter);
      return localDate;
    } catch (DateTimeParseException e) {
      localDate = null;
      return localDate;
    }
  }

  /**
   * Routes an update command to the appropriate handlers.
   * @param trailingCommand The command after the update keyword.
   * @return A list containing the messages that are the result of the update.
   */
  public ArrayList<String> update(String[] trailingCommand) {
    ArrayList<String> messages = new ArrayList<>();
    try {
      String NHI = trailingCommand[0];
      String object = trailingCommand[1];
      if (object.equalsIgnoreCase("organs")) {
        String donReceiver = trailingCommand[2];
        String detailUpdated = trailingCommand[3];
        String updated = trailingCommand[4];
        messages.addAll(issueUpdateOrgans(NHI, object, donReceiver, detailUpdated, updated.trim()));
      } else {
        String detailUpdated = trailingCommand[2];
        String updated = trailingCommand[3];
        messages.addAll(issueUpdate(NHI, object, detailUpdated, updated.trim()));
      }
    } catch(IndexOutOfBoundsException e) {
      messages.add("ERROR: Insufficient information provided for profile update");
    }
    return messages;
  }

  /**
   * Given update details, update the profile
   * @param target Account to be updated
   * @param object Object to be updated
   * @param attribute Attribute to be updated
   * @param value New value of attribute
   * @return Returns a list of messages that are the result of the update.
   */
  public ArrayList<String> issueUpdate(String target, String object, String attribute, String value) {
    ArrayList<String> messages = new ArrayList<>();
    ArrayList<DonorReceiver> accounts = database.getDonorReceiversArrayList();
    if (target.equalsIgnoreCase("donors")) {
      messages.add("ERROR: target = donors. You cannot update all donors at once!" +
          " The target should be the NHI code of the donor you wish to update.");
    } else {
      boolean found = false;
      for (DonorReceiver account : accounts) {
        if (account.getUserName().equalsIgnoreCase(target)) {
          found = true;
          messages.addAll(updateObject(AccountManager.getCurrentUser(), account, object, attribute, value));
        }
      }
      if (!found) {
        messages.add("ERROR: NHI Code " + target + " not found in database. " +
            "Did you spell it right, is it in uppercase? Enter 'help' for more information");
      }
    }
    return messages;
  }

  /**
   * Given update details, update the profile
   * @param target Account to be updated
   * @param object Object to be updated
   * @param donReceiver Update the organs of donating or receiving
   * @param attribute Attribute to be updated
   * @param value New value of attribute
   * @return Returns a list of messages that are the result of the update.
   */
  public ArrayList<String> issueUpdateOrgans(String target, String object, String donReceiver, String attribute, String value) {
    ArrayList<DonorReceiver> accounts = database.getDonorReceiversArrayList();
    ArrayList<String> messages = new ArrayList<>();
    if (target.equalsIgnoreCase("donors")) {
      messages.add("ERROR: target = donors. You cannot update all donors at once!" +
          " The target should be the NHI code of the donor you wish to update.");
    } else {
      boolean found = false;
      for (DonorReceiver account : accounts) {
        if (account.getUserName().equalsIgnoreCase(target)) {
          found = true;
          messages.addAll(updateObjectOrgans(AccountManager.getCurrentUser(), account, object, donReceiver, attribute, value));
        }
      }
      if (!found) {
        messages.add("ERROR: NHI Code " + target + " not found in database. " +
            "Did you spell it right, is it in uppercase? Enter 'help' for more information");
      }
    }
    return messages;
  }

  /**
   * Updates a given account
   * @param account Account to be updated
   * @param object Object to be updated in account
   * @param attribute Attribute in account to be updated
   * @param value New value of attribute
   * @return Returns a list of messages that are the result of the update.
   */
  public ArrayList<String> updateObject(User modifyingAccount, DonorReceiver account, String object, String attribute, String value) {
    ArrayList<String> messages = new ArrayList<>();
    if (object.equalsIgnoreCase("profile")) {
      messages.add(account.updateProfile(modifyingAccount, attribute, value));
    } else if (object.equalsIgnoreCase("attributes")) {
      messages.add(account.updateAttribute(modifyingAccount, attribute, value));
    } else if (object.equalsIgnoreCase("contacts")) {
      String[] result = account.getContactDetails().updateContact(attribute, value);
      if (result[1] != null) {
        account.logChange(new LogEntry(account, modifyingAccount, "personal contact " + attribute,
                result[1], value));
      }
      ArrayList<LogEntry> temp = account.getUpDateLog();
      messages.add(temp.get(temp.size() - 1).toString());
    } else {
      messages.add("ERROR: Unknown object " + object + ". Object should be 'profile', 'attributes', 'organs' or 'contacts'.");
    }
    return messages;
  }

  /**
   * Updates a given account
   * @param account DonorReceiver to be updated
   * @param object Object to be updated
   * @param donReceiver Update the organs of donating or receiving
   * @param attribute Attribute to be updated
   * @param value New value of attribute
   * @return Returns a list of messages that are the result of the update.
   */
  public ArrayList<String> updateObjectOrgans(User modifyingAccount, DonorReceiver account, String object, String donReceiver, String attribute, String value) {
    ArrayList<String> messages = new ArrayList<>();
    messages.add(account.updateOrgan(modifyingAccount, donReceiver, attribute, value));
    return messages;
  }


  /**
   * Creates a new user using information inside the 'information' string.
   * @param information Donor information for creation
   * @return Returns a list of messages that are the result of the creation.
   */
  public ArrayList<String> create(String information) {
    String givenName = " ";
    String otherName = " ";
    String lastName = " ";
    LocalDate localDate;
    String NHI;
    ArrayList<String> messages = new ArrayList<>();
    boolean containsName = Pattern.compile("(?i)name=").matcher(information).find();
    boolean containsDOB = Pattern.compile("(?i)dateOfBirth=").matcher(information).find();
    boolean containsNHI = Pattern.compile("(?i)nhi=").matcher(information).find();
    if ((!containsName) | (!containsDOB) | (!containsNHI)) {
      messages.add("In state create, but missing required fields. Correct template is name=<Name> dateOfBirth=<yyyyMMdd> nhi=<NHI number>.");
      return messages;
    }
    int firstEqualsIndex = information.indexOf('=');
    int secondEqualsIndex = information.indexOf('=', firstEqualsIndex + 1);
    int thirdEqualsIndex = information.indexOf('=', secondEqualsIndex + 1);
    String substringOne = information.substring(0, secondEqualsIndex - 11);
    String substringTwo = information.substring(secondEqualsIndex - 11, thirdEqualsIndex - 3);
    String substringThree = information.substring(thirdEqualsIndex - 3);
    boolean containsNameInSubstring = Pattern.compile("(?i)name=").matcher(substringOne).find();
    boolean containsDOBInSubstring = Pattern.compile("(?i)dateOfBirth=").matcher(substringTwo).find();
    boolean containsNHIInSubstring = Pattern.compile("(?i)nhi=").matcher(substringThree).find(); //parsing names
    if ((!containsNameInSubstring) | (!containsDOBInSubstring) | (!containsNHIInSubstring)) {
      messages.add("Correct tokens provided, but not in the correct order. Correct order is name=<Name> dateOfBirth=<yyyyMMdd> nhi=<NHI number>.");
      return messages;
    }
    String Name = substringOne.substring(substringOne.indexOf('=') + 1);
    Name = Name.trim();
    givenName = AccountManager.parseGivenName(Name);
    otherName = AccountManager.parseOtherNames(Name);
    lastName = AccountManager.parseLastName(Name);
    boolean testGivenName = UserAttributeCollection.validateAlphanumericString(false, givenName, 1, 50);
    boolean testOtherName = UserAttributeCollection.validateAlphanumericString(false, givenName, 1, 50);
    boolean testLastName = UserAttributeCollection.validateAlphanumericString(false, givenName, 1, 50);
    if (givenName == null) {
      messages.add("A name is required for the profile. Please try again.");
      return messages;
    }
    if ((!testGivenName) | (!testOtherName) | (!testLastName)) {
      messages.add("That name is invalid, as it it is not alphabetical. Please try again.");
      return messages;
    }
    String dateOfBirth = substringTwo.substring(substringTwo.indexOf('=') + 1);
    dateOfBirth = dateOfBirth.trim();
    localDate = convertToLocalDate(dateOfBirth);
    if (localDate == null) {
      messages.add("The format required for date is yyyyMMdd. Please try again.");
      return messages;
    }
    NHI = substringThree.substring(substringThree.indexOf('=') + 1).toUpperCase();
    boolean testNHI = database.validateNHI(NHI);
    if ((!testNHI) | (database.checkUsedNHI(NHI))) {
      messages.add("Either you have entered an invalid NHI number or there is already a profile with that number. Please try again.");
    } else {
      if (otherName == null) {
        otherName = "";
      }
      if (lastName == null) {
        lastName = "";
      }
      DonorReceiver newAccount = new DonorReceiver(givenName, otherName, lastName, localDate, NHI);
      newAccount = database.reactivateOldAccountIfExists(AccountManager.getCurrentUser(), newAccount);
      database.getDonorReceivers().put(newAccount.getUserName(), newAccount);
      if (lastName.equalsIgnoreCase("")) {
        messages.add("Account created with name: " + givenName + ", Date of birth: " + localDate.toString() + " and NHI: " + NHI);
      } else if (otherName.equalsIgnoreCase("")) {
        messages.add("Account created with name: " + givenName + ' ' + lastName + ", Date of birth: " + localDate.toString() + " and NHI: " + NHI);
      } else {
        messages.add("Account created with name: " + givenName + ' ' + otherName + ' ' + lastName + ", Date of birth: " + localDate.toString() + " and NHI: " + NHI);
      }
    }
    return messages;
  }


  /**
   * View details of the account with the name provided.
   * @param first Given Name of account
   * @param last Family name of account
   * @param object information to display
   * @return Returns a list of messages that are the result of the view (usually the person's details).
   */
  public ArrayList<String> issueView(String first, String last, String object) {

    ArrayList<DonorReceiver> matches = new ArrayList<>();
    ArrayList<DonorReceiver> accounts = database.getDonorReceiversArrayList();
    ArrayList<String> messages = new ArrayList<>();
    for (DonorReceiver account : accounts) {
      if (account.getFirstName().toLowerCase().equals(first.toLowerCase()) && account.getLastName().toLowerCase().equals(last.toLowerCase())) {
        matches.add(account);
      }
    }

    if (matches.size() == 0) {
      messages.add("ERROR: " + first + " " + last + " not found in database. Did you spell it right? " +
          "Try searching using the donor NHI code instead. Enter 'help' for more information\n");
    } else {
      messages.add(matches.size() + " match(es) for the name " + first + " " + last + ".\n");
      for (DonorReceiver acc : matches) {
        messages.addAll(viewObject(acc, object));
      }
    }
    return messages;
  }

  /**
   * View details of the account with the NHI provided.
   * @param target nhi of the account
   * @param object information to display
   * @return Returns a list of messages that are the result of the view (usually the person's details).
   */
  public ArrayList<String> issueView(String target, String object) {
    ArrayList<String> messages = new ArrayList<>();
    ArrayList<DonorReceiver> accounts = database.getDonorReceiversArrayList();
    if (target.equalsIgnoreCase("donors")) {
      if (object.equalsIgnoreCase("all")) {
        messages.add(database.toString());
      } else if (object.equalsIgnoreCase("organs")) {

        for (DonorReceiver account : accounts) {
          messages.add("Organ Donation list for " + account.getUserName() + ":\n");
          messages.addAll(viewObject(account, "organs"));
        }
      } else if (object.equalsIgnoreCase("attributes")) {
        for (DonorReceiver account : accounts) {
          messages.add("Attribute list for " + account.getUserName() + ":\n");
          messages.addAll(viewObject(account, "attributes"));
        }
      } else {
        messages.add("ERROR: Unknown object " + object + ". " + ". For donors, object should be 'all'," +
            "'organs', or 'attributes'.\n");
      }
    } else {
      boolean found = false;
      for (DonorReceiver account : accounts) {
        if (account.getUserName().equals(target.toUpperCase())) {
          found = true;
          messages.addAll(viewObject(account, object));
        }
      }

      if (!found) {
        messages.add("ERROR: NHI Code " + target + " not found in database. " +
            "Did you spell it right, is it in uppercase? Enter 'help' for more information\n");
      }
    }
    return messages;
  }


  /**
   * Deletes the selected account using NHI.
   * @param target nhi of the account to delete
   * @return Returns a list of messages that are the result of the deletion.
   */
  public ArrayList<String> issueDelete(String target) {
    ArrayList<DonorReceiver> accounts = database.getDonorReceiversArrayList();
    ArrayList<DonorReceiver> found = new ArrayList<>();
    ArrayList<String> messages = new ArrayList<>();
    for (DonorReceiver account : accounts) {
      if (account.getUserName().equals(target.toUpperCase())) {

        messages.add("Found account for nhi: " + target);
        try {
          account.setActive(false);
          LogEntry logEntry = new LogEntry(account, AccountManager.getCurrentUser(), "deleted", null, null);
          account.logChange(logEntry);
        } catch (IllegalArgumentException e) {
          System.out.println(e.getMessage());
        }
        found.add(account);
      }
    }

    if (found.size() == 0) {
      messages.add("There was no donor found that matched the nhi code " + target + ". Did you spell it correctly?");
    } else {
      database.getTwilightZone().put(found.get(0).getUserName(),found.get(0));
      accounts.removeAll(found);
      messages.add("Deletion success, remember to save the application to make the action permanent.");
    }
    return messages;
  }



  /**
   * Returns certain details of the given account depending on the specified object:
   * -'all' prints all the details of the account.
   * -'attributes' prints only the account attributes information.
   * -'organs' prints only the accounts list of organs that will be donated.
   * ='log' prints all the logs in the update log of the given account.
   *
   * @param account a valid string NHI code to identify the account.
   * @param object  a string, either 'all', 'attributes', 'log', 'contacts' or 'organs'.
   * @return Returns a list of messages that are the result of the view (usually the person's details).
   */
  public ArrayList<String> viewObject(DonorReceiver account, String object) {
    ArrayList<String> messages = new ArrayList<>();
    messages.add("Accessing data for donor " + account.getUserName() + "...");
    if (object.equalsIgnoreCase("all")) {
      messages.add(account.toString());
    } else if (object.equalsIgnoreCase("attributes")) {
      messages.add(account.getUserAttributeCollection().toString());
    } else if (object.equalsIgnoreCase("organs")) {
      messages.add(account.getDonorOrganInventory().toString());
    } else if (object.equalsIgnoreCase("contacts")) {
      messages.add(account.getContactDetails().toString());
    } else if (object.equalsIgnoreCase("log")) {
      messages.add(account.updateLogToString());
    } else {
      messages.add("ERROR: Unknown object " + object + ". Object should be 'all', 'attributes', 'log', 'organs' or 'contacts'.\n");
    }
    return messages;
  }

  /**
   * Parses an input token for the help menu, and if the token is valid, and returns the relevant help section.
   * Valid tokens are 'all', 'create', 'update', 'view', 'import', 'save', 'delete', and 'launch'.
   * @param subHelp Which part of help to be shown
   * @return A string containing the relevant help section (or error message).
   */
  public static String help(String subHelp) {
    String message = "";
    if (subHelp.equalsIgnoreCase("all")) {
      message+=("The required input for creating an account: Create name=<Full Name> dateOfBirth=<date of birth> nhi=<Nhi number>\n");
      message+=("Full Name -All the names the person has. the first name will be stored as their first name, while the last name given will be stored as the last. All names in between will be stored as other names. There should be no space between the equals sign and the first name.\n");
      message+=("date of birth -The date of birth of the account holder. The format is yyyyMMdd. y =year, M=month, d = day. There should be no space in between the equals and the date of birth.\n");
      message+=("nhi -The unique number given by the New Zealand Ministry of health to prove uniqueness\n");
      message+=("========================================================================================\n");
      message+=("The required input for updating an account: Update <nhi code> <object> <attribute> <value>\n");
      message+=("========================================================================================\n");
      message+=("There are three separate inputs for viewing accounts\n");
      message+=("1. view nhi <nhi number> <object> \n");
      message+=("2. view name <first name> <last name> <object> \n");
      message+=("3. view donors <object> \n");
      message+=("<nhi number> -The unique number given by the New Zealand Ministry of health to prove uniqueness\n");
      message+=("<object> - either 'all', 'log', 'attributes', 'contacts' or 'organs'\n");
      message+=("========================================================================================\n");
      message+=("The required input for importing saved data into the system: import\n");
      message+=("========================================================================================\n");
      message+=("The required input for saving the current data: save\n");
      message+=("========================================================================================\n");
      message+=("the required input for deleting an account: delete <nhi number>\n");
      message+=("<nhi number> - The unique number given to every patient in the New Zealand Health system.\n");
      message+=("========================================================================================\n");
      message+=("For more information regarding commands for the system, please consult the user_manual\n");
    } else if (subHelp.equalsIgnoreCase("create")) {
      //Print the template for creating an account
      message+=("The required input for creating an account: Create name=<Full Name> dateOfBirth=<date of birth> nhi=<Nhi number>\n");
      message+=("Full Name -All the names the person has. the first name will be stored as their first name, while the last name given will be stored as the last. All names in between will be stored as other names. There should be no space between the equals sign and the first name.\n");
      message+=("date of birth -The date of birth of the account holder. The format is yyyyMMdd. y =year, M=month, d = day. There should be no space in between the equals and the date of birth.\n");
      message+=("nhi -The unique number given by the New Zealand Ministry of health to prove uniqueness\n");
      message+=("For more information, please consult the user_manual\n");
    } else if (subHelp.equalsIgnoreCase("update")) {
      message+=("The required input for updating an account: Update <nhi code> <object> <attribute> <value>\n");
      message+=("For more detailed information about the seperate terms, please consult the user_manual\n");
    } else if (subHelp.equalsIgnoreCase("view")) {
      message+=("There are three separate inputs for viewing accounts\n");
      message+=("1. view nhi <nhi number> <object> \n");
      message+=("2. view name <first name> <last name> <object> \n");
      message+=("3. view donors <object> \n");
      message+=("<nhi number> -The unique number given by the New Zealand Ministry of health to prove uniqueness\n");
      message+=("<object> - either 'all', 'log', 'attributes', 'contacts' or 'organs'\n");
      message+=("Please consult the user_manual for more information\n");
    } else if (subHelp.equalsIgnoreCase("import")) {
      message+=("The required input for importing saved data into the system: import\n");
      message+=("Please consult the user_manual for more information\n");
    } else if (subHelp.equalsIgnoreCase("save")) {
      message+=("The required input for saving the current data: save\n");
      message+=("Please consult the user_manual for more information\n");
    } else if (subHelp.equalsIgnoreCase("delete")) {
      message+=("The required input for deleting an account: delete <nhi number>\n");
      message+=("<nhi number> - The unique number given to every patient in the New Zealand Health system.\n");
      message+=("For more information, please consult the user_manual\n");
    } else if (subHelp.equalsIgnoreCase("launch")) {
      message+=("Launches the GUI component of the application.\n");
      message+=("Can only be called once while the application is running.\n");
      message+=("Only recognised if used in the CLI outside of the GUI component.\n");
    } else {
      message+=("Invalid query. returning to the main menu...\n");
    }
    return message;
  }

}
