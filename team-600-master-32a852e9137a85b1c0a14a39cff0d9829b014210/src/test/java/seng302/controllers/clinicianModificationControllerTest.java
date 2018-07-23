package seng302.controllers;

import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.*;
import org.testfx.api.FxToolkit;
import seng302.App;
import seng302.model.GitlabGUITestSetup;

import org.testfx.framework.junit.ApplicationTest;
import seng302.model.AccountManager;
import seng302.model.person.Clinician;

import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.hasText;


public class clinicianModificationControllerTest extends ApplicationTest {


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
    private TextField passwordField;
    private Button loginButton;


    // GUI elements for the main menu pane:
    private Button viewEditClinicianButton;


    //GUI elements for the view/edit clinician pane:
    private Button doneButton;
    private Button Back;
    private TextField clinicianProfileNameText;
    private TextField lastNameText;
    private TextArea workAddressText;
    private ComboBox<String> regionComboBox;
    private Label clinicianTitleLabel;
    private Label staffIdLabel;
    private ListView view;
    private Label actionLabel; // From the status bar.

    private final int STAFF_ID = 3;

    private Label dateLabel;
    private Label firstNameLabel;
    private Label lastNameLabel;
    private Label workAddressLabel;
    private Label regionLabel;
    private Button editButton;
    private Button cancelButton;
    private Button backButton;




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
     * Initialize links to the FXML of the Login Page and then navigates to the main menu and from there to the view/edit clinician window
     */
    public void setUp() {
        resetAccount();
        clickOn("#clinicianButton");
        usernameTextField = lookup("#usernameTextField").query();
        passwordField = lookup("#passwordField").query();
        loginButton = lookup("#loginButton").query();
        clickOn(usernameTextField).write(Integer.toString(STAFF_ID));
        clickOn(passwordField).write("password");
        clickOn(loginButton);
        viewEditClinicianButton = lookup("#viewEditClinicianButton").query();
        clickOn(viewEditClinicianButton);
        setClinicianValues();
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
     * Resets the given name, last name, work address, and region of the
     * clinician with staff ID 'STAFF_ID'. This is called to prevent tests from
     * being affected by changes made during previous tests.
     */
    private void resetAccount() {

        AccountManager accountManager = mainGUI.getDatabase();

        for (Clinician clinician:accountManager.getClinicians()) {

            if (clinician.getUserName().equals(STAFF_ID)) {

                clinician.setFirstName("Suiko");
                clinician.setLastName("Yagami");
                clinician.updateWorkAddress("Kanto region, Japan");
                clinician.updateRegion("Canterbury");

                break;

            }

        }

    }


    /**
     * Initializes the values of the view/edit clinician GUI element hooks for the robot to interact with.
     */
    public void setClinicianValues() {
        clinicianTitleLabel = lookup("#clinicianTitleLabel").query();
        doneButton = lookup("#doneButton").query();
        Back = lookup("#Back").query();
        clinicianProfileNameText = lookup("#clinicianProfileNameText").query();
        lastNameText = lookup("#lastNameText").query();
        workAddressText = lookup("#workAddressText").query();
        regionComboBox = lookup("#regionComboBox ").query();
        staffIdLabel = lookup("#staffIdLabel").query();
        view = lookup("#view").query();
        dateLabel = lookup("#dateLabel").query();
        firstNameLabel = lookup("#firstNameLabel").query();
        lastNameLabel = lookup("#lastNameLabel").query();
        workAddressLabel = lookup("#workAddressLabel").query();
        regionLabel = lookup("#regionLabel").query();
        editButton = lookup("#editButton").query();
        cancelButton = lookup("#cancelButton").query();
        backButton = lookup("#backButton").query();
    }


    /**
     * A test to check that we have navigated to the correct window with the correct clinician once we have started up.
     * Also checks that the viewing elements are visible and the editing elements are hidden.
     */
    @Test
    public void verifyViewEditClinicianWindowIsOpen() {

        verifyThat(clinicianTitleLabel, hasText("View/Edit Clinician"));
        Assert.assertEquals("Light", firstNameLabel.getText());
        verifyThat(lastNameLabel, hasText("Yagami"));
        verifyThat(workAddressLabel, hasText("Kanto region, Japan"));
        Assert.assertFalse(clinicianProfileNameText.isVisible());
        Assert.assertFalse(lastNameText.isVisible());
        Assert.assertFalse(regionComboBox.isVisible());
        Assert.assertFalse(workAddressText.isVisible());
        Assert.assertFalse(doneButton.isVisible());
        Assert.assertFalse(cancelButton.isVisible());
        Assert.assertTrue(firstNameLabel.isVisible());
        Assert.assertTrue(lastNameLabel.isVisible());
        Assert.assertTrue(workAddressLabel.isVisible());
        Assert.assertTrue(regionLabel.isVisible());
        Assert.assertTrue(editButton.isVisible());
    }

    @Test
    /**
     *  Tests the view/edit clinician GUI page by having the robot click the edit button then input invalid values into
     *  the text fields and checking if an alert dialog box is created.
     */
    public void verifyInvalidInputAlertBoxesAreShown() {
        clickOn(editButton);
        // check that the elements that should be visible are visible, and the ones that should be hidden are hidden
        Assert.assertTrue(clinicianProfileNameText.isVisible());
        Assert.assertTrue(lastNameText.isVisible());
        Assert.assertTrue(regionComboBox.isVisible());
        Assert.assertTrue(workAddressText.isVisible());
        Assert.assertTrue(doneButton.isVisible());
        Assert.assertTrue(cancelButton.isVisible());
        Assert.assertFalse(firstNameLabel.isVisible());
        Assert.assertFalse(lastNameLabel.isVisible());
        Assert.assertFalse(workAddressLabel.isVisible());
        Assert.assertFalse(regionLabel.isVisible());
        Assert.assertFalse(editButton.isVisible());
        clinicianProfileNameText.setText("");
        verifyBadUserInputAlertIsShown();
        clickOn(clinicianProfileNameText).write("Light");


        clickOn(lastNameText).write("123");
        verifyBadUserInputAlertIsShown();
        lastNameText.setText("Yagami");


        clickOn(workAddressText).write("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        verifyBadUserInputAlertIsShown();
        workAddressText.setText("Kanto region, Japan");
    }


    // Test that this works
    @Test
    /**
     * Checks if that changes made to the clinician are saved by checking if a 'success' alert box appears
     * when changes are made to the view/edit clinician window text fields and that these changes can be seen when reloading the page.
     */
    public void verifyChangesToClinicianAreSaved() {
        clickOn(editButton);
        clinicianProfileNameText = lookup("#clinicianProfileNameText").query();
        clinicianProfileNameText.setText("");
        clickOn(clinicianProfileNameText).write("Kira");

        clickOn(doneButton);

        verifyThat(firstNameLabel, hasText("Kira"));
        clickOn(backButton);

        // Now re reload the view/edit clinician window and check that our change has persisted.
        viewEditClinicianButton = lookup("#viewEditClinicianButton").query();
        clickOn(viewEditClinicianButton);

        setClinicianValues();
        verifyThat(firstNameLabel, hasText("Kira"));

        // Now we reset the name back to its original value
        clickOn(editButton);
        clinicianProfileNameText.setText("");
        clickOn(clinicianProfileNameText).write("Light");

        clickOn(doneButton);

        verifyThat(firstNameLabel, hasText("Light"));
    }


    /**
     * Attempts to find the alert dialogue pop up in the application GUI thats generated when bad user input is entered and checks
     * if the the header of that alert box matches "Bad user Input".
     */
    public void verifyBadUserInputAlertIsShown(){
        clickOn(doneButton);
        // We try to find the Alert Dialogue box and check its header matches the input error type we were expecting
        try {
            alertDialogue = lookup("#BadUserInput").query();
            alertButton = lookup("#badAlertOkButton").query();

            // If the alert box is the one we expect, then its header message will be "Bad user Input".
            assertEquals("Bad user Input" ,alertDialogue.getHeaderText() );
            clickOn(alertButton);

            // We could not find the Alert Dialogue box
        } catch (NullPointerException e) {
            fail(String.format("Could not find bad user input alert box"));
        }

    }



    /**
     * Attempts to save a change made to the clinician and then verifies that a
     * corresponding message is shown in the status bar.
     */
    @Test
    public void verifyStatusBarIsUpdated() throws InterruptedException {

        // Retrieve existing data.
        setClinicianValues();

        // Edit first name and save clinician profile.
        clickOn(editButton);
        clinicianProfileNameText.setText("");
        clickOn(clinicianProfileNameText).write("Ned");
        clickOn(doneButton);

        actionLabel = lookup("#actionLabel").query();
        Assert.assertEquals("Light Yagami (Clinician, Staff ID: " + STAFF_ID + ") modified.", actionLabel.getText());


    }


    /**
     * Verifies that the unsaved indicator (an asterisk in the title of the main
     * window) appears when changes to the clinician are made and saved.
     */
    @Test
    public void verifyUnsavedIndicatorAppearsAfterChanges() {

        clickOn(editButton);
        lastNameText.setText("");
        clickOn(lastNameText).write("Nedbertson");
        clickOn(doneButton);

        String title = App.getWindow().getTitle();
        Assert.assertEquals("SapioCulture*", title);

    }


    /**
     * Verifies that the unsaved indicator (an asterisk in the title of the main
     * window) does not appear after the save button is pressed when no changes
     * have occurred.
     */
    @Test
    public void verifyUnsavedIndicatorDoesNotAppearAfterNoChanges() {

        clickOn(editButton);
        clickOn(cancelButton);

        String title = App.getWindow().getTitle();
        Assert.assertEquals("SapioCulture", title);

    }


    /**
     * Verifies that the unsaved indicator (an asterisk in the title of the main
     * window) does not appear when the back button is pressed. This should
     * abort any changes made in the pane.
     */
    @Test
    public void verifyUnsavedIndicatorDoesNotAppearAfterCancel() {

        clickOn(editButton);
        clickOn(lastNameText).write("Nedbertson");
        clickOn(cancelButton);

        String title = App.getWindow().getTitle();
        Assert.assertEquals("SapioCulture", title);

    }


}
