package piiksuma.exceptions;

import piiksuma.Utilities.PiikLogger;
import piiksuma.gui.Alert;

import java.util.logging.Level;

public class PiikDatabaseException extends PiikException {
    public PiikDatabaseException(String message) {
        super(message);
    }


    @Override
    public void showAlert(Exception e, String message) {
        PiikLogger.getInstance().log(Level.SEVERE, "PiikDatabaseException", e);
        Alert.newAlert().setHeading("Forbidden error").addText(message).addCloseButton().show();
    }
}
