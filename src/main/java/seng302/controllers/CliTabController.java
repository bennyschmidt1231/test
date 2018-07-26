package seng302.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import seng302.model.CLICommandHandler;

import java.util.ArrayList;
import java.util.List;


/**
 * Controller for command line gui
 */
public class CliTabController {

  /**
   * TextField for the command line input
   */
  @FXML
  TextField cliInput;

  /**
   * ListView where the command line out put will be displayed
   */
  @FXML
  ListView cliOutput;

  ObservableList commandOutput = FXCollections.observableArrayList();

  /**
   * List of previous command by the clinician
   */
  private List previousCommands;

  /**
   * Current index of the command shown in cliInput
   */
  private int currentIndex;

  private CLICommandHandler commandHandler;

  /**
   * Initialize the CLI tab
   */
  @FXML
  private void initialize() {
    previousCommands = new ArrayList();
    currentIndex = -1;
    cliInput.setOnKeyPressed(new EventHandler<javafx.scene.input.KeyEvent>() {
      @Override
      public void handle(javafx.scene.input.KeyEvent event) {
        if (event.getCode() == KeyCode.UP) {
          goBackward();
        } else if (event.getCode() == KeyCode.DOWN) {
          goForward();
        }
      }
    });
    cliOutput.setItems(commandOutput);
    commandHandler = new CLICommandHandler();
  }

  /**
   * Occurs when the user selects up, will show the last command entered
   */
  private void goBackward() {
    if (this.currentIndex > 0) {
      currentIndex--;
      cliInput.setText((String) previousCommands.get(currentIndex));
    }
  }

  /**
   * Occurs when the user selects down, will show the next command after the current
   */
  private void goForward() {
    if (this.currentIndex < previousCommands.size()) {
      try {
        currentIndex++;
        cliInput.setText((String) previousCommands.get(currentIndex));
      } catch (IndexOutOfBoundsException e) {
      }
    } else {
      cliInput.setText("");
    }
  }


  /**
   * Takes command and runs in the cli
   */
  @FXML
  private void commandEnter() {
    ArrayList<String> messages = commandHandler.commandControl(cliInput.getText());
    for (String message : messages) {
      commandOutput.add(0, message);
    }
    previousCommands.add(cliInput.getText());
    cliInput.setText("");
    currentIndex = previousCommands.size();
  }
}
