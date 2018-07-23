package seng302.controllers;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import seng302.model.AccountManager;
import seng302.model.PageNav;


public class MainMenuController {

    // JavaFX attributes referenced in 'MainMenu.fxml'.
    @FXML public Label mainMenuTitle;
    @FXML public Button viewEditClinicianButton;
    @FXML public Button viewAllDonorsButton;
    @FXML public Button addNewDonorButton;
    @FXML public Button logoutButton;

    private static String searchValue = "";

    /**
     * Loads the view/edit clinician page when the 'view/edit clinician' button is pressed in the GUI.
     */
    @FXML
    void clinicianButtonPressed() {
        PageNav.loadNewPage(PageNav.VIEWEDITCLINICIAN);
    }


    /**
     * Changes the GUI pane to the create clinician pane when the "create clinician" button is pressed during runtime.
     */
    @FXML private void createClinician() {
        PageNav.loadNewPage(PageNav.CREATECLINICIAN);
    }


    /**
     * Switches to the list view screen with no search criteria.
     *
     */
    @FXML private void viewAllDonorsSelected() {
        searchValue = "";
        PageNav.searchValue = searchValue;
        PageNav.loadNewPage(PageNav.LISTVIEW);
    }


    /**
     * Switches to the add new donor screen.
     *
     */
    @FXML
    private void addNewDonorSelected() {
        CreateUserPaneController.lastScreen = PageNav.MAINMENU;
        PageNav.loadNewPage(PageNav.CREATE);
    }

    /**
     * Logs the user out and returns to the login screen.
     *
     */
    @FXML
    private void logoutSelected() {
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
    }

}
