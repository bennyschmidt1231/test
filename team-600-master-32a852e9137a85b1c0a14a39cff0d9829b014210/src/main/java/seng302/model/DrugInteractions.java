package seng302.model;

import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import seng302.model.person.DonorReceiver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class DrugInteractions {


    /**
     * Gets the drug interaction information from the eHealthMe api and parses it into a JSONObject.
     * @param drug1 A drug to be tested for interactions.
     * @param drug2 A drug to be tested for interactions.
     * @return A JSONObject of the information returned by the api.
     * @throws IOException Thrown if there is a problem with communicating with the api.
     * @throws ParseException Thrown if there is a problem reading the information given by the api.
     */
     public JSONObject getDrugInteractions(String drug1, String drug2) throws IOException, ParseException {
        String urlString = "https://www.ehealthme.com/api/v1/drug-interaction/" + drug1.toLowerCase() + "/" + drug2.toLowerCase() + "/";
        URL url = new URL(urlString);

        HttpURLConnection connection  = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();

        if (responseCode != 200) {
            JSONObject obj = new JSONObject();
            obj.put("Error", "Error in processing the api request " + responseCode);
            return obj;
        }
        else {
            BufferedReader input = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            String inputLine;
            StringBuffer responseString = new StringBuffer();

            while ((inputLine = input.readLine()) != null) {
               responseString.append(inputLine);
            }

            input.close();

            JSONObject jobject = new JSONObject(responseString.toString());
            jobject.put("Error", "None");

            return jobject;
        }
    }


    /**
     * Uses the information given by the api (see getDrugInteractions function)
     * to give a customised set of drug interactions for a specific account.
     * @param donorDonorReceiver The account for which the specification is to occur.
     * @param drug1 A drug to be checked for interactions.
     * @param drug2 A drug to be checked for interactions.
     * @return An ArrayList of ArrayLists containing a symptom and the time that the symptom is likely to occur
     * @throws IOException Thrown if there is a problem with communicating with the api.
     * @throws ParseException Thrown if there is a problem reading the information given by the api.
     */
    public ArrayList<ArrayList<String>> getCustomisedDrugInteractions(DonorReceiver donorDonorReceiver, String drug1, String drug2) throws IOException, ParseException {
        ArrayList<ArrayList<String>> interactions = new ArrayList<>();
        Set<Object> dInteractions = new HashSet<>();
        JSONObject drugInteractions = getDrugInteractions(drug1, drug2);

        if (drugInteractions.get("Error") != "None") {
            System.out.println(drugInteractions.get("Error"));
            return new ArrayList<ArrayList<String>>();
        }

        Integer donorAge = donorDonorReceiver.calculateAge();

        //Checks the age of the donor account for the symptoms of people their age
        JSONObject ageArray = drugInteractions.getJSONObject("age_interaction");
        String ageString;

        if (10 <= donorAge && donorAge <= 19) {
            ageString = "10-19";
        } else if (20 <= donorAge && donorAge <= 29) {
            ageString = "20-29";
        } else if (30 <= donorAge && donorAge <= 39) {
            ageString = "30-39";
        } else if (40 <= donorAge && donorAge <= 49) {
            ageString = "40-49";
        } else if (50 <= donorAge && donorAge <= 59) {
            ageString = "50-59";
        } else if (60 <= donorAge) {
            ageString = "60+";
        } else {
            ageString = "nan";
        }

        for (Object symptoms : ageArray.getJSONArray(ageString)) {
            dInteractions.add(symptoms.toString());
        }

        //Checks the gender of the donor account for the symptoms of people their gender
        JSONObject genderArray = drugInteractions.getJSONObject("gender_interaction");
        boolean male = false;
        boolean female = false;

        if (donorDonorReceiver.getGender() == 'M' || donorDonorReceiver.getGender() == 'm') {
            male = true;
        } else if (donorDonorReceiver.getGender() == 'F' || donorDonorReceiver.getGender() == 'f') {
            female = true;
        } else {
            male = true;
            female = true;
        }

        if (male) {
            for (Object symptoms : genderArray.getJSONArray("male")) {
                dInteractions.add(symptoms.toString());
            }
        }
        if (female) {
            for (Object symptoms : genderArray.getJSONArray("female")) {
                dInteractions.add(symptoms.toString());
            }
        }

        //Gives the duration that the symptoms are experienced and adds them to
        JSONObject timeArray = drugInteractions.getJSONObject("duration_interaction");

        boolean found;

        for (Object symptom : dInteractions) {
            ArrayList<String> symptomsAndTime = new ArrayList<String>();
            symptomsAndTime.add(symptom.toString());

            found = false;

            for (int i = 0; i < 10 && !found; i++) {
                if (timeArray.getJSONArray("1 - 2 years").get(i).equals(symptom)) {
                    symptomsAndTime.add("1 - 2 Years");
                    found = true;
                } else if (timeArray.getJSONArray("1 - 6 months").get(i).equals(symptom)) {
                    symptomsAndTime.add("1 - 6 Months");
                    found = true;
                } else if (timeArray.getJSONArray("10+ years").get(i).equals(symptom)) {
                    symptomsAndTime.add("10+ Years");
                    found = true;
                } else if (timeArray.getJSONArray("2 - 5 years").get(i).equals(symptom)) {
                    symptomsAndTime.add("2 - 5 Years");
                    found = true;
                } else if (timeArray.getJSONArray("5 - 10 years").get(i).equals(symptom)) {
                    symptomsAndTime.add("5 - 10 Years");
                    found = true;
                } else if (timeArray.getJSONArray("6 - 12 months").get(i).equals(symptom)) {
                    symptomsAndTime.add("6 - 12 Months");
                    found = true;
                } else if (timeArray.getJSONArray("< 1 month").get(i).equals(symptom)) {
                    symptomsAndTime.add("< 1 Month");
                    found = true;
                } else if (timeArray.getJSONArray("not specified").get(i).equals(symptom)) {
                    symptomsAndTime.add("Not Specified");
                    found = true;
                }
            }

            //If the time hasn't been given in the data then the time is set to unknown
            if (symptomsAndTime.size() == 1) {
                symptomsAndTime.add("Unknown");
            }

            interactions.add(symptomsAndTime);
        }
        return interactions;
    }
}
