package piiksuma.Utilities;

import javafx.scene.control.TextField;

public class PiikTextLimiter { // https://stackoverflow.com/questions/15159988/javafx-2-2-textfield-maxlength

    public static void addTextLimiter(final TextField tf, final int maxLength) {
        tf.textProperty().addListener((ov, oldValue, newValue) -> {
            if (tf.getText().length() > maxLength) {
                String s = tf.getText().substring(0, maxLength);
                tf.setText(s);
            }
        });
    }
}
