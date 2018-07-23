package seng302.model;

import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import seng302.model.person.DonorReceiver;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class DrugInteractionsTest {
    DrugInteractions drugInteractions;

    @Before
    public void setup() {
        drugInteractions = new DrugInteractions();
    }


    /**
     * Tests that the drug interactions are returned.
     */
    @Ignore
    @Test
    public void getDrugInteractions() {
        try {
            JSONObject interactions = drugInteractions.getDrugInteractions("hydrochlorothiazide", "Lisinopril");
        } catch (IOException | ParseException e) {
            System.out.println("Error in getting drug interactions.");
            e.printStackTrace();
        }
    }


    /**
     * Tests that the customised drug data was received without error
     * @throws IOException An input output error raised by the call to the api
     * @throws ParseException A parse exception thrown by the json object parser
     */
    @Ignore
    @Test
    public void getCustomisedDrugInteractions() throws IOException, ParseException {
        DonorReceiver donorReceiver = new DonorReceiver("Susan", "Marie", "Pevensie", LocalDate.ofYearDay(1992, 287), "nar8267");
        DrugInteractions drugInteractions = new DrugInteractions();
        ArrayList<ArrayList<String>> interactions = drugInteractions.getCustomisedDrugInteractions(donorReceiver, "hydrochlorothiazide", "Lisinopril");
        System.out.println(interactions);
        assertTrue(interactions.size() > 0);
    }

}