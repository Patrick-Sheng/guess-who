package nz.ac.auckland.se206.states;

import java.io.IOException;

import javafx.application.Platform;
import javafx.scene.input.MouseEvent;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;

public class MainMenu implements GameState {

  public MainMenu(GameStateContext context) {}

  @Override
  public void handleRectangleClick(MouseEvent event, String rectangleId) throws IOException {
    switch (rectangleId) {
      case "rectStartGame":
        App.startGame(event);
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
