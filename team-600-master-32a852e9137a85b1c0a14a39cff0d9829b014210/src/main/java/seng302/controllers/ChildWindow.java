package seng302.controllers;
import javafx.stage.Stage;
import seng302.model.person.User;


public class ChildWindow {

    /**
     * ChildWindow is a simple container for a stage and account object. It is used
     * to associate accounts with a stage. The primary use for this is secondary
     * or child windows that display the information of a single account.
     */


    // Class attributes.
    private Stage window;
    private User account;
    private int windowID;
    private static int nextWindowID = 0;


    /**
     * Creates a ChildWindow instance with no associated account.
     *
     * @param window The Stage object that will represent the child window.
     */
    public ChildWindow(Stage window) {

        this.window = window;
        this.account = null;
        this.windowID = nextWindowID;
        nextWindowID += 1;

    }


    /**
     * Creates a ChildWindow instance with an associated account.
     *
     * @param window The Stage object that will represent the child window.
     * @param account The account that will be associated with teh child window.
     */
    public ChildWindow(Stage window, User account) {
        this.window = window;
        this.account = account;
    }


    /**
     * Retrieves the Stage object that represents the window.
     *
     * @return The window attribute which is a Stage object.
     */
    public Stage getWindow() {

        return window;

    }


    /**
     * Retrieves the Account object that is associated with the window.
     *
     * @return The account attribute which is an User object.
     */
    public User getAccount() {

        return account;

    }


    /**
     * Returns the ID of a childWindow instance.
     *
     * @return The windowID as an int.
     */
    public int getWindowID() {

        return windowID;

    }

    /**
     * Performs a comparison between the current ChildWindow instance and a
     * second object. If that object is a ChildWindow instance which shares the
     * same account as the current instance, it is considered equal.
     *
     * @param object An object to compare to the current ChildWindow instance.
     * @return True if the object is equal, false otherwise.
     */
    public boolean equals(Object object) {

        if (object instanceof ChildWindow) {

            ChildWindow childWindow = (ChildWindow) object;

            if(childWindow.getWindowID() == windowID || (childWindow.getAccount() != null && childWindow.getAccount().equals(account))) {

                return true;

            } else {

                return false;

            }

        } else {

            return false;

        }
    }
}
