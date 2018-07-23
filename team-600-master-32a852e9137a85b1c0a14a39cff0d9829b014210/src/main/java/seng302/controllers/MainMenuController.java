package seng302.controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import seng302.model.AccountManager;
import seng302.model.PageNav;
import seng302.model.person.Clinician;


public class MainMenuController {

    // JavaFX attributes referenced in 'MainMenu.fxml'.
    @FXML public Label mainMenuTitle;
    public Label searchForDonorLabel;
    @FXML public Button viewEditClinicianButton;
    @FXML public TextField searchTextField;
    @FXML public Button searchButton;
    @FXML public Button viewAllDonorsButton;
    @FXML public Button addNewDonorButton;
    @FXML public Button logoutButton;
    @FXML private Button createClinicianButton;

    private static String searchValue = "";

    /**
     * Loads the view/edit clinician page when the 'view/edit clinician' button is pressed in the GUI.
     * @param event
     */
    @FXML
    void clinicianButtonPressed(ActionEvent event) {
        PageNav.loadNewPage(PageNav.VIEWEDITCLINICIAN);
    }

    /**
     * Retrieves text entered by the user from the search field and switches to
     * the list view screen using it as search criteria.
     *
     * @param event The action event that triggered the event handler.
     */
    @FXML
    private void searchSelected(ActionEvent event) {

        searchValue = searchTextField.getText();
        PageNav.searchValue = searchValue;  // Set the search value to match what the user entered
        PageNav.loadNewPage(PageNav.LISTVIEW);

    }


    /**
     * Changes the GUI pane to the create clinician pane when the "create clinician" button is pressed during runtime.
     * @param event the action of the user pressing the "create clinician" button in the GUI
     */
    @FXML
    private void createClinician(ActionEvent event) {
        PageNav.loadNewPage(PageNav.CREATECLINICIAN);
    }


    /**
     * Switches to the list view screen with no search criteria.
     *
     * @param event The action event that triggered the event handler.
     */
    @FXML
    private void viewAllDonorsSelected(ActionEvent event) {

        searchValue = "";
        PageNav.searchValue = searchValue;
        PageNav.loadNewPage(PageNav.LISTVIEW);


    }


    /**
     * Switches to the add new donor screen.
     *
     * @param event The action event that triggered the event handler.
     */
    @FXML
    private void addNewDonorSelected(ActionEvent event) {

        CreateUserPaneController.lastScreen = PageNav.MAINMENU;
        PageNav.loadNewPage(PageNav.CREATE);

    }

    /**
     * Logs the user out and returns to the login screen.
     *
     * @param event The action event that triggered the event handler.
     */
    @FXML
    private void logoutSelected(ActionEvent event) {
        ClinicianProfileController.setClinician(null);
        AccountManager.setCurrentUser(null);
        PageNav.loadNewPage(PageNav.LOGIN);
    }


    /**
     * Resets the GUI whenever the main menu screen is loaded.
     */
    @FXML
    public void initialize() {

        searchValue = "";
        searchTextField.setText(searchValue);

    }

}
