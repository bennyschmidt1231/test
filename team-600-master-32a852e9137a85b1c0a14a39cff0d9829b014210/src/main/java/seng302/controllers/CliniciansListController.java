package seng302.controllers;

import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import seng302.App;
import seng302.model.AccountManager;
import seng302.model.PageNav;
import seng302.model.comparitors.ClinicianNameComparator;
import seng302.model.person.Administrator;
import seng302.model.person.Clinician;
import seng302.model.person.DonorReceiver;
import seng302.model.person.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import static java.lang.Integer.parseInt;

public class CliniciansListController {
    @FXML public Button backButton;
    @FXML public TextField idFilterTextField;
    @FXML public TextField nameFilterTextField;
    @FXML public TableView<Clinician> cliniciansTable;
    @FXML public TableColumn<Clinician, Integer> idColumn;
    @FXML public TableColumn<Clinician, String> nameColumn;
    @FXML public TableColumn<Clinician, String> regionColumn;
    @FXML public Button newClinicianButton;
    @FXML public Button openProfileButton;
    @FXML public Button deleteButton;
    @FXML public Pagination pageControl;
    @FXML public Label numAccountsLabel;

    private static boolean isChild = false;
    private boolean instanceIsChild = false;

    //========================================================
    //For the table

    /**
     * Constant which determines the number of accounts on each table page.
     */
    private final static double ACCOUNTS_PER_PAGE = 30.0;

    /**
     * An account selected by the user on the table.
     */
    private static Clinician selectedAccount;

    /**
     * Retrieves the selected account.
     *
     * @return The selected DonorReceiver object.
     */
    public static Clinician getSelectedAccount() {
        return selectedAccount;
    }

    /**
     * The AccountManager object which handles all account operations.
     */
    private AccountManager accountManager = App.getDatabase();

    public ArrayList<Clinician> clinicians = accountManager.getClinicians();

    /**
     * The list of all administrators.
     */
    private ObservableList<Clinician> observableAccounts;

    /**
     * The list of all administrators to be displayed in TableView that match the
     * search criteria.
     */
    private FilteredList<Clinician> filteredAccounts;


    /**
     * A list of sorted administrators derived from filteredList.
     */
    private SortedList<Clinician> sortedAccounts;
    //========================================================

    @FXML
    public void backButtonPressed() {
        PageNav.loadNewPage(PageNav.ADMINMENU);
    }

    @FXML
    public void newClinicianButtonPressed() {
        User clinician = AccountManager.getCurrentUser();
        try {
            if (!App.childWindowToFront(clinician)) {
                FXMLLoader loader = new FXMLLoader();
                VBox clincianPane = loader.load(getClass().getResourceAsStream(PageNav.CREATECLINICIANCHILD));

                // Create new scene.
                Scene adminScene = new Scene(clincianPane);

                // Create new stage.
                Stage clinicianStage = new Stage();
                clinicianStage.setTitle("SapioCulture - Create Clinician");
                clinicianStage.setScene(adminScene);
                clinicianStage.show();

                clinicianStage.setX(App.getWindow().getX() + ((App.getWindow().getWidth() - clinicianStage.getWidth()) / 2.0));
                clinicianStage.setY(App.getWindow().getY() + ((App.getWindow().getHeight() - clinicianStage.getHeight()) / 2.0));

                App.addChildWindow(clinicianStage, clinician);
            }
        }
        catch(IOException e) {
            System.out.println("Error with opening create administrator window");
        }
    }



    /**
     * Opens a view profile pane in a new window.
     *
     * @param clinician The profile to be opened in a new window.
     * @throws IOException If the view profile FXML cannot be loaded.
     */
    public void loadProfileWindow (Clinician clinician) throws IOException {

        // Only load new window if childWindowToFront fails.
        if (!App.childWindowToFront(clinician)) {

            // Set the selected donorReceiver for the profile pane and confirm child.
            ClinicianProfileController.setClinician(clinician);
            ClinicianProfileController.setIsChild(true);

            // Create new pane.
            FXMLLoader loader = new FXMLLoader();
            AnchorPane profilePane = loader.load(getClass().getResourceAsStream(PageNav.VIEWEDITCLINICIAN));

            // Create new scene.
            Scene profileScene = new Scene(profilePane);

            // Create new stage.
            Stage profileStage = new Stage();
            profileStage.setTitle("Profile for " + clinician.getUserName());
            profileStage.setScene(profileScene);
            profileStage.show();
            profileStage.setX(App.getWindow().getX() + App.getWindow().getWidth());

            App.addChildWindow(profileStage, clinician);

        }

    }

    @FXML
    public void openProfileButtonPressed() {
        try {
            loadProfileWindow(selectedAccount);

        } catch (IOException exception) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error Opening Profile");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred while opening the selected profile.");
            alert.showAndWait();

        }
    }

     @FXML
    public void deleteButtonPressed() {
         if (selectedAccount == null) {
             // Warn user if no account was selected.
             noAccountSelectedAlert("Delete Clinician");
         } else {
             if (!selectedAccount.getUserName().equals("0")) {
                 makeSureOfDelete();
                 PageNav.loadNewPage(PageNav.CLINICIANSLIST);
             }

         }
     }

    private void makeSureOfDelete() {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Delete");
        dialog.setHeaderText("Deletion of Clincian");
        dialog.setContentText("Are you sure you want to delete this clinician?");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.NO, ButtonType.YES);
        final Button yesButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.YES);
        yesButton.addEventFilter(ActionEvent.ACTION, event -> {
            accountManager.getClinicians().remove(selectedAccount);
            selectedAccount.setActive(false);
            accountManager.clinicianTwilightZone.add(selectedAccount);
        });
        Optional<Boolean> result = dialog.showAndWait();
    }

    /**
     * Generates an alert asking the user to select an account with a title
     * specified by the parameter title.
     * @param title The title of the alert.
     */
    private void noAccountSelectedAlert(String title) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText("Please select an account.");
        alert.showAndWait();
    }

     //======================================
    //=======================================
    //For the table

    /**
     * Using criteria specified in the search field, an ArrayList of matching
     * accounts is retrieved from the account manager.
     */
    private void generateAccounts() {

        // Clear and repopulate observableAccounts.
        observableAccounts.removeAll(observableAccounts);
        observableAccounts.addAll(clinicians);
    }

    /**
     *
     * @param clinician The Clinician who has a full name.
     * @param string The string which may be contained in the full name.
     * @return True if the full name of donorReceiver contains string. False otherwise.
     */
    private boolean isAccountVisible(Clinician clinician, String string,  String otherSearch) {

        boolean hello = isAccountVisibleId(clinician, otherSearch);
        // Generate full name from given, other, and last names.
        String fullName = clinician.getFirstName() + " " + clinician.getMiddleName() + " " + clinician.getLastName();
        fullName = fullName.replaceAll("\\s+"," ").toLowerCase();

        // Return true only if the full name contains the specified string and
        // the checkbox corresponding to birth gender is checked.
        return  (hello && fullName.contains(string.toLowerCase()));

    }

    /**
     *
     * @param clinician The Clinician who has a full name.
     * @param string The string which may be contained in the full name.
     * @return True if the full name of donorReceiver contains string. False otherwise.
     */
    private boolean isAccountVisibleId(Clinician clinician, String string) {

        // Generate full name from given, other, and last names.
        String id = clinician.getUserName();
        id = id.replaceAll("\\s+"," ");

        // Return true only if the full name contains the specified string and
        // the checkbox corresponding to birth gender is checked.
        return id.contains(string);

    }

    /**
     * Updates the number of pages shown by pageControl and resets the current
     * index page to 0.
     */
    private void updatePageCount() {

        pageControl.setCurrentPageIndex(0);
        pageControl.setPageCount(Math.max(1, (int) Math.ceil(sortedAccounts.size() /
                ACCOUNTS_PER_PAGE)));

    }

    /**
     * Updates the page count shown by the pagination widget and resets the
     * current page index to 0.
     */
    private void updateTableView() {

        // Update label showing number of matches.
        numAccountsLabel.setText(getMatchesMessage());

        // Retrieve start and end index of current page.
        int startIndex = (int)(pageControl.getCurrentPageIndex() * ACCOUNTS_PER_PAGE);
        int endIndex = Math.min(startIndex + (int)ACCOUNTS_PER_PAGE, filteredAccounts.size());

        // Create new sublist.
        SortedList<Clinician> pageClinician = new SortedList<>(
                FXCollections.observableArrayList(sortedAccounts.subList(startIndex, endIndex)));
        pageClinician.comparatorProperty().bind(cliniciansTable.comparatorProperty());

        cliniciansTable.setItems(pageClinician);

    }

    /**
     * Generates a message which indicates the number of accounts matched in
     * a search. The number corresponds to the size of sortedAccounts.
     *
     * @return A message indicating the size of sortedAccounts.
     */
    private String getMatchesMessage() {

        int number = sortedAccounts.size();

        if (number == 1) {

            return number + " matching account";

        } else {

            return number + " matching accounts";

        }

    }

    @FXML
    public void initialize() {
        selectedAccount = null;
        initializeColumns();
        observableAccounts = FXCollections.observableArrayList();
        generateAccounts();

        // Create a filtered account to exclude non-matching results.
        filteredAccounts = new FilteredList<>(observableAccounts, account -> {

            if (PageNav.searchValue.equals("")) {

                return true;

            } else {

                return isAccountVisible(account, PageNav.searchValue, PageNav.searchValue);

            }

        });

        nameFilterTextField.textProperty().addListener((observable, oldValue, newValue) -> {

            filteredAccounts.setPredicate(account -> isAccountVisible(account, newValue, idFilterTextField.getText()));
            sortedAccounts = new SortedList<>(filteredAccounts);

            updatePageCount();
            updateTableView();

        });

        idFilterTextField.textProperty().addListener((observable, oldValue, newValue) -> {

            filteredAccounts.setPredicate(account -> isAccountVisible(account, nameFilterTextField.getText(), newValue));
            sortedAccounts = new SortedList<>(filteredAccounts);

            updatePageCount();
            updateTableView();

        });

        sortedAccounts = new SortedList<>(filteredAccounts);
        updatePageCount();
        updateTableView();

        cliniciansTable.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent click) {
                Clinician record = (Clinician) cliniciansTable.getSelectionModel().getSelectedItem();
                if (record != null) {
                    selectedAccount = record;
                }
            }

        });
    }

    /**
     * Helper method for 'initialize()' which sets the cell factory of each
     * column in the TableView object.
     */
    private void initializeColumns() {

        // nameColumn
        nameColumn.setCellValueFactory(record -> new ReadOnlyStringWrapper(
                (
                        record.getValue().getFirstName() + " " +
                                record.getValue().getMiddleName() + " " +
                                record.getValue().getLastName()
                ).replaceAll("\\s+", " ")
        ));

        // Region Column
        regionColumn.setCellValueFactory(record -> new ReadOnlyStringWrapper(
                (
                        record.getValue().getContactDetails().getAddress().getRegion())));


        idColumn.setCellValueFactory(record -> new ReadOnlyObjectWrapper<Integer>(parseInt(record.getValue().getUserName())));

    }
}
