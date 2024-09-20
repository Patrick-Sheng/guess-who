package nz.ac.auckland.se206.controllers.abstractions;

import javafx.fxml.FXML;
import nz.ac.auckland.se206.App;

public abstract class ButtonController {
  @FXML
  protected void handleRectangleEntered() {
    App.playHover();
  }

  @FXML
  protected void handleRectangleExited() {
    App.stopHover();
  }
}
