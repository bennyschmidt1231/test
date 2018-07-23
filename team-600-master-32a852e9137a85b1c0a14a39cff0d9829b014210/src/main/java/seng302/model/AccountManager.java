package seng302.model;


import seng302.App;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import seng302.model.person.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AccountManager {

    @JsonIgnore private static User currentUser;
    @JsonIgnore private static DonorReceiver selectedAccount;
    @JsonIgnore private Scanner input;


    @JsonIgnore private ArrayList<DonorReceiver> donorReceivers = new ArrayList();
    @JsonIgnore private ArrayList<DonorReceiver> donorReceiverMasterList = new ArrayList();

    @JsonIgnore private ArrayList<Clinician> clinicians = new ArrayList();
    @JsonIgnore private ArrayList<Clinician> clinicianMasterList = new ArrayList<>();
    @JsonIgnore public ArrayList<Clinician> clinicianTwilightZone = new ArrayList<>();

    @JsonIgnore private ArrayList<Administrator> administrators = new ArrayList<>();
    @JsonIgnore private ArrayList<Administrator> administratorMasterList = new ArrayList<>();
    @JsonIgnore public ArrayList<Administrator> adminTwilightZone = new ArrayList<>();

    /**
     * An arraylist to store accounts to be deleted before the application is saved.
     */
    @JsonIgnore
    private ArrayList<DonorReceiver> twilightZone = new ArrayList();

    public ArrayList<DonorReceiver> getTwilightZone() {
        return twilightZone;
    }

    /**
     * An ArrayList to store all the accounts that are waiting to receive an organ.
     */
    private ArrayList<DonorReceiver> transplantWaitingList = new ArrayList<DonorReceiver>();


    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User newCurrentUser) { currentUser = newCurrentUser; }

    public static DonorReceiver getSelectedAccount() {
        return selectedAccount;
    }

    public static void setSelectedAccount(DonorReceiver newSelectedDonorReceiver) {
        selectedAccount = newSelectedDonorReceiver;
    }

    private static ObservableList<LogEntry> systemLog = FXCollections.observableArrayList();

    public ArrayList<DonorReceiver> getDonorReceivers() {
        return donorReceivers;
    }

    public ArrayList<DonorReceiver> getTransplantWaitingList() { return transplantWaitingList; }

    public ArrayList<Administrator> getAdministrators() {
        return administrators;
    }

    public void setAdministrators(ArrayList<Administrator> administrators) {
        this.administrators = administrators;
    }

    /**
     * A JSON constructor for the DonorReceiver Manager which imports the system log from file.
     * @param systemLog An arraylist of LogEntry's.
     */
    @JsonCreator
    public AccountManager(@JsonProperty("systemLog") ArrayList<LogEntry> systemLog) {
        AccountManager.systemLog = FXCollections.observableArrayList(systemLog);
    }

    /**
     * Default empty constructor for Account Manager.
     */
    public AccountManager() {
    }


    public void setInput(Scanner input) {
        this.input = input;
    }

    /**
     * Parses the input line either with or after a Create token has been inputted. No matter the order of the DOB, NHI and name they will be parsed accordingly and a new account will be created.
     * If incorrect information is inputted, an error message will appear wth information regarding what the input was missing
     * @param modifyingAccount The user which is creating the account. Set modifying account to null to indicate that
     *                         the donor/receiver is creating themselves.
     * @param input the scanner which reads commands
     * @return A scanner, which can be used for further prompts for the system
     */
    public Scanner Create(User modifyingAccount, Scanner input) {
        String givenName = " ";
        String otherName = " ";
        String lastName = " ";
        LocalDate localDate;
        String NHI;
        int count = 0;
        String information = input.nextLine();
        boolean test = information.contains("name=");
        boolean test2 = information.contains("dateOfBirth=");
        boolean test3 = information.contains("nhi=");
        while ((!test) | (!test2) | (!test3)) {
            if (count == 0) {
                System.out.println("In state create, but missing required fields. Correct template is name=<Name> dateOfBirth=<date of birth> nhi=<nhi number>");
                information = input.nextLine();
                test = information.contains("name=");
                test2 = information.contains("dateOfBirth=");
                test3 = information.contains("nhi=");
                count += 1;
            } else {
                System.out.println("In state create, but missing required fields. Correct template is name=<Name> dateOfBirth=<date of birth> nhi=<nhi number>");
                information = input.nextLine();
                test = information.contains("name=");
                test2 = information.contains("dateOfBirth=");
                test3 = information.contains("nhi=");
            }
        }

        int firstEqualsIndex = information.indexOf('=');
        int secondEqualsIndex = information.indexOf('=', firstEqualsIndex + 1);
        int thirdEqualsIndex = information.indexOf('=', secondEqualsIndex + 1);
        String substringOne = information.substring(0, secondEqualsIndex - 11);
        String substringTwo = information.substring(secondEqualsIndex - 11, thirdEqualsIndex - 3);
        String substringThree = information.substring(thirdEqualsIndex - 3);
        boolean test4 = substringOne.contains("name=");
        boolean test5 = substringTwo.contains("dateOfBirth");
        boolean test6 = substringThree.contains("nhi=");//parsing names
        while ((!test4) | (!test5) | (!test6)) {
            System.out.println("Correct tokens provided, but not in the correct order. Correct order is name=<Name> dateOfBirth=<date of birth> nhi=<nhi number>. Returning to main menu");
            return input;
        }
        //-----------------------------------------------------------------------------------
        String Name = substringOne.substring(substringOne.indexOf('=') + 1);
        Name = Name.trim();
        givenName = parseGivenName(Name);
        otherName = parseOtherNames(Name);
        lastName = parseLastName(Name);
        boolean testGivenName = UserAttributeCollection.validateAlphanumericString(false, givenName, 1, 50);
        boolean testOtherName = UserAttributeCollection.validateAlphanumericString(false, givenName, 1, 50);
        boolean testLastName = UserAttributeCollection.validateAlphanumericString(false, givenName, 1, 50);
        if ((!testGivenName) | (!testOtherName) | (!testLastName)) {
            System.out.println("That name is invalid, as it it is not alphabetical. Returning to main menu");
            return input;
        }
        if (givenName == null) {
            System.out.println("A name is required for the profile. Please try again.");
            return input;
        }
        //Parsing DOB and NHI number
//---------------------------------------------------------------------------------------

        String dateOfBirth = substringTwo.substring(substringTwo.indexOf('=') + 1);
        dateOfBirth = dateOfBirth.trim();
        localDate = convertToLocalDate(dateOfBirth);
        if (localDate == null) {
            System.out.println("The format required for date is yyyyMMdd. Returned to main menu");
            return input;
        }

        NHI = substringThree.substring(substringThree.indexOf('=') + 1);
        boolean testNHI = validateNHI(NHI);
        if ((!testNHI) | (checkUsedNHI(NHI))) {
            System.out.println("Either you have entered an invalid NHI number or there is already a profile with that number. Transaction cancelled");
            return input;
        } else {
            DonorReceiver newDonorReceiver = new DonorReceiver(givenName, otherName, lastName, localDate, NHI);
            if (modifyingAccount == null) {
                newDonorReceiver = reactivateOldAccountIfExists(newDonorReceiver, newDonorReceiver);
            } else {
                newDonorReceiver = reactivateOldAccountIfExists(modifyingAccount, newDonorReceiver);
            }
            donorReceivers.add(newDonorReceiver);
        }

        printCreationAcceptance(givenName, otherName, lastName, localDate, NHI);

        return input;
    }


    /**
     * Restores data of an old donorReceiver if it has been reactivated and logs the reactivation. Otherwise
     * Logs activation of a new donorReceiver.
     * @param modifyingAccount The user who created or is reactivating the new user.
     * @param donorReceiver A newly created DonorReceiver to be checked if it has been deactivated in the past.
     * @return the DonorReceiver with a log of reactivation or creation
     */
    public DonorReceiver reactivateOldAccountIfExists(User modifyingAccount, DonorReceiver donorReceiver) {
        for (DonorReceiver acc : donorReceiverMasterList) {
            //checking if an old account is being reactivated
            if (acc.getUserName().equals(donorReceiver.getUserName()) && !acc.isActive()) {
                // if the clinician or admin is reactivating the old account
                if (modifyingAccount != null) {

                    LogEntry logEntry = new LogEntry(donorReceiver, modifyingAccount, "reactivated", "inactive", "active");
                    donorReceiver.logChange(logEntry);
                    getSystemLog().add(logEntry);
                    //The user is reactivating their old account
                } else {
                    LogEntry logEntry = new LogEntry(donorReceiver, donorReceiver, "reactivated", "inactive", "active");
                    donorReceiver.logChange(logEntry);
                    getSystemLog().add(logEntry);
                }
                donorReceiver.setActive(true);
                return donorReceiver;
            }
        }
        // A new account is being created


        // if the clinician or admin is activating a new account
        if (modifyingAccount != null) {
            LogEntry logEntry = new LogEntry(donorReceiver, modifyingAccount, "created", "", "active");
            donorReceiver.logChange(logEntry);
            getSystemLog().add(logEntry);
            //The user is activating a new account
        } else {
            LogEntry logEntry = new LogEntry(donorReceiver, donorReceiver, "created", "", "active");
            donorReceiver.logChange(logEntry);
            getSystemLog().add(logEntry);
        }
        return donorReceiver;
    }


    /**
     * Given an NHI number, finds the appropriate profile and calls the print function.
     * @param input The scanner which reads commands
     * @return A scanner so more commands can be given
     */
    public Scanner viewWithNHI(Scanner input) {

        String NHI = input.next();
        String object = input.next();
        issueCommand("view", NHI, object);
        return input;
    }


    /**
     * Given an NHI number, finds the appropriate profile and calls the delete function.
     * @param modifyingAccount The user who is deleting the donor/receiver.
     * @param input The scanner which reads commands
     * @return A scanner so more commands can be given
     */
    public Scanner delete(User modifyingAccount, Scanner input) {

        String NHI = input.next();
        issueCommand(modifyingAccount, "delete", NHI);
        return input;
    }


    /**
     * Given the token donors number, finds the appropriate profile and calls the print function.
     * @param input The scanner which reads commands
     * @return A scanner so more commands can be given
     */
    public Scanner viewAll(Scanner input) {

        String object = input.next();
        issueCommand("view", "donors", object);
        return input;
    }

    /**
     * Given a name, finds the appropriate profile and calls the print function.
     * @param input The scanner which reads commands
     * @return A scanner so more commands can be given
     */
    public Scanner viewWithName(Scanner input) {

        String givenName = input.next();
        String lastName = input.next();
        String object = input.next();
        issueCommand("view", givenName, lastName, object);
        return input;
    }

    /**
     * Parses the required information into variables, then passes them onto the issuecommand method so the command can be carried out.
     * @param modifyingAccount The user who is updating the donor/receiver.
     * @param input the scanner which reads commands
     * @return the Scanner that is reading all commands
     */
    public Scanner Update(User modifyingAccount, Scanner input) {
        String NHI = input.next();
        String object = input.next();
        if (object.equals("organs")) {
            String donReceiver = input.next();
            String detailUpdated = input.next();
            String updated = input.nextLine();
            while (updated.equals("")) {
                System.out.println("Require a value for " + detailUpdated + " to be set to");
                updated = input.nextLine();
            }
            issueCommand(modifyingAccount, "update", NHI, object, donReceiver, detailUpdated, updated.trim());
            return input;
        } else {
            String detailUpdated = input.next();
            String updated = input.nextLine();
            while (updated.equals("")) {
                System.out.println("Require a value for " + detailUpdated + " to be set to");
                updated = input.nextLine();
            }
            issueCommand(modifyingAccount, "update", NHI, object, detailUpdated, updated.trim());
            return input;
        }


    }


    /**
     * Calls the exportAccounts method and returns the scanner.
     * @return The scanner that reads all commands.
     */
    public String exporter() {

        return exportAccounts();

    }


    /**
     * Calls the importAccounts method and returns the scanner.
     * @return Message for user
     */
    public String importer() {

        return importAccounts();

    }




    //===========================================================

    /**
     * Takes a string and attempts to convert it to LocalDate format, which is then returned.
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
            return null;
        }
    }

    /**
     * Takes a string inputted by the user, and parses the first name within the string.
     * @param fullName The full name of the person
     * @return The first name of the account holder
     */
    public String parseGivenName(String fullName) {
        try {
            return fullName.substring(0, fullName.indexOf(' '));
        } catch (IndexOutOfBoundsException e) {
            if (fullName.equals("")) {
                return null;
            } else {
                return fullName;
            }
        }
    }

    /**
     * Takes a string inputted by the user, and parses the middle names
     * @param fullName The full name of the person
     * @return the middle names of the account holder
     */
    public String parseOtherNames(String fullName) {
        String otherName = fullName.substring(fullName.indexOf(' ') + 1, fullName.lastIndexOf(' ') + 1);
        otherName = otherName.trim();
        if (otherName.equals("")) {
            otherName = null;
        }
        return otherName;
    }

    /**
     * takes a string inputted by the user, and parses the last name.
     * @param fullName the full name of the person
     * @return the last name of the account holder
     */
    public String parseLastName(String fullName) {
        String lastName = fullName.substring(fullName.lastIndexOf(' ') + 1);
        lastName = lastName.trim();
        if (lastName.equals(fullName)) {
            lastName = null;
        }
        return lastName;
    }

    /**
     * Checks if the NHI given is already in use within the database
     * @param NHI The national health index number of the account holder
     * @return A boolean variable  indicating whether the number is already being used.
     */
    public boolean checkUsedNHI(String NHI) {
        int max = donorReceivers.size();
        boolean found = false;
        DonorReceiver person;
        if (max != 0) {
            for (DonorReceiver donorReceiver : donorReceivers) {
                person = donorReceiver;
                if (person.getUserName().equals(NHI)) {
                    found = true;
                }
            }
        }
        return found;
    }

    /**
     * Prints when a profile is created and approved.
     * @param givenName  The persons first name
     * @param otherNames The persons middle names
     * @param lastName   The persons last name
     * @param localDate  The date of birth
     * @param NHI        The National Health Index number
     */
    public void printCreationAcceptance(String givenName, String otherNames, String lastName, LocalDate localDate, String NHI) {
        if (lastName == null) {
            System.out.println("DonorReceiver Created with name: " + givenName + ", Date of birth: " + localDate.toString() + " and NHI: " + NHI);
        } else if (otherNames == null) {
            System.out.println("DonorReceiver Created with name: " + givenName + ' ' + lastName + ", Date of birth: " + localDate.toString() + " and NHI: " + NHI);
        } else {
            System.out.println("DonorReceiver Created with name: " + givenName + ' ' + otherNames + ' ' + lastName + ", Date of birth: " + localDate.toString() + " and NHI: " + NHI);
        }
    }



    /**
     * Checks whether the given NHI is valid. An NHI code is valid if it meets the following conditions:
     * - It is exactly 7 characters long.
     * - The first 3 are uppercase alphabetical letters, and no letter can be 'I' or 'O'.
     * - The next 3 are any digits.
     * - The last character is a non-zero digit.
     * - The NHI number isn't already present in the List of accounts. That is, there are no duplicate NHI numbers
     * and therefore duplicate accounts.
     *
     * @param NHI A String representing the donor's unique National Health Index code
     * @return a boolean, 'true' if the NHI is valid, 'false' otherwise.
     */
    public boolean validateNHI(String NHI) {
        if(NHI.length() != 7) {
            return false;
        }
        for (DonorReceiver account : donorReceiverMasterList) {
            if (account.getUserName().equals(NHI)) {
                if (!account.isActive()) {
                    System.out.println("WARNING: NHI code already used for deactivated account.");
                    System.out.println("Reactivated account. Save to make reactivation and changes permanent.");
                    System.out.println("Note that previous unsaved changes to the deactivated account will have been lost.");
                } else {
                    return false;
                }
            }
        }
     return checkNHIRegex(NHI);
    }

  /**
   * Checks whether an NHI is valid using regex patterns
   * - It is exactly 7 characters long.
   * - The first 3 are uppercase alphabetical letters, and no letter can be 'I' or 'O'.
   * - The next 3 are any digits.
   * - The last character is a non-zero digit.
   * - The NHI number isn't already present in the List of accounts. That is, there are no duplicate NHI numbers
   * and therefore duplicate accounts.
   * @param NHI The NHI we want to check
   * @return Boolean Whether the NHI is a valid one or not
   */
  public boolean checkNHIRegex(String NHI) {
      if(NHI.length() != 7) {
        return false;
      }

      String alpha = NHI.substring(0, 3);
      String regularExpression1 = "^[A-HJ-NP-Z]{3}$";
      Pattern pattern1 = Pattern.compile(regularExpression1);
      Matcher matcher1 = pattern1.matcher(alpha);
      String numeric = NHI.substring(3, 6);
      String regularExpression2 = "^[0-9]{3}$";
      Pattern pattern2 = Pattern.compile(regularExpression2);
      Matcher matcher2 = pattern2.matcher(numeric);
      String checkSum = NHI.substring(6);
      String regularExpression3 = "^[1-9]+$";
      Pattern pattern3 = Pattern.compile(regularExpression3);
      Matcher matcher3 = pattern3.matcher(checkSum);
      return matcher1.matches() && matcher2.matches() && matcher3.matches();
    }

    /**
     * Checks whether an administrator username is valid or not (Username already taken or invalid characters in username)
     * The pattern is alphanumeric and underscores(_) allowed.
     * TODO: This should be inherited from somewhere
     *
     * @param username The username the admin wishes to use when logging in
     * @return boolean A boolean for whether the username is valid or not
     */
    public boolean validateAdminUsername(String username) {
        String regularExpressionCharacters = "^[a-zA-Z0-9_]{3,30}$";
        Pattern patternCharacters = Pattern.compile(regularExpressionCharacters);
        Matcher matcher = patternCharacters.matcher(username);

        if(matcher.matches()) {
          String containsLetter = "^(.*[a-zA-Z].*)$";
          Pattern containsLetterPattern = Pattern.compile(containsLetter);
          Matcher matcherLetter = containsLetterPattern.matcher(username);

          return matcherLetter.matches(); // Return true if there is a match otherwise false
        }
        return false;
    }

    /**
     * waits and scans for user input to a yes/no question. Returns 'true' if the user inputs 'yes' or 'y'. Returns 'false' otherwise.
     *
     * @return 'true' if the user inputs 'yes' or 'y'. Returns 'false' otherwise.
     */
    public Boolean waitForDecision() {


        System.out.println("Enter 'yes' or 'no'");
        String decision = null;
        while (decision == null) {

            Scanner scanner = new Scanner(App.getCommandLine().readInput());
            decision = scanner.next();
        }
        try {
            if (decision.equals("yes".toLowerCase()) || decision.equals("y".toLowerCase())) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * Prints all statistics (save log information) of every donor in the database (if there are any).
     */
    @Override
    public String toString() {
        String string = "Donors:\n\n";
        if (donorReceivers.size() == 0) {
            string += "No donors to display.\n";
        }
        for (DonorReceiver donorReceiver : donorReceivers) {
            string += donorReceiver.toString();
        }
        return string;
    }


    /**
     * Updates either the DonorReceiver, UserAttributeCollection, DonorOrganInventory attribute depending on the value of
     * the given object.
     * @param modifyingAccount The user updating the donor/receiver.
     * @param donorReceiver   a valid string NHI code to identify the donorReceiver to update.
     * @param object    a string representation of the object to be updated. This being 'profile', 'attributes', or 'contacts'.
     * @param attribute a string representation of the attribute to be updated.
     * @param value     a string representation of the new value that attribute will be updated as.
     */
    public void updateObject(User modifyingAccount, DonorReceiver donorReceiver, String object, String attribute, String value) {
        if (object.equals("profile")) {
            donorReceiver.updateProfile(modifyingAccount, attribute, value);
        } else if (object.equals("attributes")) {
            donorReceiver.updateAttribute(modifyingAccount, attribute, value);
        } else if (object.equals("organs")) {
            donorReceiver.updateOrgan(modifyingAccount, "donor", attribute, value);
        } else if (object.equals("contacts")) {
            //donorReceiver.updateContact(attribute, value); TODO
        } else {
            System.out.println("ERROR: Unknown object " + object + ". Object should be 'profile', 'attributes', 'organs' or 'contacts'.");
        }
    }

    /**
     * Updates either the DonorReceiver, UserAttributeCollection, DonorOrganInventory attribute depending on the value of
     * the given object.
     * @param modifyingAccount The account modifying the donor/receiver.
     * @param donorReceiver The donor/receiver being updated.
     * @param donReceiver   a valid string NHI code to identify the donorReceiver to update.
     * @param object    a string representation of the object to be updated. This being 'profile', 'attributes', or 'organs'.
     * @param attribute a string representation of the attribute to be updated.
     * @param value     a string representation of the new value that attribute will be updated as.
     */
    public void updateObject(User modifyingAccount, DonorReceiver donorReceiver, String object, String donReceiver, String attribute, String value) {
        if (object.equals("profile")) {
            donorReceiver.updateProfile(modifyingAccount, attribute, value);
        } else if (object.equals("attributes")) {
            donorReceiver.updateAttribute(modifyingAccount, attribute, value);
        } else if (object.equals("organs")) {
            donorReceiver.updateOrgan(modifyingAccount, donReceiver,attribute, value);
        } else if (object.equals("contacts")) {
            String[] result = donorReceiver.getContactDetails().updateContact(attribute, value);
            if (result[1] != null) {
                donorReceiver.logChange(new LogEntry(donorReceiver, modifyingAccount, "personal contact " + attribute, result[1], value));
            }
        } else if (object.equals("emergencyContacts")){
            String[] result = donorReceiver.getEmergencyContactDetails().updateContact(attribute, value);
            if (result[1] != null) {
                donorReceiver.logChange(new LogEntry(donorReceiver, modifyingAccount,"emergency contact " + attribute, result[1], value));
            }
        } else {
            System.out.println("ERROR: Unknown object " + object + ". Object should be 'profile', 'attributes', 'organs', 'contacts' or 'emergencyContacts'.");
        }
    }


    /**
     * Prints certain details of the given donorReceiver depending on the specified object:
     * -'all' prints all the details of the donorReceiver.
     * -'attributes' prints only the donorReceiver attributes information.
     * -'organs' prints only the accounts list of organs that will be donated.
     * ='log' prints all the logs in the update log of the given donorReceiver.
     *
     * @param donorReceiver a valid string NHI code to identify the donorReceiver.
     * @param object  a string, either 'all', 'attributes', 'log', or 'organs'.
     */
    public void viewObject(DonorReceiver donorReceiver, String object) {
        String message = "";
        System.out.println("Accessing data for donor " + donorReceiver.getUserName() + "...");
        if(object.equals("all")) { message = donorReceiver.toString(); }
        else if(object.equals("attributes")) { message = donorReceiver.getUserAttributeCollection().toString(); }
        else if(object.equals("organs")) { message = donorReceiver.getDonorOrganInventory().toString(); }
        else if(object.equals("contacts")) { message = donorReceiver.getContactDetails().toString(); }
        else if(object.equals("log")) { message = donorReceiver.updateLogToString(); }
        else { message = "ERROR: Unknown object " + object + ". Object should be 'all', 'attributes', 'log', 'organs' or 'contacts'.\n"; }
        System.out.println(message);
    }


    /**
     * Deletes the given donor from the database if the given nhi code matches an existing donor. The user must
     * confirm that action. The deletion is only permanent if the user subsequently saves the program.
     * @param modifyingAccount The account doing the deletion.
     * @param target a string of the nhi code  (e.g. "ABC1234") of the donor to be deleted.
     */
    public void issueDelete(User modifyingAccount, String target) {
        Boolean choice = null;
        ArrayList<DonorReceiver> found = new ArrayList<DonorReceiver>();
        for (DonorReceiver donorReceiver : donorReceivers) {
            if (donorReceiver.getUserName().equals(target)) {

                System.out.println("Found donorReceiver for nhi: " + target + ". Deletion is permanent only if the application" +
                        " is saved. Are you sure you wish to continue?");
                choice = waitForDecision();
                if (choice) {
                    try {
                        donorReceiver.setActive(false);
                        LogEntry logEntry = new LogEntry(donorReceiver, modifyingAccount, "deleted", null, null);
                        donorReceiver.logChange(logEntry);
                    } catch (IllegalArgumentException e) { // wrong action was given
                        System.out.println(e.getMessage());
                        System.exit(0); // probably overkill for this kind of error
                    }
                } else {
                    System.out.println("Deletion failure, action aborted.");
                }
                found.add(donorReceiver);
                break;
            }
        }
        if (found.size() == 0) {
            System.out.println("There was no donor found that matched the nhi code " + target + ". Did you spell it correctly?");
        } else if (choice) {
            twilightZone.add(found.get(0));
            donorReceivers.removeAll(found);
            System.out.println("Deletion success, remember to save the application to make the action permanent.");
        }
    }


    /**
     * Performs identically to the issueDelete method, but does not require
     * any input from the user. It is called by controllers.
     * @param modifyingAccount The account doing the deletion.
     * @param target The NHI number of the account to delete.
     */
    public void issueDeleteWithoutConfirmation(User modifyingAccount, String target) {

        for (DonorReceiver donorReceiver : donorReceivers) {

            if (donorReceiver.getUserName().equals(target)) {

                try {
                    donorReceiver.setActive(false);
                    LogEntry logEntry = new LogEntry(donorReceiver, modifyingAccount, "deleted", null, null);
                    donorReceiver.logChange(logEntry);
                    twilightZone.add(donorReceiver);
                    donorReceivers.remove(donorReceiver);
                } catch (IllegalArgumentException e) { // wrong action was given
                    System.out.println(e.getMessage());
                    System.exit(0); // probably overkill for this kind of error
                }

                break;
            }

        }

    }



    /**
     * Prints either all the details of all the donors in the database, or prints selected information about a single
     * donor depending on the value of 'target'.
     *
     * @param target a string of the target to print. Will be either a valid NHI code or 'donors'.
     * @param object a string, either 'all', 'attributes', or 'organs'.
     */
    public void issueView(String target, String object) {

        if (target.equals("donors")) {
            if (object.equals("all")) {
                System.out.println(toString());
            } else if (object.equals("organs")) {

                for (DonorReceiver donorReceiver : donorReceivers) {
                    System.out.print("Organ Donation list for " + donorReceiver.getUserName() + ":\n");
                    viewObject(donorReceiver, "organs");
                }
            } else if (object.equals("attributes")) {
                for (DonorReceiver donorReceiver : donorReceivers) {
                    System.out.print("Attribute list for " + donorReceiver.getUserName() + ":\n");
                    viewObject(donorReceiver, "attributes");
                }
            } else {
                System.out.println("ERROR: Unknown object " + object + ". " + ". For donors, object should be 'all'," +
                        "'organs', or 'attributes'.\n");
            }
        } else {
            boolean found = false;
            for (DonorReceiver donorReceiver : donorReceivers) {
                if (donorReceiver.getUserName().equals(target)) {
                    found = true;
                    viewObject(donorReceiver, object);
                }
            }

            if (!found) {
                System.out.println("ERROR: NHI Code " + target + " not found in database. " +
                        "Did you spell it right, is it in uppercase? Check the read me for a list of available commands\n");
            }
        }
    }


    /**
     * prints the details specified by the given object, of all donors in the database whose name matches the given first
     * and last name.
     *
     * @param first  A string of the first name of the donor.
     * @param last   A string of the last name of the donor.
     * @param object a string, either 'all', 'attributes', 'log', or 'organs'.
     */
    public void issueView(String first, String last, String object) {

        ArrayList<DonorReceiver> matches = new ArrayList<DonorReceiver>();

        for (DonorReceiver donorReceiver : donorReceivers) {
            if (donorReceiver.getFirstName().equals(first) && donorReceiver.getLastName().equals(last)) {
                matches.add(donorReceiver);
            }
        }

        if (matches.size() == 0) {
            System.out.println("ERROR: " + first + " " + last + " not found in database. Did you spell it right? " +
                    "Try searching using the donor NHI code instead. Check the read me for a list of available commands\n");
        } else {
            System.out.println(matches.size() + " match(s) for the name " + first + " " + last + ".\n");
            for (DonorReceiver acc : matches) {
                viewObject(acc, object);
            }
        }
    }

    /**
     * Searchs for accounts by name. Returns an ArrayList containing matching
     * accounts. If an empty string is passed, it returns all accounts.
     * @param value The value to search by.
     * @return A list of accounts containing the value in their name.
     */
    public ArrayList<DonorReceiver> getAccountsByName(String value) {

        // Handle special case where an empty string is the search value.
        if (value.equals("")) {

            return new ArrayList<DonorReceiver>(donorReceivers);

        }

        // Otherwise iterate through and search.
        ArrayList<DonorReceiver> matches = new ArrayList<DonorReceiver>();
        value = value.replaceAll("\\s","").toLowerCase();

        for (DonorReceiver donorReceiver : donorReceivers) {

            String fullName = donorReceiver.getFirstName() + donorReceiver.getMiddleName() + donorReceiver.getLastName();
            fullName = fullName.replaceAll("\\s","").toLowerCase(); // Remove any whitespace.

            if (fullName.contains(value)) {

                matches.add(donorReceiver);

            }

        }

        return matches;

    }


    /**
     * Searchs for accounts by NHI. Returns an ArrayList containing all matching
     * DonorReceiver objects, or all DonorReceiver objects if searching for an empty string.
     * @param value The value to search by.
     * @return A list of accounts containing the value in their NHI.
     */
    public ArrayList<DonorReceiver> getAccountsByNHI(String value) {

        // Handle special case where an empty string is the search value.
        if (value.equals("")) {

            return new ArrayList<DonorReceiver>(donorReceivers);

        }

        // Otherwise iterate through and search.
        ArrayList<DonorReceiver> matches = new ArrayList<DonorReceiver>();

        for (DonorReceiver donorReceiver : donorReceivers) {

            if (donorReceiver.getUserName().contains(value)) {

                matches.add(donorReceiver);

            }

        }

        return matches;

    }


    /**
     * Issues an update command to the App depending on the given parameters.
     * @param modifyingAccount The account performing the update.
     * @param target    a string NHI code of the target account for the update. Cannot be 'all'.
     * @param object    a string representation of the object to be updated. This being 'profile', 'attributes', or 'organs'.
     * @param attribute a string representation of the attribute to be updated.
     * @param value     a string representation of the new value that attribute will be updated as.
     */
    public void issueUpdate(User modifyingAccount, String target, String object, String attribute, String value) {
        if (target.equals("donors")) {
            System.out.println("ERROR: target = donors. You cannot update all donors at once!" +
                    " The target should be the NHI code of the donor you wish to update.\n");
        } else {
            boolean found = false;
            for (DonorReceiver donorReceiver : donorReceivers) {
                if (donorReceiver.getUserName().equals(target)) {
                    found = true;
                    updateObject(modifyingAccount, donorReceiver, object, attribute, value);
                }
            }
            if (!found) {
                System.out.println("ERROR: NHI Code " + target + " not found in database. " +
                        "Did you spell it right, is it in uppercase? Check the read me for a list of available commands\n");
            }
        }
    }

    /**
     * Issues an update command to the App depending on the given parameters.
     * @param modifyingAccount The account performing the update.
     * @param target    a string NHI code of the target account for the update. Cannot be 'all'.
     * @param object    a string representation of the object to be updated. This being 'profile', 'attributes', or 'organs'.
     * @param attribute a string representation of the attribute to be updated.
     * @param value     a string representation of the new value that attribute will be updated as.
     * @param donReceiver a string representation of whether the donating organ is in receiving or donating
     */
    public void issueUpdate(User modifyingAccount, String target, String object, String donReceiver, String attribute, String value) {
        if (target.equals("donors")) {
            System.out.println("ERROR: target = donors. You cannot update all donors at once!" +
                    " The target should be the NHI code of the donor you wish to update.\n");
        } else {
            boolean found = false;
            for (DonorReceiver donorReceiver : donorReceivers) {
                if (donorReceiver.getUserName().equals(target)) {
                    found = true;
                    updateObject(modifyingAccount, donorReceiver, object, donReceiver, attribute, value);
                }
            }
            if (!found) {
                System.out.println("ERROR: NHI Code " + target + " not found in database. " +
                        "Did you spell it right, is it in uppercase? Check the read me for a list of available commands\n");
            }
        }
    }


    /**
     * Issues a clinician update command to the App depending on the given parameters. If clinician given by the given target integer
     * is not in the database, an error message is given.
     * @param modifyingAccount The account performing the update.
     * @param target an integer of the clinician staff ID which identifies the specific clinician to update.
     * @param attribute A string representation of the attribute. It should be one of the clinician attributes.
     * @param value A string representation of the new value of the attribute. It should be a legal value.
     */
    public void issueClinicianUpdate(User modifyingAccount, int target, String attribute, String value) {
        boolean found = false;
        for(Clinician clinician: clinicians) {
            if (String.valueOf(target).equals(clinician.getUserName())) {
                found = true;
                updateClinicianAttribute(clinician, modifyingAccount, attribute, value);
                break;
            }
        }
        if (!found) {
            System.out.println("ERROR: Staff ID " + target + " not found in database. " +
                    "Did you input it correctly? Check the read me for a list of available commands\n");
        }
    }

    /**
     * Updates the given clinician attribute with the given value if both parameters are valid.
     * If the process was successful, a log is added to the clinician modifications log list. Otherwise an error
     * message is displayed.
     * @param clinician The clinician to be modified.
     * @param modifyingAccount The account modifying the clinician.
     * @param attribute A string representation of the attribute. It should be one of the clinician attributes
     * @param value A string representation of the new value of the attribute. It should be a legal value.
     */
    public void updateClinicianAttribute(Clinician clinician, User modifyingAccount, String attribute, String value) {
        String message = "";
        String oldVal = "";
        switch(attribute) {
            case "givenName": {
                oldVal = clinician.getFirstName();
                message = clinician.updateGivenName(value); break; }
            case "lastName": {
                oldVal = clinician.getLastName();
                message = clinician.updateLastName(value); break; }
            case "workAddress": {
                oldVal = clinician.getContactDetails().getAddress().getStreetAddressLn1();
                message = clinician.updateWorkAddress(value); break; }
            case "region": {
                oldVal = clinician.getContactDetails().getAddress().getRegion();
                message = clinician.updateRegion(value); break; }
            default: {message = "ERROR: unknown attribute " + attribute +". Attribute should be 'givenName', 'lastName'," +
                    " 'workAddress', or 'region'.\n"; break;}
        }
        String substring = message.substring(0, 5);
        if (!substring.equals("ERROR")){
            LogEntry logEntry = new LogEntry(clinician, modifyingAccount, attribute, oldVal, value);
            getSystemLog().add(logEntry);
            clinician.getModifications().add(logEntry);
        } else {
            clinician.setUpdateMessage(message);
        }
    }

    /**
     * an overloaded method to exit the program only if the given command string is 'exit'.
     *
     * @param command a string command that should be 'exit'
     */
    public void issueCommand(String command) {
        if (command.equals("exit")) {
            System.exit(1);
        } else {
            System.out.println("ERROR: Unknown command " + command + ". " +
                    "Did you spell it right? Check the read me for a list of available commands\n");
        }
    }


    /**
     * an overloaded method to issue a 'view' command to the App.
     *
     * @param command a string which should be 'view'.
     * @param target  a string NHI code of the account to view, or 'donors' for all donors.
     * @param object  a string representation of the object to be viewed. This being 'all', 'attributes', or 'organs'.
     */
    public void issueCommand(String command, String target, String object) {
        if (command.equals("view")) {
            issueView(target, object);
        } else {
            System.out.println("ERROR: Unknown command " + command + ". " +
                    "Did you spell it right? Check the read me for a list of available commands\n");
        }
    }

    /**
     * an overloaded method to issue the 'update' command to the App (for a donor).
     * @param modifyingAccount The account performing the update.
     * @param command   a string which should be 'update'
     * @param target    a string NHI code of the target account for the update. Cannot be 'all'.
     * @param object    a string representation of the object to be updated. This being 'profile', 'attributes', or 'organs'.
     * @param attribute a string representation of the attribute to be updated.
     * @param value     a string representation of the new value that attribute will be updated as.
     */
    public void issueCommand(User modifyingAccount, String command, String target, String object, String attribute, String value) {
        if (command.equals("update")) {
            issueUpdate(modifyingAccount, target, object, attribute, value);
        } else {
            System.out.println("ERROR: Wrong command " + command + ". " +
                    "Did you spell it right? Did you have too many parameters?" +
                    " Check the read me for a list of available commands\n");
        }
    }

    /**
     * an overloaded method to issue the 'update' command to the App.
     * @param modifyingAccount The account performing the update.
     * @param command   a string which should be 'update'
     * @param target    a string NHI code of the target account for the update. Cannot be 'all'.
     * @param object    a string representation of the object to be updated. This being 'profile', 'attributes', or 'organs'.
     * @param attribute a string representation of the attribute to be updated.
     * @param value     a string representation of the new value that attribute will be updated as.
     * @param donReceiver a string representation of whether the organ is donating receiving
     */
    public void issueCommand(User modifyingAccount, String command, String target, String object, String donReceiver, String attribute, String value) {
        if (command.equals("update")) {
            issueUpdate(modifyingAccount, target, object, donReceiver, attribute, value);
        } else {
            System.out.println("ERROR: Wrong command " + command + ". " +
                    "Did you spell it right? Did you have too many parameters?" +
                    " Check the read me for a list of available commands\n");
        }
    }


    /**
     * an overloaded method to view a donor's details by searching the db using their name.
     *
     * @param command a string which should be 'view'.
     * @param first   a string of the donor's first name.
     * @param last    a string of the donor's last name
     * @param object  a string representation of the object to be viewed. This being 'all', 'attributes', 'log' or 'organs'.
     */
    public void issueCommand(String command, String first, String last, String object) {
        if (command.equals("view")) {
            issueView(first, last, object);
        } else {
            System.out.println("ERROR: Unknown command " + command + ". " +
                    "Did you spell it right? Check the read me for a list of available commands\n");
        }
    }


    /**
     * An overloaded method to update a clinician's attributes.
     * @param modifyingAccount The account performing the update.
     * @param command A String of the command to issue, it should be 'update'
     * @param target an integer of the clinician staff ID which identifies the specific clinician to update.
     * @param attribute A string representation of the attribute. It should be one of the clinician attributes.
     * @param value A string representation of the new value of the attribute. It should be a legal value.
     */
    public void issueCommand(User modifyingAccount, String command, int target, String attribute, String value) {
        if (command.equals("update")) {
            issueClinicianUpdate(modifyingAccount, target, attribute, value);
        } else {
            System.out.println("ERROR: Unknown command " + command + ". " +
                    "Did you spell it right? Check the read me for a list of available commands\n");
        }
    }


    /**
     * an overloaded method to delete a donor from the account manager, if the donor exists. The deletion is
     * only permanent if the 'save' command is used subsequently.
     * @param modifyingAccount The account performing the update.
     * @param command a string which should be "delete".
     * @param target  a string of the nhi code  (e.g. "ABC1234") of the donor to be deleted.
     */
    public void issueCommand(User modifyingAccount, String command, String target) {
        if (command.equals("delete".toLowerCase())) {
            issueDelete(modifyingAccount, target);
        } else {
            System.out.println("ERROR: Unknown command " + command + " is not the delete command. " +
                    "Did you spell it right? Check the read me for a list of available commands\n");
        }
    }


    public ArrayList<DonorReceiver> getAccountList() {
        return donorReceivers;
    }


    /**
     * Calls exportAccounts method in Marshal class to export current accounts.
     * @return Returns a string containing the result.
     */
    public String exportAccounts() {

        try {
            updateMasterListUponSave();
            repopulateAccounts();
            Marshal.exportAccounts(donorReceiverMasterList);
            Marshal.exportSystemLog(systemLog);
            String message = "Accounts successfully saved\n";
            System.out.println(message);
            return message;

        } catch (IOException exception) {

            String message = "ERROR: Accounts could not be exported.\n";
            System.out.println(message);
            return message;

        }

    }

    /**
     * updates the master list of accounts with all those in the Accounts array and in the Twilight array. Thus
     * The old account records will be overwritten by any account with saved changes. Note that the logs of reactivated accounts
     * will be merged with logs of new changes.
     */
    public void updateMasterListUponSave() {
        ArrayList<DonorReceiver> intermediate = new ArrayList<>();
        for (DonorReceiver acc: twilightZone) {
            intermediate.add(acc);
        }
        for (DonorReceiver acc: donorReceivers) {
            if (intermediate.contains(acc)) { // account was deleted and recreated in same session
                DonorReceiver old = intermediate.get(intermediate.lastIndexOf(acc));
                ArrayList<LogEntry> oldLogs = old.getUpDateLog();
                ArrayList<LogEntry> newLogs = acc.getUpDateLog();
                System.out.println(newLogs);
                for (LogEntry log: newLogs) { // we add any new logs to the old account's updateLog.
                    if(!oldLogs.contains(log)) {
                        oldLogs.add(log);
                    }
                }
                acc.setUpdateLog(oldLogs); // now the reactivated account has a deletion and reactivation log.
                intermediate.set(intermediate.lastIndexOf(acc), acc);
            }
            else{
                intermediate.add(acc);
            }
        }
        for (DonorReceiver acc: donorReceiverMasterList) {

            if (!intermediate.contains(acc)) {
                intermediate.add(acc);
            }
        }
        donorReceiverMasterList = intermediate;
        twilightZone.clear();
    }

    /**
     * updates the master list of accounts with all those in the Accounts array and in the Twilight array. Thus
     * The old account records will be overwritten by any account with saved changes. Note that the logs of reactivated accounts
     * will be merged with logs of new changes.
     */
    public void updateAdminMasterListUponSave() {
        ArrayList<Administrator> intermediate = new ArrayList<>();
        intermediate.addAll(adminTwilightZone);
        for (Administrator acc: administrators) {
            if (intermediate.contains(acc)) { // account was deleted and recreated in same session
                Administrator old = intermediate.get(intermediate.lastIndexOf(acc));
                intermediate.set(intermediate.lastIndexOf(acc), acc);
            }
            else{
                intermediate.add(acc);
            }
        }
        for (Administrator acc: administratorMasterList) {

            if (!intermediate.contains(acc)) {
                intermediate.add(acc);
            }
        }
        administratorMasterList = intermediate;
        adminTwilightZone.clear();
    }

    /**
     * updates the master list of accounts with all those in the Accounts array and in the Twilight array. Thus
     * The old account records will be overwritten by any account with saved changes. Note that the logs of reactivated accounts
     * will be merged with logs of new changes.
     */
    public void updateClinicianMasterListUponSave() {
        ArrayList<Clinician> intermediate = new ArrayList<>();
        intermediate.addAll(clinicianTwilightZone);
        for (Clinician acc: clinicians) {
            if (intermediate.contains(acc)) { // account was deleted and recreated in same session
                Clinician old = intermediate.get(intermediate.lastIndexOf(acc));
                intermediate.set(intermediate.lastIndexOf(acc), acc);
            }
            else{
                intermediate.add(acc);
            }
        }
        for (Clinician acc: clinicianMasterList) {

            if (!intermediate.contains(acc)) {
                intermediate.add(acc);
            }
        }
        clinicianMasterList = intermediate;
        clinicianTwilightZone.clear();
    }

    private boolean inTransplantList(DonorReceiver donorReceiver) {
        boolean found = false;
        for (DonorReceiver acc: transplantWaitingList) {
            if (acc.getUserName().equals(donorReceiver.getUserName())) {
                found = true;
            }
        }
        return found;
    }

    public void updateTransplantWaitingList() {
        for (DonorReceiver acc: donorReceiverMasterList) {
            if (acc.getReceiver()) {
                if (!inTransplantList(acc)) {
                    transplantWaitingList.add(acc);
                }
            } else {
                if (inTransplantList(acc)) {
                    transplantWaitingList.remove(acc);
                }
            }
        }
    }


    /**
     * repopulates the donorReceivers arraylist with every active account in the master list
     */
    public void repopulateAccounts() {
        donorReceivers.clear();
        for (DonorReceiver acc: donorReceiverMasterList) {
            if (acc.isActive()) {
                donorReceivers.add(acc);
            }
        }
        clinicians.clear();
        for (Clinician acc: clinicianMasterList) {
            if (acc.isActive()) {
                clinicians.add(acc);
            }
        }

        administrators.clear();
        for (Administrator acc: administratorMasterList) {
            if (acc.isActive()) {
                administrators.add(acc);
            }
        }
    }


    /**
     * Calls the importAccounts method in the Marshal class to import a new
     * set of accounts from the accounts directory.
     * @return A string containing the result.
     */
    public String importAccounts() {

        try {

            donorReceiverMasterList = Marshal.importAccounts();
            repopulateAccounts();
            String message = ("Accounts successfully imported.\n");
            System.out.println(message);
            return message;

        } catch (IOException exception) {

            String message = ("ERROR: One or more of the account files may have been corrupted.\n");
            System.out.println(message);
            return message;
        }

    }


    /**
     * Removes leading whitespace from a string.
     * @param string The string that is to be processed.
     * @return The original string with leading whitespace removed.
     */
    public String removeLeadingWhitespace(String string) {

        int index = 0;

        while (index < string.length() &&
                Character.isWhitespace(string.charAt(index))) {

            index += 1;

        }

        return string.substring(index);

    }


    public boolean checkNHIExists(String nhi) {
        for(DonorReceiver donorReceiver : donorReceivers){
            if(donorReceiver.getUserName().equals(nhi))
            {return true;}
        }
        return false;
    }


    public ArrayList<DonorReceiver> getMasterList() {
        return donorReceiverMasterList;
    }


    /**
     * Gets all the logs in the system log and returns a string of all the logs.
     * @return A string of all system logs.
     */
    public String getLogsAsString() {
        String logs = "";
        for (LogEntry log:systemLog) {
            logs += log.toString() + "\n";
        }
        logs += "\n";
        return logs;
    }

    /**
     * Getter method for the system log array list.
     * @return system log of application
     */
    public static ObservableList<LogEntry> getSystemLog() {
        return systemLog;
    }

    /**
     * Setter method for the system log array list.
     * @param systemLog An array list of log strings.
     */
    public void setSystemLog(ArrayList<LogEntry> systemLog) {
        this.systemLog = FXCollections.observableArrayList(systemLog);
    }


    /**
     * Adds a listener to the system log, allowing actions to be performed on
     * the system log after an update.
     *
     * @param listener A new ListChangeListener.
     * @throws NullPointerException When systemLog has not been initialised.
     */
    public static void addSystemLogListener(ListChangeListener<LogEntry> listener) throws NullPointerException {

        if (systemLog != null) {

            systemLog.addListener(listener);

        } else {

            throw new NullPointerException(listener.toString() + " could not be added because systemLog is null. It must be initialised.");

        }

    }


    /**
     * The getter method for the arraylist of clinicians.
     * @return The arraylist of clinicians.
     */
    public ArrayList<Clinician> getClinicians() {
        return clinicians;
    }

    /**
     * The setter method for the arraylist of clinicians.
     * @param clinicians The arraylist of clinicians.
     */
    public void setClinicians(ArrayList<Clinician> clinicians) {
        this.clinicians = clinicians;
    }


    /**
     * Adds a clinician object to the clinician array list.
     * @param clinician A clinician administrator object.
     */
    public void addToClincians(Clinician clinician) {
        this.clinicians.add(clinician);
    }


    /**
     * Checks if the clinicians array list is empty, and if it is, populates it with a default clinician object. This ensures
     * that there is always a clinician object in the the clinicians array list upon application start up.
     */
    public void addClinicianIfNoneExists() {
        if(clinicians.size() == 0) {
            LocalDateTime now = LocalDateTime.now();
            ArrayList<LogEntry> modifications = new ArrayList<>();
            Clinician defaultClinician = new Clinician("Default Clinician", "", "Placeholder",new ContactDetails(
                    new Address("University of Canterbury", null, null,
                            null, "Canterbury", null, null), null, null, null),
                    "0", "admin", now, modifications);
            clinicians.add(defaultClinician);
        }
    }


    /**
     * Checks if the given staff id has not been used by other clinicians.
     * @param id a staff id.
     * @return Returns 'false' if the given ID is already in use, returns 'true' otherwise.
     */
    public boolean checkStaffIDIsNotUsed(String id) {
        for (Clinician clinician: clinicians) {
            if (id.equals(clinician.getUserName())) {
                return false;
            }
        }
        return true;
    }


    /**
     * Calls exportClinicians method in Marshal class to export current clinicians.
     */
    public void exportClinicians() {
        try {
            updateClinicianMasterListUponSave();
            repopulateAccounts();
            Marshal.exportClinicians(clinicianMasterList);
            System.out.println("Clinicians successfully saved.\n");

        } catch (IOException exception) {

            System.out.println("ERROR: Clinicians could not be exported.\n");

        }
    }


    /**
     * Calls the importClinicians method in the Marshal class to import a new
     * set of clinicians from the clinicians directory.
     */
    public void importClinicians() {

        try {
            clinicianMasterList = Marshal.importClinicians();
            repopulateAccounts();
            System.out.println("Clinicians successfully imported.\n");
        } catch (IOException exception) {
            System.out.println("ERROR: One or more of the clinician files may have been corrupted.\n");
        }

    }


    /**
     * Checks if the given username corresponds to an administrator account in the database. If it does, the administrator
     * User object is returned (or null if it does not).
     * @param username A string of the username to be checked.
     * @return Returns the administrator user object corresponding to the given username  if it exists, null otherwise.
     */
    public Administrator getAdminIfItExists(String username) {
        Administrator user = null;
        for (Administrator admin : administrators) {
            if (username.equals(admin.getUserName())) {
                user = admin;
            }
        }
        return user;
    }


    /**
     * Checks if the administrator array list is empty, and if it is, populates it with a default administrator object. This ensures
     * that there is always a administrator object in the the administrator array list upon application start up.
     */
    public void addDefaultAdminIfNoneExists() {
        if(administrators.size() == 0) {
            LocalDateTime now = LocalDateTime.now();
            ArrayList<LogEntry> modifications = new ArrayList<>();
            Administrator admin = new Administrator("Default", "", "Administrator",
                    new ContactDetails(null, null, null, null),
                    "Sudo", "001", now, modifications);
            administrators.add(admin);
        }
    }


    /**
     * Calls exportAdmins method in Marshal class to export current admins.
     */
    public void exportAdmins() {
        try {
            updateAdminMasterListUponSave();
            repopulateAccounts();
            Marshal.exportAdministrators(administratorMasterList);
            System.out.println("Admins successfully saved.\n");

        } catch (IOException exception) {

            System.out.println("ERROR: Admins could not be exported.\n");

        }
    }


    /**
     * Calls the importAdministrators method in the Marshal class to import a new
     * set of admins from the administrator directory.
     */
    public void importAdmins() {
        try {

            administratorMasterList = Marshal.importAdministrators();
            repopulateAccounts();
            System.out.println("Admins successfully imported.\n");
        } catch (IOException exception) {
            System.out.println("ERROR: One or more of the admins files may have been corrupted.\n");
        }

    }



    /**
     * Inserts a admin into the database administrator list. If the admin already exists, it is overwritten by the
     * given admin but ONLY IF the given 'willSearch' boolean parameter is set to 'true' otherwise a duplicate will be added.
     * Precondition: IT IS UP TO THE CALLER OF THIS FUNCTION TO ALREADY KNOW IF THE ADMIN EXISTS OR NOT AND SET 'WILLSEARCH' APPROPRIATELY.
     * Precondition: The admin to be added cannot be the default admin whose username is 'Sudo' as it is treated as immutable.
     * @param admin A Administrator object to be added to the database.
     * @param willSearch A boolean to specify whether the administrator list will be searched in order to replace the existing clinician.
     */
    public void insertAdmin(Administrator admin, boolean willSearch) {
        if (!admin.getUserName().equals("Sudo")) { // We cannot replace the default Admin!
            if (willSearch) {
                for (Administrator ad: administratorMasterList) {
                    if (ad.getUserName().equals(ad.getUserName())) {
                        administratorMasterList.set(getAdministrators().indexOf(ad), admin);
                        break;
                    }
                }
            } else {
                administratorMasterList.add(admin);
            }
        }
    }



    /**
     * Inserts a clinician into the database clinicians list. If the clincian already exists, it is overwritten by the
     * given clinician but ONLY IF the given 'willSearch' boolean parameter is set to 'true' otherwise a duplicate will be added.
     * Precondition: IT IS UP TO THE CALLER OF THIS FUNCTION TO ALREADY KNOW IF THE CLINICIAN EXISTS OR NOT AND SET 'WILLSEARCH' APPROPRIATELY.
     * Precondition: The clinician to be added cannot be the default clinician whose username is '0' as it is treated as immutable.
     * @param clinician A clinician object to be added to the database.
     * @param willSearch A boolean to specify whether the clinician list will be searched in order to replace the existing clinician.
     */
    public void insertClinician(Clinician clinician, boolean willSearch) {
        if (!clinician.getUserName().equals("0")) { // We cannot replace the default clinician!
            if (willSearch) {
                for (Clinician clin : clinicianMasterList) {
                    if (clin.getUserName().equals(clinician.getUserName())) {
                        clinicianMasterList.set(clinicianMasterList.indexOf(clin), clinician);
                        break;
                    }
                }
            } else {
                clinicianMasterList.add(clinician);
            }
        }
    }


    /**
     * Inserts a donor/receiver into the database donorReceiver list. If the donor already exists, it is overwritten by the
     * given donor but ONLY IF the given 'willSearch' boolean parameter is set to 'true' otherwise a duplicate will be added.
     * Precondition: IT IS UP TO THE CALLER OF THIS FUNCTION TO ALREADY KNOW IF THE DONOR EXISTS OR NOT AND SET 'WILLSEARCH' APPROPRIATELY.
     * @param donor A donorReceiver object to be added to the database.
     * @param willSearch A boolean to specify whether the donor list will be searched in order to replace the existing donor.
     */
    public void insertDonor(DonorReceiver donor, boolean willSearch) {
        if (willSearch) {
            for (DonorReceiver don : getDonorReceivers()) {
                if (don.getUserName().equals(donor.getUserName())) {
                    getDonorReceivers().set(getDonorReceivers().indexOf(don), donor);
                    break;
                }
            }
        } else {
            getDonorReceivers().add(donor);
        }

    }

}
