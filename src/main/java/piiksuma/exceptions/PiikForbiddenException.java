package piiksuma.exceptions;

import piiksuma.gui.Alert;

// TODO I don't think so
public class PiikForbiddenException extends PiikDatabaseException {

    public PiikForbiddenException(String message) {
        super(message);
    }

    /**
     * Create an alert with the message of the exception
     */
    @Override
    public void showAlert() {
        Alert.newAlert().setHeading("Forbidden").addText(getMessage()).addCloseButton().show();
    }
}
