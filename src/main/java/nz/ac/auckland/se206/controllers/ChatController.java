package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import nz.ac.auckland.se206.controllers.abstractions.MapController;

/**
 * Controller class for the chat view. Handles user interactions and communication with the GPT
 * model via the API proxy.
 */
public class ChatController extends MapController {
  @FXML private Pane paneMap;
  @FXML private Label timerLabel;

  @FXML
  public void initialize() {
    paneMap.setVisible(false);
  }

  public void updateLblTimer(int time) {
    int minutes = time / 60;
    int seconds = time % 60;
    System.out.println("Time left: " + minutes + ":" + seconds);
    timerLabel.setText(String.format("Time Left: %02d:%02d", minutes, seconds));
  }
}
