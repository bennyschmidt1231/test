package seng302.controllers;

import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.*;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import seng302.App;
import seng302.model.GitlabGUITestSetup;

import java.io.File;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test class for the create Clinician controller class and GUI.
 */
public class clinicianCreationControllerTest extends ApplicationTest{


    /**
     * Instance of the application in order to test GUI elements.
     */
    private App mainGUI;


    // GUI elements of alert dialogue boxes
    private String alertID;
    private Button alertButton;
    private DialogPane alertDialogue;


    //GUI elements for the login pane:
    private TextField usernameTextField;
    private Button loginButton;
    private TextField passwordField;
    private Button createClinicianButton;


    // GUI elements for the create clinician pane:
    private TextField givenNameTextField;
    private TextField lastNameTextField;
    private TextField staffIDTextField;
    private TextField streetAddressTextField;
    private TextField passwordTextField;
    private ComboBox<String> regionComboBox;
    private Button doneButton;
    private Button cancelButton;


    // GUI elements for the main menu pane:
    private Button viewEditClinicianButton;



    @Override
    /**
     * Sets the GUI stage
     */
    public void start (Stage stage) throws Exception {
        mainGUI = new App();
        mainGUI.start(stage);
        LoginController.accountManager.importClinicians();
        LoginController.accountManager.addClinicianIfNoneExists();
    }


    @Before
    /**
     * Initialize links to the FXML of the Login Page
     */
    public void setUp() {
        clickOn("#clinicianButton");
        usernameTextField = lookup("#usernameTextField").query();
        clickOn(usernameTextField).write("3");
        passwordField = lookup("#passwordField").query();
        clickOn(passwordField).write("password");
        loginButton = lookup("#loginButton").query();
        clickOn(loginButton);
        clickOn("#createClinicianButton");
    }

    /**
     * These settings are so test can be run in CI-runner, if you want to watch the robot perform the
     * tests on your local machine, comment this function out.
     */
    @BeforeClass
    public static void headless() {
        GitlabGUITestSetup.headless();
    }




    /**
     * Tear down the window after test
     * @throws TimeoutException If there is a timeout
     */
    @After
    public void tearDown() throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[] {});
        release(new MouseButton[] {});
        mainGUI.stop();
    }


    /**
     * Sets up the GUI elements of the clinician creation pane for testFX tests to interact with.
     */
    public void setCreateClinicianPane() {
        givenNameTextField = lookup("#givenNameTextField").query();
        lastNameTextField = lookup("#lastNameTextField").query();
        staffIDTextField = lookup("#staffIDTextField").query();
        streetAddressTextField = lookup("#streetAddressTextField").query();
        passwordTextField = lookup("#passwordTextField").query();
        regionComboBox = lookup("#regionComboBox").query();
        doneButton = lookup("#doneButton").query();
        cancelButton = lookup("#cancelButton").query();
    }


    @Test
    /**
     *  Tests create clinician GUI by checking if an error alert pop up box is created when invalid values are inputted into the staff ID text field.
     */
    public void createClinicianBadStaffIDValues() {
        setCreateClinicianPane();

        // Test suite for invalid Staff ID fields: empty string, non-integer, < 0, already existing
        alertID = "Staff ID";

        verifyBadUserInputAlertIsShown();


        clickOn(staffIDTextField).write("abc");
        verifyBadUserInputAlertIsShown();


        clickOn(staffIDTextField).write("-1");
        verifyBadUserInputAlertIsShown();


        clickOn(staffIDTextField).write("0");
        verifyBadUserInputAlertIsShown();

    }


    @Test
    /**
     *  Tests create clinician GUI by checking if an error alert pop up box is created when invalid values are inputted into the given name text field.
     */
    public void createClinicianBadGivenNameValues() {
        setCreateClinicianPane();
        staffIDTextField.setText("668");

        // Test suite for invalid Given Name fields: empty string, non-alphabetical, name > 50 characters

        alertID = "Given Name";
        lastNameTextField.setText("");
        sleep(5);
        verifyBadUserInputAlertIsShown();

        givenNameTextField.setText("123");
        sleep(5);
        verifyBadUserInputAlertIsShown();

        givenNameTextField.setText("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        sleep(5);
        verifyBadUserInputAlertIsShown();


    }


    @Test
    @Ignore
    /**
     * THIS TEST WORKS BY ITSELF, BUT FAILS WHEN RUN HEADLESS
     *  Tests create clinician GUI by checking if an error alert pop up box is created when invalid values are inputted into the last name text field.
     */
    public void createClinicianBadLastNameValues() {
        setCreateClinicianPane();
        staffIDTextField.setText("668");
        givenNameTextField.setText("Light");

        // Test suite for invalid Last Name fields: empty string, non-alphabetical, name > 50 characters

        alertID = "Last Name";
        lastNameTextField.setText("");
        verifyBadUserInputAlertIsShown();

        lastNameTextField.setText("&*()");
        verifyBadUserInputAlertIsShown();

        lastNameTextField.setText("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        verifyBadUserInputAlertIsShown();

    }


    @Test
    /**
     *  Tests create clinician GUI by checking if an error alert pop up box is created when invalid values are inputted into the address text field.
     */
    public void createClinicianBadAddressValues() {
        setCreateClinicianPane();
        staffIDTextField.setText("668");
        givenNameTextField.setText("Light");
        lastNameTextField.setText("Yagami");
        //passwordTextField.setText("");

        // Test suite for invalid street address fields: empty string, non-alphanumeric, address > 100 characters

        alertID = "Address";

        verifyBadUserInputAlertIsShown();

        streetAddressTextField.setText("&*()");
        verifyBadUserInputAlertIsShown();

        streetAddressTextField.setText("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        verifyBadUserInputAlertIsShown();

    }


    @Test
    /**
     *  Tests create clinician GUI by checking if an error alert pop up box is created when the region has not been picked.
     */
    public void createClinicianNoRegionPicked() {
        setCreateClinicianPane();
        staffIDTextField.setText("668");
        givenNameTextField.setText("Light");
        lastNameTextField.setText("Yagami");
        streetAddressTextField.setText("Kanto region, Honshu, Japan");

        // Test for non-selected region

        alertID = "Region";

        verifyBadUserInputAlertIsShown();

    }


    @Test
    /**
     *  Tests create clinician GUI by checking if an error alert pop up box is created when invalid values are inputted into the Password text field.
     */
    public void createClinicianBadPasswordValues() {
        setCreateClinicianPane();
        staffIDTextField.setText("668");
        givenNameTextField.setText("Light");
        lastNameTextField.setText("Yagami");
        streetAddressTextField.setText("Kanto region, Honshu, Japan");
        clickOn(regionComboBox).clickOn("Auckland");
        //clickOn(regionComboBox).clickOn(1150, 500);  //find out how to get bot to click on a region relatively without abs coordinates

        // Test suite for invalid password fields: empty string, non-alphanumeric, password > 30 characters, password < 6 characters

        alertID = "Password";
        passwordTextField.setText("");
        verifyBadUserInputAlertIsShown();

        passwordTextField.setText("%^&*()#@");
        verifyBadUserInputAlertIsShown();

        passwordTextField.setText("Kira");
        verifyBadUserInputAlertIsShown();

        passwordTextField.setText("LdidyouknowDeathgodsonlyeatapples");
        verifyBadUserInputAlertIsShown();


    }


    /**
     * Attempts to find the alert dialogue pop up (given by the "alertID" class attribute) in the application GUI and checks
     * if the the header of that alert box matches an invalid user input error alert box.
     */
    public void verifyBadUserInputAlertIsShown(){
        clickOn(doneButton);
            // We try to find the Alert Dialogue box and check its header matches the input error type we were expecting
        try {
            alertDialogue = lookup(String.format("#bad%sAlertDialog", alertID.replace(" ", ""))).query();
            alertButton = lookup(String.format("#bad%sAlertOkButton", alertID.replace(" ", ""))).query();

            // If the alert box is the one we expect, then its header message will contain the alertID string.
            assertEquals(String.format("Invalid %s given", alertID) ,alertDialogue.getHeaderText() );
            clickOn(alertButton);

            // We could not find the Alert Dialogue box
        } catch (NullPointerException e) {
            fail(String.format("Alert Box for Header %s could not be found", alertID ));
        }

    }


    @Test
    /**
     * Tests creation of a clinician object in the app GUI by filling in the text fields with correct values and clicking 'done'.
     */
    public void createClinicianTest() {
        //Populate text fields with valid values
        setCreateClinicianPane();
        clickOn(staffIDTextField).write("666");
        clickOn(givenNameTextField).write("Light");
        clickOn(lastNameTextField).write("Yagami");
        clickOn(streetAddressTextField).write("Kanto region, Honshu, Japan");
        clickOn(passwordTextField).write("Deathgodsonlyeatapples");
        clickOn(regionComboBox).clickOn("Auckland");

        // create a new clinician
        clickOn(doneButton);
        try {
            alertDialogue = lookup("#goodSaveAlert").query();
            alertButton = lookup("#goodSaveButton").query();
            assertEquals("Success" ,alertDialogue.getHeaderText() );
            clickOn(alertButton);

            // We could not find the Alert Dialogue box
        } catch (NullPointerException e) {
            fail("Alert Box for Header Success could not be found");
        }


        // Now we find and delete the clinician json file we created
        File file = new File("./clinicians/666.json");
        try {
            assertTrue(file != null);
            file.delete();
        } catch (Exception e) {
            System.out.println("Could not find json file");
            e.printStackTrace();
        }
    }




}
