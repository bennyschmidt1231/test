package seng302.model.person;



import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import seng302.model.*;
import seng302.model.person.ContactDetails;
import seng302.model.person.DonorReceiver;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.zip.DataFormatException;

import static org.junit.Assert.*;

/**
 * A class used for testing the methods of the DonorReceiver class.
 */
public class DonorReceiverTest {

    private DonorReceiver donorReceiver;
    private DonorReceiver donorReceiver2;
    private UserAttributeCollection userAttCol;
    private DonorOrganInventory donOrgInv;
    private ReceiverOrganInventory reqOrgans;
    private ContactDetails contacts;
    private Administrator admin;


    /**
     * Populating an donorReceiver with values to test.
     */
    @Before
    public void setUp() {

        LocalDate DOB = LocalDate.of(1990, 12, 31);
        LocalDate DOD = LocalDate.of(2016, 12, 31);

        userAttCol = new UserAttributeCollection(2.0, 51.0, "AB-", "1 Fleet Street",
                "", "cashmere", "christchurch", 5678, "NZ", false, "130/80", false, 2.0, "");

        donOrgInv = new DonorOrganInventory(true, false, true, true,
                false, false, true,
                false, true, false, true, true);

        reqOrgans = new ReceiverOrganInventory();

        contacts = new ContactDetails(null, null, null, null);

        donorReceiver = new DonorReceiver("Sweeny", "", "Todd", DOB, "ABC1234");
        donorReceiver.setUserAttributeCollection(userAttCol);
        donorReceiver.setDonorOrganInventory(donOrgInv);
        donorReceiver.setRequiredOrgans(reqOrgans);
        donorReceiver.setContactDetails(contacts);
        donorReceiver.setTitle("MR");
        donorReceiver.setPreferredName("Pâtissier");
        donorReceiver.setDateOfDeath(DOD);
        donorReceiver.setGender('M');
        donorReceiver.setBirthGender('M');
        donorReceiver.setLivedInUKFlag(false);
        admin = new Administrator("Looming", null, "Percy",
                new ContactDetails(null, null, null, null),
                "looming", "password", null, null);
        donorReceiver.updateProfile(admin, "gender", "F");
        donorReceiver.updateProfile(admin, "givenName", "Bobby");
        donorReceiver.updateProfile(admin, "title", "miss");
        LocalDate bobDOB = LocalDate.of(1986, 06, 15);
        donorReceiver2 = new DonorReceiver("Bob", "James", "Smith", bobDOB, "GHJ6785"){};
    }


    /**
     * Tests the generate overview method with an donorReceiver which has no organs
     * registered. It should return the full name of the donorReceiver owner and
     * nothing more.
     */
    @Test
    public void generateOverviewTestWithNoOrgans() {

        String expected = "Bobby Todd";

        donorReceiver.getDonorOrganInventory().setAllOrgans(false);
        String actual = donorReceiver.generateOverview();

        Assert.assertEquals(expected, actual);

    }


    /**
     * Tests the generate overview method with an donorReceiver which has multiple
     * organs registered. This should return the name of the donorReceiver holder and
     * a list of the organs they have donated.
     */
    @Test
    public void generateOverviewTestWithOrgans() {

        String expected = "Bobby Todd\nOrgans to donate:\n\tLiver\n\t" +
                "Pancreas\n\tHeart\n\tCorneas\n\tSkin\n\tBone marrow\n\t" +
                "Connective Tissue\n\n\n";
        String actual = donorReceiver.generateOverview();
        Assert.assertEquals(expected, actual);

    }


    /**
     * Tests for successful case where the updateLogToString() string matches what is expected. It is possible for this test to fail
     * in rare circumstances where the current time is 1 second faster than the toString method.
     */
    @Test
    public void logUpdateTest1() {
        donorReceiver.updateProfile(admin, "givenName", "Sweeny");
        String logEntry1 = new LogEntry(donorReceiver, admin, "gender", "M", "F").toString();
        logEntry1 = logEntry1.substring(0, logEntry1.length() - 4);
        String logEntry2 = new LogEntry(donorReceiver, admin, "firstName", "Sweeny", "Bobby").toString();
        logEntry2 = logEntry2.substring(0, logEntry2.length() - 4);
        donorReceiver.updateProfile(admin, "givenName", "Bobby");
        String logEntry3 = new LogEntry(donorReceiver, admin, "title", "MR", "MISS").toString();
        logEntry3 = logEntry3.substring(0, logEntry3.length() - 4);
        System.out.println(logEntry3);
        boolean foundLog1 = false;
        boolean foundLog2 = false;
        boolean foundLog3 = false;
        for (LogEntry log: donorReceiver.getUpDateLog()) {
            String logString = log.toString();
            logString = logString.substring(0, logString.length() - 4);
            if (logString.equals(logEntry1)) {
                foundLog1 = true;
            } else if (logString.equals(logEntry2)) {
                foundLog2 = true;
            } else if (logString.equals(logEntry3)) {
                foundLog3 = true;
            }
        }
        assertTrue(foundLog1);
        assertTrue(foundLog2);
        assertTrue(foundLog3);
    }

    /**
     * Tests for successful case where three logs have been added to the donorReceiver updateLog array list.
     */
    @Test
    public void logUpdateTest2() {
        assertEquals(3, donorReceiver.getUpDateLog().size());
    }

    /**
     * tests the success case where donorReceiver.toString() string matches what is expected. It is possible for this test to fail
     * in rare circumstances where the current time is 1 second faster than the toString method.
     */
    @Test
    public void toStringTest1() {
        String expected = "Donor Details:\n" +
                "\tName: MISS Bobby  Todd\n" +
                "\tPreferred Name: Pâtissier\n" +
                "\tNational Health Index: " + donorReceiver.getUserName() + "\n" +
                "\tDonorReceiver created at: " + DonorReceiver.formatDateTimeToString(LocalDateTime.now()) + "\n" +
                "\tDate of birth: 1990-12-31\n" +
                "\tDate of death: 2016-12-31\n" +
                "\tGender: F\n" +
                "\tBirth Gender: F\n" +
                "User Attributes:\n" +
                "\tHeight: 2.0m\n" +
                "\tWeight: 51.0kg\n" +
                "\tBlood Type: AB-\n\t" +
                "Blood Pressure: 130/80\n\t" +
                "Smoker: false\n\t" +
                "Alcohol Consumption: 2.00 standard drinks per week\n\n" +
                "Address:\n" +
                "\t1 Fleet Street\n" +
                "\t\n" +
                "\tcashmere\n" +
                "\tchristchurch\n" +
                "\t5678\n" +
                "\tNZ\n" +
                "\n" +
                "Organs to donate:\n" +
                "\tLiver\n" +
                "\tPancreas\n" +
                "\tHeart\n" +
                "\tCorneas\n" +
                "\tSkin\n" +
                "\tBone marrow\n" +
                "\tConnective Tissue\n" +
                "\n" +
                "\n" +
                "Personal Contact Details:\n" +
                "Address:\n" + donorReceiver.getContactDetails().getAddress() + "\n" +
                "Mobile Number: " + donorReceiver.getContactDetails().getMobileNum() + "\n" +
                "Home Number: " + donorReceiver.getContactDetails().getHomeNum() + "\n" +
                "Email: " + donorReceiver.getContactDetails().getEmail();
        assertEquals(expected, donorReceiver.toString());
    }

    /**
     * tests for successful case when the formatted localDate string matches what is expected.
     */
    @Test
    public void formatDateToStringTest1() {
        assertEquals("1990-12-31", DonorReceiver.formatDateToString(donorReceiver.getDateOfBirth()));
    }

    /**
     * tests for failure case when the formatted localDate string does not match what is expected.
     */
    @Test
    public void formatDateToStringTest2() {
        assertNotEquals("1990-12-30", DonorReceiver.formatDateToString(donorReceiver.getDateOfBirth()));
    }

    /**
     * tests for successful case when the formatted localDateTime string matches what is expected.
     */
    @Test
    public void formatDateTimeToStringTest1() {
        LocalDateTime time = LocalDateTime.of(1990,12,31,0,0,0);
        assertEquals("1990-12-31 00:00:00", DonorReceiver.formatDateTimeToString(time));
    }

    /**
     * tests for failure case when the formatted localDateTime string does not match what is expected.
     */
    @Test
    public void formatDateTimeToStringTest2() {
        LocalDateTime time = LocalDateTime.of(1990,12,31,0,0,1);
        assertEquals("1990-12-31 00:00:01", DonorReceiver.formatDateTimeToString(time));
    }


    /**
     * tests for successful case where the date of birth is valid.
     */
    @Test
    public void validateDateOfBirthTest1(){
        assertTrue(DonorReceiver.validateDateOfBirth(donorReceiver.getDateOfBirth()));
    }


    /**
     * tests for failure case where the date of birth is invalid. - Date of Birth is greater than current time
     */
    @Test
    public void validateDateOfBirthTest2(){
        LocalDate DOB = LocalDate.of(2100, 1, 1);
        donorReceiver.setDateOfDeath(LocalDate.of(2101, 1,1));
        assertFalse(DonorReceiver.validateDateOfBirth(DOB));
    }

    /**
     * tests for failure case where the date of birth is invalid. - donor is 65 or older
     */
    @Test
    public void validateDateOfBirthTest3(){
        LocalDate DOB = LocalDate.of(1950, 1, 1);
        assertTrue(DonorReceiver.validateDateOfBirth(DOB));
    }


    /**
     * tests for successful case where the date of death is valid.
     */
    @Test
    public void validateDateOfDeathTest1(){
        assertTrue(donorReceiver.validateDateOfDeath(donorReceiver.getDateOfDeath()));
    }


    /**
     * tests for failure case where the date of death is invalid. - death date is less than birth date
     */
    @Test
    public void validateDateOfDeathTest2(){
        LocalDate DOD = LocalDate.of(1989, 1, 1);
        assertFalse(donorReceiver.validateDateOfDeath(DOD));
    }

    /**
     * tests for failure case where the date of death is invalid. - death date is greater than current time
     */
    @Test
    public void validateDateOfDeathTest3(){
        LocalDate DOD = LocalDate.of(2100, 1, 1);
        donorReceiver.setDateOfBirth(LocalDate.of(2099, 1,1));
        assertFalse(donorReceiver.validateDateOfDeath(DOD));
    }


    /**
     * tests for success case where the gender is an element of {M,F,O,U}.
     */
    @Test
    public void validateGenderTest1() {
        assertTrue(donorReceiver.validateGender('M'));
    }


    /**
     * tests for failure case where the gender is not element of {M,F,O,U} - lower case
     */
    @Test
    public void validateGenderTest2() {
        assertFalse(donorReceiver.validateGender('m'));
    }


    /**
     * tests for failure case where the gender is not element of {M,F,O,U} - wrong code
     */
    @Test
    public void validateGenderTest3() {
        assertFalse(donorReceiver.validateGender('A'));
    }


    /**
     * tests for successful case where validateAlphanumericString() can be used in the DonorReceiver class.
     */
    @Test
    public void validateAlphanumericStringTest(){
        assertTrue(donorReceiver.getUserAttributeCollection().validateAlphanumericString(false, "Bobby",
                1, 100));
    }


    @Test
    /**
     * Tests for successful case where 5 Illness objects are correctly sorted into the two children array lists of the MasterIllnessList.
     */
    public void populateIllnessListsTest1() {
        assertEquals(0, donorReceiver.getCurrentDiagnoses().size());
        assertEquals(0, donorReceiver.getHistoricDiagnoses().size());
        donorReceiver.getMasterIllnessList().add(new Illness("SHINGLES", LocalDate.now(), false, true));
        donorReceiver.getMasterIllnessList().add(new Illness("SHINGLES", LocalDate.now(), false, false));
        donorReceiver.getMasterIllnessList().add(new Illness("SHINGLES", LocalDate.now(), false, true));
        donorReceiver.getMasterIllnessList().add(new Illness("SHINGLES", LocalDate.now(), true, false));
        donorReceiver.getMasterIllnessList().add(new Illness("SHINGLES", LocalDate.now(), true, false));
        donorReceiver.populateIllnessLists();
        assertEquals(3, donorReceiver.getCurrentDiagnoses().size());
        assertEquals(2, donorReceiver.getHistoricDiagnoses().size());

    }

    @Test
    /**
     * Tests for successful case where the given update is valid.
     */
    public void updateIllnessTest1() {
        donorReceiver.getMasterIllnessList().add(new Illness("SHINGLES", LocalDate.now(), false, true));
        donorReceiver.updateIllness(admin, "SHINGLES", "chronic", "false");
        // Test the attribute was updated.
        assertFalse(donorReceiver.getMasterIllnessList().get(0).isChronic());
        // check the DonorReceiver log was updated
        String expectedLogString = new LogEntry(donorReceiver, admin, "Illness 'SHINGLES' chronic", "true", "false").toString();
        expectedLogString = expectedLogString.substring(0, expectedLogString.length() - 3);
        boolean found = false;
        for (LogEntry log: donorReceiver.getUpDateLog()) {
            String logString = log.toString();
            logString = logString.substring(0, logString.length() - 3);
            if (logString.equals(expectedLogString)) {
                found = true;
            }
        }
        assertTrue(found);


    }


    @Test
    /**
     * Tests for failure case where the given update attribute is invalid.
     */
    public void updateIllnessTest2() {
        donorReceiver.getMasterIllnessList().add(new Illness("SHINGLES", LocalDate.now(), false, true));
        donorReceiver.updateIllness(admin, "SHINGLES", "blah", "false");
        String expected = String.format("ERROR: unknown attribute %s. " +
                "Attribute should be one of 'name', 'date', 'chronic', or 'cured'.", "blah");
        assertEquals(donorReceiver.getUpdateMessage(), expected);
    }


    @Test
    /**
     * Tests for failure case where the given update value is invalid (duplicate).
     */
    public void updateIllnessTest3() {
        donorReceiver.getMasterIllnessList().add(new Illness("SHINGLES", LocalDate.now(), false, true));
        donorReceiver.getMasterIllnessList().add(new Illness("DEMENTIA", LocalDate.now(), false, true));
        donorReceiver.updateIllness(admin, "DEMENTIA", "name", "SHINGLES");
        String expected = String.format("ERROR: Illness with name %s already exists in the diagnoses lists, cannot add duplicate.", "SHINGLES");
        assertEquals(donorReceiver.getUpdateMessage(), expected);
    }


    @Test
    /**
     * Tests for failure case where the given update value is invalid (missing).
     */
    public void updateIllnessTest4() {
        donorReceiver.getMasterIllnessList().add(new Illness("SHINGLES", LocalDate.now(), false, true));
        donorReceiver.updateIllness(admin, "UNLUCKY", "name", "STILL UNLUCKY");
        String expected = "ERROR: Cannot find Illness matching the name " + "UNLUCKY" + ".";
        assertEquals(donorReceiver.getUpdateMessage(), expected);
    }


    /**
     * Tests update birth gender method with a valid character.
     */
    @Test
    public void updateBirthGenderTestWithValidCharacter() {

        donorReceiver.updateBirthGender(admin, "F");
        Assert.assertEquals('F', donorReceiver.getBirthGender());


    }


    /**
     * Tests update birth gender method with an invalid character.
     */
    @Test
    public void updateBirthGenderTestWithInvalidCharacter() {

        donorReceiver.updateBirthGender(admin, "Q");
        Assert.assertEquals('M', donorReceiver.getBirthGender());

    }


    /**
     * Tests update preferred name method with a valid name.
     */
    @Test
    public void updatePreferredNameTestWithValidName() {

        donorReceiver.updatePreferredName(admin, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        Assert.assertEquals("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX", donorReceiver.getPreferredName());


    }

    /**
     * Tests update preferred name method with an invalid name which is too long.
     */
    @Test
    public void updatePreferredNameTestWithTooLongName() {

        donorReceiver.updatePreferredName(admin, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        Assert.assertEquals("Pâtissier", donorReceiver.getPreferredName());

    }


    /**
     * Tests the getAge method with a dead donorReceiver holder. This should return
     * their age on the date of their death.
     */


    /**
     * Tests the getAge method with an donorReceiver holder who died on their
     * birthday.
     */
    @Test
    public void getAgeTestOnBirthday() {

        assertEquals(26, donorReceiver.calculateAge());

    }


    /**
     * Tests the getAge method with an donorReceiver holder who died the day after
     * their birthday.
     */
    @Test
    public void getAgeTestDayAfterBirthday() {

        donorReceiver.setDateOfDeath(LocalDate.of(2017, 1, 1));
        assertEquals(26, donorReceiver.calculateAge());

    }


    /**
     * Tests the getAge method with an donorReceiver holder who died a day before
     * their birthday.
     */
    @Test
    public void getAgeTestDayBeforeBirthday() {

        donorReceiver.setDateOfDeath(LocalDate.of(2016, 12, 30));
        assertEquals(25, donorReceiver.calculateAge());

    }



    @Test
    public void addMedicalProcedureSuccess() {
        ArrayList<String> affectedOrgans = new ArrayList<>();
        affectedOrgans.add("Heart");
        try {
            donorReceiver.addMedicalProcedure(admin, "Appendectomy", "Surgical Removal of the appendix", "20/04/2018", affectedOrgans);
        } catch (Exception ex) {
            if (!(ex instanceof DataFormatException)) {
                throw new Error("expected not to throw", ex);
            }
        }
        assertEquals(1, donorReceiver.getMedicalProcedures().size());
        assertEquals("Appendectomy", donorReceiver.getMedicalProcedures().get(0).getSummary());
        assertEquals("Surgical Removal of the appendix", donorReceiver.getMedicalProcedures().get(0).getDescription());
        assertEquals("20/04/2018", DonorReceiver.formatDateToStringSlash(donorReceiver.getMedicalProcedures().get(0).getDate()));
        assertEquals(affectedOrgans, donorReceiver.getMedicalProcedures().get(0).getAffectedOrgans());
    }

    @Test
    public void failAddMedicalProcedureNonIntegerDate() {
        try {
            ArrayList<String> affectedOrgans = new ArrayList<>();
            donorReceiver.addMedicalProcedure(admin, "Appendectomy", "Surgical removal of the appendix", "12.1/4/2018", affectedOrgans);
        } catch (Exception ex) {
            if (!(ex.getMessage().equals(MedicalProcedure.invalidDateErrorMessage))) {
                throw new Error("expected not to throw", ex);
            }
        }
        assertEquals(0, donorReceiver.getMedicalProcedures().size());
    }

    @Test
    public void failAddMedicalProcedureBadFormatDate() {
        try {
            ArrayList<String> affectedOrgans = new ArrayList<>();
            donorReceiver.addMedicalProcedure(admin, "Appendectomy", "Surgical removal of the appendix", "26-04-2018", affectedOrgans);
        } catch (Exception ex) {
            if (!(ex.getMessage().equals(MedicalProcedure.invalidDateErrorMessage))) {
                throw new Error("expected not to throw", ex);
            }
        }
        assertEquals(0, donorReceiver.getMedicalProcedures().size());
    }

    @Test
    public void failAddMedicalProcedureInvalidDate() {
        try {
            ArrayList<String> affectedOrgans = new ArrayList<>();
            donorReceiver.addMedicalProcedure(admin,"Appendectomy", "Surgical removal of the appendix", "29/02/2018", affectedOrgans);
        } catch (Exception ex) {
            if (!(ex.getMessage().equals(MedicalProcedure.invalidDateErrorMessage))) {
                throw new Error("expected not to throw", ex);
            }
        }
        assertEquals(0, donorReceiver.getMedicalProcedures().size());
    }

    @Test
    public void failAddMedicalProcedureNoDOB() {
        try {
            ArrayList<String> affectedOrgans = new ArrayList<>();
            donorReceiver.setDateOfBirth(null);
            donorReceiver.addMedicalProcedure(admin, "Appendectomy", "Surgical removal of the appendix", "18/02/2018", affectedOrgans);
        } catch (Exception ex) {
            if (!(ex.getMessage().equals(MedicalProcedure.nullDOBErrorMessage))) {
                throw new Error("expected not to throw", ex);
            }
        }
        assertEquals(0, donorReceiver.getMedicalProcedures().size());
    }

    @Test
    public void failAddMedicalProcedureTooEarly() {
        try {
            ArrayList<String> affectedOrgans = new ArrayList<>();
            donorReceiver.addMedicalProcedure(admin, "Appendectomy", "Surgical removal of the appendix", "25/05/1989", affectedOrgans);
        } catch (Exception ex) {
            if (!(ex.getMessage().equals(MedicalProcedure.procedureDateTooEarlyErrorMessage))) {
                throw new Error("expected not to throw", ex);
            }
        }
        assertEquals(0, donorReceiver.getMedicalProcedures().size());
    }

    @Test
    public void failAddMedicalProcedureIncorrectOrganName() {
        try {
            ArrayList<String> affectedOrgans = new ArrayList<>();
            affectedOrgans.add("Haert");
            donorReceiver.addMedicalProcedure(admin, "Appendectomy", "Surgical removal of the appendix", "18/04/2020", affectedOrgans);
        } catch (Exception ex) {
            if (!(ex.getMessage().equals(MedicalProcedure.invalidOrgansErrorMessage))) {
                throw new Error("expected not to throw", ex);
            }
        }
        assertEquals(0, donorReceiver.getMedicalProcedures().size());
    }

    @Test
    public void failAddMedicalProcedureExtraOrgan() {
        try {
            ArrayList<String> affectedOrgans = new ArrayList<>();
            affectedOrgans.add("Brain");
            donorReceiver.addMedicalProcedure(admin, "Appendectomy", "Surgical removal of the appendix", "18/04/2020", affectedOrgans);
        } catch (Exception ex) {
            if (!(ex.getMessage().equals(MedicalProcedure.invalidOrgansErrorMessage))) {
                throw new Error("expected not to throw", ex);
            }
        }
        assertEquals(0, donorReceiver.getMedicalProcedures().size());
    }

    @Test
    public void failAddMedicalProcedureDuplicate() {
        try {
            ArrayList<String> affectedOrgans = new ArrayList<>();
            donorReceiver.addMedicalProcedure(admin, "Appendectomy", "Surgical removal of the appendix", "18/04/2020", affectedOrgans);
            donorReceiver.addMedicalProcedure(admin, "Appendectomy", "Surgical removal of the appendix", "18/04/2020", affectedOrgans);
        } catch (Exception ex) {
            if (!(ex.getMessage().equals(DonorReceiver.procedureAlreadyExistsErrorMessage))) {
                throw new Error("expected not to throw", ex);
            }
        }
        assertEquals(1, donorReceiver.getMedicalProcedures().size());
    }

    @Test
    public void failAddMedicalProcedureDuplicateNullDates() {
        try {
            ArrayList<String> affectedOrgans = new ArrayList<>();
            donorReceiver.addMedicalProcedure(admin, "Appendectomy", "Surgical removal of the appendix", null, affectedOrgans);
            donorReceiver.addMedicalProcedure(admin, "Appendectomy", "Surgical removal of the appendix", null, affectedOrgans);
        } catch (Exception ex) {
            if (!(ex.getMessage().equals(DonorReceiver.procedureAlreadyExistsErrorMessage))) {
                throw new Error("expected not to throw", ex);
            }
        }
        assertEquals(1, donorReceiver.getMedicalProcedures().size());
    }

    @Test
    public void deleteMedicalProcedureSuccess() {
        try {
            ArrayList<String> affectedOrgans = new ArrayList<>();
            donorReceiver.addMedicalProcedure(admin, "Appendectomy", "Surgical removal of the appendix", "22/04/2018", affectedOrgans);
            assertEquals(1, donorReceiver.getMedicalProcedures().size());
            donorReceiver.deleteMedicalProcedure(admin, donorReceiver.getMedicalProcedures().get(0));
            assertEquals(0, donorReceiver.getMedicalProcedures().size());
        } catch (Exception ex) {
            throw new Error("expected not to throw", ex);
        }
    }

    @Test
    public void failDeleteMedicalProcedureNotBelong() {
        try {
            ArrayList<String> affectedOrgans = new ArrayList<>();
            LocalDate dob = LocalDate.of(1996, 5, 26);
            MedicalProcedure heartSurgery = new MedicalProcedure("Heart Surgery", "Surgery performed on the heart", "25/04/2018", dob, affectedOrgans);
            assertEquals(0, donorReceiver.getMedicalProcedures().size());
            donorReceiver.deleteMedicalProcedure(admin, heartSurgery);
            assertEquals(0, donorReceiver.getMedicalProcedures().size());
        } catch (Exception ex) {
            if (!(ex.getMessage().equals(DonorReceiver.procedureDoesNotBelongErrorMessage))) {
                throw new Error("expected not to throw", ex);
            }
        }
    }

    @Test
    public void isDuplicateMedicalProcedureTrueNullDates() {
        try {
            ArrayList<String> affectedOrgans = new ArrayList<>();
            LocalDate date = null;
            donorReceiver.addMedicalProcedure(admin, "Heart Surgery", "Surgery performed on the heart", null, affectedOrgans);
            boolean duplicate = donorReceiver.isDuplicateMedicalProcedure("Heart Surgery", "Surgery performed on the heart", date, affectedOrgans);
            assertTrue(duplicate);
        } catch (Exception ex) {
            throw new Error("expected not to throw", ex);
        }
    }

    @Test
    public void isDuplicateMedicalProcedureTrueSameDates() {
        try {
            ArrayList<String> affectedOrgans = new ArrayList<>();
            LocalDate date = LocalDate.of(2018, 4, 26);
            donorReceiver.addMedicalProcedure(admin, "Heart Surgery", "Surgery performed on the heart", DonorReceiver.formatDateToStringSlash(date), affectedOrgans);
            boolean duplicate = donorReceiver.isDuplicateMedicalProcedure("Heart Surgery", "Surgery performed on the heart", date, affectedOrgans);
            assertTrue(duplicate);
        } catch (Exception ex) {
            throw new Error("expected not to throw", ex);
        }
    }

    @Test
    public void updateMedicalProcedureAllSuccess() {
        try {
            ArrayList<String> affectedOrgans = new ArrayList<>();
            donorReceiver.addMedicalProcedure(admin, "Appendectomy", "Surgical Removal of the appendix", "22/04/2018", affectedOrgans);
            MedicalProcedure toUpdate = donorReceiver.getMedicalProcedures().get(0);
            donorReceiver.updateMedicalProcedure(admin, toUpdate, "Heart Surgery", "Surgery on the heart", "21/04/2018", affectedOrgans);
            assertEquals("Heart Surgery", donorReceiver.getMedicalProcedures().get(0).getSummary());
            assertEquals("21/04/2018", DonorReceiver.formatDateToStringSlash(donorReceiver.getMedicalProcedures().get(0).getDate()));
            assertEquals("Surgery on the heart", donorReceiver.getMedicalProcedures().get(0).getDescription());
        } catch (Exception ex) {
            throw new Error("expected not to throw", ex);
        }
    }

    @Test
    public void failUpdateMedicalProcedureNotBelong() {
        try {
            ArrayList<String> affectedOrgans = new ArrayList<>();
            LocalDate dob = LocalDate.of(1997, 5, 26);
            MedicalProcedure toUpdate = new MedicalProcedure("Appendectomy", "Surgical Removal of the appendix", "22/04/2018", dob, affectedOrgans);
            donorReceiver.updateMedicalProcedure(admin, toUpdate, "Heart Surgery", "Surgery on the heart", "21/04/2018", affectedOrgans);
        } catch (Exception ex) {
            if (!(ex.getMessage().equals(DonorReceiver.procedureDoesNotBelongErrorMessage))) {
                throw new Error("expected not to throw", ex);
            } else {
                assertEquals(DonorReceiver.procedureDoesNotBelongErrorMessage, ex.getMessage());
            }
        }
    }

    @Test
    public void failUpdateMedicalProcedureDuplicate() {
        try {
            ArrayList<String> affectedOrgans = new ArrayList<>();
            donorReceiver.addMedicalProcedure(admin, "Appendectomy", "Surgical Removal of the appendix", "22/04/2018", affectedOrgans);
            MedicalProcedure toUpdate = donorReceiver.getMedicalProcedures().get(0);
            donorReceiver.addMedicalProcedure(admin, "Appendectomy", "Surgical removal of the appendix", null, affectedOrgans);
            donorReceiver.updateMedicalProcedure(admin, toUpdate, "Appendectomy", "Surgical removal of the appendix", null, affectedOrgans);
        } catch (Exception ex) {
            if (!(ex.getMessage().equals(DonorReceiver.procedureAlreadyExistsErrorMessage))) {
                throw new Error("expected not to throw", ex);
            } else {
                assertEquals("22/04/2018", DonorReceiver.formatDateToStringSlash(donorReceiver.getMedicalProcedures().get(0).getDate()));
                assertEquals(DonorReceiver.procedureAlreadyExistsErrorMessage, ex.getMessage());
            }
        }
    }

    @Test
    public void failUpdateMedicalProcedureTooEarly() {
        try {
            ArrayList<String> affectedOrgans = new ArrayList<>();
            donorReceiver.addMedicalProcedure(admin, "Appendectomy", "Surgical Removal of the appendix", "22/04/2018", affectedOrgans);
            MedicalProcedure toUpdate = donorReceiver.getMedicalProcedures().get(0);
            donorReceiver.updateMedicalProcedure(admin, toUpdate, "Heart Surgery", "Surgery on the heart", "21/04/1979", affectedOrgans);
            assertEquals("Heart Surgery", donorReceiver.getMedicalProcedures().get(0).getSummary());
            assertEquals("21/04/2018", DonorReceiver.formatDateToStringSlash(donorReceiver.getMedicalProcedures().get(0).getDate()));
            assertEquals("Surgery on the heart", donorReceiver.getMedicalProcedures().get(0).getDescription());
        } catch (Exception ex) {
            if (!(ex.getMessage().equals(MedicalProcedure.procedureDateTooEarlyErrorMessage))) {
                throw new Error("expected not to throw", ex);
            } else {
                assertEquals("Appendectomy", donorReceiver.getMedicalProcedures().get(0).getSummary());
                assertEquals("22/04/2018", DonorReceiver.formatDateToStringSlash(donorReceiver.getMedicalProcedures().get(0).getDate()));
                assertEquals("Surgical Removal of the appendix", donorReceiver.getMedicalProcedures().get(0).getDescription());
                assertEquals(MedicalProcedure.procedureDateTooEarlyErrorMessage, ex.getMessage());
            }
        }
    }

    @Test
    public void updateMedicalProcedureOrgansSuccess() {
        try {
            ArrayList<String> affectedOrgans = new ArrayList<>();
            donorReceiver.addMedicalProcedure(admin, "Appendectomy", "Surgical Removal of the appendix", "22/04/2018", affectedOrgans);
            MedicalProcedure toUpdate = donorReceiver.getMedicalProcedures().get(0);
            affectedOrgans.add("Heart");
            donorReceiver.updateMedicalProcedure(admin, toUpdate,"Appendectomy", "Surgical Removal of the appendix", "22/04/2018", affectedOrgans);
            assertEquals("Appendectomy", donorReceiver.getMedicalProcedures().get(0).getSummary());
            assertEquals("22/04/2018", DonorReceiver.formatDateToStringSlash(donorReceiver.getMedicalProcedures().get(0).getDate()));
            assertEquals("Surgical Removal of the appendix", donorReceiver.getMedicalProcedures().get(0).getDescription());
            assertEquals("Heart", donorReceiver.getMedicalProcedures().get(0).getAffectedOrgans().get(0));
            assertEquals(1, donorReceiver.getMedicalProcedures().get(0).getAffectedOrgans().size());
            String expectedLog = new LogEntry(donorReceiver, admin,
                    "Affected Organs of medical procedure 'Appendectomy'", "[]", "[Heart]").toString();
            expectedLog = expectedLog.substring(0, expectedLog.length() - 3);
            boolean found = false;
            for (LogEntry log: donorReceiver.getUpDateLog()) {
                String logString = log.toString();
                logString = logString.substring(0, logString.length() - 3);
                if (logString.equals(expectedLog)) {
                    found = true;
                }
            }
            assertTrue(found);
        } catch (Exception ex) {
            throw new Error("expected not to throw", ex);
        }
    }

    @Test
    public void extractPastProceduresSuccessNotEmpty() {
        try {
            ArrayList<String> affectedOrgans = new ArrayList<>();
            donorReceiver.addMedicalProcedure(admin, "Appendectomy", "Surgical Removal of the appendix", "22/04/2018", affectedOrgans);
            donorReceiver.addMedicalProcedure(admin, "Appendectomy 2", "Surgical Removal of the appendix", null, affectedOrgans);
            String monthAwayString = DonorReceiver.formatDateToStringSlash(LocalDate.now().plusMonths(1));
            donorReceiver.addMedicalProcedure(admin, "Appendectomy 3", "Surgical Removal of the appendix", monthAwayString, affectedOrgans);
            ArrayList<MedicalProcedure> past = donorReceiver.extractPastProcedures();
            assertEquals(1, past.size());
            assertEquals("Appendectomy", past.get(0).getSummary());
        } catch (Exception ex) {
            throw new Error("expected not to throw", ex);
        }
    }

    @Test
    public void extractPendingProceduresSuccessNotEmpty() {
        try {
            ArrayList<String> affectedOrgans = new ArrayList<>();
            donorReceiver.addMedicalProcedure(admin, "Appendectomy", "Surgical Removal of the appendix", "22/04/2018", affectedOrgans);
            donorReceiver.addMedicalProcedure(admin, "Appendectomy 2", "Surgical Removal of the appendix", null, affectedOrgans);
            String monthAwayString = DonorReceiver.formatDateToStringSlash(LocalDate.now().plusMonths(1));
            donorReceiver.addMedicalProcedure(admin, "Appendectomy 3", "Surgical Removal of the appendix", monthAwayString, affectedOrgans);
            ArrayList<MedicalProcedure> pending = donorReceiver.extractPendingProcedures();
            assertEquals(2, pending.size());
            assertEquals("Appendectomy 2", pending.get(0).getSummary());
            assertEquals("Appendectomy 3", pending.get(1).getSummary());
        } catch (Exception ex) {
            throw new Error("expected not to throw", ex);
        }
    }

    @Test
    public void extractPastProceduresSuccessNonePast() {
        try {
            ArrayList<String> affectedOrgans = new ArrayList<>();
            donorReceiver.addMedicalProcedure(admin, "Appendectomy 2", "Surgical Removal of the appendix", null, affectedOrgans);
            String monthAwayString = DonorReceiver.formatDateToStringSlash(LocalDate.now().plusMonths(1));
            donorReceiver.addMedicalProcedure(admin, "Appendectomy 3", "Surgical Removal of the appendix", monthAwayString, affectedOrgans);
            ArrayList<MedicalProcedure> past = donorReceiver.extractPastProcedures();
            assertEquals(0, past.size());
        } catch (Exception ex) {
            throw new Error("expected not to throw", ex);
        }
    }

    @Test
    public void extractPendingProceduresSuccessNonePending() {
        try {
            ArrayList<String> affectedOrgans = new ArrayList<>();
            donorReceiver.addMedicalProcedure(admin, "Appendectomy", "Surgical Removal of the appendix", "22/04/2018", affectedOrgans);
            ArrayList<MedicalProcedure> pending = donorReceiver.extractPendingProcedures();
            assertEquals(0, pending.size());
        } catch (Exception ex) {
            throw new Error("expected not to throw", ex);
        }
    }
}
