package nz.ac.auckland.se206.controllers.abstractions;

import javafx.fxml.FXML;
import javafx.scene.media.AudioClip;
import nz.ac.auckland.se206.App;

import java.util.Objects;

public abstract class ButtonController {
    @FXML
    private void handleRectangleEntered() {
        App.playHover();
    }

    @FXML
    private void handleRectangleExited() {
        App.stopHover();
    }
}
