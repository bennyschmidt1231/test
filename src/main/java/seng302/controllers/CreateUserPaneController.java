package seng302.controllers;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Region;
import seng302.*;
import seng302.model.*;
import seng302.model.person.DonorReceiver;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/* Class for handling all events from the createUserPane fxml, specifically creating a user.
 */
public class CreateUserPaneController {


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
    private TextField lastNameTextField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private DatePicker dateOfBirthDatePicker;

    @FXML
    private Button doneButton;

    @FXML
    private Button backButton;

    public static String lastScreen = PageNav.LOGIN;

    public void initialize() {

        ArrayList<TextField> textFieldArrayList = new ArrayList<>();
        textFieldArrayList.add(usernameTextField);
        textFieldArrayList.add(givenNameTextField);
        textFieldArrayList.add(lastNameTextField);


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
        //Set listeners for undo and redo
        dateOfBirthDatePicker.valueProperty().addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) {
                if(!undoRedoDatePicker) {
                    undoableManager.createDatePickerChange(dateOfBirthDatePicker, oldValue, newValue);
                }
                undoRedoDatePicker = false;
            }
        });
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

    @FXML

    /**
     * Creates a new Donor DonorReceiver and adds it to the database from the provided details. The details provided are: NHI code,
     * given name, last name, and date of birth. The account is not saved. Invalid details result in a corresponding error
     * dialog box.
     */
    void createAccount(ActionEvent event) {
        // Get entered text
        String givenName = givenNameTextField.getText();
        // What about middle name???
        String username = usernameTextField.getText();
        String password = passwordField.getText();

        // Test user inputs
        boolean testNHI = db.validateNHI((username));
        if (!testNHI) {
            showBadNHIMessage();
            return;
        }
        boolean testGivenName = UserAttributeCollection.validateAlphanumericString
                (false, givenNameTextField.getCharacters().toString(), 1, 50);
        if (!testGivenName) {
            showBadGivenNameMessage();
            return;
        }
        boolean testLastName = UserAttributeCollection.validateAlphanumericString
                (false, lastNameTextField.getCharacters().toString(), 1, 50);
        if(!testLastName) {
            showBadLastNameMessage();
            return;
        }
        LocalDate date;
        boolean valid = true;
        boolean testDOB = true;
        boolean testPassword = UserAttributeCollection.validateAlphanumericString
                (false, passwordField.getCharacters().toString(), 1, 50);
        boolean testConfirmPassword = UserAttributeCollection.validateAlphanumericString
                (false, confirmPasswordField.getCharacters().toString(), 1, 50);

        String confirmPassword = confirmPasswordField.getText();
        if ((!testConfirmPassword || !testConfirmPassword) || (!password.equals(confirmPassword))){
            showBadPasswordMethod();
            return;
        }

            //formatDateToString
        try {
            LocalDate localDate = dateOfBirthDatePicker.getValue();
            testDOB = DonorReceiver.validateDateOfBirth(localDate);
            } catch (Exception e) {
                valid = false;
        }
        date = dateOfBirthDatePicker.getValue();
        if (!(valid && testDOB)) {
            showBadDateMessage();
            return;
        }
        DonorReceiver donorReceiver = new DonorReceiver(givenNameTextField.getCharacters().toString(), "",
                lastNameTextField.getCharacters().toString(), date, usernameTextField.getCharacters().toString());
        donorReceiver.setPassword(password);

        // Look for the typed username in the donorReceivers map
        DonorReceiver acc = db.getDonorReceiverByUsername(username);
        if(acc != null) {
            // If acc is not null, the username already exists in the list
            if (acc.isActive() == false) {
                // If the account is innactive, alert the user it will be re-activated.
                showRecreationMessage();
            }
        }
        db.reactivateOldAccountIfExists(AccountManager.getCurrentUser(), donorReceiver);
        db.insertDonorReceiver(donorReceiver, true);
        showSuccessMessage();
        undoableManager.getCommandStack().save();
        backSelected(event);
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
    private void backSelected(ActionEvent event) {
        if (PageNav.isAdministrator) {
            undoableManager.getCommandStack().save();
            PageNav.loadNewPage(PageNav.LISTVIEW);
        } else {
            undoableManager.getCommandStack().save();
            PageNav.loadNewPage(lastScreen);
        }
    }


    /* An Error dialog alert box given if the inputted nhi code is invalid
     */
    public void showBadNHIMessage(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("Invalid NHI code given");
        alert.setContentText("Either you have entered an invalid NHI code or there is already an account with that code. " +
                "A valid NHI code has 7 characters. The first three are alphabetical, excluding 'I' and 'O' and are in upper case. " +
                "The next 4 characters are digits with the last being non-zero.");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }


    /**
     *  An Error dialog alert box given if the inputted nhi code is invalid
     */
    public void showBadPasswordMethod(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("Invalid password");
        alert.setContentText("Password and confirm password must match. " +
                "Valid passwords are alphanumeric and from 1 to 50 characters ");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }

    /* An Error dialog alert box given if the inputted given name is invalid
     */
    public void showBadGivenNameMessage(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("Invalid Given Name given");
        alert.setContentText("Given name must be between 1 and 50 alphabetical characters. Spaces, commas, apostrophes," +
                " and dashes are also allowed.");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();

    }

    /* An Error dialog alert box given if the inputted last name is invalid
     */
    public void showBadLastNameMessage(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("Invalid Last Name given");
        alert.setContentText("Last name must have at most 100 alphabetical characters. " +
                "Spaces, commas, apostrophes, and dashes are also allowed.");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();

    }

    /* An Error dialog alert box given if the inputted date of birth is invalid
     */
    public void showBadDateMessage(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("Invalid Date of Birth given");
        alert.setContentText("Date of birth must be a valid date that is before the current date. The date also has to " +
                "be in the following format: \" + \"'dd/MM/YYYY'.");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();

    }


    /* A success dialog alert box given if the inputted values are all correct.
     */
    public void showSuccessMessage() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText("Success");
        alert.setContentText(String.format("DonorReceiver created. Remember to save the application to make the account permanent."));
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();

    }

    public void showRecreationMessage() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText("DonorReceiver reactivation");
        alert.setContentText(String.format("WARNING: NHI code already used for deactivated account. Reactivated account. " +
                "Note that previous unsaved changes to the deactivated account will have been lost."));
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }

}
