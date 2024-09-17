package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.enums.SceneState;

/**
 * Controller class for the room view. Handles user interactions within the room where the user can
 * chat with customers and guess their profession.
 */
public class RoomController {

  @FXML private Pane paneRoom;
  @FXML private Pane paneMap;

  @FXML
  public void initialize() {
    paneMap.setVisible(false);
  }

  @FXML
  private void onGuess() {
    App.setRoot(SceneState.START_GUESSING);
  }

  @FXML
  private void onMap() {
    paneMap.setVisible(true);
    paneRoom.setOpacity(0.5);
  }

  @FXML
  private void onExitMap() {
    paneMap.setVisible(false);
    paneRoom.setOpacity(1);
  }
}
