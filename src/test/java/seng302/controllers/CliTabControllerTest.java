package seng302.controllers;


import javafx.collections.FXCollections;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.TextField;

import org.junit.Before;
import org.junit.Test;


/**
 * This class contains a series of tests for CliTabController.
 */
public class CliTabControllerTest {


  // Class attributes.
  CliTabController controller;


  /**
   *
   */
  @Before
  public void setUp() {

    new JFXPanel(); // This initialises the JavaFX toolkit.
    controller = new CliTabController();
    controller.cliInput = new TextField();
    controller.commandOutput = FXCollections.observableArrayList();

  }


  /**
   * Attempts to pass an invalid command to the commandControl method. This should produce an error
   * message and have no effect on existing accounts.
   */
  @Test
  public void invalidCommandTest() {
  }


  /**
   * Attempts to add a clinician to the database using a valid command. This should successfully
   * create a new clinician account and produce a confirmation method.
   */
  @Test
  public void validAddClinicianCommand() {
  }


}
