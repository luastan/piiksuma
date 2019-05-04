package piiksuma.exceptions;

import piiksuma.Utilities.PiikLogger;
import piiksuma.gui.Alert;

import java.util.logging.Level;

public class PiikInvalidParameters extends PiikException {
    public PiikInvalidParameters(String message) {
        super(message);
    }


    @Override
    public void showAlert(Exception e, String message) {
        PiikLogger.getInstance().log(Level.SEVERE, "PiikInvalidParameters", e);
        Alert.newAlert().setHeading("Forbidden error").addText(message).addCloseButton().show();
    }
}
