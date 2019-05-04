package piiksuma.exceptions;

import piiksuma.Utilities.PiikLogger;
import piiksuma.gui.Alert;

import java.util.logging.Level;

// TODO I don't think so
public class PiikForbiddenException extends PiikDatabaseException {

    public PiikForbiddenException(String message) {
        super(message);
    }

    /**
     * Create an alert with the message of the exception
     */
    @Override
    public void showAlert(Exception e, String message) {
        PiikLogger.getInstance().log(Level.SEVERE, "PiikForbiddenException", e);
        Alert.newAlert().setHeading("Forbidden error").addText(message).addCloseButton().show();
    }
}
