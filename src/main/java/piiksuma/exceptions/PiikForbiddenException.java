package piiksuma.exceptions;

import piiksuma.gui.Alert;

// TODO I don't think so
public class PiikForbiddenException extends PiikDatabaseException {

    public PiikForbiddenException(String message) {
        super(message);
    }

    @Override
    public void showAlert() {
        Alert.newAlert().setHeading("Forbidden").addText(getMessage()).addCloseButton().show();
    }
}
