package seng302.controllers;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.*;
import org.testfx.framework.junit.ApplicationTest;
import seng302.App;

/**
 * MenuBarControllerTest provides a series of JUnit and TestFX tests for the
 * class MenuBarController. It also defines pre and post test behaviour.
 */
public class MenuBarControllerTest extends ApplicationTest {


    // Class attributes.
    private App app;


    /**
     * Create an App instance and start it, launching the GUI.
     *
     * @param stage The window in which the application will be displayed.
     * @throws Exception If an App instance could not be created.
     */
    @Override public void start (Stage stage) throws Exception {
        app = new App();
        app.start(stage);
    }


    /**
     * Configures TestFX to operate in the Git continuous integration runner.
     */
    @BeforeClass public static void headless() {
        GitlabGUITestSetup.headless();
    }


    /**
     * Tests the removal of the unsaved indicator from the title of the main
     * window after accounts have been exported (and there are no more unsaved
     * changes). The asterisk should be removed from the title.
     */
    @Test public void unsavedIndicatorOnExportTest() {
        Stage window = App.getWindow();
        makeChange();
        Assert.assertEquals("SapioCulture*", window.getTitle());
        // Save here.
        clickOn("#file").clickOn("#save").clickOn("#saveConfirm");
        Assert.assertEquals("SapioCulture", window.getTitle());
    }


    /**
     * Tests the exportAccounts method with no unsaved indicator (an asterisk)
     * in the window title. The accounts should export successfully and no
     * change should occur in the window title.
     */
    @Test public void noUnsavedIndicatorOnExportTest() {
        Stage window = App.getWindow();
        String titleBefore = window.getTitle();
        Assert.assertEquals("SapioCulture", titleBefore);

        App.removeUnsavedIndicator();
        String titleAfter = window.getTitle();
        Assert.assertEquals("SapioCulture", titleAfter);
    }


    /**
     * Toggles the lived in UK/France flag for account ABC1234.
     */
    private void makeChange() {
        TextField NHITextField = lookup("#usernameTextField").query();
        NHITextField.setText("ABC1234");
        TextField passwordField = lookup("#passwordField").query();
        passwordField.setText("password");
        Button loginButton = lookup("#loginButton").query();
        clickOn(loginButton);
        Button editButton = lookup("#Edit").query();
        clickOn(editButton);
        CheckBox livedInUKFranceCheckBox = lookup("#editLivedInUKFrance").query();
        clickOn(livedInUKFranceCheckBox);
        Button doneButton = lookup("#Done").query();
        clickOn(doneButton);
    }


    /**
     * Destroys the testing environment after each test.
     */
    @After public void tearDown() {
        app = null;
    }


}
