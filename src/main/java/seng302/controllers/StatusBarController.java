package seng302.controllers;


import javafx.animation.FadeTransition;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import javafx.util.Duration;
import seng302.model.AccountManager;
import seng302.model.person.LogEntry;

import java.util.List;


/**
 * StatusBarController.java is a JavaFX controller for the status bar. This
 * functions as an intermediary between the menu bar and its content.
 */
public class StatusBarController {


    /* Class Attributes */
    private  static final String STATUSBAR_URI = "/statusBar.fxml";
    @FXML private Label actionLabel;
    @FXML private Pane contentPane;
    private ObservableList<String> log;


    /**
     * Initialises the status bar controller.
     */
    public void initialize() {

        actionLabel.setText("");

        // Create listener to monitor changes to system log.
        ListChangeListener<LogEntry> listener = change -> {

            String message;

            // Retrieve list of changes.
            change.next();
            List changes = change.getAddedSubList();

            // Find last change and convert to string format.
            LogEntry lastChange = (LogEntry) changes.get(changes.size() - 1);

            System.out.println("import");

            if (lastChange.getValChanged().equals("import")) {

                message = "Accounts imported.";

            } else {

                message = lastChange.getAccountModified() + " modified.";

            }

            // Set label to last change.
            setActionLabel(message);

        };

        AccountManager.addSystemLogListener(listener);

    }


    /**
     * Updates the action label with the last item added to the system log and
     * sets a timer to remove it after two seconds.
     */
    private void setActionLabel(String action) {

        actionLabel.setOpacity(1); // Reset opacity of label to 1.
        actionLabel.setText(action);

        // Perform animation which fades text after 2 seconds.
        FadeTransition fade = new FadeTransition(Duration.seconds(2.0), actionLabel);
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.setDelay(Duration.seconds(5.0));
        fade.play();

    }


    /**
     * Takes an AnchorPane object, corresponding to the highest level of the
     * hierarchy defined in statusBar.fxml, and injects a node into content
     * pane. This is used to display a page within the status bar.
     *
     * @param statusBar The AnchorPane representing the status bar.
     * @param node The Node object which is to appear in the content pane.
     * @return The complete status bar with injected node as a Node object.
     */
    public static AnchorPane injectNode(AnchorPane statusBar, Node node) {

        Pane contentPane = (Pane) statusBar.getChildren().get(0);
        contentPane.getChildren().setAll(node);

        return statusBar;

    }



}
