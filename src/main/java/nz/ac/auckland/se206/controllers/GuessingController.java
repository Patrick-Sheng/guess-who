package nz.ac.auckland.se206.controllers;

import javafx.fxml.FXML;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.enums.SceneState;

public class GuessingController {
  @FXML
  public void onSubmitFeedback() {
    App.setRoot(SceneState.END_GAME_WON);
  }
}
