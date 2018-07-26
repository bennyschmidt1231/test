package seng302.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.hasText;

import java.util.concurrent.TimeoutException;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import seng302.App;
import seng302.model.AccountManager;
import seng302.model.GitlabGUITestSetup;

@Ignore //Being fixed in another branch
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

  private final String STAFF_ID = "3";

  private Label dateLabel;
  private Label firstNameLabel;
  private Label lastNameLabel;
  private Label workAddressLabel;
  private Label regionLabel;
  private Button editButton;
  private Button cancelButton;
  private Button backButton;


  /**
   * Sets the GUI stage
   */
  @Override
  public void start(Stage stage) throws Exception {
    mainGUI = new App();
    mainGUI.start(stage);
    LoginController.accountManager.importClinicians();
    LoginController.accountManager.addClinicianIfNoneExists();
  }


  /**
   * Initialize links to the FXML of the Login Page and then navigates to the main menu and from
   * there to the view/edit clinician window
   */
  @Before
  public void setUp() {
    resetAccount();
    clickOn("#clinicianButton");
    usernameTextField = lookup("#usernameTextField").query();
    passwordField = lookup("#passwordField").query();
    loginButton = lookup("#loginButton").query();
    clickOn(usernameTextField).write(STAFF_ID);
    clickOn(passwordField).write("password");
    clickOn(loginButton);
    sleep(300);
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
   *
   * @throws TimeoutException If there is a timeout
   */
  @After
  public void tearDown() throws Exception {
    FxToolkit.hideStage();
    release(new KeyCode[]{});
    release(new MouseButton[]{});
    mainGUI.stop();
  }


  /**
   * Resets the given name, last name, work address, and region of the clinician with staff ID
   * 'STAFF_ID'. This is called to prevent tests from being affected by changes made during previous
   * tests.
   */
  private void resetAccount() {

    AccountManager am = mainGUI.getDatabase();

    am.getClinicians().get(STAFF_ID).setFirstName("Suiko");
    am.getClinicians().get(STAFF_ID).setLastName("Yagami");
    am.getClinicians().get(STAFF_ID).updateWorkAddress("Kanto region, Japan");
    am.getClinicians().get(STAFF_ID).updateRegion("Canterbury");

  }


  /**
   * Initializes the values of the view/edit clinician GUI element hooks for the robot to interact
   * with.
   */
  private void setClinicianValues() {
    clinicianTitleLabel = lookup("#clinicianTitleLabel").query();
    doneButton = lookup("#doneButton").query();
    clinicianProfileNameText = lookup("#clinicianProfileNameText").query();
    lastNameText = lookup("#lastNameText").query();
    workAddressText = lookup("#workAddressText").query();
    regionComboBox = lookup("#regionComboBox ").query();
    firstNameLabel = lookup("#firstNameLabel").query();
    lastNameLabel = lookup("#lastNameLabel").query();
    workAddressLabel = lookup("#workAddressLabel").query();
    regionLabel = lookup("#regionLabel").query();
    editButton = lookup("#editButton").query();
    cancelButton = lookup("#cancelButton").query();
    backButton = lookup("#backButton").query();
  }


  /**
   * A test to check that we have navigated to the correct window with the correct clinician once we
   * have started up. Also checks that the viewing elements are visible and the editing elements are
   * hidden.
   */
  @Test
  public void verifyViewEditClinicianWindowIsOpen() {
    verifyThat(clinicianTitleLabel, hasText("View/Edit Clinician"));
    Assert.assertEquals("Suiko", firstNameLabel.getText());
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


  /**
   * Tests the view/edit clinician GUI page by having the robot click the edit button then input
   * invalid values into the text fields and checking if an alert dialog box is created.
   */
  @Test
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
    clinicianProfileNameText.setText("Light");
    lastNameText.setText("123");
    verifyBadUserInputAlertIsShown();
    lastNameText.setText("Yagami");
    workAddressText.setText(
        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    verifyBadUserInputAlertIsShown();
    workAddressText.setText("Kanto region, Japan");
  }


  /**
   * Checks if that changes made to the clinician are saved by checking if a 'success' alert box
   * appears when changes are made to the view/edit clinician window text fields and that these
   * changes can be seen when reloading the page.
   */
  @Test
  public void verifyChangesToClinicianAreSaved() {
    clickOn(editButton);
    clinicianProfileNameText = lookup("#clinicianProfileNameText").query();
    clinicianProfileNameText.setText("Kira");
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
    clinicianProfileNameText.setText("Light");
    clickOn(doneButton);
    verifyThat(firstNameLabel, hasText("Light"));
  }


  /**
   * Attempts to find the alert dialogue pop up in the application GUI thats generated when bad user
   * input is entered and checks if the the header of that alert box matches "Bad user Input".
   */
  private void verifyBadUserInputAlertIsShown() {
    clickOn(doneButton);
    // We try to find the Alert Dialogue box and check its header matches the input error type we were expecting
    try {
      DialogPane alertDialogue = lookup("#BadUserInput").query();
      // GUI elements of alert dialogue boxes
      Button alertButton = lookup("#badAlertOkButton").query();
      // If the alert box is the one we expect, then its header message will be "Bad user Input".
      assertEquals("Bad user Input", alertDialogue.getHeaderText());
      clickOn(alertButton);
      // We could not find the Alert Dialogue box
    } catch (NullPointerException e) {
      fail("Could not find bad user input alert box");
    }
  }


  /**
   * Attempts to save a change made to the clinician and then verifies that a corresponding message
   * is shown in the status bar.
   */
  @Test
  public void verifyStatusBarIsUpdated() {
    // Retrieve existing data.
    setClinicianValues();
    // Edit first name and save clinician profile.
    clickOn(editButton);
    clinicianProfileNameText.setText("Ned");
    clickOn(doneButton);

    actionLabel = lookup("#actionLabel").query();
    Assert.assertEquals("Suiko Yagami (Clinician, Staff ID: " + STAFF_ID + ") modified.",
        actionLabel.getText());


  }


  /**
   * Verifies that the unsaved indicator (an asterisk in the title of the main window) appears when
   * changes to the clinician are made and saved.
   */
  @Test
  public void verifyUnsavedIndicatorAppearsAfterChanges() {
    clickOn(editButton);
    lastNameText.setText("Nedbertson");
    clickOn(doneButton);
    String title = App.getWindow().getTitle();
    Assert.assertEquals("SapioCulture*", title);
  }


  /**
   * Verifies that the unsaved indicator (an asterisk in the title of the main window) does not
   * appear after the save button is pressed when no changes have occurred.
   */
  @Test
  public void verifyUnsavedIndicatorDoesNotAppearAfterNoChanges() {
    clickOn(editButton);
    clickOn(cancelButton);
    String title = App.getWindow().getTitle();
    Assert.assertEquals("SapioCulture", title);
  }


  /**
   * Verifies that the unsaved indicator (an asterisk in the title of the main window) does not
   * appear when the back button is pressed. This should abort any changes made in the pane.
   */
  @Test
  public void verifyUnsavedIndicatorDoesNotAppearAfterCancel() {
    clickOn(editButton);
    lastNameText.setText("Nedbertson");
    clickOn(cancelButton);
    String title = App.getWindow().getTitle();
    Assert.assertEquals("SapioCulture", title);
  }
}
