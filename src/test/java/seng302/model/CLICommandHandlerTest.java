package seng302.model;

import java.util.ArrayList;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import seng302.model.person.Administrator;

public class CLICommandHandlerTest {

  private static CLICommandHandler commandHandler;

  @BeforeClass
  public static void setUp() {
    commandHandler = new CLICommandHandler();
    Administrator testingAdmin = new Administrator("Testy", "", "McTestFace", "tester", "password");
    AccountManager.setCurrentUser(testingAdmin);
    String creationCommand = "create Name=Test Test dateofbirth=19971201 nhi=asd9871";
    commandHandler.commandControl(creationCommand);
    commandHandler.commandControl("create name=Updating Person dateofbirth=19971201 nhi=asd9872");
  }

  @Test public void invalidCommand() {
    String command = "invalid command here :)";
    ArrayList<String> messages = commandHandler.commandControl(command);
    assertEquals(1, messages.size());
    assertEquals("Invalid command. Please try again.", messages.get(0));
  }
  //-----------------------------------------------CREATION TESTS-----------------------------------
  @Test public void createDonorSuccess() {
    String creationCommand = "create Name=Steven Philip Jones dateofbirth=19971201 nhi=asd9876";
    ArrayList<String> messages = commandHandler.commandControl(creationCommand);
    assertEquals(1, messages.size());
    assertEquals(
        "Account created with name: Steven Philip Jones, Date of birth: 1997-12-01 and NHI: ASD9876",
        messages.get(0));
  }

  @Test public void createDonorNoName() {
    String creationCommand = "create dateofbirth=19971201 nhi=asd9876";
    ArrayList<String> messages = commandHandler.commandControl(creationCommand);
    assertEquals(1, messages.size());
    assertEquals(
        "In state create, but missing required fields. Correct template is name=<Name> dateOfBirth=<yyyyMMdd> nhi=<NHI number>.",
        messages.get(0));
  }

  @Test public void createDonorWrongOrder() {
    String creationCommand = "create dateofbirth=19971201 nhi=asd9876 name=Steven Philip Jones";
    ArrayList<String> messages = commandHandler.commandControl(creationCommand);
    assertEquals(1, messages.size());
    assertEquals(
        "Correct tokens provided, but not in the correct order. Correct order is name=<Name> dateOfBirth=<yyyyMMdd> nhi=<NHI number>.",
        messages.get(0));
  }

  @Test public void createDonorNonAlphaName() {
    String creationCommand = "create name=$teven Philip Jones dateofbirth=19971201 nhi=asd9876";
    ArrayList<String> messages = commandHandler.commandControl(creationCommand);
    assertEquals(1, messages.size());
    assertEquals("That name is invalid, as it it is not alphabetical. Please try again.",
        messages.get(0));
  }

  @Test public void createDonorEmptyName() {
    String creationCommand = "create name=dateofbirth=19971201 nhi=asd9876";
    ArrayList<String> messages = commandHandler.commandControl(creationCommand);
    assertEquals(1, messages.size());
    assertEquals("A name is required for the profile. Please try again.", messages.get(0));
  }

  @Test public void createDonorInvalidDate() {
    String creationCommand = "create name=Steven Philip Jones dateofbirth=1997 nhi=asd9876";
    ArrayList<String> messages = commandHandler.commandControl(creationCommand);
    assertEquals(1, messages.size());
    assertEquals("The format required for date is yyyyMMdd. Please try again.", messages.get(0));
  }

  @Test public void createDonorInvalidNHI() {
    String creationCommand = "create name=Steven Philip Jones dateofbirth=19971201 nhi=9876abd";
    ArrayList<String> messages = commandHandler.commandControl(creationCommand);
    assertEquals(1, messages.size());
    assertEquals(
        "Either you have entered an invalid NHI number or there is already a profile with that number. Please try again.",
        messages.get(0));
  }

  @Test public void createDonorUsedNHI() {
    String creationCommand = "create name=Steven Philip Jones dateofbirth=19971201 nhi=asd9871";
    ArrayList<String> messages = commandHandler.commandControl(creationCommand);
    assertEquals(1, messages.size());
    assertEquals(
        "Either you have entered an invalid NHI number or there is already a profile with that number. Please try again.",
        messages.get(0));
  }

  @Test public void createDonorSuccessOneName() {
    String creationCommand = "create Name=Steven dateofbirth=19971201 nhi=asd9875";
    ArrayList<String> messages = commandHandler.commandControl(creationCommand);
    assertEquals(1, messages.size());
    assertEquals("Account created with name: Steven, Date of birth: 1997-12-01 and NHI: ASD9875",
        messages.get(0));
  }

  @Test public void createDonorSuccessNoMiddleName() {
    String creationCommand = "create Name=Steven Jonesy dateofbirth=19971201 nhi=asd9874";
    ArrayList<String> messages = commandHandler.commandControl(creationCommand);
    assertEquals(1, messages.size());
    assertEquals(
        "Account created with name: Steven Jonesy, Date of birth: 1997-12-01 and NHI: ASD9874",
        messages.get(0));
  }

  //-----------------------------------------------VIEW BY NAME TESTS-------------------------------
  @Test public void viewByNameAll() {
    String viewCommand = "view Name Test Test all";
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
        + "Email: \n", messages.get(2).substring(0, 109) + messages.get(2).substring(128, messages.get(2).length() - 1));
  }

  @Test public void viewByNameAttributes() {
    String viewCommand = "view Name Test Test attributes";
    ArrayList<String> messages = commandHandler.commandControl(viewCommand);
    assertEquals(3, messages.size());
    assertEquals("1 match(es) for the name Test Test.\n", messages.get(0));
    assertEquals("Accessing data for donor ASD9871...", messages.get(1));
    assertEquals("User Attributes:\n"
        + "\tBlood Pressure: 0/0\n", messages.get(2));
  }

  @Test public void viewByNameOrgans() {
    String viewCommand = "view Name Test Test organs";
    ArrayList<String> messages = commandHandler.commandControl(viewCommand);
    assertEquals(3, messages.size());
    assertEquals("1 match(es) for the name Test Test.\n", messages.get(0));
    assertEquals("Accessing data for donor ASD9871...", messages.get(1));
    assertEquals("Organs to donate:\n"
        + "No organs to donate\n"
        + "\n", messages.get(2));
  }

  @Test public void viewByNameContacts() {
    String viewCommand = "view Name Test Test contacts";
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

  @Test public void viewByNameLog() {
    String viewCommand = "view Name Test Test log";
    ArrayList<String> messages = commandHandler.commandControl(viewCommand);
    assertEquals(3, messages.size());
    assertEquals("1 match(es) for the name Test Test.\n", messages.get(0));
    assertEquals("Accessing data for donor ASD9871...", messages.get(1));
    assertEquals("Update log:\n"
        + "User Created: Test Test (NHI: ASD9871), Created by: Testy McTestFace (Administrator, username: tester), at ",
        messages.get(2).substring(0, 119));
  }

  @Test public void viewByNameInvalidOption() {
    String viewCommand = "view name Test Test help";
    ArrayList<String> messages = commandHandler.commandControl(viewCommand);
    assertEquals(3, messages.size());
    assertEquals("1 match(es) for the name Test Test.\n", messages.get(0));
    assertEquals("Accessing data for donor ASD9871...", messages.get(1));
    assertEquals("ERROR: Unknown object help. Object should be 'all', 'attributes', 'log', 'organs' or 'contacts'.\n", messages.get(2));
  }

  @Test public void viewByNameNoName() {
    String viewCommand = "view name";
    ArrayList<String> messages = commandHandler.commandControl(viewCommand);
    assertEquals(1, messages.size());
    assertEquals("ERROR: Names and/or Object not given", messages.get(0));
  }

  @Test public void viewByNameNotInDatabase() {
    String viewCommand = "view name Fake Name all";
    ArrayList<String> messages = commandHandler.commandControl(viewCommand);
    assertEquals(1, messages.size());
    assertEquals("ERROR: Fake Name not found in database. Did you spell it right? " +
        "Try searching using the donor NHI code instead. Enter 'help' for more information\n", messages.get(0));
  }

  //-----------------------------------------------VIEW BY NHI TESTS--------------------------------

  @Test public void viewByNHIAll() {
    String viewCommand = "view nhi asd9871 all";
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
        + "Email: \n", messages.get(1).substring(0, 109) + messages.get(1).substring(128, messages.get(1).length() - 1));
  }

  @Test public void viewByNHIAttributes() {
    String viewCommand = "view nhi asd9871 attributes";
    ArrayList<String> messages = commandHandler.commandControl(viewCommand);
    assertEquals(2, messages.size());
    assertEquals("Accessing data for donor ASD9871...", messages.get(0));
    assertEquals("User Attributes:\n"
        + "\tBlood Pressure: 0/0\n", messages.get(1));
  }

  @Test public void viewByNHIOrgans() {
    String viewCommand = "view nhi asd9871 organs";
    ArrayList<String> messages = commandHandler.commandControl(viewCommand);
    assertEquals(2, messages.size());
    assertEquals("Accessing data for donor ASD9871...", messages.get(0));
    assertEquals("Organs to donate:\n"
        + "No organs to donate\n"
        + "\n", messages.get(1));
  }

  @Test public void viewByNHIContacts() {
    String viewCommand = "view nhi asd9871 contacts";
    ArrayList<String> messages = commandHandler.commandControl(viewCommand);
    assertEquals(2, messages.size());
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

  @Test public void viewByNHILog() {
    String viewCommand = "view nhi asd9871 log";
    ArrayList<String> messages = commandHandler.commandControl(viewCommand);
    assertEquals(2, messages.size());
    assertEquals("Accessing data for donor ASD9871...", messages.get(0));
    assertEquals("Update log:\n"
            + "User Created: Test Test (NHI: ASD9871), Created by: Testy McTestFace (Administrator, username: tester), at ",
        messages.get(1).substring(0, 119));
  }

  @Test public void viewByNHIInvalidOption() {
    String viewCommand = "view nhi asd9871 help";
    ArrayList<String> messages = commandHandler.commandControl(viewCommand);
    assertEquals(2, messages.size());
    assertEquals("Accessing data for donor ASD9871...", messages.get(0));
    assertEquals("ERROR: Unknown object help. Object should be 'all', 'attributes', 'log', 'organs' or 'contacts'.\n", messages.get(1));
  }

  @Test public void viewByNHINoNHI() {
    String viewCommand = "view nhi";
    ArrayList<String> messages = commandHandler.commandControl(viewCommand);
    assertEquals(1, messages.size());
    assertEquals("ERROR: NHI and/or Object not given", messages.get(0));
  }

  @Test public void viewByNHINotInDatabase() {
    String viewCommand = "view nhi xyz9871 all";
    ArrayList<String> messages = commandHandler.commandControl(viewCommand);
    assertEquals(1, messages.size());
    assertEquals("ERROR: NHI Code xyz9871 not found in database. Did you spell it right, " +
        "is it in uppercase? Enter 'help' for more information\n", messages.get(0));
  }

  //-----------------------------------------------DELETE TESTS-------------------------------------

  @Test public void deleteNoNHI() {
    String deleteCommand = "delete";
    ArrayList<String> messages = commandHandler.commandControl(deleteCommand);
    assertEquals(1, messages.size());
    assertEquals("ERROR: NHI not given for deletion", messages.get(0));
  }

  @Test public void deleteNHINotInDatabase() {
    String deleteCommand = "delete XYZ9871";
    ArrayList<String> messages = commandHandler.commandControl(deleteCommand);
    assertEquals(1, messages.size());
    assertEquals("There was no donor found that matched the nhi code XYZ9871. Did you spell it correctly?", messages.get(0));
  }

  @Test public void deleteSuccess() {
    commandHandler.commandControl("create name=Throwaway dateofbirth=19971201 nhi=asj9871");
    String deleteCommand = "delete asj9871";
    ArrayList<String> messages = commandHandler.commandControl(deleteCommand);
    assertEquals(2, messages.size());
    assertEquals("Found account for nhi: asj9871", messages.get(0));
    assertEquals("Deletion success, remember to save the application to make the action permanent.", messages.get(1));
  }

  //-----------------------------------------------UPDATE TESTS-------------------------------------

  @Test public void updateNoObject() {
    String updateCommand = "update asd9872";
    ArrayList<String> messages = commandHandler.commandControl(updateCommand);
    assertEquals(1, messages.size());
    assertEquals("ERROR: Insufficient information provided for profile update", messages.get(0));
  }

  @Test public void updateOrgansOfDonors() {
    String updateCommand = "update donors organs non-receiving liver false";
    ArrayList<String> messages = commandHandler.commandControl(updateCommand);
    assertEquals(1, messages.size());
    assertEquals("ERROR: target = donors. You cannot update all donors at once!" +
        " The target should be the NHI code of the donor you wish to update.", messages.get(0));
  }

  @Test public void updateOrgansDonateLiver() {
    String updateCommand = "update asd9872 organs non-receiving liver false";
    ArrayList<String> messages = commandHandler.commandControl(updateCommand);
    assertEquals(1, messages.size());
    assertEquals("User Being Modified: Updating Person (NHI: ASD9872), Changed by User: "
        + "Testy McTestFace (Administrator, username: tester), liverDonation changed from 'false' "
        + "to 'false' at ", messages.get(0).substring(0, 171));
  }

  @Test public void updateInvalidNHI() {
    String updateCommand = "update xyz9872 organs non-receiving liver false";
    ArrayList<String> messages = commandHandler.commandControl(updateCommand);
    assertEquals(1, messages.size());
    assertEquals("ERROR: NHI Code xyz9872 not found in database. " +
        "Did you spell it right, is it in uppercase? Enter 'help' for more information", messages.get(0));
  }

  @Test public void updateAttributesOfDonors() {
    String updateCommand = "update donors attributes weight 65";
    ArrayList<String> messages = commandHandler.commandControl(updateCommand);
    assertEquals(1, messages.size());
    assertEquals("ERROR: target = donors. You cannot update all donors at once!" +
        " The target should be the NHI code of the donor you wish to update.", messages.get(0));
  }

  @Test public void updateAttributesInvalidNHI() {
    String updateCommand = "update xyz9872 attributes weight 65";
    ArrayList<String> messages = commandHandler.commandControl(updateCommand);
    assertEquals(1, messages.size());
    assertEquals("ERROR: NHI Code xyz9872 not found in database. " +
        "Did you spell it right, is it in uppercase? Enter 'help' for more information", messages.get(0));
  }

  @Test public void updateProfileTitle() {
    String updateCommand = "update asd9872 profile title MR";
    ArrayList<String> messages = commandHandler.commandControl(updateCommand);
    assertEquals(1, messages.size());
    assertEquals("User Being Modified: Updating Person (NHI: ASD9872), Changed by User: "
        + "Testy McTestFace (Administrator, username: tester), title changed from 'unspecified' "
        + "to 'MR' at ", messages.get(0).substring(0, 166));
  }

  @Test public void updateAttributesWeight() {
    String updateCommand = "update asd9872 attributes weight 65";
    ArrayList<String> messages = commandHandler.commandControl(updateCommand);
    assertEquals(1, messages.size());
    assertEquals("User Being Modified: Updating Person (NHI: ASD9872), Changed by User: "
        + "Testy McTestFace (Administrator, username: tester), weight changed from '0.0' to '65' "
        + "at ", messages.get(0).substring(0, 159));
  }

  @Test public void updateContactsMobileNum() {
    String updateCommand = "update asd9872 contacts mobileNum 0279871234";
    ArrayList<String> messages = commandHandler.commandControl(updateCommand);
    assertEquals(1, messages.size());
    assertEquals("User Being Modified: Updating Person (NHI: ASD9872), Changed by User: "
        + "Testy McTestFace (Administrator, username: tester), personal contact mobileNum changed "
        + "from '' to '0279871234' at ", messages.get(0).substring(0, 184));
  }

  @Test public void updateInvalidObject() {
    String updateCommand = "update asd9872 help weight 65";
    ArrayList<String> messages = commandHandler.commandControl(updateCommand);
    assertEquals(1, messages.size());
    assertEquals("ERROR: Unknown object help. Object should be 'profile', 'attributes', 'organs' or 'contacts'.", messages.get(0));
  }

  //-----------------------------------------------HELP TESTS---------------------------------------
  @Test public void invalidHelpCommand() {
    String command = "help";
    ArrayList<String> messages = commandHandler.commandControl(command);
    assertEquals(1, messages.size());
    assertEquals("ERROR: Type of help must be specified. Valid "
        + "options are: all, create, update, view, import, save, delete, launch."
        + " For example, 'help all' gives help with all commands.", messages.get(0));
  }

  @Test public void invalidHelpOption() {
    String command = "help export";
    ArrayList<String> messages = commandHandler.commandControl(command);
    assertEquals(1, messages.size());
    assertEquals("Invalid query. returning to the main menu...\n", messages.get(0));
  }

  @Test public void helpAll() {
    String command = "help all";
    ArrayList<String> messages = commandHandler.commandControl(command);
    assertEquals(1, messages.size());
    String expected =("The required input for creating an account: Create name=<Full Name> dateOfBirth=<date of birth> nhi=<Nhi number>\n");
    expected+=("Full Name -All the names the person has. the first name will be stored as their first name, while the last name given will be stored as the last. All names in between will be stored as other names. There should be no space between the equals sign and the first name.\n");
    expected+=("date of birth -The date of birth of the account holder. The format is yyyyMMdd. y =year, M=month, d = day. There should be no space in between the equals and the date of birth.\n");
    expected+=("nhi -The unique number given by the New Zealand Ministry of health to prove uniqueness\n");
    expected+=("========================================================================================\n");
    expected+=("The required input for updating an account: Update <nhi code> <object> <attribute> <value>\n");
    expected+=("========================================================================================\n");
    expected+=("There are three separate inputs for viewing accounts\n");
    expected+=("1. view nhi <nhi number> <object> \n");
    expected+=("2. view name <first name> <last name> <object> \n");
    expected+=("3. view donors <object> \n");
    expected+=("<nhi number> -The unique number given by the New Zealand Ministry of health to prove uniqueness\n");
    expected+=("<object> - either 'all', 'log', 'attributes', 'contacts' or 'organs'\n");
    expected+=("========================================================================================\n");
    expected+=("The required input for importing saved data into the system: import\n");
    expected+=("========================================================================================\n");
    expected+=("The required input for saving the current data: save\n");
    expected+=("========================================================================================\n");
    expected+=("the required input for deleting an account: delete <nhi number>\n");
    expected+=("<nhi number> - The unique number given to every patient in the New Zealand Health system.\n");
    expected+=("========================================================================================\n");
    expected+=("For more information regarding commands for the system, please consult the user_manual\n");
    assertEquals(expected, messages.get(0));
  }

  @Test public void helpCreate() {
    String command = "help create";
    ArrayList<String> messages = commandHandler.commandControl(command);
    assertEquals(1, messages.size());
    String expected =("The required input for creating an account: Create name=<Full Name> dateOfBirth=<date of birth> nhi=<Nhi number>\n");
    expected+=("Full Name -All the names the person has. the first name will be stored as their first name, while the last name given will be stored as the last. All names in between will be stored as other names. There should be no space between the equals sign and the first name.\n");
    expected+=("date of birth -The date of birth of the account holder. The format is yyyyMMdd. y =year, M=month, d = day. There should be no space in between the equals and the date of birth.\n");
    expected+=("nhi -The unique number given by the New Zealand Ministry of health to prove uniqueness\n");
    expected+=("For more information, please consult the user_manual\n");
    assertEquals(expected, messages.get(0));
  }

  @Test public void helpUpdate() {
    String command = "help update";
    ArrayList<String> messages = commandHandler.commandControl(command);
    assertEquals(1, messages.size());
    String expected =("The required input for updating an account: Update <nhi code> <object> <attribute> <value>\n");
    expected+=("For more detailed information about the seperate terms, please consult the user_manual\n");
    assertEquals(expected, messages.get(0));
  }

  @Test public void helpView() {
    String command = "help view";
    ArrayList<String> messages = commandHandler.commandControl(command);
    assertEquals(1, messages.size());
    String expected = ("There are three separate inputs for viewing accounts\n");
    expected+=("1. view nhi <nhi number> <object> \n");
    expected+=("2. view name <first name> <last name> <object> \n");
    expected+=("3. view donors <object> \n");
    expected+=("<nhi number> -The unique number given by the New Zealand Ministry of health to prove uniqueness\n");
    expected+=("<object> - either 'all', 'log', 'attributes', 'contacts' or 'organs'\n");
    expected+=("Please consult the user_manual for more information\n");
    assertEquals(expected, messages.get(0));
  }

  @Test public void helpImport() {
    String command = "help import";
    ArrayList<String> messages = commandHandler.commandControl(command);
    assertEquals(1, messages.size());
    String expected = "The required input for importing saved data into the system: import\n";
    expected+=("Please consult the user_manual for more information\n");
    assertEquals(expected, messages.get(0));
  }

  @Test public void helpSave() {
    String command = "help save";
    ArrayList<String> messages = commandHandler.commandControl(command);
    assertEquals(1, messages.size());
    String expected = "The required input for saving the current data: save\n";
    expected+=("Please consult the user_manual for more information\n");
    assertEquals(expected, messages.get(0));
  }

  @Test public void helpDelete() {
    String command = "help delete";
    ArrayList<String> messages = commandHandler.commandControl(command);
    assertEquals(1, messages.size());
    String expected = "The required input for deleting an account: delete <nhi number>\n";
    expected+=("<nhi number> - The unique number given to every patient in the New Zealand Health system.\n");
    expected+=("For more information, please consult the user_manual\n");
    assertEquals(expected, messages.get(0));
  }

  @Test public void helpLaunch() {
    String command = "help launch";
    ArrayList<String> messages = commandHandler.commandControl(command);
    assertEquals(1, messages.size());
    String expected = "Launches the GUI component of the application.\n";
    expected+=("Can only be called once while the application is running.\n");
    expected+=("Only recognised if used in the CLI outside of the GUI component.\n");
    assertEquals(expected, messages.get(0));
  }

}
