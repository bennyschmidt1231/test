package seng302.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import seng302.App;
import seng302.model.AccountManager;
import seng302.model.UserAttributeCollection;
import seng302.model.person.DonorReceiver;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Controller for command line gui
 */
public class CliTabController {

    /**
     * TextField for the command line input
     */
    @FXML
    TextField cliInput;

    /**
     * ListView where the command line out put will be displayed
     */
    @FXML
    ListView cliOutput;

    ObservableList commandOutput = FXCollections.observableArrayList();

    /**
     * List of previous command by the clinician
     */
    private List previousCommands;

    /**
     * Current index of the command shown in cliInput
     */
    private int currentIndex;


    private AccountManager database;

    /**
     * Initialize the CLI tab
     */
    @FXML
    private void initialize() {
        database = App.getDatabase();
        database.importAccounts();
        database.importClinicians();
        database.addClinicianIfNoneExists();
        previousCommands = new ArrayList();
        currentIndex = -1;
        cliInput.setOnKeyPressed(new EventHandler<javafx.scene.input.KeyEvent>() {
            @Override
            public void handle(javafx.scene.input.KeyEvent event) {
                if (event.getCode() == KeyCode.UP) {
                    goBackward();
                } else if (event.getCode() == KeyCode.DOWN) {
                    goForward();
                }
            }
        });
        cliOutput.setItems(commandOutput);
    }

    /**
     * Occurs when the user selects up, will show the last command entered
     */
    private void goBackward() {

        if (this.currentIndex > 0) {
            currentIndex--;
            cliInput.setText((String) previousCommands.get(currentIndex));
        }
    }

    /**
     * Occurs when the user selects down, will show the next command after the current
     */
    private void goForward() {
        if (this.currentIndex < previousCommands.size()) {
            try {
                currentIndex++;
                cliInput.setText((String) previousCommands.get(currentIndex));
            } catch(IndexOutOfBoundsException e) {

            }

        } else {
            cliInput.setText("");
        }

    }


    /**
     * Takes command and runs in the cli
     */
    @FXML
    private void commandEnter() {
        commandControl(cliInput.getText());
        previousCommands.add(cliInput.getText());
        cliInput.setText("");
        currentIndex = previousCommands.size();
    }


    /**
     * Controls what events will happen with a given command
     * @param command Command entered by user
     */
    private void commandControl(String command) {
        if (command.startsWith("Create")) {
            command = command.replace("Create", "").trim();
            create(command);
        } else if (command.startsWith("View")) {
            command = command.replace("View", "").trim();
            if (command.startsWith("name")) {
                String[] names = command.split(" ");
                String[] trailing = Arrays.copyOfRange(names, 1, names.length);
                try {
                    issueView(trailing[0], trailing[1], trailing[2]);
                } catch (IndexOutOfBoundsException e) {
                    commandOutput.add(0, "ERROR: Names and/or Object not given");
                }
            } else if (command.startsWith("nhi")) {
                String[] nhi = command.split(" ");
                String[] trailing = Arrays.copyOfRange(nhi, 1, nhi.length);
                try {
                    issueView(trailing[0], trailing[1]);
                } catch (IndexOutOfBoundsException e) {
                    commandOutput.add(0, "ERROR: NHI and/or Object not given");
                }
            } else if (command.startsWith("donors")) {
                String[] donors = command.split(" ");
                String[] trailing = Arrays.copyOfRange(donors, 1, donors.length);
                try {
                    issueView("donors", trailing[0]);
                } catch (IndexOutOfBoundsException e) {
                    commandOutput.add(0, "ERROR: NHI and/or Object not given");
                }
            } else {
                commandOutput.add(0, "Invalid option for viewing. Required field is either Name or NHI");
            }
        } else if (command.startsWith("delete")) {
            String[] donors = command.split(" ");
            String[] trailing = Arrays.copyOfRange(donors, 1, donors.length);
            try {
                issueDelete(trailing[0]);
            } catch (IndexOutOfBoundsException e) {
                commandOutput.add(0, "ERROR: NHI not given for deletion");
            }
        } else if (command.startsWith("update")) {
            String[] update = command.split(" ");
            String[] trailing = Arrays.copyOfRange(update, 1, update.length);
            update(trailing);
        } else if (command.startsWith("save")) {
            commandOutput.add(0, database.exporter());
        } else if (command.startsWith("Import")) {
            commandOutput.add(0, database.importer());
        } else if (command.startsWith("Help")) {
            String[] help = command.split(" ");
            String[] trailing = Arrays.copyOfRange(help, 1, help.length);
            try {
                commandOutput.add(0, Help(trailing[0]));
            } catch (IndexOutOfBoundsException e) {
                commandOutput.add(0, "ERROR: Help not given");
            }
        } else {
            commandOutput.add(0, "Invalid command. Please try again");
        }
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
     * Take command details and call update methods
     * @param trailingCommand command details to be used
     */
    public void update(String[] trailingCommand) {
        try {
            String NHI = trailingCommand[0];
            String object = trailingCommand[1];
            if (object.equals("organs")) {
                String donReceiver = trailingCommand[2];
                String detailUpdated = trailingCommand[3];
                String updated = trailingCommand[4];
                issueUpdate(NHI, object, donReceiver, detailUpdated, updated.trim());
            } else {
                String detailUpdated = trailingCommand[2];
                String updated = trailingCommand[3];
                issueUpdate(NHI, object, detailUpdated, updated.trim());
            }
        } catch(IndexOutOfBoundsException e) {
            commandOutput.add(0, "ERROR: Insufficient information provided for profile update");
        }


    }

    /**
     * Given update details, update the profile
     * @param target Account to be updated
     * @param object Object to be updated
     * @param attribute Attribute to be updated
     * @param value New value of attribute
     */
    public void issueUpdate(String target, String object, String attribute, String value) {
        ArrayList<DonorReceiver> accounts = database.getDonorReceivers();
        if (target.equals("donors")) {
            commandOutput.add(0, "ERROR: target = donors. You cannot update all donors at once!" +
                    " The target should be the NHI code of the donor you wish to update.");
        } else {
            boolean found = false;
            for (DonorReceiver account : accounts) {
                if (account.getUserName().equals(target)) {
                    found = true;
                    updateObject(account, object, attribute, value);
                }
            }
            if (!found) {
                commandOutput.add(0, "ERROR: NHI Code " + target + " not found in database. " +
                        "Did you spell it right, is it in uppercase? Check the read me for a list of available commands");
            }
        }
    }

    /**
     * Given update details, update the profile
     * @param target Account to be updated
     * @param object Object to be updated
     * @param donReceiver Update the organs of donating or receiving
     * @param attribute Attribute to be updated
     * @param value New value of attribute
     */
    public void issueUpdate(String target, String object, String donReceiver, String attribute, String value) {
        ArrayList<DonorReceiver> accounts = database.getDonorReceivers();
        if (target.equals("donors")) {
            commandOutput.add(0, "ERROR: target = donors. You cannot update all donors at once!" +
                    " The target should be the NHI code of the donor you wish to update.");
        } else {
            boolean found = false;
            for (DonorReceiver account : accounts) {
                if (account.getUserName().equals(target)) {
                    found = true;
                    updateObject(account, object, donReceiver, attribute, value);
                }
            }
            if (!found) {
                commandOutput.add(0, "ERROR: NHI Code " + target + " not found in database. " +
                        "Did you spell it right, is it in uppercase? Check the read me for a list of available commands");
            }
        }
    }

    /**
     * Updates a given account
     * @param account Account to be updated
     * @param object Object to be updated in account
     * @param attribute Attribute in account to be updated
     * @param value New value of attribute
     */
    public void updateObject(DonorReceiver account, String object, String attribute, String value) {
        String message;
        if (object.equals("profile")) {
            message = account.updateProfile(account, attribute, value);
        } else if (object.equals("attributes")) {
            message = account.updateAttribute(account, attribute, value);
        } else if (object.equals("organs")) {
            message = account.updateOrgan(account, "donor", attribute, value);
        } else if (object.equals("contacts")) {
            String[] listMessage = account.getContactDetails().updateContact(attribute, value);
            message = Arrays.toString(listMessage);

        } else {
            message = "ERROR: Unknown object " + object + ". Object should be 'profile', 'attributes', 'organs' or 'contacts'.";
        }
        commandOutput.add(0, message);
    }

    /**
     * Updates a given account
     * @param account DonorReceiver to be updated
     * @param object Object to be updated
     * @param donReceiver Update the organs of donating or receiving
     * @param attribute Attribute to be updated
     * @param value New value of attribute
     */
    public void updateObject(DonorReceiver account, String object, String donReceiver, String attribute, String value) {
        String message;
        if (object.equals("profile")) {
            message = account.updateProfile(account, attribute, value);
        } else if (object.equals("attributes")) {
            message = account.updateAttribute(account, attribute, value);
        } else if (object.equals("organs")) {
            message = account.updateOrgan(account, donReceiver,attribute, value);
        } else if (object.equals("contacts")) {
            String[] listMessage = account.getContactDetails().updateContact(attribute, value);
            message = Arrays.toString(listMessage);
        } else {
            message = "ERROR: Unknown object " + object + ". Object should be 'profile', 'attributes', 'organs' or 'contacts'.";
        }
        commandOutput.add(0, message);
    }


    /**
     * Creates a new user
     * @param information Donor information for creation
     */
    public void create(String information) {
        String givenName = " ";
        String otherName = " ";
        String lastName = " ";
        LocalDate localDate;
        String NHI;
        boolean test = information.contains("name=");
        boolean test2 = information.contains("dateOfBirth=");
        boolean test3 = information.contains("nhi=");
        if ((!test) | (!test2) | (!test3)) {
            commandOutput.add(0, "In state create, but missing required fields. Correct template is name=<Name> dateOfBirth=<date of birth> nhi=<nhi number>");
            return;
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
        if ((!test4) | (!test5) | (!test6)) {
            commandOutput.add(0, "Correct tokens provided, but not in the correct order. Correct order is name=<Name> dateOfBirth=<date of birth> nhi=<nhi number>. Returning to main menu");
            return;
        }
        String Name = substringOne.substring(substringOne.indexOf('=') + 1);
        Name = Name.trim();
        givenName = parseGivenName(Name);
        otherName = parseOtherNames(Name);
        lastName = parseLastName(Name);
        boolean testGivenName = UserAttributeCollection.validateAlphanumericString(false, givenName, 1, 50);
        boolean testOtherName = UserAttributeCollection.validateAlphanumericString(false, givenName, 1, 50);
        boolean testLastName = UserAttributeCollection.validateAlphanumericString(false, givenName, 1, 50);
        if ((!testGivenName) | (!testOtherName) | (!testLastName)) {
            commandOutput.add(0, "That name is invalid, as it it is not alphabetical. Returning to main menu");
            return;
        }
        if (givenName == null) {
            commandOutput.add(0, "A name is required for the profile. Please try again.");
            return;
        }
        String dateOfBirth = substringTwo.substring(substringTwo.indexOf('=') + 1);
        dateOfBirth = dateOfBirth.trim();
        localDate = convertToLocalDate(dateOfBirth);
        if (localDate == null) {
            commandOutput.add(0, "The format required for date is yyyyMMdd. Returned to main menu");
            return;
        }
        NHI = substringThree.substring(substringThree.indexOf('=') + 1);
        boolean testNHI = database.validateNHI(NHI);
        if ((!testNHI) | (database.checkUsedNHI(NHI))) {
            commandOutput.add(0, "Either you have entered an invalid NHI number or there is already a profile with that number. Transaction cancelled");
        } else {
            DonorReceiver newAccount = new DonorReceiver(givenName, otherName, lastName, localDate, NHI);
            newAccount = database.reactivateOldAccountIfExists(null, newAccount);
            database.getDonorReceivers().add(newAccount);
        }
        if (lastName == null) {
            commandOutput.add(0, "Account created with name: " + givenName + ", Date of birth: " + localDate.toString() + " and NHI: " + NHI);
        } else if (otherName == null) {
            commandOutput.add(0, "Account created with name: " + givenName + ' ' + lastName + ", Date of birth: " + localDate.toString() + " and NHI: " + NHI);
        } else {
            commandOutput.add(0, "Account created with name: " + givenName + ' ' + otherName + ' ' + lastName + ", Date of birth: " + localDate.toString() + " and NHI: " + NHI);
        }
    }


    /**
     * View details of the account
     * @param first Given Name of account
     * @param last Family name of account
     * @param object information to display
     */
    public void issueView(String first, String last, String object) {

        ArrayList<DonorReceiver> matches = new ArrayList<DonorReceiver>();
        ArrayList<DonorReceiver> accounts = database.getDonorReceivers();
        for (DonorReceiver account : accounts) {
            if (account.getFirstName().equals(first) && account.getLastName().equals(last)) {
                matches.add(account);
            }
        }

        if (matches.size() == 0) {
            commandOutput.add(0, "ERROR: " + first + " " + last + " not found in database. Did you spell it right? " +
                    "Try searching using the donor NHI code instead. Check the read me for a list of available commands");
        } else {
            commandOutput.add(0, matches.size() + " match(s) for the name " + first + " " + last + ".\n");
            for (DonorReceiver acc : matches) {
                viewObject(acc, object);
            }
        }
    }

    /**
     * View details of the account
     * @param target nhi of the account
     * @param object information to display
     */
    public void issueView(String target, String object) {

        ArrayList<DonorReceiver> accounts = database.getDonorReceivers();
        if (target.equals("donors")) {
            if (object.equals("all")) {
                commandOutput.add(0, database.toString());
            } else if (object.equals("organs")) {

                for (DonorReceiver account : accounts) {
                    commandOutput.add(0, "Organ Donation list for " + account.getUserName() + ":\n");
                    viewObject(account, "organs");
                }
            } else if (object.equals("attributes")) {
                for (DonorReceiver account : accounts) {
                    commandOutput.add(0, "Attribute list for " + account.getUserName() + ":\n");
                    viewObject(account, "attributes");
                }
            } else {
                commandOutput.add(0, "ERROR: Unknown object " + object + ". " + ". For donors, object should be 'all'," +
                        "'organs', or 'attributes'.\n");
            }
        } else {
            boolean found = false;
            for (DonorReceiver account : accounts) {
                if (account.getUserName().equals(target)) {
                    found = true;
                    viewObject(account, object);
                }
            }

            if (!found) {
                commandOutput.add(0, "ERROR: NHI Code " + target + " not found in database. " +
                        "Did you spell it right, is it in uppercase? Check the read me for a list of available commands\n");
            }
        }
    }


    /**
     * Deletes the selected account
     * @param target nhi of the account to delete
     */
    public void issueDelete(String target) {
        ArrayList<DonorReceiver> accounts = database.getDonorReceivers();
        ArrayList<DonorReceiver> found = new ArrayList<DonorReceiver>();
        for (DonorReceiver account : accounts) {
            if (account.getUserName().equals(target)) {

                commandOutput.add(0, "Found account for nhi: " + target);
                try {
                    account.setActive(false);
//                    String log = account.logDonorReceiverStatus(account.getFirstName() + " " + account.getLastName(),
//                            account.getUserName(), "delete");
//                    account.logChange(log);
                } catch (IllegalArgumentException e) {}
            }
            found.add(account);
        }

        if (found.size() == 0) {
            commandOutput.add(0,"There was no donor found that matched the nhi code " + target + ". Did you spell it correctly?");
        } else {
            database.getTwilightZone().add(found.get(0));
            accounts.removeAll(found);
            commandOutput.add(0,"Deletion success, remember to save the application to make the action permanent.");
        }
    }



    /**
     * Prints certain details of the given account depending on the specified object:
     * -'all' prints all the details of the account.
     * -'attributes' prints only the account attributes information.
     * -'organs' prints only the accounts list of organs that will be donated.
     * ='log' prints all the logs in the update log of the given account.
     *
     * @param account a valid string NHI code to identify the account.
     * @param object  a string, either 'all', 'attributes', 'log', or 'organs'.
     */
    public void viewObject(DonorReceiver account, String object) {
        String message = "";
        commandOutput.add(0, "Accessing data for donor " + account.getUserName() + "...");
        if (object.equals("all")) {
            message = account.toString();
        } else if (object.equals("attributes")) {
            message = account.getUserAttributeCollection().toString();
        } else if (object.equals("organs")) {
            message = account.getDonorOrganInventory().toString();
        } else if (object.equals("contacts")) {
            message = account.getContactDetails().toString();
        } else if (object.equals("log")) {
            message = account.updateLogToString();
        } else {
            message = "ERROR: Unknown object " + object + ". Object should be 'all', 'attributes', 'log', 'organs' or 'contacts'.\n";
        }
        commandOutput.add(0, message);
    }


    /**
     * Takes a string inputted by the user, and parses the first name within the string.
     *
     * @param fullName The full name of the person
     * @return The first name of the account holder
     */
    public String parseGivenName(String fullName) {
        try {
            String givenName = fullName.substring(0, fullName.indexOf(' '));
            return givenName;
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
     *
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
     *
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
     * Parses an input token, and if the token is correct, prints a relevant message, and returns the user to the main menu in the command line
     * @param subHelp Which part of help to be shown
     * @return  Help String
     */
    public static String Help(String subHelp) {
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
            message+=("<object> - either 'profile', 'log', 'attributes' or 'organs'\n");
            message+=("========================================================================================\n");
            message+=("The required input for importing a json file into the system: import <filename> \n");
            message+=("<filename> - the file which you wish to import into the system\n");
            message+=("========================================================================================\n");
            message+=("The required input for exporting the current data to a persistent json file: export <filename>\n");
            message+=("<filename> - the file where you wish the data to be saved\n");
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
            message+=("<object> - either 'profile', 'log', 'attributes' or 'organs'\n");
            message+=("Please consult the user_manual for more information\n");
        } else if (subHelp.equalsIgnoreCase("import")) {
            message+=("The required input for importing a json file into the system: import <filename> \n");
            message+=("<filename> - the file which you wish to import into the system\n");
            message+=("Please consult the user_manual for more information\n");
        } else if (subHelp.equalsIgnoreCase("export")) {
            message+=("The required input for exporting the current data to a persistent json file: export <filename>\n");
            message+=("<filename> - the file where you wish the data to be saved\n");
            message+=("Please consult the user_manual for more information\n");
        } else if (subHelp.equalsIgnoreCase("delete")) {
            message+=("the required input for deleting an account: delete <nhi number>\n");
            message+=("<nhi number> - The unique number given to every patient in the New Zealand Health system.\n");
            message+=("For more information, please consult the user_manual\n");
        } else if (subHelp.equalsIgnoreCase("launch")) {
            message+=("Launches the GUI component of the application.\n");
            message+=("Can only be called once while the application is running.\n");
        } else {
            message+=("Invalid query. returning to the main menu...\n");
        }
        return message;
    }

}
