package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.enums.SceneState;

/**
 * Controller class for the room view. Handles user interactions within the room where the user can
 * chat with customers and guess their profession.
 */
public class RoomController {
  @FXML private Button guessButton;

  @FXML
  public void initialize() {
    disableLabels();
    disableButton();
    enableButton();
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

  @FXML
  public void onMakeGuess() {
    App.setRoot(SceneState.START_GUESSING);
  }
}
