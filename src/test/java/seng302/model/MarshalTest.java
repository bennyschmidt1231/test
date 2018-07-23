package seng302.model;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import seng302.model.person.DonorReceiver;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MarshalTest {

    private DonorReceiver donorReceiver;
    private UserAttributeCollection userAttCol;
    private DonorOrganInventory donOrgInv;
    private ReceiverOrganInventory reqOrgans;
    private Map<String, DonorReceiver> donorReceivers;


    /**
     * Populating an donorReceiver with values to test.
     */
    @Before
    public void setUp() {

        LocalDate DOB = LocalDate.of(1990, 12, 31);
        LocalDate DOD = LocalDate.of(2016, 12, 31);
        LocalDateTime organUpdateTime = LocalDate.of(2018,5,3).atTime(14,15);

        userAttCol = new UserAttributeCollection(2.0, 51.0, "AB-", false,  "",
                false, 2.0, "None");

        donOrgInv = new DonorOrganInventory(true, false, true, true,
                false, false, true,
                false, true, false, true, true);

        reqOrgans = new ReceiverOrganInventory(true,false,false,false,false,false,false,false,false,false,false,false,organUpdateTime,organUpdateTime,organUpdateTime,organUpdateTime,organUpdateTime,organUpdateTime,organUpdateTime,organUpdateTime,organUpdateTime,organUpdateTime,organUpdateTime,organUpdateTime);

        //TODO
        //contacts = new AccountContacts("1 Fleet Street", "Christchurch", "Canterbury", "5678", "0220456543", "5452345", "randomPerson92@gmail.com",
        //        "31b Taylors Ave", "Christchurch", "Canterbury", "8052", "0213459876", "5458769", "randomHelper93@yahoo.com");;

        donorReceiver = new DonorReceiver("Sweeny", "", "Todd", DOB, "ABC1234");
        donorReceiver.setUserAttributeCollection(userAttCol);
        donorReceiver.setDonorOrganInventory(donOrgInv);
        donorReceiver.setRequiredOrgans(reqOrgans);
        //donorReceiver.setContactDetails(contacts);
        donorReceiver.setTitle("MR");
        donorReceiver.setPreferredName("Pâtissier");
        donorReceiver.setDateOfDeath(DOD);
        donorReceiver.setGender('M');
        donorReceiver.setBirthGender('M');
        donorReceiver.setLivedInUKFlag(false);
        donorReceivers = new LinkedHashMap<String, DonorReceiver>();
        donorReceivers.put(donorReceiver.getUserName(), donorReceiver);

    }


    /**
     * Calls the fileExists method for a file which does not exist.
     * Should return false.
     */
    @Test
    public void fileDoesNotExistTest() {

        assertFalse(Marshal.fileExists("fileDoesNotExistTest"));

    }

    /**
     * Calls the fileExists method for a file which does exist.
     * Should return true. Will return false if an exception occurs.
     */
    @Test
    public void fileExistsTest() {

        try {

            File file = new File("fileExistsTest.json");
            file.createNewFile();
            assertTrue(Marshal.fileExists("fileExistsTest"));

        } catch (IOException exception) {

            assertTrue(false);

        }

    }


    /**
     * Attempts to import donorReceivers from a valid file.
     */
    @Ignore
    @Test
    public void importExportTest() {

        try {
            Marshal.exportAccounts(this.donorReceivers);
            this.donorReceivers.clear(); // Clear donorReceivers ArrayList.
            donorReceivers = Marshal.importAccounts();

            for (int index = 0; index < donorReceivers.size(); index++) {

                DonorReceiver importedDonorReceiver = donorReceivers.get(index);

                if (importedDonorReceiver.getUserName().equals(donorReceiver.getUserName())) {

                    assertTrue(importedDonorReceiver.toString().equals(donorReceiver.toString()));
                    return;

                }

            }

            assertTrue(false);
            return;

        } catch (IOException exception) {

            assertTrue(false);

        }
    }





    
}
