package seng302.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import seng302.*;
import seng302.model.person.Administrator;
import seng302.model.person.DonorReceiver;
import seng302.model.AccountManager;
import seng302.model.person.Clinician;
import seng302.model.PageNav;


public class LoginController {

    @FXML
    public Text welcomeMessage;
    @FXML
    public Text loginLabel;
    @FXML
    public Text NHILabel;
    @FXML
    public TextField usernameTextField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public Button loginButton;
    @FXML
    public Button createDonorButton;
    @FXML
    public Button adminButton;
    @FXML
    public Label errorLabel;

    public static AccountManager accountManager = App.getDatabase();

    /**
     * A boolean to store whether the donor button has been selected in the GUI, 'true' the button is selected, 'false' otherwise.
     */
    private boolean donorIsSelected;

    /**
     * A boolean to store whether the clinician button has been selected in the GUI, 'true' the button is selected, 'false' otherwise.
     */
    private boolean clinicianIsSelected;

    /**
     * A boolean to store whether the admin button has been selected in the GUI, 'true' the button is selected, 'false' otherwise.
     */
    private boolean adminIsSelected;

    // ------ Donor Login Controller Functions


    @FXML
    private void initialize() {
        donorIsSelected = true;
        clinicianIsSelected = false;
        adminIsSelected = false;
        loginButton.setDefaultButton(true); // when enter is pressed, fires this button.
        errorLabel.setText("Donor/Receiver Login");
        errorLabel.setTextFill(Color.web("black"));
    }


    /**
     * resets the text fields of the GUI of the login pane.
     */
    public void resetFields() {
        errorLabel.setText("");
        usernameTextField.clear();
        passwordField.clear();
        usernameTextField.setStyle(" -fx-border-color: silver ; -fx-border-width: 1px ; ");
        errorLabel.setTextFill(Color.web("black"));

    }


    /**
     * Changes login GUI text fields and login conditions to only allow an admin to login when the 'admin' button is pressed.
     *
     * @param event Action event where the user has pressed the 'admin' button.
     */
    @FXML
    void adminButtonPressed(ActionEvent event) {
        resetFields();
        donorIsSelected = false;
        clinicianIsSelected = false;
        adminIsSelected = true;
        loginLabel.setText("Username");
        errorLabel.setText("Administrator Login");
        errorLabel.setTextFill(Color.web("black"));
    }


    /**
     * Changes login GUI text fields and login conditions to only allow a clinician to login when the 'clinician' button is pressed.
     *
     * @param event Action event where the user has pressed the 'clinician' button.
     */
    @FXML
    void clinicianButtonPressed(ActionEvent event) {
        resetFields();
        donorIsSelected = false;
        clinicianIsSelected = true;
        adminIsSelected = false;
        loginLabel.setText("Staff ID");
        errorLabel.setText("Clinician Login");
        errorLabel.setTextFill(Color.web("black"));
    }


    /**
     * Changes login GUI text fields and login conditions to only allow a donor to login when the 'donor' button is pressed.
     *
     * @param event Action event where the user has pressed the 'donor' button.
     */
    @FXML
    void donorButtonPressed(ActionEvent event) {
        resetFields();
        donorIsSelected = true;
        clinicianIsSelected = false;
        adminIsSelected = false;
        loginLabel.setText("NHI Number");
        errorLabel.setTextFill(Color.web("black"));
        errorLabel.setText("Donor/Receiver Login");
    }


    /**
     * Checks the existence of the NHI number, if exists, changes screen to single donor view,
     * otherwise shows error message
     */
    @FXML
    public void login() {
        if (donorIsSelected) {
            donorLogin();
        } else if (clinicianIsSelected) {
            clinicianLogin();
        } else {
            adminLogin();
        }

    }


    /**
     * Checks the existence of the NHI number, if exists, changes screen to single donor view,
     * otherwise shows error message
     */
    public void donorLogin() {
        //TODO implement this function after refactor to include password checking
        String typedUsername = usernameTextField.getText().toUpperCase();
        String typedPassword = passwordField.getText();

        // Get the donor receiver we are trying to log in as
        DonorReceiver donorReceiver = accountManager.getDonorReceiverByUsername(typedUsername);

        if (donorReceiver != null && typedPassword.equals(donorReceiver.getPassword())) {
            // If user found and typed password matches the stored password, login the user
            resetFields();
            AccountManager.setCurrentUser(donorReceiver);
            ViewProfilePaneController.setAccount(donorReceiver);
            PageNav.loadNewPage(PageNav.VIEW);
        } else
            // User not found or passwords do not match
            invalidLoginNotification();
    }

    /**
     * Clears all fields on the log in screen and changes page to the donor creation panel
     */
    public void createDonor() {
        resetFields();
        PageNav.loadNewPage(PageNav.CREATE);
    }


    // ------ Clinician Login Controller Functions


    /**
     * Checks the existence of the Staff Id, if exists, changes screen to clinician view,
     * otherwise shows error message
     */
    public void clinicianLogin() {
        //TODO implement this function after refactor to include password checking
        String typedUsername = usernameTextField.getText().toUpperCase();
        String typedPassword = passwordField.getText();
        try {
            // Get clinician
            Clinician clinician = accountManager.getClinicianByUsername(typedUsername);

            if (clinician != null && typedPassword.equals(clinician.getPassword())) {
                // If user was found and the typed password matches the password stored for that user, login the clinician
                resetFields();
                AccountManager.setCurrentUser(clinician);
                ClinicianProfileController.setClinician(clinician);
                PageNav.loadNewPage(PageNav.MAINMENU);
            } else {
                // User not found or passwords do not match
                invalidLoginNotification();
            }
        } catch(NumberFormatException e){
            invalidLoginNotification();
        }
    }


    /**
     * Tries to log an administrator into the application, if the given values in the textfields in the GUI are valid
     * and corresponds to an existing admin. Gives an error message otherwise.
     */
    public void adminLogin(){
        //TODO implement this function after refactor to include password checking
        Administrator admin = App.getDatabase().getAdminIfItExists(usernameTextField.getText());
        if (admin != null) {
            if ((admin.getPassword() != null) && (admin.getPassword().equals(passwordField.getText()))) {
                AccountManager.setCurrentUser(admin);
                resetFields();
                PageNav.loadNewPage(PageNav.ADMINMENU);
            } else {
                invalidLoginNotification();
            }
        }
        else {
            invalidLoginNotification();
        }
    }


    /**
     * Sets the username TextField and password PasswordField to have a
     */
    private void invalidLoginNotification() {
        errorLabel.setText("Invalid username or password.");
        errorLabel.setTextFill(Color.web("red"));
        usernameTextField.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
        passwordField.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
    }

}
