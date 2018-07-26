package seng302.controllers;


import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.TreeSet;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Pair;
import seng302.App;
import seng302.model.AccountManager;
import seng302.model.PageNav;
import seng302.model.ReceiverOrganInventory;
import seng302.model.ReceiverRecord;
import seng302.model.TransplantWaitingList;
import seng302.model.comparitors.RecDateComparator;
import seng302.model.comparitors.RecNameAlphabeticalComparator;
import seng302.model.comparitors.RecOrganComparator;
import seng302.model.comparitors.RegionComparatorRec;
import seng302.model.person.DonorReceiver;

public class TransplantWaitingListPaneController extends ListController {

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
  private ComboBox<String> organFilterComboBox;

  @FXML
  private ComboBox<String> regionFilterComboBox;

  @FXML
  private Label matchingAccounts;

  @FXML
  private TextField searchForField;

  @FXML
  private Pagination pageControl;

  private ObservableList<String> regions = FXCollections.observableArrayList();

  private ObservableList<String> organs = FXCollections.observableArrayList();

  private static ObservableList<ReceiverRecord> records = FXCollections.observableArrayList();

  /**
   * An account selected by the user on the table.
   */
  private DonorReceiver selectedAccount;

  private static ReceiverRecord selectedRecord;

  /**
   * The AccountManager object which handles all account operations.
   */
  private AccountManager accountManager = App.getDatabase();

  /**
   * Constant which determines the number of accounts on each table page.
   */
  private static final double ACCOUNTS_PER_PAGE = 16.0;

  /**
   * The list of all accounts.
   */
  private ObservableList<DonorReceiver> observableAccounts;

  /**
   * The list of all accounts to be displayed in TableView that match the search criteria.
   */
  private FilteredList<ReceiverRecord> filteredRecords;


  /**
   * A list of sorted accounts derived from filteredRecords.
   */
  private SortedList<ReceiverRecord> sortedRecords;

  /**
   * Enumeration for referencing column sort order.
   */
  private enum Order {
    DEFAULT, NAME_ASC, NAME_DESC, ORGAN_ASC, ORGAN_DESC,
    REGION_ASC, REGION_DESC, DATE_ASC, DATE_DESC
  }

  /**
   * The currently selected sort order.
   */
  private Order sortOrder = Order.DEFAULT;

  /**
   * A constant for the ascending button text.
   */
  private static final String ascText = " (Ascending)";

  /**
   * A constant for the descending button text.
   */
  private static final String descText = " (Descending)";

  /**
   * Retrieves the selected account.
   *
   * @return The selected DonorReceiver object.
   */
  public DonorReceiver getSelectedAccount() {
    return selectedAccount;
  }

  public ObservableList<DonorReceiver> getObservableAccounts() {
    return observableAccounts;
  }

  private AccountManager accountManagerTest;

  private String regionString = "Any Region";
  private String organString = "Any Organ";

  private String errorOption = "1. Error";
  private String curedOption = "2. Cured";
  private String deathOption = "3. Death";

  private String receiverAsString = "receiver";
  private String organsAsString = "organs";
  private String updateAsString = "update";


  /**
   * A base constructor that should only be used for testing. This is so private methods can be
   * accessed for testing
   */
  public TransplantWaitingListPaneController() {
    observableAccounts = FXCollections.observableArrayList();
    accountManagerTest = accountManager;
    generateAccounts();
  }


  /**
   * Using criteria specified in the search field, an ArrayList of matching accounts is retrieved
   * from the account manager.
   */
  private void generateAccounts() {
    observableAccounts.removeAll(observableAccounts);
    for (DonorReceiver acc : accountManager.getDonorReceivers().values()) {
      if (acc.getReceiver()) {
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

      nameButton.setText(nameButton.getText() + ascText);
      sortedRecords.setComparator(new RecNameAlphabeticalComparator());

    } else if (sortOrder == Order.NAME_DESC) {

      nameButton.setText(nameButton.getText() + descText);
      sortedRecords.setComparator(new RecNameAlphabeticalComparator().reversed());

    } else if (sortOrder == Order.ORGAN_ASC) {

      organButton.setText(organButton.getText() + ascText);
      sortedRecords.setComparator(new RecOrganComparator());

    } else if (sortOrder == Order.ORGAN_DESC) {

      organButton.setText(organButton.getText() + descText);
      sortedRecords.setComparator(new RecOrganComparator().reversed());

    } else if (sortOrder == Order.DATE_ASC) {

      dateButton.setText(dateButton.getText() + ascText);
      sortedRecords.setComparator(new RecDateComparator());

    } else if (sortOrder == Order.DATE_DESC) {

      dateButton.setText(dateButton.getText() + descText);
      sortedRecords.setComparator(new RecDateComparator().reversed());

    } else if (sortOrder == Order.REGION_ASC) {

      regionButton.setText(regionButton.getText() + ascText);
      sortedRecords.setComparator(new RegionComparatorRec());

    } else if (sortOrder == Order.REGION_DESC) {

      regionButton.setText(regionButton.getText() + descText);
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
    regionFilterComboBox.getSelectionModel().select(regionString);
    organFilterComboBox.getSelectionModel().select(organString);
  }

//  /**
//   * Generates an alert asking the user to select an account with a title specified by the parameter
//   * title.
//   * @param title The title of the alert.
//   */
//  private void noAccountSelectedAlert(String title) {
//    Alert alert = new Alert(Alert.AlertType.INFORMATION);
//    alert.setTitle(title);
//    alert.setHeaderText(null);
//    alert.setContentText("Please select an account.");
//    alert.showAndWait();
//  }

//  /**
//   * Updates the number of pages shown by pageControl and resets the current index page to 0.
//   */
//  private void updatePageCount() {
//
//    pageControl.setCurrentPageIndex(0);
//    pageControl.setPageCount(Math.max(1, (int) Math.ceil(sortedRecords.size() /
//        ACCOUNTS_PER_PAGE)));
//
//  }


  /**
   * Updates the page count shown by the pagination widget and resets the current page index to 0.
   */
  private void updateTableView() {

    // Update label showing number of matches.
    matchingAccounts.setTextAlignment(TextAlignment.CENTER);
    matchingAccounts.setText(TransplantWaitingList.getMatchesMessage(sortedRecords, pageControl));

    // Retrieve start and end index of current page.
    int startIndex = (int) (pageControl.getCurrentPageIndex() * ACCOUNTS_PER_PAGE);
    int endIndex = Math.min(startIndex + (int) ACCOUNTS_PER_PAGE, filteredRecords.size());

    // Create new sublist.
    SortedList<ReceiverRecord> pageAccounts = new SortedList<>(
        FXCollections.observableArrayList(sortedRecords.subList(startIndex, endIndex)));
    pageAccounts.comparatorProperty().bind(transplantWaitingList.comparatorProperty());

    transplantWaitingList.setItems(pageAccounts);

  }


  /**
   * Creates a dialog box whenever the remove button is selected (Requires an item in the transplant
   * waiting list to be selected). This dialog contains a combo box to choose the reason for
   * removal, and a date picker to choose the date of death if option 3 is selected.
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
          errorOption,
          curedOption,
          deathOption
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

      createDeathListener(choose, errorLabel, curedQuestionError, date, dateLabel, curedQuestion,
          yes, no);

      dialog.getDialogPane().setContent(grid);

      //Catches a null date of death value if the reason is death, otherwise closes
      final Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
      okButton.addEventFilter(ActionEvent.ACTION, event -> {
        if ((date.getValue() == null) && (choose.getSelectionModel().getSelectedItem()
            .equals(deathOption))) {
          errorLabel.setVisible(true);
          event.consume();
        } else if ((!yes.isSelected()) && (!no.isSelected()) && (choose.getSelectionModel()
            .getSelectedItem().equals(curedOption))) {
          curedQuestionError.setVisible(true);
          event.consume();
        } else {
          closeDeathPopup(choose, date, yes);
        }
      });
      dialog.showAndWait();
    }
  }


  /**
   * Reacts to the reason given for the organs removal from the transplant waiting list.
   *
   * @param choose The checkbox with the options in it.
   * @param date The date picker where the date of death is chosen.
   * @param yes The checkbox for whether the organ has been cured.
   */
  private void closeDeathPopup(ComboBox<String> choose, DatePicker date, CheckBox yes) {
    if (choose.getSelectionModel().getSelectedItem().equals(deathOption)) {
      LocalDate dOD = date.getValue();
      removeOrganDeceased(dOD);
    } else if (choose.getSelectionModel().getSelectedItem().equals(curedOption)) {
      if (yes.isSelected()) {
        try {
          removeOrganCured("Yes");
        } catch (IOException exception) {
          openSelectedProfileError();
        }
      } else {
        try {
          removeOrganCured("No");
        } catch (IOException exception) {
          openSelectedProfileError();
        }
      }
    } else {
      removeOrganMistake();
    }
  }


  /**
   * Adds a listener to check if the combo box is on option 3, death
   *
   * @param choose The comboBox the listener is to be attached to
   * @param errorLabel Where the error messages are shown
   * @param curedQuestionError Another place where the error messages are shown
   * @param date The date picker for the date of death
   * @param dateLabel The label for the date picker for the date of death
   * @param curedQuestion The label questioning whether the account holder has been cured.
   * @param yes The yes checkbox for the date of death
   * @param no The no checkbox for the date of death
   */
  private void createDeathListener(ComboBox<String> choose, Label errorLabel,
      Label curedQuestionError,
      DatePicker date, Label dateLabel, Label curedQuestion, CheckBox yes, CheckBox no) {
    choose.getSelectionModel().selectedItemProperty().addListener(
        (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
          if (newValue.equals(deathOption)) {
            date.setVisible(true);
            dateLabel.setVisible(true);
            curedQuestion.setVisible(false);
            yes.setVisible(false);
            no.setVisible(false);
          } else if (newValue.equals(curedOption)) {
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
        });
  }


  /**
   * Opens an alert if there was an error in opening the selected profile.
   */
  private void openSelectedProfileError() {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Error Opening Profile");
    alert.setHeaderText(null);
    alert.setContentText("An error occurred while opening the selected profile.");
    alert.showAndWait();
  }


  /**
   * Removes all organs from the receiver, sets their date of death and logs all changes
   *
   * @param dateOfDeath the accounts date of death
   */
  public void removeOrganDeceased(LocalDate dateOfDeath) {
    DonorReceiver account = accountManager.getAccountsByNHI(selectedRecord.getNhi()).get(0);
    TransplantWaitingList.removeOrganDeceased(selectedRecord, accountManager, dateOfDeath);
    PageNav.loadNewPage(PageNav.TRANSPLANTLIST);
  }


  /**
   * Removes the record from the waiting list, creates a log and sets the organ to false
   *
   * @param option Whether or not the clinician wants to view the accounts current diseases
   * @throws IOException An error
   */
  public void removeOrganCured(String option) throws IOException {
    records.remove(selectedRecord);
    DonorReceiver account = accountManager.getAccountsByNHI(selectedRecord.getNhi()).get(selectedRecord.getNhi());
    TransplantWaitingList.removeOrganCured(selectedRecord, accountManager);
    if (option.equals("Yes")) {
      loadProfileWindowEdit(accountManager.getAccountsByNHI(selectedRecord.getNhi()).get(selectedRecord.getNhi()));
    }
    PageNav.loadNewPage(PageNav.TRANSPLANTLIST);
  }


  /**
   * Removes the record, changes the relevant organ to false in the account, and creates a relevant
   * log
   */
  public void removeOrganMistake() {
    records.remove(selectedRecord);
    DonorReceiver account = accountManager.getAccountsByNHI(selectedRecord.getNhi()).get(selectedRecord.getNhi());
    TransplantWaitingList.removeOrganMistake(selectedRecord, accountManager);
    PageNav.loadNewPage(PageNav.TRANSPLANTLIST);
  }


  /**
   * Refresh the table when any account receives a change
   */
  public static void triggerRefresh() {
    TableView transplantWaitingList = (TableView) App.getWindow().getScene()
        .lookup("#transplantWaitingList");
    if (transplantWaitingList == null) {
      // Handle case were accountsList is not initialised.
      throw new NullPointerException(
          "TableView object 'transplantWaitingList' is not initialised in the main window.");
    } else {
      // Trigger instantiated update.
      transplantWaitingList.refresh();
    }
  }

  //=================================================
  //Initialize functions

  /**
   * Sets the row factory for accountsList. This creates a tooltip for each populated row showing an
   * overview of the account. This code is based on a solution (https://stackoverflow.com/a/26221242)
   * posted by James_D in response to a question on Stack Overflow.
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
          DonorReceiver account = accountManager.getAccountsByNHI(record.getNhi()).get(record
              .getNhi()); // A dummy account variable for the account donating the specified organ
          // Generate an account-specific tool tip.
          Tooltip overview = new Tooltip();
          overview.setText(account.generateOverview());
          setTooltip(overview);
          // Highlight the entry if they donor is also donating this organ that they are receiving
          String organ = record.getOrgan();
          organ = TransplantWaitingList.organConversion(organ);
          Boolean isConflicting = account.getDonorOrganInventory().isDonating(organ);
          if (isConflicting) {
            setStyle("-fx-background-color:f93333;-fx-opacity:0.5;");
          } else {
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
  public void loadProfileWindow(DonorReceiver account) throws IOException {
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
   * Opens a view profile pane in a new window, then switches to the edit pane so the clinician can
   * view the diseases
   *
   * @param account The profile to be opened in a new window.
   * @throws IOException If the view profile FXML cannot be loaded.
   */
  public void loadProfileWindowEdit(DonorReceiver account) throws IOException {

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
   * Switches to the edit profile screen within the given window. Called from method
   * LoadProfileWindowEdit
   *
   * @throws IOException When the FXML cannot be retrieved.
   */
  private void loadEditWindow(DonorReceiver account, AnchorPane pane, Stage stage)
      throws IOException {

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
   * Populates the organ combobox with a static list of possible organs
   */
  private void initializeOrganFilterComboBox() {
    organFilterComboBox.setItems(organs);
    organFilterComboBox.getItems()
        .addAll(organString, "Liver", "Kidneys", "Heart", "Lungs", "Intestines",
            "Corneas", "Middle Ear", "Skin", "Bone", "Bone Marrow", "Connective Tissue");
    organFilterComboBox.getSelectionModel().select(0);
  }


  /**
   * Populates the region combobox dynamically with the regions of those on the transplant waiting
   * list.
   */
  private void populateRegionFilterComboBox() {
    Collection<String> uniqueRegions = new TreeSet<>();
    for (ReceiverRecord record : records) {
      uniqueRegions.add(record.getRegion());
    }
    regionFilterComboBox.getItems().removeAll(regionFilterComboBox.getItems());
    regionFilterComboBox.getItems().add(regionString);
    regionFilterComboBox.getItems().addAll(uniqueRegions);
    regionFilterComboBox.getSelectionModel().select(0);
  }


  /**
   * Initialize the list view whenever the pane is created.
   */
  @FXML
  public void initialize() {
    transplantWaitingList.setPlaceholder(new Label("No organ requests found"));
    selectedAccount = null;
    records.clear();
    setAccountsListRowFactory();
    initializeColumns();

    // Populate a master list with all Accounts, regardless of page.
    observableAccounts = FXCollections.observableArrayList();
    generateAccounts();

    for (DonorReceiver acc : observableAccounts) {
      receiverRecordCreator(acc);
    }

    // Initialize filter comboboxes
    initializeOrganFilterComboBox();
    populateRegionFilterComboBox();

    filteredRecords = new FilteredList<>(records, p -> true);
    organFilterComboBox.valueProperty()
        .addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
          filteredRecords.setPredicate(
              record -> TransplantWaitingList.receiverRequiresOrgan(record, newValue, organString)
                  &&
                  TransplantWaitingList.receiverFromRegion(record,
                      regionFilterComboBox.getSelectionModel().getSelectedItem().toString())

          );
          updatePageCount();
          updateTableView();
        });

    regionFilterComboBox.valueProperty()
        .addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
          filteredRecords
              .setPredicate(record -> TransplantWaitingList.receiverFromRegion(record, newValue) &&
                  TransplantWaitingList.receiverRequiresOrgan(record,
                      organFilterComboBox.getSelectionModel().getSelectedItem().toString(),
                      organString));
          updatePageCount();
          updateTableView();
        });

    sortedRecords = new SortedList<>(filteredRecords);

    super.pageControl = pageControl;
    super.sortedAccounts = sortedRecords;

    updatePageCount();
    updateTableView();

    pageControl.currentPageIndexProperty().addListener((observable, oldValue, newValue) ->
        updateTableView()
    );

    transplantWaitingList.setOnMouseClicked(click -> {
      ReceiverRecord record = (ReceiverRecord) transplantWaitingList.getSelectionModel()
          .getSelectedItem();
      selectedRecord = record;
      if (record != null) {
        selectedAccount = accountManager.getAccountsByNHI(selectedRecord.getNhi()).get(selectedRecord.getNhi());
        if (click.getButton().equals(MouseButton.PRIMARY) &&
            click.getClickCount() == 2 && selectedAccount != null) {
          try {
            loadProfileWindow(selectedAccount);
          } catch (IOException exception) {
            openSelectedProfileError();
          }
        }
      }
    });
  }


  /**
   * Helper method for 'initialize()' which sets the cell factory of each column in the TableView
   * object.
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
    dateColumn.setCellValueFactory(record -> new ReadOnlyStringWrapper(
        TransplantWaitingList.formatCreationDate(record.getValue().getTimestamp())));

  }


  /**
   * Creates records for the given account.
   *
   * @param acc The DonorReceiver to be looked through
   */
  private static void receiverRecordCreator(DonorReceiver acc) {
    ReceiverOrganInventory organInventory = acc.getRequiredOrgans();

    boolean[] organPresent = {
        organInventory.getLiver(), organInventory.getKidneys(), organInventory.getHeart(),
        organInventory.getLungs(), organInventory.getIntestine(), organInventory.getCorneas(),
        organInventory.getMiddleEars(), organInventory.getSkin(), organInventory.getBone(),
        organInventory.getBoneMarrow(), organInventory.getConnectiveTissue(),
        organInventory.getPancreas()
    };

    LocalDateTime[] organTimes = {
        organInventory.getLiverTimeStamp(), organInventory.getKidneysTimeStamp(),
        organInventory.getHeartTimeStamp(), organInventory.getLungsTimeStamp(),
        organInventory.getIntestineTimeStamp(), organInventory.getCorneasTimeStamp(),
        organInventory.getMiddleEarsTimeStamp(), organInventory.getSkinTimeStamp(),
        organInventory.getBoneTimeStamp(), organInventory.getBoneMarrowTimeStamp(),
        organInventory.getConnectiveTissueTimeStamp(), organInventory.getPancreasTimeStamp()
    };

    String[] organString = {
        "Liver", "Kidneys", "Heart", "Lungs", "Intestines", "Corneas", "Middle Ear", "Skin", "Bone",
        "Bone Marrow", "Connective Tissue", "Pancreas"
    };

    for (int i = 0; i < organPresent.length; i++) {
      if (organPresent[i]) {
        ReceiverRecord record = new ReceiverRecord(acc.fullName(), acc.getUserName(),
            acc.getContactDetails().getAddress().getRegion(), organTimes[i], organString[i]);
        records.add(record);
      }
    }
  }

}
