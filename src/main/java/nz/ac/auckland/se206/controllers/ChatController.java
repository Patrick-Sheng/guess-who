package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import nz.ac.auckland.se206.controllers.abstractions.MapController;

/**
 * Controller class for the chat view. Handles user interactions and communication with the GPT
 * model via the API proxy.
 */
public class ChatController extends MapController {
  @FXML private Pane paneMap;

  @FXML
  public void initialize() {
    paneMap.setVisible(false);
  }
}
