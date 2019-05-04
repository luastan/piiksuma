package piiksuma.exceptions;

import piiksuma.gui.Alert;

public abstract class PiikException extends Exception {

    public PiikException(String message) {
        super(message);
    }

    public abstract void showAlert(Exception e, String message);
}
