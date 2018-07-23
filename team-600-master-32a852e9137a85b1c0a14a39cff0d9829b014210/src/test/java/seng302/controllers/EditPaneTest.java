package seng302.controllers;

import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.*;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.api.FxToolkit;
import org.testfx.util.WaitForAsyncUtils;
import seng302.model.person.DonorReceiver;
import seng302.model.AccountManager;
import seng302.App;
import seng302.model.GitlabGUITestSetup;


import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EditPaneTest extends ApplicationTest {

    private App mainGUI;
    private AccountManager accountManager;
    private DonorReceiver donorReceiver;

    private TextField usernameTextField;
    private Label actionLabel; // From the status bar.

    private Button loginButton;
    private TextField passwordField;
    private Button Logout;
    private Button createUserButton;

    private Button Edit;

    private Button doneButton;
    private Button cancelButton;

    //===================================================
    private Text DonorLoggedIn;

    //Basic information

    private TextField editGivenNames;
    private TextField editLastName;
    private TextField editNHINumber;
    private DatePicker editDateOfBirth;
    private DatePicker editDateOfDeath;
    private ChoiceBox editGender;
    private ChoiceBox editTitle;
    private TextField editHeight;
    private TextField editWeight;
    private ChoiceBox editBloodType;
    private CheckBox editLivedInUKFrance;
    private TextField preferredNameField;
    private ChoiceBox birthGenderChoiceBox;

    //Contact details

    private TextField editStreetAddress;
    private TextField editCity;
    private TextField editRegion;
    private TextField editPostcode;
    private TextField editMobileNumber;
    private TextField editHomeNumber;
    private TextField editEmail;
    private TextField editEmergStreetAddress;
    private TextField editEmergCity;
    private TextField editEmergRegion;
    private TextField editEmergPostcode;
    private TextField editEmergMobileNumber;
    private TextField editEmergHomeNumber;
    private TextField editEmergEmail;

    //Organs

    private CheckBox editLiver;
    private CheckBox editKidney;
    private CheckBox editLung;
    private CheckBox editHeart;
    private CheckBox editPancreas;
    private CheckBox editIntestine;
    private CheckBox editCornea;
    private CheckBox editMiddleEar;
    private CheckBox editBone;
    private CheckBox editBoneMarrow;
    private CheckBox editSkin;
    private CheckBox editConnectiveTissue;

    //Medications

    private Button moveToCurrent;
    private Button moveToPrevious;
    private Button addMedication;
    private Button editMedication;
    private Button removeMedication;
    private TextField createNewMedication;

    //Medical History
    private CheckBox editSmoker;
    private TextField editAlcoholConsumption;
    private TextField editBloodPressure;
    private TextField editChronicDiseases;


    /**
     * These settings are so test can be run in CI-runner, if you want to watch the robot perform the
     * tests on your local machine, comment this function out.
     */
    @BeforeClass
    public static void headless() {
        GitlabGUITestSetup.headless();
    }


    @Override
    public void start (Stage stage) throws Exception {
        mainGUI = new App();
        mainGUI.start(stage);
    }


    /**
     * Initialize links to the FXML
     */
    @Before
    public void setUp() {
        accountManager = App.getDatabase();
        usernameTextField = lookup("#usernameTextField").query();
        passwordField = lookup("#passwordField").query();
        createUserButton = lookup("#createDonorButton").query();
        loginButton = lookup("#loginButton").query();
    }


    /**
     * Sets up the buttons in the test with the expected values
     * (won't cause a null pointer if the element is not on the page)
     */
    private void setEditPane() {
        editGivenNames = lookup("#editGivenNames").query();
        editLastName = lookup("#editLastName").query();
        preferredNameField = lookup("#preferredName").query();
        editNHINumber = lookup("#editNHINumber").query();
        editDateOfBirth = lookup("#editDateOfBirth").query();
        editDateOfDeath = lookup("#editDateOfDeath").query();
        editGender = lookup("#editGender").query();
        birthGenderChoiceBox = lookup("#birthGender").query();
        editTitle = lookup("#editTitle").query();
        editHeight = lookup("#editHeight").query();
        editWeight = lookup("#editWeight").query();
        editBloodType = lookup("#editBloodType").query();
        editLivedInUKFrance = lookup("#editLivedInUKFrance").query();

        editStreetAddress = lookup("#editStreetAddress").query();
        editCity = lookup("#editCity").query();
        editRegion = lookup("#editRegion").query();
        editPostcode = lookup("#editPostcode").query();
        editMobileNumber = lookup("#editMobileNumber").query();
        editHomeNumber = lookup("#editHomeNumber").query();
        editEmail = lookup("#editEmail").query();
        editEmergStreetAddress = lookup("#editEmergStreetAddress").query();
        editEmergCity = lookup("#editEmergCity").query();
        editEmergRegion = lookup("#editEmergRegion").query();
        editEmergPostcode = lookup("#editEmergPostcode").query();
        editEmergMobileNumber = lookup("#editEmergMobileNumber").query();
        editEmergHomeNumber = lookup("#editEmergHomeNumber").query();
        editEmergEmail = lookup("#editEmergEmail").query();

        editLiver = lookup("#editLiver").query();
        editKidney = lookup("#editKidney").query();
        editLung = lookup("#editLung").query();
        editHeart = lookup("#editHeart").query();
        editPancreas = lookup("#editPancreas").query();
        editIntestine = lookup("#editIntestine").query();
        editCornea = lookup("#editCornea").query();
        editMiddleEar = lookup("#editMiddleEar").query();
        editBone = lookup("#editBone").query();
        editBoneMarrow = lookup("#editBoneMarrow").query();
        editSkin = lookup("#editSkin").query();
        editConnectiveTissue = lookup("#editConnectiveTissue").query();

        editSmoker = lookup("#editSmoker").query();
        editAlcoholConsumption = lookup("#editAlcoholConsumption").query();
        editBloodPressure = lookup("#editBloodPressure").query();
        editChronicDiseases = lookup("#editChronicDiseases").query();

        doneButton = lookup("#Done").query();
        cancelButton = lookup("#Cancel").query();
    }


    /**
     * Sets the fields of the edit controller to test the editing of the donor
     * (will cause a null pointer exception if the element isn't on the page)
     */
    private void setFields(){
        editGivenNames.setText("Steve Paul");
        editLastName.setText("Jobs");
        preferredNameField.setText("Stephanie");
        editNHINumber.setText("ASD9876");
        clickOn(birthGenderChoiceBox).clickOn("Male");
        WaitForAsyncUtils.waitForFxEvents();  // NOTE: Added to give time for the dropdown to initialize
        sleep(2, SECONDS);
        clickOn(editGender).clickOn("Female");
        clickOn(editTitle).clickOn("Mrs");
        editHeight.setText("1.82");
        editWeight.setText("70");
        clickOn(editBloodType).clickOn("O+");
        editLivedInUKFrance.setSelected(true);

        editStreetAddress.setText("23 Apple Lane");
        editCity.setText("San Francisco");
        editRegion.setText("California");
        editPostcode.setText("8456");
        editMobileNumber.setText("0278383838");
        editHomeNumber.setText("0800838383");
        editEmail.setText("steve.jobs@windows.com");
        editEmergStreetAddress.setText("23 Window Lane");
        editEmergCity.setText("New York");
        editEmergRegion.setText("New York");
        editEmergPostcode.setText("6854");
        editEmergMobileNumber.setText("0228383838");
        editEmergHomeNumber.setText("0800838383");
        editEmergEmail.setText("Bill.Gates@apple.com");

        editLiver.setSelected(false);
        editKidney.setSelected(true);
        editHeart.setSelected(false);
        editLung.setSelected(true);
        editIntestine.setSelected(true);
        editCornea.setSelected(false);
        editMiddleEar.setSelected(true);
        editSkin.setSelected(false);
        editBone.setSelected(true);
        editBoneMarrow.setSelected(false);
        editConnectiveTissue.setSelected(false);
        editPancreas.setSelected(false);

        /* Cannot edit receiver organs unless you're a clinician
         editReceiverLiver.setSelected(true);
         editReceiverKidney.setSelected(true);
         editReceiverHeart.setSelected(false);
         editReceiverLung.setSelected(false);
         editReceiverIntestine.setSelected(false);
         editReceiverCornea.setSelected(false);
         editReceiverMiddleEar.setSelected(false);
         editReceiverSkin.setSelected(false);
         editReceiverBone.setSelected(false);
         editReceiverBoneMarrow.setSelected(false);
         editReceiverConnectiveTissue.setSelected(false);
         editReceiverPancreas.setSelected(false);
         */

        editSmoker.setSelected(true);
        editAlcoholConsumption.setText("3.4");
        editBloodPressure.setText("2.0/3.5");
        editChronicDiseases.setText("Cancer");
    }


    private void getAccountSave(ArrayList<DonorReceiver> donorReceivers) {
        for(int i = 0; i< donorReceivers.size(); i++) {
            DonorReceiver donorReceiver2 = donorReceivers.get(i);
            if (donorReceiver2.getUserName().equals("ASD9876")) {
                donorReceiver = donorReceiver2;
                System.out.println(donorReceiver.getFirstName());
                break;
            }
        }
    }


    private void getAccountClose(ArrayList<DonorReceiver> donorReceivers) {
        for(int i = 0; i< donorReceivers.size(); i++) {
            DonorReceiver donorReceiver2 = donorReceivers.get(i);
            if (donorReceiver2.getUserName().equals("ABC1234")) {
                donorReceiver = donorReceiver2;
                break;
            }
        }
    }


    /**
     * Tests the expected behaviour of the done button
     * The user makes changes, presses done, and the changes are applied to the donor
     * (seen through changes in the view pane of the donor).
     */
    @Test
    @Ignore //Ignored as it causes other tests to fail - when the NHI number is changed it changes the NHI number of
            // the user so the original donor (the ones the other tests are using) does not exist changing this
            // in this test would require changing a number of fields in the test.
    public void editProfileClickDone() {
        clickOn(usernameTextField).write("ABC1234");
        clickOn(passwordField).write("password");
        clickOn(loginButton);
        Edit = lookup("#Edit").query();
        clickOn(Edit);
        setEditPane();
        setFields();
        clickOn(doneButton);

        ArrayList<DonorReceiver> list = accountManager.getDonorReceivers();
        getAccountSave(list);
        assertEquals("Steve", donorReceiver.getFirstName());
        assertEquals("Paul", donorReceiver.getMiddleName());
        assertEquals("Jobs", donorReceiver.getLastName());
        assertEquals("Stephanie", donorReceiver.getPreferredName());
        assertEquals("ASD9876", donorReceiver.getUserName());
        //testdateof birth
        //testDateofDeath
        assertEquals('F', donorReceiver.getGender());
        assertEquals('M', donorReceiver.getBirthGender());
        assertEquals("MRS", donorReceiver.getTitle());
        double weight = donorReceiver.getUserAttributeCollection().getWeight();
        String weight2 = Double.toString(weight);
        double height = donorReceiver.getUserAttributeCollection().getHeight();
        String height2 = Double.toString(height);
        assertEquals("1.82", height2 );
        assertEquals("70.0", weight2);
        assertEquals("O+", donorReceiver.getUserAttributeCollection().getBloodType());
        assertEquals(true, donorReceiver.getLivedInUKFlag());

        //TODO
       /* assertEquals("23 Apple Lane", donorReceiver.getContactDetails().getAddressStreet());
        assertEquals("San Francisco", donorReceiver.getContactDetails().getAddressCity());
        assertEquals("California", donorReceiver.getContactDetails().getAddressRegion());
        assertEquals("8456", donorReceiver.getContactDetails().getAddressPostcode());
        assertEquals("0278383838", donorReceiver.getContactDetails().getMobileNumber());
        assertEquals("0800838383", donorReceiver.getContactDetails().getHomeNumber());
        assertEquals("steve.jobs@windows.com", donorReceiver.getContactDetails().getEmail());
        assertEquals("23 Window Lane", donorReceiver.getContactDetails().getEmergAddressStreet());
        assertEquals("New York", donorReceiver.getContactDetails().getEmergAddressCity());
        assertEquals("New York", donorReceiver.getContactDetails().getEmergAddressRegion());
        assertEquals("6854", donorReceiver.getContactDetails().getEmergAddressPostcode());
        assertEquals("0228383838", donorReceiver.getContactDetails().getEmergMobileNumber());
        assertEquals("0800838383", donorReceiver.getContactDetails().getEmergHomeNumber());
        assertEquals("Bill.Gates@apple.com", donorReceiver.getContactDetails().getEmergEmail());*/

        assertEquals(false, donorReceiver.getDonorOrganInventory().getLiver());
        assertEquals(true, donorReceiver.getDonorOrganInventory().getKidneys());
        assertEquals(false, donorReceiver.getDonorOrganInventory().getHeart());
        assertEquals(true, donorReceiver.getDonorOrganInventory().getLungs());
        assertEquals(true, donorReceiver.getDonorOrganInventory().getIntestine());
        assertEquals(false, donorReceiver.getDonorOrganInventory().getCorneas());
        assertEquals(true, donorReceiver.getDonorOrganInventory().getMiddleEars());
        assertEquals(false, donorReceiver.getDonorOrganInventory().getSkin());
        assertEquals(true, donorReceiver.getDonorOrganInventory().getBone());
        assertEquals(false, donorReceiver.getDonorOrganInventory().getBoneMarrow());
        assertEquals(false, donorReceiver.getDonorOrganInventory().getConnectiveTissue());
        assertEquals(false, donorReceiver.getDonorOrganInventory().getPancreas());

        /* Can't edit receiver organs so this can't be tested by a script
         assertEquals(true, donorReceiver.getRequiredOrgans().getLiver());
         assertEquals(false, donorReceiver.getRequiredOrgans().getKidneys());
         assertEquals(false, donorReceiver.getRequiredOrgans().getHeart());
         assertEquals(false, donorReceiver.getRequiredOrgans().getLungs());
         assertEquals(false, donorReceiver.getRequiredOrgans().getIntestine());
         assertEquals(false, donorReceiver.getRequiredOrgans().getCorneas());
         assertEquals(false, donorReceiver.getRequiredOrgans().getMiddleEars());
         assertEquals(false, donorReceiver.getRequiredOrgans().getSkin());
         assertEquals(false, donorReceiver.getRequiredOrgans().getBone());
         assertEquals(false, donorReceiver.getRequiredOrgans().getBoneMarrow());
         assertEquals(false, donorReceiver.getRequiredOrgans().getConnectiveTissue());
         assertEquals(false, donorReceiver.getRequiredOrgans().getPancreas());
         */

        assertEquals(true, donorReceiver.getUserAttributeCollection().getSmoker());
        double alcohol = donorReceiver.getUserAttributeCollection().getAlcoholConsumption();
        String alcohol2 = Double.toString(alcohol);
        assertEquals("3.4", alcohol2);
        assertEquals("2.0/3.5", donorReceiver.getUserAttributeCollection().getBloodPressure());
        assertEquals("Cancer", donorReceiver.getUserAttributeCollection().getChronicDiseases());
    }


    /**
     * Checks the expected behaviour of the edit pane when the cancel button is pressed
     * (validated by no changes occurring in the view pane for the controller).
     */
    @Test
    @Ignore
    public void editProfileClickCancel() {
        clickOn(usernameTextField).write("ABC1234");
        clickOn(passwordField).write("password");
        clickOn(loginButton);

        for (DonorReceiver donorReceiver : accountManager.getDonorReceivers()) {
            System.out.println(donorReceiver.getFirstName());
            System.out.println(donorReceiver.getUserName());
        }

        Edit = lookup("#Edit").query();
        clickOn(Edit);
        setEditPane();
        setFields();
        clickOn(cancelButton);

        accountManager = App.getDatabase();
        ArrayList<DonorReceiver> list = accountManager.getDonorReceivers();
        getAccountClose(list);
        assertEquals("Sweeny", donorReceiver.getFirstName());
        assertEquals("", donorReceiver.getMiddleName());
        assertEquals("Todd", donorReceiver.getLastName());
        assertEquals("ABC1234", donorReceiver.getUserName());

        //testdateof birth
        //testDateofDeath

        assertEquals('M', donorReceiver.getGender());
        assertEquals("MR", donorReceiver.getTitle());

        double weight = donorReceiver.getUserAttributeCollection().getWeight();
        String weight2 = Double.toString(weight);
        double height = donorReceiver.getUserAttributeCollection().getHeight();
        String height2 = Double.toString(height);

        assertEquals("2.0", height2);
        assertEquals("51.0",weight2);
        assertEquals("AB-", donorReceiver.getUserAttributeCollection().getBloodType());
        assertEquals(false, donorReceiver.getLivedInUKFlag());

        //TODO
/*        assertEquals("1 Fleet Street", donorReceiver.getContactDetails().getAddressStreet());
        assertEquals("Christchurch", donorReceiver.getContactDetails().getAddressCity());
        assertEquals("Canterbury", donorReceiver.getContactDetails().getAddressRegion());
        assertEquals("5678", donorReceiver.getContactDetails().getAddressPostcode());
        assertEquals("0220456543", donorReceiver.getContactDetails().getMobileNumber());
        assertEquals("5452345", donorReceiver.getContactDetails().getHomeNumber());
        assertEquals("randomPerson92@gmail.com", donorReceiver.getContactDetails().getEmail());
        assertEquals("31b Taylors Ave", donorReceiver.getContactDetails().getEmergAddressStreet());
        assertEquals("Christchurch", donorReceiver.getContactDetails().getEmergAddressCity());
        assertEquals("Canterbury", donorReceiver.getContactDetails().getEmergAddressRegion());
        assertEquals("8052", donorReceiver.getContactDetails().getEmergAddressPostcode());
        assertEquals("0213459876", donorReceiver.getContactDetails().getEmergMobileNumber());
        assertEquals("5458769", donorReceiver.getContactDetails().getEmergHomeNumber());
        assertEquals("randomHelper93@yahoo.com", donorReceiver.getContactDetails().getEmergEmail());*/

        assertEquals("1 Fleet Street", donorReceiver.getContactDetails().getAddress().getStreetAddressLn1());
        assertEquals("", donorReceiver.getContactDetails().getAddress().getStreetAddressLn2());
        assertEquals("Christchurch", donorReceiver.getContactDetails().getAddress().getCityName());
        assertEquals("Canterbury", donorReceiver.getContactDetails().getAddress().getRegion());
        assertEquals("5678", donorReceiver.getContactDetails().getAddress().getPostCode());
        assertEquals("0220456543", donorReceiver.getContactDetails().getMobileNum());
        assertEquals("5452345", donorReceiver.getContactDetails().getHomeNum());
        assertEquals("randomPerson92@gmail.com", donorReceiver.getContactDetails().getEmail());



        assertEquals(true, donorReceiver.getDonorOrganInventory().getLiver());
        assertEquals(false, donorReceiver.getDonorOrganInventory().getKidneys());
        assertEquals(true, donorReceiver.getDonorOrganInventory().getHeart());
        assertEquals(false, donorReceiver.getDonorOrganInventory().getLungs());
        assertEquals(false, donorReceiver.getDonorOrganInventory().getIntestine());
        assertEquals(true, donorReceiver.getDonorOrganInventory().getCorneas());
        assertEquals(false, donorReceiver.getDonorOrganInventory().getMiddleEars());
        assertEquals(true, donorReceiver.getDonorOrganInventory().getSkin());
        assertEquals(false, donorReceiver.getDonorOrganInventory().getBone());
        assertEquals(true, donorReceiver.getDonorOrganInventory().getBoneMarrow());
        assertEquals(true, donorReceiver.getDonorOrganInventory().getConnectiveTissue());
        assertEquals(true, donorReceiver.getDonorOrganInventory().getPancreas());

        assertEquals(false, donorReceiver.getRequiredOrgans().getLiver());
        assertEquals(false, donorReceiver.getRequiredOrgans().getKidneys());
        assertEquals(false, donorReceiver.getRequiredOrgans().getHeart());
        assertEquals(false, donorReceiver.getRequiredOrgans().getLungs());
        assertEquals(false, donorReceiver.getRequiredOrgans().getIntestine());
        assertEquals(false, donorReceiver.getRequiredOrgans().getCorneas());
        assertEquals(false, donorReceiver.getRequiredOrgans().getMiddleEars());
        assertEquals(false, donorReceiver.getRequiredOrgans().getSkin());
        assertEquals(false, donorReceiver.getRequiredOrgans().getBone());
        assertEquals(false, donorReceiver.getRequiredOrgans().getBoneMarrow());
        assertEquals(false, donorReceiver.getRequiredOrgans().getConnectiveTissue());
        assertEquals(false, donorReceiver.getRequiredOrgans().getPancreas());

        assertEquals(false, donorReceiver.getUserAttributeCollection().getSmoker());
        double alcohol = donorReceiver.getUserAttributeCollection().getAlcoholConsumption();
        String alcohol2 = Double.toString(alcohol);
        assertEquals("2.0", alcohol2);
        assertEquals("10/10", donorReceiver.getUserAttributeCollection().getBloodPressure());
        assertEquals("None", donorReceiver.getUserAttributeCollection().getChronicDiseases());
    }



    @Ignore
    @Test
    public void editOrgansClickSave() {
        // Ignored due to not being able to open a clinician donorReceiver into editing the receiving organs of
        clickOn(usernameTextField).write("ABC1234");
        clickOn(passwordField).write("password");
        clickOn(loginButton);
        Edit = lookup("#Edit").query();
        clickOn(Edit);
        setEditPane();

        System.out.println("Debugger");
    }


    /**
     * Verifies that the status bar displays a message notifying the user of an
     * account modification after changes are made to donor ABC1234.
     */
    @Test
    public void verifyStatusBarIsUpdated() {

        toggleLivedInUKFrance();
        clickOn(doneButton);

        actionLabel = lookup("#actionLabel").query();
        assertEquals("Sweeny Todd (NHI: ABC1234) modified.", actionLabel.getText());

    }


    /**
     * Verifies that the status indicator (an asterisk in the title of the main
     * window) appears after changes are made to donor ABC1234.
     */
    @Test
    public void verifyUnsavedIndicatorAppearsAfterChanges() {

        toggleLivedInUKFrance();
        clickOn(doneButton);

        String title = App.getWindow().getTitle();
        assertEquals("SapioCulture*", title);

    }


    /**
     * Verifies that the unsaved indicator (an asterisk in the title of the main
     * window) does not appear when no changes are made to donor.
     */
    @Test
    public void verifyUnsavedIndicatorDoesNotAppearAfterNoChanges() {

        clickOn(usernameTextField).write("ABC1234");
        clickOn(passwordField).write("password");
        clickOn(loginButton);

        Edit = lookup("#Edit").query();
        clickOn(Edit);
        setEditPane();

        clickOn(doneButton);

        String title = App.getWindow().getTitle();
        assertEquals("SapioCulture", title);

    }


    /**
     * Verifies that the unsaved indicator (an asterisk in the title of the main
     * window) does not appear when changes made to the donor are cancelled.
     */
    @Test
    public void verifyUnsavedIndicatorDoesNotAppearAfterCancel() {

        toggleLivedInUKFrance();
        clickOn(cancelButton);

        String title = App.getWindow().getTitle();
        assertEquals("SapioCulture", title);

    }


    /**
     * Logs in using donor profile ABC1234, switches to the edit pane, toggles
     * the boolean variable editLivedInUKFrance, and clicks the done button.
     * This is a helper method for tests that examine post-change actions.
     */
    private void toggleLivedInUKFrance() {

        // Login.
        clickOn(usernameTextField).write("ABC1234");
        clickOn(passwordField).write("password");
        clickOn(loginButton);

        // Find and open edit pane.
        Edit = lookup("#Edit").query();
        clickOn(Edit);
        setEditPane();

        // Change smoker and save.
        clickOn(editLivedInUKFrance);

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
        // mainGUI.stop();
    }
}