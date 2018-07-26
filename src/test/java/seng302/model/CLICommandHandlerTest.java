package seng302.model;

import java.util.ArrayList;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import seng302.App;
import seng302.controllers.childWindows.ChildWindowManager;
import seng302.model.person.Administrator;

public class CLICommandHandlerTest {

  private static CLICommandHandler commandHandler;

  @BeforeClass
  public static void setUp() {
    commandHandler = new CLICommandHandler();
    Administrator testingAdmin = new Administrator("Testy", "", "McTestFace", "tester", "password");
    App.childWindowManager = ChildWindowManager.getChildWindowManager();
    AccountManager.setCurrentUser(testingAdmin);
    String creationCommand = "create donor Name=Test Test dateofbirth=19971201 nhi=asd9871";
    commandHandler.commandControl(creationCommand);
    commandHandler
        .commandControl("create donor name=Updating Person dateofbirth=19971201 nhi=asd9872");
  }

  @Test
  public void invalidCommand() {
    String command = "invalid command here :)";
    ArrayList<String> messages = commandHandler.commandControl(command);
    assertEquals(1, messages.size());
    assertEquals("Invalid command. Please try again.", messages.get(0));
  }

  //-----------------------------------------------CREATION TESTS-----------------------------------
  @Test
  public void createDonorSuccess() {
    String creationCommand = "create donor Name=Steven Philip Jones dateofbirth=19971201 nhi=asd9876";
    ArrayList<String> messages = commandHandler.commandControl(creationCommand);
    assertEquals(1, messages.size());
    assertEquals(
        "Account created with name: Steven Philip Jones, Date of birth: 1997-12-01 and NHI: ASD9876",
        messages.get(0));
  }

  @Test
  public void createDonorNoName() {
    String creationCommand = "create donor dateofbirth=19971201 nhi=asd9876";
    ArrayList<String> messages = commandHandler.commandControl(creationCommand);
    assertEquals(1, messages.size());
    assertEquals(
        "In state create, but missing required fields. Correct template is name=<Name> dateOfBirth=<yyyyMMdd> nhi=<NHI number>.",
        messages.get(0));
  }

  @Test
  public void createDonorWrongOrder() {
    String creationCommand = "create donor dateofbirth=19971201 nhi=asd9876 name=Steven Philip Jones";
    ArrayList<String> messages = commandHandler.commandControl(creationCommand);
    assertEquals(1, messages.size());
    assertEquals(
        "Correct tokens provided, but not in the correct order. Correct order is name=<Name> dateOfBirth=<yyyyMMdd> nhi=<NHI number>.",
        messages.get(0));
  }

  @Test
  public void createDonorNonAlphaName() {
    String creationCommand = "create donor name=$teven Philip Jones dateofbirth=19971201 nhi=asd9876";
    ArrayList<String> messages = commandHandler.commandControl(creationCommand);
    assertEquals(1, messages.size());
    assertEquals("That name is invalid, as it it is not alphabetical. Please try again.",
        messages.get(0));
  }

  @Test
  public void createDonorEmptyName() {
    String creationCommand = "create donor name=dateofbirth=19971201 nhi=asd9876";
    ArrayList<String> messages = commandHandler.commandControl(creationCommand);
    assertEquals(1, messages.size());
    assertEquals("A name is required for the profile. Please try again.", messages.get(0));
  }

  @Test
  public void createDonorInvalidDate() {
    String creationCommand = "create donor name=Steven Philip Jones dateofbirth=1997 nhi=asd9876";
    ArrayList<String> messages = commandHandler.commandControl(creationCommand);
    assertEquals(1, messages.size());
    assertEquals("The format required for date is yyyyMMdd. Please try again.", messages.get(0));
  }

  @Test
  public void createDonorInvalidNHI() {
    String creationCommand = "create donor name=Steven Philip Jones dateofbirth=19971201 nhi=9876abd";
    ArrayList<String> messages = commandHandler.commandControl(creationCommand);
    assertEquals(1, messages.size());
    assertEquals(
        "Either you have entered an invalid NHI number or there is already a profile with that number. Please try again.",
        messages.get(0));
  }

  @Test
  public void createDonorUsedNHI() {
    String creationCommand = "create donor name=Steven Philip Jones dateofbirth=19971201 nhi=asd9871";
    ArrayList<String> messages = commandHandler.commandControl(creationCommand);
    assertEquals(1, messages.size());
    assertEquals(
        "Either you have entered an invalid NHI number or there is already a profile with that number. Please try again.",
        messages.get(0));
  }

  @Test
  public void createDonorSuccessOneName() {
    String creationCommand = "create donor Name=Steven dateofbirth=19971201 nhi=asd9875";
    ArrayList<String> messages = commandHandler.commandControl(creationCommand);
    assertEquals(1, messages.size());
    assertEquals("Account created with name: Steven, Date of birth: 1997-12-01 and NHI: ASD9875",
        messages.get(0));
  }

  @Test
  public void createDonorSuccessNoMiddleName() {
    String creationCommand = "create donor Name=Steven Jonesy dateofbirth=19971201 nhi=asd9874";
    ArrayList<String> messages = commandHandler.commandControl(creationCommand);
    assertEquals(1, messages.size());
    assertEquals(
        "Account created with name: Steven Jonesy, Date of birth: 1997-12-01 and NHI: ASD9874",
        messages.get(0));
  }

  //-----------------------------------------------VIEW BY NAME TESTS-------------------------------
  @Test
  public void viewByNameAll() {
    String viewCommand = "view donor Name Test Test all";
    ArrayList<String> messages = commandHandler.commandControl(viewCommand);
    assertEquals(3, messages.size());
    assertEquals("1 match(es) for the name Test Test.\n", messages.get(0));
    assertEquals("Accessing data for donor ASD9871...", messages.get(1));
    assertEquals("Donor Details:\n"
        + "\tName: Test Test\n"
        + "\tPreferred Name: \n"
        + "\tNational Health Index: ASD9871\n"
        + "\tDonorReceiver created at: \n"
        + "\tDate of birth: 1997-12-01\n"
        + "User Attributes:\n"
        + "\tBlood Pressure: 0/0\n"
        + "Organs to donate:\n"
        + "No organs to donate\n"
        + "\n"
        + "Personal Contact Details:\n"
        + "Address:\n"
        + "Street Address Line 1: \n"
        + "Street Address Line 2: \n"
        + "Suburb: \n"
        + "City: \n"
        + "Region: \n"
        + "Post Code: \n"
        + "Country Code: \n"
        + "Mobile Number: \n"
        + "Home Number: \n"
        + "Email: \n"
        + "Emergency Contact Details:\n"
        + "Address:\n"
        + "Street Address Line 1: \n"
        + "Street Address Line 2: \n"
        + "Suburb: \n"
        + "City: \n"
        + "Region: \n"
        + "Post Code: \n"
        + "Country Code: \n"
        + "Mobile Number: \n"
        + "Home Number: \n"
        + "Email: \n", messages.get(2).substring(0, 109) + messages.get(2)
        .substring(128, messages.get(2).length() - 1));
  }

  @Test
  public void viewByNameAttributes() {
    String viewCommand = "view donor Name Test Test attributes";
    ArrayList<String> messages = commandHandler.commandControl(viewCommand);
    assertEquals(3, messages.size());
    assertEquals("1 match(es) for the name Test Test.\n", messages.get(0));
    assertEquals("Accessing data for donor ASD9871...", messages.get(1));
    assertEquals("User Attributes:\n"
        + "\tBlood Pressure: 0/0\n", messages.get(2));
  }

  @Test
  public void viewByNameOrgans() {
    String viewCommand = "view donor Name Test Test organs";
    ArrayList<String> messages = commandHandler.commandControl(viewCommand);
    assertEquals(3, messages.size());
    assertEquals("1 match(es) for the name Test Test.\n", messages.get(0));
    assertEquals("Accessing data for donor ASD9871...", messages.get(1));
    assertEquals("Organs to donate:\n"
        + "No organs to donate\n"
        + "\n", messages.get(2));
  }

  @Test
  public void viewByNameContacts() {
    String viewCommand = "view donor Name Test Test contacts";
    ArrayList<String> messages = commandHandler.commandControl(viewCommand);
    assertEquals(3, messages.size());
    assertEquals("1 match(es) for the name Test Test.\n", messages.get(0));
    assertEquals("Accessing data for donor ASD9871...", messages.get(1));
    assertEquals("Address:\n"
        + "Street Address Line 1: \n"
        + "Street Address Line 2: \n"
        + "Suburb: \n"
        + "City: \n"
        + "Region: \n"
        + "Post Code: \n"
        + "Country Code: \n"
        + "Mobile Number: \n"
        + "Home Number: \n"
        + "Email: \n", messages.get(2));
  }

  @Test
  public void viewByNameLog() {
    String viewCommand = "view donor Name Test Test log";
    ArrayList<String> messages = commandHandler.commandControl(viewCommand);
    assertEquals(3, messages.size());
    assertEquals("1 match(es) for the name Test Test.\n", messages.get(0));
    assertEquals("Accessing data for donor ASD9871...", messages.get(1));
    assertEquals("Update log:\n"
            + "User Created: Test Test (NHI: ASD9871), Created by: Testy McTestFace (Administrator, username: tester), at ",
        messages.get(2).substring(0, 119));
  }

  @Test
  public void viewByNameInvalidOption() {
    String viewCommand = "view donor name Test Test help";
    ArrayList<String> messages = commandHandler.commandControl(viewCommand);
    assertEquals(3, messages.size());
    assertEquals("1 match(es) for the name Test Test.\n", messages.get(0));
    assertEquals("Accessing data for donor ASD9871...", messages.get(1));
    assertEquals(
        "ERROR: Unknown object help. Object should be 'all', 'attributes', 'log', 'organs' or 'contacts'.\n",
        messages.get(2));
  }

  @Test
  public void viewByNameNoName() {
    String viewCommand = "view donor name";
    ArrayList<String> messages = commandHandler.commandControl(viewCommand);
    assertEquals(1, messages.size());
    assertEquals("ERROR: Names and/or Object not given", messages.get(0));
  }

  @Test
  public void viewByNameNotInDatabase() {
    String viewCommand = "view donor name Fake Name all";
    ArrayList<String> messages = commandHandler.commandControl(viewCommand);
    assertEquals(1, messages.size());
    assertEquals("ERROR: Fake Name not found in database. Did you spell it right? " +
            "Try searching using the donor NHI code instead. Enter 'help' for more information\n",
        messages.get(0));
  }

  //-----------------------------------------------VIEW BY NHI TESTS--------------------------------

  @Test
  public void viewByNHIAll() {
    String viewCommand = "view donor nhi asd9871 all";
    ArrayList<String> messages = commandHandler.commandControl(viewCommand);
    assertEquals(2, messages.size());
    assertEquals("Accessing data for donor ASD9871...", messages.get(0));
    assertEquals("Donor Details:\n"
        + "\tName: Test Test\n"
        + "\tPreferred Name: \n"
        + "\tNational Health Index: ASD9871\n"
        + "\tDonorReceiver created at: \n"
        + "\tDate of birth: 1997-12-01\n"
        + "User Attributes:\n"
        + "\tBlood Pressure: 0/0\n"
        + "Organs to donate:\n"
        + "No organs to donate\n"
        + "\n"
        + "Personal Contact Details:\n"
        + "Address:\n"
        + "Street Address Line 1: \n"
        + "Street Address Line 2: \n"
        + "Suburb: \n"
        + "City: \n"
        + "Region: \n"
        + "Post Code: \n"
        + "Country Code: \n"
        + "Mobile Number: \n"
        + "Home Number: \n"
        + "Email: \n"
        + "Emergency Contact Details:\n"
        + "Address:\n"
        + "Street Address Line 1: \n"
        + "Street Address Line 2: \n"
        + "Suburb: \n"
        + "City: \n"
        + "Region: \n"
        + "Post Code: \n"
        + "Country Code: \n"
        + "Mobile Number: \n"
        + "Home Number: \n"
        + "Email: \n", messages.get(1).substring(0, 109) + messages.get(1)
        .substring(128, messages.get(1).length() - 1));
  }

  @Test
  public void viewByNHIAttributes() {
    String viewCommand = "view donor nhi asd9871 attributes";
    ArrayList<String> messages = commandHandler.commandControl(viewCommand);
    assertEquals(2, messages.size());
    assertEquals("Accessing data for donor ASD9871...", messages.get(0));
    assertEquals("User Attributes:\n"
        + "\tBlood Pressure: 0/0\n", messages.get(1));
  }

  @Test
  public void viewByNHIOrgans() {
    String viewCommand = "view donor nhi asd9871 organs";
    ArrayList<String> messages = commandHandler.commandControl(viewCommand);
    assertEquals(2, messages.size());
    assertEquals("Accessing data for donor ASD9871...", messages.get(0));
    assertEquals("Organs to donate:\n"
        + "No organs to donate\n"
        + "\n", messages.get(1));
  }

  @Test
  public void viewByNHIContacts() {
    String viewCommand = "view donor nhi asd9871 contacts";
    ArrayList<String> messages = commandHandler.commandControl(viewCommand);
    // assertEquals(2, messages.size());
    System.out.println(messages.get(0));
    assertEquals("Accessing data for donor ASD9871...", messages.get(0));
    assertEquals("Address:\n"
        + "Street Address Line 1: \n"
        + "Street Address Line 2: \n"
        + "Suburb: \n"
        + "City: \n"
        + "Region: \n"
        + "Post Code: \n"
        + "Country Code: \n"
        + "Mobile Number: \n"
        + "Home Number: \n"
        + "Email: \n", messages.get(1));
  }

  @Test
  public void viewByNHILog() {
    String viewCommand = "view donor nhi asd9871 log";
    ArrayList<String> messages = commandHandler.commandControl(viewCommand);
    assertEquals(2, messages.size());
    assertEquals("Accessing data for donor ASD9871...", messages.get(0));
    assertEquals("Update log:\n"
            + "User Created: Test Test (NHI: ASD9871), Created by: Testy McTestFace (Administrator, username: tester), at ",
        messages.get(1).substring(0, 119));
  }

  @Test
  public void viewByNHIInvalidOption() {
    String viewCommand = "view donor nhi asd9871 help";
    ArrayList<String> messages = commandHandler.commandControl(viewCommand);
    assertEquals(2, messages.size());
    assertEquals("Accessing data for donor ASD9871...", messages.get(0));
    assertEquals(
        "ERROR: Unknown object help. Object should be 'all', 'attributes', 'log', 'organs' or 'contacts'.\n",
        messages.get(1));
  }

  @Test
  public void viewByNHINoNHI() {
    String viewCommand = "view donor nhi";
    ArrayList<String> messages = commandHandler.commandControl(viewCommand);
    assertEquals(1, messages.size());
    assertEquals("ERROR: NHI and/or Object not given", messages.get(0));
  }

  @Test
  public void viewByNHINotInDatabase() {
    String viewCommand = "view donor nhi xyz9871 all";
    ArrayList<String> messages = commandHandler.commandControl(viewCommand);
    assertEquals(1, messages.size());
    assertEquals("ERROR: NHI Code xyz9871 not found in database. Did you spell it right, " +
        "is it in uppercase? Enter 'help' for more information\n", messages.get(0));
  }

  //-----------------------------------------------VIEW CLINICIAN TESTS-----------------------------
  @Test
  public void viewClinSuccess() {
    String viewCommand = "view clinician 0";
    ArrayList<String> messages = commandHandler.commandControl(viewCommand);
    assertEquals("Clinician Details:\n"
        + "\n"
        + "Name: Default Clinician Placeholder\n"
        + "Staff ID: 0\n"
        + "\n"
        + "Contact Details:\n"
        + "Address:\n"
        + "Street Address Line 1: workplace\n"
        + "Street Address Line 2: \n"
        + "Suburb: \n"
        + "City: \n"
        + "Region: Northland\n"
        + "Post Code: \n"
        + "Country Code: \n"
        + "Mobile Number: \n"
        + "Home Number: \n"
        + "Email: \n"
        + "\n"
        + "Account created at: 2018", messages.get(0).substring(0, 276));
    assertEquals(1, messages.size());
  }

  @Test
  public void viewClinInvalidSpelling() {
    String viewCommand = "view clincan 0";
    ArrayList<String> messages = commandHandler.commandControl(viewCommand);
    assertEquals(1, messages.size());
    assertEquals("ERROR: Invalid account type", messages.get(0));
  }

  @Test
  public void viewClinNotInDatabase() {
    String viewCommand = "view clinician 5";
    ArrayList<String> messages = commandHandler.commandControl(viewCommand);
    assertEquals(1, messages.size());
    assertEquals("ERROR: Staff ID 5 not found in database. Enter 'help view' for more information.",
        messages.get(0));
  }

  @Test
  public void viewClinNoID() {
    String viewCommand = "view clinician";
    ArrayList<String> messages = commandHandler.commandControl(viewCommand);
    assertEquals(1, messages.size());
    assertEquals("ERROR: Insufficient information provided for viewing account.", messages.get(0));
  }

  @Test
  public void viewNoAccountType() {
    String viewCommand = "view";
    ArrayList<String> messages = commandHandler.commandControl(viewCommand);
    assertEquals(1, messages.size());
    assertEquals(
        "ERROR: Account type and identifier must be specified. Enter 'help view' for more help.",
        messages.get(0));
  }

  //-----------------------------------------------DELETE TESTS-------------------------------------

  @Test
  public void deleteNoNHI() {
    String deleteCommand = "delete donor";
    ArrayList<String> messages = commandHandler.commandControl(deleteCommand);
    assertEquals(1, messages.size());
    assertEquals("ERROR: NHI not given for deletion", messages.get(0));
  }

  @Test
  public void deleteNHINotInDatabase() {
    String deleteCommand = "delete donor XYZ9871";
    ArrayList<String> messages = commandHandler.commandControl(deleteCommand);
    assertEquals(1, messages.size());
    assertEquals(
        "There was no donor found that matched the nhi code XYZ9871. Did you spell it correctly?",
        messages.get(0));
  }

  @Test
  public void deleteSuccess() {
    commandHandler.commandControl("create donor name=Throwaway dateofbirth=19971201 nhi=asj9871");
    String deleteCommand = "delete donor asj9871";
    ArrayList<String> messages = commandHandler.commandControl(deleteCommand);
    assertEquals(2, messages.size());
    assertEquals("Found account for nhi: asj9871", messages.get(0));
    assertEquals("Deletion success, remember to save the application to make the action permanent.",
        messages.get(1));
  }

  //-----------------------------------------------UPDATE TESTS-------------------------------------

  @Test
  public void updateNoObject() {
    String updateCommand = "update donor asd9872";
    ArrayList<String> messages = commandHandler.commandControl(updateCommand);
    assertEquals(1, messages.size());
    assertEquals("ERROR: Insufficient information provided for profile update", messages.get(0));
  }

  @Test
  public void updateOrgansOfDonors() {
    String updateCommand = "update donor donors organs non-receiving liver false";
    ArrayList<String> messages = commandHandler.commandControl(updateCommand);
    assertEquals(1, messages.size());
    assertEquals("ERROR: target = donors. You cannot update all donors at once!" +
        " The target should be the NHI code of the donor you wish to update.", messages.get(0));
  }

  @Test
  public void updateOrgansDonateLiver() {
    String updateCommand = "update donor asd9872 organs non-receiving liver false";
    ArrayList<String> messages = commandHandler.commandControl(updateCommand);
    assertEquals(1, messages.size());
    assertEquals("User Being Modified: Updating Person (NHI: ASD9872), Changed by User: "
        + "Testy McTestFace (Administrator, username: tester), liverDonation changed from 'false' "
        + "to 'false' at ", messages.get(0).substring(0, 171));
  }

  @Test
  public void updateInvalidNHI() {
    String updateCommand = "update donor xyz9872 organs non-receiving liver false";
    ArrayList<String> messages = commandHandler.commandControl(updateCommand);
    assertEquals(1, messages.size());
    assertEquals("ERROR: NHI Code xyz9872 not found in database. " +
            "Did you spell it right, is it in uppercase? Enter 'help' for more information",
        messages.get(0));
  }

  @Test
  public void updateAttributesOfDonors() {
    String updateCommand = "update donor donors attributes weight 65";
    ArrayList<String> messages = commandHandler.commandControl(updateCommand);
    assertEquals(1, messages.size());
    assertEquals("ERROR: target = donors. You cannot update all donors at once!" +
        " The target should be the NHI code of the donor you wish to update.", messages.get(0));
  }

  @Test
  public void updateAttributesInvalidNHI() {
    String updateCommand = "update donor xyz9872 attributes weight 65";
    ArrayList<String> messages = commandHandler.commandControl(updateCommand);
    assertEquals(1, messages.size());
    assertEquals("ERROR: NHI Code xyz9872 not found in database. " +
            "Did you spell it right, is it in uppercase? Enter 'help' for more information",
        messages.get(0));
  }

  @Test
  public void updateProfileTitle() {
    String updateCommand = "update donor asd9872 profile title MR";
    ArrayList<String> messages = commandHandler.commandControl(updateCommand);
    assertEquals(1, messages.size());
    assertEquals("User Being Modified: Updating Person (NHI: ASD9872), Changed by User: "
        + "Testy McTestFace (Administrator, username: tester), title changed from 'unspecified' "
        + "to 'MR' at ", messages.get(0).substring(0, 166));
  }

  @Test
  public void updateAttributesWeight() {
    String updateCommand = "update donor asd9872 attributes weight 65";
    ArrayList<String> messages = commandHandler.commandControl(updateCommand);
    assertEquals(1, messages.size());
    assertEquals("User Being Modified: Updating Person (NHI: ASD9872), Changed by User: "
        + "Testy McTestFace (Administrator, username: tester), weight changed from '0.0' to '65' "
        + "at ", messages.get(0).substring(0, 159));
  }

  @Test
  public void updateContactsMobileNum() {
    String updateCommand = "update donor asd9872 contacts mobileNum 0279871234";
    ArrayList<String> messages = commandHandler.commandControl(updateCommand);
    assertEquals(1, messages.size());
    assertEquals("User Being Modified: Updating Person (NHI: ASD9872), Changed by User: "
        + "Testy McTestFace (Administrator, username: tester), personal contact mobileNum changed "
        + "from '' to '0279871234' at ", messages.get(0).substring(0, 184));
  }

  @Test
  public void updateInvalidObject() {
    String updateCommand = "update donor asd9872 help weight 65";
    ArrayList<String> messages = commandHandler.commandControl(updateCommand);
    assertEquals(1, messages.size());
    assertEquals(
        "ERROR: Unknown object help. Object should be 'profile', 'attributes', 'organs' or 'contacts'.",
        messages.get(0));
  }

  //-----------------------------------------------HELP TESTS---------------------------------------
  @Test
  public void invalidHelpOption() {
    String command = "help asd";
    ArrayList<String> messages = commandHandler.commandControl(command);
    assertEquals(1, messages.size());
    String modelResult = "Couldn't recognise help text. Options include all, update, view, create, delete, donor, clinician, import, save and launch";
    assertTrue(messages.get(0).contains(modelResult));
  }

  @Test
  public void helpAll() {
    String command = "help all";
    ArrayList<String> messages = commandHandler.commandControl(command);
    assertEquals(1, messages.size());
    String result = messages.get(0);

    String modelResult1 = "Create Donor Command";
    String modelResult2 = "Create Clinician Command";
    String modelResult3 = "Update Donor Command";
    String modelResult4 = "Update Clinician Command";
    String modelResult5 = "Delete Donor Command";
    String modelResult6 = "Delete Clinician Command";
    String modelResult7 = "The required input for importing saved data into the system: import";
    String modelResult8 = "The required input for saving the current data: save";
    String modelResult9 = "The command to launch the GUI component of the app from the CLI: launch";
    String modelResult10 = "Can only be called once while the application is running.";

    assertTrue(result.contains(modelResult1));
    assertTrue(result.contains(modelResult2));
    assertTrue(result.contains(modelResult3));
    assertTrue(result.contains(modelResult4));
    assertTrue(result.contains(modelResult5));
    assertTrue(result.contains(modelResult6));
    assertTrue(result.contains(modelResult7));
    assertTrue(result.contains(modelResult8));
    assertTrue(result.contains(modelResult9));
    assertTrue(result.contains(modelResult10));
  }

  @Test
  public void helpCreate() {
    String command = "help create";
    ArrayList<String> messages = commandHandler.commandControl(command);

    assertEquals(1, messages.size());

    String outputMessage = messages.get(0);

    String modelResult1 = "Create Donor Command";
    String modelResult2 = "Create Clinician Command";

    assertTrue(outputMessage.contains(modelResult1));
    assertTrue(outputMessage.contains(modelResult2));

  }

  @Test
  public void helpUpdate() {
    String command = "help update";
    ArrayList<String> messages = commandHandler.commandControl(command);
    assertEquals(1, messages.size());

    String outputMessage = messages.get(0);

    String modelResult1 = "Update Donor Command";
    String modelResult2 = "Update Clinician Command";

    assertTrue(outputMessage.contains(modelResult1));
    assertTrue(outputMessage.contains(modelResult2));

  }

  @Test
  public void helpView() {
    String command = "help view";
    ArrayList<String> messages = commandHandler.commandControl(command);
    assertEquals(1, messages.size());

    String outputMessage = messages.get(0);

    String modelResult1 = "View Donor Command";
    String modelResult2 = "View Clinician Command";

    assertTrue(outputMessage.contains(modelResult1));
    assertTrue(outputMessage.contains(modelResult2));
  }

  @Test
  public void helpDelete() {
    String command = "help delete";
    ArrayList<String> messages = commandHandler.commandControl(command);
    assertEquals(1, messages.size());

    String outputMessage = messages.get(0);

    String modelResult1 = "Delete Donor Command";
    String modelResult2 = "Delete Clinician Command";

    assertTrue(outputMessage.contains(modelResult1));
    assertTrue(outputMessage.contains(modelResult2));
  }

  @Test
  public void helpImport() {
    String command = "help import";
    ArrayList<String> messages = commandHandler.commandControl(command);
    assertEquals(1, messages.size());
    String expected = "The required input for importing saved data into the system: import";
    assertTrue(messages.get(0).contains(expected));
  }

  @Test
  public void helpSave() {
    String command = "help save";
    ArrayList<String> messages = commandHandler.commandControl(command);
    assertEquals(1, messages.size());
    String expected = "The required input for saving the current data: save";
    assertTrue(messages.get(0).contains(expected));
  }


  @Test
  public void helpLaunch() {
    String command = "help launch";
    ArrayList<String> messages = commandHandler.commandControl(command);
    assertEquals(1, messages.size());

    String modelResult1 = "The command to launch the GUI component of the app from the CLI: launch";
    String modelResult2 = "Can only be called once while the application is running.";

    assertTrue(messages.get(0).contains(modelResult1));
    assertTrue(messages.get(0).contains(modelResult2));
  }

}
