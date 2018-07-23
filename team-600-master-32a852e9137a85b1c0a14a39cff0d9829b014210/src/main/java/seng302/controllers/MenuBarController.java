package seng302.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.event.ActionEvent;
import seng302.model.AccountManager;
import seng302.App;
import seng302.model.PageNav;


import java.io.IOException;
import java.util.Optional;

public class MenuBarController {
    private AccountManager db = App.getDatabase();


    /**
     *  The status bar.
     */
    @FXML private AnchorPane statusBar;

    private final static String directory = "/src/main/resources";

    /**
     * swaps out the current page with the new page.
     * @param page the new page to be displayed
     * @throws IOException io exception
     */
    public void setNewPage(Node page) throws IOException {

        statusBar = StatusBarController.injectNode(statusBar, page);

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


    @FXML
    /**
     * Attempts to save all changes made to the application given the user accepts the confirmation message. if the save
     * is successful or fails, a notification dialog box will appear.
     */
    public void callSaveConfirmation(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Save Application");
        alert.getDialogPane().lookupButton(ButtonType.OK).setId("saveConfirm");
        alert.setHeaderText("You are about to save the application. In this case all changes, creations, and" +
                " deletions will become permanent.");
            alert.setContentText("Do you wish to proceed?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                try {
                    App.getDatabase().exportAccounts();
                    App.getDatabase().exportClinicians();
                    App.getDatabase().exportAdmins();
                    App.removeUnsavedIndicator();
                    administratorMainMenuController.hasSaved();
                    showGoodSave();
                } catch (Exception e) {
                    showBadSaveError();
                }
            }
    }

    @FXML
    private void undoAction() throws IOException {
        String current = PageNav.getCurrentNav();
        if(current.equals("/editPane.fxml")) {
            EditPaneController.undoCalled();
        } else if (current.equals("/ClinicianProfileView.fxml")) {
            ClinicianProfileController.undoCalled();
        } else if (current.equals("/createUserPane.fxml")) {
            CreateUserPaneController.undoCalled();
        } else if (current.equals("/clinicianCreation.fxml")) {
            ClinicianCreationController.undoCalled();
        }



    }

    @FXML
    private void redoAction() {
        String current = PageNav.getCurrentNav();
        if(current.equals("/editPane.fxml")) {
            EditPaneController.redoCalled();
        } else if (current.equals("/ClinicianProfileView.fxml")) {
            ClinicianProfileController.redoCalled();
        } else if (current.equals("/createUserPane.fxml")) {
            CreateUserPaneController.redoCalled();
        } else if (current.equals("/clinicianCreation.fxml")) {
            ClinicianCreationController.redoCalled();
        }
    }

    @FXML
    /**
     * Closes the application when clicked.
     */
    void closeApp(ActionEvent event) {
        Platform.exit();

    }

    /**
     * Shows login help in an alert.
     */
    @FXML
    private void loginHelpSelected() {

        createInformationDialog("Login", "The login screen is where you can " +
                "login to the application.\n\nIf you have created an " +
                "account, all you have to do is enter your NHI number into " +
                "field shown and click the 'login' button. You will be taken" +
                "to the main menu screen.\n\nIf you still need to create an " +
                "account, click the 'create new user button'. This will take " +
                "you to the create account screen.");

    }


    /**
     * Shows main menu help in an alert.
     */
    @FXML
    private void menuHelpSelected() {

        createInformationDialog("Main Menu", "The main menu screen is " +
                "presented after you login. If you wish to logout, a button " +
                "at the top right corner of the screen will return you to " +
                "the login screen.\n\nThe search area the screen helps you " +
                "find existing accounts. You can either search by NHI number " +
                "or name by selecting the corresponding radio button. Once " +
                "you have entered some text into the search field, clicking " +
                "the search button will take you to the search screen. " +
                "If you do not enter anything into the field, then you will " +
                "search for all donors.\n\nThe 'View All Donors' button will " +
                "search for all accounts.\n\nThe 'Add New Donor' button will " +
                "take you to the create account screen where you add a new " +
                "donor to the application.");

    }

    /**
     * Shows create account help in an alert.
     */
    @FXML
    private void createHelpSelected() {

        createInformationDialog("Create DonorReceiver", "The create account screen " +
                "is where you enter all of the information required to add a " +
                "donor to the database. This includes a National Health " +
                "Index number, a given name, a last name, and a date of " +
                "birth. The NHI number must be unique.\n\nClicking the " +
                "'Done' button will add the account to the application. " +
                "Please note that it will not be stored permanently until " +
                "you save. This is accomplished by selecting 'File' in the " +
                "menu bar and then 'Save'.\n\nThe 'Back' button will return " +
                "you to the previous screen.");

    }

    /**
     * Shows search help in alert.
     */
    @FXML
    private void searchHelpSelected() {

        createInformationDialog("Search", "A clinician can search for a " +
                "donor profile. From the clinician’s main menu, clicking " +
                "‘Search’ or ‘View All Donors’ will transition the " +
                "application to the search screen. This presents a search " +
                "field, a table, a page controller, and a set of buttons. " +
                "The number of profiles displayed in the table is limited " +
                "to 30 per page. The page controller, immediately below the " +
                "table, allows you to select a page.\n\nEntering text into " +
                "the search field will limit the number of profiles " +
                "displayed. If the name of a profile contains the text " +
                "entered in the search field, it will be shown.\n\nProfiles " +
                "within the table can be sorted by name, age, gender, or " +
                "region. This is accomplished by clicking the appropriate " +
                "column header. Clicking several times will loop through " +
                "default, ascending, and descending sort.\n\nThe buttons at " +
                "the bottom of the screen can be used to view, edit, or " +
                "delete a profile selected in the table. The back button " +
                "will return you to the clinician’s main menu.\n\nIf you wish " +
                "to open a profile in a seperate window, double click on a " +
                "profile in the table. Multiple windows can be opened at once.");

    }

    @FXML
    private void viewHelpSelected() {

        createInformationDialog("View", "The view screen is " +
                "where you can see all of the attributes associated with an " +
                "account.\n\nThis is a read-only view, which means you " +
                "cannot change any attributes. If you wish to edit an " +
                "account, click the 'close' button to return to the search " +
                "screen and use the 'Edit' button.");

    }

    @FXML
    private void editHelpSelected() {


        createInformationDialog("Edit", "The edit screen is " +
                "where you can edit the attributes of an account. The only " +
                "required fields are 'Given Name', 'Last Name' and 'Date of " +
                "Birth'.\n\nWhen you are satisfied with your changes, press " +
                "'Done' to return to the previous screen.\n\nPlease note " +
                "that changes will only be saved if you select File -> Save " +
                "in the menu bar.");

    }


    @FXML
    /**
     * brings up a small info alert dialog box describing the application and listing its designers when clicked.
     */
    void showAboutDialog(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Sapioculture, the favourite New Zealand organ donation registration application.");
        alert.setContentText(String.format("Sapioculture is a Java application for easily registering as a New Zealand organ donor and" +
                " viewing information about other donors.\n\nSapioculture is brought to you by Team A:" +
                "\nRobert Bruce" +
                "\nAlan Brook" +
                "\nQuentin McKenzie" +
                "\nTimothy McKenzie" +
                "\nRebecka Cox" +
                "\nLucy Turner" +
                "\nImas Neupane" +
                "\nCameron Auld" +
                "\nBenjamin Schmidt"

        ));
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }

    /**
     * Creates a help information dialog with given title and content.
     * @param title The header text of the alert.
     * @param content The content of the alert.
     */
    private void createInformationDialog(String title, String content) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Help");
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();

    }

}