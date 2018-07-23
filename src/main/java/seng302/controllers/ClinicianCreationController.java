package seng302.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import seng302.*;
import javafx.scene.control.Button;
import seng302.model.*;
import seng302.model.person.Clinician;
import seng302.model.person.LogEntry;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/* Class for handling all events from the clinicianCreation fxml, specifically creating a clinician.
 */
public class ClinicianCreationController {

    /**
     * An Account Manager object created on application start up.
     */
    private AccountManager db = App.getDatabase();

    private static UndoableManager undoableManager = App.getUndoableManager();
    private static Boolean undoRedoComboBox = false;
    private static Boolean undoRedoTextField = false;
    private ObservableList<TextField> fields = FXCollections.observableArrayList();


    /**
     * A string array of all the administrative regions in New Zealand.
     */
    private final String[] REGIONS = {"Northland", "Auckland", "Waikato", "Bay of Plenty", "Gisborne", "Hawke's Bay",
            "Taranaki", "Manawatu-Wanganui", "Wellington", "Tasman", "Nelson", "Malborough", "West Coast", "Canterbury", "Otago", "Southland"};

    /**
     * A string of the Clinician's region.
     */
    private String region = null;

    @FXML
    public Label createClinicianLabel;

    @FXML
    public TextField givenNameTextField;

    @FXML
    public TextField lastNameTextField;

    @FXML
    public TextField staffIDTextField;

    @FXML
    public TextField streetAddressTextField;

    @FXML
    public ComboBox regionComboBox;

    @FXML
    public Button doneButton;

    @FXML
    public Button helpButton;

    @FXML
    public Button cancelButton;

    @FXML
    public TextField passwordTextField;


    /** An Error dialog alert box given if the inputted attribute is invalid.
     * @param attribute A string of the attribute entered. Should be one of "Given Name", "Last Name", "Staff ID", "Address", or "Region".
     * @param message A string of the error message to show in the error dialog.
     */
    public void showBadInputMessage(String attribute, String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("Invalid "+ attribute + " given");
        alert.setContentText(message);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.getDialogPane().setId(String.format("bad%sAlertDialog", attribute.replace(" ", "")));
        alert.getDialogPane().lookupButton(ButtonType.OK).setId(String.format("bad%sAlertOkButton", attribute.replace(" ", "")));
        alert.showAndWait();

    }

    /**
     * A failure dialog alert box given if the application fails to save.
     */
    public void showBadSaveError(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("Save failed");
        alert.setContentText("Something went wrong and and the application failed to save the clinician to file. Please try again.");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }


    /* A success dialog alert box given if the application successfully saved.
     */
    public void showGoodSave() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText("Success");
        alert.setContentText(String.format("New clinician successfully created and saved to file."));
        alert.getDialogPane().setId("goodSaveAlert");
        alert.getDialogPane().lookupButton(ButtonType.OK).setId("goodSaveButton");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }


    @FXML
    /**
     * Changes the current page to the Login page when the cancel button is clicked in the GUI.
     * @param event The action event of the user pressing the 'cancel' button.
     */
    void cancelButtonClicked(ActionEvent event) {

        undoableManager.getCommandStack().save();
        PageNav.loadNewPage(PageNav.MAINMENU);
    }


    @FXML
    /**
     * Creates a new clinician and adds it to the database from the provided details. The details provided are:
     * given name, last name, staff ID, address, region, and password. Invalid details result in a corresponding error
     * dialog box. If there are no errors then the clinician is saved to file.
     */
    void doneButtonClicked(ActionEvent event) {

        boolean testID2 = Clinician.validateStaffIDIsInt(staffIDTextField.getText());
        if (!testID2) {
            showBadInputMessage("Staff ID", "Staff ID has to be an integer greater than zero.");
            return;
        } else { // Staff ID is a valid integer, now must check it is not already being used.
            boolean testID = db.checkStaffIDIsNotUsed(staffIDTextField.getText());
            if (!testID) {
                showBadInputMessage("Staff ID", "Staff ID has already been used. Make sure the ID given is unique.");
                return;
            }
        }

        boolean testGivenName = UserAttributeCollection.validateAlphanumericString
                (false, givenNameTextField.getText(), 1, 50);
        if (!testGivenName) {
            showBadInputMessage("Given Name", "Given name must be between 1 and 50 alphabetical characters. " +
                    "Spaces, commas, apostrophes, and dashes are also allowed.");
            return;
        }

        boolean testLastName = UserAttributeCollection.validateAlphanumericString
                (false, lastNameTextField.getText(), 1, 50);
        if (!testLastName) {
            showBadInputMessage("Last Name", "Last name  must be between 1 and 50 alphabetical characters. " +
                    "Spaces, commas, apostrophes, and dashes are also allowed.");
            return;
        }

        boolean testStreetAddress = UserAttributeCollection.validateAlphanumericString
                (true, streetAddressTextField.getText(), 1, 100);
        if (!testStreetAddress) {
            showBadInputMessage("Address", "Street Address must be between 1 and 100 alphanumeric characters. " +
                    "Spaces, commas, apostrophes, and dashes are also allowed.");
            return;
        }

        if (region == null) {
            showBadInputMessage("Region", "Region has not been selected!");
            return;
        }

        boolean testPassword = Clinician.validatePassword(passwordTextField.getText());
        if (!testPassword) {
            showBadInputMessage("Password", "Password should be between 6 and 30 alphanumeric characters of any case.");
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        ArrayList<LogEntry> modifications = new ArrayList<>();

        Clinician clinician = new Clinician(givenNameTextField.getText(), lastNameTextField.getText(), streetAddressTextField.getText(), region, staffIDTextField.getText()
                 , passwordTextField.getText(), now, modifications);
        LogEntry logEntry = new LogEntry(clinician, AccountManager.getCurrentUser(), "created", null, null);
        AccountManager.getSystemLog().add(logEntry);
        clinician.getModifications().add(logEntry);
        db.getClinicians().put(clinician.getUserName(), clinician);
        //TODO why are we saving system here??????
        try {
            db.exportClinicians();
            showGoodSave();
        } catch (Exception e) {
            showBadSaveError();
        }
        undoableManager.getCommandStack().save();
        PageNav.loadNewPage(PageNav.MAINMENU);
    }


    @FXML
    /**
     * Sets the region attribute given by the combo box option selected by the user in the GUI.
     * @param event An action event whereby the user has selected a region from the GUI combo box.
     */
    void regionSelected(ActionEvent event) {
        String reg = String.valueOf(regionComboBox.getSelectionModel().getSelectedIndex());
        if (Integer.parseInt(reg) == 0) {
            region = "Northland";
        } else if (Integer.parseInt(reg) == 1) {
            region = "Auckland";
        } else if (Integer.parseInt(reg) == 2) {
            region = "Waikato";
        } else if (Integer.parseInt(reg) == 3) {
            region = "Bay of Plenty";
        } else if (Integer.parseInt(reg) == 4) {
            region = "Gisborne";
        } else if (Integer.parseInt(reg) == 5) {
            region = "Hawke's Bay";
        } else if (Integer.parseInt(reg) == 6) {
            region = "Taranaki";
        } else if (Integer.parseInt(reg) == 7) {
            region = "Manawatu-Wanganui";
        } else if (Integer.parseInt(reg) == 8) {
            region = "Wellington";
        } else if (Integer.parseInt(reg) == 9) {
            region = "Tasman";
        } else if (Integer.parseInt(reg) == 10) {
            region = "Nelson";
        } else if (Integer.parseInt(reg) == 11) {
            region = "Malborough";
        } else if (Integer.parseInt(reg) == 12) {
            region = "West Coast";
        } else if (Integer.parseInt(reg) == 13) {
            region = "Canterbury";
        } else if (Integer.parseInt(reg) == 14) {
            region = "Otago";
        } else {
            region = "Southland";
        }
    }

    @FXML
    private void initialize() {
        regionComboBox.getItems().addAll(REGIONS);
        fields.addAll(givenNameTextField, lastNameTextField, staffIDTextField, passwordTextField, streetAddressTextField);

        for(TextField textField: fields) {
            textField.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    if(!undoRedoTextField) {
                        undoableManager.createTextFieldChange(textField, oldValue, newValue);
                    }
                    undoRedoTextField = false;
                }
            });
            textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(javafx.scene.input.KeyEvent event) {
                    if(event.getCode() == KeyCode.Z && event.isControlDown()) {
                        undoEvent();
                    } else if(event.getCode() == KeyCode.Y && event.isControlDown()) {
                        redoEvent();
                    }
                }
            });
        }

        //Undo and redo
        regionComboBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(!undoRedoComboBox) {
                    undoableManager.createComboBoxChange(regionComboBox, oldValue, newValue);
                }
                undoRedoComboBox = false;
            }
        });



    }


    /**
     * Undos the last undoable on the command stack
     */
    @FXML
    public void undoEvent() {
        undoCalled();
    }

    /**
     * Redos the last undoable on the commands stack
     */
    @FXML
    public void redoEvent() {
        redoCalled();

    }

    /**
     * Calls the undo event when using menu bar
     */
    public static void undoCalled() {
        CommandStack current = undoableManager.getCommandStack();
        if(!current.getUndo().empty() && current.getUndo().peek().getUndoRedoName().equals("Combo Box")) undoRedoComboBox = true;
        if(!current.getUndo().empty() && current.getUndo().peek().getUndoRedoName().equals("Text Field")) undoRedoTextField = true;
        undoableManager.getCommandStack().undo();
    }

    /**
     * Calls the redo event when using the menu bar
     */
    public static void redoCalled() {
        CommandStack current = undoableManager.getCommandStack();
        if(!current.getRedo().empty() && current.getRedo().peek().getUndoRedoName().equals("Combo Box")) undoRedoComboBox = true;
        if(!current.getRedo().empty() && current.getRedo().peek().getUndoRedoName().equals("Text Field")) undoRedoTextField = true;
        undoableManager.getCommandStack().redo();

    }
}
