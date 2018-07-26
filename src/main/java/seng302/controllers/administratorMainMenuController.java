package seng302.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import seng302.App;
import seng302.controllers.childWindows.ChildWindowType;
import seng302.model.*;
import seng302.model.person.Administrator;
import seng302.model.person.LogEntry;
import seng302.model.person.User;
import seng302.model.person.UserValidator;


/**
 * Controller class for the Administrator main menu.
 */
public class administratorMainMenuController {

  private ListChangeListener<LogEntry> listener;

  @FXML
  private TextField givenNameTextField;
  @FXML
  private TextField otherNameTextField;
  @FXML
  private TextField lastNameTextField;
  @FXML
  private TextField usernameTextField;
  @FXML
  private PasswordField passwordField;
  @FXML
  private PasswordField confirmPasswordField;
  @FXML
  private ListView modificationsTextField;


  @FXML
  private Label givenNameLabel;
  @FXML
  private Label otherNameLabel;
  @FXML
  private Label lastNameLabel;
  @FXML
  private Label usernameLabel;
  @FXML
  private Label passwordLabel;
  @FXML
  private Label confirmPasswordLabel;
  @FXML
  private Label errorLabel;
  @FXML
  private Label informationLabel;

  @FXML
  private Button editButton;
  @FXML
  private Button cancelButton;
  @FXML
  private Button undoButton;
  @FXML
  private Button redoButton;
  @FXML
  private Button viewAdministratorButton;
  @FXML
  private Button commandLineButton;


  /**
   * An instance of the AccountManager class. The linked hashmaps are all declared as static.
   */
  private AccountManager accountManager;

  /**
   * A boolean to check whether the admin's attribute fields have been edited (true) or not
   * (false).
   */
  private boolean isEditing;

  /**
   * A boolean to signify whether a field or GUI element is available to be undone or redone.
   */
  private static Boolean undoRedoTextField = false;


  /**
   * An undoableManager which allows us to implement undo and redo functionality for this class.
   */
  private static UndoableManager undoableManager = App.getUndoableManager();

  /**
   * The current administrator user using the application Admin main menu GUI.
   */
  private User admin;


  /**
   * An instance of the admin class specifically to get imported data information.
   */
  private Marshal marshal;


  /**
   * Switches the attribute labels of the administrator to display in the GUI and hides the edit
   * text fields for these attributes from view
   */
  private void editSwitchModeToView() {
    isEditing = false;
    givenNameTextField.setVisible(false);
    otherNameTextField.setVisible(false);
    lastNameTextField.setVisible(false);
    usernameTextField.setVisible(false);
    passwordField.setVisible(false);
    confirmPasswordField.setVisible(false);
    confirmPasswordLabel.setVisible(false);
    passwordLabel.setVisible(false);
    cancelButton.setVisible(false);
    undoButton.setVisible(false);
    redoButton.setVisible(false);
    errorLabel.setVisible(false);
    informationLabel.setVisible(true);

    givenNameLabel.setVisible(true);
    otherNameLabel.setVisible(true);
    lastNameLabel.setVisible(true);
    usernameLabel.setVisible(true);
    editButton.setText("Edit");
  }


  /**
   * Switches the editable text fields of the administrator to display in the GUI and hides the view
   * labels for these attributes from view
   */
  private void viewSwitchModeToEdit() {
    isEditing = true;
    givenNameTextField.setVisible(true);
    otherNameTextField.setVisible(true);
    lastNameTextField.setVisible(true);
    usernameTextField.setVisible(true);
    passwordField.setVisible(true);
    confirmPasswordField.setVisible(true);
    confirmPasswordLabel.setVisible(true);
    passwordLabel.setVisible(true);
    cancelButton.setVisible(true);
    undoButton.setVisible(true);
    redoButton.setVisible(true);
    givenNameLabel.setVisible(false);
    otherNameLabel.setVisible(false);
    lastNameLabel.setVisible(false);
    usernameLabel.setVisible(false);
    editButton.setText("Done");
    errorLabel.setVisible(true);
    informationLabel.setVisible(false);
    informationLabel.setText("");

    //Remove all error labeling
    errorLabel.setText("");
    givenNameTextField.setStyle(" -fx-border-color: silver ; -fx-border-width: 1px ; ");
    otherNameTextField.setStyle(" -fx-border-color: silver ; -fx-border-width: 1px ; ");
    lastNameTextField.setStyle(" -fx-border-color: silver ; -fx-border-width: 1px ; ");
    usernameTextField.setStyle(" -fx-border-color: silver ; -fx-border-width: 1px ; ");
    passwordField.setStyle(" -fx-border-color: silver ; -fx-border-width: 1px ; ");
    confirmPasswordField.setStyle(" -fx-border-color: silver ; -fx-border-width: 1px ; ");
  }

  /**
   * Sets all the attribute labels of the administrator in the GUI to the values given in their
   * account.
   */
  private void setAdministatorLabels() {

    givenNameLabel.setText(admin.getFirstName());
    otherNameLabel.setText(admin.getMiddleName());
    lastNameLabel.setText(admin.getLastName());
    usernameLabel.setText(admin.getUserName());
    givenNameTextField.setText(admin.getFirstName());
    otherNameTextField.setText(admin.getMiddleName());
    lastNameTextField.setText(admin.getLastName());
    usernameTextField.setText(admin.getUserName());
    passwordField.setText(admin.getPassword());
    confirmPasswordField.setText(admin.getPassword());
    setListView(modificationsTextField);
  }


  /**
   * Initializes the GUI elements for the admin GUI pane as well as the undo and redo functionality
   * for the editing text fields.
   */
  @FXML
  public void initialize() {
    marshal = new Marshal();

    // We set the default values for the text fields and labels
    admin = App.getDatabase().getCurrentUser();
    setAdministatorLabels();
    //passwordTextField.setText(admin.getPassword.replaceAll("^[a-zA-Z]$","*"));

    if (admin.getUserName().equals(Administrator.DEFAULT)) {
      editButton.setVisible(false);
      editButton.setDisable(true);
    }

    errorLabel.setTextFill(Color.web("red"));

    // An array to store our text fields, we will add listeners to each field to enable undo/redo on each of them
    ArrayList<TextField> textFieldArrayList = new ArrayList<>();
    textFieldArrayList.add(usernameTextField);
    textFieldArrayList.add(otherNameTextField);
    textFieldArrayList.add(givenNameTextField);
    textFieldArrayList.add(lastNameTextField);

    ArrayList<PasswordField> passwordFieldArrayList = new ArrayList<>();
    passwordFieldArrayList.add(passwordField);
    passwordFieldArrayList.add(confirmPasswordField);

    // We now add listeners to each editable text field so we can can call command pattern methods to invoke undo/redo actions.
    for (TextField textField : textFieldArrayList) {
      textField.textProperty().addListener(new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue,
            String newValue) {
          if (!undoRedoTextField) {
            undoableManager.createTextFieldChange(textField, oldValue, newValue);
          }
          undoRedoTextField = false;
        }
      });
      textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
        @Override
        public void handle(javafx.scene.input.KeyEvent event) {
          if (event.getCode() == KeyCode.Z && event.isControlDown()) {
            undoEvent();
          } else if (event.getCode() == KeyCode.Y && event.isControlDown()) {
            redoEvent();
          }
        }
      });
    }

    for (PasswordField passwordField : passwordFieldArrayList) {
      passwordField.textProperty().addListener(new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue,
            String newValue) {
          if (!undoRedoTextField) {
            undoableManager.createTextFieldChange(passwordField, oldValue, newValue);
          }
          undoRedoTextField = false;
        }
      });
      passwordField.setOnKeyPressed(new EventHandler<javafx.scene.input.KeyEvent>() {
        @Override
        public void handle(javafx.scene.input.KeyEvent event) {
          if (event.getCode() == KeyCode.Z && event.isControlDown()) {
            undoEvent();
          } else if (event.getCode() == KeyCode.Y && event.isControlDown()) {
            redoEvent();
          }
        }
      });
    }

    editSwitchModeToView();
  }

  /**
   * Undos the last event on the command stack
   */
  @FXML
  public void undoEvent() {
    undoCalled();
  }

  /**
   * Redos the last undid event on the command stack while editing
   */
  @FXML
  public void redoEvent() {
    redoCalled();
  }

  /**
   * Calls the last undo event when using the menu bar
   */
  public static void undoCalled() {
    CommandStack current = undoableManager.getCommandStack();
    if (!current.getUndo().empty() && current.getUndo().peek().getUndoRedoName()
        .equals("Text Field")) {
      undoRedoTextField = true;
    }
    undoableManager.getCommandStack().undo();
  }

  /**
   * Redos the last undid event when using menu bar
   */
  public static void redoCalled() {
    CommandStack current = undoableManager.getCommandStack();
    if (!current.getRedo().empty() && current.getRedo().peek().getUndoRedoName()
        .equals("Text Field")) {
      undoRedoTextField = true;
    }
    undoableManager.getCommandStack().redo();
  }


  /**
   * sets and prints the update log of the admin to the list view in the History pane
   *
   * @param toView listview to set of
   */
  public void setListView(ListView toView) {
    ObservableList<LogEntry> observableLogs = FXCollections.observableArrayList();
    observableLogs.addAll(admin.getModifications());
    toView.setCellFactory(param -> new ListCell<LogEntry>() {
      @Override
      protected void updateItem(LogEntry log, boolean empty) {
        super.updateItem(log, empty);
        if (empty || log == null) {
          setText(null);
        } else {
          setText(log.toString());
        }
      }
    });
    toView.setItems(observableLogs);
  }


  /**
   * Checks if the given TextField text is valid (alphanumeric 1-50 characters long) and returns a
   * 'true' boolean if so. If not the TextField will be highlighted in red and the GUI error label
   * will be updated with an appropriate error message relating to the given 'name' string.
   *
   * @param name A string being either "Given", "Other", or "Last".
   * @param field A java fx Text Field.
   * @return Returns 'true' if the given Text Field text is valid, 'false' otherwise.
   */
  public boolean checkName(String name, TextField field) {

    boolean givenNameIsValid = UserValidator
        .validateAlphanumericString(false, field.getText(), 0, 50);
    if (!givenNameIsValid) {
      errorLabel.setTextFill(Color.web("red"));
      errorLabel.setText(
          String.format("%s name must be between 0 and 50 alphabetical characters. ", name));
      field.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
      return false;
    } else {
      // We recolor the text field border if the border was previously red. We also clear the error message.
      field.setStyle(" -fx-border-color: silver ; -fx-border-width: 1px ; ");
      errorLabel.setText("");
      return true;
    }
  }


  /**
   * Checks if the usernameTextField text is a valid username (alphanumeric 3-30 characters long)
   * and returns 'true' if so. If not the usernameTextField will be highlighted in red and the GUI
   * error label will be updated with an appropriate error message.
   *
   * @return Returns 'true' if the usernameTextField is valid, 'false' otherwise.
   */
  public boolean checkUsername() {
    boolean usernameIsValid = UserValidator.validateAdminUsername(usernameTextField.getText());
    if (!usernameIsValid) {
      errorLabel.setTextFill(Color.web("red"));
      errorLabel.setText(
          "Username needs to be between 3 and 20 alphanumeric characters and contain at least 1 letter. Underscores are allowed.");
      usernameTextField.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
      return false;
    } else {
      boolean usernameIsUsedAdmin = (null != App.getDatabase()
          .getAdminIfItExists(usernameTextField.getCharacters().toString()));
      //System.out.println(usernameTextField.getText());
      boolean usernameIsNHI = UserValidator.checkNHIRegex(usernameTextField.getText());
      // System.out.println(usernameIsNHI);

      if (usernameIsNHI) {
        errorLabel.setTextFill(Color.web("red"));
        errorLabel.setText("Username cannot be a NHI. Please use another username.");
        usernameTextField.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
        return false;
      } else if (usernameIsUsedAdmin) {
        errorLabel.setTextFill(Color.web("red"));
        errorLabel.setText("Username is already in use. Please use another username.");
        usernameTextField.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
        return false;
      } else {
        // We recolor the text field border if the border was previously red. We also clear the error message.
        usernameTextField.setStyle(" -fx-border-color: silver ; -fx-border-width: 1px ; ");
        errorLabel.setText("");
        return true;
      }
    }
  }


  /**
   * Checks if the passwordTextField text is a valid password (alphanumeric 6-30 characters long)
   * and returns 'true' if so. If not the passwordTextField will be highlighted in red and the GUI
   * error label will be updated with an appropriate error message.
   *
   * @return Returns 'true' if the passwordTextField is valid, 'false' otherwise.
   */
  public boolean checkPassword() {
    boolean passwordIsValid = UserValidator.validatePassword(passwordField.getText());
    if (!passwordIsValid) {
      errorLabel.setTextFill(Color.web("red"));
      errorLabel
          .setText("Password should be between 6 and 30 alphanumeric characters of any case.");
      passwordField.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
      return false;
    } else {
      // We recolor the text field border if the border was previously red. We also clear the error message.
      errorLabel.setText("");
      passwordField.setStyle(" -fx-border-color: silver ; -fx-border-width: 1px ; ");
      return true;
    }
  }


  /**
   * Checks the admin attribute text fields to see if they have been updated to valid values. If
   * they have, then the admin's account is updated with the new values. Returns 'true' if either
   * there has been no updates to the attributes, or the updates were carried out successfully.
   * Returns 'false' otherwise.
   *
   * @return Returns 'true' upon successful update of admin updates or no updates were carried out.
   * Returns 'false' otherwise.
   */
  public boolean checkInputsAndUpdateIfValid() {
    boolean hasChanged = false;
    boolean givenNameValid = checkName("Given", givenNameTextField);
    if (!givenNameTextField.getText().equals(admin.getFirstName())) {
      LogEntry logEntry = new LogEntry(admin, accountManager.getCurrentUser(), "firstName",
          admin.getFirstName(), givenNameTextField.getText());
      if (!givenNameValid) {
        return false;
      } else {
        admin.setFirstName(givenNameTextField.getText());
        hasChanged = true;
        accountManager.getSystemLog().add(logEntry);
        admin.getModifications().add(logEntry);
      }
    }

    boolean otherNameValid = checkName("Other", otherNameTextField);
    if (!otherNameTextField.getText().equals(admin.getMiddleName())) {
      LogEntry logEntry = new LogEntry(admin, accountManager.getCurrentUser(), "middleName",
          admin.getMiddleName(), otherNameTextField.getText());
      if (!otherNameValid) {
        return false;
      } else {
        admin.setMiddleName(otherNameTextField.getText());
        hasChanged = true;
        accountManager.getSystemLog().add(logEntry);
        admin.getModifications().add(logEntry);
      }
    }

    Boolean lastNameValid = checkName("Last", lastNameTextField);
    if (!lastNameTextField.getText().equals(admin.getLastName())) {
      LogEntry logEntry = new LogEntry(admin, accountManager.getCurrentUser(), "lastName",
          admin.getLastName(), lastNameTextField.getText());
      if (!lastNameValid) {
        return false;
      } else {
        admin.setLastName(lastNameTextField.getText());
        hasChanged = true;
        accountManager.getSystemLog().add(logEntry);
        admin.getModifications().add(logEntry);
      }
    }

    Boolean usernameValid = checkUsername();
    if (!usernameTextField.getText().equals(admin.getUserName())) {
      LogEntry logEntry = new LogEntry(admin, accountManager.getCurrentUser(), "userName",
          admin.getUserName(), usernameTextField.getText());
      if (!usernameValid) {
        return false;
      } else {
        admin.setUserName(usernameTextField.getText());
        hasChanged = true;
        accountManager.getSystemLog().add(logEntry);
        admin.getModifications().add(logEntry);
      }
    }

    Boolean passwordValid = checkPassword();
    if (!passwordField.getText().equals(admin.getPassword())) {
      LogEntry logEntry = new LogEntry(admin, accountManager.getCurrentUser(), "password",
          admin.getPassword(), passwordField.getText());
      if (!passwordValid) {
        return false;
      } else {
        admin.setPassword(passwordField.getText());
        hasChanged = true;
        accountManager.getSystemLog().add(logEntry);
        admin.getModifications().add(logEntry);
      }
    }
    if (hasChanged) {
      informationLabel.setText(
          "Attribute(s) changed successfully. Please save the application to make the changes permanent.");
      informationLabel.setTextFill(Color.web("green"));
    } else {
      errorLabel.setText("");
      errorLabel.setTextFill(Color.web("red"));
    }
    return true;
  }


  /**
   * Carries out a swapping of the admins attribute test fields and labels when the 'edit' or 'done'
   * button is pressed in the GUI.
   *
   * @param event The action of the user pressing the 'Edit' or 'Done' button.
   */
  @FXML
  public void editButtonPressed(ActionEvent event) {

    if (isEditing) {
      if (checkInputsAndUpdateIfValid()) {
        editSwitchModeToView();
        setAdministatorLabels();
      }
    } else {
      viewSwitchModeToEdit();
      setAdministatorLabels();
    }

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
    if (!App.saveInProgress()) {
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
        App.setSaveInProgress(true);
        SaveTask saveTask = new SaveTask(App.getDatabase());
        saveTask.setOnSucceeded(event1 -> {
          App.removeUnsavedIndicator();
          App.setSaveInProgress(false);
          showGoodSave();
          informationLabel.setText(""); // we clear the edit label in the admin profile.
        });
        saveTask.setOnFailed(event2 -> {
          showBadSaveError();
          App.setSaveInProgress(false);
        });
        Thread t = new Thread(saveTask);
        t.setDaemon(true); // thread will not prevent application shutdown
        t.start();
        return true;

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

    } else {
      return true;
    }

  }


  /**
   * Imports a list of donors into the App database. For each user in the list that already exists
   * in the database, It first checks if the user wants to overwrite that existing user in the
   * database or not. Default clinician and administrators will not be overwritten. Returns the
   * number of user profile overwrites performed by the import.
   *
   * @throws IOException If the import user data FXML cannot be loaded.
   */
  public void importUserData() throws IOException {
    PageNav.isAdministrator = true;
    PageNav.loadNewPage(PageNav.IMPORTDATA);
  }

  /**
   * Checks if the application has had a change in state, and if it has, prompts the user to save
   * the application before proceeding with the importation of user files.
   *
   * @param event The event of the user pressing the 'Import Donor Data' button on the admin menu
   * GUI.
   * @throws IOException IOException when the users can not be imported
   */
  @FXML
  public void importUserDataButtonPressed(ActionEvent event) throws IOException {

    if (App.unsavedChangesExist()) {
      if (saveOrContinueOrCancelAnOperation() == true) {

        importUserData();
      } else {
        return;
      }

    } else {
      importUserData();
    }

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


  /**
   * Toggles the admin attribute editable text fields to uneditable labels without committing any
   * changes.
   *
   * @param event event when cancelled
   */
  @FXML
  public void cancelButtonPressed(ActionEvent event) {
    editSwitchModeToView();
  }


  @FXML
  public void viewDonorsButtonPressed(ActionEvent event) {
    PageNav.isAdministrator = true;
    PageNav.loadNewPage(PageNav.LISTVIEW);
  }


    /**
     * Clears the Drug Interactions API cache if the user selects 'OK' from a GUI pop-up. A system log of the action is stored
     * if the cache is cleared.
     *
     * @param event the event
     */
    @FXML
    public void clearCache(ActionEvent event) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Clearing the Drugs Interaction Cache");
        alert.setHeaderText("You are going to completely empty the local Drug Interactions cache.");
        alert.setContentText("Do you wish to proceed?");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK) {
            int cacheSize = App.getDrugInteractionsCache().size();
            App.getDrugInteractionsCache().refreshCache();
            LogEntry logEntry = new LogEntry(admin, admin, "Cleared the Drugs Interactions Cache.", "size: " + cacheSize, "size: 0");
            AccountManager.getSystemLog().add(logEntry);
        }

    }


}
