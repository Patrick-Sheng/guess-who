package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.controllers.abstractions.ButtonController;
import nz.ac.auckland.se206.enums.SceneState;

public class MainMenuController extends ButtonController {
  public ImageView startGame;
  public ImageView settings;
  public ImageView exit;

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
    App.startTimer(300);
  }
}
