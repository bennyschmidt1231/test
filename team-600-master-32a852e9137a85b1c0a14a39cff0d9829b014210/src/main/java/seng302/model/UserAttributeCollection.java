package seng302.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import seng302.model.person.DonorReceiver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Stores the Donor's attributes such as height, weight, and current address.
 */
public class UserAttributeCollection {

    /**
     *  The current height of the Donor (in meters).
     */
    private double height;

    /**
     * The current weight of the Donor (in kilograms).
     */
    private double weight;

    /**
     * A string of the Donor's blood type. It is a code beginning with {A, B, AB, O} followed by either a '-' or '+' symbol.
     */
    private String bloodType;


    /**
     * The blood pressure of the donor expressed as a String object showing
     * systolic pressure over diastolic pressure.
     */
    private String bloodPressure;

    /**
     * Whether or not the donor is a smoker.
     */
    private Boolean smoker;

    /**
     * The alcohol consumption of the donor expressed in standard drinks per
     * week(a standard drink contains 10 grams of alcohol).
     */
    private double alcoholConsumption;

    /**
     * A string used to store any chronic diseases the donor suffers from.
     */
    private String chronicDiseases;

    /**
     * Contains the Donor's house number/building name and the name and number of their street address.
     * The address cannot be null and has a length of at most 100 alphanumeric characters.
     */
    private String streetAddress1;

    /**
     * An optional variable for storing additional address information. It has a length of at most 100 alphanumeric characters.
     */
    private String streetAddress2;

    /**
     * An optional variable for storing the address suburb. It has a length of at most 100 alphabetical characters.
     */
    private String suburb;

    /**
     * An optional variable for storing the address town or city. It has a length of at most 100 alphabetical characters.
     */
    private String town;

    /**
     * The donor's postal code. It will be a 4 digit number.
     */
    private int postCode;

    /**
     * An optional two letter code for the Donor's country. NZ is for New Zealand.
     */
    private String countryCode;

    /**
     * A boolean which is 'true' if the donor has a BMI too high to make them eligible, 'false' otherwise.
     * In the public health system, a BMI > 30 is considered obese. The BMI is calculated from the given height and
     * weight.
     */
    private boolean bodyMassIndexFlag;

    private double bmi;

    /**
     * Constructor for a blank UserAttributeCollection.
     */
    public UserAttributeCollection() {
    }

    /**
     * Constructor for a complete UserAttributeCollection.
     * @param height The height of the donor in meters
     * @param weight the weight of the donor in kgs
     * @param bloodType the bloodType of the donor
     * @param streetAddress1 the  house and street address of the donor
     * @param streetAddress2 additional address information
     * @param suburb the suburb of the the donor
     * @param town the town of the donor
     * @param postCode the postcode of the area where the account holder lives
     * @param countryCode the country code of the donor (NZ for New Zealand)
     * @param bmi the bmi of the account holder
     * @param bloodPressure The blood pressure of the donor represented as
     *                      'systolic pressure/diastolic pressure'
     * @param smoker True if the donor is a smoker, false otherwise.
     * @param alcoholConsumption The amount of alcohol the donor usually consumes
     *                           in standard drinks (10 grams of alcohol) per week.
     * @param chronicDiseases Any chronic diseases the donor has.
     */
    public UserAttributeCollection(double height, double weight, String bloodType, String streetAddress1, String streetAddress2,
                                   String suburb, String town, int postCode, String countryCode, boolean bmi, String bloodPressure,
                                   boolean smoker, double alcoholConsumption, String chronicDiseases){
        this.height = height;
        this.weight = weight;
        this.bloodType = bloodType;
        this.streetAddress1 = streetAddress1;
        this.streetAddress2 = streetAddress2;
        this.suburb = suburb;
        this.town = town;
        this.postCode = postCode;
        this.countryCode = StringExtension.toUpperCase(countryCode);
        bodyMassIndexFlag = bmi;
        this.smoker = smoker;
        this.alcoholConsumption = alcoholConsumption;
        this.bloodPressure = bloodPressure;
        this.chronicDiseases = chronicDiseases;


    }

    /**
     * Constructor for a complete UserAttributeCollection with json.
     * @param height The height of the donor in meters
     * @param weight the weight of the donor in kgs
     * @param bloodType the bloodType of the donor
     * @param streetAddress1 the  house and street address of the donor
     * @param streetAddress2 additional address information
     * @param suburb the suburb of the the donor
     * @param town the town of the donor
     * @param postCode the postcode of the area where the account holder lives
     * @param countryCode the country code of the donor (NZ for New Zealand)
     * @param bmi the bmi of the account holder
     * @param smoker Whether or not the donor is a smoker
     * @param bloodPressure The donor's blood pressure
     * @param alcoholConsumption The alcohol consumption of the donor
     * @param chronicDiseases The chronic diseases the donor has
     */
    @JsonCreator public UserAttributeCollection(@JsonProperty("height") double height,
                                   @JsonProperty("weight") double weight,
                                   @JsonProperty("bloodType") String bloodType,
                                   @JsonProperty("streetAddress1") String streetAddress1,
                                   @JsonProperty("streetAddress2") String streetAddress2,
                                   @JsonProperty("suburb") String suburb,
                                   @JsonProperty("town") String town,
                                   @JsonProperty("postCode") int postCode,
                                   @JsonProperty("countryCode") String countryCode,
                                   @JsonProperty("bodyMassIndexFlag") boolean bmi,
                                   @JsonProperty("smoker") Boolean smoker,
                                   @JsonProperty("bloodPressure") String bloodPressure,
                                   @JsonProperty("alcoholConsumption") double alcoholConsumption,
                                   @JsonProperty("chronicDiseases") String chronicDiseases){
        this.height = height;
        this.weight = weight;
        this.bloodType = bloodType;
        this.streetAddress1 = streetAddress1;
        this.streetAddress2 = streetAddress2;
        this.suburb = suburb;
        this.town = town;
        this.postCode = postCode;
        this.countryCode = StringExtension.toUpperCase(countryCode);
        bodyMassIndexFlag = bmi;
        this.smoker = smoker;
        this.alcoholConsumption = alcoholConsumption;
        this.bloodPressure = bloodPressure;
        this.chronicDiseases = chronicDiseases;
        if (bloodPressure.equals("0/0")) {
            this.bloodPressure = "0/0";
        } else {
            this.bloodPressure = bloodPressure;
        }
        if(chronicDiseases == null) {
            this.chronicDiseases = "";
        } else {
            this.chronicDiseases = chronicDiseases;
        }
    }

    public boolean getBodyMassIndexFlag() { return bodyMassIndexFlag; }

    public double getHeight() {
        return height;
    }

    public double getWeight() {
        return weight;
    }

    public String getBloodType() {
        return bloodType;
    }

    public String getBloodPressure() { return bloodPressure; }

    public Boolean getSmoker() { return smoker; }

    public double getAlcoholConsumption() { return alcoholConsumption; }

    public String getChronicDiseases() { return chronicDiseases; }

    public String getStreetAddress1() {
        return streetAddress1;
    }

    public String getStreetAddress2() {
        return streetAddress2;
    }

    public String getSuburb() {
        return suburb;
    }

    public String getTown() {
        return town;
    }

    public int getPostCode() { return postCode;}

    public String getCountryCode() {
        return countryCode;
    }

    public double getBMI() { return bmi; }

    public void setHeight(double height) { this.height = height; }

    public void setWeight(double weight) { this.weight = weight; }

    public void setBloodType(String bloodType) { this.bloodType = StringExtension.toUpperCase(bloodType); }

    public void setBloodPressure(double systolic, double diastolic) { bloodPressure = String.format("%.2f/%.2f", systolic, diastolic); }

    public void setBloodPressure(String pressure) { this.bloodPressure = pressure; }

    public void setSmoker(boolean smoker) { this.smoker = smoker; }

    public void setAlcoholConsumption(double alcoholConsumption) { this.alcoholConsumption = alcoholConsumption; }

    public void setChronicDiseases(String chronicDiseases) { this.chronicDiseases = chronicDiseases; }

    public void setStreetAddress1(String streetAddress1) { this.streetAddress1 = streetAddress1; }

    public void setStreetAddress2(String streetAddress2) { this.streetAddress2 = streetAddress2; }

    public void setSuburb(String suburb) { this.suburb = suburb; }

    public void setTown(String town) { this.town = town; }

    public void setPostCode(int postCode) { this.postCode = postCode;}

    public void setCountryCode(String countryCode) { this.countryCode = StringExtension.toUpperCase(countryCode); }

    public void setBodyMassIndexFlag(boolean bodyMassIndexFlag) { this.bodyMassIndexFlag = bodyMassIndexFlag; }



    /**
     * Checks if the given height is a reasonable value. The bodyMassIndexFlag  boolean is also calculated if weight
     * has previously been given.
     * @param height the height of the donor.
     * @return returns true if the height of the donor is within 'normal' bounds, false otherwise.
     */
    public boolean validateHeight(double height) {
        if(weight != 0.0) {
            double bmi = weight / (height * height);
            bodyMassIndexFlag = weight <= 30;
        }
        return height > 0.0 && height < 4.0;
    }

    /**
     * Checks if the given weight is greater than 0.0kg. The bodyMassIndexFlag  boolean is also calculated if height
     * has previously been given.
     * @param weight the weight of the donor.
     * @return returns 'true' if the weight of the donor is greater than or equal to 0.0 kilograms, 'false' otherwise.
     */
    public boolean validateWeight(double weight) {
        if(height != 0.0) {
            double bmi = weight / (height * height);
            bodyMassIndexFlag = weight <= 30;
        }
        return weight > 0.0;
    }


    /**
     * Checks if the given blood type string is a valid blood type.
     * @param bloodType A string blood type code.
     * @return returns 'true' if bloodType is valid, 'false' otherwise.
     */
    public boolean validateBloodType(String bloodType) {
        String bloodTypePattern = "^(A|B|O|AB)[+-]$";
        Pattern pattern = Pattern.compile(bloodTypePattern);
        Matcher matcher = pattern.matcher(bloodType);
        return matcher.matches();
    }




    /**
     * Checks if the given string is alphanumeric (if isAlphanumeric is 'true'), or if the string is alphabetical (if isAlphanumeric
     * is 'false'). The string is also allowed to contain spaces, apostrophes, commas, and dashes (-).
     * Also checks if the string is between minLength and maxLength characters (inclusive).
     * @param isAlphanumeric a boolean that sets whether to check a string is Alphanumeric (true) or Alphabetical (false).
     * @param string A string to be validated.
     * @param minLength an int representing the strings minimum required size. minLength should be less than or equal to maxLength.
     * @param maxLength an int representing the string's maximum required size.
     * @return returns 'true' if the string meets the specified conditions, 'false' otherwise.
     */
    public static boolean validateAlphanumericString(boolean isAlphanumeric, String string, int minLength, int maxLength) {
        String numbers = "";
        if (minLength > maxLength) {
            return false;
        }
        if (isAlphanumeric){
            numbers = "0-9";
        }
        try {
            String regularExpression = "^[a-zA-Z\\s-'," + numbers + "]{" + minLength + "," + maxLength + "}$";
            Pattern pattern = Pattern.compile(regularExpression);
            Matcher matcher = pattern.matcher(string);
            return matcher.matches();
        } catch (NullPointerException e) {
            return false;
        }
    }


    /**
     * Checks if the given postcode is a represented with 4 digits (the given postCode may be padded with zeros) and is between
     * 0001 and 9999.
     * Assumes there is no postcode '0000'. (unverified).
     * @param postCode an int
     * @return returns 'true' if the post code has a 4 digit representation, 'false' otherwise.
     */
    public boolean validatePostCode(int postCode) {
        String postCodeString = Integer.toString(postCode);
        String formattedString = postCodeString.format("%04d", postCode);
        return postCode > 0 && postCode <= 9999 && formattedString.length() == 4;
    }


    /**
     *  currently unimplemented, may scrape.
     */
    public void validateAttributeInput(){};


    /**
     * Creates an ordered string of all the user's attributes if the attributes are not null.
     * @return returns a string of all the user's attributes.
     */
    @Override
    public String toString(){
        String attributeString = "User Attributes:\n\t";
        if(height != 0.0) {attributeString += String.format("Height: %sm\n\t" , height);}
        if(weight != 0.0) {attributeString += String.format("Weight: %skg\n\t" , weight);}
        if(bloodType != null) {attributeString += String.format("Blood Type: %s\n\t" , bloodType);}
        if(bloodPressure != null) {attributeString += "Blood Pressure: " + bloodPressure + "\n\t";}
        if (smoker != null) { attributeString += "Smoker: " + smoker + "\n\t"; }
        if(alcoholConsumption != 0.0) { attributeString += String.format("Alcohol Consumption: %.2f standard drinks per week\n", alcoholConsumption); }
        if(chronicDiseases != null && !chronicDiseases.equals("")) { attributeString += "\tChronic Diseases: " + chronicDiseases + "\n";}
        String addressString = "\nAddress:\n\t";
        if(streetAddress1 != null) {addressString += streetAddress1 + "\n\t";}
        if(streetAddress2 != null) {addressString += streetAddress2 + "\n\t";}
        if(suburb != null) {addressString += suburb + "\n\t";}
        if(town != null) {addressString += town + "\n\t";}
        if(postCode != 0) {addressString += postCode + "\n\t";}
        if(countryCode != null) {addressString += countryCode;}
        addressString += "\n\n";
        return attributeString + addressString;
    }


    /**
     * Updates the account UserAttributeCollection height attribute if it is valid and logs the result.
     * @param value a string representation of the new height.
     * @return a string log of the result of the operation.
     */
    public String updateHeight(String value) {
        double h;
        String message = "ERROR: Invalid value " + value + ". Height should be of type double and be between 0.0m and 4.0m.\n";
        try {
            h = Double.parseDouble(value.trim());
        } catch (Exception e) {
            return message;
        }
        h = Double.parseDouble(value.trim());
        if (validateHeight(h)) {

            message = DonorReceiver.createLog("height", String.valueOf(height), value);
            setHeight(h);
        }
        return message;
    }




    /**
     * Updates the account UserAttributeCollection weight attribute if it is valid and logs the result.
     * @param value a string representation of the new weight.
     * @return a string log of the result of the operation.
     */
    public String updateWeight(String value){
        double w;
        String message = "ERROR: Invalid value " + value + ". Weight should be of type double and greater than 0.0kg.\n";
        try {
            w = Double.parseDouble(value.trim());
        } catch (Exception e) {
            return message;
        }
        w = Double.parseDouble(value.trim());
        if (validateWeight(w)) {

            message = DonorReceiver.createLog("weight", String.valueOf(weight), value);
            setWeight(w);
        }
        return message;
    }


    /**
     * Updates the account UserAttributeCollection bloodType attribute if it is valid and logs the result.
     * @param value a string representation of the new bloodType.
     * @return a string log of the result of the operation.
     */
    public String updateBloodType(String value){
        String message = "ERROR: Invalid value " + value + ". Blood Type should be one of 'A-', 'A+', 'B+', 'B-', 'AB+', " +
                "'AB-', 'O+', or 'O-'.\n";
        if (validateBloodType(value.trim())) {

            if(bloodType != null) { message = DonorReceiver.createLog("bloodType", bloodType, value);  }
            else { message = DonorReceiver.createLog("bloodType", "unspecified", value);  }
            setBloodType(value.trim());
        }
        return message;
    }


    /**
     * Updates the account UserAttributeCollection smoker attribute if it is
     * valid and logs the result.
     * @param value A string representing a boolean value.
     * @return A string showing the result of the operation.
     */
    public String updateSmoker(String value) {

        String message = "ERROR: Invalid value '" + value + "'. Smoker " +
                "should be set to either 'true' or 'false'.";

        Boolean validatedValue = validateBoolean(value.trim());

        if (validatedValue != null) {

            if (smoker != null) {

                message = DonorReceiver.createLog("smoker", smoker.toString(), validatedValue.toString());
                

            } else {

                message = DonorReceiver.createLog("smoker", "null", validatedValue.toString());
                

            }

            setSmoker(validatedValue);

        }

        return message;
    }


    /**
     * Updates the UserAttributeCollection attribute bloodPressure if it is
     * valid and logs the result.
     * @param value The new value for bloodPressure.
     * @return The result of the operation described in a String.
     */
    public String updateBloodPressure(String value) {

        String message = "ERROR: Invalid value '" + value + "'. Blood " +
                "pressure should be in the format " +
                "'<systolic pressure>/<diastolic pressure>'.";

        if (value != null && validateBloodPressure(value)) {


            message = DonorReceiver.createLog("bloodPressure", bloodPressure, value);
            
            setBloodPressure(value);

        }

        return message;

    }

    /**
     * Updates the UserAttributeCollection attribute chronicDiseases and logs
     * the result.
     * @param value The new value for chronicDiseases.
     * @return The result of the update operation described in a String.
     */
    public String updateChronicDiseases(String value) {

        String message = DonorReceiver.createLog("chronicDiseases", chronicDiseases, value);
        
        setChronicDiseases(value);

        return message;

    }


    public String updateAlcoholConsumption(String value) {

        String message = "ERROR: Invalid value '" + value + "'. Alcohol " +
                "consumption should be a number greater than or equal to 0.";

        if (validatePositiveDouble(value)) {

            message = DonorReceiver.createLog("alcoholConsumption", Double.toString(alcoholConsumption), value);
            
            setAlcoholConsumption(Double.parseDouble(value));

        }

        return message;
    }


    /**
     * Validates a positive double.
     * @param value The value to be validated as a String.
     * @return True if the value is a positive double, false otherwise.
     */
    public boolean validatePositiveDouble (String value) {

        try {

            double newDouble = Double.parseDouble(value);
            if (newDouble < 0) {
                return false;
            } else {
                return true;
            }

        } catch (Exception exception) {

            return false;

        }

    }


    /**
     * Validates a String representing blood pressure. Returns true if String is
     * of the form "systolic pressure/diastolic pressure".
     * @param value The String to be validated.
     * @return True if the String is valid, false otherwise.
     */
    public boolean validateBloodPressure(String value) {

        try {
            String[] splitValue = value.split("/");

            if (splitValue.length == 2) {

                double systolic = Double.parseDouble(splitValue[0]);
                double diastolic = Double.parseDouble(splitValue[1]);
                return true;

            } else {

                return false;

            }

        } catch (Exception exception) {

            return false;

        }

    }


    /**
     * Parses and validates a string meant to represent a boolean value. Returns
     * null if the string cannot be parsed.
     * @param value The boolean represented as a string.
     * @return A Boolean object.
     */
    public Boolean validateBoolean (String value) {

        if (value.toLowerCase().equals("true")) {

            return true;

        } else if (value.toLowerCase().equals("false")) {

            return false;

        } else {

            return null;

        }

    }



    /**
     * Updates the account UserAttributeCollection streetAddress1 attribute if it is valid and logs the result.
     * @param value a string representation of the new streetAddress1.
     * @return a string log of the result of the operation.
     */
    public String updateStreetAddress1(String value){
        String message = "ERROR: Invalid value " + value + ". Street address must be between 1 and 100 alphanumeric " +
                "characters. Spaces, commas, apostrophes, and dashes are also allowed.\n";
        if (validateAlphanumericString(true, value, 1, 100)) {

            if(streetAddress1 != null) { message = DonorReceiver.createLog("streetAddress1", streetAddress1, value); }
            else { message = DonorReceiver.createLog("streetAddress1", "unspecified", value); }
            setStreetAddress1(value);
        }
        return message;
    }


    /**
     * Updates the account UserAttributeCollection streetAddress2 attribute if it is valid and logs the result.
     * @param value a string representation of the new streetAddress2.
     * @return a string log of the result of the operation.
     */
    public String updateStreetAddress2(String value){
        String message = "ERROR: Invalid value " + value + ". Street address 2 must have at most 100 alphanumeric " +
                "characters. Spaces, commas, apostrophes, and dashes are also allowed.\n";
        if (validateAlphanumericString(true, value, 0, 100)) {

            if(streetAddress2 != null) { message = DonorReceiver.createLog("streetAddress2", streetAddress2, value);  }
            else { message = DonorReceiver.createLog("streetAddress2", "unspecified", value); }
            setStreetAddress2(value);
        }
        return message;
    }


    /**
     * Updates the account UserAttributeCollection suburb attribute if it is valid and logs the result.
     * @param value a string representation of the new suburb.
     * @return a string log of the result of the operation.
     */
    public String updateSuburb(String value) {
        String message = "ERROR: Invalid value " + value + ". Suburb must have at most 100 alphabetical " +
                "characters. Spaces, commas, apostrophes, and dashes are also allowed.\n";
        if (validateAlphanumericString(false, value, 0, 100)) {

            if(suburb != null) { message = DonorReceiver.createLog("suburb", suburb, value); }
            else { message = DonorReceiver.createLog("suburb", "unspecified", value);  }
            setSuburb(value);
        }
        return message;
    }


    /**
     * Updates the account UserAttributeCollection town attribute if it is valid and logs the result.
     * @param value a string representation of the new town.
     * @return a string log of the result of the operation.
     */
    public String updateTown(String value){
        String message = "ERROR: Invalid value " + value + ". Town must have at most 100 alphabetical " +
                "characters. Spaces, commas, apostrophes, and dashes are also allowed.\n";
        if (validateAlphanumericString(false, value, 0, 100)) {

            if(town != null) { message = DonorReceiver.createLog("town", town, value);  }
            else { message = DonorReceiver.createLog("town", "unspecified", value); }
            setTown(value);
        }
        return message;
    }


    /**
     * Updates the account UserAttributeCollection postCode attribute if it is valid and logs the result.
     * @param value a string representation of the new postCode.
     * @return a string log of the result of the operation.
     */
    public String updatePostCode(String value){
        int code;
        String message = "ERROR: Invalid value " + value + ". Post code must be a 4 digit integer that is not 0000.\n";
        try {
            code = Integer.parseInt(value.trim());
        } catch (Exception e) {
            return message;
        }
        code = Integer.parseInt(value.trim());
        if(validatePostCode(code)) {

            if(postCode !=0) {message = DonorReceiver.createLog("postCode", Integer.toString(postCode), value);  }
            else { message = DonorReceiver.createLog("postCode", "unspecified", value); }
            setPostCode(code);
        }
        return message;
    }

    //String h = "Create Name=John Doe DOB=1990-02-18 NHI=ABC1234";


    /**
     * Updates the account UserAttributeCollection countryCode attribute if it is valid and logs the result.
     * @param value a string representation of the new countryCode.
     * @return a string log of the result of the operation.
     */
    public String updateCountryCode(String value){
        String message = "ERROR: Invalid value " + value + ". Country code is strictly two alphabetical characters.\n";
        if (validateAlphanumericString(false, value.trim(), 2, 2)) {

            if(countryCode != null) { message = DonorReceiver.createLog("countryCode", countryCode, value);  }
            else { message = DonorReceiver.createLog("countryCode", "unspecified", value); }
            setCountryCode(value.trim());
        }
        return message;
    }

}


