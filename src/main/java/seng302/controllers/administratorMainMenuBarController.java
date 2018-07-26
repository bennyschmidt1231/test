package seng302.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import seng302.App;
import seng302.controllers.childWindows.ChildWindowType;
import seng302.model.AccountManager;
import seng302.model.Marshal;
import seng302.model.PageNav;
import seng302.model.person.Administrator;
import seng302.model.person.Clinician;
import seng302.model.person.DonorReceiver;
import seng302.model.person.LogEntry;
import seng302.model.person.User;

/**
 * Controller class for the main menu sidebar
 */
public class administratorMainMenuBarController {


  /**
   * An instance of the admin class specifically to get imported data information.
   */
  private Marshal marshal;

  private ListChangeListener<LogEntry> listener;

  /**
   * Sets the appNeedsSaving boolean to false. This is called by the parent window,
   * MenuBarController after application has been saved through the menu item.
   */
  public static void hasSaved() {
    appNeedsSaving = false;
  }

  /**
   * A boolean to signify whether the application has had a change of state and therefore needs to
   * save (true). False otherwise.
   */
  private static boolean appNeedsSaving;

  @FXML
  private Button viewAdministratorButton;
  @FXML
  private Button commandLineButton;

  @FXML
  public void initialize() {
    marshal = new Marshal();
    listener = change -> {
      appNeedsSaving = true;
    };
    AccountManager.addSystemLogListener(listener);
    appNeedsSaving = false;
  }

  /**
   * An alert which appears when there is an IO exception when either the System Log window or the
   * CLI window failed to load.
   */
  public void showPaneLoadErrorMessage() {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error Dialog");
    alert.setHeaderText("Unable to load Pane");
    alert.setContentText(
        "We were unable to load the pane, please restart the application and try again.");
    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    alert.showAndWait();

  }

  /**
   * Creates a new CLI window if one does not already exist when the "Command Line" button is
   * pressed in the GUI. If it does exist, it is brought to the front.
   *
   * @param event The vent of the user (an admin) presseing the "Command Line" button.
   */
  @FXML
  public void createCLIWindow(ActionEvent event) {

    // Check to see if a CLI window already exists.
    if (!App.childWindowToFront(ChildWindowType.CONSOLE)) {

      Pane cliPane = new Pane();
      try {
        FXMLLoader loader = new FXMLLoader();

        cliPane = loader.load(getClass().getResourceAsStream(PageNav.COMMANDLINE));
//            MenuBarController menuBarControl = loader.getController();

        //PageNav.setMenuBarController(menuBarControl);
        //PageNav.loadNewPage(PageNav.CLI);

      } catch (IOException e) {
        showPaneLoadErrorMessage();
      }
      Stage window = new Stage();
      Scene scene = new Scene(cliPane);
      window.setTitle("SapioCulture - Command Line Interface");
      window.setScene(scene);
      window.initModality(Modality.APPLICATION_MODAL);
      window.show();

      App.addChildWindow(window, ChildWindowType.CONSOLE);

    }
  }

  /**
   * Creates a new system log window if one does not already exist. If it does, the previous system
   * log window is brought to the front.
   *
   * @param event The event of the user (an admin or clinician) pressing the "View System Log"
   * button.
   */
  @FXML
  public void createSystemLogWindow(ActionEvent event) {

    // Check to see if a child window already exists for the system log.
    if (!App.childWindowToFront(ChildWindowType.SYSTEM_LOG)) {
      Pane logPane = new Pane();
      try {
        FXMLLoader loader = new FXMLLoader();

        logPane = loader.load(getClass().getResourceAsStream(PageNav.SYSTEMLOG));

      } catch (IOException e) {
        showPaneLoadErrorMessage();
      }
      Stage window = new Stage();
      Scene scene = new Scene(logPane);
      window.setTitle("SapioCulture - System Log");
      window.setScene(scene);
      window.show();

      App.addChildWindow(window, ChildWindowType.SYSTEM_LOG);
    }
  }

  /**
   * Loads the donor list view when the "View Donors" button is pressed in the GUI.
   *
   * @param event The action of the user pressing the "View Donors" button
   */
  @FXML
  public void viewDonorsButtonPressed(ActionEvent event) {
    PageNav.isAdministrator = true;
    PageNav.loadNewPage(PageNav.LISTVIEW);
  }

  /**
   * Loads the clinician list fxml when the "View Clinicians" button is pressed in the GUI.
   *
   * @param event The action of the user pressing the "View Clinicians" button.
   */
  @FXML
  public void viewCliniciansButtonPressed(ActionEvent event) {
    PageNav.loadNewPage(PageNav.CLINICIANSLIST);
  }


  /**
   * Loads the admins list fxml when the "View Clinicians" button is pressed in the GUI.
   *
   * @param event The action of the user pressing the "View Clinicians" button.
   */
  @FXML
  public void viewAdminsButtonPressed(ActionEvent event) {
    PageNav.loadNewPage(PageNav.ADMINSLIST);
  }

  /**
   * A factory method to create a customizable alert dialog box that has a check box. This code was
   * sourced from a stack overflow post from user 'ctg' made on 30/4/2016. See:
   * https://stackoverflow.com/questions/36949595/how-do-i-create-a-javafx-alert-with-a-check-box-for-do-not-ask-again
   * The comments in the code are from the code's author. the code itself is entirely unmodified.
   *
   * @param type the type of Alert the alert will be.
   * @param title A String of the alert's title.
   * @param headerText A string of what the alert's header tex twill be.
   * @param message A string of the alert content message.
   * @param optOutMessage A string which will be the alert's check box method.
   * @param optOutAction A Consumer variable that is used to return the result of the user's
   * interaction with the alert text box.
   * @param buttonTypes The button types the alert will have
   * @return An Alert
   */
  public static Alert createAlertWithOptOut(Alert.AlertType type, String title, String headerText,
      String message, String optOutMessage, Consumer<Boolean> optOutAction,
      ButtonType... buttonTypes) {
    Alert alert = new Alert(type);
    // Need to force the alert to layout in order to grab the graphic,
    // as we are replacing the dialog pane with a custom pane
    alert.getDialogPane().applyCss();
    Node graphic = alert.getDialogPane().getGraphic();
    // Create a new dialog pane that has a checkbox instead of the hide/show details button
    // Use the supplied callback for the action of the checkbox
    alert.setDialogPane(new DialogPane() {
      @Override
      protected Node createDetailsButton() {
        CheckBox optOut = new CheckBox();
        optOut.setText(optOutMessage);
        optOut.setOnAction(e -> optOutAction.accept(optOut.isSelected()));
        return optOut;
      }
    });
    alert.getDialogPane().getButtonTypes().addAll(buttonTypes);
    alert.getDialogPane().setContentText(message);
    // Fool the dialog into thinking there is some expandable content
    // a Group won't take up any space if it has no children
    alert.getDialogPane().setExpandableContent(new Group());
    alert.getDialogPane().setExpanded(true);
    // Reset the dialog graphic using the default style
    alert.getDialogPane().setGraphic(graphic);
    alert.setTitle(title);
    alert.setHeaderText(headerText);
    return alert;
  }


  /**
   * Imports a list of donors into the App database. For each user in the list that already exists
   * in the database, It first checks if the user wants to overwrite that existing user in the
   * database or not. Default clinician and administrators will not be overwritten. Returns the
   * number of user profile overwrites performed by the import.
   *
   * @param users An array list of of users to be imported.
   * @param type A string to signify what type of users are being imported ('donorReceivers' or
   * 'clinicians' or 'administrators').
   * @return Returns the number of existing user profiles in the database that were overwritten by
   * the import.
   */
  private int importUsers(ArrayList<User> users, String type) {
    int overrideCount = 0;
    boolean overwriteAppliesToAll = false; //Control boolean to overwrite all existing files automatically
    HashMap<Integer, String> choice = new HashMap<>(); // A hashmap to store the result of whether the user choose to overwrite all files automatically

    //Custom alert which asks the user whether they wish to overwrite and existing file and whether to overwrite all existing files automatically.
    Alert alert2 = createAlertWithOptOut(Alert.AlertType.CONFIRMATION, "Overwrite file", null,
        "Do you wish to proceed?", "Do not ask again",
        param -> choice.put(0, param ? "Always" : "Never"), ButtonType.YES,
        ButtonType.NO); //param -> prefs.put(KEY_AUTO_EXIT, param ? "Always" : "Never")

    for (User user : users) {
      alert2.setHeaderText(
          "You are going to overwrite existing " + type + " " + user.getUserName() + ".");

      // We check against the donors NHI number to see if the account already exists in the database
      if (type.equals("donorReceiver")) {
        if (App.getDatabase().checkUsedNHI(user.getUserName())) {

          // if its not true that we overwrite the file automatically
          if (!overwriteAppliesToAll) {
            if (alert2.showAndWait().filter(t -> t == ButtonType.YES).isPresent()) {
              //We search the donor database and replace the existing donor account.

              App.getDatabase().getDonorReceivers().put(user.getUserName(), (DonorReceiver) user);
              overrideCount++;

              //If the user choose to automatically overwrite existing files
              if (choice.get(0) != null && choice.get(0).equals("Always")) {
                overwriteAppliesToAll = true;
              }
            }
          } else {
            App.getDatabase().getDonorReceivers().put(user.getUserName(), (DonorReceiver) user);
            overrideCount++;
          }
        } else {
          App.getDatabase().getDonorReceivers().put(user.getUserName(), (DonorReceiver) user);
        }

        // We check against the clinician's staff number to see if the account already exists
      } else if (type.equals("clinician")) {
        if (!App.getDatabase().checkStaffIDIsNotUsed(user.getUserName()) && !user.getUserName()
            .equals("0")) { // Cannot override default clinician!
          if (!overwriteAppliesToAll) {
            if (alert2.showAndWait().filter(t -> t == ButtonType.YES).isPresent()) {
              App.getDatabase().insertClinician((Clinician) user, true);
              overrideCount++;
              if (choice.get(0) != null && choice.get(0).equals("Always")) {
                overwriteAppliesToAll = true;
              }
            }
          } else {
            App.getDatabase().insertClinician((Clinician) user, true);
            overrideCount++;
          }
        } else if (!user.getUserName().equals("0")) {
          App.getDatabase().insertClinician((Clinician) user, false);
        }

        // Checking for existing admins
      } else {
        if (null != App.getDatabase().getAdminIfItExists(user.getUserName()) && !user.getUserName()
            .equals("Sudo")) { // Cannot override default admin!
          if (!overwriteAppliesToAll) {
            if (alert2.showAndWait().filter(t -> t == ButtonType.YES).isPresent()) {
              App.getDatabase().insertAdmin((Administrator) user, true);
              overrideCount++;
              if (choice.get(0) != null && choice.get(0).equals("Always")) {
                overwriteAppliesToAll = true;
              }
            }
          } else {
            App.getDatabase().insertAdmin((Administrator) user, true);
            overrideCount++;
          }
        } else if (!user.getUserName().equals("Sudo")) {
          App.getDatabase().insertAdmin((Administrator) user, false);
        }
      }
    }
    return overrideCount;
  }


  /**
   * Generates an informational dialog alert box which documents various statistics regarding a User
   * import action triggered by an admin user in the admin GUI. A list of import totals are
   * displayed as well as two lists, one of the successful imports, and one of the failed imports.
   * The statistics allow the admin to quickly determine which user files are corrupted. The code is
   * based off an example dialog box from the code.makery website. see:
   * http://code.makery.ch/blog/javafx-dialogs-official/
   *
   * @param successes An array list of strings which list all the successful user import file
   * names.
   * @param failures An array list of strings which list all the failed user import file names.
   * @param type A string which specifies the type of user that was imported. Will be either
   * "donorReceiver", "clinician", or "administrator".
   * @param additions An integer total of the number of new user files that were added to the
   * Application database.
   * @param overrides An integer total of the number of existing user files that were overwritten
   * during the import.
   */
  private void informUserOfImportResults(ArrayList<String> successes, ArrayList<String> failures,
      String type, int additions, int overrides) {

    int numberOfSuccesses = successes.size();
    int numberOfFailures = failures.size();
    Alert alert = new Alert(Alert.AlertType.INFORMATION);

    //We build the initial alert box
    alert.setTitle("Results Dialog");
    alert.setHeaderText("Results of Import Operation");
    alert.setContentText(String.format("Results of the import operation are:\n" +
            "\tTotal Import attempts: %d,\n" +
            "\tTotal Import successes: %d,\n" +
            "\tTotal Import failures: %d,\n" +
            "\tNumber of %s added: %d\n" +
            "\tNumber of file overrides: %d",
        numberOfSuccesses + numberOfFailures, numberOfSuccesses, numberOfFailures, type, additions,
        overrides));

    //Now we create the text area for all files that were successfully read
    Label readFiles = new Label("The successfully read files were:");
    String readFilesString = "";
    for (String string : successes) {
      readFilesString += string + "\n";
    }

    TextArea textArea = new TextArea(readFilesString);
    textArea.setEditable(false);
    textArea.setWrapText(true);

    textArea.setMaxWidth(Double.MAX_VALUE);
    textArea.setMaxHeight(Double.MAX_VALUE);
    GridPane.setVgrow(textArea, Priority.ALWAYS);
    GridPane.setHgrow(textArea, Priority.ALWAYS);

    //Now we create the text area for unsuccessful imports
    Label badImports = new Label("The failed file imports were:");
    String badImportsString = "";
    for (String string : failures) {
      badImportsString += string + "\n";
    }

    TextArea textArea2 = new TextArea(badImportsString);
    textArea2.setEditable(false);
    textArea2.setWrapText(true);

    textArea2.setMaxWidth(Double.MAX_VALUE);
    textArea2.setMaxHeight(Double.MAX_VALUE);
    GridPane.setVgrow(textArea2, Priority.ALWAYS);
    GridPane.setHgrow(textArea2, Priority.ALWAYS);

    //We populate the original alert box with the text areas and labels
    GridPane expContent = new GridPane();
    expContent.setMaxWidth(Double.MAX_VALUE);
    expContent.add(readFiles, 0, 0);
    expContent.add(textArea, 0, 1);
    expContent.add(badImports, 0, 2);
    expContent.add(textArea2, 0, 3);

    // Update system log.
    updateSystemLog(successes);

    //Setting the size of the alert box proved tricky, I had to have absolute values in the end
    alert.getDialogPane().setExpandableContent(expContent);
    alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label)
        .forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
    alert.setResizable(true);
    alert.getDialogPane().setPrefSize(1000, 1000);

    alert.showAndWait();
  }


  /**
   * Updates the system log with a new entry describing an import operation executed by the current
   * user. This only occurs if at least one file was successfully imported.
   */
  private void updateSystemLog(List<String> successes) {

    if (successes.size() > 0) {

      String addedAccounts = "";

      for (String success : successes) {

        addedAccounts += success + " ";

      }

      AccountManager.getSystemLog().add(new LogEntry(
          AccountManager.getCurrentUser(),
          AccountManager.getCurrentUser(), "import", "", addedAccounts));

    }

  }


  /**
   * A success dialog alert box given if the application successfully saved.
   */
  public void showGoodSave() {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Information Dialog");
    alert.setHeaderText("Success");
    alert.setContentText(String.format("All changes have been successfully saved."));
    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    alert.showAndWait();
  }

  /**
   * A failure dialog alert box given if the application fails to save.
   */
  public void showBadSaveError() {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error Dialog");
    alert.setHeaderText("Save failed");
    alert.setContentText("Something went wrong and and the save failed.");
    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    alert.showAndWait();
  }

  /**
   * Informs the user that the action they are about to perform will override existing data and
   * prompt them to save the application. A boolean is returned depending on the user's choice (in a
   * GUI alert box): They can choose to save the application, in which case a 'true' boolean is
   * returned after the application saves. They can choose to continue the operation without saving,
   * in which case a 'true' boolean is returned. They can choose to cancel the operation in which
   * case a 'false' boolean is returned. A false boolean is returned if all else fails.
   *
   * @return Returns a boolean, 'true' if the user decides to save and or continue an operation,
   * 'false' otherwise.
   */
  private boolean saveOrContinueOrCancelAnOperation() {

    //We build the alert and buttons
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Confirmation Overwrite Dialog");
    alert.setHeaderText("Warning, possible data overwrite.");
    alert.setContentText(
        "You have unsaved changes, these changes may be lost in ensuring action. Do you wish to save the application before proceeding?");

    ButtonType buttonTypeSave = new ButtonType("Save changes");
    ButtonType buttonTypeContinue = new ButtonType("Continue with no save");
    ButtonType buttonTypeCancel = new ButtonType("Cancel action",
        ButtonBar.ButtonData.CANCEL_CLOSE);

    //We add the buttons to the dialog pane and resize the pane to correctly show all buttons. Code from
    // https://stackoverflow.com/questions/45866249/javafx-8-alert-different-button-sizes
    alert.getButtonTypes().setAll(buttonTypeSave, buttonTypeContinue, buttonTypeCancel);
    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    alert.getDialogPane().getButtonTypes().stream()
        .map(alert.getDialogPane()::lookupButton)
        .forEach(node -> ButtonBar.setButtonUniformSize(node, false));

    Optional<ButtonType> result = alert.showAndWait();

    //User decides what action to take.
    if (result.get() == buttonTypeSave) {
      try {
        App.getDatabase().exportAccounts();
        App.getDatabase().exportClinicians();
        App.getDatabase().exportAdmins();
        App.removeUnsavedIndicator();
        showGoodSave();
        appNeedsSaving = false;
        // administratorMainMenuAreaController.clearInformationLabel();
        return true;
      } catch (Exception e) {
        showBadSaveError();
      }
    } else if (result.get() == buttonTypeContinue) {
      return true;
    } else if (result.get() == buttonTypeCancel) {
      alert.close();
      return false;
    } else {
      alert.close();
      return false;
    }
    //Something went wrong
    return false;
  }





  /**
   * Logs the admin out of the application when the 'f' button is pressed in the GUI.
   *
   * @param event The event of the user pressing "Logout" in the GUI.
   */
  @FXML
  public void logoutButtonPressed(ActionEvent event) {
    App.getDatabase().setCurrentUser(null);
    PageNav.loadNewPage(PageNav.LOGIN);

  }
}
