package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.controllers.abstractions.MapController;
import nz.ac.auckland.se206.enums.SceneState;

/**
 * Controller class for the room view. Handles user interactions within the room where the user can
 * chat with customers and guess their profession.
 */
public class RoomController extends MapController {
  @FXML private Button guessButton;
  @FXML private Pane paneMap;
  @FXML private Label timerLabel;

  @FXML
  public void initialize() {
    disableLabels();
    disableButton();
    enableButton();
    paneMap.setVisible(false);
  }

  private void disableLabels() {}

  private void disableButton() {
    guessButton.setStyle("-fx-background-color: darkred; -fx-text-fill: #222;");
    guessButton.setDisable(true);
  }

  public void enableButton() {
    guessButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");
    guessButton.setDisable(false);
  }

  public void updateLblTimer(int time) {
    int minutes = time / 60;
    int seconds = time % 60;
    System.out.println("Time left: " + minutes + ":" + seconds);
    timerLabel.setText(String.format("Time Left: %02d:%02d", minutes, seconds));
  }

  @FXML
  private void onMakeGuess() {
    App.stopTimer();
    App.setRoot(SceneState.START_GUESSING);
  }
}
