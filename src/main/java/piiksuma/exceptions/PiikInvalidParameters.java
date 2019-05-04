package piiksuma.exceptions;

import piiksuma.gui.Alert;

public class PiikInvalidParameters extends PiikException {
    public PiikInvalidParameters(String message) {
        super(message);
    }

    /**
     * Create an alert with the message of the exception
     */
    @Override
    public void showAlert() {
        Alert.newAlert().setHeading("InvalidParameters").addText(getMessage()).addCloseButton().show();
    }
}
