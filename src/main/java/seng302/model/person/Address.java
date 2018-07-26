package seng302.model.person;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import seng302.model.StringExtension;


/**
 * Class for storing address details.
 */
public class Address {

  private String streetAddressLn1;
  private String streetAddressLn2;
  private String suburbName;
  private String cityName;
  private String region;
  private String postCode;
  private String countryCode;

  /**
   * Constructor for the Address class, creates a new instance of the Address class with the
   * necessary information with JSON.
   *
   * @param streetAddressLn1 The first line in the address (flat number, ect..)
   * @param streetAddressLn2 The second line in the address (house number, ect..)
   * @param suburbName The name of the suburb the address is in
   * @param cityName The name of the city the address is in
   * @param region The region the address is in
   * @param postCode The postcode of the address. If the postcode is invalid, then the postcode is
   * set to be an empty string.
   * @param countryCode The country code of the address
   */
  @JsonCreator
  public Address(@JsonProperty("streetAddressLn1") String streetAddressLn1,
      @JsonProperty("streetAddressLn2") String streetAddressLn2,
      @JsonProperty("suburbName") String suburbName,
      @JsonProperty("cityName") String cityName,
      @JsonProperty("region") String region,
      @JsonProperty("postCode") String postCode,
      @JsonProperty("countryCode") String countryCode) {
    setStreetAddressLn1(StringExtension.nullFilter(streetAddressLn1));
    setStreetAddressLn2(StringExtension.nullFilter(streetAddressLn2));
    setSuburbName(StringExtension.nullFilter(suburbName));
    setCityName(StringExtension.nullFilter(cityName));
    setRegion(StringExtension.nullFilter(region));
    updatePostCode(StringExtension.nullFilter(postCode));
    setCountryCode(StringExtension.nullFilter(countryCode));
  }

  @Override
  public String toString() {
    return "Street Address Line 1: " + this.streetAddressLn1 +
        "\nStreet Address Line 2: " + this.streetAddressLn2 +
        "\nSuburb: " + this.suburbName +
        "\nCity: " + this.cityName +
        "\nRegion: " + this.region +
        "\nPost Code: " + this.postCode +
        "\nCountry Code: " + this.countryCode;
  }

  public String getStreetAddressLn1() {
    return streetAddressLn1;
  }

  /**
   * Sets line 1 of the street address. If the line given is null, then the line will be stored as
   * an empty string instead.
   *
   * @param streetAddressLn1 The line 1 that the address will be set to have.
   */
  private void setStreetAddressLn1(String streetAddressLn1) {
    this.streetAddressLn1 = StringExtension.nullFilter(streetAddressLn1);
  }


  public String getStreetAddressLn2() {
    return streetAddressLn2;
  }

  /**
   * Sets line 2 of the street address. If the line given is null, then the line will be stored as
   * an empty string instead.
   *
   * @param streetAddressLn2 The line 2 that the address will be set to have.
   */
  private void setStreetAddressLn2(String streetAddressLn2) {

    this.streetAddressLn2 = StringExtension.nullFilter(streetAddressLn2);

  }


  /**
   * Returns the name of the suburb as a String object.
   *
   * @return A String object representing the name of the suburb.
   */
  public String getSuburbName() {

    return suburbName;

  }


  /**
   * Sets the suburb name of the address. If the suburb name given is null, then the suburb name
   * will be stored as an empty string instead.
   *
   * @param suburbName The suburb name that the address will be set to have.
   */
  private void setSuburbName(String suburbName) {

    this.suburbName = StringExtension.nullFilter(suburbName);

  }


  /**
   * Returns the city of the address as a String object.
   *
   * @return A String object containing the city as its value.
   */
  public String getCityName() {

    return cityName;

  }


  /**
   * Sets the city name of the address. If the city name given is null, then the city name will be
   * stored as an empty string instead.
   *
   * @param cityName The city name that the address will be set to have.
   */
  private void setCityName(String cityName) {
    this.cityName = StringExtension.nullFilter(cityName);
  }


  /**
   * Returns the region of the address as a String object.
   *
   * @return A String object containing the region of the address.
   */
  public String getRegion() {

    return region;

  }


  /**
   * Sets the region of the address. If the region given is null, the the region will be stored as
   * an empty string instead.
   *
   * @param region The region that the address will be set to have.
   */
  private void setRegion(String region) {

    this.region = StringExtension.nullFilter(region);

  }


  /**
   * Returns the post code of the address as a String oject.
   *
   * @return A String object containing the post code of the address.
   */
  public String getPostCode() {

    return postCode;

  }


  /**
   * Sets the post code of the address. If the post code has no characters, then the post code will
   * be stored as an empty string instead. If the post code is invalid, then the post code will not
   * be changed. If the post code is valid (by the definition of {@link Address#postCodeIsValid})
   * then that is the post code the address will be set to have.
   *
   * @param postCode The post code that the address will be set to have.
   * @return String of status of update postcode
   */
  public String updatePostCode(String postCode) {
    String message =
        "ERROR: Invalid value " + postCode + ". Post code must be a 4 digit integer.\n";
    if (postCode == null || postCode.trim().length() == 0) {
      message = "success";
      this.postCode = "";
    } else {
      //validate post code
      postCode = postCode.trim();
      if (postCodeIsValid(postCode)) {
        message = "success";
        this.postCode = postCode;
      }
    }
    return message;
  }

  /**
   * Validates a (non-empty) post code. The post code must be 4 characters long and contain only
   * numbers in order to be considered valid.
   *
   * @param postCode A string containing the post code to be tested (not an empty string).
   * @return Whether or not a post code is valid i.e. true if the post code is valid, false if not.
   */
  public static boolean postCodeIsValid(String postCode) {
    boolean valid = false;
    if (postCode != null && postCode.matches("[0-9]+") && postCode.length() == 4) {
      valid = true;
    }
    return valid;
  }


  public String getCountryCode() {
    return countryCode;
  }

  /**
   * Sets the country code of the address. If the country code given is null, the the country code
   * will be stored as an empty string instead.
   *
   * @param countryCode The country code that the address will be set to have.
   */
  private void setCountryCode(String countryCode) {
    this.countryCode = StringExtension.nullFilter(countryCode);
  }

  /**
   * Validates then updates the value addressStreet. returns an error message if update not
   * possible
   *
   * @param value The value to update addressStreet with.
   * @return A message whether or not the update passed.
   */
  public String updateStreetAddressLine1(String value) {
    String message = "ERROR: Invalid value " + value
        + ". Street address line 1 must be between 1 and 100 alphanumeric " +
        "characters. Spaces, commas, apostrophes, and dashes are also allowed.\n";
    if (validateAlphanumericString(true, value, 1, 100)) {
      message = "updated";
      setStreetAddressLn1(value);
    }
    return message;
  }

  /**
   * Validates then updates the value addressStreet. returns an error message if update not
   * possible
   *
   * @param value The value to update addressStreet with.
   * @return A message whether or not the update passed.
   */
  public String updateStreetAddressLine2(String value) {
    String message = "ERROR: Invalid value " + value
        + ". Street address line 2 must be between 0 and 100 alphanumeric " +
        "characters. Spaces, commas, apostrophes, and dashes are also allowed.\n";
    if (validateAlphanumericString(true, value, 0, 100)) {
      message = "updated";
      setStreetAddressLn2(value);
    }
    return message;
  }

  /**
   * Validates and updates the value addressCity.
   *
   * @param value The value to update addressCity
   * @return A message whether the update passed or not.
   */
  public String updateCityName(String value) {
    String message =
        "ERROR: Invalid value " + value + ". City name must have at most 100 alphabetical " +
            "characters. Spaces, commas, apostrophes, and dashes are also allowed.\n";
    if (validateAlphanumericString(true, value, 0, 100)) {
      message = "updated";
      setCityName(value);
    }
    return message;
  }

  /**
   * Validates and updates the value addressRegion
   *
   * @param value The value to update addressRegion
   * @return A message whether or not the update passed
   */
  public String updateRegion(String value) {
    String message =
        "ERROR: Invalid value " + value + ". Region must have at most 100 alphabetical " +
            "characters. Spaces, commas, apostrophes, and dashes are also allowed.\n";
    if (validateAlphanumericString(true, value, 0, 100)) {

      if (region != null && !region.equals("")) {
        message = "updated";
      } else {
        message = "updated";
      }
      setRegion(value);
    }
    return message;
  }

  public String updateSuburb(String value) {
    String message =
        "ERROR: Invalid value " + value + ". Suburb must have at most 100 alphabetical " +
            "characters. Spaces, commas, apostrophes, and dashes are also allowed.\n";
    if (validateAlphanumericString(true, value, 0, 100)) {
      message = "updated";
      setSuburbName(value);
    }
    return message;
  }

  public String updateCountryCode(String value) {
    String message =
        "ERROR: Invalid value " + value + ". Country Code must have at most 100 alphabetical " +
            "characters. Spaces, commas, apostrophes, and dashes are also allowed.\n";
    if (validateAlphanumericString(true, value, 0, 100)) {
      message = "updated";
      setCountryCode(value);
    }
    return message;
  }

  /**
   * Validates a string that is provided. Validation is whether or not the string is alphanumeric
   * and within the given length bounds
   *
   * @param isAlphanumeric A boolean variable htat tells whether the string is alphanumeric or not
   * @param string The string to be tested
   * @param minLength the minimum possible length for the string
   * @param maxLength The maximum possible length for the string
   * @return A boolean whether the string is valid for the given constraints
   */
  public static boolean validateAlphanumericString(boolean isAlphanumeric, String string,
      int minLength, int maxLength) {
    String numbers = "";
    if (minLength > maxLength) {
      return false;
    }
    if (isAlphanumeric) {
      numbers = "0-9";
    }
    String regularExpression =
        "^[a-zA-Z\\s-'," + numbers + "]{" + minLength + "," + maxLength + "}$";
    Pattern pattern = Pattern.compile(regularExpression);
    Matcher matcher = pattern.matcher(string);
    return matcher.matches();
  }


}
