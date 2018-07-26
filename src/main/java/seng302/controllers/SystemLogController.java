package seng302.controllers;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Paint;
import seng302.model.AccountManager;
import seng302.model.comparitors.LogComparator;
import seng302.model.person.LogEntry;


/**
 * SystemLogController is a JavaFX controller used in conjunction with the FXML file SystemLog to
 * produce an interactive pane.
 */
public class SystemLogController {


  // Class attributes.
  @FXML
  private ListView<String> logDisplay;
  private final char DATE_ENTRY_CHARACTER = 0;
  private final Background BACKGROUND_STYLE = new Background(
      new BackgroundFill(Paint.valueOf("lightgrey"), CornerRadii.EMPTY, Insets.EMPTY));


  /**
   * Adds a listener to the system log which will call the refreshList method each time the system
   * log is changed.
   */
  private void addRefreshListener() {

    ListChangeListener<LogEntry> listener = change -> {

      refreshList();

    };

    AccountManager.addSystemLogListener(listener);

  }

  /**
   * Refreshes the system log display pane.
   */
  private void refreshList() {

    initialize();

  }


  /**
   * Returns a new ObservableList which contains the log entries in the original list sorted in
   * descending order with day entries added. A day entry consists of a null char used in cell
   * processing and the date.
   *
   * @param logEntries The system log in which day entries are to be inserted.
   * @return A new ObservableList containing the original log entries sorted with day entries
   * inserted every time the date changes.
   */
  private ObservableList<String> addDayEntries(List<LogEntry> logEntries) {

    logEntries.sort(new LogComparator());

    ObservableList<String> observableLogEntries = FXCollections.observableArrayList();

    LocalDate date = LocalDate.now();

    if (logEntries.size() > 0 && logEntries.get(0).getChangeTime().toLocalDate().equals(date)) {

      observableLogEntries.add(DATE_ENTRY_CHARACTER + "Today");

    }

    for (LogEntry logEntry : logEntries) {

      LocalDate logDate = logEntry.getChangeTime().toLocalDate();
      if (!date.equals(logDate)) {

        date = logDate;

        if (date.equals(LocalDate.now())) {

          observableLogEntries.add("Today");

        } else {

          observableLogEntries.add(DATE_ENTRY_CHARACTER + date.toString());

        }

      }

      observableLogEntries.add(logEntry.toString());

    }

    return observableLogEntries;

  }


  /**
   * Initialises the system log pane. Populates the list with log entries and adds a listener to the
   * system log to monitor changes.
   */
  public void initialize() {

    initializeRowFactory();
    addRefreshListener();

    List<LogEntry> systemLog = new ArrayList<>();
    systemLog.addAll(AccountManager.getSystemLog());

    ObservableList<String> printedSystemLog = addDayEntries(systemLog);
    logDisplay.setItems(printedSystemLog);

  }


  /**
   * Initializes the row factory used to display log entries. If an entry is a day entry, specified
   * by a null char in the first position, it will be highlighted light grey. Otherwise, the cell
   * will display normally.
   */
  private void initializeRowFactory() {

    logDisplay.setCellFactory(log -> new ListCell<String>() {

      @Override
      public void updateItem(String entry, boolean empty) {

        super.updateItem(entry, empty);

        if (entry == null) {

          setText("");

        } else {

          if (entry.charAt(0) == DATE_ENTRY_CHARACTER) {

            this.setBackground(BACKGROUND_STYLE);


          }

          setText(entry);

        }

      }

    });

  }


}
