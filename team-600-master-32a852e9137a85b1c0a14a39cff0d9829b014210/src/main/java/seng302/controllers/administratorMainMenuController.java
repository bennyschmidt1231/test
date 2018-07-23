package seng302.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import seng302.App;
import seng302.model.*;
import seng302.model.person.*;
import seng302.model.person.Clinician;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import javafx.collections.ListChangeListener;


/**
 * Controller class for the Administrator main menu.
 */
public class administratorMainMenuController {



    private ListChangeListener<LogEntry> listener;

    @FXML private TextField givenNameTextField;
    @FXML private TextField otherNameTextField;
    @FXML private TextField lastNameTextField;
    @FXML private TextField usernameTextField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ListView modificationsTextField;


    @FXML private Label givenNameLabel;
    @FXML private Label otherNameLabel;
    @FXML private Label lastNameLabel;
    @FXML private Label usernameLabel;
    @FXML private Label passwordLabel;
    @FXML private Label confirmPasswordLabel;
    @FXML private Label errorLabel;
    @FXML private Label informationLabel;

    @FXML private Button editButton;
    @FXML private Button cancelButton;
    @FXML private Button undoButton;
    @FXML private Button redoButton;
    @FXML private Button viewAdministratorButton;
    @FXML private Button commandLineButton;


    /**
     * A boolean to check whether the admin's attribute fields have been edited (true) or not (false).
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
     * A boolean to signify whether the application has had a change of state and therefore needs to save (true). False otherwise.
     */
    private static boolean appNeedsSaving;


    /**
     * Sets the appNeedsSaving boolean to false. This is called by the parent window, MenuBarController after application has been saved
     * through the menu item.
     */
    public static void hasSaved() {
        appNeedsSaving = false;
    }


    /**
     * Switches the attribute labels of the administrator to display in the GUI and hides the edit text fields for these attributes from view
     */
    public void editSwitchModeToView(){
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
     * Switches the editable text fields of the administrator to display in the GUI and hides the view labels for these attributes from view
     */
    public void viewSwitchModeToEdit(){
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
     * Sets all the attribute labels of the administrator in the GUI to the values given in their account.
     */
    public void setAdministatorLabels() {

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
     * Initializes the GUI elements for the admin GUI pane as well as the undo and redo functionality for the editing text fields.
     */
    @FXML
    public void initialize(){
        marshal = new Marshal();
        appNeedsSaving = false;

        // We add a system log listener to check whenever there has been a modification that requires the application to be saved.
        listener = change -> { appNeedsSaving = true;};
        AccountManager.addSystemLogListener(listener);
        // We set the default values for the text fields and labels
        admin = App.getDatabase().getCurrentUser();
        setAdministatorLabels();
        //passwordTextField.setText(admin.getPassword.replaceAll("^[a-zA-Z]$","*"));

        if (admin.getUserName().equals("Sudo")) {
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
            textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
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
        if(!current.getUndo().empty() && current.getUndo().peek().getUndoRedoName().equals("Text Field")) undoRedoTextField = true;
        undoableManager.getCommandStack().undo();
    }

    /**
     * Redos the last undid event when using menu bar
     */
    public static void redoCalled() {
        CommandStack current = undoableManager.getCommandStack();
        if(!current.getRedo().empty() && current.getRedo().peek().getUndoRedoName().equals("Text Field")) undoRedoTextField = true;
        undoableManager.getCommandStack().redo();
    }


    /**
     * sets and prints the update log of the admin to the list view in the History pane
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
     * Checks if the given TextField text is valid (alphanumeric 1-50 characters long) and returns a 'true' boolean if so. If not the
     * TextField will be highlighted in red and the GUI error label will be updated with an appropriate error message relating to the given
     * 'name' string.
     * @param name A string being either "Given", "Other", or "Last".
     * @param field A java fx Text Field.
     * @return Returns 'true' if the given Text Field text is valid, 'false' otherwise.
     */
    public boolean checkName(String name, TextField field) {
        //TODO move validation up to the person class
        boolean givenNameIsValid = UserAttributeCollection.validateAlphanumericString(false, field.getText(), 0, 50);
        if (!givenNameIsValid) {
            errorLabel.setTextFill(Color.web("red"));
            errorLabel.setText(String.format("%s name must be between 0 and 50 alphabetical characters. ", name));
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
     * Checks if the usernameTextField text is a valid username (alphanumeric 3-30 characters long) and returns 'true' if so.
     * If not the usernameTextField will be highlighted in red and the GUI error label will be updated with an appropriate error message.
     * @return Returns 'true' if the usernameTextField is valid, 'false' otherwise.
     */
    public boolean checkUsername() {
        boolean usernameIsValid = App.getDatabase().validateAdminUsername(usernameTextField.getText());
        if (!usernameIsValid) {
            errorLabel.setTextFill(Color.web("red"));
            errorLabel.setText("Username needs to be between 3 and 20 alphanumeric characters and contain at least 1 letter. Underscores are allowed.");
            usernameTextField.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
            return false;
        } else {
            boolean usernameIsUsedAdmin = (null != App.getDatabase().getAdminIfItExists(usernameTextField.getCharacters().toString()));
            //System.out.println(usernameTextField.getText());
            boolean usernameIsNHI = App.getDatabase().checkNHIRegex(usernameTextField.getText());
            // System.out.println(usernameIsNHI);

            if(usernameIsNHI) {
              errorLabel.setTextFill(Color.web("red"));
              errorLabel.setText("Username cannot be a NHI. Please use another username.");
              usernameTextField.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
              return false;
            }
           else if (usernameIsUsedAdmin) {
                errorLabel.setTextFill(Color.web("red"));
                errorLabel.setText("Username is already in use. Please use another username.");
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
     * Checks if the passwordTextField text is a valid password (alphanumeric 6-30 characters long) and returns 'true' if so.
     * If not the passwordTextField will be highlighted in red and the GUI error label will be updated with an appropriate error message.
     * @return Returns 'true' if the passwordTextField is valid, 'false' otherwise.
     */
    public boolean checkPassword(){
        boolean passwordIsValid = Clinician.validatePassword(passwordField.getText()); // TODO move clinician password validation up to USER
        if (!passwordIsValid) {
            errorLabel.setTextFill(Color.web("red"));
            errorLabel.setText("Password should be between 6 and 30 alphanumeric characters of any case.");
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
     * Checks the admin attribute text fields to see if they have been updated to valid values. If they have, then the admin's account
     * is updated with the new values. Returns 'true' if either there has been no updates to the attributes, or the updates were carried
     * out successfully. Returns 'false' otherwise.
     * @return Returns 'true' upon successful update of admin updates or no updates were carried out. Returns 'false' otherwise.
     */
    public boolean checkInputsAndUpdateIfValid() {
        boolean hasChanged = false;
        boolean givenNameValid = checkName("Given", givenNameTextField);
        if (!givenNameTextField.getText().equals(admin.getFirstName())) {
            LogEntry logEntry = new LogEntry(admin, AccountManager.getCurrentUser(), "firstName", admin.getFirstName(), givenNameTextField.getText());
            if (!givenNameValid) {
                return false;
            } else {
                admin.setFirstName(givenNameTextField.getText());
                hasChanged = true;
                AccountManager.getSystemLog().add(logEntry);
                admin.getModifications().add(logEntry);
            }
        }

        boolean otherNameValid = checkName("Other", otherNameTextField);
        if (!otherNameTextField.getText().equals(admin.getMiddleName())) {
            LogEntry logEntry = new LogEntry(admin, AccountManager.getCurrentUser(), "middleName", admin.getMiddleName(), otherNameTextField.getText());
            if (!otherNameValid) {
                return false;
            } else {
                admin.setMiddleName(otherNameTextField.getText());
                hasChanged = true;
                AccountManager.getSystemLog().add(logEntry);
                admin.getModifications().add(logEntry);
            }
        }

        Boolean lastNameValid = checkName("Last", lastNameTextField);
        if (!lastNameTextField.getText().equals(admin.getLastName())) {
            LogEntry logEntry = new LogEntry(admin, AccountManager.getCurrentUser(), "lastName", admin.getLastName(), lastNameTextField.getText());
            if (!lastNameValid) {
                return false;
            } else {
                admin.setLastName(lastNameTextField.getText());
                hasChanged = true;
                AccountManager.getSystemLog().add(logEntry);
                admin.getModifications().add(logEntry);
            }
        }

        Boolean usernameValid = checkUsername();
        if (!usernameTextField.getText().equals(admin.getUserName())) {
            LogEntry logEntry = new LogEntry(admin, AccountManager.getCurrentUser(), "userName", admin.getUserName(), usernameTextField.getText());
            if (!usernameValid) {
                return false;
            } else {
                admin.setUserName(usernameTextField.getText());
                hasChanged = true;
                AccountManager.getSystemLog().add(logEntry);
                admin.getModifications().add(logEntry);
            }
        }

        Boolean passwordValid = checkPassword();
        if (!passwordField.getText().equals(admin.getPassword())) {
            LogEntry logEntry = new LogEntry(admin, AccountManager.getCurrentUser(), "password", admin.getPassword(), passwordField.getText());
            if (!passwordValid) {
                return false;
            } else {
                admin.setPassword(passwordField.getText());
                hasChanged = true;
                AccountManager.getSystemLog().add(logEntry);
                admin.getModifications().add(logEntry);
            }
        }
        if(hasChanged) {
            informationLabel.setText("Attribute(s) changed successfully. Please save the application to make the changes permanent.");
            informationLabel.setTextFill(Color.web("green"));
        } else {
            errorLabel.setText("");
            errorLabel.setTextFill(Color.web("red"));
        }
        return true;
    }


    /**
     * Carries out a swapping of the admins attribute test fields and labels when the 'edit' or 'done' button is pressed in the GUI.
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
     * An alert which appears when there is an IO exception when either the System Log window or the CLI window failed to load.
     */
    public void showPaneLoadErrorMessage(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("Unable to load Pane");
        alert.setContentText("We were unable to load the pane, please restart the application and try again.");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();

    }


    /**
     * Creates a new window (pane, window and scene), the Command Line Interface window when the "Command Line" button is pressed in the GUI.
     * @param event The event of the user (an admin) pressing the "Command Line" button.
     */
    @FXML
    public void createCLIWindow(ActionEvent event) {
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

        App.addChildWindow(window);
    }

    /**
     * Creates a new window (pane, window and scene), the System Log window when the "View System Log" button is pressed in the GUI.
     * @param event The event of the user (an admin or clinician) pressing the "View System Log" button.
     */
    @FXML
    public void createSystemLogWindow(ActionEvent event) {
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

        App.addChildWindow(window);
    }

    /**
     * Loads the clinician list fxml when the "View Clinicians" button is pressed in the GUI.
     * @param event The action of the user pressing the "View Clinicians" button.
     */
    @FXML
    public void viewCliniciansButtonPressed(ActionEvent event) {
        PageNav.loadNewPage(PageNav.CLINICIANSLIST);
    }


    /**
     * Loads the admins list fxml when the "View Clinicians" button is pressed in the GUI.
     * @param event The action of the user pressing the "View Clinicians" button.
     */
    @FXML
    public void viewAdminsButtonPressed(ActionEvent event) {
        PageNav.loadNewPage(PageNav.ADMINSLIST);
    }


    /**
     * A factory method to create a customizable alert dialog box that has a check box. This code was sourced from a stack overflow
     *  post from user 'ctg' made on 30/4/2016. See: https://stackoverflow.com/questions/36949595/how-do-i-create-a-javafx-alert-with-a-check-box-for-do-not-ask-again
     *  The comments in the code are from the code's author. the code itself is entirely unmodified.
     * @param type the type of Alert the alert will be.
     * @param title A String of the alert's title.
     * @param headerText A string of what the alert's header tex twill be.
     * @param message A string of the alert content message.
     * @param optOutMessage A string which will be the alert's check box method.
     * @param optOutAction A Consumer variable that is used to return the result of the user's interaction with the alert text box.
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
     * A failure dialog alert box given if the application fails to save.
     */
    public void showBadSaveError(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("Save failed");
        alert.setContentText("Something went wrong and and the save failed.");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }

    /** A success dialog alert box given if the application successfully saved.
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
     * Informs the user that the action they are about to perform will override existing data and prompt them to save the application.
     * A boolean is returned depending on the user's choice (in a GUI alert box):
     *  They can choose to save the application, in which case a 'true' boolean is returned after the application saves.
     *  They can choose to continue the operation without saving, in which case a 'true' boolean is returned.
     *  They can choose to cancel the operation in which case a 'false' boolean is returned.
     *  A false boolean is returned if all else fails.
     * @return Returns a boolean, 'true' if the user decides to save and or continue an operation, 'false' otherwise.
     */
    private boolean saveOrContinueOrCancelAnOperation() {

        //We build the alert and buttons
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Overwrite Dialog");
        alert.setHeaderText("Warning, possible data overwrite.");
        alert.setContentText("You have unsaved changes, these changes may be lost in ensuring action. Do you wish to save the application before proceeding?");

        ButtonType buttonTypeSave = new ButtonType("Save changes");
        ButtonType buttonTypeContinue = new ButtonType("Continue with no save");
        ButtonType buttonTypeCancel = new ButtonType("Cancel action", ButtonBar.ButtonData.CANCEL_CLOSE);

        //We add the buttons to the dialog pane and resize the pane to correctly show all buttons. Code from
        // https://stackoverflow.com/questions/45866249/javafx-8-alert-different-button-sizes
        alert.getButtonTypes().setAll(buttonTypeSave, buttonTypeContinue, buttonTypeCancel);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.getDialogPane().getButtonTypes().stream()
                .map(alert.getDialogPane()::lookupButton)
                .forEach(node -> ButtonBar.setButtonUniformSize(node, false));

        Optional<ButtonType> result = alert.showAndWait();

        //User decides what action to take.
        if (result.get() == buttonTypeSave){
                try {
                    App.getDatabase().exportAccounts();
                    App.getDatabase().exportClinicians();
                    App.getDatabase().exportAdmins();
                    App.removeUnsavedIndicator();
                    showGoodSave();
                    appNeedsSaving = false;
                    informationLabel.setText(""); // we clear the edit label in the admin profile.
                    return true;
                } catch (Exception e) {
                    showBadSaveError();
                }
        } else if (result.get() == buttonTypeContinue) {
            return true;
        } else if (result.get() == buttonTypeCancel)  {
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
     * Imports a list of donors into the App database. For each user in the list that already exists in the database,
     * It first checks if the user wants to overwrite that existing user in the database or not.
     * Default clinician and administrators will not be overwritten.
     * Returns the number of user profile overwrites performed by the import.
     * @param users An array list of of users to be imported.
     * @param type A string to signify what type of users are being imported ('donorReceivers' or 'clinicians' or 'administrators').
     * @return Returns the number of existing user profiles in the database that were overwritten by the import.
     */
    private int importUsers(ArrayList<User> users, String type) {
        int overrideCount = 0;
        boolean overwriteAppliesToAll = false; //Control boolean to overwrite all existing files automatically
        HashMap<Integer, String> choice = new HashMap<>(); // A hashmap to store the result of whether the user choose to overwrite all files automatically

        //Custom alert which asks the user whether they wish to overwrite and existing file and whether to overwrite all existing files automatically.
        Alert alert2 = createAlertWithOptOut(Alert.AlertType.CONFIRMATION, "Overwrite file", null,
                "Do you wish to proceed?", "Do not ask again",
                param -> choice.put(0, param ? "Always" : "Never"), ButtonType.YES, ButtonType.NO); //param -> prefs.put(KEY_AUTO_EXIT, param ? "Always" : "Never")

        for (User user : users) {
            alert2.setHeaderText("You are going to overwrite existing " + type + " " + user.getUserName() + ".");


            // We check against the donors NHI number to see if the account already exists in the database
            if (type.equals("donorReceiver")) {
                if (App.getDatabase().checkUsedNHI(user.getUserName()) ) {

                    // if its not true that we overwrite the file automatically
                    if (!overwriteAppliesToAll) {
                        if (alert2.showAndWait().filter(t -> t == ButtonType.YES).isPresent()) {
                            //We search the donor database and replace the existing donor account.

                            App.getDatabase().insertDonor((DonorReceiver) user, true);
                            overrideCount ++;

                            //If the user choose to automatically overwrite existing files
                            if (choice.get(0) != null && choice.get(0) .equals("Always")) {
                                overwriteAppliesToAll = true;
                            }
                        }
                    } else {
                        App.getDatabase().insertDonor((DonorReceiver) user, true);
                        overrideCount ++;
                    }
                } else {
                    App.getDatabase().insertDonor((DonorReceiver) user, false);
                }


                // We check against the clinician's staff number to see if the account already exists
            } else if (type.equals("clinician")) {
                if (!App.getDatabase().checkStaffIDIsNotUsed(user.getUserName()) && !user.getUserName().equals("0")) { // Cannot override default clinician!
                    if (!overwriteAppliesToAll) {
                        if (alert2.showAndWait().filter(t -> t == ButtonType.YES).isPresent()) {
                            App.getDatabase().insertClinician((Clinician) user, true);
                            overrideCount ++;
                            if (choice.get(0) != null && choice.get(0).equals("Always")) {
                                overwriteAppliesToAll = true;
                            }
                        }
                    } else {
                        App.getDatabase().insertClinician((Clinician) user, true);
                        overrideCount ++;
                    }
                } else if (!user.getUserName().equals("0")) {
                    App.getDatabase().insertClinician((Clinician) user, false);
                }


                // Checking for existing admins
            } else {
                if (null != App.getDatabase().getAdminIfItExists(user.getUserName()) && !user.getUserName().equals("Sudo")) { // Cannot override default admin!
                    if (!overwriteAppliesToAll) {
                        if (alert2.showAndWait().filter(t -> t == ButtonType.YES).isPresent()) {
                            App.getDatabase().insertAdmin((Administrator) user, true);
                            overrideCount ++;
                            if (choice.get(0) != null && choice.get(0).equals("Always")) {
                                overwriteAppliesToAll = true;
                            }
                        }
                    } else {
                        App.getDatabase().insertAdmin((Administrator) user, true);
                        overrideCount ++;
                    }
                } else if (!user.getUserName().equals("Sudo")) {
                    App.getDatabase().insertAdmin((Administrator) user, false);
                }
            }
        }
        return overrideCount;
    }





    /**
     * Generates an informational dialog alert box which documents various statistics regarding a User import action triggered
     * by an admin user in the admin GUI. A list of import totals are displayed as well as two lists, one of the successful imports,
     * and one of the failed imports. The statistics allow the admin to quickly determine which user files are corrupted.
     * The code is based off an example dialog box from the code.makery website. see: http://code.makery.ch/blog/javafx-dialogs-official/
     * @param successes An array list of strings which list all the successful user import file names.
     * @param failures An array list of strings which list all the failed user import file names.
     * @param type A string which specifies the type of user that was imported. Will be either "donorReceiver", "clinician", or "administrator".
     * @param additions An integer total of the number of new user files that were added to the Application database.
     * @param overrides An integer total of the number of existing user files that were overwritten during the import.
     */
    private void informUserOfImportResults(ArrayList<String> successes, ArrayList<String> failures, String type, int additions, int overrides) {


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
                numberOfSuccesses + numberOfFailures, numberOfSuccesses, numberOfFailures, type, additions, overrides));

        //Now we create the text area for successful imports
        Label goodImports = new Label("The successful file imports were:");
        String goodImportsString = "";
        for (String string: successes) {
            goodImportsString += string + "\n";
        }

        TextArea textArea = new TextArea(goodImportsString);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        //Now we create the text area for unsuccessful imports
        Label badImports = new Label("The failed file imports were:");
        String badImportsString = "";
        for (String string: failures) {
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
        expContent.add(goodImports, 0, 0);
        expContent.add(textArea, 0, 1);
        expContent.add(badImports, 0, 2);
        expContent.add(textArea2, 0, 3);

        // Update system log.
        updateSystemLog(successes);

        //Setting the size of the alert box proved tricky, I had to have absolute values in the end
        alert.getDialogPane().setExpandableContent(expContent);
        alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
        alert.setResizable(true);
        alert.getDialogPane().setPrefSize(1000, 1000);

        alert.showAndWait();
    }


    /**
     * Updates the system log with a new entry describing an import operation
     * executed by the current user. This only occurs if at least one file was
     * successfully imported.
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
     * Opens a directory chooser window in the GUI for the admin to find a directory of donor/receiver type user objects to import into the
     * App database.
     */
    private void importDonorsFromFile() {
        try {
            App.closeChildWindows();
            //Create a new instance of a file chooser for the admin to pick a directory to import donor files
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Import Donor/Receiver User.json files from a directory");
            File defaultDirectory = new File(Paths.get(App.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI()).getParent().toString());
            chooser.setInitialDirectory(defaultDirectory);
            File directory = chooser.showDialog(App.getWindow());


            if (directory != null) {


                ArrayList<User> users = marshal.ImportSelectedUsersFromFile(directory, "donorReceivers");

                int donorListSize = App.getDatabase().getDonorReceivers().size();
                int count = importUsers(users, "donorReceiver");
                int additions = App.getDatabase().getDonorReceivers().size() - donorListSize;
                informUserOfImportResults((ArrayList<String>) marshal.getSuccessfulImports(), (ArrayList<String>)
                        marshal.getFailedImports(), "donorReceivers", additions, count);
                marshal.clearImportAndExportLists();
            }

        } catch (URISyntaxException e) {

        }


    }


    /**
     * Opens a directory chooser window in the GUI for the admin to find a directory of clinician type user objects to import into the
     * App database.
     */
    private void importCliniciansFromFile() {
        try {
            //Create a new instance of a file chooser for the admin to pick a directory to import clinician files
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Import Clinician User.json files from a directory");
            File defaultDirectory = new File(Paths.get(App.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI()).getParent().toString());
            chooser.setInitialDirectory(defaultDirectory);
            File directory = chooser.showDialog(App.getWindow());
            if (directory != null) {
                ArrayList<User> users = marshal.ImportSelectedUsersFromFile(directory, "clinicians");
                int clinicianListSize = App.getDatabase().getClinicians().size();
                int count = importUsers(users, "clinician");
                int additions = App.getDatabase().getClinicians().size() - clinicianListSize;
                informUserOfImportResults((ArrayList<String>) marshal.getSuccessfulImports(), (ArrayList<String>)
                        marshal.getFailedImports(), "clinician", additions, count);
                marshal.clearImportAndExportLists();
            }

        } catch (URISyntaxException e) {

        }

    }


    /**
     * Opens a directory chooser window in the GUI for the admin to find a directory of administrator type user objects to import into the
     * App database.
     */
    private void importAdminsFromFile() {
        try {
            //Create a new instance of a file chooser for the admin to pick a directory to import admin files
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Import Administrator User.json files from a directory");
            File defaultDirectory = new File(Paths.get(App.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI()).getParent().toString());
            chooser.setInitialDirectory(defaultDirectory);
            File directory = chooser.showDialog(App.getWindow());
            if (directory != null) {
                ArrayList<User> users = marshal.ImportSelectedUsersFromFile(directory, "administrators");
                int adminsListSize = App.getDatabase().getAdministrators().size();
                int count = importUsers(users, "administrator");
                int additions = App.getDatabase().getAdministrators().size() - adminsListSize;
                informUserOfImportResults((ArrayList<String>) marshal.getSuccessfulImports(), (ArrayList<String>)
                        marshal.getFailedImports(), "administrator", additions, count);
                marshal.clearImportAndExportLists();
            }

        } catch (URISyntaxException e) {

        }
    }




    /**
     * Checks if the application has had a change in state, and if it has, prompts the user to save the application before
     * proceeding with the importation of donorReceiver user files.
     * @param event The event of the user pressing the 'Import Donor Data' button on the admin menu GUI.
     */
    @FXML
    public void importDonorDataButtonPressed(ActionEvent event) {

     if (appNeedsSaving)   {
         if (saveOrContinueOrCancelAnOperation() == true) {

            importDonorsFromFile();
         } else {
             return;
         }

     } else {
         importDonorsFromFile();
     }

    }



    /**
     * Checks if the application has had a change in state, and if it has, prompts the user to save the application before
     * proceeding with the importation of clinician user files.
     * @param event The event of the user pressing the 'Import Clinician Data' button on the admin menu GUI.
     */
    @FXML
    public void importClinicianDataButtonPressed(ActionEvent event) {
        if (appNeedsSaving)   {
            if (saveOrContinueOrCancelAnOperation() == true) {

                importCliniciansFromFile();
            } else {
                return;
            }

        } else {
            importCliniciansFromFile();
        }

    }




    /**
     * Checks if the application has had a change in state, and if it has, prompts the user to save the application before
     * proceeding with the importation of administrator user files.
     * @param event The event of the user pressing the 'Import Admin Data' button on the admin menu GUI.
     */
    @FXML
    public void importAdminDataButtonPressed(ActionEvent event) {
        if (appNeedsSaving)   {
            if (saveOrContinueOrCancelAnOperation() == true) {

                importAdminsFromFile();
            } else {
                return;
            }

        } else {
            importAdminsFromFile();
        }

    }





    /**
     * Logs the admin out of the application when the 'f' button is pressed in the GUI.
     * @param event The event of the user pressing "Logout" in the GUI.
     */
    @FXML
    public void logoutButtonPressed(ActionEvent event) {
        App.getDatabase().setCurrentUser(null);
        PageNav.loadNewPage(PageNav.LOGIN);

    }



    /**
     * Toggles the admin attribute editable text fields to uneditable labels without committing any changes.
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








}
