package seng302.controllers;


import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.CheckComboBox;
import seng302.*;
import seng302.model.person.DonorReceiver;
import seng302.model.AccountManager;
import seng302.model.PageNav;
import seng302.model.comparitors.*;
import java.io.IOException;
import java.util.*;
import java.util.function.UnaryOperator;


/**
 * This is a JavaFX controller for the ListView pane. This displays a list of
 * accounts in a table which can be sorted in ascending or descending order.
 */
public class ListViewController {


    /**
     * The TableView object in which each TableColumn object is contained.
     */
    @FXML private HeaderlessTable accountsList;

    /**
     * Checkbox to filter donors with male specified as their birth gender.
     */
    @FXML private CheckBox maleCheckBox;

    /**
     * Checkbox to filter donors with female specified as their birth gender.
     */
    @FXML private CheckBox femaleCheckBox;

    /**
     * Checkbox to filter donors with other specified as their birth gender.
     */
    @FXML private CheckBox otherCheckBox;

    /**
     * Checkbox to filter donors with other unknown as their birth gender.
     */
    @FXML private CheckBox unknownCheckBox;

    /**
     * Checkbox to filter donors with no specified birth gender.
     */
    @FXML private CheckBox unspecifiedCheckBox;

    /**
     * TableColumn object for displaying account names.
     */
    @FXML private TableColumn<DonorReceiver, String> nameColumn;

    /**
     * Button for changing name column sort order.
     */
    @FXML private Button nameOrder;

    /**
     * TableColumn for displaying account ages.
     */
    @FXML private TableColumn<DonorReceiver, Integer> ageColumn;

    /**
     * Button for changing age column sort order.
     */
    @FXML private Button ageOrder;

    /**
     * TableColumn object for displaying account genders.
     */
    @FXML private TableColumn<DonorReceiver, Character> genderColumn;

    /**
     * Button for changing gender order.
     */
    @FXML private Button genderOrder;

    /**
     * TableColumn object for displaying account regions.
     */
    @FXML private TableColumn<DonorReceiver, String> regionColumn;

    /**
     * Button for changing region order.
     */
    @FXML private Button regionOrder;

    /**
     * TableColumn object for displaying account citys.
     */
    @FXML private TableColumn<DonorReceiver, String> cityColumn;

    /**
     * Button for changing city order
     */
    @FXML private Button cityOrder;

    /**
     * TableColumn object for displaying account status (donor/receiver).
     */
    @FXML private TableColumn<DonorReceiver, String> statusColumn;

    /**
     * Button for changing account status (donor/receiver) order
     */
    @FXML private Button statusOrder;

    /**
     * A search field used by the user to narrow the number of accounts shown.
     */
    @FXML private TextField searchField; // NOT USED, TASK NOT STARTED.

    /**
     * A Button object which redirects the user to the edit pane when clicked.
     */
    @FXML private Button editButton;

    /**
     * A Button object which redirects the user to the view pane when clicked.
     */
    @FXML private Button viewButton;

    @FXML
    private Button createDonorButton;

    /**
     * A Button object which is used to delete the selected account.
     */
    @FXML private Button deleteButton;

    /**
     * A Button object which redirects the user to the main menu when clicked.
     */
    @FXML private Button backButton;

    /**
     * A Pagination object used to switch between TableView pages.
     */
    @FXML private Pagination pageControl;


    /**
     * Label for displaying the number of matches during a search.
     */
    @FXML private Label matches;


    /**
     *  CheckComboBox for filtering table entries by birth gender
     */
    @FXML private CheckComboBox filterBirthGender;


    /**
     *  CheckComboBox for filtering of table entries by receiver organs
     */
    @FXML private CheckComboBox filterReceiverOrgans;


    /**
     *  CheckComboBox for filtering of table entries by donor organs
     */
    @FXML private CheckComboBox filterDonorOrgans;


    /**
     *  CheckComboBox for filtering of table entries by region
     */
    @FXML private CheckComboBox filterRegion;


    /**
     *  CheckComboBox for filtering of table entries by receiver/donor status
     */
    @FXML private CheckComboBox filterStatus;


    /**
     *  TextField for filtering of table entries by minimum age
     */
    @FXML private TextField filterMinAge;


    /**
     *  TextField for filtering of table entries by maximum age
     */
    @FXML private TextField filterMaxAge;


    /**
     *  ObservableList for storing region filtering options
     */
    private ObservableList<String> regions = FXCollections.observableArrayList();


    /**
     *  ObservableList for storing birth gender filtering options
     */
    private ObservableList<String> birthGenders = FXCollections.observableArrayList("Any Gender", "Male",
            "Female", "Other", "Unknown", "Unspecified");


    /**
     *  ObservableList for storing receiver organ filtering options
     */
    private ObservableList<String> receiverOrgans = FXCollections.observableArrayList("Any Receiver Organ",
            "Liver","Kidneys", "Pancreas", "Heart", "Lungs", "Intestine", "Corneas",
            "Middle Ears", "Skin", "Bone", "Bone marrow", "Connective Tissue");


    /**
     *  ObservableList for storing donor organ filtering options
     */
    private ObservableList<String> donorOrgans = FXCollections.observableArrayList("Any Donor Organ", "Liver",
            "Kidneys", "Pancreas", "Heart", "Lungs", "Intestine", "Corneas",
            "Middle Ears", "Skin", "Bone", "Bone marrow", "Connective Tissue");


    /**
     *  ObservableList for storing donor/receiver status filtering options
     */
    private ObservableList<String> donorReceiverStatus = FXCollections.observableArrayList("Any Status",
            "Donor/Receiver", "Donor", "Receiver", "Neither");


    /**
     *  ArrayList for storing CheckComboBox filtering elements
     */
    private ArrayList<CheckComboBox> checkComboBoxes = new ArrayList<CheckComboBox>();


    /**
     *  ArrayList for storing TextField filtering elements
     */
    private ArrayList<TextField> textFields = new ArrayList<>();


    /**
     * An account selected by the user on the table.
     */
    private static DonorReceiver selectedAccount;

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
    private FilteredList<DonorReceiver> filteredAccounts;


    /**
     * A list of sorted accounts derived from filteredAccounts.
     */
    private SortedList<DonorReceiver> sortedAccounts;

    /**
     * Enumeration for referencing column sort order.
     */
    private enum Order {DEFAULT, NAME_ASC, NAME_DESC, AGE_ASC, AGE_DESC,
        GENDER_ASC, GENDER_DESC, REGION_ASC, REGION_DESC, CITY_ASC, CITY_DESC, STATUS_ASC, STATUS_DESC};

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
     * Using criteria specified in the search field, an ArrayList of matching
     * accounts is retrieved from the account manager.
     */
    private void generateAccounts() {

        // Clear and repopulate observableAccounts.
        observableAccounts.removeAll(observableAccounts);
        observableAccounts.addAll(accountManager.getAccountsByName(""));

    }


    /**
     * Deletes the selected account.
     */
    @FXML
    private void deleteSelected() {

        if (selectedAccount != null) {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete DonorReceiver");
            alert.setHeaderText("You are going to delete " +
                    selectedAccount.getUserName() + " (" +
                    selectedAccount.getFirstName() + " " +
                    selectedAccount.getMiddleName() + " " +
                    selectedAccount.getLastName() + ")");
            alert.setContentText("Do you wish to proceed?");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == ButtonType.OK) {

                accountManager.issueDeleteWithoutConfirmation(AccountManager.getCurrentUser(), selectedAccount.getUserName());
                PageNav.loadNewPage(PageNav.LISTVIEW);

            }

        } else {

            // Warn user if no account was selected.
            noAccountSelectedAlert("Delete DonorReceiver");

        }


    }


    /**
     * Switches back to the main menu screen.
     */
    @FXML
    private void backSelected() {
        if (PageNav.isAdministrator) {
            PageNav.isAdministrator = false;
            PageNav.loadNewPage(PageNav.ADMINMENU);
        } else {
            PageNav.loadNewPage(PageNav.MAINMENU);
        }

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
     * Switches back to the Donor list
     */
    @FXML
    private void transplantSelected() {
        PageNav.loadNewPage(PageNav.TRANSPLANTLIST);
    }


    /**
     * Updates the text of all order buttons on the list view pane.
     */
    private void updateOrderButtons() {

        // Reset button names.
        nameOrder.setText("Name");
        ageOrder.setText("Age");
        genderOrder.setText("Gender");
        regionOrder.setText("Region");
        cityOrder.setText("City");
        statusOrder.setText("Donor/Receiver");

        if (sortOrder == Order.NAME_ASC) {

            nameOrder.setText(nameOrder.getText() + ASC_TEXT);
            sortedAccounts.setComparator(new NameAlphabeticalComparator());

        } else if (sortOrder == Order.NAME_DESC) {

            nameOrder.setText(nameOrder.getText() + DESC_TEXT);
            sortedAccounts.setComparator(new NameAlphabeticalComparator().reversed());

        } else if (sortOrder == Order.AGE_ASC) {

            ageOrder.setText(ageOrder.getText() + ASC_TEXT);
            sortedAccounts.setComparator(new AgeComparator());

        } else if (sortOrder == Order.AGE_DESC) {

            ageOrder.setText(ageOrder.getText() + DESC_TEXT);
            sortedAccounts.setComparator(new AgeComparator().reversed());

        } else if (sortOrder == Order.GENDER_ASC) {

            genderOrder.setText(genderOrder.getText() + ASC_TEXT);
            sortedAccounts.setComparator(new GenderComparator());

        } else if (sortOrder == Order.GENDER_DESC) {

            genderOrder.setText(genderOrder.getText() + DESC_TEXT);
            sortedAccounts.setComparator(new GenderComparator().reversed());

        } else if (sortOrder == Order.REGION_ASC) {

            regionOrder.setText(regionOrder.getText() + ASC_TEXT);
            sortedAccounts.setComparator(new RegionComparator());

        } else if (sortOrder == Order.REGION_DESC) {

            regionOrder.setText(regionOrder.getText() + DESC_TEXT);
            sortedAccounts.setComparator(new RegionComparator().reversed());

        } else if (sortOrder == Order.CITY_ASC) {

            cityOrder.setText(cityOrder.getText() + ASC_TEXT);
            sortedAccounts.setComparator(new CityComparator());

        } else if (sortOrder == Order.CITY_DESC) {

            cityOrder.setText(cityOrder.getText() + DESC_TEXT);
            sortedAccounts.setComparator(new CityComparator().reversed());

        } else if (sortOrder == Order.STATUS_ASC) {

            statusOrder.setText(statusOrder.getText() + ASC_TEXT);
            sortedAccounts.setComparator(new DonorReceiverStatusComparator());

        } else if (sortOrder == Order.STATUS_DESC) {

            statusOrder.setText(statusOrder.getText() + DESC_TEXT);
            sortedAccounts.setComparator(new DonorReceiverStatusComparator().reversed());

        } else if (sortOrder == Order.DEFAULT) {

            // Revert to default order based on name.
            sortedAccounts.setComparator(new NameComparator());

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
    private void updateAgeOrder(ActionEvent event) {

        if (sortOrder == Order.AGE_ASC) {

            sortOrder = Order.AGE_DESC;

        } else if (sortOrder == Order.AGE_DESC) {

            sortOrder = Order.DEFAULT;

        } else {

            sortOrder = Order.AGE_ASC;

        }

        updateOrderButtons();

    }


    /**
     * Event handler for the genderOrder button.
     *
     * @param event An action which occurred on the genderOrder button.
     */
    @FXML
    private void updateGenderOrder(ActionEvent event) {

        if (sortOrder == Order.GENDER_ASC) {

            sortOrder = Order.GENDER_DESC;

        } else if (sortOrder == Order.GENDER_DESC) {

            sortOrder = Order.DEFAULT;

        } else {

            sortOrder = Order.GENDER_ASC;

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

    /**
     * Event handler for the cityOrder button.
     *
     * @param event An action which occurred on the cityOrder button.
     */
    @FXML
    private void updateCityOrder(ActionEvent event) {

        if (sortOrder == Order.CITY_ASC) {

            sortOrder = Order.CITY_DESC;

        } else if (sortOrder == Order.CITY_DESC) {

            sortOrder = Order.DEFAULT;

        } else {

            sortOrder = Order.CITY_ASC;

        }

        updateOrderButtons();

    }

    /**
     * Event handler for the statusOrder button.
     *
     * @param event An action which occurred on the statusOrder button.
     */
    @FXML
    private void updateStatusOrder(ActionEvent event) {

        if (sortOrder == Order.STATUS_ASC) {

            sortOrder = Order.STATUS_DESC;

        } else if (sortOrder == Order.STATUS_DESC) {

            sortOrder = Order.DEFAULT;

        } else {

            sortOrder = Order.STATUS_ASC;

        }

        updateOrderButtons();

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


    /**
     * Generates a message which indicates the number of accounts matched in
     * a search. The number corresponds to the size of sortedAccounts.
     *
     * @return A message indicating the size of sortedAccounts.
     */
    private String getMatchesMessage() {

        int number = sortedAccounts.size();

        if (number == 1) {
            return "\n" +
                    number + " matching account";

        } else {
            int startNumber = pageControl.getCurrentPageIndex() * 16 + 1;
            int endNumber = (pageControl.getCurrentPageIndex() + 1) * 16;
            endNumber = (endNumber < number) ? endNumber : number;  // Set the endNumber to the lower value
            return "Accounts " + startNumber + "-" + endNumber + "\n" +
                    number + " matching accounts";

        }

    }


    /**
     * Updates the page count shown by the pagination widget and resets the
     * current page index to 0.
     */
    private void updateTableView() {

        // Update label showing number of matches.
        matches.setTextAlignment(TextAlignment.CENTER);
        matches.setText(getMatchesMessage());

        // Retrieve start and end index of current page.
        int startIndex = (int)(pageControl.getCurrentPageIndex() * ACCOUNTS_PER_PAGE);
        int endIndex = Math.min(startIndex + (int)ACCOUNTS_PER_PAGE, filteredAccounts.size());

        // Create new sublist.
        SortedList<DonorReceiver> pageDonorReceivers = new SortedList<>(
                FXCollections.observableArrayList(sortedAccounts.subList(startIndex, endIndex)));
        pageDonorReceivers.comparatorProperty().bind(accountsList.comparatorProperty());

        accountsList.setItems(pageDonorReceivers);

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
     * Sets the row factory for accountsList. This creates a tooltip for each
     * populated row showing an overview of the account. This code is based on
     * a solution (https://stackoverflow.com/a/26221242) posted by James_D in
     * response to a question on Stack Overflow.
     */
    private void setAccountsListRowFactory() {

        accountsList.setRowFactory(tableView -> new TableRow<DonorReceiver>() {

            @Override
            public void updateItem(DonorReceiver donorReceiver, boolean empty) {

                super.updateItem(donorReceiver, empty);

                if (donorReceiver == null) {

                    // If no donorReceiver is associated with row, remove tool tip.
                    setTooltip(null);

                } else {

                    // Otherwise, generate donorReceiver-specific tool tip.
                    Tooltip overview = new Tooltip();
                    String conflictingOrgans = donorReceiver.isReceivingDonatingOrgans();

                    // Highlight the row if the donorReceiver is donating an organ they're receiving
                    if (conflictingOrgans != null) {
                      setStyle("-fx-background-color:f93333;-fx-opacity:0.5;");
                      overview.setText(donorReceiver.generateOverview()
                      + '\n' + donorReceiver.isReceivingDonatingOrgans());
                    }
                    else {
                      overview.setText(donorReceiver.generateOverview());
                      setStyle(""); // Reset the style to prevent random rows from being higlighted
                    }
                  setTooltip(overview);
                }

            }

        });

    }

  /**
   * Refresh the table when any account receives a changeF
   * @throws NullPointerException an exception
   */
    public static void triggerRefresh() throws NullPointerException {

        TableView accountsList = (TableView) App.getWindow().getScene().lookup("#accountsList");

        if (accountsList == null) {

            // Handle case were accountsList is not initialised.
            throw new NullPointerException("TableView object 'accountsList' is not initialised in the main window.");

        } else {

            // Trigger instantiated update.
            accountsList.refresh();

        }

    }

    /**
     * Opens a view profile pane in a new window.
     *
     * @param donorReceiver The profile to be opened in a new window.
     * @throws IOException If the view profile FXML cannot be loaded.
     */
    public void loadProfileWindow (DonorReceiver donorReceiver) throws IOException {

        // Only load new window if childWindowToFront fails.
        if (!App.childWindowToFront(donorReceiver)) {

            // Set the selected donorReceiver for the profile pane and confirm child.
            ViewProfilePaneController.setAccount(donorReceiver);
            ViewProfilePaneController.setIsChild(true);

            // Create new pane.
            FXMLLoader loader = new FXMLLoader();
            AnchorPane profilePane = loader.load(getClass().getResourceAsStream(PageNav.VIEW));

            // Create new scene.
            Scene profileScene = new Scene(profilePane);

            // Create new stage.
            Stage profileStage = new Stage();
            profileStage.setTitle("Profile for " + donorReceiver.getUserName());
            profileStage.setScene(profileScene);
            profileStage.show();

            // Place stage in center of main window.
            profileStage.setX(App.getWindow().getX() + ((App.getWindow().getWidth() - profileStage.getWidth()) / 2.0));
            profileStage.setY(App.getWindow().getY() + ((App.getWindow().getHeight() - profileStage.getHeight()) / 2.0));

            App.addChildWindow(profileStage, donorReceiver);
        }

    }


    /**
     * Returns true if the full name of the given donorReceiver contains the given
     * string. Returns false otherwise. Both the full name and string will be
     * converted to lowercase during the comparison.
     *
     * @param donorReceiver The donorReceiver with a full name.
     * @param string The string which may be contained in the full name.
     * @return True if the full name of donorReceiver contains string. False otherwise.
     */
    private boolean isAccountVisible(DonorReceiver donorReceiver, String string) {

        // Generate full name from given, other, and last names.
        String fullName = donorReceiver.getFirstName() + " " + donorReceiver.getMiddleName() + " " + donorReceiver.getLastName();
        fullName = fullName.replaceAll("\\s+"," ").toLowerCase();

        char birthGender = donorReceiver.getBirthGender();

        // Return true only if the full name contains the specified string and
        // the checkbox corresponding to birth gender is checked.
        return fullName.contains(string.toLowerCase()) && ((
                birthGender == 'M' && maleCheckBox.isSelected()) ||
                (birthGender == 'F' && femaleCheckBox.isSelected()) ||
                (birthGender == 'O' && otherCheckBox.isSelected()) ||
                (birthGender == 'U' && unknownCheckBox.isSelected()) ||
                (birthGender == 0 && unspecifiedCheckBox.isSelected()));

    }


    /**
     * Initialize the list view whenever the pane is created.
     */
    @FXML
    public void initialize() {
        accountsList.setPlaceholder(new Label("No accounts found"));
        if(!PageNav.isAdministrator) {
            createDonorButton.setVisible(false);
            createDonorButton.setDisable(true);
        }

        selectedAccount = null; // Reset selected account.
        setAccountsListRowFactory();
        initializeColumns(); // Set comparators.

        // Populate a master list with all Accounts, regardless of page.
        observableAccounts = FXCollections.observableArrayList();
        generateAccounts();

        // Create a filtered account to exclude non-matching results.
        filteredAccounts = new FilteredList<>(observableAccounts, account -> {

            if (PageNav.searchValue.equals("")) {

                return true;

            } else {

                return isAccountVisible(account, PageNav.searchValue);

            }

        });

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {

            filteredAccounts.setPredicate(account -> isAccountVisible(account, newValue));

            updatePageCount();
            updateTableView();

        });

        sortedAccounts = new SortedList<>(filteredAccounts);
        sortedAccounts.setComparator(new NameComparator());

        updatePageCount();
        updateTableView();

        pageControl.currentPageIndexProperty().addListener((observable, oldValue, newValue) -> {

            updateTableView();

        });

        // Event handler for account selection.
        accountsList.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent click) {

                selectedAccount = (DonorReceiver)accountsList.getSelectionModel().getSelectedItem();

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
        });

        searchField.setText(PageNav.searchValue); // Set the search text to what the user entered.
                                                  // NOTE: This has to be set after the listener is added otherwise the filteredlist doesn't update the search
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

        statusColumn.setCellValueFactory(record -> new ReadOnlyStringWrapper(
                getAccountStatus(record.getValue())
        ));

        // ageColumn
        ageColumn.setCellValueFactory(record -> new SimpleIntegerProperty(
                record.getValue().calculateAge()).asObject());

        // genderColumn
        genderColumn.setCellValueFactory(record -> new
                ReadOnlyObjectWrapper<Character>(record.getValue().getGender()));

        // regionColumn
        regionColumn.setCellValueFactory(record -> new ReadOnlyStringWrapper(
                record.getValue().getContactDetails().getAddress().getRegion()));

        // cityColumn
        cityColumn.setCellValueFactory(record -> new ReadOnlyStringWrapper(
                record.getValue().getContactDetails().getAddress().getCityName()));

    }


    /**
     * Sets the event handlers for each birth gender checkbox.
     */
    @FXML
    private void birthGenderFilterSelected() {

        filteredAccounts.setPredicate(account -> isAccountVisible(account, searchField.getText()));
        updatePageCount();
        updateTableView();

    }

    /**
     * Checks the status of a donor
     */
    private String getAccountStatus(DonorReceiver account) {
        String donatingString = account.getDonorOrganInventory().toString(); // to-do: Once accounts are refactored change this

        Boolean isDonating = true;
        Boolean isReceiving = account.isReceiver();

        if(donatingString.equals("Organs to donate:\nNo organs to donate\n\n")) {
            isDonating = false;
        }

        if (isDonating && isReceiving) {
            return "Donor/Receiver";
        }
        if (isDonating) {
            return "Donor";
        }
        if (isReceiving) {
            return "Receiver";
        }
        return "";
    }


    /**
     * Returns true if the given account has an greater than or equal to
     * min and less than or equal to max. If max/min arent present then true.
     * Otherwise false.
     *
     * @param account account to be filtered
     * @return true if age is in the given range, false otherwise.
     */
    private boolean accountAgeRange(DonorReceiver account){
        if (filterMaxAge.getText().isEmpty() && filterMinAge.getText().isEmpty()){
            return true;
        } else if (filterMaxAge.getText().isEmpty()) {
            return (account.calculateAge() >= Integer.parseInt(filterMinAge.getText()));
        } else if (filterMinAge.getText().isEmpty()) {
            return (Integer.parseInt(filterMaxAge.getText()) >= account.calculateAge());
        } else {
           return ((Integer.parseInt(filterMaxAge.getText()) >= account.calculateAge())&&
                   (account.calculateAge() >= Integer.parseInt(filterMinAge.getText())));
        }
    }


    /**
     * Returns true if the given account is from the given region
     *
     * @param account the account to check
     * @param region  the string name of the given region
     * @return true if from region, false if not
     */
    private boolean accountFromRegion(DonorReceiver account, ObservableList<String> region) {
        if (region.contains("Any Region")) {
            return true;
        } else {
            return region.contains(account.getContactDetails().getAddress().getRegion());
        }
    }


    /**
     * Returns true if the given account matches the status filter
     *
     * @param account the account to filter
     * @param donorReceiverStatus  an ObservableList of status strings
     * @return true if matching status, false if not
     */
    private boolean accountStatus(DonorReceiver account, ObservableList<String> donorReceiverStatus) {
        if (donorReceiverStatus.contains("Any Status")) {
            return true;
        } else if ((donorReceiverStatus.contains("Neither")) && (getAccountStatus(account).equalsIgnoreCase(""))) {
            return true;
        } {
            return donorReceiverStatus.contains(getAccountStatus(account));
        }
    }


    /**
     * Returns true if the given account matches any of the given genders.
     * False otherwise.
     * @param account the account to filter
     * @param birthGenders the genders that are accepted
     * @return true if matching, false otherwise
     */
    private boolean accountBirthGender(DonorReceiver account, ObservableList<String> birthGenders) {
        if (birthGenders.contains("Any Gender")) {
            return true;
        } else {
            return birthGenders.contains(account.genderString());
        }
    }


    /**
     * Returns true if the given accounts receiver organs matches any of the
     * given organs. False otherwise.
     * @param account the account to filter
     * @param receiverOrgans the organs that are accepted
     * @return true if matching, false otherwise
     */
    private boolean accountReceiverOrgans(DonorReceiver account, ObservableList<String> receiverOrgans) {
        if (receiverOrgans.contains("Any Receiver Organ")) {
            return true;
        } else {
            for (String organ: receiverOrgans){
                if (account.getRequiredOrgans().toString().contains(organ)){
                    return true;
                }
            }
            return false;
        }
    }


    /**
     * Returns true if the given accounts donor organs matches any of the
     * given organs. False otherwise.
     * @param account the account to filter
     * @param donorOrgans the organs that are accepted
     * @return true if matching, false otherwise
     */
    private boolean accountDonorOrgans(DonorReceiver account, ObservableList<String> donorOrgans) {
        if (donorOrgans.contains("Any Donor Organ")) {
            return true;
        } else {
            for (String organ: donorOrgans){
                if (account.getDonorOrganInventory().toString().contains(organ)){
                    return true;
                }
            }
            return false;
        }
    }


    /**
     * Uses TextFormatter to only allow numbers in the given TextField
     * @param numberField TextField to restrict
     */
    private void numberOnlyTextField(TextField numberField){
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String input = change.getText();
            if (input.matches("[0-9]*")) {
                return change;
            }
            return null;
        };
        numberField.setTextFormatter(new TextFormatter<String>(integerFilter));
    }


    /**
     * Returns true if the full name of the given account contains the given
     * string. Returns false otherwise. Both the full name and string will be
     * converted to lowercase during the comparison.
     *
     * @param account The account with a full name.
     * @param string The string which may be contained in the full name.
     * @return True if the full name of account contains string. False otherwise.
     */
    private boolean accountName(DonorReceiver account, String string) {

        // Generate full name from given, other, and last names.
        String fullName = account.getFirstName() + " " + account.getMiddleName() + " " + account.getLastName();
        fullName = fullName.replaceAll("\\s+", " ").toLowerCase();

        // Return true only if the full name contains the specified string and
        // the checkbox corresponding to birth gender is checked.
        return fullName.contains(string.toLowerCase());
    }


    /**
     * Used to filter the list, called by filter and search event listeners
     */
    private void updateFiltering() {
        filteredAccounts.setPredicate(account ->
                accountFromRegion(account, filterRegion.getCheckModel().getCheckedItems()) &&
                accountBirthGender(account, filterBirthGender.getCheckModel().getCheckedItems()) &&
                accountDonorOrgans(account, filterDonorOrgans.getCheckModel().getCheckedItems()) &&
                accountReceiverOrgans(account, filterReceiverOrgans.getCheckModel().getCheckedItems()) &&
                accountStatus(account, filterStatus.getCheckModel().getCheckedItems()) &&
                accountName(account, searchField.getText()) &&
                accountAgeRange(account));

        updatePageCount();
        updateTableView();
    }


    /**
     * Populates the filter options and adds the filter elements to ArrayLists so that
     * they can have event listeners added iteratively.
     */
    private void populateFilterOptions(){

        // Add the GUI elements used for filtering to ArrayLists so that event listeners can be added iteratively
        checkComboBoxes.addAll(Arrays.asList(filterBirthGender, filterReceiverOrgans, filterDonorOrgans, filterRegion, filterStatus));
        textFields.addAll(Arrays.asList(searchField, filterMaxAge, filterMinAge));

        // Set the min and max age TextFields to only accept positive integer input
        numberOnlyTextField(filterMaxAge);
        numberOnlyTextField(filterMinAge);

        // Dynamically generate options for region filtering
        Collection<String> uniqueRegions = new TreeSet<String>();
        for (DonorReceiver account: observableAccounts) {
            uniqueRegions.add(account.getContactDetails().getAddress().getRegion());
        }
        regions.add("Any Region");
        regions.addAll(uniqueRegions);
        filterRegion.getItems().addAll(regions);

        // Populate the filters that have static options
        filterDonorOrgans.getItems().addAll(donorOrgans);
        filterReceiverOrgans.getItems().addAll(receiverOrgans);
        filterBirthGender.getItems().addAll(birthGenders);
        filterStatus.getItems().addAll(donorReceiverStatus);

        // Set all CheckComboBoxes to the default value
        filterBirthGender.getCheckModel().check(0);
        filterDonorOrgans.getCheckModel().check(0);
        filterReceiverOrgans.getCheckModel().check(0);
        filterStatus.getCheckModel().check(0);
        filterRegion.getCheckModel().check(0);
    }


    /**
     * Iteratively adds listeners to the elements used for filtering.
     */
    private void setupFilterListeners() {

        //Add an event listener to each CheckComboBox
        checkComboBoxes.forEach(comboBox -> {
            comboBox.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
                @Override
                public void onChanged(ListChangeListener.Change<? extends String> c) {
                    updateFiltering();
                }
            });
        });

        //Add an event listener to each TextField
        textFields.forEach(textField -> {
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                updateFiltering();

            });
        });
    }

    @FXML
    private void createDonorPressed() {
        PageNav.loadNewPage(PageNav.CREATE);
    }
}

