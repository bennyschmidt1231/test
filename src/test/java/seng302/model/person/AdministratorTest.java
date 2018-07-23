package seng302.model.person;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.Assert.*;

/** Only testing the creation of an administrator **/
public class AdministratorTest {

    @Test
    public void testConstructor() {
        Administrator admin = new Administrator("Test", "Help", "Testing", "tester", "password");
        assertEquals("Test", admin.getFirstName());
        assertEquals("Help", admin.getMiddleName());
        assertEquals("Testing", admin.getLastName());
        assertEquals("tester", admin.getUserName());
        assertEquals("password", admin.getPassword());
        assertEquals("Sudo", Administrator.DEFAULT);
    }

    @Test
    public void testJSONConstructor() {
        Address address = new Address("20 Kirkwood Ave", "Erskine Building", "Ilam", "Christchurch", "Canterbury", "8041", "NZ");
        ContactDetails details = new ContactDetails(address, "0221234545", "031234545", "help@test.com");
        LocalDateTime creation = LocalDateTime.now();
        ArrayList<LogEntry> logs = new ArrayList<>();
        Administrator admin = new Administrator("TestJson", "HelpJson", "TestingJson", details, "testerJson", "password", creation, logs);
        assertEquals("TestJson", admin.getFirstName());
        assertEquals("HelpJson", admin.getMiddleName());
        assertEquals("TestingJson", admin.getLastName());
        assertEquals("20 Kirkwood Ave", admin.getContactDetails().getAddress().getStreetAddressLn1());
        assertEquals("Erskine Building", admin.getContactDetails().getAddress().getStreetAddressLn2());
        assertEquals("Ilam", admin.getContactDetails().getAddress().getSuburbName());
        assertEquals("Christchurch", admin.getContactDetails().getAddress().getCityName());
        assertEquals("Canterbury", admin.getContactDetails().getAddress().getRegion());
        assertEquals("8041", admin.getContactDetails().getAddress().getPostCode());
        assertEquals("NZ", admin.getContactDetails().getAddress().getCountryCode());
        assertEquals("0221234545", admin.getContactDetails().getMobileNum());
        assertEquals("031234545", admin.getContactDetails().getHomeNum());
        assertEquals("help@test.com", admin.getContactDetails().getEmail());
        assertEquals("testerJson", admin.getUserName());
        assertEquals("password", admin.getPassword());
        assertEquals(creation, admin.getCreationDate());
        assertEquals(logs, admin.getModifications());
    }
}
