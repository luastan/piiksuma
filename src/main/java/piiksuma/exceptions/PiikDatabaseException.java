package piiksuma.exceptions;

import piiksuma.Utilities.PiikLogger;
import piiksuma.gui.Alert;

import java.util.logging.Level;

public class PiikDatabaseException extends PiikException {

    public PiikDatabaseException(String message) {
        super(message);
    }

<<<<<<< HEAD

=======
    /**
     * Create an alert with the message of the exception
     */
>>>>>>> 49c36003ff789a2871a06df61fe15cc3eb4f3e07
    @Override
    public void showAlert(Exception e, String message) {
        PiikLogger.getInstance().log(Level.SEVERE, "PiikDatabaseException", e);
        Alert.newAlert().setHeading("Forbidden error").addText(message).addCloseButton().show();
    }
}
