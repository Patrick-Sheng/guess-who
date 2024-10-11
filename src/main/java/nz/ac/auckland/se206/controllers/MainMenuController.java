package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.controllers.abstractions.ButtonController;
import nz.ac.auckland.se206.enums.SceneState;

public class MainMenuController extends ButtonController {
  private boolean hasEnteredRoom;

  @FXML
  public void initialize() {
    hasEnteredRoom = false;
  }

  @FXML
  private void onExit() {
    System.exit(0);
  }

  @FXML
  private void onSettings() {
    if (!hasEnteredRoom) {
      App.setRoot(SceneState.SETTINGS, "Entering settings menu...");
    }
  }

  @FXML
  private void onStart() {
    if (!hasEnteredRoom) {
      hasEnteredRoom = true;
      App.setRoot(SceneState.INTRODUCTION, "Playing introduction...");
      App.getGameState().startTimer(300);
    }
  }
}
