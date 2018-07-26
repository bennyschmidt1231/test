package seng302.model;

import java.util.ArrayList;

public class CLIAdminCommandHandler {

  private AccountManager database;

  public CLIAdminCommandHandler(AccountManager database) {
    this.database = database;
  }

  // TODO: Implement
  public ArrayList<String> delete(String username) {
    ArrayList<String> messages = new ArrayList<>();

    try {

    } catch (IndexOutOfBoundsException e) {
      messages.add("ERROR");
    }

    return messages;
  }

  // TODO: Implement
  public ArrayList<String> update(String[] trailingCommand) {
    ArrayList<String> messages = new ArrayList<>();

    try {

    } catch (IndexOutOfBoundsException e) {
      messages.add("ERROR");
    }

    return messages;
  }

  // TODO: Implement
  public ArrayList<String> create(String command) {
    ArrayList<String> messages = new ArrayList<>();

    try {

    } catch (IndexOutOfBoundsException e) {
      messages.add("ERROR");
    }

    return messages;
  }

  // TODO: Implement
  public ArrayList<String> view(String command) {
    ArrayList<String> messages = new ArrayList<>();

    try {

    } catch (IndexOutOfBoundsException e) {
      messages.add("ERROR");
    }

    return messages;
  }

  // TODO: When adding help refer to how its done in CLIDonorReceiverCommandHandler
  public String help() {
    String message = "";
    // TODO: Add help here
    message += "========================================================================================\n";
    return message;
  }

  public String help(String subHelp) {
    String message = "";
    subHelp = subHelp.toLowerCase();

    switch (subHelp) {
      case "all":
        message += "";
        break;

      case "update":
        message += "";
        break;

      case "view":
        message += "";
        break;

      case "create":
        message += "";
        break;

      case "delete":
        message += "";
        break;
    }

    message += "========================================================================================\n";
    return message;
  }

}
