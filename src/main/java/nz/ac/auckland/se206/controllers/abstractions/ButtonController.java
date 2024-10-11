package nz.ac.auckland.se206.controllers.abstractions;

import javafx.fxml.FXML;
import nz.ac.auckland.se206.App;

public abstract class ButtonController {

  /**
   * Handles the mouse enter event for a rectangle (button). This method plays a hover sound effect
   * when the mouse enters the button area.
   */
  @FXML
  protected void handleRectangleEntered() {
    App.playHover();
  }

  /**
   * Handles the mouse exit event for a rectangle (button). This method stops the hover sound effect
   * when the mouse exits the button area.
   */
  @FXML
  protected void handleRectangleExited() {
    App.stopHover();
  }
}
