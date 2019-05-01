package piiksuma.exceptions;

import piiksuma.gui.Alert;

public class PiikInvalidParameters extends PiikException {
    public PiikInvalidParameters(String message) {
        super(message);
    }


    @Override
    public void showAlert() {
        Alert.newAlert().setHeading("InvalidParameters").addText(getMessage()).addCloseButton().show();
    }
}
