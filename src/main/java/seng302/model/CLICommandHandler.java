package seng302.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;
import seng302.App;

/**
 * The CLICommandHandler class handles all commands sent by the GUI and lets the separate Account
 * Type class handles like CLIAdminCommandHandler handle the implementation by giving it the
 * necessary information
 *
 * Refer to the eng-git wiki for how the commands should be structured
 */
public class CLICommandHandler {

  private AccountManager database;

  private CLIDonorReceiverCommandHandler donorReceiverCLI;
  private CLIClinicianCommandHandler clinicianCLI;
  // private CLIAdminCommandHandler adminCLI;

  /**
   * Default constructor Initializes the database to make sure its up to date with all modifications
   * Also initializes the other class instances as well with the same database.
   */
  public CLICommandHandler() {
    database = App.getDatabase();

    donorReceiverCLI = new CLIDonorReceiverCommandHandler(database);
    clinicianCLI = new CLIClinicianCommandHandler(database);
    // adminCLI = new CLIAdminCommandHandler(database);
  }

  /**
   * Attempts to match the target string, starting at the beginning of the string, against the
   * regex.
   *
   * @param target The string being inspected for a pattern.
   * @param regex The regular expression to search for in the string.
   * @return True if the regular expression is matched, False if it is not matched.
   */
  private boolean isLookingAtPattern(String target, String regex) {
    return Pattern.compile(regex).matcher(target).lookingAt();
  }

  /**
   * Routes the commands to the appropriate handlers.
   *
   * @param command Command entered by user
   * @return Returns a list of messages that are the result of the command. Message are in order of
   * occurrence i.e. the message that was added first is first in the list.
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
      try {
        String[] trailingCommand = command.split(" ");
        if (trailingCommand[0].equals("")) {
          throw new IndexOutOfBoundsException();
        }
        String accountType = trailingCommand[0];
        trailingCommand = Arrays.copyOfRange(trailingCommand, 1, trailingCommand.length);
        command = String.join(" ", trailingCommand);
        messages = create(accountType, command); // Redirect
      } catch (IndexOutOfBoundsException e) {
        messages.add("ERROR: Account type must be specified. Enter 'help create' for more help.");
      }
    } else if (isLookingAtPattern(command, viewRegex)) {
      String[] trailingCommand = command.split(" ");
      trailingCommand = Arrays.copyOfRange(trailingCommand, 1, trailingCommand.length);
      try {
        String accountType = trailingCommand[0];
        command = command.replaceFirst(viewRegex, "").trim();
        messages = view(accountType, command); // Redirect
      } catch (IndexOutOfBoundsException e) {
        messages.add(
            "ERROR: Account type and identifier must be specified. Enter 'help view' for more help.");
      }
    } else if (isLookingAtPattern(command, deleteRegex)) {
      String[] donors = command.split(" ");
      String[] trailing = Arrays.copyOfRange(donors, 1, donors.length);
      try {
        messages = delete(trailing[0], trailing[1]); // Redirect
      } catch (IndexOutOfBoundsException e) {
        messages.add("ERROR: NHI not given for deletion");
      }
    } else if (isLookingAtPattern(command, updateRegex)) {
      String[] updateCommand = command.split(" ");
      String[] trailing = Arrays.copyOfRange(updateCommand, 1, updateCommand.length);
      messages = update(trailing); // Redirect
    } else if (isLookingAtPattern(command, saveRegex)) {
      messages.add(database.exporter()); // Redirect
    } else if (isLookingAtPattern(command, importRegex)) {
      messages.add(database.importer()); // Redirect
    } else if (isLookingAtPattern(command, helpRegex)) {
      String[] help = command.split(" ");
      if (help.length == 1) {
        messages.add(help());
      } else {
        String[] trailing = Arrays.copyOfRange(help, 1, help.length);
        try {
          messages.add(help(trailing[0])); // Redirect
        } catch (IndexOutOfBoundsException e) {
          messages.add("ERROR: Type of help must be specified. Valid "
              + "options are: all, create, update, view, import, save, delete, launch."
              + " For example, 'help all' gives help with all commands.");
        }
      }
    } else {
      messages.add("Invalid command. Please try again.");
    }
    return messages;
  }

  /**
   * A create method that creates an appropriate account by letting the proper class handle it
   *
   * @param account The account type to create
   * @param command The raw command passed in since to create an account we need all the information
   * provided
   * @return An ArrayList of messages about the account creation
   */
  public ArrayList<String> create(String account, String command) {
    ArrayList<String> messages = new ArrayList<>();
    int accountType = accountType(account);
    try {
      switch (accountType) {
        case 0:
          messages = donorReceiverCLI.create(command);
          break;
        case 1:
          messages = clinicianCLI.create(command);
          break;
        case 2:
          // messages = adminCLI.create(command);
          break;
        case -1:
          messages.add("ERROR: " + accountType + " was not recognised as a valid account type");
          break;
      }
    } catch (IndexOutOfBoundsException e) {
      messages.add("ERROR: Insufficient information provided for account creation");
    }
    return messages;
  }

  /**
   * Takes the view command given to the command line (without the view keyword), and routes the
   * command to the correct view method depending on the type of account specified.
   *
   * @param account The account type specified, e.g. "donor", "clinician", "admin" etc.
   * @param command The command entered on the command line without the "view" keyword, e.g. command
   * = "clinician 0", where the original command entered was "view clinician 0".
   * @return Returns a list of messages that are the result of the command. Message are in order of
   * occurrence i.e. the message that was added first is first in the list.
   */
  public ArrayList<String> view(String account, String command) {
    ArrayList<String> messages = new ArrayList<>();
    int accountType = accountType(account);

    try {
      String[] commandArray = command.split(" ");
      commandArray = Arrays.copyOfRange(commandArray, 1, commandArray.length);
      if (commandArray.length == 0) {
        throw new IndexOutOfBoundsException();
      }
      command = String.join(" ", commandArray);
      switch (accountType) {
        case 0:
          messages = donorReceiverCLI.view(command);
          break;
        case 1:
          messages = clinicianCLI.view(command);
          break;
        case 2:
          // messages = adminCLI.view(command);
          break;
        case -1:
          messages.add("ERROR: Invalid account type");
          break;
      }
    } catch (IndexOutOfBoundsException e) {
      messages.add("ERROR: Insufficient information provided for viewing account.");
    }
    return messages;
  }

  /**
   * A delete method that deletes the account specified by its username (NHI for donors, Staff ID
   * for clinicians and username for admins)
   *
   * @param account The account type that we are trying to delete
   * @param username The username of the account to delete
   * @return An ArrayList of messages about the account to delete
   */
  public ArrayList<String> delete(String account, String username) {
    ArrayList<String> messages = new ArrayList<>();
    int accountType = accountType(account);

    try {
      switch (accountType) {
        case 0:
          messages = donorReceiverCLI.delete(username);
          break;
        case 1:
          messages = clinicianCLI.delete(username);
          break;
        case 2:
          // messages = adminCLI.delete(username);
          break;
        case -1:
          messages.add("ERROR: " + username + " was not recognised as a valid account of any type");
          break;
      }
    } catch (IndexOutOfBoundsException e) {
      messages.add("ERROR: Insufficient information provided for account deletion");
    }
    return messages;
  }


  /**
   * Routes an update command to the appropriate handlers based on the account type
   *
   * @param trailingCommand The command after the update keyword.
   * @return A list containing the messages that are the result of the update.
   */
  public ArrayList<String> update(String[] trailingCommand) {
    ArrayList<String> messages = new ArrayList<>();

    try {
      String account = trailingCommand[0];

      int accountType = accountType(account);

      trailingCommand = Arrays.copyOfRange(trailingCommand, 1, trailingCommand.length);

      switch (accountType) {
        case 0:
          messages = donorReceiverCLI.update(trailingCommand);
          break;
        case 1:
          messages = clinicianCLI.update(trailingCommand);
          break;
        case 2:
          // messages = adminCLI.update(trailingCommand);
          break;
        case -1:
          messages.add("ERROR: " + account + " was not recognised as a valid account of any type");
          break;
      }
    } catch (IndexOutOfBoundsException e) {
      messages.add("ERROR: Insufficient information provided for profile update");
    }

    return messages;
  }

  /**
   * Parses an input token for the help menu, and if the token is valid, and returns the relevant
   * help section. Valid tokens are 'all', 'create', 'update', 'view', 'import', 'save', 'delete',
   * and 'launch'.
   *
   * @param subHelp Which part of help to be shown
   * @return A string containing the relevant help section (or error message).
   */
  public String help(String subHelp) {
    String message = "";

    subHelp = subHelp.toLowerCase();

    switch (subHelp) {
      case "all":
        message = help();

      case "update":
        message += "Update commands";
        message += "\n----------\n----------\n";
        message += "Update Donor Command\n";
        message += donorReceiverCLI.help("update");
        message += "\n==========\n";
        message += "Update Clinician Command\n";
        message += clinicianCLI.help("update");
        // message += adminCLI.help("update");
        break;

      case "view":
        message += "View commands";
        message += "\n----------\n----------\n";
        message += "View Donor Command\n";
        message += donorReceiverCLI.help("view");
        message += "\n==========\n";
        message += "View Clinician Command\n";
        message += clinicianCLI.help("view");
        // message += adminCLI.help("view");
        break;

      case "create":
        message += "Create commands";
        message += "\n----------\n----------\n";
        message += "Create Donor Command\n";
        message += donorReceiverCLI.help("create");
        message += "\n==========\n";
        message += "Create Clinician Command\n";
        message += clinicianCLI.help("create");
        // message += adminCLI.help("create");
        break;

      case "delete":
        message += "Delete commands";
        message += "\n----------\n----------\n";
        message += "Delete Donor Command\n";
        message += donorReceiverCLI.help("delete");
        message += "\n==========\n";
        message += "Delete Clinician Command\n";
        message += clinicianCLI.help("delete");
        // message += adminCLI.help("delete");
        break;

      case "import":
        message += "Import command";
        message += "\n----------\n----------\n";
        message += "The required input for importing saved data into the system: import\n";
        break;

      case "save":
        message += "Save command";
        message += "\n----------\n----------\n";
        message += "The required input for saving the current data: save\n";
        break;

      case "launch":
        message += "Launch command";
        message += "\n----------\n----------\n";
        message += "The command to launch the GUI component of the app from the CLI: launch";
        message += "Can only be called once while the application is running.\n";
        message += "Only recognised if used in the CLI outside of the GUI component.\n";
        break;

      default:
        int accountType = accountType(subHelp);

        switch (accountType) {
          case 0:
            message = donorReceiverCLI.help();
            break;

          case 1:
            message = clinicianCLI.help();
            break;

          case 2:
            // message = adminCLI.help();
            break;

          case -1:
            message = ("Couldn't recognise help text. Options include all, update, view, create, delete, donor, clinician, import, save and launch\n");
            break;
        }
    }

    message += "========================================================================================\n";

    return message;
  }

  /**
   * The default help command that returns all commands
   *
   * @return A string of all commands and how to use them
   */
  public String help() {
    String message = "";
    message += "Donor Help\n==========\n";
    message += donorReceiverCLI.help();
    message += "Clinician Help\n==========\n";
    message += clinicianCLI.help();
    message += "\n";
    // message += adminCLI.help();
    // message += "\n";
    message += help("import");
    message += "\n";
    message += help("save");
    message += "\n";
    message += help("launch");
    message += "\n";
//    message+=("The required input for importing saved data into the system: import\n");
//    message+=("The required input for saving the current data: save\n");
//    message+=("Launches the GUI component of the application.\n");
//    message+=("Can only be called once while the application is running.\n");
//    message+=("Only recognised if used in the CLI outside of the GUI component.\n");

    message += ("For more information regarding commands for the system, please consult the user_manual\n");

    return message;
  }

  /**
   * A small method that simply returns an int of what the account type is based on the entered
   * string
   *
   * @param accountType The accountType to check
   * @return An int representing the account; 0 = DonorReceiver, 1 = Clinician, 2 = Admin, -1 =
   * Invalid
   */
  private int accountType(String accountType) {
    accountType = accountType.toLowerCase();
    switch (accountType) {
      case "donor":
      case "receiver":
      case "donorReceiver":
      case "account":
      case "user":
        return 0;

      case "clinician":
      case "staff":
        return 1;

      case "admin":
      case "administrator":
        return -1; // Change back to 2 when the admin commands are added to the CLI

      default:
        return -1;
    }
  }

}
