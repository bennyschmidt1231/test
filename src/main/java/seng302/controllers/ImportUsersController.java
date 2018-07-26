package seng302.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import seng302.App;
import seng302.model.AccountManager;
import seng302.model.Marshal;
import seng302.model.PageNav;
import seng302.model.import_export_strategies.CSVImport;
import seng302.model.import_export_strategies.JSONFolderImport;
import seng302.model.import_export_strategies.JSONImport;
import seng302.model.person.Administrator;
import seng302.model.person.Clinician;
import seng302.model.person.DonorReceiver;
import seng302.model.person.User;
import seng302.model.person.UserValidationReport;

public class ImportUsersController {

  @FXML
  private ComboBox<String> fileType;

  @FXML
  private RadioButton singleFile;

  @FXML
  private RadioButton directory;


  @FXML
  private ComboBox<String> userType;

  @FXML
  public Button cancelButton;

  /**
   * ObservableList containing all types of the user that can be imported
   */
  private ObservableList<String> USER_TYPES = FXCollections
      .observableArrayList("Donor/Receiver", "Clinician",
          "Administrator");

  /**
   * ObservableList containing all types of file that can be imported
   */
  private ObservableList<String> FILE_TYPES = FXCollections.observableArrayList("JSON", "CSV");

  /**
   * The AccountManager object which handles all account operations.
   */
  private AccountManager accountManager = App.getDatabase();

  private ToggleGroup group;


  /**
   * An instance of the admin class specifically to get imported data information.
   */
  private Marshal marshal;


  /**
   * Initializes the GUI elements for the admin GUI pane as well as the undo and redo functionality
   * for the editing text fields.
   */
  public void initialize() {
    marshal = new Marshal();
    setupComboBoxOptions();
    setupRadioButtons();
    setupComboBoxListener();
  }


  /**
   * Populates the comboboxes
   */
  private void setupComboBoxOptions() {

    userType.getItems().addAll(USER_TYPES);
    userType.getSelectionModel().selectFirst();
    fileType.getItems().addAll(FILE_TYPES);
    fileType.getSelectionModel().selectFirst();

  }


  /**
   * Adds the radio buttons to a toggle group and selects default option
   */
  private void setupRadioButtons() {

    group = new ToggleGroup();

    singleFile.setToggleGroup(group);
    singleFile.setSelected(true);
    directory.setToggleGroup(group);

  }


  /**
   * Adds a listener to fileType combobox which modifies the radiobuttons in response the selected
   * filetype.
   */
  private void setupComboBoxListener() {
    fileType.getSelectionModel().selectedItemProperty()
        .addListener((options, oldValue, newValue) -> {
              if (newValue.equalsIgnoreCase("CSV")) {
                singleFile.setSelected(true);
                directory.setDisable(true);
                userType.getSelectionModel().select("Donor/Receiver");
                userType.setDisable(true);
              } else {
                directory.setDisable(false);
                userType.setDisable(false);
              }
            }
        );
  }


  /**
   * Called when import users button is pressed
   */
  public void importUsers() {
    setUsers();
    selectStrategy();
    if (singleFile.isSelected()) {
      fileSelect();
    } else {
      directorySelect();
    }
  }


  /**
   * Updates the current users in marshal based on options selected
   */
  private void setUsers() {
    String userTypeString = userType.getSelectionModel().getSelectedItem();
    if (userTypeString.equalsIgnoreCase("Donor/Receiver")) {
      marshal.setCurrentUsers(convertDonorsToUsers(accountManager.getDonorReceivers()));
    } else if (userTypeString.equalsIgnoreCase("Clinician")) {
      marshal.setCurrentUsers(convertCliniciansToUsers(accountManager.getClinicians()));
    } else if (userTypeString.equalsIgnoreCase("Administrator")) {
      marshal.setCurrentUsers(convertAdminsToUsers(accountManager.getAdministrators()));
    }
  }


  /**
   * Takes in a Donor linked hash map and returns a User linked hash map from the donor key-value
   * pairings
   *
   * @param donors a Map of DonorReceivers
   * @return a linked Hash map of Users
   */
  private LinkedHashMap<String, User> convertDonorsToUsers(Map<String, DonorReceiver> donors) {
    LinkedHashMap<String, User> users = new LinkedHashMap<>();
    for (Map.Entry<String, DonorReceiver> donor : donors.entrySet()) {
      users.put(donor.getKey(), donor.getValue());
    }
    return users;
  }


  /**
   * Takes in a Admin linked hash map and returns a User linked hash map from the Admin key-value
   * pairings
   *
   * @param admins a Map of Administrators
   * @return a linked Hash map of Users
   */
  private LinkedHashMap<String, User> convertAdminsToUsers(Map<String, Administrator> admins) {
    LinkedHashMap<String, User> users = new LinkedHashMap<>();
    for (Map.Entry<String, Administrator> admin : admins.entrySet()) {
      users.put(admin.getKey(), admin.getValue());
    }
    return users;
  }


  /**
   * Takes in a Clinician linked hash map and returns a User linked hash map from the clinician
   * key-value pairings
   *
   * @param clinicians a Map of Clinicians
   * @return a linked Hash map of Users
   */
  private LinkedHashMap<String, User> convertCliniciansToUsers(Map<String, Clinician> clinicians) {
    LinkedHashMap<String, User> users = new LinkedHashMap<>();
    for (Map.Entry<String, Clinician> clinician : clinicians.entrySet()) {
      users.put(clinician.getKey(), clinician.getValue());
    }
    return users;
  }


  /**
   * Sets the import strategy to be used by marshal
   */
  private void selectStrategy() {
    String fileTypeString = fileType.getSelectionModel().getSelectedItem();
    if (fileTypeString.equalsIgnoreCase("csv")) {
      marshal.setImportStrategy(new CSVImport());
    } else if (fileTypeString.equalsIgnoreCase("json")) {
      if (singleFile.isSelected()) {
        marshal.setImportStrategy(new JSONImport());
      } else {
        marshal.setImportStrategy(new JSONFolderImport());
      }
    }
  }


  /**
   * Generates a title for the directory/file chooser window
   *
   * @return title generated
   */
  private String generateTitle() {
    String title = "";
    String fileTypeString = fileType.getSelectionModel().getSelectedItem();
    String userTypeString = userType.getSelectionModel().getSelectedItem();
    if (fileTypeString.equalsIgnoreCase("csv")) {
      title = "Select CSV containing " + userTypeString + " data.";
    } else if (fileTypeString.equalsIgnoreCase("json")) {
      if (singleFile.isSelected()) {
        title = "Select " + userTypeString + " User.json file.";
      } else {
        title = "Select directory containing " + userTypeString + " User.json files.";
      }
    }
    return title;
  }


  /**
   * Called when a directory of files is being imported. Opens a directory chooser.
   */
  private void directorySelect() {
    try {
      //Create a new instance of a file chooser for the admin to pick a directory to import user files
      DirectoryChooser chooser = new DirectoryChooser();
      //Generate the file chooser title based on options selected by admin
      chooser.setTitle(generateTitle());
      File defaultDirectory = new File(Paths.get(App.class.getProtectionDomain()
          .getCodeSource().getLocation().toURI()).getParent().toString());
      chooser.setInitialDirectory(defaultDirectory);
      File directory = chooser.showDialog(App.getWindow());
      if (directory != null) {
        importUsers(directory);
      }
    } catch (URISyntaxException e) {
      System.err.println("Something went wrong");
    }
  }


  /**
   * Imports all User files in the given directory and generates a report on the success/failure of
   * the import.
   *
   * @param directory the directory for which to import User files.
   */
  private void importUsers(File directory) {
    int numberOfDuplicates = 0;
    try {
      LinkedHashMap<String, User> users = marshal
          .importer(userType.getSelectionModel().getSelectedItem(), directory.getPath(), directory);
      if (marshal.getDuplicateImports().size() != 0) {
        numberOfDuplicates = importDuplicateUsers(marshal.getDuplicateUsers(),
            userType.getSelectionModel().getSelectedItem(),
            users, marshal.getDuplicateImports(), marshal.getSuccessfulImports(),
            marshal.getFailedImports());
      }
      App.getDatabase().setDonorReceivers(convertUsersToDonors(users));
      generateReport(numberOfDuplicates);
      marshal.clearImportAndExportLists();
    } catch (IOException e1) {
      System.err.println("Something went wrong");
    }
  }


  /**
   * Takes in a User linked hash map and returns a DonorReceiver linked hash map from the user
   * key-value pairings
   *
   * @param users a linked hash map of Users
   * @return a linked hash map of donorReceivers
   */
  private LinkedHashMap<String, DonorReceiver> convertUsersToDonors(
      LinkedHashMap<String, User> users) {
    System.out.println("got here");
    LinkedHashMap<String, DonorReceiver> donors = new LinkedHashMap<>();
    for (Map.Entry<String, User> user : users.entrySet()) {
      donors.put(user.getKey(), (DonorReceiver) user.getValue());
    }
    return donors;
  }


  /**
   * Called when a single file is being imported. Opens a file chooser.
   */
  private void fileSelect() {
    try {
      //Create a new instance of a file chooser for the admin to pick a directory to import user files
      FileChooser fileChooser = new FileChooser();
      //Generate the file chooser title based on options selected by admin
      fileChooser.setTitle(generateTitle());
      File defaultDirectory = new File(Paths.get(App.class.getProtectionDomain()
          .getCodeSource().getLocation().toURI()).getParent().toString());
      fileChooser.setInitialDirectory(defaultDirectory);
      File directory = fileChooser.showOpenDialog(App.getWindow());
      if (directory != null) {
        importUsers(directory);
      }
    } catch (URISyntaxException e) {
      System.err.println("Something went wrong");
    }
  }

  /**
   * @param numberOfDuplicates the number of duplicate accounts after importing accounts
   * @throws IOException If the import user data FXML cannot be loaded.
   */
  public void generateReport(int numberOfDuplicates) throws IOException {

    // Create new pane.
    FXMLLoader loader = new FXMLLoader();
    AnchorPane reportPane = loader.load(getClass().getResourceAsStream(PageNav.IMPORTREPORT));

    // Create new scene.
    Scene reportScene = new Scene(reportPane);

    // Create new stage.
    Stage reportStage = new Stage();
    reportStage.setTitle("Report");
    reportStage.setScene(reportScene);
    reportStage.show();

    // Place stage in center of main window.
    reportStage.setX(
        App.getWindow().getX() + ((App.getWindow().getWidth() - reportStage.getWidth()) / 2.0));
    reportStage.setY(
        App.getWindow().getY() + ((App.getWindow().getHeight() - reportStage.getHeight()) / 2.0));

    //App.addChildWindow(reportStage);

    ImportUsersReportController importUsersReportController = loader.getController();
    importUsersReportController.setDuplicates(numberOfDuplicates);
    importUsersReportController.setMarshal(marshal);

    PageNav.loadNewPage(PageNav.ADMINMENU);

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
   * database or not. Default clinician and administrators will not be overwritten. Also for each
   * user added to the database, its corresponding validation report will also be added to the list
   * of successful imports. Returns the number of user profile overwrites performed by the import.
   *
   * @param duplicatedUsers An array list of of users to be imported.
   * @param type A string to signify what type of users are being imported ('donorReceivers' or
   * 'clinicians' or 'administrators').
   * @param currentUsers A linkedHashMap of the existing Users to be replaced.
   * @param duplicateReports A LinkedHashMap of the reports relating to the duplicate users
   * @param successfulReports A collection of successful imports that may or may not be added to by
   * reports from the duplicateReports linked hash map.
   * @param rejectedReports A collection of rejected imports that may or may not be added to by
   * reports from the duplicateReports linked hash map.
   * @return Returns the number of existing user profiles in the database that were overwritten by
   * the import.
   */
  private int
  importDuplicateUsers(ArrayList<User> duplicatedUsers, String type,
      LinkedHashMap<String, User> currentUsers,
      LinkedHashMap<String, UserValidationReport> duplicateReports, Collection successfulReports,
      Collection rejectedReports) {
    int overrideCount = 0;
    boolean overwriteAll = false; //Control boolean to overwrite all existing files automatically
    boolean overwriteNone = false; // Control boolean to perform no overwrites
    HashMap<Integer, String> choice = new HashMap<>(); // A hashmap to store the result of whether the user choose to overwrite all files automatically

    //Custom alert which asks the user whether they wish to overwrite and existing file and whether to overwrite all existing files automatically.
    Alert alert = createAlertWithOptOut(Alert.AlertType.CONFIRMATION, "Overwrite file", null,
        "Do you wish to proceed?", "Do this for all files",
        param -> choice.put(0, param ? "Always" : "Never"), ButtonType.YES, ButtonType.NO);

    for (User user : duplicatedUsers) {

      String currentUserName =
          currentUsers.get(user.getUserName()).getFirstName() + " " + currentUsers
              .get(user.getUserName()).getLastName();
      alert.setHeaderText(
          "You are going to overwrite existing " + type + " " + user.getUserName() + " ( "
              + currentUserName + " ).");
      if ((!user.getUserName().equals("0")) && (!user.getUserName().equals("Sudo"))) {
        if ((!overwriteAll) && (!overwriteNone)) {
          if (alert.showAndWait().filter(t -> t == ButtonType.YES).isPresent()) {
            //We search the donor database and merge the imported user with the existing donor account.
            DonorReceiver original = (DonorReceiver) currentUsers.get(user.getUserName());
            currentUsers.put(user.getUserName(),
                marshal.mergeOriginalDonorWithNewDonor(original, (DonorReceiver) user));
            successfulReports.add(duplicateReports.get(user.getUserName()));
            overrideCount++;

            //If the user choose to automatically overwrite existing files
            if ((choice.get(0) != null) && (choice.get(0).equals("Always"))) {
              overwriteAll = true;
              // or chooses never to overwrite
            }
          } else { // the user chooses not to overwrite
            if (choice.get(0) != null && choice.get(0).equals("Always")) {
              overwriteNone = true;
              rejectedReports.add(duplicateReports.get(user.getUserName()));
            }
          }

        } else if (overwriteAll) {
          DonorReceiver original = (DonorReceiver) currentUsers.get(user.getUserName());
          currentUsers.put(user.getUserName(),
              marshal.mergeOriginalDonorWithNewDonor(original, (DonorReceiver) user));
          successfulReports.add(duplicateReports.get(user.getUserName()));
          overrideCount++;
        } else {
          rejectedReports.add(duplicateReports.get(user.getUserName()));
        }
      }
    }
    return overrideCount;
  }

  @FXML
  public void cancelButtonPressed() {
    PageNav.loadNewPage(PageNav.ADMINMENU);
  }
}



