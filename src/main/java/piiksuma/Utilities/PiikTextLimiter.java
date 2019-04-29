package piiksuma.Utilities;

import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;

public class PiikTextLimiter {

    /**
     * Generates a limit of characters for textFields
     *
     * @param tf        TextField to be limited
     * @param maxLength Max of characters permitted on the textField
     */
    public static void addTextLimiter(final TextInputControl tf, final int maxLength) {
        tf.textProperty().addListener((ov, oldValue, newValue) -> {
            if (tf.getText().length() > maxLength) {
                String s = tf.getText().substring(0, maxLength);
                tf.setText(s);
            }
        });
    }
}
