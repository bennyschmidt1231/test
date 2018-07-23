package seng302.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import seng302.App;
import seng302.model.*;
import seng302.model.person.Administrator;

import java.util.ArrayList;
import seng302.model.person.Clinician;
import seng302.model.person.LogEntry;
import seng302.model.person.User;


/* Class for handling all events from the createUserPane fxml, specifically creating a user.
 */
public class CreateAdministratorPaneController {

    private User admin;

    public Label createAccountLabel;
    private AccountManager db = App.getDatabase();
    private static UndoableManager undoableManager = App.getUndoableManager();
    private static Boolean undoRedoDatePicker = false;
    private static Boolean undoRedoTextField = false;

    @FXML
    private TextField usernameTextField;

    @FXML
    private TextField givenNameTextField;

    @FXML
    private TextField middleNameTextField;

    @FXML
    private TextField lastNameTextField;

    @FXML
    private PasswordField initialPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button doneButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label errorLabel;

    public static String lastScreen = PageNav.ADMINSLIST;

    public void initialize() {
        admin = db.getCurrentUser();

        errorLabel.setTextAlignment(TextAlignment.CENTER);

        ArrayList<TextField> textFieldArrayList = new ArrayList<>();
        textFieldArrayList.add(usernameTextField);
        textFieldArrayList.add(middleNameTextField);
        textFieldArrayList.add(givenNameTextField);
        textFieldArrayList.add(lastNameTextField);

        ArrayList<PasswordField> passwordFieldArrayList = new ArrayList<>();
        passwordFieldArrayList.add(initialPasswordField);
        passwordFieldArrayList.add(confirmPasswordField);

        for(TextField textField: textFieldArrayList) {
            textField.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    if(!undoRedoTextField) {
                        undoableManager.createTextFieldChange(textField, oldValue, newValue);
                    }
                    undoRedoTextField = false;
                }
            });
            textField.setOnKeyPressed(new EventHandler<javafx.scene.input.KeyEvent>() {
                @Override
                public void handle(javafx.scene.input.KeyEvent event) {
                    if(event.getCode() == KeyCode.Z && event.isControlDown()) {
                        undoEvent();
                    } else if(event.getCode() == KeyCode.Y && event.isControlDown()) {
                        redoEvent();
                    }
                }
            });
        }

        for(PasswordField passwordField: passwordFieldArrayList) {
          passwordField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
              if(!undoRedoTextField) {
                undoableManager.createTextFieldChange(passwordField, oldValue, newValue);
              }
              undoRedoTextField = false;
            }
          });
          passwordField.setOnKeyPressed(new EventHandler<javafx.scene.input.KeyEvent>() {
            @Override
            public void handle(javafx.scene.input.KeyEvent event) {
              if(event.getCode() == KeyCode.Z && event.isControlDown()) {
                undoEvent();
              } else if(event.getCode() == KeyCode.Y && event.isControlDown()) {
                redoEvent();
              }
            }
          });
        }
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
        if(!current.getUndo().empty() && current.getUndo().peek().getUndoRedoName().equals("Date Picker")) undoRedoDatePicker = true;
        if(!current.getUndo().empty() && current.getUndo().peek().getUndoRedoName().equals("Text Field")) undoRedoTextField = true;
        undoableManager.getCommandStack().undo();
    }

    /**
     * Redos the last undid event when using menu bar
     */
    public static void redoCalled() {
        CommandStack current = undoableManager.getCommandStack();
        if(!current.getRedo().empty() && current.getRedo().peek().getUndoRedoName().equals("Date Picker")) undoRedoDatePicker = true;
        if(!current.getRedo().empty() && current.getRedo().peek().getUndoRedoName().equals("Text Field")) undoRedoTextField = true;
        undoableManager.getCommandStack().redo();
    }

    /**
     * Checks if the usernameTextField text is a valid username and returns 'true' if so.
     * If not the usernameTextField will be highlighted in red and the GUI error label will be updated with an appropriate error message.
     * @return Returns 'true' if the usernameTextField is valid, 'false' otherwise.
     */
    public boolean checkUsername() {
      boolean usernameIsValid = App.getDatabase().validateAdminUsername(usernameTextField.getText());
      if (!usernameIsValid) {
        errorLabel.setTextFill(Color.web("red"));
        errorLabel.setText("Invalid username\nUsername needs to be between 3 and 20 alphanumeric characters and contain at least 1 letter\nUnderscores are allowed.");
        usernameTextField.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
        return false;
      } else {
        boolean usernameIsUsedAdmin = (null != App.getDatabase().getAdminIfItExists(usernameTextField.getCharacters().toString()));
        //System.out.println(usernameTextField.getText());
        boolean usernameIsNHI = App.getDatabase().checkNHIRegex(usernameTextField.getText());
        // System.out.println(usernameIsNHI);

        if(usernameIsNHI) {
          errorLabel.setTextFill(Color.web("red"));
          errorLabel.setText("Username cannot be a NHI\nPlease use another username.");
          usernameTextField.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
          return false;
        }
        else if (usernameIsUsedAdmin) {
          errorLabel.setTextFill(Color.web("red"));
          errorLabel.setText("Username is already in use\nPlease use another username.");
          usernameTextField.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
          return false;
        }
        else {
          // We recolor the text field border if the border was previously red. We also clear the error message.
          usernameTextField.setStyle(" -fx-border-color: silver ; -fx-border-width: 1px ; ");
          errorLabel.setText("");
          return true;
        }
      }
    }


    /**
     * Creates a new Administrator and adds it to the database from the provided details. The details provided are: username,
     * given name, middle name, last name, and password. The account is not saved. Invalid details result in a corresponding error
     * dialog box.
     */
    @FXML void createAccount(ActionEvent event) {
        boolean testUsername = checkUsername();
        if (!testUsername) {
            return;
        }

        boolean testGivenName = checkGivenName();
        if (!testGivenName) {
            return;
        }

        boolean testMiddleName = checkMiddleName();
        if(!testMiddleName) {
          return;
        }

        boolean testLastName = checkLastName();
        if(!testLastName) {
            return;
        }

        boolean testPassword = checkPassword();
        if(!testPassword) {
          return;
        }

        Administrator administrator = new Administrator(givenNameTextField.getCharacters().toString(),
                middleNameTextField.getCharacters().toString(), lastNameTextField.getCharacters().toString(),
            usernameTextField.getCharacters().toString(), initialPasswordField.getCharacters().toString());

        db.getAdministrators().add(administrator);
        LogEntry logEntry = new LogEntry(administrator, admin, "created", null, usernameTextField.getText());
        AccountManager.getSystemLog().add(logEntry);
        admin.getModifications().add(logEntry); // TODO: Discuss whether we should add a log to the created admin about when it was created and by who?
        showSuccessMessage(); // TODO: Remove once we can see the list of administrators
        undoableManager.getCommandStack().save();
        cancelSelected(event);
        PageNav.loadNewPage(PageNav.ADMINSLIST);
    }

    @FXML
    /* changes the page to the Login page.
     */
    void goToLogin(ActionEvent event) {
        undoableManager.getCommandStack().save();
        PageNav.loadNewPage(PageNav.LOGIN);
    }


    /**
     * Loads the last screen viewed before the create user pane. This is
     * specified as the lastScreen variable, which should be updated when
     * a controller switches to the create user pane.
     * @param event The event triggered by selecting the back button.
     */
    @FXML
    private void cancelSelected(ActionEvent event) {
      undoableManager.getCommandStack().save();
      Stage stage = (Stage) cancelButton.getScene().getWindow();
      stage.fireEvent(
          new WindowEvent(
              stage,
              WindowEvent.WINDOW_CLOSE_REQUEST
          )
      );
    }

  /**
   * Check whether the given name that the admin has entered is valid
   * A valid given name is between 1-50 characters and contains
   * only alphanumeric characters
   * @return Boolean Whether or not the given name is valid
   */
  private Boolean checkGivenName(){
      Boolean isValid = UserAttributeCollection.validateAlphanumericString
          (false, givenNameTextField.getCharacters().toString(), 1, 50);

        if(!isValid) {
          errorLabel.setTextFill(Color.web("red"));
          errorLabel.setText(
              "Invalid Given Name\nGiven name must be between 1 and 50 alphabetical characters"
                  + "\nSpaces, commas, apostrophes, and dashes are also allowed");
          givenNameTextField.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
          return false;
        }
        else {
          givenNameTextField.setStyle(" -fx-border-color: silver ; -fx-border-width: 1px ; ");
          errorLabel.setText("");
          return true;
        }
    }


  /**
   * Check whether the middle name that the admin has entered is valid
   * A valid middle name is between 1-50 characters and contains
   * only alphanumeric characters
   * @return Boolean Whether or not the middle name is valid
   */
    private Boolean checkMiddleName(){
      Boolean isValid = UserAttributeCollection.validateAlphanumericString
          (false, middleNameTextField.getCharacters().toString(),1,50);
      if(!isValid) {
        errorLabel.setTextFill(Color.web("red"));
        errorLabel.setText(
            "Invalid Middle Name\nMiddle name must be between 1 and 50 alphabetical characters\n"
                + "Spaces, commas, apostrophes, and dashes are also allowed.");
        middleNameTextField.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
        return false;
      }
      else {
        middleNameTextField.setStyle(" -fx-border-color: silver ; -fx-border-width: 1px ; ");
        errorLabel.setText("");
        return true;
      }
    }


  /**
   * Check whether the last name that the admin has entered is valid
   * A valid last name is between 1-100 characters and contains
   * only alphanumeric characters
   * @return Boolean Whether or not the last name is valid
   */
    private Boolean checkLastName(){
        Boolean isValid = UserAttributeCollection.validateAlphanumericString
            (false, lastNameTextField.getCharacters().toString(), 1, 100);
        if(!isValid) {
          errorLabel.setTextFill(Color.web("red"));
          errorLabel.setText(
              "Invalid Last Name\nLast name must have have at most 100 characters\n" +
                  "Spaces, commas, apostrophes, and dashes are also allowed.");
          lastNameTextField.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
          return false;
        }
        else {
          lastNameTextField.setStyle(" -fx-border-color: silver ; -fx-border-width: 1px ; ");
          errorLabel.setText("");
          return true;
        }
    }

  /**
   * Check whether the password is valid and if the passwords are the same
   * The admin has the same password restriction as clinicians which is alphanumeric string of 6-30 characters
   * @return Boolean Whether the password fields match
   */
  private Boolean checkPassword() {

        Boolean validPassword = Clinician.validatePassword(initialPasswordField.getCharacters().toString());
        if(!validPassword) {
          errorLabel.setTextFill(Color.web("red"));
          errorLabel.setText("Invalid password\nPassword must be between 6-30 characters\n"
              + "Password can only contain letters and numbers");
          initialPasswordField.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
          confirmPasswordField.setStyle(" -fx-border-color: silver ; -fx-border-width: 1px ; ");
          return false;
        }
        else {
          initialPasswordField.setStyle(" -fx-border-color: silver ; -fx-border-width: 1px ; ");
        }

        Boolean samePassword = initialPasswordField.getCharacters().toString()
            .equals(confirmPasswordField.getCharacters().toString());
        if(!samePassword) {
          errorLabel.setTextFill(Color.web("red"));
          errorLabel.setText("Passwords don't match");
          confirmPasswordField.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
          return false;
        }

        else {
          confirmPasswordField.setStyle(" -fx-border-color: silver ; -fx-border-width: 1px ; ");
          errorLabel.setText("");
          return true;
        }
    }

    /**
     *  A success dialog alert box given if the inputted values are all correct
     */
    public void showSuccessMessage() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText("Success");
        alert.setContentText(String.format("Administrator created. Remember to save the application to make the account permanent."));
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();

    }


}
