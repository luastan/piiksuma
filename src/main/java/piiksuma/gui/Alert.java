package piiksuma.gui;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Arrays;


public class Alert {
    private JFXAlert alert;
    private JFXDialogLayout layout;
    private VBox content;

    public JFXDialogLayout getLayout() {
        return layout;
    }

    public Alert() {
        alert = new JFXAlert(ContextHandler.getContext().getStage("main"));
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setOverlayClose(false);
        layout = new JFXDialogLayout();
        content = new VBox();
        layout.getBody().add(content);
        alert.setContent(layout);
    }

    public Alert(Stage stage) {
        alert = new JFXAlert(stage);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setOverlayClose(false);
        layout = new JFXDialogLayout();
        content = new VBox();
        layout.getBody().add(content);
        alert.setContent(layout);
    }

    public static Alert newAlert() {
        Stage stage = ContextHandler.getContext().getCurrentStage();
        if (stage == null) {
            return new Alert();
        }
        return new Alert(stage);
    }

    public static Alert newAlert(Stage stage) {
        return new Alert(stage);
    }

    /**
     * Shows an alert with a title and a message
     *
     * @param title  title of the alert
     * @param message explaining message
     */
    public static void exceptionAlert(String title, String message) {
        newAlert().setHeading(title).addText(message).addCloseButton().show();
    }

    /**
     * Adds nodes/panels to the body
     *
     * @param node Node which we want to add
     * @return current instance
     */
    public Alert addInBody(Node node) {
        content.getChildren().add(node);
        return this;
    }

    /**
     * Adds nodes/panels to the body
     *
     * @param elements Nodes we want to add
     * @return current instance
     */
    public Alert addInBodyTodos(Node... elements) {
        content.getChildren().addAll(elements);
        return this;
    }

    /**
     * Adds a close button to the alert
     *
     * @return current instance
     */
    public Alert addCloseButton() {
        JFXButton closeButton = new JFXButton("Close");
        closeButton.setOnAction(event -> {
            alert.setHideOnEscape(true);
            alert.hideWithAnimation();
        });
        closeButton.getStyleClass().add("accept-dialog-button");
        return addButtons(closeButton);
    }

    /**
     * Adds buttons to the alert
     *
     * @param botones Botones a añadir
     * @return Instancia actual de accion
     */
    public Alert addButtons(JFXButton... botones) {
        layout.getActions().addAll(botones);
        Arrays.stream(botones).forEach(boton -> boton.getStyleClass().add("accept-dialog-button"));
        return this;
    }

    /**
     * Defines a heading to the alert
     *
     * @param node Nodo we put in the alert
     * @return current instance
     */
    public Alert setHeading(Node node) {
        layout.getHeading().add(node);
        return this;
    }

    /**
     * Defines a heading given by a String
     *
     * @param title title we want to add
     * @return current instance
     */
    public Alert setHeading(String title) {
        layout.getHeading().add(new Label(title));
        return this;
    }

    /**
     * JFXalert
     *
     * @return JFX alert
     */
    public JFXAlert getAlert() {
        return alert;
    }

    /**
     * Adds a text line to the body of the alert
     *
     * @param title Text in black. Title.
     * @param text  Normal text
     * @return current instance
     */
    public Alert addText(String title, String text) {
        HBox line = new HBox();
        line.setStyle("-fx-spacing: 10px");
        Label negrita = new Label(title);
        Label textNormal = new Label(text);

        negrita.getStyleClass().add("alert-text");
        textNormal.getStyleClass().add("alert-text");

        line.getChildren().addAll(negrita, textNormal);

        return this.addInBody(line);
    }

    public Alert addText(String text) {
        HBox line = new HBox();
        line.setStyle("-fx-spacing: 10px");
        Label textNormal = new Label(text);

        textNormal.getStyleClass().add("alert-text");
        line.getChildren().add(textNormal);

        return this.addInBody(line);
    }

    /**
     * Shows an error alert
     *
     * @param text Given error
     */
    public static void error(String text) {
        Alert.newAlert()
                .addCloseButton()
                .addText("Error", text)
                .show();
    }

    /**
     * Shows the alert
     */
    public void show() {
        alert.showAndWait();
    }
}