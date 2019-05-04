package piiksuma.exceptions;

import piiksuma.gui.Alert;

public class PiikDatabaseException extends PiikException {

    public PiikDatabaseException(String message) {
        super(message);
    }

    /**
     * Create an alert with the message of the exception
     */
    @Override
    public void showAlert() {
        Alert.newAlert().setHeading("Database error").addText(getMessage()).addCloseButton().show();
    }
}
