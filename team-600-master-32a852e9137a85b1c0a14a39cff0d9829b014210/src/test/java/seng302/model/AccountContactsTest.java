///*
////TODO
//
//package seng302.model;
//
//import org.junit.Before;
//import org.junit.Ignore;
//import org.junit.Test;
//import seng302.model.*;
//
//import java.io.ByteArrayOutputStream;
//import java.time.LocalDate;
//import java.util.ArrayList;
//
//import static org.junit.Assert.assertEquals;
//
//
//public class AccountContactsTest {
//
//    private UserAttributeCollection userAttCol;
//    private DonorOrganInventory donOrgInv;
//    private ReceiverOrganInventory reqOrgans;
//    private ContactDetails contacts;
//    private AccountManager accMan;
//    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//
//    @Before
//    public void setUp() {
//
//
//        accMan = new AccountManager(){};
//
//        LocalDate DOB = LocalDate.of(1990, 12, 31);
//        LocalDate DOD = LocalDate.of(2016, 12, 31);
//
//        userAttCol = new UserAttributeCollection(2.0, 51.0, "AB-", "1 Fleet Street",
//                "", "cashmere", "christchurch", 5678, "NZ", false, "130/80", false, 2.0, "");
//
//        donOrgInv = new DonorOrganInventory(true, false, true, true,
//                false, false, true,
//                false, true, false, true, true);
//
//        //TODO
//        //contacts = new AccountContacts("1 Fleet Street", "Christchurch", "Canterbury", "5678", "0220456543", "5452345", "randomPerson92@gmail.com",
//        //        "31b Taylors Ave", "Christchurch", "Canterbury", "8052", "0213459876", "5458769", "randomHelper93@yahoo.com");
//
//
//
//        DonorReceiver donorReceiver1 = new DonorReceiver("Sweeny", "", "Todd", DOB, "ABC1234");
//        donorReceiver1.setUserAttributeCollection(userAttCol);
//        donorReceiver1.setDonorOrganInventory(donOrgInv);
//        donorReceiver1.setRequiredOrgans(reqOrgans);
//        donorReceiver1.setContactDetails(contacts);
//        donorReceiver1.setTitle("MR");
//        donorReceiver1.setPreferredName("Pâtissier");
//        donorReceiver1.setDateOfDeath(DOD);
//        donorReceiver1.setGender('M');
//        donorReceiver1.setBirthGender('M');
//        donorReceiver1.setLivedInUKFlag(false);
//       DonorReceiver donorReceiver2 = new DonorReceiver("Sweeny", "", "Todd", DOB, "ABC1264");
//        donorReceiver2.setUserAttributeCollection(userAttCol);
//        donorReceiver2.setDonorOrganInventory(donOrgInv);
//        donorReceiver2.setRequiredOrgans(reqOrgans);
//        donorReceiver2.setContactDetails(contacts);
//        donorReceiver1.setTitle("MR");
//        donorReceiver2.setPreferredName("Pâtissier");
//        donorReceiver2.setDateOfDeath(DOD);
//        donorReceiver2.setGender('M');
//        donorReceiver2.setBirthGender('M');
//        donorReceiver2.setLivedInUKFlag(false);
//        DonorReceiver donorReceiver3 = new DonorReceiver("Sweeny", "", "Todd", DOB, "ABC1274");
//        donorReceiver3.setUserAttributeCollection(userAttCol);
//        donorReceiver3.setDonorOrganInventory(donOrgInv);
//        donorReceiver3.setRequiredOrgans(reqOrgans);
//        donorReceiver3.setContactDetails(contacts);
//        donorReceiver1.setTitle("MR");
//        donorReceiver3.setPreferredName("Pâtissier");
//        donorReceiver3.setDateOfDeath(DOD);
//        donorReceiver3.setGender('M');
//        donorReceiver3.setBirthGender('M');
//        donorReceiver3.setLivedInUKFlag(false);
//        accMan.getDonorReceivers().add(donorReceiver1);
//        accMan.getDonorReceivers().add(donorReceiver2);
//        accMan.getDonorReceivers().add(donorReceiver3);
//    }
//
//    /**
//     * Testing whether the updateAddressStreet works with valid input
//     */
//
///*
//
//
//@Test
//    public void updateAddressStreet1() {
//        accMan.issueCommand("update", "ABC1234", "contacts", "addressStreet", "27 Peters Road");
//        ArrayList<DonorReceiver> test = accMan.getDonorReceivers();
//        DonorReceiver test2 = test.get(0);
//        ContactDetails test3 = test2.getContactDetails();
//        assertEquals(test3.getAddress().getStreetAddressLn1(), "27 Peters Road");
//    }
//
//    /**
//     * Testing whether updateAddressStreet fails with invalid input
//
//    @Test
//    public void updateAddressStreet2() {
//        accMan.issueCommand("update", "ABC1234", "contacts", "addressStreet", "");
//        ArrayList<DonorReceiver> test = accMan.getDonorReceivers();
//        DonorReceiver test2 = test.get(0);
//        ContactDetails test3 = test2.getContactDetails();
//        assertEquals(test3.getAddress().getStreetAddressLn2(), "1 Fleet Street");
//    }
//
//    /**
//     * Testing whether the updateAddressCity works with valid input
//
//    @Test
//    public void updateAddressCity1() {
//        accMan.issueCommand("update", "ABC1234", "contacts", "addressCity", "Nelson");
//        ArrayList<DonorReceiver> test = accMan.getDonorReceivers();
//        DonorReceiver test2 = test.get(0);
//        ContactDetails test3 = test2.getContactDetails();
//        assertEquals(test3.getAddress().getCityName(), "Nelson");
//    }
//
//    /**
//     * Testing whether updateAddressCity fails with invalid input
//
//    @Test
//    public void updateAddressCity2() {
//        accMan.issueCommand("update", "ABC1234", "contacts", "addressCity", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//        ArrayList<DonorReceiver> test = accMan.getDonorReceivers();
//        DonorReceiver test2 = test.get(0);
//        AccountContacts test3 = test2.getContactDetails();
//        assertEquals(test3.getAddressCity(), "Christchurch");
//    }
//
//    /**
//     * Testing whether the updateAddressRegion works with valid input
//
//    @Test
//    public void updateAddressRegion1() {
//        accMan.issueCommand("update", "ABC1234", "contacts", "addressRegion", "Buller");
//        ArrayList<DonorReceiver> test = accMan.getDonorReceivers();
//        DonorReceiver test2 = test.get(0);
//        AccountContacts test3 = test2.getContactDetails();
//        assertEquals(test3.getAddressRegion(), "Buller");
//    }
//
//    /**
//     * Testing whether updateAddressRegion fails with invalid input
//
//    @Test
//    public void updateAddressRegion2() {
//        accMan.issueCommand("update", "ABC1234", "contacts", "addressRegion", "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
//        ArrayList<DonorReceiver> test = accMan.getDonorReceivers();
//        DonorReceiver test2 = test.get(0);
//        AccountContacts test3 = test2.getContactDetails();
//        assertEquals(test3.getAddressRegion(), "Canterbury");
//    }
//
//    /**
//     * Testing whether the updateAddressPostcode works with valid input
//
//    @Test
//    public void updateAddressPostcode1() {
//        accMan.issueCommand("update", "ABC1234", "contacts", "addressPostcode", "8541");
//        ArrayList<DonorReceiver> test = accMan.getDonorReceivers();
//        DonorReceiver test2 = test.get(0);
//        AccountContacts test3 = test2.getContactDetails();
//        assertEquals(test3.getAddressPostcode(), "8541");
//    }
//
//    /**
//     * Testing whether updateAddressPostcode fails with invalid input
//     */
//    @Test
//    public void updateAddressPostCode2() {
//        accMan.issueCommand("update", "ABC1234", "contacts", "addressPostcode", "27 Peters Road");
//        ArrayList<DonorReceiver> test = accMan.getDonorReceivers();
//        DonorReceiver test2 = test.get(0);
//        AccountContacts test3 = test2.getContactDetails();
//        assertEquals(test3.getAddressPostcode(), "5678");
//    }
//
//    /**
//     * Testing whether the updateMobileNumber works with valid input
//     */
//    @Test
//    public void updateMobileNumber1() {
//        accMan.issueCommand("update", "ABC1234", "contacts", "mobileNumber", "0220867417");
//        ArrayList<DonorReceiver> test = accMan.getDonorReceivers();
//        DonorReceiver test2 = test.get(0);
//        AccountContacts test3 = test2.getContactDetails();
//        assertEquals(test3.getMobileNumber(), "0220867417");
//    }
//
//    /**
//     * Testing whether updateMobileNumber fails with invalid input
//     */
//    @Test
//    public void updateMobileNumber2() {
//        accMan.issueCommand("update", "ABC1234", "contacts", "mobileNumber", "Hello");
//        ArrayList<DonorReceiver> test = accMan.getDonorReceivers();
//        DonorReceiver test2 = test.get(0);
//        AccountContacts test3 = test2.getContactDetails();
//        assertEquals(test3.getMobileNumber(), "0220456543");
//    }
//
//    /**
//     * Testing whether the updateHomeNumber works with valid input
//     */
//    @Test
//    public void updateHomeNumber1() {
//        accMan.issueCommand("update", "ABC1234", "contacts", "homeNumber", "5458978");
//        ArrayList<DonorReceiver> test = accMan.getDonorReceivers();
//        DonorReceiver test2 = test.get(0);
//        AccountContacts test3 = test2.getContactDetails();
//        assertEquals(test3.getHomeNumber(), "5458978");
//    }
//
//    /**
//     * Testing whether updateHomeNumber fails with invalid input
//     */
//    @Test
//    public void updateHomeNumber2() {
//        accMan.issueCommand("update", "ABC1234", "contacts", "homeNumber", "Hello");
//        ArrayList<DonorReceiver> test = accMan.getDonorReceivers();
//        DonorReceiver test2 = test.get(0);
//        AccountContacts test3 = test2.getContactDetails();
//        assertEquals(test3.getHomeNumber(), "5452345");
//    }
//
//    /**
//     * Testing whether the updateEmail works with valid input
//     */
//    @Test
//    public void updateEmail1() {
//        accMan.issueCommand("update", "ABC1234", "contacts", "email", "randomPerson94@gmail.com");
//        ArrayList<DonorReceiver> test = accMan.getDonorReceivers();
//        DonorReceiver test2 = test.get(0);
//        AccountContacts test3 = test2.getContactDetails();
//        assertEquals(test3.getEmail(), "randomPerson94@gmail.com");
//    }
//
//    /**
//     * Testing whether updateEmail fails with invalid input
//     */
//    @Test
//    public void updateEmail2() {
//        accMan.issueCommand("update", "ABC1234", "contacts", "email", "Hello");
//        ArrayList<DonorReceiver> test = accMan.getDonorReceivers();
//        DonorReceiver test2 = test.get(0);
//        AccountContacts test3 = test2.getContactDetails();
//        assertEquals(test3.getEmail(), "randomPerson92@gmail.com");
//    }
//
//    /**
//     * Testing whether the updateEmergAddressStreet works with valid input
//     */
//    @Test
//    public void updateEmergAddressStreet1() {
//        accMan.issueCommand("update", "ABC1234", "contacts", "emergAddressStreet", "27 Peters Road");
//        ArrayList<DonorReceiver> test = accMan.getDonorReceivers();
//        DonorReceiver test2 = test.get(0);
//        AccountContacts test3 = test2.getContactDetails();
//        assertEquals(test3.getEmergAddressStreet(), "27 Peters Road");
//    }
//
//    /**
//     * Testing whether updateEmergAddressStreet fails with invalid input
//     */
//    @Test
//    public void updateEmergAddressStreet2() {
//        accMan.issueCommand("update", "ABC1234", "contacts", "emergAddressStreet", "");
//        ArrayList<DonorReceiver> test = accMan.getDonorReceivers();
//        DonorReceiver test2 = test.get(0);
//        AccountContacts test3 = test2.getContactDetails();
//        assertEquals(test3.getEmergAddressStreet(), "31b Taylors Ave");
//    }
//
//    /**
//     * Testing whether the updateEmergAddressCity works with valid input
//     */
//    @Test
//    public void updateEmergAddressCity1() {
//        accMan.issueCommand("update", "ABC1234", "contacts", "emergAddressCity", "Nelson");
//        ArrayList<DonorReceiver> test = accMan.getDonorReceivers();
//        DonorReceiver test2 = test.get(0);
//        AccountContacts test3 = test2.getContactDetails();
//        assertEquals(test3.getEmergAddressCity(), "Nelson");
//    }
//
//    /**
//     * Testing whether updateEmergAddressCity fails with invalid input
//     */
//    @Test
//    public void updateEmergAddressCity2() {
//        accMan.issueCommand("update", "ABC1234", "contacts", "emergAddressCity", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//        ArrayList<DonorReceiver> test = accMan.getDonorReceivers();
//        DonorReceiver test2 = test.get(0);
//        AccountContacts test3 = test2.getContactDetails();
//        assertEquals(test3.getEmergAddressCity(), "Christchurch");
//    }
//
//    /**
//     * Testing whether the updateEmergAddressRegion works with valid input
//     */
//    @Test
//    public void updateEmergAddressRegion1() {
//        accMan.issueCommand("update", "ABC1234", "contacts", "emergAddressRegion", "Buller");
//        ArrayList<DonorReceiver> test = accMan.getDonorReceivers();
//        DonorReceiver test2 = test.get(0);
//        AccountContacts test3 = test2.getContactDetails();
//        assertEquals(test3.getEmergAddressRegion(), "Buller");
//    }
//
//    /**
//     * Testing whether updateEmergAddressRegion fails with invalid input
//     */
//    @Test
//    public void updateEmergAddressRegion2() {
//        accMan.issueCommand("update", "ABC1234", "contacts", "emergAddressRegion", "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
//        ArrayList<DonorReceiver> test = accMan.getDonorReceivers();
//        DonorReceiver test2 = test.get(0);
//        AccountContacts test3 = test2.getContactDetails();
//        assertEquals(test3.getEmergAddressRegion(), "Canterbury");
//    }
//
//    /**
//     * Testing whether the updateEmergAddressPostcode works with valid input
//     */
//    @Test
//    public void updateEmergAddressPostcode1() {
//        accMan.issueCommand("update", "ABC1234", "contacts", "emergAddressPostcode", "8541");
//        ArrayList<DonorReceiver> test = accMan.getDonorReceivers();
//        DonorReceiver test2 = test.get(0);
//        AccountContacts test3 = test2.getContactDetails();
//        assertEquals(test3.getEmergAddressPostcode(), "8541");
//    }
//
//    /**
//     * Testing whether updateEmergAddressPostcode fails with invalid input
//     */
//    @Test
//    public void updateEmergAddressPostcode2() {
//        accMan.issueCommand("update", "ABC1234", "contacts", "emergAddressPostcode", "27 Peters Road");
//        ArrayList<DonorReceiver> test = accMan.getDonorReceivers();
//        DonorReceiver test2 = test.get(0);
//        AccountContacts test3 = test2.getContactDetails();
//        assertEquals(test3.getEmergAddressPostcode(), "8052");
//    }
//
//    /**
//     * Testing whether the updateEmergMobileNumber works with valid input
//     */
//    @Test
//    public void updateEmergMobileNumber1() {
//        accMan.issueCommand("update", "ABC1234", "contacts", "emergMobileNumber", "0220867417");
//        ArrayList<DonorReceiver> test = accMan.getDonorReceivers();
//        DonorReceiver test2 = test.get(0);
//        AccountContacts test3 = test2.getContactDetails();
//        assertEquals(test3.getEmergMobileNumber(), "0220867417");
//    }
//
//    /**
//     * Testing whether updateEmergMobileNumber fails with invalid input
//     */
//    @Test
//    public void updateEmergMobileNumber2() {
//        accMan.issueCommand("update", "ABC1234", "contacts", "emergMobileNumber", "Hello");
//        ArrayList<DonorReceiver> test = accMan.getDonorReceivers();
//        DonorReceiver test2 = test.get(0);
//        AccountContacts test3 = test2.getContactDetails();
//        assertEquals(test3.getEmergMobileNumber(), "0213459876");
//    }
//
//    /**
//     * Testing whether the updateEmergHomeNumber works with valid input
//     */
//    @Test
//    public void updateEmergHomeNumber1() {
//        accMan.issueCommand("update", "ABC1234", "contacts", "emergHomeNumber", "5458978");
//        ArrayList<DonorReceiver> test = accMan.getDonorReceivers();
//        DonorReceiver test2 = test.get(0);
//        AccountContacts test3 = test2.getContactDetails();
//        assertEquals(test3.getEmergHomeNumber(), "5458978");
//    }
//
//    /**
//     * Testing whether updateEmergHomeNumber fails with invalid input
//     */
//    @Test
//    public void updateEmergHomeNumber2() {
//        accMan.issueCommand("update", "ABC1234", "contacts", "emergHomeNumber", "Hello");
//        ArrayList<DonorReceiver> test = accMan.getDonorReceivers();
//        DonorReceiver test2 = test.get(0);
//        AccountContacts test3 = test2.getContactDetails();
//        assertEquals(test3.getEmergHomeNumber(), "5458769");
//    }
//
//    /**
//     * Testing whether the updateEmergEmail works with valid input
//     */
//    @Test
//    public void updateEmergEmail1() {
//        accMan.issueCommand("update", "ABC1234", "contacts", "emergEmail", "randomHelper1@gmail.com");
//        ArrayList<DonorReceiver> test = accMan.getDonorReceivers();
//        DonorReceiver test2 = test.get(0);
//        AccountContacts test3 = test2.getContactDetails();
//        assertEquals(test3.getEmergEmail(), "randomHelper1@gmail.com");
//    }
//
//    /**
//     * Testing whether updateEmergEmail fails with invalid input
//     */
//    @Test
//    public void updateEmergEmail2() {
//        accMan.issueCommand("update", "ABC1234", "contacts", "emergEmail", "Hello");
//        ArrayList<DonorReceiver> test = accMan.getDonorReceivers();
//        DonorReceiver test2 = test.get(0);
//        AccountContacts test3 = test2.getContactDetails();
//        assertEquals(test3.getEmergEmail(), "randomHelper93@yahoo.com");
//    }
//
//
//
//}
//*/
