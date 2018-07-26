package seng302.controllers;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import seng302.App;
import seng302.model.AccountManager;
import seng302.model.PageNav;
import seng302.model.person.Administrator;
import seng302.model.person.User;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;


public class AdminsListController extends ListController {

  @FXML
  public Button backButton;
  @FXML
  public TextField nameFilterTextField;
  @FXML
  public TextField usernameFilterTextField;
  @FXML
  public TableView<Administrator> adminsTable;
  @FXML
  public TableColumn<Administrator, String> nameColumn;
  @FXML
  public TableColumn<Administrator, String> usernameColumn;
  @FXML
  public Button newAdminButton;
  @FXML
  public Button openProfileButton;
  @FXML
  public Button deleteButton;
  @FXML
  public Label numAccountsLabel;
  @FXML
  public Pagination pageControl;

  //========================================================
  //For the table

  /**
   * Constant which determines the number of accounts on each table page.
   */
  private static int ACCOUNTS_PER_PAGE = 30;

  /**
   * An account selected by the user on the table.
   */
  private static Administrator selectedAccount;

  /**
   * Retrieves the selected account.
   *
   * @return The selected DonorReceiver object.
   */
  public static Administrator getSelectedAccount() {
    return selectedAccount;
  }

  /**
   * The AccountManager object which handles all account operations.
   */
  private AccountManager accountManager = App.getDatabase();

  public Collection<Administrator> administrators = accountManager.getAdministratorsArrayList();

  /**
   * The list of all administrators.
   */
  private ObservableList<Administrator> observableAccounts;

  /**
   * The list of all administrators to be displayed in TableView that match the search criteria.
   */
  private FilteredList<Administrator> filteredAccounts;


  /**
   * A list of sorted administrators derived from filteredList.
   */
  private SortedList<Administrator> sortedAccounts;
  //========================================================

  @FXML
  public void backButtonPressed() {
    PageNav.loadNewPage(PageNav.ADMINMENU);
  }

  /**
   * Handle the clicking of the "Add new admin" button and open a window that creates a new admin
   */
  @FXML
  public void newAdminButtonPressed() {
    User admin = AccountManager.getCurrentUser();
    try {
      if (!App.childWindowToFront(admin)) {
        FXMLLoader loader = new FXMLLoader();
        VBox adminPane = loader.load(getClass().getResourceAsStream(PageNav.CREATEADMIN));

        // Create new scene.
        Scene adminScene = new Scene(adminPane);

        // Create new stage.
        Stage adminStage = new Stage();
        adminStage.setTitle("SapioCulture - Create administrator");
        adminStage.setScene(adminScene);
        adminStage.show();

        adminStage.setX(
            App.getWindow().getX() + ((App.getWindow().getWidth() - adminStage.getWidth()) / 2.0));
        adminStage.setY(
            App.getWindow().getY() + ((App.getWindow().getHeight() - adminStage.getHeight())
                / 2.0));

        App.addChildWindow(adminStage, admin);
      }
    } catch (IOException e) {
      System.out.println("Error with opening create administrator window");
    }
  }

  @FXML
  public void openProfileButtonPressed() {
    if (selectedAccount == null) {
      // Warn user if no account was selected.
      noAccountSelectedAlert("Administrator");
    } else {
      try {
        loadProfileWindow(selectedAccount);
      } catch (IOException e) {
        System.out.println("Error with opening view administrator window");
      }
    }
  }

  /**
   * Opens a view profile pane in a new window.
   *
   * @param account The profile to be opened in a new window.
   * @throws IOException If the view profile FXML cannot be loaded.
   */
  public void loadProfileWindow(Administrator account) throws IOException {

    // Only load new window if childWindowToFront fails.
    if (!App.childWindowToFront(account)) {

      // Set the selected account for the profile pane and confirm child.
      administratorViewProfileController.setSelectedAdmin(account);

      // Create new pane.
      FXMLLoader loader = new FXMLLoader();
      AnchorPane profilePane = loader.load(getClass().getResourceAsStream(PageNav.VIEWADMIN));

      // Create new scene.
      Scene profileScene = new Scene(profilePane);

      // Create new stage.
      Stage profileStage = new Stage();
      profileStage.setTitle("Viewing an Administrator");
      profileStage.setScene(profileScene);
      profileStage.show();
      profileStage.setX(App.getWindow().getX() + App.getWindow().getWidth());

      App.addChildWindow(profileStage, account);
    }

  }


  @FXML
  public void deleteButtonPressed() {
    if (selectedAccount == null) {
      // Warn user if no account was selected.
      noAccountSelectedAlert("Delete Administrator");
    } else {
      if (!selectedAccount.getUserName().equals(Administrator.DEFAULT)) {
        makeSureOfDelete();
        PageNav.loadNewPage(PageNav.ADMINSLIST);
      }

    }
  }

  private void makeSureOfDelete() {
    Dialog<Boolean> dialog = new Dialog<>();
    dialog.setTitle("Delete");
    dialog.setHeaderText("Deletion of Administrator");
    dialog.setContentText("Are you sure you want to delete this administrator?");
    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.NO, ButtonType.YES);
    final Button yesButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.YES);
    yesButton.addEventFilter(ActionEvent.ACTION, event -> {
      accountManager.getAdministrators().remove(selectedAccount);
      selectedAccount.setActive(false);
      accountManager.adminTwilightZone.put(selectedAccount.getUserName(), selectedAccount);
    });
    Optional<Boolean> result = dialog.showAndWait();
  }

  //===============================================================
  //===============================================================
  //Initializing the table

  /**
   * Using criteria specified in the search field, an ArrayList of matching accounts is retrieved
   * from the account manager.
   */
  private void generateAccounts() {

    // Clear and repopulate observableAccounts.
    observableAccounts.removeAll(observableAccounts);
    observableAccounts.addAll(administrators);

  }

  /**
   * @param administrator The Administrators with a full name.
   * @param string The string which may be contained in the full name.
   * @return True if the full name of administrator contains string. False otherwise.
   */
  private boolean isAccountVisible(Administrator administrator, String string, String otherString) {

    boolean id = isAccountVisibleUsername(administrator, otherString);
    // Generate full name from given, other, and last names.
    String fullName =
        administrator.getFirstName() + " " + administrator.getMiddleName() + " " + administrator
            .getLastName();
    fullName = fullName.replaceAll("\\s+", " ").toLowerCase();

    // Return true only if the full name contains the specified string and
    return (id && fullName.contains(string.toLowerCase()));

  }

  /**
   * @param administrator The Administrators with a full name.
   * @param string The string which may be contained in the full name.
   * @return True if the full name of administrator contains string. False otherwise.
   */
  private boolean isAccountVisibleUsername(Administrator administrator, String string) {

    // Generate full name from given, other, and last names.
    String username = administrator.getUserName();
    username = username.replaceAll("\\s+", " ").toLowerCase();

    // Return true only if the full name contains the specified string and
    return username.contains(string.toLowerCase());

  }


  /**
   * Updates the page count shown by the pagination widget and resets the current page index to 0.
   */
  private void updateTableView() {

    // Update label showing number of matches.
    numAccountsLabel.setText(getMatchesMessage("account"));

    // Retrieve start and end index of current page.
    int startIndex = pageControl.getCurrentPageIndex() * ACCOUNTS_PER_PAGE;
    int endIndex = Math.min(startIndex + ACCOUNTS_PER_PAGE, filteredAccounts.size());

    // Create new sublist.
    SortedList<Administrator> pageAdministrators = new SortedList<>(
        FXCollections.observableArrayList(sortedAccounts.subList(startIndex, endIndex)));
    pageAdministrators.comparatorProperty().bind(adminsTable.comparatorProperty());

    adminsTable.setItems(pageAdministrators);

  }


  /**
   * Initialize the list view whenever the pane is created.
   */
  @FXML
  public void initialize() {
    super.pageControl = pageControl;
    super.ACCOUNTS_PER_PAGE = ACCOUNTS_PER_PAGE;

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

      filteredAccounts.setPredicate(
          account -> isAccountVisible(account, newValue, usernameFilterTextField.getText()));
      sortedAccounts = new SortedList<>(filteredAccounts);

      updatePageCount();
      updateTableView();

    });

    usernameFilterTextField.textProperty().addListener((observable, oldValue, newValue) -> {

      filteredAccounts.setPredicate(
          account -> isAccountVisible(account, nameFilterTextField.getText(), newValue));
      sortedAccounts = new SortedList<>(filteredAccounts);

      updatePageCount();
      updateTableView();

    });

    sortedAccounts = new SortedList<>(filteredAccounts);
    super.sortedAccounts = sortedAccounts;

    updatePageCount();
    updateTableView();

    pageControl.currentPageIndexProperty().addListener((observable, oldValue, newValue) -> {

      updateTableView();

    });

    adminsTable.setOnMouseClicked(new EventHandler<MouseEvent>() {

      @Override
      public void handle(MouseEvent click) {
        Administrator record = adminsTable.getSelectionModel().getSelectedItem();
        if (record != null) {
          selectedAccount = record;
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
        (
            record.getValue().getFirstName() + " " +
                record.getValue().getMiddleName() + " " +
                record.getValue().getLastName()
        ).replaceAll("\\s+", " ")
    ));

    // usernameColumn
    usernameColumn.setCellValueFactory(record -> new ReadOnlyStringWrapper(
        (
            record.getValue().getUserName())));

  }
}
