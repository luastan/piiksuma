package piiksuma.gui.deckControllers;

import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXPopup;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class AbstractDeckController {
    private JFXHamburger hamburguerButton;

    public AbstractDeckController() {
    }

    public JFXHamburger getHamburguerButton() {
        return hamburguerButton;
    }

    /**
     * Applies Common behaviour to the Hamburguer Button on the deck
     *
     * @param hamburguerButton the button which recieves the funcionality
     */
    public void setHamburguerButton(JFXHamburger hamburguerButton) {
        this.hamburguerButton = hamburguerButton;
        // TODO: Behavioural code
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/deckPopUp.fxml"));
        try {
            JFXPopup popup = new JFXPopup(loader.load());
            hamburguerButton.setOnMouseClicked(event -> {
                popup.show(
                        hamburguerButton,
                        JFXPopup.PopupVPosition.BOTTOM,
                        JFXPopup.PopupHPosition.LEFT,
                        0,
                        0
                );
                popup.setAutoHide(true);
            });
        } catch (IOException e) {
            // TODO: Handle the exception
            e.printStackTrace();
        }

    }
}
