package piiksuma.gui.deckControllers;

import com.jfoenix.controls.JFXHamburger;

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
    }
}
