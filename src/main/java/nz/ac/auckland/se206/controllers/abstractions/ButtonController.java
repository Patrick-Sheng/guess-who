package nz.ac.auckland.se206.controllers.abstractions;

import javafx.fxml.FXML;
import javafx.scene.media.AudioClip;
import nz.ac.auckland.se206.App;

public abstract class ButtonController {
    private AudioClip hoverSound;

    @FXML
    public void initialize() {
        hoverSound = new AudioClip(App.class.getResource("/sounds/buttonHover.wav").toExternalForm());
    }

    @FXML
    private void handleRectangleEntered() {
        hoverSound.play();
    }

    @FXML
    private void handleRectangleExited() {
        hoverSound.stop();
    }
}
