package seng302;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import seng302.controllers.MenuBarController;
import seng302.controllers.childWindows.ChildWindow;
import seng302.controllers.childWindows.ChildWindowManager;
import seng302.controllers.childWindows.ChildWindowType;
import seng302.model.*;
import seng302.model.person.LogEntry;
import seng302.model.person.User;


public class App extends Application {

    // Class attributes.
    private static Stage window;
    private static ArrayList<ChildWindow> childWindows = new ArrayList<>();
    private static AccountManager Database;
    public static ChildWindowManager childWindowManager = ChildWindowManager.getChildWindowManager();
    private static CommandLine commandLine;
    private static boolean launchedGUIBefore = false;
    private static UndoableManager undoableManager = new UndoableManager();
    private final Set<KeyCode> pressedKeys = new HashSet<>();
    private static boolean unsavedChanges = false;
    private static boolean saveInProgress = false;
    private static DrugInteractionsCache drugInteractionsCache;

  public static UndoableManager getUndoableManager() {
    return undoableManager;
  }


  /**
   * Gets the main stage of the GUI..
   *
   * @return The main stage of the GUI.
   */
  public static Stage getWindow() {

    return App.window;

  }


  /**
   * Returns the instantiated account manager Database.
   *
   * @return An AccountManager instance.
   */
  public static AccountManager getDatabase() {
    if (Database == null) {
      Database = new AccountManager();
      Database.importAccounts();
      Database.importClinicians();
      Database.addClinicianIfNoneExists();
    }
    return Database;

  }

    /**
     * Returns the instantiated cache
     * @return A cache instance.
     */
    public static DrugInteractionsCache getDrugInteractionsCache() {
        if (drugInteractionsCache == null){
            drugInteractionsCache = new DrugInteractionsCache();
        }
        return drugInteractionsCache;

    }


  /**
   * Sets window to the specified JavaFX stage and shows the stage to the user.
   *
   * @param stage The stage to set window to.
   * @throws IOException An exception occurred.
   */
  @Override
  public void start(Stage stage) throws IOException {
    window = stage;

    // Set close request so that all windows are closed with the main window.
    window.setOnCloseRequest(request -> {

      closeChildWindows();

    });

    initialiseUnsavedIndicator();
    childWindowManager = ChildWindowManager.getChildWindowManager();

    stage.setTitle("SapioCulture");
    stage.setScene(createScene(loadMainPane()));
    stage.show();

  }


  /**
   * Loads the main pane from an FXML file.
   *
   * @return The main pane.
   * @throws IOException When the main pane cannot be retrieved from FXML.
   */
  private Pane loadMainPane() throws IOException {

    FXMLLoader loader = new FXMLLoader();

    Pane mainPane = loader.load(getClass().getResourceAsStream(PageNav.MENUBAR));
    MenuBarController menuBarControl = loader.getController();

    PageNav.setMenuBarController(menuBarControl);
    PageNav.loadNewPage(PageNav.LOGIN);

    return mainPane;
  }


  /**
   * Closes all child windows.
   */
  public static void closeChildWindows() {

    childWindowManager.closeAllChildWindows();

  }


  /**
   * Adds a new stage to childWindows. This is done so that the new window can be closed with the
   * main window.
   *
   * @param window The window to be added to the childWindow list.
   * @param user The user object to be associated with the window.
   */
  public static void addChildWindow(Stage window, User user) {

    childWindowManager.addChildWindow(window, user);

  }


  /**
   * Adds a child window without an associated account.
   *
   * @param window The window to be added.
   */
  public static void addChildWindow(Stage window, ChildWindowType type) {

    childWindowManager.addChildWindow(window, type);

  }


  /**
   * Searches for a child window associated with the given account. If one exists, it moves it to
   * the front and returns true. If it does not, the method returns false signifying failure.
   *
   * @param account The account object associated with the window that is to
   * @return True if a window was brought forward. False otherwise.
   */
  public static boolean childWindowToFront(User account) {

    return childWindowManager.childWindowToFront(account);

  }


  /**
   * Searches for a child window of the given type. If one exists, it is moved to the front and the
   * method returns true. Otherwise, it returns false.
   *
   * @param type The ChildWindow type.
   */
  public static boolean childWindowToFront(ChildWindowType type) {

    return childWindowManager.childWindowToFront(type);

  }


  /**
   * Creates a scene using the specified pane.
   *
   * @param mainPane The pane to create the scene with.
   * @return The new scene.
   */
  private Scene createScene(Pane mainPane) {

    Scene scene = new Scene(mainPane);
    return scene;

  }


  /**
   * Gets the command line handler.
   *
   * @return The command line object.
   */
  public static CommandLine getCommandLine() {
    return commandLine;

  }


  /**
   * /* Launches the GUI for the application
   */
  private static void launchGUI(String[] args) {
    if (launchedGUIBefore) {
      System.out.println(
          "ERROR: The GUI can only be launched once. If you wish to launch it again, please restart the application.");
    } else {
      System.out.println(
          "Launching GUI.\nPlease note that the GUI can only be launched once. You will have to restart the application if you close it.");
      launchedGUIBefore = true;

      launch(args);

    }
  }

  /**
   * Appends an asterisk to the title of the main window if the character is not already present.
   */
  private static void appendAsteriskToTitle() {

    String title = window.getTitle();

    if (title.charAt(title.length() - 1) != '*') {

      window.setTitle(title + '*');

    }

  }

  /**
   * Removes an asterisk from the end of the main window title if one is present.
   *
   * @throws IllegalStateException When the window does not exist.
   */
  private static void removeAsteriskFromTitle() throws IllegalStateException {

    if (window == null) {

      throw new IllegalStateException("The main window was not " +
          "initialised. The unsaved indicator could not be removed.");

    }

    String title = window.getTitle();
    int lastCharIndex = title.length() - 1;

    if (title.charAt(lastCharIndex) == '*') {

      window.setTitle(title.substring(0, lastCharIndex));

    }

  }


  /**
   * Creates a listener which places the application in an unsaved state after a change is detected
   * on the system log. This indicates that the application must be saved by the user.
   */
  private static void initialiseUnsavedIndicator() {

    ListChangeListener<LogEntry> listener = change -> {

      unsavedChanges = true;
      appendAsteriskToTitle();

    };

    AccountManager.addSystemLogListener(listener);

  }


  public static void setSaveInProgress(boolean value){
      saveInProgress = value;
  }


  /**
   * Removes unsaved status indicators, placing the application in a saved state.
   *
   * @throws IllegalStateException When the variable window is not initialised.
   */
  public static void removeUnsavedIndicator() throws IllegalStateException {

    unsavedChanges = false;
    removeAsteriskFromTitle();

  }


  /**
   * Returns true if the application is in an unsaved state, or false if it is not.
   *
   * @return The value of the variable unsavedChanges.
   */
  public static boolean unsavedChangesExist() {

    return unsavedChanges;

  }

    /**
     * Returns true if the application is in an unsaved state, or false if it is not.
     * @return The value of the variable unsavedChanges.
     */
    public static boolean saveInProgress() {

        return saveInProgress;

    }


  /**
   * Parses an input token, and if the token is correct, prints a relevant message, and returns the
   * user to the main menu in the command line
   *
   * @param input the scanner for taking in all user input
   * @return The scanner input that takes in all user input
   */
  public static Scanner Help(Scanner input) {
    String subHelp = input.next();

    if (subHelp.equalsIgnoreCase("all")) {
      System.out.println(
          "The required input for creating an account: Create name=<Full Name> dateOfBirth=<date of birth> nhi=<Nhi number>");
      System.out.println(
          "Full Name -All the names the person has. the first name will be stored as their first name, while the last name given will be stored as the last. All names in between will be stored as other names. There should be no space between the equals sign and the first name.");
      System.out.println(
          "date of birth -The date of birth of the account holder. The format is yyyyMMdd. y =year, M=month, d = day. There should be no space in between the equals and the date of birth.");
      System.out.println(
          "nhi -The unique number given by the New Zealand Ministry of health to prove uniqueness");
      System.out.println(
          "========================================================================================");
      System.out.println(
          "The required input for updating an account: Update <nhi code> <object> <attribute> <value>");
      System.out.println(
          "========================================================================================");
      System.out.println("There are three separate inputs for viewing accounts");
      System.out.println("1. view nhi <nhi number> <object> ");
      System.out.println("2. view name <first name> <last name> <object> ");
      System.out.println("3. view donors <object> ");
      System.out.println(
          "<nhi number> -The unique number given by the New Zealand Ministry of health to prove uniqueness");
      System.out.println("<object> - either 'profile', 'log', 'attributes' or 'organs'");
      System.out.println(
          "========================================================================================");
      System.out.println(
          "The required input for importing a json file into the system: import <filename> ");
      System.out.println("<filename> - the file which you wish to import into the system");
      System.out.println(
          "========================================================================================");
      System.out.println(
          "The required input for exporting the current data to a persistent json file: export <filename>");
      System.out.println("<filename> - the file where you wish the data to be saved");
      System.out.println(
          "========================================================================================");
      System.out.println("the required input for deleting an account: delete <nhi number>");
      System.out.println(
          "<nhi number> - The unique number given to every patient in the New Zealand Health system.");
      System.out.println(
          "========================================================================================");
      System.out.println(
          "For more information regarding commands for the system, please consult the user_manual");
    } else if (subHelp.equalsIgnoreCase("create")) {
      //Print the template for creating an account
      System.out.println(
          "The required input for creating an account: Create name=<Full Name> dateOfBirth=<date of birth> nhi=<Nhi number>");
      System.out.println(
          "Full Name -All the names the person has. the first name will be stored as their first name, while the last name given will be stored as the last. All names in between will be stored as other names. There should be no space between the equals sign and the first name.");
      System.out.println(
          "date of birth -The date of birth of the account holder. The format is yyyyMMdd. y =year, M=month, d = day. There should be no space in between the equals and the date of birth.");
      System.out.println(
          "nhi -The unique number given by the New Zealand Ministry of health to prove uniqueness");
      System.out.println("For more information, please consult the user_manual");
    } else if (subHelp.equalsIgnoreCase("update")) {
      System.out.println(
          "The required input for updating an account: Update <nhi code> <object> <attribute> <value>");
      System.out.println(
          "For more detailed information about the seperate terms, please consult the user_manual");
    } else if (subHelp.equalsIgnoreCase("view")) {
      System.out.println("There are three separate inputs for viewing accounts");
      System.out.println("1. view nhi <nhi number> <object> ");
      System.out.println("2. view name <first name> <last name> <object> ");
      System.out.println("3. view donors <object> ");
      System.out.println(
          "<nhi number> -The unique number given by the New Zealand Ministry of health to prove uniqueness");
      System.out.println("<object> - either 'profile', 'log', 'attributes' or 'organs'");
      System.out.println("Please consult the user_manual for more information");
    } else if (subHelp.equalsIgnoreCase("import")) {
      System.out.println(
          "The required input for importing a json file into the system: import <filename> ");
      System.out.println("<filename> - the file which you wish to import into the system");
      System.out.println("Please consult the user_manual for more information");
    } else if (subHelp.equalsIgnoreCase("export")) {
      System.out.println(
          "The required input for exporting the current data to a persistent json file: export <filename>");
      System.out.println("<filename> - the file where you wish the data to be saved");
      System.out.println("Please consult the user_manual for more information");
    } else if (subHelp.equalsIgnoreCase("delete")) {
      System.out.println("the required input for deleting an account: delete <nhi number>");
      System.out.println(
          "<nhi number> - The unique number given to every patient in the New Zealand Health system.");
      System.out.println("For more information, please consult the user_manual");
    } else if (subHelp.equalsIgnoreCase("launch")) {
      System.out.println("Launches the GUI component of the application.");
      System.out.println("Can only be called once while the application is running.");
    } else {
      System.out.println("Invalid query. returning to the main menu...");
    }
    return input;
  }


  /**
   * The main method of the application.
   *
   * @param args Command line arguments represented as an array of String objects.
   */
  public static void main(String[] args) {

    // Create GUI.
    System.out.println("Welcome to the Sapioculture");

        childWindowManager = ChildWindowManager.getChildWindowManager();
        getDrugInteractionsCache();
        Database = new AccountManager();
        Database.setSystemLog(Marshal.importSystemLog());

    Database.importAccounts();
    Database.importClinicians();
    Database.importAdmins();
    Database.addClinicianIfNoneExists();
    Database.addDefaultAdminIfNoneExists();
    Database.exportAdmins(); // Makes sure the default admin is created and saved on first start up.
    Database
        .exportClinicians(); //Makes sure that a default clinician is created and saved on first start up.
    launchGUI(
        args); // Currently call this to launch GUI automatically as commandline scanner not working on windows environment.
    // Command line history.
    commandLine = new CommandLine();
  }
}
