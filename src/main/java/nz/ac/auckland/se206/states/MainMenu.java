package nz.ac.auckland.se206.states;

import java.io.IOException;
import javafx.application.Platform;
import javafx.scene.input.MouseEvent;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;
import nz.ac.auckland.se206.SceneState;

public class MainMenu implements GameState {

  public MainMenu(GameStateContext context) {}

  @Override
  public void handleRectangleClick(MouseEvent event, String rectangleId) throws IOException {
    switch (rectangleId) {
      case "rectStartGame":
        App.openScene(event, SceneState.START_GAME);
        break;
      case "rectSettings":
        System.out.println("Settings clicked");
        break;
      case "rectExit":
        Platform.exit();
        break;
    }
  }
}
