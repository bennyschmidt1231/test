package seng302.controllers;


import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import seng302.App;
import seng302.model.*;
import seng302.model.person.DonorReceiver;
import seng302.model.person.LogEntry;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ViewProfilePaneController {

    // Basic information
    @FXML
    private Label firstNames;
    @FXML
    private Label lastName;
    @FXML
    private Label preferredName;
    @FXML
    private Label nationalHealthIndex;
    @FXML
    private Label dateCreated;
    @FXML
    private Label dateOfBirth;
    @FXML
    private Label dateOfDeath;
    @FXML
    private Label gender;
    @FXML
    private Label birthGender;
    @FXML
    private Label livedInUKFrance;
    @FXML
    private Label height;
    @FXML
    private Label weight;
    @FXML
    private Label title;
    @FXML
    private Label bloodType;
    @FXML
    private Label bmi;
    @FXML
    private Label smoker;
    @FXML
    private Label alcoholConsumption;
    @FXML
    private Label bloodPressure;
    @FXML
    private Label chronicDiseases;

    //Organs text
    @FXML
    private Text LiverText;
    @FXML
    private Text KidneyText;
    @FXML
    private Text LungText;
    @FXML
    private Text HeartText;
    @FXML
    private Text PancreasText;
    @FXML
    private Text IntestineText;
    @FXML
    private Text CorneaText;
    @FXML
    private Text MiddleEarText;
    @FXML
    private Text BoneText;
    @FXML
    private Text BoneMarrowText;
    @FXML
    private Text SkinText;
    @FXML
    private Text ConnectiveTissueText;

    //Organs checkbox
    @FXML
    private CheckBox LiverCheckBox;
    @FXML
    private CheckBox KidneyCheckBox;
    @FXML
    private CheckBox LungCheckBox;
    @FXML
    private CheckBox HeartCheckBox;
    @FXML
    private CheckBox PancreasCheckBox;
    @FXML
    private CheckBox IntestineCheckBox;
    @FXML
    private CheckBox CorneaCheckBox;
    @FXML
    private CheckBox MiddleEarCheckBox;
    @FXML
    private CheckBox BoneCheckBox;
    @FXML
    private CheckBox BoneMarrowCheckBox;
    @FXML
    private CheckBox SkinCheckBox;
    @FXML
    private CheckBox ConnectiveTissueCheckBox;

    //Receiving organs text
    @FXML
    private Text receiverLiverText;
    @FXML
    private Text receiverKidneyText;
    @FXML
    private Text receiverLungText;
    @FXML
    private Text receiverHeartText;
    @FXML
    private Text receiverPancreasText;
    @FXML
    private Text receiverIntestineText;
    @FXML
    private Text receiverCorneaText;
    @FXML
    private Text receiverMiddleEarText;
    @FXML
    private Text receiverBoneText;
    @FXML
    private Text receiverBoneMarrowText;
    @FXML
    private Text receiverSkinText;
    @FXML
    private Text receiverConnectiveTissueText;

    //Receiving organs checkbox
    @FXML
    private CheckBox receiverLiverCheckBox;
    @FXML
    private CheckBox receiverKidneyCheckBox;
    @FXML
    private CheckBox receiverLungCheckBox;
    @FXML
    private CheckBox receiverHeartCheckBox;
    @FXML
    private CheckBox receiverPancreasCheckBox;
    @FXML
    private CheckBox receiverIntestineCheckBox;
    @FXML
    private CheckBox receiverCorneaCheckBox;
    @FXML
    private CheckBox receiverMiddleEarCheckBox;
    @FXML
    private CheckBox receiverBoneCheckBox;
    @FXML
    private CheckBox receiverBoneMarrowCheckBox;
    @FXML
    private CheckBox receiverSkinCheckBox;
    @FXML
    private CheckBox receiverConnectiveTissueCheckBox;

    // Contact information
    @FXML
    private Label streetAddress;
    @FXML
    private Label city;
    @FXML
    private Label region;
    @FXML
    private Label postcode;
    @FXML
    private Label mobileNumber;
    @FXML
    private Label homeNumber;
    @FXML
    private Label email;
    @FXML
    private Label emergStreetAddress;
    @FXML
    private Label emergCity;
    @FXML
    private Label emergRegion;
    @FXML
    private Label emergPostcode;
    @FXML
    private Label emergMobileNumber;
    @FXML
    private Label emergHomeNumber;
    @FXML
    private Label emergEmail;
    @FXML
    private Label age;

    @FXML
    private Button Logout;
    @FXML
    private Button Edit;
    @FXML
    private ListView history;
    @FXML
    private Text DonorLoggedIn;
    @FXML
    private ListView currentMedications;
    @FXML
    private ListView previousMedications;
    @FXML
    private ListView historyOfMedications;

    //Tabs
    @FXML
    private Tab receiverTab;
    @FXML
    private Tab basicInformation;
    @FXML
    private Tab contactDetails;
    @FXML
    private Tab organsTab;
    @FXML
    private Tab historyTab;
    @FXML
    private Tab medicationsTab;
    @FXML
    private Tab medicalInformation;
    @FXML
    private Tab medicalHistoryTab;
    @FXML
    private Tab medicalProcedures;

    @FXML
    private TabPane organTabPane;
    @FXML
    private TabPane profileViewTabbedPane;

    //Medications

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

    //FXML properties of the medications tabs (current, previous and history) with the information table present.
    @FXML
    public ListView informationTableCM;
    @FXML
    public ListView informationTablePM;

    @FXML
    public Label errorInfoCM;
    @FXML
    public Label errorInfoPM;

    @FXML
    public Button getInformationCM;
    @FXML
    public Label informationContentsLabelCM;
    @FXML
    public Button getInformationPM;
    @FXML
    public Label informationContentsLabelPM;

    //Medical history diseases

    private static DonorReceiver staticSelectedAccount;
    private DonorReceiver selectedAccount;
    private static boolean isChild = false;
    private boolean instanceIsChild = false;

    private ActiveIngredientsService activeIngredientsService;
    private DrugInteractions drugInteractions;

    public static DonorReceiver getSelectedAccount() {
        return staticSelectedAccount;
    }

    public static void setAccount(DonorReceiver newDonorReceiver) {
        staticSelectedAccount = newDonorReceiver;
    }


    /**
     * An observable array list of Illness objects representing current diseases/illnesses the donor suffers from.
     */
    private ObservableList<Illness> currentDiagnoses;

    /**
     * An observable array list of Illness objects representing historic diseases/illnesses the donor suffered from.
     */
    private ObservableList<Illness> historicDiagnoses;


    // Medical History Procedures
    @FXML
    private TableView<MedicalProcedure> pastProceduresTable;
    @FXML
    private TableColumn<MedicalProcedure, LocalDate> pastProcedureDateColumn;
    @FXML
    private TableColumn<MedicalProcedure, String> pastProcedureSummaryColumn;
    @FXML
    private TableView<MedicalProcedure> pendingProcedureTable;
    @FXML
    private TableColumn<MedicalProcedure, LocalDate> pendingProcedureDateColumn;
    @FXML
    private TableColumn<MedicalProcedure, String> pendingProcedureSummaryColumn;
    @FXML
    private Label pastProcedureAffectedOrgans;
    @FXML
    private TextArea pastProcedureDescription;
    @FXML
    private Label pendingProcedureAffectedOrgans;
    @FXML
    private TextArea pendingProcedureDescription;

    final ObservableList<MedicalProcedure> pastProcedures = FXCollections.observableArrayList(staticSelectedAccount.extractPastProcedures());

    final ObservableList<MedicalProcedure> pendingProcedures = FXCollections.observableArrayList(staticSelectedAccount.extractPendingProcedures());


    /**
     * The over-arching call to set the values for each tab in this pane. Calls relevant methods to edit the labels in the pane
     *
     * @param donorReceiver The donorReceiver we are currently viewing
     */
    private void setLabels(DonorReceiver donorReceiver) {
        setProfile(getSelectedAccount());
        setContactDetails(getSelectedAccount());
        setMedicalHistory(getSelectedAccount());
        setListView(history);

        currentMedications.setItems(FXCollections.observableArrayList(donorReceiver.getMedications().getCurrentMedications()));
        previousMedications.setItems(FXCollections.observableArrayList(donorReceiver.getMedications().getPreviousMedications()));
        historyOfMedications.setItems(FXCollections.observableArrayList(donorReceiver.getMedications().getMedicationLog()));
        setOrgans(getSelectedAccount());
        DonorLoggedIn.setText(getSelectedAccount().fullName());
        highlightConflictingOrgans(donorReceiver);
    }


    /**
     * Sets all labels in the Basic information tab
     *
     * @param donorReceiver The donorReceiver of the donor we are viewing
     */
    public void setProfile(DonorReceiver donorReceiver) {
        if (donorReceiver.getMiddleName().trim().length() == 0) {
            firstNames.setText(donorReceiver.getFirstName());
        } else {
            firstNames.setText(donorReceiver.getFirstName() + " " + donorReceiver.getMiddleName());
        }
        lastName.setText(donorReceiver.getLastName());
        preferredName.setText(donorReceiver.getPreferredName());
        nationalHealthIndex.setText(String.format("%s", donorReceiver.getUserName()));
        dateCreated.setText(formatCreationDate(donorReceiver.getCreationTimeStamp()));
        dateOfBirth.setText(String.format("%s", donorReceiver.getDateOfBirth().toString()));

        if (donorReceiver.getDateOfDeath() == null) {
            dateOfDeath.setText("N.A.");
        } else {
            dateOfDeath.setText(String.format("%s", donorReceiver.getDateOfDeath()));
        }
        gender.setText(String.format("%s", donorReceiver.getGender()));
        birthGender.setText(String.format("%s", donorReceiver.getBirthGender()));
        livedInUKFrance.setText(String.format("%s", donorReceiver.getLivedInUKFlag()));
        age.setText(String.format("%s", calculateAge(donorReceiver.getDateOfBirth(), donorReceiver.getDateOfDeath(), LocalDate.now())));
        height.setText(String.format("%s", donorReceiver.getUserAttributeCollection().getHeight()));
        weight.setText(String.format("%s", donorReceiver.getUserAttributeCollection().getWeight()));
        bloodType.setText(String.format("%s", donorReceiver.getUserAttributeCollection().getBloodType()));
        bmi.setText(String.format("%s", setBMI(donorReceiver.getUserAttributeCollection().getHeight(), donorReceiver.getUserAttributeCollection().getWeight())));
        title.setText(String.format("%s", donorReceiver.getTitle()));
    }


    /**
     * Formats the creation date of an donorReceiver into a readable value
     *
     * @param time The LocalDatetime to be formatted
     * @return A string of format dd-MM-yyyy HH:mm:ss which represents the creation date of the donorReceiver
     */
    public static String formatCreationDate(LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String creationDate = time.format(formatter);
        return creationDate;
    }

    /**
     * Sets the checkboxes on the Organs tab to show whether or not the user is donating organs. Afterwards, disables all check boxes as they shouldn't be editable.
     *
     * @param donorReceiver The donorReceiver of the donor to be viewed
     */
    public void setOrgans(DonorReceiver donorReceiver) {
        DonorOrganInventory donorOrganInventory = donorReceiver.getDonorOrganInventory();
        if (donorOrganInventory.getLiver()) {
            LiverCheckBox.setSelected(true);
            LiverCheckBox.setDisable(true);
        }
        if (donorOrganInventory.getKidneys()) {
            KidneyCheckBox.setSelected(true);
            KidneyCheckBox.setDisable(true);
        }
        if (donorOrganInventory.getHeart()) {
            HeartCheckBox.setSelected(true);
            HeartCheckBox.setDisable(true);
        }
        if (donorOrganInventory.getLungs()) {
            LungCheckBox.setSelected(true);
            LungCheckBox.setDisable(true);
        }
        if (donorOrganInventory.getIntestine()) {
            IntestineCheckBox.setSelected(true);
            IntestineCheckBox.setDisable(true);
        }
        if (donorOrganInventory.getCorneas()) {
            CorneaCheckBox.setSelected(true);
            CorneaCheckBox.setDisable(true);
        }
        if (donorOrganInventory.getMiddleEars()) {
            MiddleEarCheckBox.setSelected(true);
            MiddleEarCheckBox.setDisable(true);
        }
        if (donorOrganInventory.getSkin()) {
            SkinCheckBox.setSelected(true);
            SkinCheckBox.setDisable(true);
        }
        if (donorOrganInventory.getBone()) {
            BoneCheckBox.setSelected(true);
            BoneCheckBox.setDisable(true);
        }
        if (donorOrganInventory.getBoneMarrow()) {
            BoneMarrowCheckBox.setSelected(true);
            BoneMarrowCheckBox.setDisable(true);
        }
        if (donorOrganInventory.getConnectiveTissue()) {
            ConnectiveTissueCheckBox.setSelected(true);
            ConnectiveTissueCheckBox.setDisable(true);
        }
        if (donorOrganInventory.getPancreas()) {
            PancreasCheckBox.setSelected(true);
            PancreasCheckBox.setDisable(true);
        }
        LiverCheckBox.setDisable(true);
        KidneyCheckBox.setDisable(true);
        HeartCheckBox.setDisable(true);
        LungCheckBox.setDisable(true);
        IntestineCheckBox.setDisable(true);
        CorneaCheckBox.setDisable(true);
        MiddleEarCheckBox.setDisable(true);
        SkinCheckBox.setDisable(true);
        BoneCheckBox.setDisable(true);
        BoneMarrowCheckBox.setDisable(true);
        ConnectiveTissueCheckBox.setDisable(true);
        PancreasCheckBox.setDisable(true);

        // Same as above but for receiving organs
        ReceiverOrganInventory requiredOrgans = donorReceiver.getRequiredOrgans();

        if (requiredOrgans != null) {  // In case the profile doesn't have receiver organs yet

            if (requiredOrgans.getLiver()) {
                receiverLiverCheckBox.setSelected(true);
                receiverLiverCheckBox.setDisable(true);
            }
            if (requiredOrgans.getKidneys()) {
                receiverKidneyCheckBox.setSelected(true);
                receiverKidneyCheckBox.setDisable(true);
            }
            if (requiredOrgans.getHeart()) {
                receiverHeartCheckBox.setSelected(true);
                receiverHeartCheckBox.setDisable(true);
            }
            if (requiredOrgans.getLungs()) {
                receiverLungCheckBox.setSelected(true);
                receiverLungCheckBox.setDisable(true);
            }
            if (requiredOrgans.getIntestine()) {
                receiverIntestineCheckBox.setSelected(true);
                receiverIntestineCheckBox.setDisable(true);
            }
            if (requiredOrgans.getCorneas()) {
                receiverCorneaCheckBox.setSelected(true);
                receiverCorneaCheckBox.setDisable(true);
            }
            if (requiredOrgans.getMiddleEars()) {
                receiverMiddleEarCheckBox.setSelected(true);
                receiverMiddleEarCheckBox.setDisable(true);
            }
            if (requiredOrgans.getSkin()) {
                receiverSkinCheckBox.setSelected(true);
                receiverSkinCheckBox.setDisable(true);
            }
            if (requiredOrgans.getBone()) {
                receiverBoneCheckBox.setSelected(true);
                receiverBoneCheckBox.setDisable(true);
            }
            if (requiredOrgans.getBoneMarrow()) {
                receiverBoneMarrowCheckBox.setSelected(true);
                receiverBoneMarrowCheckBox.setDisable(true);
            }
            if (requiredOrgans.getConnectiveTissue()) {
                receiverConnectiveTissueCheckBox.setSelected(true);
                receiverConnectiveTissueCheckBox.setDisable(true);
            }
            if (requiredOrgans.getPancreas()) {
                receiverPancreasCheckBox.setSelected(true);
                receiverPancreasCheckBox.setDisable(true);
            }
            receiverLiverCheckBox.setDisable(true);
            receiverKidneyCheckBox.setDisable(true);
            receiverHeartCheckBox.setDisable(true);
            receiverLungCheckBox.setDisable(true);
            receiverIntestineCheckBox.setDisable(true);
            receiverCorneaCheckBox.setDisable(true);
            receiverMiddleEarCheckBox.setDisable(true);
            receiverSkinCheckBox.setDisable(true);
            receiverBoneCheckBox.setDisable(true);
            receiverBoneMarrowCheckBox.setDisable(true);
            receiverConnectiveTissueCheckBox.setDisable(true);
            receiverPancreasCheckBox.setDisable(true);
        }
    }

    /**
     * Sets all the labels in the contact details tab.
     *
     * @param donorReceiver The donor donorReceiver being viewed
     */
    public void setContactDetails(DonorReceiver donorReceiver) {
        streetAddress.setText(String.format("%s", donorReceiver.getContactDetails().getAddress().getStreetAddressLn1()));
        city.setText(String.format("%s", donorReceiver.getContactDetails().getAddress().getCityName()));
        region.setText(String.format("%s", donorReceiver.getContactDetails().getAddress().getRegion()));
        postcode.setText(String.format("%s", donorReceiver.getContactDetails().getAddress().getPostCode()));
        mobileNumber.setText(String.format("%s", donorReceiver.getContactDetails().getMobileNum()));
        homeNumber.setText(String.format("%s", donorReceiver.getContactDetails().getHomeNum()));
        email.setText(String.format("%s", donorReceiver.getContactDetails().getEmail()));

        emergStreetAddress.setText(String.format("%s", donorReceiver.getEmergencyContactDetails().getAddress().getStreetAddressLn1()));
        emergCity.setText(String.format("%s", donorReceiver.getEmergencyContactDetails().getAddress().getCityName()));
        emergRegion.setText(String.format("%s", donorReceiver.getEmergencyContactDetails().getAddress().getRegion()));
        emergPostcode.setText(String.format("%s", donorReceiver.getEmergencyContactDetails().getAddress().getPostCode()));
        emergMobileNumber.setText(String.format("%s", donorReceiver.getEmergencyContactDetails().getMobileNum()));
        emergHomeNumber.setText(String.format("%s", donorReceiver.getEmergencyContactDetails().getHomeNum()));
        emergEmail.setText(String.format("%s", donorReceiver.getEmergencyContactDetails().getEmail()));
    }

    /**
     * Highlights the text labels for organs that are conflicting (Both donating and receiving)
     *
     * @param donorReceiver The donorReceiver to get information from
     */
    public void highlightConflictingOrgans(DonorReceiver donorReceiver) {
        if (donorReceiver.getDonorOrganInventory().getLiver() && donorReceiver.getRequiredOrgans().getLiver()) {
            LiverText.setFill(Color.RED);
            receiverLiverText.setFill(Color.RED);
        }
        if (donorReceiver.getDonorOrganInventory().getKidneys() && donorReceiver.getRequiredOrgans().getKidneys()) {
            KidneyText.setFill(Color.RED);
            receiverKidneyText.setFill(Color.RED);
        }
        if (donorReceiver.getDonorOrganInventory().getLungs() && donorReceiver.getRequiredOrgans().getLungs()) {
            LungText.setFill(Color.RED);
            receiverLungText.setFill(Color.RED);
        }
        if (donorReceiver.getDonorOrganInventory().getHeart() && donorReceiver.getRequiredOrgans().getHeart()) {
            HeartText.setFill(Color.RED);
            receiverHeartText.setFill(Color.RED);
        }
        if (donorReceiver.getDonorOrganInventory().getPancreas() && donorReceiver.getRequiredOrgans().getPancreas()) {
            PancreasText.setFill(Color.RED);
            receiverPancreasText.setFill(Color.RED);
        }
        if (donorReceiver.getDonorOrganInventory().getIntestine() && donorReceiver.getRequiredOrgans().getIntestine()) {
            IntestineText.setFill(Color.RED);
            receiverIntestineText.setFill(Color.RED);
        }
        if (donorReceiver.getDonorOrganInventory().getCorneas() && donorReceiver.getRequiredOrgans().getCorneas()) {
            CorneaText.setFill(Color.RED);
            receiverCorneaText.setFill(Color.RED);
        }
        if (donorReceiver.getDonorOrganInventory().getMiddleEars() && donorReceiver.getRequiredOrgans().getMiddleEars()) {
            MiddleEarText.setFill(Color.RED);
            receiverMiddleEarText.setFill(Color.RED);
        }
        if (donorReceiver.getDonorOrganInventory().getBone() && donorReceiver.getRequiredOrgans().getBone()) {
            BoneText.setFill(Color.RED);
            receiverBoneText.setFill(Color.RED);
        }
        if (donorReceiver.getDonorOrganInventory().getBoneMarrow() && donorReceiver.getRequiredOrgans().getBoneMarrow()) {
            BoneMarrowText.setFill(Color.RED);
            receiverBoneMarrowText.setFill(Color.RED);
        }
        if (donorReceiver.getDonorOrganInventory().getSkin() && donorReceiver.getRequiredOrgans().getSkin()) {
            SkinText.setFill(Color.RED);
            receiverSkinText.setFill(Color.RED);
        }
        if (donorReceiver.getDonorOrganInventory().getConnectiveTissue() && donorReceiver.getRequiredOrgans().getConnectiveTissue()) {
            ConnectiveTissueText.setFill(Color.RED);
            receiverConnectiveTissueText.setFill(Color.RED);
        }
    }

    /**
     * Collects the update log from the donor being viewed
     *
     * @return An arrayList containing all the logged updates to the donorReceiver
     */
    private ArrayList<LogEntry> generateUpdateLog() {
        return selectedAccount.getUpDateLog();
    }

    /**
     * Sets all the labels in the Medical History tab
     *
     * @param donorReceiver The donorReceiver being viewed
     */
    public void setMedicalHistory(DonorReceiver donorReceiver) {
        smoker.setText(String.format("%s", donorReceiver.getUserAttributeCollection().getSmoker()));
        alcoholConsumption.setText(String.format("%s", donorReceiver.getUserAttributeCollection().getAlcoholConsumption()));
        bloodPressure.setText(String.format("%s", donorReceiver.getUserAttributeCollection().getBloodPressure()));
        chronicDiseases.setText(String.format("%s", donorReceiver.getUserAttributeCollection().getChronicDiseases()));
    }

    /**
     * sets and prints the update log of the donorReceiver to the list view in the History pane
     *
     * @param toView list view sets
     */
    public void setListView(ListView toView) {
        ObservableList<LogEntry> observableLogs = FXCollections.observableArrayList();
        observableLogs.addAll(generateUpdateLog());
        toView.setCellFactory(param -> new ListCell<LogEntry>() {
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
        toView.setItems(observableLogs);
    }

    /**
     * Calculates the age of the person being viewed. If they are deceased, the age when they died is shown. If their isn't enough information, unknown is returned.
     *
     * @param born The date when the person was born
     * @param dead The date when the person died
     * @param now  The date currently
     * @return A string depicting either the age of the person, deceased with the age of death or unknown
     */
    public static String calculateAge(LocalDate born, LocalDate dead, LocalDate now) {
        if ((dead == null) && (born == null)) {
            return "Unknown";
        } else {
            if (dead == null) {
                return String.format("%s", Period.between(born, now).getYears());
            } else {
                if (dead.isBefore(now.plusDays(1))) {
                    if ((born != null)) {
                        return String.format("Deceased, age %s ", Period.between(born, dead).getYears());
                    } else {
                        return "Deceased";
                    }
                } else {
                    return String.format("%s", Period.between(born, now).getYears());
                }
            }
        }
    }

    /**
     * Calculates and sets the BMI of the donorReceiver
     *
     * @param height The height of the donor
     * @param weight The weight of the donor
     * @return A string consisting of the BMI of the donor, or unknown if there isn't enough information
     */
    public static String setBMI(double height, double weight) {
        if ((height == 0.0) || (weight == 0.0)) {
            return "Unknown";
        } else {
            double bmi = weight / (height * height);
            return String.format("%s", bmi);
        }

    }


    /**
     * Medication information pressed - different behaviour for different number of things selected.
     */
    public void selectMedicationButtonClicked() {
        ArrayList<String> selectedDrugs = new ArrayList<String>();
        try {
            for (int i = 0; i < currentMedications.getSelectionModel().getSelectedItems().size(); i++) {
                selectedDrugs.add(currentMedications.getSelectionModel().getSelectedItems().get(i).toString());
            }
            for (int i = 0; i < previousMedications.getSelectionModel().getSelectedItems().size(); i++) {
                selectedDrugs.add(previousMedications.getSelectionModel().getSelectedItems().get(i).toString());
            }
        } catch (NullPointerException e) {
            System.err.println(e);
        }


        if (selectedDrugs.size() == 0) {
            errorInfoCM.setText("No Drugs Set");
            informationTablePM.setItems(null);
            informationTableCM.setItems(null);
            informationContentsLabelCM.setText("");
            informationContentsLabelPM.setText("");

        } else if (selectedDrugs.size() == 1) {
            try {
                List<String> activeIngredients = activeIngredientsService.ActiveIngredients(selectedDrugs.get(0));
                if (activeIngredients.size() == 0 || Objects.equals(activeIngredients.get(0), "invalid drug name")) {
                    errorInfoCM.setText("Error in Getting Data");
                    errorInfoPM.setText("Error in Getting Data");
                    informationTablePM.setItems(null);
                    informationTableCM.setItems(null);
                } else {
                    errorInfoCM.setText("");
                    errorInfoPM.setText("");
                    informationContentsLabelCM.setText("Active Ingredients Shown");
                    informationContentsLabelPM.setText("Active Ingredients Shown");
                    informationTableCM.setItems(FXCollections.observableArrayList(activeIngredients));
                    informationTablePM.setItems(FXCollections.observableArrayList(activeIngredients));
                }
            } catch (IOException e) {
                System.out.println("API did not respond");
            }
        } else if (selectedDrugs.size() == 2) {
            try {

                // Uses Task to check cache/query remote API off main gui thread
                DrugInteractionTask task = new DrugInteractionTask(selectedDrugs.get(0),
                        selectedDrugs.get(1), selectedAccount, App.getDrugInteractionsCache());
                task.setOnSucceeded(event -> {
                    informationTableCM.setItems(task.getValue());
                    informationTablePM.setItems(task.getValue());
                    errorInfoCM.setText("");
                    errorInfoPM.setText("");
                    informationContentsLabelCM.setText("Drug Interactions Shown");
                    informationContentsLabelPM.setText("Drug Interactions Shown");

                });
                task.setOnFailed(event -> {
                    errorInfoCM.setText("Error in Getting Data");
                    errorInfoPM.setText("Error in Getting Data");
                    informationTablePM.setItems(null);
                    informationTableCM.setItems(null);
                });
                new Thread(task).start();

            } catch (Exception e) {
                System.out.println("API did not respond");
            }
        } else {
            informationTablePM.setItems(null);
            informationTableCM.setItems(null);
            informationContentsLabelCM.setText("");
            informationContentsLabelPM.setText("");
            errorInfoCM.setText("");
            errorInfoPM.setText("");
        }
    }


    /**
     * Changes the pane to reflect its status as a child window.
     */
    private void configureWindowAsChild() {

        instanceIsChild = isChild;
        isChild = false;

        // Disable and hide close button.
        Logout.setDisable(true);
        Logout.setVisible(false);

        // Reposition edit button to close button.
        Edit.setLayoutX(Logout.getLayoutX());


    }

    /**
     * Change the pane to hide any details of being a receiver to the user
     */
    private void hideReceiver() {
        organTabPane.getTabs().remove(receiverTab);
    }


    /**
     * Takes a boolean value should be true if this pane is supposed to appear
     * in a child window. Otherwise, the boolean value should be false.
     *
     * @param childStatus True if pane appears in a child window, false
     *                    otherwise.
     */
    public static void setIsChild(boolean childStatus) {

        isChild = childStatus;

    }


    /**
     * Switches to the edit profile screen within the given window.
     *
     * @throws IOException When the FXML cannot be retrieved.
     */
    private void loadEditWindow() throws IOException {

        // Set the selected donorReceiver for the profile pane and confirm child.
        EditPaneController.setAccount(selectedAccount);
        EditPaneController.setIsChild(true);

        // Create new pane.
        FXMLLoader loader = new FXMLLoader();
        AnchorPane editPane = loader.load(getClass().getResourceAsStream(PageNav.EDIT));
        // Create new scene.
        Scene editScene = new Scene(editPane);
        TabPane tabPane = (TabPane) editPane.lookup("#mainTabPane");
        int tabIndex = profileViewTabbedPane.getSelectionModel().getSelectedIndex();
        if (tabIndex > 3) {
            tabIndex -= 1; //Tab is past history tab
        } else if (tabIndex == 3) {
            tabIndex = 0;
        }
        tabPane.getSelectionModel().clearAndSelect(tabIndex);
        // Retrieve current stage and set scene.
        Stage current = (Stage) history.getScene().getWindow();
        current.setScene(editScene);

    }

    /**
     * Modify what is displayed/editable based on whos logged in
     */
    public void handleView() {
        if (AccountManager.getCurrentUser() instanceof DonorReceiver && !selectedAccount.isReceiver()) {    // Check that it is a donor logged in and that they're a receiver before hiding
            hideReceiver();
        }
    }

    /**
     * Controls when the close button is pressed
     *
     * @param event The clicking of the button
     */
    @FXML
    private void logoutSelected(ActionEvent event) {
        selectedAccount = null;
        App.getDatabase().setCurrentUser(null);
        PageNav.loadNewPage(PageNav.LOGIN);
    }

    /**
     * Controls when the edit button is pressed
     *
     * @param event The clicking of the button
     */
    @FXML
    private void editSelected(ActionEvent event) {
        int currentTab = profileViewTabbedPane.getSelectionModel().getSelectedIndex();
        EditPaneController.setAccount(selectedAccount);
        if (instanceIsChild) {
            try {
                loadEditWindow();
            } catch (IOException exception) {
                System.err.println("There is an error here");
            }
        } else {
            PageNav.loadNewPage(PageNav.EDIT, currentTab, "mainTabPane"); // Get the current tab and set it in the edit view
        }
    }


    //=============================================================//
    //Medical Procedure History Tab
    //=============================================================//


    /**
     * Sets sorting for current illness tableview that groups chronic illnesses and non-chronic illnesses and then applies
     * default sorting behaviour to these two groups. Tableview can be sorted by name or date, ascending or descending.
     *
     * @param table the table that is viewed
     */
    public static void setChronicIllnessFirstSort(TableView<Illness> table) {
        table.sortPolicyProperty().set(new Callback<TableView<Illness>, Boolean>() {
            @Override
            public Boolean call(TableView<Illness> param) {
                Comparator<Illness> comparator = new Comparator<Illness>() {
                    @Override
                    public int compare(Illness illness1, Illness illness2) {
                        Comparator<Illness> defaultComparator = table.getComparator();
                        if ((illness1.isChronic() == illness2.isChronic())) {
                            if (defaultComparator != null) {
                                return table.getComparator().compare(illness1, illness2);
                            } else {
                                return -illness1.getDate().compareTo(illness2.getDate());
                            }
                        } else {
                            if (illness1.isChronic()) {
                                return -1;
                            } else {
                                return 1;
                            }
                        }
                    }
                };
                FXCollections.sort(table.getItems(), comparator);
                return true;
            }
        });
    }


    /**
     * Overwrites the default comparator for the java fx TableView to order by chronic illnesses first, and then by date. Currently
     * cannot order by name.
     */
    public void orderMedicalProceduresTable() {

        pastProceduresTable.sortPolicyProperty().set(new Callback<TableView<MedicalProcedure>, Boolean>() {
            @Override
            public Boolean call(TableView<MedicalProcedure> param) {

                Comparator<MedicalProcedure> comparator = new Comparator<MedicalProcedure>() {
                    public int compare(MedicalProcedure i1, MedicalProcedure i2) {
                        LocalDate date1 = i1.getDate();
                        LocalDate date2 = i2.getDate();
                        if (pastProcedureDateColumn.getSortType() == TableColumn.SortType.ASCENDING) {
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
        pendingProcedureTable.sortPolicyProperty().set(new Callback<TableView<MedicalProcedure>, Boolean>() {
            @Override
            public Boolean call(TableView<MedicalProcedure> param) {

                Comparator<MedicalProcedure> comparator = new Comparator<MedicalProcedure>() {
                    public int compare(MedicalProcedure i1, MedicalProcedure i2) {
                        LocalDate date1 = i1.getDate();
                        LocalDate date2 = i2.getDate();
                        if (pendingProcedureDateColumn.getSortType() == TableColumn.SortType.ASCENDING) {
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

    /**
     * Populates the pending procedures, and past procedures table for the donorReceiver.
     *
     * @param donorReceiver The donorReceiver that is being viewed
     */
    public void populateProcedureTables(DonorReceiver donorReceiver) {
        pastProcedureSummaryColumn.setCellValueFactory(new PropertyValueFactory<>("summary"));
        pastProcedureDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        DateTimeFormatter myDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        pastProcedureDateColumn.setCellFactory(column -> {
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
        pendingProcedureSummaryColumn.setCellFactory(tc -> {
            TableCell<MedicalProcedure, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(pendingProcedureSummaryColumn.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell;
        });
        pendingProcedureSummaryColumn.setCellValueFactory(new PropertyValueFactory<>("summary"));
        pendingProcedureDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        pendingProcedureDateColumn.setCellFactory(column -> {
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
        pastProcedureSummaryColumn.setCellFactory(tc -> {
            TableCell<MedicalProcedure, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(pastProcedureSummaryColumn.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell;
        });
        pastProceduresTable.setItems(pastProcedures);
        pendingProcedureTable.setItems(pendingProcedures);
        orderMedicalProceduresTable();
    }

    @FXML
    void pastProcedureClicked() {
        procedureDescriptionAffectedOrgansPopulate(false);
    }

    @FXML
    void pendingProcedureClicked() {
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
        for (String organ : procedureClicked.getAffectedOrgans()) {
            if (affectedOrgansText.length() == 0) {
                affectedOrgansText = affectedOrgansText + organ;
            } else {
                affectedOrgansText = affectedOrgansText + ", " + organ;
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
    }

    /**
     * Converts the boolean values of the cells in the chronic TableColumn into strings, 'true' becomes 'chronic' (in red text),
     * and 'false' becomes 'no' (in black text). This method is based on code by two posts, one post from user jkaufmann on Stack Overflow
     * on 11/10/2011 at 2.44pm. See https://stackoverflow.com/questions/6998551/setting-font-color-of-javafx-tableview-cells. The
     * other post was from user Grimerie at 6/4/2016 at 6.05pm. See https://stackoverflow.com/questions/36436169/boolean-to-string-in-tableview-javafx.
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
        chronic.setCellFactory(new Callback<TableColumn<Illness, String>, TableCell<Illness, String>>() {
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
     * Called when the pane is initialized. The master call to set all labels in the pane and populate the table views.
     */
    @FXML
    public void initialize() {


        selectedAccount = staticSelectedAccount;
        setLabels(selectedAccount);
        populateProcedureTables(selectedAccount);
        nationalHealthIndex.setText(selectedAccount.getUserName());
        DonorLoggedIn.setText(selectedAccount.fullName());
        currentDiagnoses = FXCollections.observableArrayList(selectedAccount.getCurrentDiagnoses());
        historicDiagnoses = FXCollections.observableArrayList(selectedAccount.getHistoricDiagnoses());

        historicName.setCellValueFactory(new PropertyValueFactory<Illness, String>("name"));
        historicDate.setCellValueFactory(new PropertyValueFactory<Illness, String>("date"));
        CurrentName.setCellValueFactory(new PropertyValueFactory<Illness, String>("name"));
        currentDate.setCellValueFactory(new PropertyValueFactory<Illness, String>("date"));
        setChronicColoumn();
        currentTable.setItems(currentDiagnoses);
        historicTable.setItems(historicDiagnoses);
        setChronicIllnessFirstSort(currentTable);
        setChronicIllnessFirstSort(historicTable); // Used because it also gives the default sort of descending date


        activeIngredientsService = new ActiveIngredientsService();
        drugInteractions = new DrugInteractions();

        currentMedications.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        previousMedications.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        informationContentsLabelCM.setText("");
        informationContentsLabelPM.setText("");
        errorInfoPM.setText("");
        errorInfoCM.setText("");
        informationTablePM.setItems(null);
        informationTableCM.setItems(null);

        if (App.getDatabase().getCurrentUser() == null) { //DONT TOUCH THIS EVER IT WILL BREAK THE VIEW
            Logout.setVisible(false);
        } else {
            Logout.setVisible(true);
        }

        if (isChild) {
            configureWindowAsChild();
        } else {
            Logout.setVisible(true);
            Logout.setDisable(false);
        }

        handleView();
    }
}
