package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.controllers.abstractions.ButtonController;
import nz.ac.auckland.se206.enums.SceneState;

/** Controller for the main menu screen. */
public class MainMenuController extends ButtonController {
  private boolean hasEnteredRoom;

  /** Initializes the main menu controller, setting the initial state of the room entry flag. */
  @FXML
  public void initialize() {
    hasEnteredRoom = false;
  }

  /** Exits the application when the exit button is clicked. */
  @FXML
  private void onExit() {
    System.exit(0);
  }

  /** Navigates to the settings menu if the player has not yet entered a room. */
  @FXML
  private void onSettings() {
    if (!hasEnteredRoom) {
      App.setRoot(SceneState.SETTINGS, "Entering settings menu...");
    }
  }

  /**
   * Starts the game by navigating to the introduction scene, setting the room entry flag, and
   * starting the game timer if the player has not yet entered a room.
   */
  @FXML
  private void onStart() {
    if (!hasEnteredRoom) {
      hasEnteredRoom = true;
      App.setRoot(SceneState.INTRODUCTION, "Playing introduction...");
      App.getGameState().startTimer(300);
    }
  }
}
