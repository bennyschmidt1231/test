package seng302.model.person;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class User extends Person{

    private String userName;
    private String password;

    private LocalDateTime creationDate;
    private ArrayList<LogEntry> modifications;

    private boolean active = true;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean Active) { active = Active; }


    /**
     * The constructor method for a User (when creating a new User).
     * @param givenName The given name of the User.
     * @param lastName The last name of the User.
     * @param username The staff ID of the User.
     * @param workAddress The work address of the User.
     * @param region The region of the User.
     * @param password The password of the User.
     * @param modifications modifications of the user
     * @param creationDate creationdate of user
     */
    public User(String givenName,
                     String lastName,
                     String workAddress,
                     String region,
                     String username,
                     String password,
                     LocalDateTime creationDate,
                     ArrayList<LogEntry> modifications) {

        super(givenName, null,lastName, new ContactDetails(
                new Address(workAddress, null, null, null, region, null, null)
                ,null,null,null));
        this.userName = username;
        this.password = password;
        if (creationDate == null) {
            this.creationDate = LocalDateTime.now();
        } else {
            this.creationDate = creationDate;
        } if (modifications == null) {
            this.modifications = new ArrayList<>();
        } else {
            this.modifications = modifications;
        }

    }


    /**
     * Full constructor for the User class with JSON
     * @param userName A string of the User's username. For a donor this is their NHI number. For a clinician it is their
     *                 staff id. For an admin it is their username.
     * @param password A string of the User's password. It should be between 6 and 30 alphanumeric characters long
     * @param firstName A string of the Person's first (or given) name
     * @param middleName A string of the Person's middle (or other) name
     * @param lastName A string of the Person's last name
     * @param contactDetails A ContactDetails object containing the Person's address and contact numbers.
     * @param modifications modifications of person
     * @param creationDate creationDate of person
     */
    @JsonCreator
    public User(
            //inherited from Person
            @JsonProperty("firstName") String firstName,
            @JsonProperty("middleName") String middleName,
            @JsonProperty("lastName") String lastName,
            @JsonProperty("contactDetails") ContactDetails contactDetails,

            // User attributes
            @JsonProperty("userName") String userName,
            @JsonProperty("password") String password,
            @JsonProperty("creationDate") LocalDateTime creationDate,
            @JsonProperty("modifications") ArrayList<LogEntry> modifications){
        super(firstName, middleName, lastName, contactDetails);
        this.userName = userName;
        this.password = password;
        if (creationDate == null) {
            this.creationDate = LocalDateTime.now();
        } else {
            this.creationDate = creationDate;
        } if (modifications == null) {
            this.modifications = new ArrayList<>();
        } else {
            this.modifications = modifications;
        }
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public ArrayList<LogEntry> getModifications() {
        return modifications;
    }

    public void setModifications(ArrayList<LogEntry> modifications) {
        this.modifications = modifications;
    }


    //TODO This needs to be changed so that it's an abstract class and each of its subclasses implement it in the same way
    /**
     * Checks if the given password string is alphanumeric and is between 6 and 30 characters (inclusive) long.
     * @param username a string of the User's username. It should be alphanumeric and between 3 and 30 characters long (underscores are allowed).
     * @return check if username is valid
     */
    public static boolean validateUsername(String username) {
        try {
            String regularExpression = "^[a-zA-Z0-9_]{3,30}$";
            Pattern pattern = Pattern.compile(regularExpression);
            Matcher matcher = pattern.matcher(username);
            return matcher.matches();
        } catch (NullPointerException e) {
            return false;
        }
    }
}
