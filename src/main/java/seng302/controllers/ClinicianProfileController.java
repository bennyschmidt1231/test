package seng302.controllers;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import seng302.App;
import seng302.model.AccountManager;
import seng302.model.CommandStack;
import seng302.model.PageNav;
import seng302.model.UndoableManager;
import seng302.model.person.Clinician;
import seng302.model.person.LogEntry;

/**
 * The controller class for all viewing or editing functionality for the current logged in clinician
 * in the App.w
 */
public class ClinicianProfileController {

  /**
   * The current clinician being viewed/edited.
   */
  public static Clinician clinician;

  private static UndoableManager undoableManager = App.getUndoableManager();
  private static Boolean undoRedoComboBox = false;
  private static Boolean undoRedoTextField = false;
  private static Boolean undoRedoTextArea = false;
  private static Boolean undoRedoLabel = false;

  @FXML
  private TextField clinicianProfileNameText;
  @FXML
  private TextField lastNameText;
  @FXML
  private TextArea workAddressText;
  @FXML
  private Button doneButton;
  @FXML
  private Label staffIDLabel;
  @FXML
  private Label dateLabel;
  @FXML
  private ComboBox<String> regionComboBox;
  @FXML
  private ListView view;
  @FXML
  private Label firstNameLabel;
  @FXML
  private Label lastNameLabel;
  @FXML
  private Label workAddressLabel;
  @FXML
  private Label regionLabel;
  @FXML
  private Button editButton;
  @FXML
  private Button cancelButton;
  @FXML
  private Button backButton;

  private final ArrayList<String> ignoredKeys = new ArrayList<>(Arrays
      .asList("ESCAPE", "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10", "F11", "F12",
          "PRINTSCREEN", "SCROLL_LOCK", "PAUSE", "PAGE_UP", "HOME", "INSERT", "END", "PAGE_DOWN",
          "NUM_LOCK", "ENTER", "RIGHT", "UP", "DOWN", "LEFT", "CONTROL", "CONTEXT_MENU",
          "ALT_GRAPH", "ALT", "WINDOWS", "CONTROL", "SHIFT", "CAPS"));

  private ObservableList<TextField> fields = FXCollections.observableArrayList();

  private ObservableList<Label> labels = FXCollections.observableArrayList();

  /**
   * An DonorReceiver Manager object created on application start up.
   */
  private AccountManager db = App.getDatabase();

  /**
   * A string array of all the administrative regions in New Zealand.
   */
  private final String[] REGIONS = {"Northland", "Auckland", "Waikato", "Bay of Plenty", "Gisborne",
      "Hawke's Bay",
      "Taranaki", "Manawatu-Wanganui", "Wellington", "Tasman", "Nelson", "Malborough", "West Coast",
      "Canterbury", "Otago", "Southland"};

  /**
   * A string of the Clinician's region.
   */
  private String region = null;

  public static void setClinician(Clinician newClinician) {
    clinician = newClinician;
  }

  public static Clinician getClinician() {
    return clinician;
  }

  private static boolean isChild = false;
  private boolean instanceIsChild = false;

  public static Boolean clinicianIsSet() {
    if (clinician != null) {
      return true;
    } else {
      return false;
    }
  }


  public void initialize() {
    // TODO where are the undo redo buttons?
    // show all labels
    loadViewLabels();
    disableEditFields();
    setUpProfileHistory();
    setUpUndoRedo();
    if (clinician.getUserName().equals(Clinician.DEFAULT)) {
      editButton.setDisable(true);
      editButton.setVisible(false);
    }

    if (isChild) {
      configureWindowAsChild();
    } else {
      backButton.setVisible(true);
      backButton.setDisable(false);
    }
  }

  /**
   * Changes the pane to reflect its status as a child window.
   */
  private void configureWindowAsChild() {

    instanceIsChild = isChild;
    isChild = false;

    // Disable and hide close button.
    backButton.setDisable(true);
    backButton.setVisible(false);
  }

  /**
   * Takes a boolean value should be true if this pane is supposed to appear in a child window.
   * Otherwise, the boolean value should be false.
   *
   * @param childStatus True if pane appears in a child window, false otherwise.
   */
  public static void setIsChild(boolean childStatus) {

    isChild = childStatus;

  }

  public void loadViewLabels() {
    firstNameLabel.setVisible(true);
    lastNameLabel.setVisible(true);
    regionLabel.setVisible(true);
    workAddressLabel.setVisible(true);
    if (clinician != null) {
      //Set the text boxes to the values of the clinician information
      firstNameLabel.setText(clinician.getFirstName());
      lastNameLabel.setText(clinician.getLastName());
      staffIDLabel.setText(String.valueOf(clinician.getUserName()));
      regionLabel.setText(clinician.getContactDetails().getAddress().getRegion());
      workAddressLabel.setText(clinician.getContactDetails().getAddress().getStreetAddressLn1());
      dateLabel.setText(formatCreationDate(clinician.getCreationDate()));
    } else {
      //If the clinician value is null, empty the information
      firstNameLabel.setText("");
      lastNameLabel.setText("");
      staffIDLabel.setText(String.valueOf(""));
      regionLabel.setText("");
      workAddressLabel.setText("");
      dateLabel.setText("");
    }
    editButton.setVisible(true);
  }

  public void disableEditFields() {
    clinicianProfileNameText.setVisible(false);
    lastNameText.setVisible(false);
    workAddressText.setVisible(false);
    regionComboBox.setVisible(false);
    doneButton.setVisible(false);
    cancelButton.setVisible(false);
  }

  public void setUpProfileHistory() {
    ObservableList<LogEntry> observableLogs = FXCollections.observableArrayList();
    observableLogs.addAll(clinician.getModifications());
    view.setCellFactory(param -> new ListCell<LogEntry>() {
      @Override
      protected void updateItem(LogEntry log, boolean empty) {
        super.updateItem(log, empty);
        if (empty || log == null) {
          setText(null);
        } else {
          setText(log.toString());
        }
      }
    });
    view.setItems(observableLogs);
  }

  /**
   * Initializes all the attributes seen by the user when they load the View/Edit Clinician GUI
   * page. This includes the current clinician's details as well as a log of any modifications they
   * have made to their profile.
   */
  public void setUpUndoRedo() {
    regionComboBox.getItems().addAll(REGIONS);
    fields.addAll(clinicianProfileNameText, lastNameText);
    labels.addAll(staffIDLabel, firstNameLabel, lastNameLabel, workAddressLabel, regionLabel);
    // TODO how to add the region combo box and the labels to the undo redo stuff
    // undo and redo for text fields
    for (TextField textField : fields) {
      textField.textProperty().addListener(new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue,
            String newValue) {
          if (!undoRedoTextField) {
            undoableManager.createTextFieldChange(textField, oldValue, newValue);
          }
          undoRedoTextField = false;
        }
      });
      textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
        @Override
        public void handle(javafx.scene.input.KeyEvent event) {
          if (event.getCode() == KeyCode.Z && event.isControlDown()) {
            undoEvent();
          } else if (event.getCode() == KeyCode.Y && event.isControlDown()) {
            redoEvent();
          }
        }
      });
    }
    workAddressText.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observable, String oldValue,
          String newValue) {
        if (!undoRedoTextArea) {
          undoableManager.createTextAreaUndoable(workAddressText, oldValue, newValue);
        }
        undoRedoTextArea = false;
      }
    });
    workAddressText.setOnKeyPressed(new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent event) {
        if (event.getCode() == KeyCode.Z && event.isControlDown()) {
          undoEvent();
        } else if (event.getCode() == KeyCode.Y && event.isControlDown()) {
          redoEvent();
        }
      }
    });
    // undo and redo for labels
    for (Label label : labels) {
      label.textProperty().addListener(new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue,
            String newValue) {
          if (!undoRedoLabel) {
            undoableManager.createLabelUndoable(label, oldValue, newValue);
          }
          undoRedoLabel = false;
        }
      });
      label.setOnKeyPressed(new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event) {
          if (event.getCode() == KeyCode.Z && event.isControlDown()) {
            undoEvent();
          } else if (event.getCode() == KeyCode.Y && event.isControlDown()) {
            redoEvent();
          }
        }
      });
    }

    //Undo and redo for region combo box
    regionComboBox.getSelectionModel().selectedIndexProperty()
        .addListener(new ChangeListener<Number>() {
          @Override
          public void changed(ObservableValue<? extends Number> observable, Number oldValue,
              Number newValue) {
            if (!undoRedoComboBox) {
              undoableManager.createComboBoxChange(regionComboBox, oldValue, newValue);
            }
            undoRedoComboBox = false;
          }
        });
  }


  /**
   * Formats the creation date of an donorReceiver into a readable value
   *
   * @param time The LocalDatetime to be formatted
   * @return a string with ceration date
   */
  public String formatCreationDate(LocalDateTime time) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    String creationDate = time.format(formatter);
    return creationDate;
  }


  /**
   * Function setting the values of the text fields in the clinician details tab
   */
  private void loadEditFields() {
    clinicianProfileNameText.setVisible(true);
    lastNameText.setVisible(true);
    regionComboBox.setVisible(true);
    workAddressText.setVisible(true);
    //Set the text boxes to the values of the clinician information
    clinicianProfileNameText.setText(clinician.getFirstName());
    lastNameText.setText(clinician.getLastName());
    regionComboBox.getSelectionModel()
        .select(clinician.getContactDetails().getAddress().getRegion());
    workAddressText.setText(clinician.getContactDetails().getAddress().getStreetAddressLn1());
    doneButton.setVisible(true);
    cancelButton.setVisible(true);
  }

  private void disableViewFields() {
    firstNameLabel.setVisible(false);
    lastNameLabel.setVisible(false);
    regionLabel.setVisible(false);
    workAddressLabel.setVisible(false);
    editButton.setVisible(false);
  }

  @FXML
  private void editButtonPressed() {
    loadEditFields();
    disableViewFields();
  }

  @FXML
  private void cancelButtonPressed() {
    loadViewLabels();
    disableEditFields();
  }

  /**
   * Undos the last undoable on the command stack
   */
  @FXML
  public void undoEvent() {
    undoCalled();
  }

  /**
   * Redos the last undoable on the command stakc
   */
  @FXML
  public void redoEvent() {
    redoCalled();

  }


  @FXML
  /**
   * Navigates the user back to the main menu GUI page.
   */
  void backButtonPressed() {
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

  /**
   * Parses the given string for the keyword "ERROR". If the keyword is present then an alert dialog
   * box is created with the given string as its content detailing the mistake they have made in
   * their data input.
   *
   * @param message A string thats either 'waiting for new message' or an error message.
   */
  public void showBadInputMessage(String message) {

    if (message.substring(0, 5).equals("ERROR")) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Error Dialog");
      alert.setHeaderText("Bad user Input");
      alert.setContentText(message);
      alert.getDialogPane().setId("BadUserInput");
      alert.getDialogPane().lookupButton(ButtonType.OK).setId("badAlertOkButton");
      alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      alert.showAndWait();
    }
  }


  /**
   * Checks each text field in the View/Edit Clinician GUI for any changes to the attributes and
   * attempts to validate, then update, then finally applies any changes made. Afterwards the
   * viewing element are shown. If any of the updates were invalid, the user will be informed about
   * the violation in an alert pop-up box.
   */
  @FXML
  void doneButtonPressed() {
    boolean changes = false;
    if (!clinicianProfileNameText.getText().equals(clinician.getFirstName())) {
      LogEntry logEntry = new LogEntry(clinician, AccountManager.getCurrentUser(), "firstName",
          clinician.getFirstName(), clinicianProfileNameText.getText());
      String updateMessage = clinician.updateGivenName(clinicianProfileNameText.getText());
      if (updateMessage.substring(0, 5).equals("ERROR")) {
        showBadInputMessage(updateMessage);
        clinician.setUpdateMessage("waiting for new message");
        return;
      } else {
        AccountManager.getSystemLog().add(logEntry);
        clinician.getModifications().add(logEntry);
      }
    }
    if (!lastNameText.getText().equals(clinician.getLastName())) {
      LogEntry logEntry = new LogEntry(clinician, AccountManager.getCurrentUser(), "lastName",
          clinician.getLastName(), lastNameText.getText());
      String updateMessage = clinician.updateLastName(lastNameText.getText());
      if (updateMessage.substring(0, 5).equals("ERROR")) {
        showBadInputMessage(updateMessage);
        clinician.setUpdateMessage("waiting for new message");
        return;
      } else {
        AccountManager.getSystemLog().add(logEntry);
        clinician.getModifications().add(logEntry);
      }
    }
    if (!workAddressText.getText()
        .equals(clinician.getContactDetails().getAddress().getStreetAddressLn1())) {
      LogEntry logEntry = new LogEntry(clinician, AccountManager.getCurrentUser(), "workAddress",
          clinician.getContactDetails().getAddress().getStreetAddressLn1(),
          workAddressText.getText());
      String updateMessage = clinician.updateWorkAddress(workAddressText.getText());
      if (updateMessage.substring(0, 5).equals("ERROR")) {
        showBadInputMessage(updateMessage);
        clinician.setUpdateMessage("waiting for new message");
        return;
      } else {
        AccountManager.getSystemLog().add(logEntry);
        clinician.getModifications().add(logEntry);
      }
    }
    if (region != null && !region.equals(clinician.getContactDetails().getAddress().getRegion())) {
      LogEntry logEntry = new LogEntry(clinician, AccountManager.getCurrentUser(), "region",
          clinician.getContactDetails().getAddress().getRegion(), region);
      String updateMessage = clinician.updateRegion(region);
      if (updateMessage.substring(0, 5).equals("ERROR")) {
        showBadInputMessage(updateMessage);
        clinician.setUpdateMessage("waiting for new message");
        return;
      } else {
        AccountManager.getSystemLog().add(logEntry);
        clinician.getModifications().add(logEntry);
      }
    }
    loadViewLabels();
    disableEditFields();
    setUpProfileHistory();
  }


  /**
   * Undos the last event when using menu bar
   */
  public static void undoCalled() {
    CommandStack current = undoableManager.getCommandStack();
    if (!current.getUndo().empty() && current.getUndo().peek().getUndoRedoName()
        .equals("Combo Box")) {
      undoRedoComboBox = true;
    }
    if (!current.getUndo().empty() && current.getUndo().peek().getUndoRedoName()
        .equals("Text Field")) {
      undoRedoTextField = true;
    }
    if (!current.getUndo().empty() && current.getUndo().peek().getUndoRedoName().equals("Label")) {
      undoRedoLabel = true;
    }
    if (!current.getUndo().empty() && current.getUndo().peek().getUndoRedoName()
        .equals("Text Area")) {
      undoRedoTextArea = true;
    }
    undoableManager.getCommandStack().undo();
  }


  /**
   * Redos the last event when using the menu bar
   */
  public static void redoCalled() {
    CommandStack current = undoableManager.getCommandStack();
    if (!current.getRedo().empty() && current.getRedo().peek().getUndoRedoName()
        .equals("Combo Box")) {
      undoRedoComboBox = true;
    }
    if (!current.getRedo().empty() && current.getRedo().peek().getUndoRedoName()
        .equals("Text Field")) {
      undoRedoTextField = true;
    }
    if (!current.getRedo().empty() && current.getRedo().peek().getUndoRedoName().equals("Label")) {
      undoRedoLabel = true;
    }
    if (!current.getUndo().empty() && current.getUndo().peek().getUndoRedoName()
        .equals("Text Area")) {
      undoRedoTextArea = true;
    }
    undoableManager.getCommandStack().redo();

  }


}
