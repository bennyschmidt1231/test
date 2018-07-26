package seng302.model;


import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import seng302.App;
import seng302.controllers.childWindows.ChildWindowManager;
import seng302.model.person.Administrator;
import seng302.model.person.Clinician;

/**
 * This class contains a series of JUnit tests for the CLIClinicianCommandHandler class.
 */
public class CLIClinicianCommandHandlerTest {


  // Class variables.
  private CLICommandHandler handler;


  @Before
  public void setUp() {

    handler = new CLICommandHandler();

    // Sets up the modifyingUser otherwise it causes errors
    Administrator testingAdmin = new Administrator("Testy", "", "McTestFace", "tester", "password");
    App.childWindowManager = ChildWindowManager.getChildWindowManager();
    AccountManager.setCurrentUser(testingAdmin);

  }


  /**
   * Tests the createClinician method with a String corresponding to a valid set of parameters. A
   * new clinician object should be added to the list with the specified values.
   */
  @Test
  public void createClinicianTestWithValidClinician() {

    String command = "create staff givenname=John Edward staffid=1029 lastname=Smith password=password workaddress=43 West Street region=South Canterbury";
    ArrayList<String> result = handler.commandControl(command);

    ArrayList<String> modelResult = new ArrayList<String>();
    modelResult.add(
        "Clinician created with name 'John Edward Smith', work address '43 West Street, South Canterbury', and staff ID '1029'.");

    Assert.assertEquals(modelResult, result);
    verifyClinicianValues("1029", "John Edward", "Smith", "password", "43 West Street",
        "South Canterbury");

  }


  /**
   * Tests the createClinician method with illegal parameters which cannot be processed. An error
   * message should be returned describing the problem. No clinician object should be added.
   */
  @Test
  public void createClinicianTestWithIllegalParameters() {

    String command = "create staff givenname=John Edward staffid=1029 lastname=Smith password=password workaddress=43 West Street region=South Canterbury invalidparameter=null";
    ArrayList<String> result = handler.commandControl(command);

    ArrayList<String> modelResult = new ArrayList<String>();
    modelResult.add("'invalidparameter' is not a valid parameter for creating a clinician.");
    modelResult.add("The new clinician was not added.");

    Assert.assertEquals(modelResult, result);
    Assert.assertEquals(null, searchForClinician("1029"));

  }


  /**
   * Tests the createClinician method with no parameters. An error message should be returned for
   * each missing parameter, describing the problem. No clinician object should be added.
   */
  @Test
  public void createClinicianTestWithNoParameters() {

    String command = "create staff";
    ArrayList<String> result = handler.commandControl(command);

    ArrayList<String> modelResult = new ArrayList<String>();
    modelResult.add("You did not specify a given name for the clinician.");
    modelResult.add("You did not specify a last name for the clinician.");
    modelResult.add("You did not specifiy the staff ID of the clinician.");
    modelResult.add("You did not specify a password for the clinician.");
    modelResult.add("You did not specify the work address of the clinician.");
    modelResult.add("You did not specify the region in which the clinician lives.");
    modelResult.add("The new clinician was not added.");

    Assert.assertEquals(modelResult, result);

  }


  /**
   * Tests the createClinician method with a collection of invalid parameter values. An error
   * message should be returned for each invalid value. No clinician object should be added. Note,
   * this method is not designed to test the actual validation functionality defined elsewhere.
   */
  @Test
  public void createClinicianTestWithInvalidParameterValues() {

    String command = "create staff givenname=John Edward lastname=Smith staffid=id password=pass workaddress=#$@! region=Canterbury";
    ArrayList<String> result = handler.commandControl(command);

    ArrayList<String> modelResult = new ArrayList<String>();
    modelResult.add("The specified staff ID is invalid. It must be a whole number greater than 0.");
    modelResult.add(
        "The specified password is invalid. It must contain between 6 and 30 alphanumeric characters.");
    modelResult.add("The address you specified is invalid. It must be an alphanumeric sequence.");
    modelResult.add("The new clinician was not added.");

    Assert.assertEquals(modelResult, result);
    Assert.assertEquals(null, searchForClinician("id"));


  }


  /**
   * Tests the createClinician method with parameters defined but no parameter values. A list of
   * error messages should be returned. No clinician object should be added.
   */
  @Test
  public void createClinicianTestWithMissingParameterValues() {

    String command = "create staff givenname= lastname= staffid= password= workaddress= region=";
    ArrayList<String> result = handler.commandControl(command);

    ArrayList<String> modelResult = new ArrayList<String>();
    modelResult.add("You did not specify a given name for the clinician.");
    modelResult.add("You did not specify a last name for the clinician.");
    modelResult.add("You did not specifiy the staff ID of the clinician.");
    modelResult.add("You did not specify a password for the clinician.");
    modelResult.add("You did not specify the work address of the clinician.");
    modelResult.add("You did not specify the region in which the clinician lives.");
    modelResult.add("The new clinician was not added.");

    Assert.assertEquals(modelResult, result);
    Assert.assertEquals(null, searchForClinician(""));

  }

  /**
   * Tests the update clinician CLI method with an invalid given name
   */
  @Test
  public void updateClinicianWithInvalidGivenName() {
    Clinician clinician = searchForClinician("3");

    String command = "update staff 3 givenName !@#";
    ArrayList<String> result = handler.commandControl(command);
    String resultText = result.get(0);

    String modelResult = "ERROR: Invalid value";

    Assert.assertEquals(clinician.getFirstName(), "Light");
    Assert.assertTrue(resultText.contains(modelResult));
  }

  /**
   * Tests the update clinician CLI method with a missing value
   */
  @Test
  public void updateClinicianWithMissingValue() {
    Clinician clinician = searchForClinician("3");

    String command = "update staff 3 givenName";
    ArrayList<String> result = handler.commandControl(command);
    String resultText = result.get(0);

    String modelResult = "ERROR: Insufficient information provided";

    Assert.assertEquals(clinician.getFirstName(), "Light");
    Assert.assertTrue(resultText.contains(modelResult));
  }

  /**
   * Tests the update clinician CLI method with an invalid region]
   */
  @Test
  public void updateClinicianWithInvalidRegion() {
    String command = "update staff 3 region @!#";
    ArrayList<String> result = handler.commandControl(command);
    String resultText = result.get(0);

    String modelResult = "ERROR: Invalid value";

    Assert.assertTrue(resultText.contains(modelResult));
  }

  /**
   * Tests the update clinician CLI method with an invalid work address
   */
  @Test
  public void updateClinicianWithInvalidWorkAddress() {
    String command = "update staff 3 workAddress @!#";
    ArrayList<String> result = handler.commandControl(command);
    String resultText = result.get(0);

    String modelResult = "ERROR: Invalid value";

    Assert.assertTrue(resultText.contains(modelResult));
  }

  /**
   * Tests the update clinician CLI method with an invalid staff id
   */
  @Test
  public void updateClinicianWithInvalidId() {
    Clinician clinician = searchForClinician("3");

    String command = "update staff 3 id 123";
    ArrayList<String> result = handler.commandControl(command);
    String resultText = result.get(0);

    String modelString = "ERROR: There is already a clinician with that staff id";

    Assert.assertTrue(resultText.contains(modelString));

    command = "update staff 3 id ^&*(";
    result = handler.commandControl(command);
    resultText = result.get(0);

    modelString = "ERROR: Invalid";

    Assert.assertTrue(resultText.contains(modelString));

    command = "update staff 3 id 3";
    result = handler.commandControl(command);
    resultText = result.get(0);

    modelString = "ERROR: This account already has that staff id";

    Assert.assertTrue(resultText.contains(modelString));

    Assert.assertEquals("3", clinician.getUserName());
  }

  /**
   * Blue sky test Tests the update clinician CLI method with valid values
   */
  @Test
  public void updateClinicianWithValidValues() {

    Clinician clinician = searchForClinician("3");
    Assert.assertEquals(clinician.getFirstName(), "Light");

    String command = "update staff 3 givenName Bob";
    ArrayList<String> result = handler.commandControl(command);
    String resultText = result.get(0);

    String modelResult = "User Being Modified: Bob Yagami (Clinician, Staff ID: 3), Changed by User: Testy McTestFace";

    Assert.assertEquals(clinician.getFirstName(), "Bob");
    Assert.assertTrue(resultText.contains(modelResult));

    handler
        .commandControl("update staff 3 givenName Light"); // Reset the given name for other tests
  }

  /**
   * Tests the delete clinician CLI method  with an invalid staff id
   */
  @Test
  public void deleteClinicianWithInvalidStaffId() {
    Clinician clinician = searchForClinician("0");
    Assert.assertTrue(clinician.isActive());

    String command = "delete staff 456";
    ArrayList<String> result = handler.commandControl(command);
    String resultText = result.get(0);

    String modelResult = "ERROR: No clinician found";

    Assert.assertTrue(resultText.contains(modelResult));

    command = "delete staff 0";
    result = handler.commandControl(command);
    resultText = result.get(0);

    modelResult = "ERROR: Can't delete the default clinician";

    Assert.assertTrue(resultText.contains(modelResult));

    Assert.assertTrue(clinician.isActive());
  }

  /**
   * Blue sky test Tests the delete clinician CLI method with a valid staff id
   */
  @Test
  public void deleteClinicianWithValidStaffId() {
    Clinician clinician = searchForClinician("123");
    Assert.assertTrue(clinician.isActive());

    String command = "delete staff 123";
    ArrayList<String> result = handler.commandControl(command);
    String resultText = result.get(1);

    String modelResult = "User Deleted";

    Assert.assertTrue(resultText.contains(modelResult));
    Assert.assertFalse(clinician.isActive());
  }


  /**
   * Searches for a Clinician with the corresponding staff ID and returns it.
   *
   * @param staffID The staff ID of the clinician to be retrieved.
   * @return The Clinician with the given staff ID.
   */
  private Clinician searchForClinician(String staffID) {

    for (Clinician clinician : App.getDatabase().getCliniciansArrayList()) {

      if (clinician.getUserName().equals(staffID)) {

        return clinician;

      }

    }

    return null; // Returns null if clinician is not found.

  }


  /**
   * Takes a set of parameter values used to initialise a clinician object and verifies that a
   * clinicina exists with the given ID in the database with the given values.
   *
   * @param staffID The staff ID or username of the clinician as a String.
   * @param firstName The first name of the clinician as a String.
   * @param lastName The last name of the clinician as a String.
   * @param password The password of the clinician as a String.
   * @param workAddress The work address of the clinician as a String.
   * @param region The region
   */
  private void verifyClinicianValues(String staffID, String firstName, String lastName,
      String password, String workAddress, String region) {

    Clinician clinician = searchForClinician(staffID);
    Assert.assertEquals(staffID, clinician.getUserName());
    Assert.assertEquals(firstName, clinician.getFirstName());
    Assert.assertEquals(lastName, clinician.getLastName());
    Assert.assertEquals(password, clinician.getPassword());
    Assert.assertEquals(workAddress,
        clinician.getContactDetails().getAddress().getStreetAddressLn1());
    Assert.assertEquals(region, clinician.getContactDetails().getAddress().getRegion());

  }


}
