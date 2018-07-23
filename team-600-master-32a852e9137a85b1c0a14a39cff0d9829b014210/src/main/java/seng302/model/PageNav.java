package seng302.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import seng302.App;
import seng302.controllers.MenuBarController;
import seng302.controllers.StatusBarController;

/**
 * Class for other controllers to use to load other pages in the app. Based on the PageNavigator class from seng202 team 8 (2017).
 */
public class PageNav {

    public static String searchValue = ""; // The search term to keep consistent between searches
    public static Boolean isAdministrator = false;

    public static final String MAINMENU = "/MainMenu.fxml";
    public static final String EDIT = "/editPane.fxml";
    public static final String VIEW = "/viewProfilePane.fxml";
    public static final String LOGIN = "/LoginPane.fxml";
    public static final String MENUBAR = "/menuBar.fxml";
    public static final String LISTVIEW = "/ListView.fxml";
    public static final String CREATE = "/createUserPane.fxml";
    public static final String LOG = "/logPane.fxml";
    public static final String CREATECLINICIAN = "/clinicianCreation.fxml";
    public static final String CREATECLINICIANCHILD = "/clinicianCreationChildPane.fxml";
    public static final String VIEWEDITCLINICIAN = "/ClinicianProfileView.fxml";
    public static final String TRANSPLANTLIST = "/transplantWaitingListPane.fxml";

    public static final String ILLNESS = "/createOrModifyIllnessPane.fxml";
    public static final String PROCEDURE = "/addEditProcedure.fxml";

    public static final String ADMINMENU = "/administratorMainMenu.fxml";
    public static final String CLINICIANSLIST = "/cliniciansList.fxml";
    public static final String ADMINSLIST = "/adminsList.fxml";
    public static final String SYSTEMLOG = "/systemLog.fxml";
    public static final String CREATEADMIN = "/administratorCreation.fxml";
    public static final String VIEWADMIN = "/administratorViewProfile.fxml";
    public static final String COMMANDLINE = "/cliTab.fxml";


    /**
     *  The app main controller to use.
     */
    private static MenuBarController menuBar;

    private static String currentNav;


    /**
     * Keeps the menuBar controller as the back layer when navigating to other pages
     * @param menuBarController The menu bar controller to be set for this instance of the class PageNav
     */
    public static void setMenuBarController(MenuBarController menuBarController) {
        PageNav.menuBar = menuBarController;
    }

    /**
     * Change the current page with reference to the last tab in tabbed pane
     * @param page Page to go to
     */
    public static void loadNewPage (String page) {
        try {
            System.getProperty("user.dir");
            menuBar.setNewPage(FXMLLoader.load(PageNav.class.getResource(page)));
            currentNav = page;
        } catch (IOException e) { // we can't find the resources we need
            System.getProperty("user.dir");
        }

    }

    /**
     * Change the current page with reference to the last tab in tabbed pane
     * @param page Page to go to
     * @param tabIndex Tab previously used
     * @param tabString String of tab
     */
    public static void loadNewPage(String page, int tabIndex, String tabString) {
        try {
            System.getProperty("user.dir");
            menuBar.setNewPage(FXMLLoader.load(PageNav.class.getResource(page)));
            currentNav = page;
            TabPane tabPane = (TabPane) App.getWindow().getScene().lookup("#" + tabString); // Get tab in edit tab
            if(tabIndex < 3) { // Tabs containing clinician only information
                tabPane.getSelectionModel().clearAndSelect(tabIndex);
            } else if(tabIndex == 3 && tabString.equals("profileViewTabbedPane")) { // Other medical history which can be edited by the user
                tabPane.getSelectionModel().clearAndSelect(5);
            } else if(tabIndex == 5 && tabString.equals("mainTabPane")) { // Other medical history which can be edited by the user
                tabPane.getSelectionModel().clearAndSelect(3);
            }
        } catch (IOException e) { // we can't find the resources we need
            System.getProperty("user.dir");
        }
    }

    public static String getCurrentNav() {
        return currentNav;
    }
}
