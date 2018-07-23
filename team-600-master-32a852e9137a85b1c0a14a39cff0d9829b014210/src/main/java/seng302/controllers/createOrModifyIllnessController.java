package seng302.controllers;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import seng302.model.AccountManager;
import seng302.model.person.DonorReceiver;
import seng302.model.Illness;
import seng302.model.PageNav;
import seng302.model.person.LogEntry;
import seng302.App;
import seng302.model.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Controller class for handling all GUI events relating to the create/update Illness fxml pane.
 */
public class createOrModifyIllnessController {


    /**
     * An Illness object of the current Illness object being created or updated.
     */
    private static Illness illness;


    /**
     * An DonorReceiver object of the current donor being edited.
     */
    private static DonorReceiver donor;

    /**
     * Sets the donor attribute to the given DonorReceiver.
     * @param donorReceiver An DonorReceiver object to be edited.
     */
    public static void setDonor(DonorReceiver donorReceiver) {
        donor = donorReceiver;
    }

    /**
     * Sets the illness attribute to the given Illness.
     * @param newIllness An Illness object to be edited.
     */
    public static void setIllness(Illness newIllness) {
       illness = newIllness;
    }

    /**
     * A boolean flag which determines whether an Illness object is being created or an existing one is being edited.
     */
    private boolean editing;
    private boolean undoRedoChoiceBox = false;
    private boolean undoRedoDatePicker = false;
    private boolean undoRedoTextField = false;

    private UndoableManager undoableManager = App.getUndoableManager();

    // The following attributes correspond to FXML GUI artifacts.
    @FXML
    private TextField nameOfDiagnoses;

    @FXML
    private CheckBox chronicCheckBox;

    @FXML
    private CheckBox curedCheckBox;

    @FXML
    private DatePicker dateOfDiagnoses;


    @FXML
    private Button Cancel;


    @FXML
    private Button Done;


    @FXML
    /**
     * Closes the create/update Illness window in the GUI when the 'cancel' button is pressed and loads the ProfileView page.
     */
    void cancelButtonPressed(ActionEvent event) {
        illness = null;
        donor.populateIllnessLists();
        EditPaneController.setAccount(donor);
        goBackToEditPane();
    }


    /**
     * Checks if the updateMessage attribute of the current clinician being viewed starts with the word 'ERROR'.
     * @return Returns 'true' if the updateMessage starts with the word 'ERROR', or 'false' otherwise.
     */
    public boolean updateMessageIsAnErrorMessage() {
        return donor.getUpdateMessage().substring(0,5).equals("ERROR");
    }


    /**
     * Parses the given string for the keyword "ERROR". If the keyword is present then an alert dialog box is created
     * with the given string as its content detailing the mistake they have made in their data input.
     * @param message A string that is either 'waiting for new message' or an error message.
     */
    public void showBadInputMessage(String message) {

        if (message.substring(0,5).equals("ERROR")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Bad user Input");
            alert.setContentText(message);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
        }
    }

    /** A success dialog alert box given if the inputted values are all correct. The given message is displayed in the alert.
    * @param message A string to display in the alert box, in the GUI.
     **/
    public void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText("Success");
        alert.setContentText(String.format(message));
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();

    }

    /** A success dialog alert box given if the clinician made no changes to the diagnosis.
     */
    public void showNoChangesMade() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText("Success");
        alert.setContentText(String.format("No changes made to Diagnosis."));
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }


    /**
     * Checks if the GUI text field dateOfDiagnoses has been changed, and if it has attempts to update the Illness date attribute.
     * @return Returns 'true' only if the Illness date attribute has been successfully changed, 'false' otherwise.
     */
    public boolean checkDate() {
        boolean changes = false;
        if (!dateOfDiagnoses.getValue().equals(illness.getDate())) {
            LocalDate date;
            try {
                date = dateOfDiagnoses.getValue();
                donor.updateIllness(AccountManager.getCurrentUser(), illness.getName(), "date", DonorReceiver.formatDateToString(date));
                if (!updateMessageIsAnErrorMessage()) {
                    changes = true;
                }
            } catch (Exception e) {
                showBadInputMessage(String.format("ERROR: Invalid value %s. Date must be a valid date that is not after the " +
                        "current date nor before the donor's birth date of %s. The date also has to be in the following format: " +
                        "'dd/MM/YYYY'.\n", dateOfDiagnoses.getValue(), illness.formatDate(donor.getDateOfBirth())));
            }
        }
        return changes;
    }


    /**
     * Checks if the curedCheckBox and/or chronicCheckBox has been changed and if (they) have, attempts to update the Illness object
     * cured and/or chronic attributes. Order of resolution is important here and the calls to this function should NOT be changed or modified.
     * @return Returns 'true' if the illness object's cured and/or chronic attributes have been successfully changed, 'false' otherwise.
     */
    public boolean checkCured() {
        boolean changes = false;
        boolean curedChanged = !curedCheckBox.isSelected() == (illness.isCured());
        boolean chronicChanged = !chronicCheckBox.isSelected() == (illness.isChronic());
        boolean curedIsTrue = illness.isCured();
        if (curedChanged && chronicChanged && curedIsTrue) { // chronic is becoming true and cured is becoming false
            //must edit cured BEFORE editing chronic.
            donor.updateIllness(AccountManager.getCurrentUser(), illness.getName(), "cured", "false");
            if (!updateMessageIsAnErrorMessage()) {
                changes = true;
            }
            donor.updateIllness(AccountManager.getCurrentUser(), illness.getName(), "chronic", "true");
            if (!updateMessageIsAnErrorMessage()) {
                changes = true;
            }

        } else if (curedChanged && chronicChanged) { // chronic is becoming false and cured is becoming true
            checkChronic();
            if (!updateMessageIsAnErrorMessage()) {
                changes = true;
            }
            donor.updateIllness(AccountManager.getCurrentUser(), illness.getName(), "cured", "true");
            if (!updateMessageIsAnErrorMessage()) {
                changes = true;
            }

        } else if (curedChanged) { // cured is changing only
            if (curedCheckBox.isSelected() == true) {
                donor.updateIllness(AccountManager.getCurrentUser(), illness.getName(), "cured", "true");
                if (!updateMessageIsAnErrorMessage()) {
                    changes = true;
                }
            } else {
                donor.updateIllness(AccountManager.getCurrentUser(), illness.getName(), "cured", "false");
                if (!updateMessageIsAnErrorMessage()) {
                    changes = true;
                }
            }
        }
        return changes;
    }


    /**
     * Checks if the chronicCheckBox has been changed, and if it has then attempts to update the illness object's chronic
     * attribute.
     * @return Returns 'true' only if the illness chronic attribute was successfully changed, 'false' otherwise.
     */
    public boolean checkChronic() {
        boolean changes = false;
        if (!chronicCheckBox.isSelected() == (illness.isChronic())) {
            if (chronicCheckBox.isSelected() == true) {
                donor.updateIllness(AccountManager.getCurrentUser(), illness.getName(), "chronic", "true");
                if (!updateMessageIsAnErrorMessage()) {
                    changes = true;
                }
            } else {
                donor.updateIllness(AccountManager.getCurrentUser(), illness.getName(), "chronic", "false");
                if (!updateMessageIsAnErrorMessage()) {
                    changes = true;
                }
            }
        }
        return changes;
    }


    /**
     * Checks if the nameOfDiagnoses GUI text field has changed, and if it has attempts to update the illness object's
     * name attribute.
     * @return Returns 'true' only if the illness object's name attribute was successfully changed, 'false' otherwise.
     */
    public boolean checkName() {
        boolean changes = false;
        if (!nameOfDiagnoses.getText().toUpperCase().equals(illness.getName())) {
            donor.updateIllness(AccountManager.getCurrentUser(), illness.getName(), "name", nameOfDiagnoses.getText());
            if (!updateMessageIsAnErrorMessage()) {
                changes = true;
            }
        }
        return changes;
    }


    /**
     * Checks each Illness GUI field to see if any of the fields have been edited, and if they have, update the corresponding
     * illness attribute. If any inputted value is invalid, an error alert box will inform the user of the violation. If
     * No updates were made then the user is taken back to the viewProfile page. If there were successful updates made, then
     * the user will be informed this was the case, and taking back to the viewprofile page.
     */
    public void editIllness() {
        boolean nameChanged = checkName();
        if (updateMessageIsAnErrorMessage()) {
            showBadInputMessage(donor.getUpdateMessage());
            donor.setUpdateMessage("waiting for new message");
            return;
        }
        boolean dateChanged = checkDate();
        if (updateMessageIsAnErrorMessage()) {
            showBadInputMessage(donor.getUpdateMessage());
            donor.setUpdateMessage("waiting for new message");
            return;
        }
        //DO NOT CHANGE THE ORDER OF CHECKCURED() AND CHECKCHRONIC()!!!
        boolean curedChanged = checkCured();
        if (updateMessageIsAnErrorMessage()) {
            showBadInputMessage(donor.getUpdateMessage());
            donor.setUpdateMessage("waiting for new message");
            return;
        }

        boolean chronicChanged = checkChronic();
        if (updateMessageIsAnErrorMessage()) {
            showBadInputMessage(donor.getUpdateMessage());
            donor.setUpdateMessage("waiting for new message");
            return;
        }

        if (nameChanged || dateChanged|| chronicChanged || curedChanged) {
            showSuccessMessage("Diagnosis successfully updated. Make sure to save the application to make the changes permanent.");

        } else {
            showNoChangesMade();
        }
        illness = null;
        donor.populateIllnessLists();
        EditPaneController.setAccount(donor);
        goBackToEditPane();

    }


    /**
     * Loads the parent edit pane controller for the current edited donorReceiver.
     */
    public void goBackToEditPane() {
        try {
            // Set child status.
            EditPaneController.setIsChild(true);
            EditPaneController.setAccount(donor);

            // Create new pane.
            FXMLLoader loader = new FXMLLoader();
            AnchorPane editPane = loader.load(getClass().getResourceAsStream(PageNav.EDIT));

            // Create new scene.
            Scene editScene = new Scene(editPane);

            // Retrieve current stage and set scene.
            Stage current = (Stage) nameOfDiagnoses.getScene().getWindow();
            current.setScene(editScene);
        } catch(IOException exception) {
            System.out.println("Error loading edit window from illness");
        }
        //PageNav.loadNewPage(EDIT);
    }


    /**
     * Iterates through the donor masterIllnessList and attempts to match the given name to an Illness.
     * @param name A string name for an Illness.
     * @return Returns 'true' if the name is already being used by an Illness, 'false' otherwise.
     */
    public boolean checkNameUsed(String name) {
        boolean found = false;
        for (Illness ill: donor.getMasterIllnessList()) {
            if (name.toUpperCase().equals(ill.getName())) {
                found = true;
                break;
            }
        }
        return found;
    }


    /**
     * Attempts to create an Illness object from the given Illness GUI fields. If any inputted value is invalid,
     * an error alert box will inform the user of the violation. If the new diagnosis was successfully created then
     * the user will be informed this was the case, and taking back to the viewprofile page.
     */
    public void createIllness() {
        if (!illness. validateName(nameOfDiagnoses.getText())) {
            showBadInputMessage("ERROR: Invalid name" + nameOfDiagnoses.getText() +". Name must be between 1 and 50 alphabetical characters.");
            return;
        } else if (checkNameUsed(nameOfDiagnoses.getText())) {
            showBadInputMessage("ERROR: Duplicate name" + nameOfDiagnoses.getText() +". This diagnosis already exists.");
            return;
        }

        LocalDate date;
        boolean valid = true;
        boolean testDOB = true;
        try { // check that the date can actually be parsed.
            date = dateOfDiagnoses.getValue();
            testDOB = illness.validateDate(date, donor.getDateOfBirth());
        } catch (Exception e) {
            valid = false;
        }
        if (!(valid && testDOB)) {
            showBadInputMessage(String.format("ERROR: Invalid value %s. Date must be a valid date that is not after the " +
            "current date nor before the donor's birth date of %s. The date also has to be in the following format: " +
                    "'dd/MM/YYYY'.\n", dateOfDiagnoses.getValue(), donor.getDateOfBirth()));
            return;
        }
        date = dateOfDiagnoses.getValue();

        if (!illness.compareChronicWithCured(chronicCheckBox.isSelected(), curedCheckBox.isSelected())) {
            showBadInputMessage("ERROR: A diagnosis cannot be both chronic and cured at the same time.");
            return;
        }

        Illness ill = new Illness(nameOfDiagnoses.getText().toUpperCase(), date, curedCheckBox.isSelected(), chronicCheckBox.isSelected());

        donor.getMasterIllnessList().add(ill);
        LogEntry logEntry = new LogEntry(donor, AccountManager.getCurrentUser(), "Illness '" + ill.getName() + "'", "non-existent", "created");
        donor.logChange(logEntry);
        showSuccessMessage("New Diagnosis successfully created. Don't forget to save the application to make the change permanent.");
        illness = null;
        donor.populateIllnessLists();
        EditPaneController.setAccount(donor);
        goBackToEditPane();;
    }


    @FXML
    /**
     * Calls either the editIllness() function if an existing Illness is being edited. Otherwise calls the createIllness() function.
     */
    void doneButtonPressed(ActionEvent event) {
        if (editing) {
            editIllness();
        } else {
            createIllness();
        }
    }

    /**
     * Adds an undoable event when a checkbox is selected or unselected
     * @param event Event of un/selection od checkbox
     */
    @FXML
    private void checkBoxEvent(ActionEvent event) {
        undoableManager.createDonationUndoable(event);
    }

    /**
     * Calls the undo event when editing an illness
     */
    @FXML
    private void undo() {
        CommandStack current = undoableManager.getCommandStack();
        if(!current.getUndo().empty() && current.getUndo().peek().getUndoRedoName().equals("Choice Box")) undoRedoChoiceBox = true;
        if(!current.getUndo().empty() && current.getUndo().peek().getUndoRedoName().equals("Date Picker")) undoRedoDatePicker = true;
        if(!current.getUndo().empty() && current.getUndo().peek().getUndoRedoName().equals("Text Field")) undoRedoTextField = true;
        undoableManager.getCommandStack().undo();
    }

    /**
     * Calls the redo event when editing an illness
     */
    @FXML
    private void redo() {
        CommandStack current = undoableManager.getCommandStack();
        if(!current.getRedo().empty() && current.getRedo().peek().getUndoRedoName().equals("Choice Box")) undoRedoChoiceBox = true;
        if(!current.getRedo().empty() && current.getRedo().peek().getUndoRedoName().equals("Date Picker")) undoRedoDatePicker = true;
        if(!current.getRedo().empty() && current.getRedo().peek().getUndoRedoName().equals("Text Field")) undoRedoTextField = true;
        undoableManager.getCommandStack().redo();
    }


    /**
     * Initializes the create/update Illness GUI with default text values in each field. If the illness attribute is not null,
     * then these values will be those of the Illness object selected in the medical History(diseases) Illness list for editing.
     */
    public void initialize () {
        if (illness == null) {
            illness = new Illness("", false, false);
            editing = false;
        } else {
            editing = true;
        }
        nameOfDiagnoses.setText(illness.getName());
        dateOfDiagnoses.setValue(illness.getDate());
        chronicCheckBox.setSelected(illness.isChronic());
        curedCheckBox.setSelected(illness.isCured());
        if(illness.isChronic()) curedCheckBox.setDisable(true);
        else if (illness.isCured())chronicCheckBox.setDisable(true);

        // Undo and Redo components for illness changes
        chronicCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue == true) {
                    curedCheckBox.setSelected(false);
                    curedCheckBox.setDisable(true);
                } else {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Deregister Disease");
                    alert.setHeaderText("The currently selected disease is longer chronic anymore");
                    alert.setContentText("Are you ok with this?");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK){
                        curedCheckBox.setDisable(false);
                    } else {
                        undo();
                    }
                }
            }
        });
        curedCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue == true) {
                    chronicCheckBox.setSelected(false);
                    chronicCheckBox.setDisable(true);
                } else {
                    chronicCheckBox.setDisable(false);
                }
            }
        });
        nameOfDiagnoses.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(!undoRedoTextField) {
                    undoableManager.createTextFieldChange(nameOfDiagnoses, oldValue, newValue);
                }
            }
        });
        dateOfDiagnoses.valueProperty().addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) {
                if(!undoRedoDatePicker) {
                    undoableManager.createDatePickerChange(dateOfDiagnoses, oldValue, newValue);
                }
            }
        });
    }



}
