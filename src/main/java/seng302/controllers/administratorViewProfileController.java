package seng302.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import seng302.App;
import seng302.model.AccountManager;
import seng302.model.UserAttributeCollection;
import seng302.model.person.Administrator;
import seng302.model.person.LogEntry;
import seng302.model.person.User;

public class administratorViewProfileController {

    /**
     * Error label for all validation errors
     */
    @FXML Label errorLabel;

    @FXML Label adminFirstName;
    @FXML Label adminMiddleNames;
    @FXML Label adminLastName;
    @FXML Label adminUsername;

    @FXML TextField adminEditFirstName;
    @FXML TextField adminEditMiddleNames;
    @FXML TextField adminEditLastName;
    @FXML TextField adminEditUserName;

    @FXML Button editButton;
    @FXML Button doneButton;
    @FXML Button cancelButton;

    private static Administrator selectedAdmin;

    public static void setSelectedAdmin(Administrator admin) {
        selectedAdmin = admin;
    }

    @FXML
    private void editClicked() {
      editButton.setDisable(true);
      editButton.setVisible(false);

      doneButton.setDisable(false);
      doneButton.setVisible(true);

      cancelButton.setDisable(false);
      cancelButton.setVisible(true);


      adminEditFirstName.setDisable(false);
      adminEditFirstName.setVisible(true);

      adminEditMiddleNames.setDisable(false);
      adminEditMiddleNames.setVisible(true);

      adminEditLastName.setDisable(false);
      adminEditLastName.setVisible(true);

      adminEditUserName.setDisable(false);
      adminEditUserName.setVisible(true);


      adminEditFirstName.setText(selectedAdmin.getFirstName());
      adminEditFirstName.setDisable(false);
      adminEditFirstName.setVisible(true);

      adminEditMiddleNames.setText(selectedAdmin.getMiddleName());
      adminEditMiddleNames.setDisable(false);
      adminEditMiddleNames.setVisible(true);

      adminEditLastName.setText(selectedAdmin.getLastName());
      adminEditLastName.setDisable(false);
      adminEditLastName.setVisible(true);

      adminEditUserName.setText(selectedAdmin.getUserName());
      adminEditUserName.setDisable(false);
      adminEditUserName.setVisible(true);
    }

    @FXML
    public void doneClicked() {
        Boolean modifiedFirstName = false;
        Boolean modifiedMiddleName = false;
        Boolean modifiedLastName = false;
        Boolean modifiedUserName = false;

        Boolean validGivenName = checkGivenName();
        Boolean validMiddleName = checkMiddleName();
        Boolean validLastName = checkLastName();
        Boolean validUsername = checkUsername();

        if(!adminEditUserName.getText().equals(selectedAdmin.getUserName())) {
          modifiedUserName = true;
        }
        else {
          validUsername = true; // Stop the error of (username already in use) if its their own one
        }

        if(!adminEditLastName.getText().equals(selectedAdmin.getLastName())) {
          modifiedLastName = true;
        }
        if(!adminEditMiddleNames.getText().equals(selectedAdmin.getMiddleName())) {
          modifiedMiddleName = true;
        }
        if(!adminEditFirstName.getText().equals(selectedAdmin.getFirstName())) {
          modifiedFirstName = true;
        }

        setStyle(validUsername,validGivenName,validMiddleName,validLastName);

        if(validGivenName && validMiddleName && validLastName && validUsername) {
          User currentUser = AccountManager.getCurrentUser();

          if(modifiedFirstName) {
            LogEntry firstNameLog = new LogEntry(selectedAdmin,currentUser,"firstName",selectedAdmin.getFirstName(),adminEditFirstName.getText());
            selectedAdmin.setFirstName(adminEditFirstName.getText());
            AccountManager.getSystemLog().add(firstNameLog);
            selectedAdmin.getModifications().add(firstNameLog);
          }
          if(modifiedMiddleName) {
            LogEntry middleNameLog = new LogEntry(selectedAdmin,currentUser,"middleName",selectedAdmin.getMiddleName(),adminEditMiddleNames.getText());
            selectedAdmin.setMiddleName(adminEditMiddleNames.getText());
            AccountManager.getSystemLog().add(middleNameLog);
            selectedAdmin.getModifications().add(middleNameLog);
          }
          if(modifiedLastName) {
            LogEntry lastNameLog = new LogEntry(selectedAdmin,currentUser,"lastName",selectedAdmin.getLastName(),adminEditLastName.getText());
            selectedAdmin.setLastName(adminEditLastName.getText());
            AccountManager.getSystemLog().add(lastNameLog);
            selectedAdmin.getModifications().add(lastNameLog);
          }
          if(modifiedUserName) {
            LogEntry userNameLog = new LogEntry(selectedAdmin,currentUser,"userName",selectedAdmin.getUserName(),adminEditUserName.getText());
            selectedAdmin.setUserName(adminEditUserName.getText());
            AccountManager.getSystemLog().add(userNameLog);
            selectedAdmin.getModifications().add(userNameLog);
          }
          setView();
        }
    }

    @FXML
    public void cancelClicked() {
        setView();
    }


    @FXML
    public void initialize() {

        if (selectedAdmin.getUserName().equals(Administrator.DEFAULT)) {
            editButton.setDisable(true);
            editButton.setVisible(false);
        }

        adminFirstName.setText(selectedAdmin.getFirstName());
        adminMiddleNames.setText(selectedAdmin.getMiddleName());

        adminLastName.setText(selectedAdmin.getLastName());
        adminUsername.setText(selectedAdmin.getUserName());

        doneButton.setDisable(true);
        doneButton.setVisible(false);

        cancelButton.setDisable(true);
        cancelButton.setVisible(false);

        adminEditFirstName.setDisable(true);
        adminEditFirstName.setVisible(false);

        adminEditMiddleNames.setDisable(true);
        adminEditMiddleNames.setVisible(false);

        adminEditLastName.setDisable(true);
        adminEditLastName.setVisible(false);

        adminEditUserName.setDisable(true);
        adminEditUserName.setVisible(false);

    }

    // Get these checks to work

  /**
   * Check whether the given name that the admin has entered is valid
   * A valid given name is between 1-50 characters and contains
   * only alphanumeric characters
   * @return Boolean Whether or not the given name is valid
   */
  private Boolean checkGivenName(){
    Boolean isValid = UserAttributeCollection.validateAlphanumericString
        (false, adminEditLastName.getCharacters().toString(), 0, 50);

    if(!isValid) {
      errorLabel.setTextFill(Color.web("red"));
      errorLabel.setText(
          "Invalid Given Name\nGiven name can be up to 50 alphanumeric characters"
              + "\nSpaces, commas, apostrophes, and dashes are also allowed");
      return false;
    }
    else {
      errorLabel.setText("");
      return true;
    }
  }


  /**
   * Check whether the middle name that the admin has entered is valid
   * A valid middle name is between 1-50 characters and contains
   * only alphanumeric characters
   * @return Boolean Whether or not the middle name is valid
   */
  private Boolean checkMiddleName(){
    Boolean isValid = UserAttributeCollection.validateAlphanumericString
        (false, adminEditMiddleNames.getCharacters().toString(),0,50);
    if(!isValid) {
      errorLabel.setTextFill(Color.web("red"));
      errorLabel.setText(
          "Invalid Middle Name\nMiddle name can be up to 50 alphanumeric nitecharacters\n"
              + "Spaces, commas, apostrophes, and dashes are also allowed.");
      return false;
    }
    else {
      errorLabel.setText("");
      return true;
    }
  }


  /**
   * Check whether the last name that the admin has entered is valid
   * A valid last name is between 1-100 characters and contains
   * only alphanumeric characters
   * @return Boolean Whether or not the last name is valid
   */
  private Boolean checkLastName(){
    Boolean isValid = UserAttributeCollection.validateAlphanumericString
        (false, adminEditLastName.getCharacters().toString(), 0, 50);
    if(!isValid) {
      errorLabel.setTextFill(Color.web("red"));
      errorLabel.setText(
          "Invalid Last Name\nLast name can be up to 50 alphanumeric characters\n" +
              "Spaces, commas, apostrophes, and dashes are also allowed.");
      return false;
    }
    else {
      errorLabel.setText("");
      return true;
    }
  }

  /**
   * Checks if the usernameTextField text is a valid username and returns 'true' if so.
   * If not the usernameTextField will be highlighted in red and the GUI error label will be updated with an appropriate error message.
   * @return Returns 'true' if the usernameTextField is valid, 'false' otherwise.
   */
  public boolean checkUsername() {
    boolean usernameIsValid = App.getDatabase().validateAdminUsername(adminEditUserName.getText());
    if (!usernameIsValid) {
      errorLabel.setTextFill(Color.web("red"));
      errorLabel.setText("Invalid username\nUsername needs to be between 3 and 20 alphanumeric characters and contain at least 1 letter\nUnderscores are allowed.");
      return false;
    } else {
      boolean usernameIsUsedAdmin = (null != App.getDatabase().getAdminIfItExists(adminEditUserName.getCharacters().toString()));
      boolean usernameIsNHI = App.getDatabase().checkNHIRegex(adminEditUserName.getText());

      if(usernameIsNHI) {
        errorLabel.setTextFill(Color.web("red"));
        errorLabel.setText("Username cannot be a NHI\nPlease use another username.");
        return false;
      }
      else if (usernameIsUsedAdmin) {
        errorLabel.setTextFill(Color.web("red"));
        errorLabel.setText("Username is already in use\nPlease use another username.");
        return false;
      }
      else {
        errorLabel.setText("");
        return true;
      }
    }
  }

  private void setView() {
    editButton.setDisable(false);
    editButton.setVisible(true);

    doneButton.setDisable(true);
    doneButton.setVisible(false);
    cancelButton.setDisable(true);
    cancelButton.setVisible(false);

    adminEditFirstName.setDisable(true);
    adminEditFirstName.setVisible(false);
    adminEditMiddleNames.setDisable(true);
    adminEditMiddleNames.setVisible(false);
    adminEditLastName.setDisable(true);
    adminEditLastName.setVisible(false);
    adminEditUserName.setDisable(true);
    adminEditUserName.setVisible(false);

    adminUsername.setText(selectedAdmin.getUserName());
    adminFirstName.setText(selectedAdmin.getFirstName());
    adminMiddleNames.setText(selectedAdmin.getMiddleName());
    adminLastName.setText(selectedAdmin.getLastName());

    setStyle(true,true,true,true);
  }

  private void setStyle(Boolean validUsername, Boolean validGivenName, Boolean validMiddleName, Boolean validLastName) {
    if(validUsername) {
      adminEditUserName.setStyle(" -fx-border-color: silver ; -fx-border-width: 1px ; ");
      errorLabel.setText("");
    }
    else {
      adminEditUserName.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
    }

    if(validGivenName) {
      adminEditFirstName.setStyle(" -fx-border-color: silver ; -fx-border-width: 1px ; ");
    }
    else {
      adminEditFirstName.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
    }

    if(validMiddleName) {
      adminEditMiddleNames.setStyle(" -fx-border-color: silver ; -fx-border-width: 1px ; ");
    }
    else {
      adminEditMiddleNames.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
    }

    if(validLastName) {
      adminEditLastName.setStyle(" -fx-border-color: silver ; -fx-border-width: 1px ; ");
    }
    else {
      adminEditLastName.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
    }
  }

}
