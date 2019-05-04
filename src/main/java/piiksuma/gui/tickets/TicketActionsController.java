package piiksuma.gui.tickets;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import com.jfoenix.validation.base.ValidatorBase;
import de.jensd.fx.glyphs.GlyphsBuilder;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import piiksuma.Ticket;

import java.net.URL;
import java.util.ResourceBundle;

public class TicketActionsController implements Initializable {

    public Label reason;
    public JFXButton closeTicket;
    public JFXTextField messageField;
    public JFXButton sendButton;
    private Ticket ticket;

    public TicketActionsController(Ticket ticket) {
        this.ticket = ticket;
    }

    /**
     * Init the window components
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reason.setText(ticket.getTextProblem());
        if (ticket.getAdminClosing() != null) {
            closeTicket.setText("Closed by: " + ticket.getAdminClosing().getId());
            closeTicket.setDisable(true);
        }

        ValidatorBase validator = new RequiredFieldValidator("Required field");
        validator.setIcon(GlyphsBuilder.create(FontAwesomeIconView.class)
                .glyph(FontAwesomeIcon.WARNING)
                .build());
        messageField.setValidators(validator);

        messageField.textProperty().addListener((observable, oldValue, newValue) -> {
            sendButton.setDisable(!messageField.validate());
        });
    }
}
