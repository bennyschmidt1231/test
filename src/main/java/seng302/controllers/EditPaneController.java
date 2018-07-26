package seng302.controllers;

import static seng302.controllers.ViewProfilePaneController.setChronicIllnessFirstSort;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import seng302.App;
import seng302.model.AccountManager;
import seng302.model.BooleanExtension;
import seng302.model.CommandStack;
import seng302.model.DonorOrganInventory;
import seng302.model.Illness;
import seng302.model.MedicalProcedure;
import seng302.model.PageNav;
import seng302.model.ReceiverOrganInventory;
import seng302.model.StringExtension;
import seng302.model.UndoableManager;
import seng302.model.person.Administrator;
import seng302.model.person.Clinician;
import seng302.model.person.DonorReceiver;
import seng302.model.person.LogEntry;

/**
 * The controller class for the editpane. This class calls the model's issueCommand(Update) methods
 * on whatever data fields the user wishes to edit.
 */
public class EditPaneController {

  /**
   * The current donorReceiver being edited.
   */
  private DonorReceiver donorReceiver;
  private static DonorReceiver staticAccount;
  private static boolean isChild = false;
  private boolean instanceIsChild = false;
  private boolean invalidFlag = false;

  private String nhi;
  private AccountManager db = App.getDatabase();
  private ArrayList<String> medicationToDelete = new ArrayList<>();

  static void setAccount(DonorReceiver newDonorReceiver) {
    staticAccount = newDonorReceiver;
  }

  private static UndoableManager undoableManager = App.getUndoableManager();

  private static Boolean undoRedoChoiceBox = false;
  private static Boolean undoRedoDatePicker = false;
  private static Boolean undoRedoTextField = false;

  private final String[] gender = {"Male", "Female", "Other", "Unknown", "Unspecified"};
  private final String[] bloodTypes = {"A+", "B+", "O+", "A-", "B-", "O-", "AB+", "AB-"};
  private final String[] titles = {"Mr", "Mrs", "Ms", "Master"};

  private ObservableList<String> autoCompleteSuggestions = FXCollections.observableArrayList();

  private String errorMessage = "";

  @FXML
  private Text DonorLoggedIn;
  @FXML
  private TabPane mainTabPane;

  //Basic information
  @FXML
  private TextField editGivenNames;
  @FXML
  private Text givenNamesText;
  @FXML
  private TextField editLastName;
  @FXML
  private Text lastNameText;
  @FXML
  private TextField preferredName;
  @FXML
  private Text preferredNameText;
  @FXML
  private TextField editNHINumber;
  @FXML
  private Text nhiText;
  @FXML
  private DatePicker editDateOfBirth;
  @FXML
  private Text dateOfBirthText;
  @FXML
  private DatePicker editDateOfDeath;
  @FXML
  private Text dateOfDeathText;
  @FXML
  private ChoiceBox editGender;
  @FXML
  private Text genderText;
  @FXML
  private ChoiceBox birthGender;
  @FXML
  private Text birthGenderText;
  @FXML
  private ChoiceBox editTitle;
  @FXML
  private Text titleText;
  @FXML
  private TextField editHeight;
  @FXML
  private Text heightText;
  @FXML
  private TextField editWeight;
  @FXML
  private Text weightText;
  @FXML
  private ChoiceBox editBloodType;
  @FXML
  private Text bloodTypeText;
  @FXML
  private CheckBox editLivedInUKFrance;
  @FXML
  private Text ukFranceText;

  //Contact details
  @FXML
  private TextField editStreetAddress;
  @FXML
  private Text streetText;
  @FXML
  private TextField editCity;
  @FXML
  private Text cityText;
  @FXML
  private TextField editRegion;
  @FXML
  private Text regionText;
  @FXML
  private TextField editPostcode;
  @FXML
  private Text postcodeText;
  @FXML
  private TextField editMobileNumber;
  @FXML
  private Text mobileNumberText;
  @FXML
  private TextField editHomeNumber;
  @FXML
  private Text homeNumberText;
  @FXML
  private TextField editEmail;
  @FXML
  private Text emailText;
  @FXML
  private TextField editEmergStreetAddress;
  @FXML
  private Text emergStreetText;
  @FXML
  private TextField editEmergCity;
  @FXML
  private Text emergCityText;
  @FXML
  private TextField editEmergRegion;
  @FXML
  private Text emergRegionText;
  @FXML
  private TextField editEmergPostcode;
  @FXML
  private Text emergPostcodeText;
  @FXML
  private TextField editEmergMobileNumber;
  @FXML
  private Text emergMobileNumberText;
  @FXML
  private TextField editEmergHomeNumber;
  @FXML
  private Text emergHomeNumberText;
  @FXML
  private TextField editEmergEmail;
  @FXML
  private Text emergEmailText;

  //Tabs
  @FXML
  private Tab receiverTab;
  @FXML
  private TabPane organTabPane;

  //Organs
  @FXML
  private CheckBox editLiver;
  @FXML
  private CheckBox editKidney;
  @FXML
  private CheckBox editLung;
  @FXML
  private CheckBox editHeart;
  @FXML
  private CheckBox editPancreas;
  @FXML
  private CheckBox editIntestine;
  @FXML
  private CheckBox editCornea;
  @FXML
  private CheckBox editMiddleEar;
  @FXML
  private CheckBox editBone;
  @FXML
  private CheckBox editBoneMarrow;
  @FXML
  private CheckBox editSkin;
  @FXML
  private CheckBox editConnectiveTissue;

  //Receiving organs
  @FXML
  private CheckBox editReceiverLiver;
  @FXML
  private CheckBox editReceiverKidney;
  @FXML
  private CheckBox editReceiverLung;
  @FXML
  private CheckBox editReceiverHeart;
  @FXML
  private CheckBox editReceiverPancreas;
  @FXML
  private CheckBox editReceiverIntestine;
  @FXML
  private CheckBox editReceiverCornea;
  @FXML
  private CheckBox editReceiverMiddleEar;
  @FXML
  private CheckBox editReceiverBone;
  @FXML
  private CheckBox editReceiverBoneMarrow;
  @FXML
  private CheckBox editReceiverSkin;
  @FXML
  private CheckBox editReceiverConnectiveTissue;

  // Donating organ text
  @FXML
  private Text editLiverText;
  @FXML
  private Text editKidneyText;
  @FXML
  private Text editLungText;
  @FXML
  private Text editHeartText;
  @FXML
  private Text editPancreasText;
  @FXML
  private Text editIntestineText;
  @FXML
  private Text editCorneaText;
  @FXML
  private Text editMiddleEarText;
  @FXML
  private Text editBoneText;
  @FXML
  private Text editBoneMarrowText;
  @FXML
  private Text editSkinText;
  @FXML
  private Text editConnectiveTissueText;

  //Receiving organ text
  @FXML
  private Text editReceiverLiverText;
  @FXML
  private Text editReceiverKidneyText;
  @FXML
  private Text editReceiverLungText;
  @FXML
  private Text editReceiverHeartText;
  @FXML
  private Text editReceiverPancreasText;
  @FXML
  private Text editReceiverIntestineText;
  @FXML
  private Text editReceiverCorneaText;
  @FXML
  private Text editReceiverMiddleEarText;
  @FXML
  private Text editReceiverBoneText;
  @FXML
  private Text editReceiverBoneMarrowText;
  @FXML
  private Text editReceiverSkinText;
  @FXML
  private Text editReceiverConnectiveTissueText;

  //Medications
  @FXML
  private Button moveToCurrent;
  @FXML
  private Button moveToPrevious;
  @FXML
  private Button addMedication;
  @FXML
  private Button editMedication;
  @FXML
  private Button removeMedication;
  @FXML
  private ComboBox<String> createNewMedication;
  @FXML
  private ListView editCurrentMedications;
  @FXML
  private ListView editPreviousMedications;
  @FXML
  private Tab medicationsTab;

  //Medical History
  @FXML
  private CheckBox editSmoker;
  @FXML
  private Text smokerText;
  @FXML
  private TextField editAlcoholConsumption;
  @FXML
  private Text alcoholConsumptionText;
  @FXML
  private TextField editBloodPressure;
  @FXML
  private Text bloodPressureText;
  @FXML
  private TextField editChronicDiseases;
  @FXML
  private Text chronicDiseasesText;

  // Menu Buttons
  @FXML
  private Button Done;
  @FXML
  private Button Cancel;

  //ChoiceBox values for Undo/Redo

  //medical history diseases
  @FXML
  private ListView history;
  @FXML
  private TableView<Illness> currentTable;
  @FXML
  private TableColumn<Illness, String> CurrentName;
  @FXML
  private TableColumn<Illness, String> chronic;
  @FXML
  private TableColumn<Illness, String> currentDate;
  @FXML
  private TableView<Illness> historicTable;
  @FXML
  private TableColumn<Illness, String> historicName;
  @FXML
  private TableColumn<Illness, String> historicDate;
  @FXML
  private Tab diseasesTab;

  /**
   * An observable array list of Illness objects representing current diseases/illnesses the donor
   * suffers from.
   */
  private ObservableList<Illness> currentDiagnoses;

  /**
   * An observable array list of Illness objects representing historic diseases/illnesses the donor
   * suffered from.
   */
  private ObservableList<Illness> historicDiagnoses;


  // Medical History Procedures
  @FXML
  private TableView<MedicalProcedure> pastProceduresTable;
  @FXML
  private TableColumn<MedicalProcedure, LocalDate> editPastProceduresDateColumn;
  @FXML
  private TableColumn<MedicalProcedure, String> editPastProceduresSummaryColumn;
  @FXML
  private TableView<MedicalProcedure> pendingProcedureTable;
  @FXML
  private TableColumn<MedicalProcedure, LocalDate> editPendingProceduresDateColumn;
  @FXML
  private TableColumn<MedicalProcedure, String> editPendingProceduresSummaryColumn;
  @FXML
  private Label pastProcedureAffectedOrgans;
  @FXML
  private TextArea pastProcedureDescription;
  @FXML
  private Label pendingProcedureAffectedOrgans;
  @FXML
  private TextArea pendingProcedureDescription;
  @FXML
  private Tab medicalProceduresTab;

  final ObservableList<MedicalProcedure> pastProcedures = FXCollections
      .observableArrayList(staticAccount.extractPastProcedures());

  final ObservableList<MedicalProcedure> pendingProcedures = FXCollections
      .observableArrayList(staticAccount.extractPendingProcedures());

  //===================================================
  //===================================================

  /**
   * Initializes combo boxes/check boxes and labels in the pane
   */
  @FXML
  private void initialize() {

    // Handle configuration of scene.
    configureWindowAsChild();

    donorReceiver = staticAccount;
    nhi = donorReceiver.getUserName();

    editGender.getItems().addAll(gender);
    birthGender.getItems().addAll(gender);
    editBloodType.getItems().addAll(bloodTypes);
    editTitle.getItems().addAll(titles);

    initCheckBoxes();
    initLabels();
    initMedications();
    hideMedicationsEditingFromDonor();
    initContactDetails();
    initBasicInfomation();
    initMedicalHistory();

    donorReceiver.setUpdateMessage("waiting for new message");
    DonorLoggedIn.setText(donorReceiver.fullName());
    hideOrShowMedicalProcedures();
    new AutoCompleteComboBoxListener<>(createNewMedication);

    //medical history diseases related gui elements
    currentDiagnoses = FXCollections.observableArrayList(donorReceiver.getCurrentDiagnoses());
    historicDiagnoses = FXCollections.observableArrayList(donorReceiver.getHistoricDiagnoses());

    historicName.setCellValueFactory(new PropertyValueFactory<>("name"));
    historicDate.setCellValueFactory(new PropertyValueFactory<>("date"));
    CurrentName.setCellValueFactory(new PropertyValueFactory<>("name"));
    currentDate.setCellValueFactory(new PropertyValueFactory<>("date"));

    setChronicColoumn();
    currentTable.setItems(currentDiagnoses);
    historicTable.setItems(historicDiagnoses);

    setChronicIllnessFirstSort(currentTable);
    setChronicIllnessFirstSort(
        historicTable); // Used because it also gives the default sort of descending date

    hideMedicationsEditingFromDonor();
    if (AccountManager
        .getCurrentUser() instanceof DonorReceiver) { // a donor/receiver is logged in so we hide edit medical history tab
      diseasesTab.setDisable(true); // index 5 is the medicial history (disease tab)
    }

    hideOrShowMedicationsTab();

    // Undo and redo for text fields
    ArrayList<TextField> textFieldArrayList = new ArrayList<TextField>();
    textFieldArrayList.add(editChronicDiseases);
    textFieldArrayList.add(editBloodPressure);
    textFieldArrayList.add(editAlcoholConsumption);
    textFieldArrayList.add(editEmergEmail);
    textFieldArrayList.add(editEmergHomeNumber);
    textFieldArrayList.add(editEmergMobileNumber);
    textFieldArrayList.add(editEmergPostcode);
    textFieldArrayList.add(editEmergRegion);
    textFieldArrayList.add(editEmergCity);
    textFieldArrayList.add(editEmergStreetAddress);
    textFieldArrayList.add(editEmail);
    textFieldArrayList.add(editHomeNumber);
    textFieldArrayList.add(editMobileNumber);
    textFieldArrayList.add(editPostcode);
    textFieldArrayList.add(editRegion);
    textFieldArrayList.add(editCity);
    textFieldArrayList.add(editStreetAddress);
    textFieldArrayList.add(editHeight);
    textFieldArrayList.add(editWeight);
    textFieldArrayList.add(editNHINumber);
    textFieldArrayList.add(editLastName);
    textFieldArrayList.add(editGivenNames);

    for (TextField textField : textFieldArrayList) {
      textField.textProperty().addListener((observable, oldValue, newValue) -> {
        if (!undoRedoTextField) {
          undoableManager.createTextFieldChange(textField, oldValue, newValue);
        }
        undoRedoTextField = false;
      });
      textField.setOnKeyPressed(event -> {
        if (event.getCode() == KeyCode.Z && event.isControlDown()) {
          undoEvent();
        } else if (event.getCode() == KeyCode.Y && event.isControlDown()) {
          redoEvent();
        }
      });
    }

    // Add change event listeners for undoing and redoing events
    editTitle.getSelectionModel().selectedIndexProperty()
        .addListener((observable, oldValue, newValue) -> {
          if (!undoRedoChoiceBox) {
            undoableManager.createChoiceBoxUndoable(editTitle, oldValue, newValue);
          }
          undoRedoChoiceBox = false;
        });
    editGender.getSelectionModel().selectedIndexProperty()
        .addListener((observable, oldValue, newValue) -> {
          if (!undoRedoChoiceBox) {
            undoableManager.createChoiceBoxUndoable(editGender, oldValue, newValue);
          }
          undoRedoChoiceBox = false;
        });
    editBloodType.getSelectionModel().selectedIndexProperty()
        .addListener((observable, oldValue, newValue) -> {
          if (!undoRedoChoiceBox) {
            undoableManager.createChoiceBoxUndoable(editBloodType, oldValue, newValue);
          }
          undoRedoChoiceBox = false;
        });

    editDateOfBirth.valueProperty().addListener((observable, oldValue, newValue) -> {
      if (!undoRedoDatePicker) {
        undoableManager.createDatePickerChange(editDateOfBirth, oldValue, newValue);
      }
      undoRedoDatePicker = false;
    });

    editDateOfDeath.valueProperty().addListener((observable, oldValue, newValue) -> {
      if (!undoRedoDatePicker) {
        undoableManager.createDatePickerChange(editDateOfDeath, oldValue, newValue);
      }
      undoRedoDatePicker = false;
    });
    editCurrentMedications.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    editPreviousMedications.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    handleView();
  }


  /**
   * Changes the pane to reflect its status as a child window.
   */
  private void configureWindowAsChild() {
    instanceIsChild = isChild;
    isChild = false;
  }


  private void hideOrShowMedicationsTab() {
    if (AccountManager.getCurrentUser() instanceof DonorReceiver) {
      medicationsTab.setDisable(true);
    } else if (AccountManager.getCurrentUser() instanceof Clinician || AccountManager
        .getCurrentUser() instanceof Administrator) {
      medicationsTab.setDisable(false);
    }
  }


  /**
   * Initialise the ListViews containing medication information
   */
  @FXML
  public void initMedications() {
    ObservableList<String> currentMedications = FXCollections
        .observableArrayList(donorReceiver.getMedications().getCurrentMedications());
    editCurrentMedications.setItems(currentMedications);
    ObservableList<String> previousMedications = FXCollections
        .observableArrayList(donorReceiver.getMedications().getPreviousMedications());
    editPreviousMedications.setItems(previousMedications);
  }

  /**
   * Log and save current medications to the donorReceiver
   *
   * @param event When the ActionEvent occurs in the gui
   */
  @FXML
  public void saveCurrentMedication(ActionEvent event) {
    ObservableList<String> currentMedications = editCurrentMedications.getItems();
    for (String medication : currentMedications) {
      if (donorReceiver.getMedications().getPreviousMedications().contains(medication)) {
        donorReceiver.getMedications()
            .addMedicationAlphabetically(donorReceiver.getMedications().getCurrentMedications(),
                medication, "Previous Medication");
      } else {
        donorReceiver.getMedications()
            .addMedicationAlphabetically(donorReceiver.getMedications().getCurrentMedications(),
                medication, null);
      }
    }
  }

  /**
   * Log and save previous medications to the donorReceiver
   *
   * @param event When the ActionEvent occurs in the gui
   */
  @FXML
  public void savePreviousMedication(ActionEvent event) {
    ObservableList<String> previousMedication = editPreviousMedications.getItems();
    for (String medication : previousMedication) {
      if (donorReceiver.getMedications().getCurrentMedications().contains(medication)) {
        donorReceiver.getMedications()
            .addMedicationAlphabetically(donorReceiver.getMedications().getPreviousMedications(),
                medication, "Current Medications");
      } else {
        donorReceiver.getMedications()
            .addMedicationAlphabetically(donorReceiver.getMedications().getPreviousMedications(),
                medication, null);
      }

    }
  }

  /**
   * Create a new medication and add to current medications
   */
  @FXML
  public void addCurrentMedication() {
    String newMedication = createNewMedication.getEditor().getText();
    if (!newMedication.trim().equals("") && !editCurrentMedications.getItems()
        .contains(newMedication) && !editPreviousMedications.getItems().contains(newMedication)) {
      editCurrentMedications.getItems().add(newMedication);
      editCurrentMedications.refresh();
      undoableManager.createMedicationAddChange(editCurrentMedications, newMedication);
      createNewMedication.getItems().removeAll(createNewMedication.getItems());
      createNewMedication.getEditor().setText("");
    }
  }

  /**
   * Log and save removed medications to the donorReceiver
   *
   * @param event When the ActionEvent occurs in the gui
   */
  @FXML
  private void saveRemovedMedications(ActionEvent event) {
    for (String medication : medicationToDelete) {
      if (medication.startsWith("C") && donorReceiver.getMedications().getCurrentMedications()
          .contains(medication.substring(1))) {
        donorReceiver.getMedications()
            .removeMedication(donorReceiver.getMedications().getCurrentMedications(),
                medication.substring(1));
      } else if (medication.startsWith("P") && donorReceiver.getMedications()
          .getPreviousMedications().contains(medication.substring(1))) {
        donorReceiver.getMedications()
            .removeMedication(donorReceiver.getMedications().getPreviousMedications(),
                medication.substring(1));
      }
    }
  }

  /**
   * Remove the currently selected medication
   */
  @FXML
  private void removeCurrentPreviousMedication() {
    ObservableList<String> currentlySelectedMedications = editCurrentMedications.getSelectionModel()
        .getSelectedItems();
    ArrayList<String> currentToRemove = new ArrayList<>(
        currentlySelectedMedications); //Selection doesn't work with ObservableList
    ObservableList<String> currentlySelectedPreviousMedications = editPreviousMedications
        .getSelectionModel().getSelectedItems();
    ArrayList<String> previousToRemove = new ArrayList<>(
        currentlySelectedPreviousMedications); //Selection doesn't work with ObservableList
    for (String currentlySelected : currentToRemove) {
      if (currentlySelected != null) {
        editCurrentMedications.getItems().remove(currentlySelected);
        medicationToDelete.add("C" + currentlySelected);
        undoableManager
            .createMedicationRemoveChange(editCurrentMedications, currentlySelected, true,
                medicationToDelete);
      }
    }
    for (String previousSelected : previousToRemove) {
      if (previousSelected != null) {
        editPreviousMedications.getItems().remove(previousSelected);
        medicationToDelete.add("P" + previousSelected);
        undoableManager
            .createMedicationRemoveChange(editPreviousMedications, previousSelected, true,
                medicationToDelete);
      }
    }
    editCurrentMedications.getSelectionModel().clearSelection();
    editPreviousMedications.getSelectionModel().clearSelection();
    editCurrentMedications.refresh();
    editPreviousMedications.refresh();
  }

  /**
   * Moves a medication located in Current Medications to Previous Medications
   */
  @FXML
  private void moveCurrentToPrevious() {
    ObservableList<String> currentlySelectedMedications = editCurrentMedications.getSelectionModel()
        .getSelectedItems();
    ArrayList<String> medicationsToMove = new ArrayList<>(
        currentlySelectedMedications); //Selection doesn't work with ObservableList
    for (int i = 0; i < medicationsToMove.size(); i++) {
      String currentlySelected = medicationsToMove.get(i);
      if (currentlySelected != null) {
        undoableManager.createMedicationCurrentToPreviousChange(editCurrentMedications,
            editPreviousMedications, currentlySelected);
        editCurrentMedications.getItems().remove(currentlySelected);
        editPreviousMedications.getItems().add(currentlySelected);
      }
    }
    editCurrentMedications.getSelectionModel().clearSelection();
    editPreviousMedications.getSelectionModel().clearSelection();
    editCurrentMedications.refresh();
    editPreviousMedications.refresh();

  }

  /**
   * Moves a medication located in Previous Medications to Current Medications
   */
  @FXML
  private void movePreviousToCurrent() {
    String previousSelected = (String) editPreviousMedications.getSelectionModel()
        .getSelectedItem();
    if (previousSelected != null) {
      undoableManager
          .createMedicationPreviousToCurrentChange(editCurrentMedications, editPreviousMedications,
              previousSelected);
      editPreviousMedications.getItems().remove(previousSelected);
      editCurrentMedications.getItems().add(previousSelected);
    }
    editCurrentMedications.refresh();
    editPreviousMedications.refresh();

  }

  /**
   * Initializes all check boxes
   */
  @FXML
  public void initCheckBoxes() {
    editLivedInUKFrance.setSelected(donorReceiver.getLivedInUKFlag());

    editSmoker.setSelected(
        BooleanExtension.getBoolean(donorReceiver.getUserAttributeCollection().getSmoker()));

    editLiver.setSelected(donorReceiver.getDonorOrganInventory().getLiver());
    editKidney.setSelected(donorReceiver.getDonorOrganInventory().getKidneys());
    editLung.setSelected(donorReceiver.getDonorOrganInventory().getLungs());
    editHeart.setSelected(donorReceiver.getDonorOrganInventory().getHeart());
    editPancreas.setSelected(donorReceiver.getDonorOrganInventory().getPancreas());
    editIntestine.setSelected(donorReceiver.getDonorOrganInventory().getIntestine());
    editCornea.setSelected(donorReceiver.getDonorOrganInventory().getCorneas());
    editMiddleEar.setSelected(donorReceiver.getDonorOrganInventory().getMiddleEars());
    editBone.setSelected(donorReceiver.getDonorOrganInventory().getBone());
    editBoneMarrow.setSelected(donorReceiver.getDonorOrganInventory().getBoneMarrow());
    editSkin.setSelected(donorReceiver.getDonorOrganInventory().getSkin());
    editConnectiveTissue.setSelected(donorReceiver.getDonorOrganInventory().getConnectiveTissue());

    editReceiverLiver.setSelected(donorReceiver.getRequiredOrgans().getLiver());
    editReceiverKidney.setSelected(donorReceiver.getRequiredOrgans().getKidneys());
    editReceiverLung.setSelected(donorReceiver.getRequiredOrgans().getLungs());
    editReceiverHeart.setSelected(donorReceiver.getRequiredOrgans().getHeart());
    editReceiverPancreas.setSelected(donorReceiver.getRequiredOrgans().getPancreas());
    editReceiverIntestine.setSelected(donorReceiver.getRequiredOrgans().getIntestine());
    editReceiverCornea.setSelected(donorReceiver.getRequiredOrgans().getCorneas());
    editReceiverMiddleEar.setSelected(donorReceiver.getRequiredOrgans().getMiddleEars());
    editReceiverBone.setSelected(donorReceiver.getRequiredOrgans().getBone());
    editReceiverBoneMarrow.setSelected(donorReceiver.getRequiredOrgans().getBoneMarrow());
    editReceiverSkin.setSelected(donorReceiver.getRequiredOrgans().getSkin());
    editReceiverConnectiveTissue
        .setSelected(donorReceiver.getRequiredOrgans().getConnectiveTissue());
  }

  public void initLabels() {
    // Highlight the organ label of any organ that the donator is both receiving and donating
    DonorOrganInventory donorOrganInventory = donorReceiver.getDonorOrganInventory();
    ReceiverOrganInventory receiverOrganInventory = donorReceiver.getRequiredOrgans();
    if (donorOrganInventory != null && receiverOrganInventory != null) {

      if (donorOrganInventory.getLiver() && receiverOrganInventory.getLiver()) {
        editLiverText.setFill(Color.RED);
        editReceiverLiverText.setFill(Color.RED);
      }
      if (donorOrganInventory.getKidneys() && receiverOrganInventory.getKidneys()) {
        editKidneyText.setFill(Color.RED);
        editReceiverKidneyText.setFill(Color.RED);
      }
      if (donorOrganInventory.getLungs() && receiverOrganInventory.getLungs()) {
        editLungText.setFill(Color.RED);
        editReceiverLungText.setFill(Color.RED);
      }
      if (donorOrganInventory.getHeart() && receiverOrganInventory.getHeart()) {
        editHeartText.setFill(Color.RED);
        editReceiverHeartText.setFill(Color.RED);
      }
      if (donorOrganInventory.getPancreas() && receiverOrganInventory.getPancreas()) {
        editPancreasText.setFill(Color.RED);
        editReceiverPancreasText.setFill(Color.RED);
      }
      if (donorOrganInventory.getIntestine() && receiverOrganInventory.getIntestine()) {
        editIntestineText.setFill(Color.RED);
        editReceiverIntestineText.setFill(Color.RED);
      }
      if (donorOrganInventory.getCorneas() && receiverOrganInventory.getCorneas()) {
        editCorneaText.setFill(Color.RED);
        editReceiverCorneaText.setFill(Color.RED);
      }
      if (donorOrganInventory.getMiddleEars() && receiverOrganInventory.getMiddleEars()) {
        editMiddleEarText.setFill(Color.RED);
        editReceiverMiddleEarText.setFill(Color.RED);
      }
      if (donorOrganInventory.getBone() && receiverOrganInventory.getBone()) {
        editBoneText.setFill(Color.RED);
        editReceiverBoneText.setFill(Color.RED);
      }
      if (donorOrganInventory.getBoneMarrow() && receiverOrganInventory.getBoneMarrow()) {
        editBoneMarrowText.setFill(Color.RED);
        editReceiverBoneMarrowText.setFill(Color.RED);
      }
      if (donorOrganInventory.getSkin() && receiverOrganInventory.getSkin()) {
        editSkinText.setFill(Color.RED);
        editReceiverSkinText.setFill(Color.RED);
      }
      if (donorOrganInventory.getConnectiveTissue() && receiverOrganInventory
          .getConnectiveTissue()) {
        editConnectiveTissueText.setFill(Color.RED);
        editReceiverConnectiveTissueText.setFill(Color.RED);
      }
    }
  }


  /**
   * Initializes the contact details tab with the selected donorReceiver(the donor) current values
   */
  private void initContactDetails() {

    editStreetAddress.setText(
        String.format("%s", donorReceiver.getContactDetails().getAddress().getStreetAddressLn1()));
    editCity
        .setText(String.format("%s", donorReceiver.getContactDetails().getAddress().getCityName()));
    editRegion
        .setText(String.format("%s", donorReceiver.getContactDetails().getAddress().getRegion()));
    editPostcode
        .setText(String.format("%s", donorReceiver.getContactDetails().getAddress().getPostCode()));
    editMobileNumber.setText(String.format("%s", donorReceiver.getContactDetails().getMobileNum()));
    editHomeNumber.setText(String.format("%s", donorReceiver.getContactDetails().getHomeNum()));
    editEmail.setText(String.format("%s", donorReceiver.getContactDetails().getEmail()));

    editEmergStreetAddress.setText(String.format("%s",
        donorReceiver.getEmergencyContactDetails().getAddress().getStreetAddressLn1()));
    editEmergCity.setText(
        String.format("%s", donorReceiver.getEmergencyContactDetails().getAddress().getCityName()));
    editEmergRegion.setText(
        String.format("%s", donorReceiver.getEmergencyContactDetails().getAddress().getRegion()));
    editEmergPostcode.setText(
        String.format("%s", donorReceiver.getEmergencyContactDetails().getAddress().getPostCode()));
    editEmergMobileNumber
        .setText(String.format("%s", donorReceiver.getEmergencyContactDetails().getMobileNum()));
    editEmergHomeNumber
        .setText(String.format("%s", donorReceiver.getEmergencyContactDetails().getHomeNum()));
    editEmergEmail
        .setText(String.format("%s", donorReceiver.getEmergencyContactDetails().getEmail()));

  }


  /**
   * Initializes the basic infomation tab with the selected donorReceiver(the donor) current values
   */
  private void initBasicInfomation() {

    editGivenNames.setText(donorReceiver.getFirstName() + " " + donorReceiver.getMiddleName());
    editLastName.setText(donorReceiver.getLastName());
    preferredName.setText(donorReceiver.getPreferredName());
    editNHINumber.setText(String.format("%s", donorReceiver.getUserName().toString()));
    editDateOfBirth.setValue(donorReceiver.getDateOfBirth());
    if (donorReceiver.getDateOfDeath() == null) {
      editDateOfDeath.getEditor().setText("");
    } else {
      editDateOfDeath.setValue(donorReceiver.getDateOfDeath());
    }
    editGender.getSelectionModel().select(donorReceiver.genderString());
    birthGender.getSelectionModel().select(donorReceiver.birthGenderString());
    editLivedInUKFrance.setSelected(donorReceiver.getLivedInUKFlag());
    editHeight.setText(String.format("%s", donorReceiver.getUserAttributeCollection().getHeight()));
    editWeight.setText(String.format("%s", donorReceiver.getUserAttributeCollection().getWeight()));
    editBloodType.getSelectionModel()
        .select(donorReceiver.getUserAttributeCollection().getBloodType());
    editTitle.getSelectionModel().select(donorReceiver.titleString());
  }


  /**
   * Initializes the medical history tab with the selected donorReceiver(the donor) current values
   */
  private void initMedicalHistory() {

    editSmoker.setSelected(
        BooleanExtension.getBoolean(donorReceiver.getUserAttributeCollection().getSmoker()));
    editAlcoholConsumption.setText(
        String.valueOf(donorReceiver.getUserAttributeCollection().getAlcoholConsumption()));
    editBloodPressure.setText(donorReceiver.getUserAttributeCollection().getBloodPressure());
    editChronicDiseases.setText(donorReceiver.getUserAttributeCollection().getChronicDiseases());

  }

  //=============================================================//
  //Basic Information Tab
  //=============================================================//

  /**
   * Retrieves the inputted string, converts to given and other names and then executes update.
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void givenNameEntered(ActionEvent event) {
    if (!editGivenNames.getText()
        .equals(donorReceiver.getFirstName() + " " + donorReceiver.getMiddleName())) {
      String name = editGivenNames.getText();
      name = name.trim();
      if (name.indexOf(" ") != -1) {
        String otherNames = name.substring(name.indexOf(" ") + 1);
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "profile", "givenName",
            name.substring(0, name.indexOf(" ")));
        if (showBadInputMessageIfError(donorReceiver.getUpdateMessage())) {
          errorMessage = errorMessage + donorReceiver.getUpdateMessage();
          givenNamesText.setFill(Color.RED);
          editGivenNames.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
        } else {
          givenNamesText.setFill(Color.BLACK);
          editGivenNames.setStyle("-fx-focus-color: transparent;");
        }
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "profile", "otherName",
            otherNames);
        if (showBadInputMessageIfError(donorReceiver.getUpdateMessage())) {
          errorMessage = errorMessage + donorReceiver.getUpdateMessage();
          givenNamesText.setFill(Color.RED);
          editGivenNames.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
        } else {
          givenNamesText.setFill(Color.BLACK);
          editGivenNames.setStyle("-fx-focus-color: transparent;");
        }
        donorReceiver.setUpdateMessage("waiting for new message");
      } else {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "profile", "givenName",
            name);
        if (showBadInputMessageIfError(donorReceiver.getUpdateMessage())) {
          errorMessage = errorMessage + donorReceiver.getUpdateMessage();
          givenNamesText.setFill(Color.RED);
          editGivenNames.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
        } else {
          givenNamesText.setFill(Color.BLACK);
          editGivenNames.setStyle("-fx-focus-color: transparent;");
        }
        donorReceiver.setUpdateMessage("waiting for new message");
      }
    }
  }

  /**
   * Retrieves the inputted string, and updates accordingly
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void lastNameEntered(ActionEvent event) {
    if (!editLastName.getText().equals(donorReceiver.getLastName())) {
      db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "profile", "lastName",
          editLastName.getText());
      if (showBadInputMessageIfError(donorReceiver.getUpdateMessage())) {
        errorMessage = errorMessage + donorReceiver.getUpdateMessage();
        lastNameText.setFill(Color.RED);
        editLastName.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
      } else {
        lastNameText.setFill(Color.BLACK);
        editLastName.setStyle("-fx-focus-color: transparent;");
      }
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Retrieves the inputted string, and updates accordingly.
   *
   * @param event When an event occurs in the GUI.
   */
  @FXML
  void preferredNameEntered(ActionEvent event) {
    if (preferredName.getText() == null) {
      preferredName.setText("");
    }

    if (!preferredName.getText().equals(donorReceiver.getPreferredName())) {

      db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "profile", "preferredName",
          preferredName.getText());
      if (showBadInputMessageIfError(donorReceiver.getUpdateMessage())) {
        errorMessage = errorMessage + donorReceiver.getUpdateMessage();
        preferredNameText.setFill(Color.RED);
        preferredName.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
      } else {
        preferredNameText.setFill(Color.BLACK);
        preferredName.setStyle("-fx-focus-color: transparent;");
      }
      donorReceiver.setUpdateMessage("waiting for new message");

    }

  }

  /**
   * Retrieves the inputted String, and updates the NHI accordingly. Currently not in use, as unsure
   * whether NHI should be changed.
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void nhiEntered(ActionEvent event) {
    if (!editNHINumber.getText().equals("") && !editNHINumber.getText()
        .equals(donorReceiver.getUserName())) {
      String newNhi = editNHINumber.getText();
      db.issueCommand(AccountManager.getCurrentUser(), "update", donorReceiver.getUserName(),
          "profile", "NHI", newNhi);
      if (showBadInputMessageIfError(donorReceiver.getUpdateMessage())) {
        errorMessage = errorMessage + donorReceiver.getUpdateMessage();
        nhiText.setFill(Color.RED);
        editNHINumber.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
      } else {
        nhiText.setFill(Color.BLACK);
        editNHINumber.setStyle("-fx-focus-color: transparent;");
      }
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Retrieves the given date and attempts to update the donorReceivers date of birth
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void dobEntered(ActionEvent event) {
    LocalDate date;
    try {
      date = editDateOfBirth.getValue();
      if ((date != null) && (!date.isEqual(donorReceiver.getDateOfBirth()))) {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "profile", "dateOfBirth",
            DonorReceiver.formatDateToString(date).replace("-", ""));
        if (showBadInputMessageIfError(donorReceiver.getUpdateMessage())) {
          errorMessage = errorMessage + donorReceiver.getUpdateMessage();
          dateOfBirthText.setFill(Color.RED);
          editDateOfBirth.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
        } else {
          dateOfBirthText.setFill(Color.BLACK);
          editDateOfDeath.setStyle("-fx-focus-color: transparent;");
        }
        donorReceiver.setUpdateMessage("waiting for new message");
      }
    } catch (Exception e) {
      System.out.println(
          "The date of birth was either empty or formatted incorrectly. Please use the format DD/MM/YYYY.");
    }
  }

  /**
   * Retrieves the given date and attempts to update the donorReceivers date of death
   */
  @FXML
  void DODEntered(ActionEvent event) {
    LocalDate date;
    if (editDateOfDeath.getEditor().getText().isEmpty() && donorReceiver.getDateOfDeath() != null) {
      db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "profile", "dateOfDeath", "");
      if (showBadInputMessageIfError(donorReceiver.getUpdateMessage())) {
        errorMessage = errorMessage + donorReceiver.getUpdateMessage();
        dateOfDeathText.setFill(Color.RED);
        editDateOfDeath.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
      } else {
        dateOfDeathText.setFill(Color.BLACK);
        editDateOfDeath.setStyle("-fx-focus-color: transparent;");
      }
      donorReceiver.setUpdateMessage("waiting for new message");
    } else {
      try {
        date = editDateOfDeath.getValue();
        if ((((date != null) && (donorReceiver.getDateOfDeath() != null)) && (!date
            .isEqual(donorReceiver.getDateOfDeath()))) ||
            ((date != null) && (donorReceiver.getDateOfDeath() == null))) {

          db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "profile", "dateOfDeath",
              DonorReceiver.formatDateToString(date).replace("-", ""));
          if (donorReceiver.getUpdateMessage().substring(0, 5).equals("ERROR")) {
            showBadInputMessageIfError(
                "ERROR: Invalid value " + DonorReceiver.formatDateToStringSlash(date)
                    + ". Date of death must be a valid date that is before the " +
                    "current date and after the donor's date of birth. It also has to be in the following format: "
                    +
                    "DD/MM/YYYY.\n");
            dateOfDeathText.setFill(Color.RED);
            errorMessage = errorMessage + donorReceiver.getUpdateMessage();
            editDateOfDeath.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
          } else {
            dateOfDeathText.setFill(Color.BLACK);
            editDateOfDeath.setStyle("-fx-focus-color: transparent;");
          }
          donorReceiver.setUpdateMessage("waiting for new message");
        }
      } catch (Exception e) {
        System.out.println(
            "The date of death was either empty or formatted incorrectly. Please use the format DD/MM/YYYY.");
        System.out.print(e);
      }
    }
  }

  /**
   * Retrieves the selected gender, and attempts to update the donorReceivers gender
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void selectGender(ActionEvent event) {
    String gender = String.valueOf(editGender.getSelectionModel().getSelectedIndex());
    gender = convertGenderIndexToValue(gender);

    if (gender.equals("")) {
      char g = donorReceiver.getGender();
      gender = String.format("%c", g);
    }

    if (donorReceiver.getGender() != gender.charAt(0)) {
      db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "profile", "gender", gender);
      if (showBadInputMessageIfError(donorReceiver.getUpdateMessage())) {
        errorMessage = errorMessage + donorReceiver.getUpdateMessage();
        genderText.setFill(Color.RED);
      } else {
        genderText.setFill(Color.BLACK);
      }
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Retrieves the selected birth gender and attempts to update the donorReceiver.
   *
   * @param event When an event occurs in the GUI.
   */
  @FXML
  void selectBirthGender(ActionEvent event) {

    String value = String.valueOf(birthGender.getSelectionModel().getSelectedIndex());
    value = convertGenderIndexToValue(value);

    if (value.equals("")) {
      char g = donorReceiver.getBirthGender();
      value = String.format("%c", g);
    }

    if (donorReceiver.getBirthGender() != value.charAt(0)) {
      db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "profile", "birthGender",
          value);
      if (showBadInputMessageIfError(donorReceiver.getUpdateMessage())) {
        errorMessage = errorMessage + donorReceiver.getUpdateMessage();
        birthGenderText.setFill(Color.RED);
      } else {
        birthGenderText.setFill(Color.BLACK);
      }
      donorReceiver.setUpdateMessage("waiting for new message");
    }

  }

  /**
   * Retrieves the selected title, and attempts to update the donorReceivers title
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void titleEntered(ActionEvent event) {
    if (editTitle.getSelectionModel().getSelectedIndex() >= 0) {
      String title = String.valueOf(editTitle.getSelectionModel().getSelectedIndex());
      if (Integer.parseInt(title) == 0) {
        title = "Mr";
      } else if (Integer.parseInt(title) == 1) {
        title = "Mrs";
      } else if (Integer.parseInt(title) == 2) {
        title = "Ms";
      } else if (Integer.parseInt(title) == 3) {
        title = "Master";
      } else {
        title = donorReceiver.getTitle();
      }
      if (!(StringExtension.nullCompare(donorReceiver.titleString(), title))) {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "profile", "title", title);
        if (showBadInputMessageIfError(donorReceiver.getUpdateMessage())) {
          errorMessage = errorMessage + donorReceiver.getUpdateMessage();
          titleText.setFill(Color.RED);
        } else {
          titleText.setFill(Color.BLACK);
        }
        donorReceiver.setUpdateMessage("waiting for new message");
      }
    }


  }

  /**
   * Retrieves the given string, and attempts to update the donorReceivers height
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void heightEntered(ActionEvent event) {
    if (!editHeight.getText()
        .equals(String.valueOf(donorReceiver.getUserAttributeCollection().getHeight()))) {
      db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "attributes", "height",
          editHeight.getText());
      if (showBadInputMessageIfError(donorReceiver.getUpdateMessage())) {
        errorMessage = errorMessage + donorReceiver.getUpdateMessage();
        heightText.setFill(Color.RED);
        editHeight.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
      } else {
        heightText.setFill(Color.BLACK);
        editHeight.setStyle("-fx-focus-color: transparent;");
      }
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Retrieves the given string and attempts to update the donorReceivers weight
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void weightEntered(ActionEvent event) {
    if (!editWeight.getText()
        .equals(String.valueOf(donorReceiver.getUserAttributeCollection().getWeight()))) {
      db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "attributes", "weight",
          editWeight.getText());
      if (showBadInputMessageIfError(donorReceiver.getUpdateMessage())) {
        errorMessage = errorMessage + donorReceiver.getUpdateMessage();
        weightText.setFill(Color.RED);
        editWeight.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
      } else {
        weightText.setFill(Color.BLACK);
        editWeight.setStyle("-fx-focus-color: transparent;");
      }
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Retrieves the selected blood type, and attempts to update the donorReceivers blood type
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void bloodTypeSelected(ActionEvent event) {

    String blood = String.valueOf(editBloodType.getSelectionModel().getSelectedIndex());
    if (Integer.parseInt(blood) == 0) {
      blood = "A+";
    } else if (Integer.parseInt(blood) == 1) {
      blood = "B+";
    } else if (Integer.parseInt(blood) == 2) {
      blood = "O+";
    } else if (Integer.parseInt(blood) == 3) {
      blood = "A-";
    } else if (Integer.parseInt(blood) == 4) {
      blood = "B-";
    } else if (Integer.parseInt(blood) == 5) {
      blood = "O-";
    } else if (Integer.parseInt(blood) == 6) {
      blood = "AB+";
    } else if (Integer.parseInt(blood) == 7) {
      blood = "AB-";
    } else {
      blood = null;
    }
    if (!(StringExtension
        .nullCompare(donorReceiver.getUserAttributeCollection().getBloodType(), blood))
        && blood != null) {
      db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "attributes", "bloodType",
          blood);
      if (showBadInputMessageIfError(donorReceiver.getUpdateMessage())) {
        errorMessage = errorMessage + donorReceiver.getUpdateMessage();
        bloodTypeText.setFill(Color.RED);
      } else {
        bloodTypeText.setFill(Color.BLACK);
      }
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Retrieves whether the checkbox is selected or not, and attempts to update the donorReceivers
   * lived in UK/France flag
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void livedInUKChecked(ActionEvent event) {
    if (donorReceiver.getLivedInUKFlag() != editLivedInUKFrance.isSelected()) {
      if (editLivedInUKFrance.isSelected()) {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "profile", "livedInUKFlag",
            "true");
      } else {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "profile", "livedInUKFlag",
            "false");
      }
      if (showBadInputMessageIfError(donorReceiver.getUpdateMessage())) {
        errorMessage = errorMessage + donorReceiver.getUpdateMessage();
        ukFranceText.setFill(Color.RED);
      } else {
        ukFranceText.setFill(Color.BLACK);
      }
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }
  //=============================================================//

  //=============================================================//
  //Contact Details Tab
  //=============================================================//

  /**
   * Retrieves the given string and attempts to update the donorReceivers street address
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void streetAddressEntered(ActionEvent event) {
    if (!editStreetAddress.getText()
        .equals(donorReceiver.getContactDetails().getAddress().getStreetAddressLn1())) {
      db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "contacts", "addressStreet",
          editStreetAddress.getText());
      if (showBadInputMessageIfError(donorReceiver.getUpdateMessage())) {
        errorMessage = errorMessage + donorReceiver.getUpdateMessage();
        streetText.setFill(Color.RED);
        editStreetAddress.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
      } else {
        streetText.setFill(Color.BLACK);
        editStreetAddress.setStyle("-fx-focus-color: transparent;");
      }
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Retrieves the given string, and attempts to update the donorReceivers city
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void cityEntered(ActionEvent event) {
    if (!editCity.getText().equals(donorReceiver.getContactDetails().getAddress().getCityName())) {
      db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "contacts", "addressCity",
          editCity.getText());
      if (showBadInputMessageIfError(donorReceiver.getUpdateMessage())) {
        errorMessage = errorMessage + donorReceiver.getUpdateMessage();
        cityText.setFill(Color.RED);
        editCity.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
      } else {
        cityText.setFill(Color.BLACK);
        editCity.setStyle("-fx-focus-color: transparent;");
      }
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Retrieves the given string and attempts to update the donorReceivers region
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void regionEntered(ActionEvent event) {
    if (!editRegion.getText().equals(donorReceiver.getContactDetails().getAddress().getRegion())) {
      db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "contacts", "addressRegion",
          editRegion.getText());
      if (showBadInputMessageIfError(donorReceiver.getUpdateMessage())) {
        errorMessage = errorMessage + donorReceiver.getUpdateMessage();
        regionText.setFill(Color.RED);
        editRegion.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
      } else {
        regionText.setFill(Color.BLACK);
        editRegion.setStyle("-fx-focus-color: transparent;");
      }
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Retrieves the given string and attempts to update the donorReceivers postcode
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void postCodeEntered(ActionEvent event) {
    if (!editPostcode.getText()
        .equals(donorReceiver.getContactDetails().getAddress().getPostCode())) {
      db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "contacts", "addressPostcode",
          editPostcode.getText());
      if (showBadInputMessageIfError(donorReceiver.getUpdateMessage())) {
        errorMessage = errorMessage + donorReceiver.getUpdateMessage();
        postcodeText.setFill(Color.RED);
        editPostcode.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
      } else {
        postcodeText.setFill(Color.BLACK);
        editPostcode.setStyle("-fx-focus-color: transparent;");
      }
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Retrieves the given string and attempts to update the donorReceivers mobile number
   */
  @FXML
  void mobileNumberEntered(ActionEvent event) {
    if (!editMobileNumber.getText().equals(donorReceiver.getContactDetails().getMobileNum())) {
      db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "contacts", "mobileNumber",
          editMobileNumber.getText());
      if (showBadInputMessageIfError(donorReceiver.getUpdateMessage())) {
        errorMessage = errorMessage + donorReceiver.getUpdateMessage();
        mobileNumberText.setFill(Color.RED);
        editMobileNumber.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
      } else {
        mobileNumberText.setFill(Color.BLACK);
        editMobileNumber.setStyle("-fx-focus-color: transparent;");
      }
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Retrieves the given string and attempts to update the donorReceivers home number
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void homeNumberEntered(ActionEvent event) {
    if (!editHomeNumber.getText().equals(donorReceiver.getContactDetails().getHomeNum())) {
      db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "contacts", "homeNumber",
          editHomeNumber.getText());
      if (showBadInputMessageIfError(donorReceiver.getUpdateMessage())) {
        errorMessage = errorMessage + donorReceiver.getUpdateMessage();
        homeNumberText.setFill(Color.RED);
        editHomeNumber.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
      } else {
        homeNumberText.setFill(Color.BLACK);
        editHomeNumber.setStyle("-fx-focus-color: transparent;");
      }
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Retrieves the given string and attempts to update the donorReceivers email
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void emailEntered(ActionEvent event) {
    if (!editEmail.getText().equals(donorReceiver.getContactDetails().getEmail())) {
      db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "contacts", "email",
          editEmail.getText());
      if (showBadInputMessageIfError(donorReceiver.getUpdateMessage())) {
        errorMessage = errorMessage + donorReceiver.getUpdateMessage();
        emailText.setFill(Color.RED);
        editEmail.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
      } else {
        emailText.setFill(Color.BLACK);
        editEmail.setStyle("-fx-focus-color: transparent;");
      }
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Retrieves the given string and attempts to update the donorReceivers emergency street address.
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void emStreetAddressEntered(ActionEvent event) {
    if (!editEmergStreetAddress.getText()
        .equals(donorReceiver.getEmergencyContactDetails().getAddress().getStreetAddressLn1())) {
      db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "contacts",
          "emergAddressStreet", editEmergStreetAddress.getText());
      if (showBadInputMessageIfError(donorReceiver.getUpdateMessage())) {
        errorMessage = errorMessage + donorReceiver.getUpdateMessage();
        emergStreetText.setFill(Color.RED);
        editEmergStreetAddress.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
      } else {
        emergStreetText.setFill(Color.BLACK);
        editEmergStreetAddress.setStyle("-fx-focus-color: transparent;");
      }
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Retrieves the given string and attempts to update the donorReceivers emergency city address
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void emCityEntered(ActionEvent event) {
    if (!editEmergCity.getText()
        .equals(donorReceiver.getEmergencyContactDetails().getAddress().getCityName())) {
      db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "contacts",
          "emergAddressCity", editEmergCity.getText());
      if (showBadInputMessageIfError(donorReceiver.getUpdateMessage())) {
        errorMessage = errorMessage + donorReceiver.getUpdateMessage();
        emergCityText.setFill(Color.RED);
        editEmergCity.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
      } else {
        emergCityText.setFill(Color.BLACK);
        editEmergCity.setStyle("-fx-focus-color: transparent;");
      }
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Retrieves the given string and attempts to update the donorReceivers emergency region
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void emRegionEntered(ActionEvent event) {
    if (!editEmergRegion.getText()
        .equals(donorReceiver.getEmergencyContactDetails().getAddress().getRegion())) {
      db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "contacts",
          "emergAddressRegion", editEmergRegion.getText());
      if (showBadInputMessageIfError(donorReceiver.getUpdateMessage())) {
        errorMessage = errorMessage + donorReceiver.getUpdateMessage();
        emergRegionText.setFill(Color.RED);
        editEmergRegion.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
      } else {
        emergRegionText.setFill(Color.BLACK);
        editEmergRegion.setStyle("-fx-focus-color: transparent;");
      }
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Retrieves the given string and attempts to update the donorReceivers emergency postcode
   */
  @FXML
  void emPostCodeEntered(ActionEvent event) {
    if (!editEmergPostcode.getText()
        .equals(donorReceiver.getEmergencyContactDetails().getAddress().getPostCode())) {
      db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "contacts",
          "emergAddressPostcode", editEmergPostcode.getText());
      if (showBadInputMessageIfError(donorReceiver.getUpdateMessage())) {
        errorMessage = errorMessage + donorReceiver.getUpdateMessage();
        emergPostcodeText.setFill(Color.RED);
        editEmergPostcode.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
      } else {
        emergPostcodeText.setFill(Color.BLACK);
        editEmergPostcode.setStyle("-fx-focus-color: transparent;");
      }
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Retreieves the given string and attempts to update the donorReceivers emergency mobile number
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void emMobileNumberEntered(ActionEvent event) {
    if (!editEmergMobileNumber.getText()
        .equals(donorReceiver.getEmergencyContactDetails().getMobileNum())) {
      db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "contacts",
          "emergMobileNumber", editEmergMobileNumber.getText());
      if (showBadInputMessageIfError(donorReceiver.getUpdateMessage())) {
        errorMessage = errorMessage + donorReceiver.getUpdateMessage();
        emergMobileNumberText.setFill(Color.RED);
        editEmergMobileNumber.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
      } else {
        emergMobileNumberText.setFill(Color.BLACK);
        editEmergMobileNumber.setStyle("-fx-focus-color: transparent;");
      }
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Retrieves the given string and attempts to update the donorReceivers emergency home number
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void emHomeNumberEntered(ActionEvent event) {
    if (!editEmergHomeNumber.getText()
        .equals(donorReceiver.getEmergencyContactDetails().getHomeNum())) {
      db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "contacts", "emergHomeNumber",
          editEmergHomeNumber.getText());
      if (showBadInputMessageIfError(donorReceiver.getUpdateMessage())) {
        errorMessage = errorMessage + donorReceiver.getUpdateMessage();
        emergHomeNumberText.setFill(Color.RED);
        editEmergHomeNumber.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
      } else {
        emergHomeNumberText.setFill(Color.BLACK);
        editEmergHomeNumber.setStyle("-fx-focus-color: transparent;");
      }
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Retrieves the given string and attempts to update the donorReceivers emergency email address
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void emEmailEntered(ActionEvent event) {
    if (!editEmergEmail.getText().equals(donorReceiver.getEmergencyContactDetails().getEmail())) {
      db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "contacts", "emergEmail",
          editEmergEmail.getText());
      if (showBadInputMessageIfError(donorReceiver.getUpdateMessage())) {
        errorMessage = errorMessage + donorReceiver.getUpdateMessage();
        emergEmailText.setFill(Color.RED);
        editEmergEmail.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
      } else {
        emergEmailText.setFill(Color.BLACK);
        editEmergEmail.setStyle("-fx-focus-color: transparent;");
      }
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }
  //=============================================================//

  //=============================================================//
  //Organs Tab
  //=============================================================//

  /**
   * Checks whether the lungs checkbox is selected, and attempts to update the donorReceivers lungs
   * value
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void selectedLungs(ActionEvent event) {
    if (donorReceiver.getDonorOrganInventory().getLungs() != editLung.isSelected()) {
      if (editLung.isSelected()) {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "lungs", "true");
      } else {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "lungs", "false");
      }
      showBadInputMessageIfError(donorReceiver.getUpdateMessage());
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Checks whether the liver checkbox is selected, and attempts to update the donorReceivers liver
   * value
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void selectedLiver(ActionEvent event) {
    if (donorReceiver.getDonorOrganInventory().getLiver() != editLiver.isSelected()) {
      if (editLiver.isSelected()) {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "liver", "true");
      } else {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "liver", "false");
      }
      showBadInputMessageIfError(donorReceiver.getUpdateMessage());
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Checks whether the bone checkbox is selected and attempts to update the donorReceivers bone
   * value
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void selectedBone(ActionEvent event) {
    if (donorReceiver.getDonorOrganInventory().getBone() != editBone.isSelected()) {
      if (editBone.isSelected()) {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "bone", "true");
      } else {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "bone", "false");
      }
      showBadInputMessageIfError(donorReceiver.getUpdateMessage());
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Checks whether the bone marrow checkbox is selected, and attempts to update the donorReceivers
   * bone marrow value
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void selectedBoneMarrow(ActionEvent event) {
    if (donorReceiver.getDonorOrganInventory().getBoneMarrow() != editBoneMarrow.isSelected()) {
      if (editBoneMarrow.isSelected()) {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "boneMarrow",
            "true");
      } else {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "boneMarrow",
            "false");
      }
      showBadInputMessageIfError(donorReceiver.getUpdateMessage());
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Checks whether the connective tissue checkbox is selected, and attempts to update the
   * donorReceivers connective tissue value
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void selectedConnectiveTissue(ActionEvent event) {
    if (donorReceiver.getDonorOrganInventory().getConnectiveTissue() != editConnectiveTissue
        .isSelected()) {
      if (editConnectiveTissue.isSelected()) {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs",
            "connectiveTissue", "true");
      } else {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs",
            "connectiveTissue", "false");
      }
      showBadInputMessageIfError(donorReceiver.getUpdateMessage());
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Checks whether the corneas checkbox is selected, and attempts to update the donorReceivers
   * corneas value
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void selectedCorneas(ActionEvent event) {
    if (donorReceiver.getDonorOrganInventory().getCorneas() != editCornea.isSelected()) {
      if (editCornea.isSelected()) {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "corneas",
            "true");
      } else {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "corneas",
            "false");
      }
      showBadInputMessageIfError(donorReceiver.getUpdateMessage());
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Checks whether the heart checkbox is selected, and attempts to update the donorReceivers heart
   * value
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void selectedHeart(ActionEvent event) {
    if (donorReceiver.getDonorOrganInventory().getHeart() != editHeart.isSelected()) {
      if (editHeart.isSelected()) {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "heart", "true");
      } else {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "heart", "false");
      }
      showBadInputMessageIfError(donorReceiver.getUpdateMessage());
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Checks whether the intestines checkbox is selected, and attempts to update the donorReceivers
   * intestines value
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void selectedIntestines(ActionEvent event) {
    if (donorReceiver.getDonorOrganInventory().getIntestine() != editIntestine.isSelected()) {
      if (editIntestine.isSelected()) {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "intestines",
            "true");
      } else {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "intestines",
            "false");
      }
      showBadInputMessageIfError(donorReceiver.getUpdateMessage());
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Checks whether the kidney checkbox is selected, and attempts to updates the donorReceivers
   * kidney value
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void selectedKidneys(ActionEvent event) {
    if (donorReceiver.getDonorOrganInventory().getKidneys() != editKidney.isSelected()) {
      if (editKidney.isSelected()) {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "kidneys",
            "true");
      } else {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "kidneys",
            "false");
      }
      showBadInputMessageIfError(donorReceiver.getUpdateMessage());
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Checks whether the middle ears checkbox has been selected, and attempts to update the
   * donorReceivers middle ears value
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void selectedMiddleEars(ActionEvent event) {
    if (donorReceiver.getDonorOrganInventory().getMiddleEars() != editMiddleEar.isSelected()) {
      if (editMiddleEar.isSelected()) {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "middleEars",
            "true");
      } else {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "middleEars",
            "false");
      }
      showBadInputMessageIfError(donorReceiver.getUpdateMessage());
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Checks whether the pancreas checkbox is selected, and attempts to update the donorReceivers
   * pancreas value
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void selectedPancreas(ActionEvent event) {
    if (donorReceiver.getDonorOrganInventory().getPancreas() != editPancreas.isSelected()) {
      if (editPancreas.isSelected()) {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "pancreas",
            "true");
      } else {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "pancreas",
            "false");
      }
      showBadInputMessageIfError(donorReceiver.getUpdateMessage());
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Checks whether the skin checkbox is slected, and attempts to update the donorReceivers skin
   * value
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void selectedSkin(ActionEvent event) {
    if (donorReceiver.getDonorOrganInventory().getSkin() != editSkin.isSelected()) {
      if (editSkin.isSelected()) {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "skin", "true");
      } else {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "skin", "false");
      }
      showBadInputMessageIfError(donorReceiver.getUpdateMessage());
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  // Receiving organs

  /**
   * Checks whether the lungs checkbox is selected, and attempts to update the donorReceivers lungs
   * value
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void selectedLungsReceiving(ActionEvent event) {
    if (donorReceiver.getRequiredOrgans().getLungs() != editReceiverLung.isSelected()) {
      if (editReceiverLung.isSelected()) {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "receiver",
            "lungs", "true");
      } else {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "receiver",
            "lungs", "false");
      }
      showBadInputMessageIfError(donorReceiver.getUpdateMessage());
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Checks whether the liver checkbox is selected, and attempts to update the donorReceivers liver
   * value
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void selectedLiverReceiving(ActionEvent event) {
    if (donorReceiver.getRequiredOrgans().getLiver() != editReceiverLiver.isSelected()) {
      if (editReceiverLiver.isSelected()) {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "receiver",
            "liver", "true");
      } else {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "receiver",
            "liver", "false");
      }
      showBadInputMessageIfError(donorReceiver.getUpdateMessage());
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Checks whether the bone checkbox is selected and attempts to update the donorReceivers bone
   * value
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void selectedBoneReceiving(ActionEvent event) {
    if (donorReceiver.getRequiredOrgans().getBone() != editReceiverBone.isSelected()) {
      if (editReceiverBone.isSelected()) {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "receiver",
            "bone", "true");
      } else {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "receiver",
            "bone", "false");
      }
      showBadInputMessageIfError(donorReceiver.getUpdateMessage());
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Checks whether the bone marrow checkbox is selected, and attempts to update the donorReceivers
   * bone marrow value
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void selectedBoneMarrowReceiving(ActionEvent event) {
    if (donorReceiver.getRequiredOrgans().getBoneMarrow() != editReceiverBoneMarrow.isSelected()) {
      if (editReceiverBoneMarrow.isSelected()) {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "receiver",
            "boneMarrow", "true");
      } else {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "receiver",
            "boneMarrow", "false");
      }
      showBadInputMessageIfError(donorReceiver.getUpdateMessage());
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Checks whether the connective tissue checkbox is selected, and attempts to update the
   * donorReceivers connective tissue value
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void selectedConnectiveTissueReceiving(ActionEvent event) {
    if (donorReceiver.getRequiredOrgans().getConnectiveTissue() != editReceiverConnectiveTissue
        .isSelected()) {
      if (editReceiverConnectiveTissue.isSelected()) {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "receiver",
            "connectiveTissue", "true");
      } else {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "receiver",
            "connectiveTissue", "false");
      }
      showBadInputMessageIfError(donorReceiver.getUpdateMessage());
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Checks whether the corneas checkbox is selected, and attempts to update the donorReceivers
   * corneas value
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void selectedCorneasReceiving(ActionEvent event) {
    if (donorReceiver.getRequiredOrgans().getCorneas() != editReceiverCornea.isSelected()) {
      if (editReceiverCornea.isSelected()) {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "receiver",
            "corneas", "true");
      } else {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "receiver",
            "corneas", "false");
      }
      showBadInputMessageIfError(donorReceiver.getUpdateMessage());
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Checks whether the heart checkbox is selected, and attempts to update the donorReceivers heart
   * value
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void selectedHeartReceiving(ActionEvent event) {
    if (donorReceiver.getRequiredOrgans().getHeart() != editReceiverHeart.isSelected()) {
      if (editReceiverHeart.isSelected()) {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "receiver",
            "heart", "true");
      } else {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "receiver",
            "heart", "false");
      }
      showBadInputMessageIfError(donorReceiver.getUpdateMessage());
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Checks whether the intestines checkbox is selected, and attempts to update the donorReceivers
   * intestines value
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void selectedIntestinesReceiving(ActionEvent event) {
    if (donorReceiver.getRequiredOrgans().getIntestine() != editReceiverIntestine.isSelected()) {
      if (editReceiverIntestine.isSelected()) {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "receiver",
            "intestines", "true");
      } else {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "receiver",
            "intestines", "false");
      }
      showBadInputMessageIfError(donorReceiver.getUpdateMessage());
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Checks whether the kidney checkbox is selected, and attempts to updates the donorReceivers
   * kidney value
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void selectedKidneysReceiving(ActionEvent event) {
    if (donorReceiver.getRequiredOrgans().getKidneys() != editReceiverKidney.isSelected()) {
      if (editReceiverKidney.isSelected()) {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "receiver",
            "kidneys", "true");
      } else {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "receiver",
            "kidneys", "false");
      }
      showBadInputMessageIfError(donorReceiver.getUpdateMessage());
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Checks whether the middle ears checkbox has been selected, and attempts to update the
   * donorReceivers middle ears value
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void selectedMiddleEarsReceiving(ActionEvent event) {
    if (donorReceiver.getRequiredOrgans().getMiddleEars() != editReceiverMiddleEar.isSelected()) {
      if (editReceiverMiddleEar.isSelected()) {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "receiver",
            "middleEars", "true");
      } else {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "receiver",
            "middleEars", "false");
      }
      showBadInputMessageIfError(donorReceiver.getUpdateMessage());
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Checks whether the pancreas checkbox is selected, and attempts to update the donorReceivers
   * pancreas value
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void selectedPancreasReceiving(ActionEvent event) {
    if (donorReceiver.getRequiredOrgans().getPancreas() != editReceiverPancreas.isSelected()) {
      if (editReceiverPancreas.isSelected()) {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "receiver",
            "pancreas", "true");
      } else {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "receiver",
            "pancreas", "false");
      }
      showBadInputMessageIfError(donorReceiver.getUpdateMessage());
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Checks whether the skin checkbox is slected, and attempts to update the donorReceivers skin
   * value
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void selectedSkinReceiving(ActionEvent event) {
    if (donorReceiver.getRequiredOrgans().getSkin() != editReceiverSkin.isSelected()) {
      if (editReceiverSkin.isSelected()) {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "receiver",
            "skin", "true");
      } else {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "organs", "receiver",
            "skin", "false");
      }
      showBadInputMessageIfError(donorReceiver.getUpdateMessage());
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  //=============================================================//

  //=============================================================//
  //Medications Tab
  //=============================================================//

  /**
   * Hide the medications editing tab from donors, as only clinicians can edit medications.
   */
  void hideMedicationsEditingFromDonor() {
    if (AccountManager.getCurrentUser() instanceof DonorReceiver) {
      mainTabPane.getTabs().remove(medicationsTab);
    }
  }

  //=============================================================//

  //=============================================================//
  //Medical History Tab
  //=============================================================//


  @FXML
  /**
   *  Loads the create/update Illness GUI page when the 'create diagnosis' button is pressed and sets the Illness donor
   *  attribute to the current Account being edited.
   */
  void createDiagnosesButtonPressed(ActionEvent event) {
    createOrModifyIllnessController.setDonor(donorReceiver);

    loadIllnessPage();
  }


  /**
   * Load the illness window on top of the current scene.
   */
  public void loadIllnessPage() {
    try {
      // Create new pane.
      FXMLLoader loader = new FXMLLoader();
      VBox diseasePane = loader.load(getClass().getResourceAsStream(PageNav.ILLNESS));

      // Create new scene.
      Scene diseaseScene = new Scene(diseasePane);

      // Retrieve current stage and set scene.
      Stage current = (Stage) Cancel.getScene().getWindow();
      current.setScene(diseaseScene);

    } catch (IOException exception) {
      System.out.println("Error loading illness pane");
    }

  }


  /**
   * An alert error message dialog box which is called when there has been an error in the GUI
   * diagnosis selection.
   *
   * @param message A string of the selection error message to display in the alert box.
   */
  public void showSelectionErrorDialog(String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error Dialog");
    alert.setHeaderText("Invalid Selection");
    alert.setContentText(message);
    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    alert.showAndWait();
  }


  /**
   * Loads the create/update Illness GUI page when the 'create diagnosis' button is pressed and sets
   * the Illness donor attribute to the current Account being edited as well as the Illness illness
   * attribute to the diagnosis selected in the current diagnosis table in the GUI.
   */
  @FXML
  public void editCurrentDiagnosesButtonPressed(ActionEvent event) {
    Illness current = currentTable.getSelectionModel().getSelectedItem();
    if (current != null) {
      createOrModifyIllnessController.setIllness(current);
      createOrModifyIllnessController.setDonor(donorReceiver);
      loadIllnessPage();
    } else {
      showSelectionErrorDialog(
          "No current diagnosis has been selected. Please select a diagnosis to edit.");
    }
  }


  /**
   * Loads the create/update Illness GUI page when the 'create diagnosis' button is pressed and sets
   * the Illness donor attribute to the current Account being edited as well as the Illness illness
   * attribute to the diagnosis selected in the historic diagnosis table in the GUI.
   */
  @FXML
  void editHistoricDiagnosesButtonPressed(ActionEvent event) {
    Illness historic = historicTable.getSelectionModel().getSelectedItem();
    if (historic != null) {
      createOrModifyIllnessController.setIllness(historic);
      createOrModifyIllnessController.setDonor(donorReceiver);
      loadIllnessPage();
    } else {
      showSelectionErrorDialog(
          "No historic diagnosis has been selected. Please select a diagnosis to edit.");
    }
  }


  /**
   * Converts the boolean values of the cells in the chronic TableColumn into strings, 'true'
   * becomes 'chronic' (in red text), and 'false' becomes 'no' (in black text). This method is based
   * on code by two posts, one post from user jkaufmann on Stack Overflow on 11/10/2011 at 2.44pm.
   * See https://stackoverflow.com/questions/6998551/setting-font-color-of-javafx-tableview-cells.
   * The other post was from user Grimerie at 6/4/2016 at 6.05pm. See
   * https://stackoverflow.com/questions/36436169/boolean-to-string-in-tableview-javafx.
   */
  public void setChronicColoumn() {
    chronic.setSortable(false);
    chronic.setCellValueFactory(new PropertyValueFactory<Illness, String>("chronic"));
    //Converts boolean cell values into strings
    chronic.setCellValueFactory(cellData -> {
      boolean chronic = cellData.getValue().isChronic();
      String chronicString;
      if (chronic == true) {
        chronicString = "chronic";
      } else {
        chronicString = "no";
      }
      return new ReadOnlyStringWrapper(chronicString);
    });
    chronic
        .setCellFactory(new Callback<TableColumn<Illness, String>, TableCell<Illness, String>>() {
          @Override
          public TableCell<Illness, String> call(TableColumn<Illness, String> param) {
            return new TableCell<Illness, String>() {
              @Override
              public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                //override default CellFactory to colour the cell text depending on the cell value.
                if (!isEmpty()) {
                  if (item.contains("chronic")) {
                    this.setTextFill(Color.RED);
                  } else {
                    this.setTextFill(Color.BLACK);
                  }
                  setText(item);
                }
              }
            };
          }
        });
  }


  /**
   * A success dialog alert box given if an Illness object has been deleted. The given message is
   * displayed in the alert.
   *
   * @param message A string to display in the alert box, in the GUI.
   **/
  public void showDeletionSuccessMessage(String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Information Dialog");
    alert.setHeaderText("Deletion Success");
    alert.setContentText(
        String.format("%s Please save the application to make the deletion permanent.", message));
    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    alert.showAndWait();
  }

  @FXML
  /**
   * Checks if any Diagnoses have been selected for deletion by the user in the medical history GUI. If one or more
   * have been selected then callDeleteConfirmation is called. Otherwise an selection error message is shown.
   */
  void deleteDiagnosisButtonPressed(ActionEvent event) {
    Illness historic = historicTable.getSelectionModel().getSelectedItem();
    Illness current = currentTable.getSelectionModel().getSelectedItem();
    if (historic != null && current != null) {
      callDeleteConfirmation(historic, current, 2);
    } else if (historic != null) {
      callDeleteConfirmation(historic, null, 1);
    } else if (current != null) {
      callDeleteConfirmation(current, null, 1);
    } else {
      showSelectionErrorDialog(
          "No diagnosis has been selected. Please select a diagnosis to delete.");
    }
  }


  /**
   * Attempts to delete the given donor's illness diagnosis from their medical history. The user
   * must press the 'OK' button to proceed with the deletion. The user may optionally pick two
   * diagnoses to delete. A log of the deletion will be created if the deletion is carried out.
   *
   * @param diagnosis An Illness object to be deleted.
   * @param diagnosis2 An Illness object to be deleted.
   * @param number An integer signifying whether 1 or 2 Illness's will be deleted.
   */
  public void callDeleteConfirmation(Illness diagnosis, Illness diagnosis2, int number) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    alert.setTitle("Delete selected Diagnosis/Diagnoses.");
    alert.setHeaderText("You are about to perform a delete operation.");
    try {
      if (number == 1) {
        alert.setContentText(String
            .format("The Diagnosis %s will be deleted. Do you wish to proceed?",
                diagnosis.getName()));
      } else {
        alert.setContentText(
            String.format("The Diagnoses %s and %s will be deleted. Do you wish to proceed?",
                diagnosis.getName(), diagnosis2.getName()));
      }
      Optional<ButtonType> result = alert.showAndWait();
      if (result.get() == ButtonType.OK) {
        if (number == 1) {
          LogEntry logEntry = new LogEntry(donorReceiver, AccountManager.getCurrentUser(),
              "Illness '" + diagnosis.getName() + "'", "valid", "deleted");
//                    String log = donorReceiver.logIllnessStatus(diagnosis.getName(), "delete");
          donorReceiver.getMasterIllnessList().remove(diagnosis);
          donorReceiver.logChange(logEntry);
          showDeletionSuccessMessage(
              "Diagnosis " + diagnosis.getName() + " has been successfully deleted.");
        } else {
          LogEntry logEntry = new LogEntry(donorReceiver, AccountManager.getCurrentUser(),
              "Illness '" + diagnosis.getName() + "'", "valid", "deleted");
          donorReceiver.getMasterIllnessList().remove(diagnosis);
          donorReceiver.logChange(logEntry);
          LogEntry logEntry2 = new LogEntry(donorReceiver, AccountManager.getCurrentUser(),
              "Illness '" + diagnosis2.getName() + "'", "valid", "deleted");
          donorReceiver.getMasterIllnessList().remove(diagnosis2);
          donorReceiver.logChange(logEntry2);
          showDeletionSuccessMessage(String
              .format("Diagnoses %s and %s have been successfully deleted.", diagnosis.getName(),
                  diagnosis2.getName()));
        }
        donorReceiver.populateIllnessLists();
        currentDiagnoses = FXCollections.observableArrayList(donorReceiver.getCurrentDiagnoses());
        historicDiagnoses = FXCollections.observableArrayList(donorReceiver.getHistoricDiagnoses());
        currentTable.setItems(currentDiagnoses);
        currentTable.refresh();
        historicTable.setItems(historicDiagnoses);
        historicTable.refresh();
      }
    } catch (NullPointerException e) {
      System.out.println("ERROR: One or more of the given parameters is null.");
    }

  }

  //=============================================================//


  /**
   * Checks if the smoker checkbox is selected, and attempts to update the donorReceivers smoker
   * value
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void smokerChecked(ActionEvent event) {
    // BooleanExtension used to prevent null pointer exceptions. However,
    // this means the user cannot specify smoker as unknown. Consider replacing with drop-down.
    if (BooleanExtension.getBoolean(donorReceiver.getUserAttributeCollection().getSmoker())
        != editSmoker.isSelected()) {
      if (editSmoker.isSelected()) {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "attributes", "smoker",
            "true");
      } else {
        db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "attributes", "smoker",
            "false");
      }
      if (showBadInputMessageIfError(donorReceiver.getUpdateMessage())) {
        errorMessage = errorMessage + donorReceiver.getUpdateMessage();
        smokerText.setFill(Color.RED);
        editSmoker.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
      } else {
        smokerText.setFill(Color.BLACK);
        editSmoker.setStyle("-fx-focus-color: transparent;");
      }
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Retrieves the given String, and attempts to update the donorReceivers alcohol consumption
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void alcoholEntered(ActionEvent event) {
    if (!editAlcoholConsumption.getText().equals(
        String.valueOf(donorReceiver.getUserAttributeCollection().getAlcoholConsumption()))) {
      db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "attributes",
          "alcoholConsumption", editAlcoholConsumption.getText());
      if (showBadInputMessageIfError(donorReceiver.getUpdateMessage())) {
        errorMessage = errorMessage + donorReceiver.getUpdateMessage();
        alcoholConsumptionText.setFill(Color.RED);
        editAlcoholConsumption.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
      } else {
        alcoholConsumptionText.setFill(Color.BLACK);
        editAlcoholConsumption.setStyle("-fx-focus-color: transparent;");
      }
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Retrieves the given string, and attempts to update the donorReceivers blood pressure.
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void bloodPressureEntered(ActionEvent event) {
    if (editBloodPressure.getText() == null) {
      editBloodPressure.setText("0/0");
    }

    if (!editBloodPressure.getText()
        .equals(donorReceiver.getUserAttributeCollection().getBloodPressure())) {
      db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "attributes", "bloodPressure",
          editBloodPressure.getText());
      if (showBadInputMessageIfError(donorReceiver.getUpdateMessage())) {
        errorMessage = errorMessage + donorReceiver.getUpdateMessage();
        bloodPressureText.setFill(Color.RED);
        editBloodPressure.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
      } else {
        bloodPressureText.setFill(Color.BLACK);
        editBloodPressure.setStyle("-fx-focus-color: transparent;");
      }
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }

  /**
   * Retrieves the given string, and attempts to update the donorReceivers chronic diseases.
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  void chronicDiseasesEntered(ActionEvent event) {
    if (editChronicDiseases.getText() == null) {
      editChronicDiseases.setText("");
    }
    if (!editChronicDiseases.getText()
        .equals(donorReceiver.getUserAttributeCollection().getChronicDiseases())) {
      db.issueCommand(AccountManager.getCurrentUser(), "update", nhi, "attributes",
          "chronicDiseases", editChronicDiseases.getText());
      if (showBadInputMessageIfError(donorReceiver.getUpdateMessage())) {
        errorMessage = errorMessage + donorReceiver.getUpdateMessage();
        chronicDiseasesText.setFill(Color.RED);
        editChronicDiseases.setStyle(" -fx-border-color: red ; -fx-border-width: 2px ; ");
      } else {
        chronicDiseasesText.setFill(Color.BLACK);
        editChronicDiseases.setStyle("-fx-focus-color: transparent;");
      }
      donorReceiver.setUpdateMessage("waiting for new message");
    }
  }
  //=============================================================//
  //Medical Procedure History Tab
  //=============================================================//

  private void hideOrShowMedicalProcedures() {
    if (AccountManager.getCurrentUser() instanceof DonorReceiver) {
      // donor logged in
      medicalProceduresTab.setDisable(true);
    } else if (AccountManager.getCurrentUser() instanceof Clinician || AccountManager
        .getCurrentUser() instanceof Administrator) {
      // clinician/admin logged in
      medicalProceduresTab.setDisable(false);
      populateMedicalProcedureTables();
    }
  }

  /**
   * Overwrites the default comparator for the java fx TableView to order by chronic illnesses
   * first, and then by date. Currently cannot order by name.
   */
  public void orderMedicalProceduresTable() {

    pastProceduresTable.sortPolicyProperty()
        .set(new Callback<TableView<MedicalProcedure>, Boolean>() {
          @Override
          public Boolean call(TableView<MedicalProcedure> param) {
            Comparator<MedicalProcedure> comparator = new Comparator<MedicalProcedure>() {
              public int compare(MedicalProcedure i1, MedicalProcedure i2) {
                LocalDate date1 = i1.getDate();
                LocalDate date2 = i2.getDate();
                if (editPastProceduresDateColumn.getSortType() == TableColumn.SortType.ASCENDING) {
                  return date1.compareTo(date2) * -1;
                } else {
                  return date1.compareTo(date2);
                }
              }
            };
            FXCollections.sort(pastProceduresTable.getItems(), comparator);
            return true;
          }
        });
    pendingProcedureTable.sortPolicyProperty()
        .set(new Callback<TableView<MedicalProcedure>, Boolean>() {
          @Override
          public Boolean call(TableView<MedicalProcedure> param) {
            Comparator<MedicalProcedure> comparator = new Comparator<MedicalProcedure>() {
              public int compare(MedicalProcedure i1, MedicalProcedure i2) {
                LocalDate date1 = i1.getDate();
                LocalDate date2 = i2.getDate();
                if (editPendingProceduresDateColumn.getSortType()
                    == TableColumn.SortType.ASCENDING) {
                  if (date1 == null && date2 == null) {
                    return 0;
                  } else if (date1 == null) {
                    return -1;
                  } else if (date2 == null) {
                    return 1;
                  } else {
                    return date1.compareTo(date2);
                  }
                } else {
                  if (date1 == null && date2 == null) {
                    return 0;
                  } else if (date1 == null) {
                    return 1;
                  } else if (date2 == null) {
                    return -1;
                  } else {
                    return date1.compareTo(date2) * -1;
                  }
                }
              }
            };
            FXCollections.sort(pendingProcedureTable.getItems(), comparator);
            return true;
          }
        });

  }

  private void populateMedicalProcedureTables() {
    editPastProceduresSummaryColumn.setCellValueFactory(new PropertyValueFactory<>("summary"));
    editPastProceduresDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
    DateTimeFormatter myDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    editPastProceduresDateColumn.setCellFactory(column -> {
      return new TableCell<MedicalProcedure, LocalDate>() {
        @Override
        protected void updateItem(LocalDate item, boolean empty) {
          super.updateItem(item, empty);
          if (item == null || empty) {
            setText(null);
          } else {
            setText(myDateFormatter.format(item));
          }
        }
      };
    });
    editPendingProceduresSummaryColumn.setCellFactory(tc -> {
      TableCell<MedicalProcedure, String> cell = new TableCell<>();
      Text text = new Text();
      cell.setGraphic(text);
      cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
      text.wrappingWidthProperty().bind(editPendingProceduresSummaryColumn.widthProperty());
      text.textProperty().bind(cell.itemProperty());
      return cell;
    });
    editPendingProceduresSummaryColumn.setCellValueFactory(new PropertyValueFactory<>("summary"));
    editPendingProceduresDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
    editPendingProceduresDateColumn.setCellFactory(column -> {
      return new TableCell<MedicalProcedure, LocalDate>() {
        @Override
        protected void updateItem(LocalDate item, boolean empty) {
          super.updateItem(item, empty);
          if (item == null || empty) {
            setText(null);
          } else {
            setText(myDateFormatter.format(item));
          }
        }
      };
    });
    editPastProceduresSummaryColumn.setCellFactory(tc -> {
      TableCell<MedicalProcedure, String> cell = new TableCell<>();
      Text text = new Text();
      cell.setGraphic(text);
      cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
      text.wrappingWidthProperty().bind(editPastProceduresSummaryColumn.widthProperty());
      text.textProperty().bind(cell.itemProperty());
      return cell;
    });
    pastProceduresTable.setItems(pastProcedures);
    pendingProcedureTable.setItems(pendingProcedures);
    orderMedicalProceduresTable();
  }

  @FXML
  void addNewProcedureButtonPressed(ActionEvent event) throws IOException {
    AddEditProcedureController.setMedicalProcedure(null);
    AddEditProcedureController.setDonorReceiver(donorReceiver);
    loadAddEditProcedureWindow();
  }

  @FXML
  void pastProcedureDeleteButtonPressed(ActionEvent event) throws Exception {
    MedicalProcedure procedureToDelete = pastProceduresTable.getSelectionModel().getSelectedItem();
    if (procedureToDelete != null) {
      areYouSureDeleteProcedure(procedureToDelete);
    }
  }

  void areYouSureDeleteProcedure(MedicalProcedure procedure) throws Exception {
    Alert deleteConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
    deleteConfirmation.setTitle("Medical Procedure Deletion");
    deleteConfirmation.setHeaderText("Are you sure you want to delete this medical procedure?");
    deleteConfirmation.setContentText(
        "Are you sure you want to delete medical procedure: '" + procedure.getSummary() + "'?");
    Optional<ButtonType> result = deleteConfirmation.showAndWait();
    if (result.get() == ButtonType.OK) {
      donorReceiver.deleteMedicalProcedure(AccountManager.getCurrentUser(), procedure);
      pastProceduresTable.getItems().clear();
      pendingProcedureTable.getItems().clear();
      pastProceduresTable.getItems().addAll(donorReceiver.extractPastProcedures());
      pendingProcedureTable.getItems().addAll(donorReceiver.extractPendingProcedures());
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Information Dialog");
      alert.setHeaderText("Done");
      alert.setContentText(
          "Procedure successfully deleted. Don't forget to save the application to make the change permanent.");
      alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
      alert.showAndWait();
    }
  }

  @FXML
  void pastProcedureEditButtonPressed(ActionEvent event) throws IOException {
    MedicalProcedure procedureToEdit = pastProceduresTable.getSelectionModel().getSelectedItem();
    if (procedureToEdit != null) {
      AddEditProcedureController.setDonorReceiver(donorReceiver);
      AddEditProcedureController.setMedicalProcedure(procedureToEdit);
      loadAddEditProcedureWindow();
    }
  }

  @FXML
  void pendingProcedureDeleteButtonPressed(ActionEvent event) throws Exception {
    MedicalProcedure procedureToDelete = pendingProcedureTable.getSelectionModel()
        .getSelectedItem();
    if (procedureToDelete != null) {
      areYouSureDeleteProcedure(procedureToDelete);
    }
  }

  @FXML
  void pendingProcedureEditButtonPressed(ActionEvent event) throws IOException {
    MedicalProcedure procedureToEdit = pendingProcedureTable.getSelectionModel().getSelectedItem();
    if (procedureToEdit != null) {
      AddEditProcedureController.setDonorReceiver(donorReceiver);
      AddEditProcedureController.setMedicalProcedure(procedureToEdit);
      loadAddEditProcedureWindow();
    }
  }

  @FXML
  void pastProcedureClicked(MouseEvent event) {
    procedureDescriptionAffectedOrgansPopulate(false);
  }

  @FXML
  void pendingProcedureClicked(MouseEvent event) {
    procedureDescriptionAffectedOrgansPopulate(true);
  }

  private void procedureDescriptionAffectedOrgansPopulate(boolean pending) {
    MedicalProcedure procedureClicked;
    if (pending) {
      procedureClicked = pendingProcedureTable.getSelectionModel().getSelectedItem();
    } else {
      procedureClicked = pastProceduresTable.getSelectionModel().getSelectedItem();
    }
    String affectedOrgansText = "";
    try {
      ArrayList<String> affectedOrgans = procedureClicked.getAffectedOrgans();
      for (String affectedOrgan : affectedOrgans) {
        if (affectedOrgansText.length() == 0) {
          affectedOrgansText = affectedOrgansText + affectedOrgan;
        } else {
          affectedOrgansText = affectedOrgansText + ", " + affectedOrgan;
        }
      }
      if (pending) {
        if (affectedOrgansText.length() == 0) {
          pendingProcedureAffectedOrgans.setText("None");
        } else {
          pendingProcedureAffectedOrgans.setText(affectedOrgansText);
        }
        pendingProcedureDescription.setText(procedureClicked.getDescription());
      } else {
        if (affectedOrgansText.length() == 0) {
          pastProcedureAffectedOrgans.setText("None");
        } else {
          pastProcedureAffectedOrgans.setText(affectedOrgansText);
        }
        pastProcedureDescription.setText(procedureClicked.getDescription());
      }
    } catch (NullPointerException e) {
      System.out.print(" ");
    }
  }

  /**
   * Loads the add edit procedure scene in the donor's window.
   *
   * @throws IOException When the FXML cannot be retrieved.
   */
  private void loadAddEditProcedureWindow() throws IOException {
    // Create new pane.
    FXMLLoader loader = new FXMLLoader();
    AnchorPane addEditProcedure = loader.load(getClass().getResourceAsStream(PageNav.PROCEDURE));

    // Create new scene.
    Scene addEditProcedureScene = new Scene(addEditProcedure);

    // Retrieve current stage and set scene.
    Stage current = (Stage) Cancel.getScene().getWindow();
    current.setScene(addEditProcedureScene);

  }

  //=============================================================//

  @FXML
  public void attributeHelpButtonPressed(ActionEvent event) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Help");
    alert.setHeaderText("Attribute editing.");
    alert.setContentText(
        String.format("This pane gives you the ability to edit the donor's attributes. Simply" +
            " Simply edit whichever text field you wish and press 'enter' to update the database. If you have inputted invalid information "
            +
            "an alert dialog box will appear and give you advice on what the correct input should be."
            +
            "Pressing 'save' will make" +
            "any changes you have made permanent. Pressing done will take you back to the main menu."));
    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    alert.showAndWait();

  }

  @FXML
  public void contactHelpButtonPressed(ActionEvent event) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Help");
    alert.setHeaderText("Contact detail editing.");
    alert.setContentText(String
        .format("This pane gives you the ability to edit the donor's contact details. Simply" +
            " Simply edit whichever text field you wish and press 'enter' to update the database. If you have inputted invalid information "
            +
            "an alert dialog box will appear and give you advice on what the correct input should be. Note that the emergency address given here can"
            +
            "be the same as the address given in Attribute Details if the emergency contact and living address are the same."
            +
            "Pressing 'save' will make" +
            "any changes you have made permanent. Pressing done will take you back to the main menu."));
    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    alert.showAndWait();

  }

  @FXML
  public void organHelpButtonPressed(ActionEvent event) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Help");
    alert.setHeaderText("Donation editing.");
    alert.setContentText(String
        .format("This pane gives you the ability to edit the donor's organ registration. Simply" +
            " select whichever organ you wish to donate and select 'register'. You can also select 'unregister' if you wish."
            +
            " Pressing 'save' will make" +
            " any changes you have made permanent. Pressing done will take you back to the main menu."));
    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    alert.showAndWait();

  }

  public void profileHelpButtonPressed(ActionEvent event) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Help");
    alert.setHeaderText("Profile editing.");
    alert.setContentText(String
        .format("This pane gives you the ability to edit the donor's personal information. Simply" +
            "edit whichever text field you wish and press 'enter' to update the database. If you have inputted invalid information"
            +
            " an alert dialog box will appear and give you advice on what the correct input should be. Date of birth and"
            +
            "date of birth can also be specified using the calender icon. Note you still have to" +
            "press 'enter' in the date text field to trigger the update. Gender can be selected from the combo box. Pressing 'save' will make"
            +
            "any changes you have made permanent. Pressing done will take you back to the main menu."));
    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    alert.showAndWait();
  }

  /**
   * A failure dialog alert box given if the application fails to save.
   */
  public void showBadSaveError() {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error Dialog");
    alert.setHeaderText("Save failed");
    alert.setContentText("Something went wrong and and the save failed.");
    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    alert.showAndWait();
  }

  /* A success dialog alert box given if the application successfully saved.
   */
  public void showGoodSave() {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Information Dialog");
    alert.setHeaderText("Success");
    alert.setContentText(String.format("All changes have been successfully saved."));
    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    alert.showAndWait();
  }


  /**
   * Checks whether the account is that of a Receiver.
   */
  private void checkAccountReceiving() {
    CheckBox[] receiverOrgansCheckBox = {
        editReceiverLiver, editReceiverKidney, editReceiverLung, editReceiverHeart,
        editReceiverPancreas,
        editReceiverIntestine, editReceiverCornea, editReceiverMiddleEar, editReceiverBone,
        editReceiverBoneMarrow,
        editReceiverSkin, editReceiverConnectiveTissue
    };

    donorReceiver.setReceiver(false);

    for (CheckBox receiverOrgan : receiverOrgansCheckBox) {
      if (receiverOrgan.isSelected()) {
        donorReceiver.setReceiver(true);
        break;
      }
    }
  }


  /**
   * Modify what is displayed/editable based on who's logged in
   */
  private void handleView() {
    if (AccountManager
        .getCurrentUser() instanceof DonorReceiver) {    // Check that it is a donor logged in and that they're a receiver before hiding
      disableReceiver();
      if (!donorReceiver.isReceiver()) {
        hideReceiver();
      }
    } else {
      CheckBox[] receiverOrgansCheckBox = {
          editReceiverLiver, editReceiverKidney, editReceiverLung, editReceiverHeart,
          editReceiverPancreas,
          editReceiverIntestine, editReceiverCornea, editReceiverMiddleEar, editReceiverBone,
          editReceiverBoneMarrow,
          editReceiverSkin, editReceiverConnectiveTissue
      };
      for (CheckBox receiverOrgan : receiverOrgansCheckBox) {
        if (receiverOrgan.isSelected()) {
          receiverOrgan.setDisable(true);
        }
      }
    }
  }


  /**
   * Attempts to save all changes made to the application given the user accepts the confirmation
   * message. if the save is successful or fails, a notification dialog box will appear.
   */
  @FXML
  public void callSaveConfirmation(ActionEvent event) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Save Application");
    alert.setHeaderText(
        "You are about to save the application. In this case all changes, creations, and" +
            " deletions will become permanent.");
    alert.setContentText("Do you wish to proceed?");
    Optional<ButtonType> result = alert.showAndWait();
    if (result.get() == ButtonType.OK) {
      try {
        db.exportAccounts();
        showGoodSave();
      } catch (Exception e) {
        showBadSaveError();
      }
    }
  }


  /**
   * Converts the index of a gender combobox selection to a value which is either M, F, O, or U. If
   * the selection cannot be converted, an empty String object is returned instead.
   *
   * @param index The selected gender index as a String object.
   * @return Either M, F, O, or U if the index is valid. Otherwise, an empty String object is
   * returned.
   */
  private String convertGenderIndexToValue(String index) {
    if (Integer.parseInt(index) == 0) {
      return "M";
    } else if (Integer.parseInt(index) == 1) {
      return "F";
    } else if (Integer.parseInt(index) == 2) {
      return "O";
    } else if (Integer.parseInt(index) == 3) {
      return "U";
    } else {
      return "";
    }
  }


  /**
   * Parses the given string for the keyword "ERROR". If the keyword is present then an alert dialog
   * box is created with the given string as its content detailing the mistake they have made in
   * their data input.
   *
   * @param message A string thats either 'waiting for new message' or an error message.
   * @return boolean of error
   */
  private boolean showBadInputMessageIfError(String message) {

    if (message.substring(0, 5).equals("ERROR")) {
      invalidFlag = true;
      return true;
    }
    return false;
  }

  /**
   * Provides a general warning if invalid values have been entered anywhere during editing a
   * profile.
   */
  private void generalAlert() {
    Alert alert = new Alert(Alert.AlertType.ERROR, errorMessage, ButtonType.OK);
    alert.setHeaderText("Invalid Values");
    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
    alert.getDialogPane().setMinWidth(800);
    alert.showAndWait();
  }


  /**
   * Calls all listeners, to check for updates.
   *
   * @param event When an event occurs in the gui.
   */
  @FXML
  private void editAll(ActionEvent event) {
    errorMessage = "";
    givenNameEntered(event);
    lastNameEntered(event);
    preferredNameEntered(event);
    dobEntered(event);
    DODEntered(event);

    selectGender(event);
    selectBirthGender(event);
    titleEntered(event);
    heightEntered(event);
    weightEntered(event);
    bloodTypeSelected(event);
    livedInUKChecked(event);
    //----------------------------
    streetAddressEntered(event);
    cityEntered(event);
    regionEntered(event);
    postCodeEntered(event);
    mobileNumberEntered(event);
    homeNumberEntered(event);
    emailEntered(event);
    emStreetAddressEntered(event);
    emCityEntered(event);
    emRegionEntered(event);
    emPostCodeEntered(event);
    emMobileNumberEntered(event);
    emHomeNumberEntered(event);
    emEmailEntered(event);
    //------------------------
    selectedLiver(event);
    selectedKidneys(event);
    selectedHeart(event);
    selectedLungs(event);
    selectedIntestines(event);
    selectedCorneas(event);
    selectedMiddleEars(event);
    selectedSkin(event);
    selectedBone(event);
    selectedBoneMarrow(event);
    selectedConnectiveTissue(event);
    selectedPancreas(event);
    //------------------------
    selectedLiverReceiving(event);
    selectedKidneysReceiving(event);
    selectedHeartReceiving(event);
    selectedLungsReceiving(event);
    selectedIntestinesReceiving(event);
    selectedCorneasReceiving(event);
    selectedMiddleEarsReceiving(event);
    selectedSkinReceiving(event);
    selectedBoneReceiving(event);
    selectedBoneMarrowReceiving(event);
    selectedConnectiveTissueReceiving(event);
    selectedPancreasReceiving(event);
    //----------------------
    checkAccountReceiving();  // Register the donor as a reciever if they're donating any organ
    //----------------------
    smokerChecked(event);
    alcoholEntered(event);
    bloodPressureEntered(event);
    chronicDiseasesEntered(event);

    nhiEntered(event); //Needs to be here as other updates require old NHI
    //-----------------------
    saveCurrentMedication(event);
    savePreviousMedication(event);
    saveRemovedMedications(event);
    if (invalidFlag) {
      generalAlert();
    }
  }


  /**
   * Controls when the close button is pressed. Closes the window but does not save changes. Changes
   * are lost
   */
  @FXML
  private void cancelSelected() {
    handlePageSwitching();
    donorReceiver = null;
    undoableManager.getCommandStack().save();
  }

  /**
   * Controls when the done button is pressed. Updates using data entered in the edit pane for the
   * current session. Not a permanent save
   *
   * @param event The clicking of the button
   */
  @FXML
  private void doneSelected(ActionEvent event) {
    invalidFlag = false;
    editAll(event);
    if (invalidFlag) {

    } else {
      handlePageSwitching();
      try {
        DonorListController.triggerRefresh();
      } catch (NullPointerException exception) {
        // Catch case where DonorListController is null because the main
        // window is not currently on that screen.
      }
      try {
        TransplantWaitingListPaneController.triggerRefresh();
      } catch (NullPointerException exception) {
        // Catch case where TransplanrWaitingListPaneController is null because the main
        // window is not currently on that screen.
      }

    }


  }


  /**
   * Sets the isChild property.
   *
   * @param isChildValue The value to set isChild to.
   */
  public static void setIsChild(boolean isChildValue) {

    isChild = isChildValue;

  }


  /**
   * Handles how page switching occurs, which is dependent on whether or not the current window is a
   * child window.
   */
  private void handlePageSwitching() {
    ViewProfilePaneController.setAccount(donorReceiver);
    if (instanceIsChild) {
      try {
        loadProfileWindow();
      } catch (IOException exception) {
        System.out.println("Error loading the view pane");
      }
    } else {
      PageNav.loadNewPage(PageNav.VIEW, mainTabPane.getSelectionModel().getSelectedIndex(),
          "profileViewTabbedPane");
    }
  }

  /**
   * Change the pane to hide any details of being a receiver to the user
   */
  private void hideReceiver() {
    organTabPane.getTabs().remove(receiverTab);
  }

  /**
   * Disable the reciever organs tickbox if its not a clinician
   */
  private void disableReceiver() {
    // Receiving organs
    editReceiverLiver.setDisable(true);
    editReceiverKidney.setDisable(true);
    editReceiverLung.setDisable(true);
    editReceiverHeart.setDisable(true);
    editReceiverPancreas.setDisable(true);
    editReceiverIntestine.setDisable(true);
    editReceiverCornea.setDisable(true);
    editReceiverMiddleEar.setDisable(true);
    editReceiverBone.setDisable(true);
    editReceiverBoneMarrow.setDisable(true);
    editReceiverSkin.setDisable(true);
    editReceiverConnectiveTissue.setDisable(true);
  }

  /**
   * Returns to the profile screen within the same window.
   */
  private void loadProfileWindow() throws IOException {

    // Set child status.
    ViewProfilePaneController.setIsChild(true);
    ViewProfilePaneController.setAccount(donorReceiver);

    // Create new pane.
    FXMLLoader loader = new FXMLLoader();
    AnchorPane editPane = loader.load(getClass().getResourceAsStream(PageNav.VIEW));

    // Create new scene.
    Scene editScene = new Scene(editPane);
    TabPane tabPane = (TabPane) editPane.lookup("#profileViewTabbedPane");
    int currentTab = mainTabPane.getSelectionModel().getSelectedIndex();
    if (currentTab >= 3) {
      currentTab++; //History tab is not in edit profile
    }

    tabPane.getSelectionModel().clearAndSelect(currentTab);

    // Retrieve current stage and set scene.
    Stage current = (Stage) Cancel.getScene().getWindow();
    current.setScene(editScene);

    undoableManager.getCommandStack().save();

  }

  // Undo and Redo

  /**
   * Adds an undoable event when a checkbox is selected or unselected
   *
   * @param event Event of un/selection od checkbox
   */
  @FXML
  private void checkBoxEvent(ActionEvent event) {
    undoableManager.createDonationUndoable(event);
  }

  /**
   * Undos the last event on the command stack while editing
   */
  @FXML
  private void undoEvent() {
    undoCalled();
  }

  /**
   * Redos the last undid event on the command stack while editing
   */
  @FXML
  private void redoEvent() {
    redoCalled();
  }

  /**
   * Calls the undo event when using menu bar
   */
  public static void undoCalled() {
    CommandStack current = undoableManager.getCommandStack();
    if (!current.getUndo().empty() && current.getUndo().peek().getUndoRedoName()
        .equals("Choice Box")) {
      undoRedoChoiceBox = true;
    }
    if (!current.getUndo().empty() && current.getUndo().peek().getUndoRedoName()
        .equals("Date Picker")) {
      undoRedoDatePicker = true;
    }
    if (!current.getUndo().empty() && current.getUndo().peek().getUndoRedoName()
        .equals("Text Field")) {
      undoRedoTextField = true;
    }
    undoableManager.getCommandStack().undo();
  }

  /**
   * Calls the redo event when using menu bar
   */
  public static void redoCalled() {
    CommandStack current = undoableManager.getCommandStack();
    if (!current.getRedo().empty() && current.getRedo().peek().getUndoRedoName()
        .equals("Choice Box")) {
      undoRedoChoiceBox = true;
    }
    if (!current.getRedo().empty() && current.getRedo().peek().getUndoRedoName()
        .equals("Date Picker")) {
      undoRedoDatePicker = true;
    }
    if (!current.getRedo().empty() && current.getRedo().peek().getUndoRedoName()
        .equals("Text Field")) {
      undoRedoTextField = true;
    }
    undoableManager.getCommandStack().redo();
  }
}
