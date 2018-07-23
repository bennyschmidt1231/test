
//this controller is now not used due to the history being moved o the sam pane as the details of the profile

package seng302.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import seng302.model.person.DonorReceiver;
import seng302.model.AccountManager;
import seng302.App;
import seng302.model.PageNav;
import seng302.model.person.LogEntry;

import java.util.ArrayList;

public class LogPaneController {

    private static DonorReceiver selectedAccount;
    private AccountManager accountManager = App.getDatabase();

    public static DonorReceiver getSelectedAccount() {
        return selectedAccount;
    }

    public static void setAccount(DonorReceiver newDonorReceiver) {
        selectedAccount = newDonorReceiver;
    }

    @FXML
    private Button close;

    @FXML
    private ListView view;

    @FXML
    private void closeSelected(ActionEvent event) {
        PageNav.loadNewPage(PageNav.VIEW);
        ViewProfilePaneController.setAccount(selectedAccount);
    }


    private ArrayList<LogEntry> generateUpdateLog() {
        //System.out.println(selectedAccount.getUpDateLog());
        return selectedAccount.getUpDateLog();
    }
    /**
     * Initialize the profile whenever the pane is created.
     */
    @FXML
    public void initialize() {
        ObservableList<LogEntry> observableLogs = FXCollections.observableArrayList();
        observableLogs.addAll(generateUpdateLog());

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

}

