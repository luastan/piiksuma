package piiksuma.exceptions;

import piiksuma.gui.Alert;

public class PiikDatabaseException extends PiikException {
    public PiikDatabaseException(String message) {
        super(message);
    }

    @Override
    public void showAlert() {
        Alert.newAlert().setHeading("Database error").addText(getMessage()).addCloseButton().show();
    }
}
