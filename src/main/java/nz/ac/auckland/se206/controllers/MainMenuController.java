package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.controllers.abstractions.ButtonController;
import nz.ac.auckland.se206.enums.SceneState;

public class MainMenuController extends ButtonController {
  @FXML
  private void onExit() {
    System.exit(0);
  }

  @FXML
  private void onSettings() {
    App.setRoot(SceneState.SETTINGS, "Entering settings menu...");
  }

  @FXML
  private void onStart() {
    App.setRoot(SceneState.START_GAME, "Starting game!");
    App.getGameState().startTimer(300);
  }
}
