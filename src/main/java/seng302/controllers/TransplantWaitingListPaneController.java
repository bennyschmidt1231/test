package seng302.controllers;


import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import javafx.scene.layout.AnchorPane;
import javafx.scene.text.TextAlignment;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Pair;
import seng302.*;
import seng302.model.person.DonorReceiver;
import seng302.model.AccountManager;
import seng302.model.PageNav;
import seng302.model.ReceiverRecord;
import seng302.model.comparitors.RecDateComparator;
import seng302.model.comparitors.RecNameAlphabeticalComparator;
import seng302.model.comparitors.RecOrganComparator;
import seng302.model.comparitors.RegionComparatorRec;
import seng302.model.person.LogEntry;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.Collection;
import java.util.TreeSet;

public class TransplantWaitingListPaneController {


    @FXML
    private HeaderlessTable transplantWaitingList;

    @FXML
    private TableColumn<ReceiverRecord, String> nameColumn;

    @FXML
    private TableColumn<ReceiverRecord, String> organColumn;

    @FXML
    private TableColumn<ReceiverRecord, String> regionColumn;

    @FXML
    private TableColumn<ReceiverRecord, String> dateColumn;

    @FXML
    private Button donorButton;

    @FXML
    private Button viewButton;

    @FXML
    private Button editButton;

    @FXML
    private Button removeOrgan;

    @FXML
    private Button nameButton;

    @FXML
    private Button regionButton;

    @FXML
    private Button organButton;

    @FXML
    private Button dateButton;

    @FXML
    private ComboBox organFilterComboBox;

    @FXML
    private ComboBox regionFilterComboBox;

    @FXML
    private Label matchingAccounts;

    @FXML
    private TextField searchForField;

    @FXML
    private Pagination pageControl;

    private ObservableList<String> regions = FXCollections.observableArrayList();

    private ObservableList<String> organs = FXCollections.observableArrayList();

    private ObservableList<ReceiverRecord> records = FXCollections.observableArrayList();

    /**
     * An account selected by the user on the table.
     */
    private static DonorReceiver selectedAccount;

    private static ReceiverRecord selectedRecord;

    /**
     * The AccountManager object which handles all account operations.
     */
    private AccountManager accountManager = App.getDatabase();

    /**
     * Constant which determines the number of accounts on each table page.
     */
    private final static double ACCOUNTS_PER_PAGE = 16.0;

    /**
     * The list of all accounts.
     */
    private ObservableList<DonorReceiver> observableAccounts;

    /**
     * The list of all accounts to be displayed in TableView that match the
     * search criteria.
     */
    private FilteredList<ReceiverRecord> filteredRecords;


    /**
     * A list of sorted accounts derived from filteredRecords.
     */
    private SortedList<ReceiverRecord> sortedRecords;

    /**
     * Enumeration for referencing column sort order.
     */
    private enum Order {DEFAULT, NAME_ASC, NAME_DESC, ORGAN_ASC, ORGAN_DESC,
        REGION_ASC, REGION_DESC, DATE_ASC, DATE_DESC};

    /**
     * The currently selected sort order.
     */
    private Order sortOrder = Order.DEFAULT;

    /**
     * A constant for the ascending button text.
     */
    private final String ASC_TEXT = " (Ascending)";

    /**
     * A constant for the descending button text.
     */
    private final String DESC_TEXT = " (Descending)";

    /**
     * Retrieves the selected account.
     *
     * @return The selected DonorReceiver object.
     */
    public static DonorReceiver getSelectedAccount() {
        return selectedAccount;
    }

    /**
     * Using criteria specified in the search field, a list of matching
     * accounts is retrieved from the account manager.
     */
    private void generateAccounts() {

        observableAccounts.removeAll(observableAccounts);
        for (DonorReceiver acc: accountManager.getMasterList().values()) {
            if (acc.getReceiver()){
                observableAccounts.add(acc);
            }
        }

    }
    /**
     * Updates the text of all order buttons on the list view pane.
     */
    private void updateOrderButtons() {

        // Reset button names.
        nameButton.setText("Name");
        organButton.setText("Organ");
        regionButton.setText("Region");
        dateButton.setText("Date Added");

        if (sortOrder == Order.NAME_ASC) {

            nameButton.setText(nameButton.getText() + ASC_TEXT);
            sortedRecords.setComparator(new RecNameAlphabeticalComparator());

        } else if (sortOrder == Order.NAME_DESC) {

            nameButton.setText(nameButton.getText() + DESC_TEXT);
            sortedRecords.setComparator(new RecNameAlphabeticalComparator().reversed());

        } else if (sortOrder == Order.ORGAN_ASC) {

            organButton.setText(organButton.getText() + ASC_TEXT);
            sortedRecords.setComparator(new RecOrganComparator());

        } else if (sortOrder == Order.ORGAN_DESC) {

            organButton.setText(organButton.getText() + DESC_TEXT);
            sortedRecords.setComparator(new RecOrganComparator().reversed());

        } else if (sortOrder == Order.DATE_ASC) {

            dateButton.setText(dateButton.getText() + ASC_TEXT);
            sortedRecords.setComparator(new RecDateComparator());

        } else if (sortOrder == Order.DATE_DESC) {

            dateButton.setText(dateButton.getText() + DESC_TEXT);
            sortedRecords.setComparator(new RecDateComparator().reversed());

        } else if (sortOrder == Order.REGION_ASC) {

            regionButton.setText(regionButton.getText() + ASC_TEXT);
            sortedRecords.setComparator(new RegionComparatorRec());

        } else if (sortOrder == Order.REGION_DESC) {

            regionButton.setText(regionButton.getText() + DESC_TEXT);
            sortedRecords.setComparator(new RegionComparatorRec().reversed());

        } else if (sortOrder == Order.DEFAULT) {

            // Revert to default order based on name.
            sortedRecords.setComparator(new RecNameAlphabeticalComparator());

        }

        updatePageCount();
        updateTableView();

    }


    /**
     * eventHandler for the nameOrder button.
     *
     * @param event An action which occurred on the nameOrder button.
     */
    @FXML
    private void updateNameOrder(ActionEvent event) {

        if (sortOrder == Order.NAME_ASC) {

            sortOrder = Order.NAME_DESC;

        } else if (sortOrder == Order.NAME_DESC) {

            sortOrder = Order.DEFAULT;

        } else {

            sortOrder = Order.NAME_ASC;

        }

        updateOrderButtons();

    }


    /**
     * Event handler for the ageOrder button.
     *
     * @param event An action which occurred on the ageOrder button.
     */
    @FXML
    private void updateOrganOrder(ActionEvent event) {

        if (sortOrder == Order.ORGAN_ASC) {

            sortOrder = Order.ORGAN_DESC;

        } else if (sortOrder == Order.ORGAN_DESC) {

            sortOrder = Order.DEFAULT;

        } else {

            sortOrder = Order.ORGAN_ASC;

        }

        updateOrderButtons();

    }


    /**
     * Event handler for the genderOrder button.
     *
     * @param event An action which occurred on the genderOrder button.
     */
    @FXML
    private void updateDateOrder(ActionEvent event) {

        if (sortOrder == Order.DATE_ASC) {

            sortOrder = Order.DATE_DESC;

        } else if (sortOrder == Order.DATE_DESC) {

            sortOrder = Order.DEFAULT;

        } else {

            sortOrder = Order.DATE_ASC;

        }

        updateOrderButtons();

    }


    /**
     * Event handler for the regionOrder button.
     *
     * @param event An action which occurred on the regionOrder button.
     */
    @FXML
    private void updateRegionOrder(ActionEvent event) {

        if (sortOrder == Order.REGION_ASC) {

            sortOrder = Order.REGION_DESC;

        } else if (sortOrder == Order.REGION_DESC) {

            sortOrder = Order.DEFAULT;

        } else {

            sortOrder = Order.REGION_ASC;

        }

        updateOrderButtons();

    }




    //===================================================
    //Navigating buttons

    @FXML
    private void donorSelected() {
        PageNav.loadNewPage(PageNav.LISTVIEW);
    }

    /**
     * Switches to the view/edit screen.
     */
    @FXML
    private void viewSelected() {
        if (selectedAccount == null) {
            // Warn user if no account was selected.
            noAccountSelectedAlert("View DonorReceiver");
        } else {
            ViewProfilePaneController.setAccount(selectedAccount);
            PageNav.loadNewPage(PageNav.VIEW);
        }
    }

    /**
     * Switches to the edit screen.
     */
    @FXML
    private void editSelected() {
        if (selectedAccount == null) {
            // Warn user if no account was selected.
            noAccountSelectedAlert("Edit DonorReceiver");
        } else {
            EditPaneController.setAccount(selectedAccount);
            PageNav.loadNewPage(PageNav.EDIT);
        }
    }


    /**
     * Resets the combobox filters for organ and region
     */
    @FXML
    private void resetFilters() {
        regionFilterComboBox.getSelectionModel().select("Any Region");
        organFilterComboBox.getSelectionModel().select("Any Organ");
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

    //=================================================


    /**
     * Joins all of the names the donor has to create their full name
     * @param acc receiver account
     * @return The full name of the donor profile
     */
    public String fullName(DonorReceiver acc) {
        if (acc.getMiddleName().equals("")) {
            if (acc.getLastName().equals("")) {
                return acc.getFirstName();
            } else {
                return acc.getFirstName() + " " + acc.getLastName();
            }
        } else {
            return acc.getFirstName() + " " + acc.getMiddleName() + " " + acc.getLastName();
        }
    }

    /**
     * Updates the number of pages shown by pageControl and resets the current
     * index page to 0.
     */
    private void updatePageCount() {

        pageControl.setCurrentPageIndex(0);
        pageControl.setPageCount(Math.max(1, (int) Math.ceil(sortedRecords.size() /
                ACCOUNTS_PER_PAGE)));

    }

    /**
     * Generates a message which indicates the number of accounts matched in
     * a search. The number corresponds to the size of sortedRecords.
     *
     * @return A message indicating the size of sortedRecords.
     */
    private String getMatchesMessage() {
        int number = sortedRecords.size();
        if (number == 1) {
            return "\n" +
                     number + " matching records";
        } else {
            int startNumber = pageControl.getCurrentPageIndex() * 16 + 1;
            int endNumber = (pageControl.getCurrentPageIndex() + 1) * 16;
            endNumber = (endNumber < number) ? endNumber : number;  // Set the endNumber to the lower value
            return "Records " + startNumber + "-" + endNumber + "\n" +
                    number + " matching records";
        }
    }

    /**
     * Updates the page count shown by the pagination widget and resets the
     * current page index to 0.
     */
    private void updateTableView() {

        // Update label showing number of matches.
        matchingAccounts.setTextAlignment(TextAlignment.CENTER);
        matchingAccounts.setText(getMatchesMessage());

        // Retrieve start and end index of current page.
        int startIndex = (int)(pageControl.getCurrentPageIndex() * ACCOUNTS_PER_PAGE);
        int endIndex = Math.min(startIndex + (int)ACCOUNTS_PER_PAGE, filteredRecords.size());

        // Create new sublist.
        SortedList<ReceiverRecord> pageAccounts = new SortedList<>(
                FXCollections.observableArrayList(sortedRecords.subList(startIndex, endIndex)));
        pageAccounts.comparatorProperty().bind(transplantWaitingList.comparatorProperty());

        transplantWaitingList.setItems(pageAccounts);

    }

    /**
     * Creates a dialog box whenever the remove button is selected (Requires an item in the transplant waiting list to be selected).
     * This dialog contains a combo box to choose the reason for removal, and a date picker to choose the date of death if option 3 is selected.
     */
    @FXML
    public void removeFromTableChoiceDialog() {
        if (selectedAccount == null) {
            // Warn user if no account was selected.
            noAccountSelectedAlert("View Account");
        } else {
            Dialog<Pair<String, LocalDateTime>> dialog = new Dialog<>();
            dialog.setTitle("Removal from Transplant List");
            dialog.setHeaderText("Removal from Transplant List");
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            //Creating a setting labels buttons for dialog
            ComboBox<String> choose = new ComboBox<>();
            choose.getItems().addAll(
                    "1. Error",
                    "2. Cured",
                    "3. Death"
            );
            choose.getSelectionModel().selectFirst();
            DatePicker date = new DatePicker();  //For the date of death option
            date.setValue(LocalDate.now());
            date.setVisible(false);
            Label dateLabel = new Label();  //Date of death label
            dateLabel.setText("Date of Death: ");
            dateLabel.setVisible(false);
            Label errorLabel = new Label(); //Error label for date of death
            errorLabel.setText("Please enter a date");
            errorLabel.setTextFill(Color.RED);
            errorLabel.setVisible(false);

            CheckBox yes = new CheckBox();
            yes.setText("Yes");
            yes.setVisible(false);
            CheckBox no = new CheckBox();
            no.setText("No");
            no.setVisible(false);
            Label curedQuestion = new Label();
            curedQuestion.setText("Would you like to check the illnesses of the receiver?");
            curedQuestion.setVisible(false);
            Label curedQuestionError = new Label();
            curedQuestionError.setText("Please choose yes or no");
            curedQuestionError.setTextFill(Color.RED);
            curedQuestionError.setVisible(false);

            //Adding buttons/Labels to the grid
            grid.add(new Label("Reason for removal:"), 0, 0);
            grid.add(choose, 1, 0);
            grid.add(dateLabel, 0, 1);
            grid.add(date, 1, 1);
            grid.add(errorLabel, 2, 1);

            grid.add(curedQuestion, 0, 1);
            grid.add(yes, 1, 1);
            grid.add(no, 2, 1);
            grid.add(curedQuestionError, 3, 1);

            //Adding a listener to check if the combo box is on option 3, death
            choose.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    if (newValue.equals("3. Death")) {
                        date.setVisible(true);
                        dateLabel.setVisible(true);
                        curedQuestion.setVisible(false);
                        yes.setVisible(false);
                        no.setVisible(false);
                    } else if (newValue.equals("2. Cured")){
                        curedQuestion.setVisible(true);
                        yes.setVisible(true);
                        no.setVisible(true);
                        date.setVisible(false);
                        dateLabel.setVisible(false);
                    } else {
                        date.setVisible(false);
                        dateLabel.setVisible(false);
                        errorLabel.setVisible(false);
                        curedQuestion.setVisible(false);
                        yes.setVisible(false);
                        no.setVisible(false);
                        curedQuestionError.setVisible(false);
                    }
                }
            });

            dialog.getDialogPane().setContent(grid);

            //Catches a null date of death value if the reason is death, otherwise closes
            final Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
            okButton.addEventFilter(ActionEvent.ACTION, event -> {
                if ((date.getValue() == null) && (choose.getSelectionModel().getSelectedItem().equals("3. Death"))) {
                    errorLabel.setVisible(true);
                    event.consume();
                } else if ((!yes.isSelected()) && (!no.isSelected()) && (choose.getSelectionModel().getSelectedItem().equals("2. Cured"))){
                    curedQuestionError.setVisible(true);
                    event.consume();
                } else {
                    if (choose.getSelectionModel().getSelectedItem().equals("3. Death")) {
                        LocalDate dOD = date.getValue();
                        removeOrganDeceased(dOD);
                    } else if (choose.getSelectionModel().getSelectedItem().equals("2. Cured")) {
                        if (yes.isSelected()) {
                            try {
                                removeOrganCured("Yes");
                            } catch (IOException exception) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Error Opening Profile");
                                alert.setHeaderText(null);
                                alert.setContentText("An error occurred while opening the selected profile.");
                                alert.showAndWait();
                            }
                        } else {
                            try {
                                removeOrganCured("No");
                            } catch (IOException exception) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Error Opening Profile");
                                alert.setHeaderText(null);
                                alert.setContentText("An error occurred while opening the selected profile.");
                                alert.showAndWait();
                            }
                        }
                    } else {
                        removeOrganMistake();
                    }
                }
            });
            Optional<Pair<String, LocalDateTime>> result = dialog.showAndWait();
        }
    }

    /**
     * Removes all organs from the receiver, sets their date of death and logs all changes
     * @param dateOfDeath the accounts date of death
     */
    public void removeOrganDeceased(LocalDate dateOfDeath) {
        //Remove all organs from records
        //Set all organs to false in receiver organs -Logged
        DonorReceiver account = accountManager.getAccountsByNHI(selectedRecord.getNhi()).get(0);
        String nhi = selectedRecord.getNhi();
        String organ = selectedRecord.getOrgan();

        LogEntry a = new LogEntry(account, accountManager.getCurrentUser(), organ, "true", "false, reason for removal: Death,");
//        account.getUpDateLog().add(a);
        accountManager.getSystemLog().add(a);
        accountManager.issueCommand(accountManager.getCurrentUser(), "update", nhi, "organs", "receiver", "all", "false, reason for removal: Death,");
        PageNav.loadNewPage(PageNav.TRANSPLANTLIST);
        //Set receiver flag false  -Logged
        account.setReceiver(false);
        //Set date of death -Logged
        String dateTime = formatDateToString(dateOfDeath);
        accountManager.issueCommand(accountManager.getCurrentUser(), "update", nhi, "profile", "dateOfDeath", dateTime); //It is not noted on the profile that they are now deceased
    }

    /**
     * Removes the record from the waiting list, creates a log and sets the organ to false
     * @param option Whether or not the clinician wants to view the accounts current diseases
     * @throws IOException An error
     */
    public void removeOrganCured(String option) throws IOException{
        //Remove organ from record
        records.remove(selectedRecord);
        //Set organ to false in receiver organs
        DonorReceiver account = accountManager.getAccountsByNHI(selectedRecord.getNhi()).get(0);
        String nhi = selectedRecord.getNhi();
        String organ = selectedRecord.getOrgan();

        LogEntry a = new LogEntry(account, accountManager.getCurrentUser(), organ, "true", "false, reason for removal: Cured,");
//        account.getUpDateLog().add(a);
        accountManager.getSystemLog().add(a);

        organ = organ.toLowerCase();
        if (organ.equals("middle ear")) {
            organ = "middleEars";
        }
        if (organ.equals("bone marrow")) {
            organ = "boneMarrow";
        }
        if (organ.equals("connective tissue")) {
            organ = "connectiveTissue";
        }
        accountManager.issueCommand( accountManager.getCurrentUser(), "update", nhi, "organs", "receiver", organ, "false, reason for removal: Cured,");
        if (option.equals("Yes")) {
            loadProfileWindowEdit(accountManager.getAccountsByNHI(selectedRecord.getNhi()).get(0));
        }
        PageNav.loadNewPage(PageNav.TRANSPLANTLIST);
        updateAccountReceiving(account);    //Check if all organs are false, if so, set receiver to false
    }

    /**
     * Removes the record, changes the relevant organ to false in the account, and creates a relevant log
     */
    public void removeOrganMistake() {
        //Remove organ from record -Where the log should be
        records.remove(selectedRecord);
        System.out.println(selectedRecord.getNhi());
        DonorReceiver account = accountManager.getAccountsByNHI(selectedRecord.getNhi()).get(0);
        //Set organ to false
        String nhi = selectedRecord.getNhi();
        String organ = selectedRecord.getOrgan();
//
        LogEntry a = new LogEntry(account, accountManager.getCurrentUser(), organ, "true", "false, reason for removal: Mistake,");
//        account.getUpDateLog().add(a);
        accountManager.getSystemLog().add(a);
        organ = organ.toLowerCase();
        if (organ.equals("middle ear")) {
            organ = "middleEars";
        }
        if (organ.equals("bone marrow")) {
            organ = "boneMarrow";
        }
        if (organ.equals("connective tissue")) {
            organ = "connectiveTissue";
        }
        accountManager.issueCommand(accountManager.getCurrentUser(), "update", nhi, "organs", "receiver", organ, "false, reason for removal: Mistake,");

        updateAccountReceiving(account);    //Check if all organs are false, if so, set receiver to false

        PageNav.loadNewPage(PageNav.TRANSPLANTLIST);
    }

    /**
     * Refresh the table when any account receives a change
     * @throws NullPointerException an exception
     */
    public static void triggerRefresh() throws NullPointerException {

        TableView transplantWaitingList = (TableView) App.getWindow().getScene().lookup("#transplantWaitingList");

        if (transplantWaitingList == null) {

            // Handle case were accountsList is not initialised.
            throw new NullPointerException("TableView object 'transplantWaitingList' is not initialised in the main window.");

        } else {

            // Trigger instantiated update.
            transplantWaitingList.refresh();

        }

    }

    //=================================================
    //Initalize functions

    /**
     * Sets the row factory for accountsList. This creates a tooltip for each
     * populated row showing an overview of the account. This code is based on
     * a solution (https://stackoverflow.com/a/26221242) posted by James_D in
     * response to a question on Stack Overflow.
     */
    private void setAccountsListRowFactory() {

        transplantWaitingList.setRowFactory(tableView -> new TableRow<ReceiverRecord>() {
            @Override
            public void updateItem(ReceiverRecord record, boolean empty) {

                super.updateItem(record, empty);

                if (record == null) {

                    // If no account is associated with row, remove tool tip.
                    setTooltip(null);

                } else {

                    DonorReceiver account = accountManager.getAccountsByNHI(record.getNhi()).get(0); // A dummy account variable for the account donating the specified organ

                    // Generate an account-specific tool tip.
                    Tooltip overview = new Tooltip();
                    overview.setText(account.generateOverview());
                    setTooltip(overview);

                    // Highlight the entry if they donator is also donating this organ that they are receiving
                    String organ = record.getOrgan();
                    organ = organ.toLowerCase();
                    if (organ.equals("middle ear")) {
                        organ = "middleEars";
                    }
                    if (organ.equals("bone marrow")) {
                        organ = "boneMarrow";
                    }
                    if (organ.equals("connective tissue")) {
                        organ = "connectiveTissue";
                    }
                    if (organ.equals("intestines")) {
                        organ = "intestine";
                    }
                    Boolean isConflicting = account.getDonorOrganInventory().isDonating(organ);
                    if (isConflicting) {
                        setStyle("-fx-background-color:f93333;-fx-opacity:0.5;");
                    }
                    else {
                        setStyle(""); // Reset the style to prevent random rows from being highlighted
                    }

                }

            }

        });

    }

    /**
     * Opens a view profile pane in a new window.
     *
     * @param account The profile to be opened in a new window.
     * @throws IOException If the view profile FXML cannot be loaded.
     */
    public void loadProfileWindow (DonorReceiver account) throws IOException {



        // Only load new window if childWindowToFront fails.
        if (!App.childWindowToFront(account)) {

            // Set the selected account for the profile pane and confirm child.
            ViewProfilePaneController.setAccount(account);
            ViewProfilePaneController.setIsChild(true);

            // Create new pane.
            FXMLLoader loader = new FXMLLoader();
            AnchorPane profilePane = loader.load(getClass().getResourceAsStream(PageNav.VIEW));

            // Create new scene.
            Scene profileScene = new Scene(profilePane);

            // Create new stage.
            Stage profileStage = new Stage();
            profileStage.setTitle("Profile for " + account.getUserName());
            profileStage.setScene(profileScene);
            profileStage.show();
            profileStage.setX(App.getWindow().getX() + App.getWindow().getWidth());

            App.addChildWindow(profileStage, account);
        }

    }

    /**
     * Opens a view profile pane in a new window, then switches to the edit pane so the clinician can view the diseases
     *
     * @param account The profile to be opened in a new window.
     * @throws IOException If the view profile FXML cannot be loaded.
     */
    public void loadProfileWindowEdit (DonorReceiver account) throws IOException {

        // Only load new window if childWindowToFront fails.
        if (!App.childWindowToFront(account)) {

            // Set the selected donorReceiver for the profile pane and confirm child.
            ViewProfilePaneController.setAccount(account);
            ViewProfilePaneController.setIsChild(true);

            // Create new pane.
            FXMLLoader loader = new FXMLLoader();
            AnchorPane profilePane = loader.load(getClass().getResourceAsStream(PageNav.VIEW));

            // Create new scene.
            Scene profileScene = new Scene(profilePane);

            // Create new stage.
            Stage profileStage = new Stage();
            profileStage.setTitle("Profile for " + account.getUserName());
            profileStage.setScene(profileScene);
            profileStage.show();
            profileStage.setX(App.getWindow().getX() + App.getWindow().getWidth());

            App.addChildWindow(profileStage, account);

            loadEditWindow(account, profilePane, profileStage);
        }
    }

    /**
     * Switches to the edit profile screen within the given window. Called from method LoadProfileWindowEdit
     *
     * @throws IOException When the FXML cannot be retrieved.
     */
    private void loadEditWindow(DonorReceiver account, AnchorPane pane, Stage stage) throws IOException {

        // Set the selected account for the profile pane and confirm child.
        EditPaneController.setAccount(account);
        EditPaneController.setIsChild(true);

        // Create new pane.
        FXMLLoader loader = new FXMLLoader();
        AnchorPane editPane = loader.load(getClass().getResourceAsStream(PageNav.EDIT));
        // Create new scene.
        Scene editScene = new Scene(editPane);
        TabPane tabPane = (TabPane) editPane.lookup("#mainTabPane");
        TabPane profileViewTabbedPane = (TabPane) pane.lookup("#profileViewTabbedPane");
        int tabIndex = profileViewTabbedPane.getSelectionModel().getSelectedIndex();
        tabIndex += 5;
        tabPane.getSelectionModel().clearAndSelect(tabIndex);
        // Retrieve current stage and set scene.
        Stage current = (Stage) stage.getScene().getWindow();
        current.setScene(editScene);

    }

    /**
     * Returns true if the full name of the given account contains the given
     * string. Returns false otherwise. Both the full name and string will be
     * converted to lowercase during the comparison.
     *
     * @param record The account with a full name.
     * @param string The string which may be contained in the full name.
     * @return True if the full name of account contains string. False otherwise.
     */
    private boolean accountNameContainsString(ReceiverRecord record, String string) {

        // Generate full name from given, other, and last names.
        String fullName = record.getFullName();
        fullName = fullName.replaceAll("\\s+"," ").toLowerCase();

        return fullName.contains(string.toLowerCase());


    }


    /**
     * Returns true is the given account is a receiver for the given organ
     * @param record the record to check
     * @param organ the string name of the given orgran
     * @return true if receiver of organ, false if not
     */
    private boolean receiverRequiresOrgan(ReceiverRecord record, String organ){
        if (organ.equalsIgnoreCase("Any Organ")){return true;}
        else if ((organ.equalsIgnoreCase("Liver")) && (record.getOrgan().equalsIgnoreCase("Liver"))){return true;}
        else if ((organ.equalsIgnoreCase("Kidneys")) && (record.getOrgan().equalsIgnoreCase("Kidneys"))){return true;}
        else if ((organ.equalsIgnoreCase("Heart")) && (record.getOrgan().equalsIgnoreCase("Heart"))){return true;}
        else if ((organ.equalsIgnoreCase("Lungs")) && (record.getOrgan().equalsIgnoreCase("Lungs"))){return true;}
        else if ((organ.equalsIgnoreCase("Intestines")) && (record.getOrgan().equalsIgnoreCase("Intestines"))){return true;}
        else if ((organ.equalsIgnoreCase("Corneas")) && (record.getOrgan().equalsIgnoreCase("Corneas"))){return true;}
        else if ((organ.equalsIgnoreCase("Middle Ear")) && (record.getOrgan().equalsIgnoreCase("Middle Ear"))){return true;}
        else if ((organ.equalsIgnoreCase("Skin")) && (record.getOrgan().equalsIgnoreCase("Skin"))){return true;}
        else if ((organ.equalsIgnoreCase("Bone")) && (record.getOrgan().equalsIgnoreCase("Bone"))){return true;}
        else if ((organ.equalsIgnoreCase("Bone Marrow")) && (record.getOrgan().equalsIgnoreCase("Bone Marrow"))){return true;}
        else if ((organ.equalsIgnoreCase("Connective Tissue")) && (record.getOrgan().equalsIgnoreCase("Connective Tissue"))){return true;}
        else{return false;}
    }


    /**
     * Returns true is the given account is from the given region
     * @param record the record to check
     * @param region the string name of the given region
     * @return true if from region, false if not
     */
    private boolean receiverFromRegion(ReceiverRecord record, String region){
        if (region.equalsIgnoreCase("Any Region")){return true;}
        else{return record.getRegion().equalsIgnoreCase(region);}

    }


    /**
     * Populates the organ combobox with a static list of possible organs
     */
    private void initializeOrganFilterComboBox(){
        organFilterComboBox.setItems(organs);
        organFilterComboBox.getItems().addAll("Any Organ", "Liver", "Kidneys", "Heart", "Lungs", "Intestines",
                "Corneas", "Middle Ear", "Skin", "Bone", "Bone Marrow", "Connective Tissue");
        organFilterComboBox.getSelectionModel().select(0);
    }


    /**
     * Populates the region combobox dynamically with the regions of those on the transplant waiting list.
     */
    private void populateRegionFilterComboBox(){
        Collection<String> uniqueRegions = new TreeSet<String>();
        for (ReceiverRecord record: records) {
            uniqueRegions.add(record.getRegion());
        }
        regionFilterComboBox.getItems().removeAll(regionFilterComboBox.getItems());
        regionFilterComboBox.getItems().add("Any Region");
        regionFilterComboBox.getItems().addAll(uniqueRegions);
        regionFilterComboBox.getSelectionModel().select(0);
    }


    /**
     * Initialize the list view whenever the pane is created.
     */
    @FXML
    public void initialize() {

        transplantWaitingList.setPlaceholder(new Label("No organ requests found"));
        selectedAccount = null; // Reset selected account.
        setAccountsListRowFactory();
        initializeColumns();


        // Populate a master list with all Accounts, regardless of page.
        observableAccounts = FXCollections.observableArrayList();

        generateAccounts();

        for (DonorReceiver acc : observableAccounts) {
            if (acc.getRequiredOrgans().getLiver()) {
                ReceiverRecord record = new ReceiverRecord(fullName(acc), acc.getUserName(), acc.getContactDetails().getAddress().getRegion(), acc.getRequiredOrgans().getLiverTimeStamp(), "Liver");
                records.add(record);
            }
            if (acc.getRequiredOrgans().getKidneys()) {
                ReceiverRecord record = new ReceiverRecord(fullName(acc), acc.getUserName(), acc.getContactDetails().getAddress().getRegion(), acc.getRequiredOrgans().getKidneysTimeStamp(), "Kidneys");
                records.add(record);
            }
            if (acc.getRequiredOrgans().getHeart()) {
                ReceiverRecord record = new ReceiverRecord(fullName(acc), acc.getUserName(), acc.getContactDetails().getAddress().getRegion(), acc.getRequiredOrgans().getHeartTimeStamp(), "Heart");
                records.add(record);
            }
            if (acc.getRequiredOrgans().getLungs()) {
                ReceiverRecord record = new ReceiverRecord(fullName(acc), acc.getUserName(), acc.getContactDetails().getAddress().getRegion(), acc.getRequiredOrgans().getLungsTimeStamp(), "Lungs");
                records.add(record);
            }
            if (acc.getRequiredOrgans().getIntestine()) {
                ReceiverRecord record = new ReceiverRecord(fullName(acc), acc.getUserName(), acc.getContactDetails().getAddress().getRegion(), acc.getRequiredOrgans().getIntestineTimeStamp(), "Intestines");
                records.add(record);
            }
            if (acc.getRequiredOrgans().getCorneas()) {
                ReceiverRecord record = new ReceiverRecord(fullName(acc), acc.getUserName(), acc.getContactDetails().getAddress().getRegion(), acc.getRequiredOrgans().getCorneasTimeStamp(), "Corneas");
                records.add(record);
            }
            if (acc.getRequiredOrgans().getMiddleEars()) {
                ReceiverRecord record = new ReceiverRecord(fullName(acc), acc.getUserName(), acc.getContactDetails().getAddress().getRegion(), acc.getRequiredOrgans().getMiddleEarsTimeStamp(), "Middle Ear");
                records.add(record);
            }
            if (acc.getRequiredOrgans().getSkin()) {
                ReceiverRecord record = new ReceiverRecord(fullName(acc), acc.getUserName(), acc.getContactDetails().getAddress().getRegion(), acc.getRequiredOrgans().getSkinTimeStamp(), "Skin");
                records.add(record);
            }
            if (acc.getRequiredOrgans().getBone()) {
                ReceiverRecord record = new ReceiverRecord(fullName(acc), acc.getUserName(), acc.getContactDetails().getAddress().getRegion(), acc.getRequiredOrgans().getBoneTimeStamp(), "Bone");
                records.add(record);
            }
            if (acc.getRequiredOrgans().getBoneMarrow()) {
                ReceiverRecord record = new ReceiverRecord(fullName(acc), acc.getUserName(), acc.getContactDetails().getAddress().getRegion(), acc.getRequiredOrgans().getBoneMarrowTimeStamp(), "Bone Marrow");
                records.add(record);
            }
            if (acc.getRequiredOrgans().getConnectiveTissue()) {
                ReceiverRecord record = new ReceiverRecord(fullName(acc), acc.getUserName(), acc.getContactDetails().getAddress().getRegion(), acc.getRequiredOrgans().getConnectiveTissueTimeStamp(), "Connective Tissue");
                records.add(record);
            }
            if (acc.getRequiredOrgans().getPancreas()) {
                ReceiverRecord record = new ReceiverRecord(fullName(acc), acc.getUserName(), acc.getContactDetails().getAddress().getRegion(), acc.getRequiredOrgans().getPancreasTimeStamp(), "Pancreas");
                records.add(record);
            }
        }

        // Initialize filter comboboxes
        initializeOrganFilterComboBox();
        populateRegionFilterComboBox();

        /**  // Create a filtered account to exclude non-matching results.
         filteredRecords = new FilteredList<>(records, record -> {

         if (PageNav.searchValue.equals("")) {

         return true;

         } else {

         return accountNameContainsString(record, PageNav.searchValue);

         }

         });**/

        filteredRecords = new FilteredList<>(records, p -> true);
        organFilterComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue observable, String oldValue, String newValue) {
                filteredRecords.setPredicate(record -> receiverRequiresOrgan(record, newValue) &&
                        receiverFromRegion(record, regionFilterComboBox.getSelectionModel().getSelectedItem().toString())

                );
                updatePageCount();
                updateTableView();
            }

        });

        regionFilterComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue observable, String oldValue, String newValue) {
                filteredRecords.setPredicate(record -> receiverFromRegion(record, newValue) &&
                        receiverRequiresOrgan(record, organFilterComboBox.getSelectionModel().getSelectedItem().toString()));
                updatePageCount();
                updateTableView();
            }
        });

        /** searchForField.textProperty().addListener((observable, oldValue, newValue) -> {

         filteredRecords.setPredicate(account -> accountNameContainsString(account, newValue));

         updatePageCount();
         updateTableView();

         });**/

        sortedRecords = new SortedList<>(filteredRecords);
        //      sortedRecords.setComparator(new NameComparator());

        updatePageCount();
        updateTableView();

        pageControl.currentPageIndexProperty().addListener((observable, oldValue, newValue) -> {

            updateTableView();

        });

        // Event handler for account selection.
        transplantWaitingList.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent click) {

                ReceiverRecord record = (ReceiverRecord) transplantWaitingList.getSelectionModel().getSelectedItem();
                selectedRecord = record;
                if (record != null) {
                    DonorReceiver donorReceiver = accountManager.getAccountsByNHI(record.getNhi()).get(0);
                    selectedAccount = donorReceiver;

                    if (click.getButton().equals(MouseButton.PRIMARY) &&
                            click.getClickCount() == 2 && selectedAccount != null) {

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
                }
            }

            //searchField.setText(PageNav.searchValue); // Set the search text to what the user entered.
            // NOTE: This has to be set after the listener is added otherwise the filteredlist doesn't update the search

        });
    }


    /**
     * Creates a date string from the given LocalDate object formatted to "CCYYMMDD".
     * @param time a LocalDate object.
     * @return returns a formatted string of the form "CCYYMMDD".
     */
    public static String formatDateToString(LocalDate time){
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
        return time.format(dateFormat);
    }

    /**
     * Formats the creation date of an account into a readable value
     * @param time The LocalDatetime to be formatted
     * @return A string of format dd-MM-yyyy HH:mm:ss which represents the creation date of the account
     */
    public static String formatCreationDate(LocalDateTime time) {
        if(time == null) {
          return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String creationDate = time.format(formatter);
        return creationDate;
    }

    /**
     * Helper method for 'initialize()' which sets the cell factory of each
     * column in the TableView object.
     */
    private void initializeColumns() {

        // nameColumn
        nameColumn.setCellValueFactory(record -> new ReadOnlyStringWrapper(
                record.getValue().getFullName()
        ));

        // ageColumn
        organColumn.setCellValueFactory(record -> new ReadOnlyStringWrapper(
                record.getValue().getOrgan()));

        // genderColumn
        regionColumn.setCellValueFactory(record -> new
                ReadOnlyStringWrapper(record.getValue().getRegion()));

        // dateColumn. Depends on the given string
        dateColumn.setCellValueFactory(record -> new ReadOnlyStringWrapper(formatCreationDate(record.getValue().getTimestamp())));
    }

    /**
     * Update the receiving status of an account by checking if they're still receiving any organs
     * @param account The account we're checking
     */
    public void updateAccountReceiving(DonorReceiver account) {
        if(account.getRequiredOrgans().getLiver()) {
            account.setReceiver(true);
        }
        else if(account.getRequiredOrgans().getKidneys()) {
            account.setReceiver(true);
        }
        else if(account.getRequiredOrgans().getHeart()) {
            account.setReceiver(true);
        }
        else if(account.getRequiredOrgans().getLungs()) {
            account.setReceiver(true);
        }
        else if(account.getRequiredOrgans().getIntestine()) {
            account.setReceiver(true);
        }
        else if(account.getRequiredOrgans().getCorneas()) {
            account.setReceiver(true);
        }
        else if(account.getRequiredOrgans().getMiddleEars()) {
            account.setReceiver(true);
        }
        else if(account.getRequiredOrgans().getSkin()) {
            account.setReceiver(true);
        }
        else if(account.getRequiredOrgans().getBone()) {
            account.setReceiver(true);
        }
        else if(account.getRequiredOrgans().getBoneMarrow()) {
            account.setReceiver(true);
        }
        else if(account.getRequiredOrgans().getConnectiveTissue()) {
            account.setReceiver(true);
        }
        else if(account.getRequiredOrgans().getPancreas()) {
            account.setReceiver(true);
        }
        else {
            account.setReceiver(false);
        }
    }

}
