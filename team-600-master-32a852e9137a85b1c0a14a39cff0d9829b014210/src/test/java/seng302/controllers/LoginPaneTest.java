package seng302.controllers;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

import org.junit.*;

import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import seng302.App;
import seng302.model.GitlabGUITestSetup;

import static junit.framework.TestCase.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.hasText;

import java.util.concurrent.TimeoutException;

@Ignore
public class LoginPaneTest extends ApplicationTest {

    private App mainGUI;
    private TextField usernameTextField;
    private Button loginButton;
    private Button createUserButton;
    private Label errorLabel;
    private Button clinicianButton;
    private TextField passwordField;


    // ------ Precondition Setting and Testing


    /**
     * These settings are so test can be run in CI-runner, if you want to watch the robot perform the
     * tests on your local machine, comment this function out.
     */
    @BeforeClass
    public static void headless() {
        GitlabGUITestSetup.headless();
    }


    /**
     * Start up the loginPane view and its controller.
     * @param stage the stage to be set
     * @throws Exception If the stage is not set
     */
    @Override
    public void start(Stage stage) throws Exception {
        mainGUI = new App();
        mainGUI.start(stage);

        LoginController.accountManager.importClinicians();
        LoginController.accountManager.addClinicianIfNoneExists();
    }


    /**
     * Links the variables with the FXML buttons and labels.
     */
    @Before
    public void setUp() {
        usernameTextField = lookup("#usernameTextField").query();
        passwordField = lookup("#passwordField").query();
        createUserButton = lookup("#createDonorButton").query();
        loginButton = lookup("#loginButton").query();
        errorLabel = lookup("#errorLabel").query();
        clinicianButton = lookup("#clinicianButton").query();
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
     * Basic tests to check all the buttons are initialized correctly.
     */
    @Test
    public void verifyAllButtonsInitialized(){
        assertTrue(usernameTextField.getText().contains(""));
        assertTrue(passwordField.getText().contains(""));
        assertTrue(createUserButton.getText().contains("Create Donor"));
        assertTrue(loginButton.getText().contains("Login"));
        assertTrue(clinicianButton.getText().contains("Clinician"));
        assertTrue(errorLabel.getText().contains("Donor/Receiver Login"));
    }


    // ------ Donor Login Controller Tests


    /**
     * Tests that the donor login behaves as expected in when the correct NHI is entered
     */
    @Test
    @Ignore
    public void donorLoginCorrectNHI() {
        //Setting the NHI for the logging in of the donor
        clickOn(usernameTextField).write("ABC1234");
        clickOn(passwordField).write("password");
        clickOn(loginButton);

        //Verification that the fields on the view pane exist (the page has changed from login)
        Label firstNames = lookup("#firstNames").query();
        Label lastName = lookup("#lastName").query();
        Label nationalHealthIndex = lookup("#nationalHealthIndex").query();

        //Verification that the correct user is shown once logged in
        Assert.assertEquals( "Sweeny", firstNames.getText());
        //verifyThat(firstNames, hasText("Sweeny"));
        verifyThat(lastName, hasText("Todd"));
        verifyThat(nationalHealthIndex, hasText("ABC1234"));
    }


    /**
     * Tests that the donor login behaves as expected when an invalid NHI is entered
     */
    @Test
    public void donorLoginInvalidNHI() {
        clickOn(usernameTextField).write("ABCGM12354");
        clickOn(passwordField).write("password");
        clickOn(loginButton);
        assertTrue(errorLabel.getText().contains("Invalid username or password."));
    }


    /**
     * Tests that the donor creation button changes from the login pane to the donor creation pane
     */
    @Test
    @Ignore
    public void donorCreationClicked() {
        clickOn(createUserButton);
        Label createPaneTitle = lookup("#createAccountLabel").query();
        verifyThat(createPaneTitle, hasText("Create Account"));
    }


    // ------ Clinician Login Controller Tests


    /**
     * Tests that the donor login behaves as expected in when the correct NHI is entered
     */
    @Test
    @Ignore
    public void staffLoginCorrectID() {
        //Setting the Staff field for the logging in of the donor
        clickOn("#clinicianButton");
        clickOn(usernameTextField).write("0");
        clickOn(passwordField).write("password");
        clickOn(loginButton);

        //Verification that the fields on the main menu pane exist (the page has changed from login)
        Label mainMenuTitle = lookup("#mainMenuTitle").query();
        Label searchForDonor = lookup("#searchForDonorLabel").query();

        //Verification that the correct user is shown once logged in
        verifyThat(mainMenuTitle, hasText("Main Menu"));
        verifyThat(searchForDonor, hasText("Search for Donor"));
    }


    /**
     * Tests that the donor login acts as expected when an incorrect Staff ID is entered
     */
    @Test
    public void staffLoginIncorrectID() {
        clickOn("#clinicianButton");
        clickOn(usernameTextField).write("567");
        clickOn(passwordField).write("password");
        clickOn(loginButton);
        assertTrue(errorLabel.getText().contains("Invalid username or password."));
    }


    /**
     * Tests that the clinician creation button changes the pane from the login pane to the clinician creation pane
     */
    @Test
    @Ignore
    public void clinicianCreationClick() {
        clickOn("#clinicianButton");
        clickOn("#usernameTextField");
        write("0");
        clickOn("#loginButton");
        clickOn("#createClinicianButton");
        Label createPaneTitle = lookup("#createClinicianLabel").query();
        assertTrue(createPaneTitle.getText().contains("Create Clinician"));
    }
}